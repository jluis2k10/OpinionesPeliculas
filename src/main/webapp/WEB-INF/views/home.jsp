<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>

<div class="row">
    <form:form method="post" modelAttribute="searchForm">
        <spring:bind path="searchTerm">
            <div class="col-xs-12 ${status.error ? "has-error" : ""}">
                <div class="input-group">
                    <div class="input-group-btn">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Origen <span class="caret"></span></button>
                        <ul class="dropdown-menu" id="sources-dropdown"></ul>
                    </div>
                    <form:input path="searchTerm" type="text" cssClass="form-control" placeholder="Término de búsqueda" aria-describedby="errorsSearchTerm"></form:input>
                    <div class="input-group-addon source-placeholder"></div>
                </div>
                <form:errors path="searchTerm" cssClass="help-block" id="errorsSearchTerm"></form:errors>
            </div>
        </spring:bind>
        <spring:bind path="limit">
            <div class="col-xs-3 limit-container" style="display: none;">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="limit">Comentarios a recuperar (máx.)</form:label>
                    <form:input path="limit" type="text" cssClass="form-control" id="limit" value="50" aria-describedby="errorsLimit"></form:input>
                    <form:errors path="limit" cssClass="help-block" id="errorsLimit"></form:errors>
                </div>
            </div>
        </spring:bind>
        <spring:bind path="sinceDate">
            <div class="col-xs-3 sinceDate-container" style="display: none;">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="sinceDate">Desde</form:label>
                    <form:input path="sinceDate" type="text" cssClass="form-control" id="sinceDate" placeholder="DD/MM/AAAA" aria-describedby="errorsSinceDate"></form:input>
                    <form:errors path="sinceDate" cssClass="help-block" id="errorsSinceDate"></form:errors>
                </div>
            </div>
        </spring:bind>
        <spring:bind path="untilDate">
            <div class="col-xs-3 untilDate-container" style="display: none;">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="untilDate">Hasta</form:label>
                    <form:input path="untilDate" type="text" cssClass="form-control" id="untilDate" placeholder="DD/MM/AAAA" aria-describedby="errorsUntilDate"></form:input>
                    <form:errors path="untilDate" cssClass="help-block" id="errorsUntilDate"></form:errors>
                </div>
            </div>
        </spring:bind>
        <spring:bind path="lang">
            <div class="col-xs-3 language-container ${status.error ? "has-error" : ""}" style="display: none;">
                <p><strong>Idioma</strong></p>
                <label class="radio-inline">
                    <form:radiobutton path="lang" id="en" value="en" checked="checked"></form:radiobutton> Inglés
                </label>
                <label class="radio-inline">
                    <form:radiobutton path="lang" id="es" value="es"></form:radiobutton> Español
                </label>
            </div>
        </spring:bind>
        <div class="col-xs-3 imdbID-container" style="display: none;">
            <label for="imdbID">Película</label>
            <select class="imdb-select form-control" id="imdbID">
                <option value=""></option>
            </select>
        </div>
        <form:hidden path="sourceClass" value="" id="sourceClass"></form:hidden>
        <div class="col-xs-12">
            <button type="submit" class="btn btn-primary">Enviar</button>
        </div>
    </form:form>
    <div class="col-xs-12">
        <c:if test="${!empty comments}">
            <ol>
            <c:forEach var="comment" items="${comments}">
                <li><c:out value="${comment.value}"></c:out></li>
            </c:forEach>
            </ol>
        </c:if>
    </div>
</div>

<%@ include file="_js.jsp"%>
<link rel="stylesheet" href="${path}/css/bootstrap-datetimepicker.min.css" />
<link rel="stylesheet" href="${path}/css/select2.min.css" />
<link rel="stylesheet" href="${path}/css/select2-bootstrap.min.css" />
<script type="text/javascript" src="${path}/js/select2.min.js"></script>
<script type="text/javascript" src="${path}/js/select2.es.js"></script>
<script type="text/javascript" src="${path}/js/moment-with-locales.min.js"></script>
<script type="text/javascript" src="${path}/js/transition.js"></script>
<script type="text/javascript" src="${path}/js/collapse.js"></script>
<script type="text/javascript" src="${path}/js/bootstrap-datetimepicker.min.js"></script>

<script>
    /* Select para elegir película y encontrar su identificador en IMDB */
    $('.imdb-select').select2({
        theme: "bootstrap",
        placeholder: "Título de película",
        language: "es",
        ajax: {
            url: "${path}/api/imdb-lookup",
            dataType: 'json',
            delay: 250,
            data: function(params) {
                return {
                    q: params.term,
                    page: params.page
                };
            },
            processResults: function(data, params) {
                params.page = params.page || 1;
                // Eliminamos de los resultados los que no tengan imdbID
                for (var i=0; i<data.films.length; i++) {
                    if (!data.films[i].imdbID)
                        data.films.splice(i, 1);
                }
                // select2 necesita atributos id y text en el objeto que maneja
                var select2Data = $.map(data.films, function (obj) {
                    obj.id = obj.id || obj.imdbID;
                    obj.text = obj.text || obj.title;
                    return obj;
                });
                return {
                    results: select2Data,
                    pagination: {
                        more: (params.page * 10) < data.total_count
                    }
                };
            },
            cache: true
        },
        escapeMarkup: function(markup) {
            return markup;
        },
        minimumInputLength: 1,
        templateResult: function(result) {
            if (result.loading) return result.text;
            return result.text + " (" + result.year + ")";
        },
        templateSelection: function(result) {
            return result.title || result.text;
        }
    });

    // Al seleccionar la película pasamos el imdbID al input de la búsqueda
    $('.imdb-select').on('select2:selecting', function(e) {
        $('#searchTerm').val(e.params.args.data.id);
    });
</script>
<script>
    $( document ).ready(function() {
        /* Recuperar las posibles fuentes de comentarios */
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/api/comments-source",
            timeout: 5000,
            success: function(data) {
                makeSourcesButton(data);
                var first_link = $("#sources-dropdown li:first-child a");
                makeOptions(first_link.get(0));
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.error("Request: " + JSON.stringify(XMLHttpRequest) + "\n\nStatus: " + textStatus + "\n\nError: " + errorThrown);
            }
        });
    });

    /* Inicializar selectores de fecha */
    $(function () {
        $('#sinceDate').datetimepicker({
            format: 'DD/MM/YYYY',
            locale: 'es'
        });
        $('#untilDate').datetimepicker({
            format: 'DD/MM/YYYY',
            locale: 'es'
        });
    });

    /* Crear enlaces con las fuentes de comentarios posibles */
    function makeSourcesButton(sources) {
        $.each(sources, function (index, source) {
            $("#sources-dropdown").append("<li><a href='javascript:void(0)' onclick='makeOptions(this); return false;'" +
                "data-name='"+source.name+"'" +
                "data-adapter='"+source.adapterClass+"'" +
                "data-sinceDate='"+source.sinceDateEnabled+"'" +
                "data-untilDate='"+source.untilDateEnabled+"'" +
                "data-limit='"+source.limitEnabled+"'" +
                "data-imdbID='"+source.imdbIDEnabled+"'" +
                "data-language='"+source.languageEnabled+"'>" + source.name + "</a></li>");
        })
    }

    /* Presentar los elementos del formulario necesarios según la fuente de comentarios */
    function makeOptions(e) {
        $(".source-placeholder").html(e.dataset.name);
        $("#sourceClass").val(e.dataset.adapter);
        if (e.dataset.imdbid === "true") {
            $("#searchTerm").attr("disabled", "disabled");
            $(".imdbID-container").show();
            $("span.select2-container").width("100%");
        } else {
            $("#searchTerm").removeAttr("disabled");
            $(".imdbID-container").hide();
        }
        if (e.dataset.limit === "true") {
            $(".limit-container").show();
        } else {
            $(".limit-container").hide();
        }
        if (e.dataset.sincedate === "true"){
            $(".sinceDate-container").show();
        } else {
            $(".sinceDate-container").hide();
        }
        if (e.dataset.untildate === "true"){
            $(".untilDate-container").show();
        } else {
            $(".untilDate-container").hide();
        }
        if (e.dataset.language === "true") {
            $(".language-container").show();
        } else {
            $(".language-container").hide();
        }
    }
</script>
<%@ include file="_footer.jsp"%>