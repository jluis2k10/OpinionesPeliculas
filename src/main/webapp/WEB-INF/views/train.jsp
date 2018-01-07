<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<form:form method="post" modelAttribute="trainForm" enctype="multipart/form-data">
    <!-- Fuente de comentarios para entrenar -->
    <div class="row">
        <fieldset class="col-xs-12">
            <legend>Fuente para entrenamiento</legend>
            <spring:bind path="term">
                <div class="col-xs-12 ${status.error ? "has-error" : ""}">
                    <div class="input-group form-group">
                        <div class="input-group-btn">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Origen <span class="caret"></span></button>
                            <ul class="dropdown-menu" id="sources-dropdown"></ul>
                        </div>
                        <form:input path="term" type="text" cssClass="form-control" placeholder="Término de búsqueda" aria-describedby="errorsSearchTerm"></form:input>
                        <div class="input-group-addon source-placeholder"></div>
                    </div>
                    <form:errors path="term" cssClass="help-block" id="errorsSearchTerm"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="limit">
                <div class="col-xs-3 limit-container" style="display: none;">
                    <div class="form-group ${status.error ? "has-error" : ""}">
                        <form:label path="limit">Comentarios a recuperar (máx.)</form:label>
                        <form:input path="limit" type="number" min="1" cssClass="form-control" id="limit" value="50" aria-describedby="errorsLimit"></form:input>
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

            <div class="col-xs-3 imdbID-container" style="display: none;">
                <label for="imdbID">Película</label>
                <select class="imdb-select form-control" id="imdbID">
                    <option value=""></option>
                </select>
            </div>
        </fieldset>
    </div> <!-- Fin fuentes de comentarios -->

    <!-- Subir o introducir Datasets -->
    <div class="row datasets-container" style="display: none;">
        <fieldset class="col-xs-12">
            <legend>Datasets</legend>
            <div class="text-datasets" style="display: none;">
                <spring:bind path="psText">
                    <div class="col-xs-12">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="psText">Comentarios positivos</form:label>
                            <form:textarea path="psText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="psText" cssClass="help-block" id="errorsPsText"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="noText">
                    <div class="col-xs-12">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="noText">Comentarios negativos</form:label>
                            <form:textarea path="noText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="noText" cssClass="help-block" id="errorsNoText"></form:errors>
                        </div>
                    </div>
                </spring:bind>
            </div>
            <div class="file-datasets" style="display: none;">
                <spring:bind path="psFile">
                    <div class="col-xs-12">
                        <div class="form-group">
                            <label for="psFile">Comentarios positivos</label>
                            <input type="file" name="psFile" id="psFile">
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="noFile">
                    <div class="col-xs-12">
                        <div class="form-group">
                            <label for="noFile">Comentarios negativos</label>
                            <input type="file" name="noFile" id="noFile">
                        </div>
                    </div>
                </spring:bind>
            </div>
        </fieldset>
    </div> <!-- Fin introducir datasets -->
    <div class="row">
        <form:hidden path="lang"></form:hidden>
        <form:hidden path="adapterType"></form:hidden>
        <form:hidden path="modelLocation"></form:hidden>
        <form:hidden path="adapterClass"></form:hidden>
        <form:hidden path="sourceClass" value="" id="sourceClass"></form:hidden>
        <div class="col-xs-12">
            <button type="submit" class="btn btn-primary">Entrenar modelo</button>
        </div>
    </div>
</form:form>

<%@ include file="_js.jsp"%>
<link rel="stylesheet" href="${path}/css/select2.min.css" />
<link rel="stylesheet" href="${path}/css/select2-bootstrap.min.css" />
<script type="text/javascript" src="${path}/js/select2.min.js"></script>
<script type="text/javascript" src="${path}/js/select2.es.js"></script>
<script type="text/javascript" src="${path}/js/custom.js"></script>

<script>
    $.when(getCommentSources("${trainForm.lang}"))
        .done(function(_commentSources) {
            _commentSources = genExtraSources(_commentSources);
            makeSourcesButton(_commentSources);
            /* Añadir eventlistener para la acción al seleccionar una fuente de comentarios */
            $('.sourceButton').click(function () {
                makeSourceOptions($(this).children(":first").get(0), _commentSources);
            });
        })
        .fail(function () {
            console.error("Error");
        });

    // Select para elegir película y encontrar su identificador en IMDB
    createIMDBSelect("${path}");
    // Al seleccionar la película pasamos el imdbID al input de la búsqueda
    $('.imdb-select').on('select2:selecting', function(e) {
        $('#term').val(e.params.args.data.id);
    });
</script>

<%@ include file="_footer.jsp"%>
