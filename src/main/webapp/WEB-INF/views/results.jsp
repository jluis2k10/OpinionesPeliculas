<%@ page import="es.uned.entities.CommentWithSentiment" %>
<%@ page import="es.uned.entities.Search" %>
<%@ page import="com.fasterxml.jackson.databind.node.ObjectNode" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>
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
<script type="text/javascript" src="${path}/js/custom.js"></script>
<script>
    $(document).ready(function() {
        myPagination(5);

        /* Recuperar token csrf para incluirlo como cabecera en cada envío ajax */
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function (e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
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
        $("#comments-list").empty();
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
                feather.replace({
                    height: 16,
                    width: 16
                });
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

            var $mainContent = $('<p></p>').addClass('card-text mb-2').html(comment.comment);
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
    }

    function saveSearch() {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: "/searches/save",
            timeout: 5000
        }));
    }
</script>
<%@ include file="_footer.jsp"%>
