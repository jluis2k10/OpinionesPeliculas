<%@ page import="es.uned.entities.CommentWithSentiment" %>
<%@ page import="es.uned.entities.Search" %>
<%@ page import="com.fasterxml.jackson.databind.node.ObjectNode" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>

<h2 class="mb-2">Resultados del análisis</h2>

<!-- Nuevos comentarios tras actualización -->
<c:if test="${newComments != null}">
    <div class="alert alert-info alert-dismissible fade show" role="alert">
        Añadidos <strong>${newComments}</strong> nuevos comentarios a la búsqueda.
        <button type="button" class="close" data-dismiss="alert" aria-label="Cerrar">
            <span aria-hidden="true">&times;</span>
        </button>
    </div>
</c:if>

<!-- Gráficos -->
<div class="card">
    <div class="card-header">
        <ul class="nav nav-tabs card-header-tabs" role="tablist">
            <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#general">Vista General</a></li>
            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#evolution">Evolución temporal</a></li>
            <c:if test="${search.classifySubjectivity}">
                <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#scatter">Gráfico de Dispersión</a></li>
            </c:if>
        </ul>
    </div>
    <div class="card-body">
        <div class="tab-content">
            <div id="general" class="tab-pane fade show active" role="tabpanel">
                <canvas id="pieChart" class="col-12 col-md-6 float-left"></canvas>
                <canvas id="barChart" class="col-12 col-md-6"></canvas>
            </div>
            <div id="evolution" class="tab-pane fade" role="tabpanel">
                <canvas id="timeChart" class="col-12"></canvas>
                <div class="col-12 d-flex justify-content-center">
                    <div class="custom-control custom-radio custom-control-inline">
                        <input type="radio" id="dayScale" name="selectScale" class="custom-control-input" checked="checked" value="day">
                        <label class="custom-control-label" for="dayScale">Día</label>
                    </div>
                    <div class="custom-control custom-radio custom-control-inline">
                        <input type="radio" id="weekScale" name="selectScale" class="custom-control-input" value="isoWeek">
                        <label class="custom-control-label" for="weekScale">Semana</label>
                    </div>
                    <div class="custom-control custom-radio custom-control-inline">
                        <input type="radio" id="monthScale" name="selectScale" class="custom-control-input" value="month">
                        <label class="custom-control-label" for="monthScale">Mes</label>
                    </div>
                </div>
            </div>
            <c:if test="${search.classifySubjectivity}">
                <div id="scatter" class="tab-pane fade" role="tabpanel">
                    <canvas id="scatterChart" class="col-12"></canvas>
                </div>
            </c:if>
        </div>
    </div>
</div>

<!-- Guardar búsqueda -->
<sec:authorize access="isAuthenticated()">
    <div class="row">
        <div class="col-12">
            <button type="button" class="btn btn-primary save-search">Guardar búsqueda</button>
        </div>
    </div>
</sec:authorize>
<%
    Search mysearch = (Search) request.getAttribute("search");
    ObjectNode searchJSON = mysearch.toJSON(true);
%>
<div class="row">
    <div id="comments" class="col-12">
        <c:if test="${!empty search.comments}">
            <ul id="comments-list" class="list-group mb-3"></ul>
            <div class="row">
                <div class="col-12 col-md-6">
                    <label>
                        Mostrar
                        <select class="form-control form-control-sm pages-size" id="page-size">
                            <option>5</option>
                            <option>10</option>
                            <option>25</option>
                            <option>50</option>
                            <option>100</option>
                        </select>
                        comentarios
                    </label>
                </div>
                <div id="comments-pagination" class="col-12 col-md-6"></div>
            </div>
        </c:if>
    </div>
</div>
<%@ include file="_js.jsp"%>
<script type="text/javascript" src="${path}/js/pagination.js"></script>
<script type="text/javascript" src="${path}/js/readmore.js"></script>
<script type="text/javascript" src="${path}/js/moment-with-locales.min.js"></script>
<script type="text/javascript" src="${path}/js/Chart.min.js"></script>
<script type="text/javascript" src="${path}/js/custom.js"></script>
<script>
    moment.locale('es');

    $(document).ready(function() {
        myPagination(5);
    });

    $(".save-search").click(function (e) {
        $button = $(e.target);
        $button.prop("disabled", true);
        $.when(saveSearch())
            .done(function () {
                $button.hide();
                alertMsg("success", "Búsqueda guardada.");
            })
            .fail(function () {
                $button.prop("disabled", false);
                alertMsg("danger", "No se ha podido guardar la búsqueda.");
            });
    });

    // Listener para selección de comentarios por página
    $("#page-size").change(function () {
        $("#comments-pagination").pagination('destroy');
        myPagination(this.value);
    });

    // Paginación de resultados
    function myPagination(size) {
        $("#comments-pagination").pagination({
            dataSource: <%=searchJSON.toString()%>,
            locator: 'comments',
            pageSize: size,
            callback: function (comments, pagination) {
                formatComments(comments, $("#comments-list"));
                generateReadMore();
            },
            ulClassName: "pagination justify-content-end"
        });
    }

    function formatComments(comments, $container) {
        $container.empty();
        comments.forEach(function (comment) {
            var $listItem = $('<li></li>').addClass('list-group-item flex-column align-items-start');

            var $headerDiv = $('<div></div>').addClass('d-flex justify-content-between');
            var $sourceContent = $('<small></small>').addClass('mb-2 text-muted').html('<%=mysearch.getSource()%>: ' +
                '<a href="' + comment.source_url + '" target="_blank">' + comment.source_url + '</a>');
            var $dateContent = $('<small></small>').addClass('text-muted').html(comment.date);
            $sourceContent.appendTo($headerDiv);
            $dateContent.appendTo($headerDiv);
            $headerDiv.appendTo($listItem);

            var formComment = comment.comment.replace(/(\r\n|\n|\r)/g, "<br />");
            var $mainContent = $('<p></p>').addClass('card-text mb-2 readmore').html(formComment);
            $mainContent.appendTo($listItem);

            var sentiment = "Positivo"
            var cssClass = "text-success";
            var sentimentIcon = "<i data-feather=\"thumbs-up\"></i> ";
            if (comment.sentiment === "Negative") {
                sentiment = "Negativo";
                cssClass = "text-danger";
                sentimentIcon = "<i data-feather=\"thumbs-down\"></i> "
            } else if (comment.sentiment === "Neutral") {
                sentiment = "Neutral";
                cssClass = "";
                sentimentIcon = "<i data-feather=\"minus\"></i> "
            }

            var $sentimentFooter = $('<small></small>').addClass(cssClass).html(
                    '<strong>' + sentimentIcon + sentiment + '</strong> ' +
                    '(' + (parseFloat(comment.sentiment_score) * 100).toFixed(2) + '%)'
            );
            $sentimentFooter.appendTo($listItem);

            if (comment.subjectivity != null) {
                var subjectivity = "";
                (comment.subjectivity === "Subjective" ? subjectivity = "Subjetivo" : subjectivity = "Objetivo")
                var $subjectivityFooter = $('<small></small>').html(
                    ' &ndash; <strong>' + subjectivity + '</strong> ' +
                    '(' + (parseFloat(comment.subjectivity_score) * 100).toFixed(2) + '%)'
                );
                $subjectivityFooter.appendTo($listItem);
            }

            $listItem.appendTo($container);
        });

        feather.replace({
            height: 16,
            width: 16
        });
    }

    function generateReadMore() {
        $("p.readmore").readmore({
            speed: 75,
            moreLink: '<a href="#" class="readmore" title="Leer más"><i data-feather="plus-circle"></i></a>"',
            lessLink: '<a href="#" class="readmore" title="Leer menos"><i data-feather="minus-circle"></i></a>"',
            afterToggle: function (trigger, element, expanded) {
                feather.replace({
                    height: 24,
                    width: 24
                });
            }
        });
        feather.replace({
            height: 24,
            width: 24
        });
    }

    function saveSearch() {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: ctx + "/searches/save",
            timeout: 5000
        }));
    }

    // Gráfico tarta
    var search = <%=searchJSON.toString()%>;
    var pieChart = renderPie(search, $("#pieChart"));
    var barChart = renderBar(search, $("#barChart"));
    var timeChart = renderTime(search, $("#timeChart"), 'day');
    if (search.subjectivity !== "No")
        var scatterChart = renderScatter(search, $("#scatterChart"));

    // Listener para el cambio de escala
    $("input[name=selectScale]").change(function (e) {
        timeChart.destroy();
        timeChart = renderTime(search, $("#timeChart"), $(this).val());
    });

</script>
<%@ include file="_footer.jsp"%>
