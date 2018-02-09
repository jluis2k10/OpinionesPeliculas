<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<c:choose>
    <c:when test="${trainForm.adapterType.adapterType.equals('Sentiment')}">
        <c:set var="psText" value="positivos"/>
        <c:set var="noText" value="negativos"/>
    </c:when>
    <c:otherwise>
        <c:set var="psText" value="subjetivos"/>
        <c:set var="noText" value="objetivos"/>
    </c:otherwise>
</c:choose>
<form:form method="post" modelAttribute="trainForm" enctype="multipart/form-data">
    <!-- Fuente de comentarios para entrenar -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Fuente para entrenamiento</h5>
            <div class="row sources-container">
                <spring:bind path="term">
                    <div class="col-12 ${status.error ? "has-error" : ""}">
                        <div class="input-group form-group">
                            <div class="input-group-prepend">
                                <button class="btn btn-primary dropdown-toggle sources-dropdown" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Origen</button>
                                <div id="sources-dropdown" class="dropdown-menu"></div>
                            </div>
                            <form:input path="term" type="text" cssClass="form-control" placeholder="Término de búsqueda" aria-describedby="errorsSearchTerm"></form:input>
                        </div>
                        <form:errors path="term" cssClass="help-block" id="errorsSearchTerm"></form:errors>
                    </div>
                </spring:bind>
                <spring:bind path="limit">
                    <div class="col-3 limit-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="limit">Comentarios a recuperar (máx.)</form:label>
                            <form:input path="limit" type="number" min="1" cssClass="form-control" id="limit" value="50" aria-describedby="errorsLimit"></form:input>
                            <form:errors path="limit" cssClass="help-block" id="errorsLimit"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="sinceDate">
                    <div class="col-3 sinceDate-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="sinceDate">Desde</form:label>
                            <form:input path="sinceDate" type="text" cssClass="form-control" id="sinceDate" placeholder="DD/MM/AAAA" aria-describedby="errorsSinceDate"></form:input>
                            <form:errors path="sinceDate" cssClass="help-block" id="errorsSinceDate"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="untilDate">
                    <div class="col-3 untilDate-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="untilDate">Hasta</form:label>
                            <form:input path="untilDate" type="text" cssClass="form-control" id="untilDate" placeholder="DD/MM/AAAA" aria-describedby="errorsUntilDate"></form:input>
                            <form:errors path="untilDate" cssClass="help-block" id="errorsUntilDate"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <div class="col-3 imdbID-container" style="display: none;">
                    <label for="imdbID">Película</label>
                    <select class="imdb-select form-control" id="imdbID">
                        <option value=""></option>
                    </select>
                </div>
            </div>
        </div>
    </div>
    <!-- Fin fuentes de comentarios -->

    <!-- Subir o introducir Datasets -->
    <div class="card mb-4 border-secondary bg-light datasets-container" style="display: none;">
        <div class="card-body">
            <h5 class="card-title mb-4">Datasets</h5>
            <div class="row">
                <spring:bind path="psText">
                    <div class="col-12 text-datasets">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="psText">Comentarios ${psText}</form:label>
                            <form:textarea path="psText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="psText" cssClass="help-block" id="errorsPsText"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="noText">
                    <div class="col-12 text-datasets">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="noText">Comentarios ${noText}</form:label>
                            <form:textarea path="noText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="noText" cssClass="help-block" id="errorsNoText"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="psFile">
                    <div class="custom-file mb-3 file-datasets">
                        <input type="file" class="custom-file-input" name="psFile" id="psFile" lang="es">
                        <label class="custom-file-label" for="psFile">Comentarios ${psText}</label>
                    </div>
                </spring:bind>
                <spring:bind path="noFile">
                    <div class="custom-file mb-3 file-datasets">
                        <input type="file" class="custom-file-input" name="noFile" id="noFile" lang="es">
                        <label class="custom-file-label" for="noFile">Comentarios ${noText}</label>
                    </div>
                </spring:bind>
            </div>
        </div>
    </div>
    <!-- Fin introducir datasets -->
    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-lg btn-block">Entrenar modelo</button>
        </div>
    </div>
    <form:hidden path="lang"></form:hidden>
    <form:hidden path="adapterType"></form:hidden>
    <form:hidden path="modelLocation"></form:hidden>
    <form:hidden path="adapterClass"></form:hidden>
    <form:hidden path="sourceClass" value="" id="sourceClass"></form:hidden>
</form:form>

<%@ include file="_js.jsp"%>
<link rel="stylesheet" href="${path}/css/select2.min.css" />
<link rel="stylesheet" href="${path}/css/select2-bootstrap.min.css" />
<link rel="stylesheet" href="${path}/css/bootstrap-datetimepicker.min.css" />
<script type="text/javascript" src="${path}/js/select2.min.js"></script>
<script type="text/javascript" src="${path}/js/select2.es.js"></script>
<script type="text/javascript" src="${path}/js/moment-with-locales.min.js"></script>
<script type="text/javascript" src="${path}/js/transition.js"></script>
<script type="text/javascript" src="${path}/js/collapse.js"></script>
<script type="text/javascript" src="${path}/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="${path}/js/custom.js"></script>

<script>
    $.when(getCommentSources("${trainForm.lang}"))
        .done(function(_commentSources) {
            _commentSources = genExtraSources(_commentSources);
            makeSourcesButton(_commentSources);
            /* Añadir eventlistener para la acción al seleccionar una fuente de comentarios */
            $('.source-button').click(function () {
                makeSourceOptions($(this).get(0), _commentSources);
            });
        })
        .fail(function () {
            console.error("Error");
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

    // Select para elegir película y encontrar su identificador en IMDB
    createIMDBSelect("${path}");
    // Al seleccionar la película pasamos el imdbID al input de la búsqueda
    $('.imdb-select').on('select2:selecting', function(e) {
        $('#term').val(e.params.args.data.id);
    });
</script>

<%@ include file="_footer.jsp"%>
