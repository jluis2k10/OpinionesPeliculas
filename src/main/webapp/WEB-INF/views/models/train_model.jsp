<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../_header.jsp"%>

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
                            <form:input path="sinceDate" type="text" cssClass="form-control datetimepicker-input" id="sinceDate" placeholder="DD/MM/AAAA" aria-describedby="errorsSinceDate" data-toggle="datetimepicker" data-target="#sinceDate"></form:input>
                            <form:errors path="sinceDate" cssClass="help-block" id="errorsSinceDate"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="untilDate">
                    <div class="col-3 untilDate-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="untilDate">Hasta</form:label>
                            <form:input path="untilDate" type="text" cssClass="form-control datetimepicker-input" id="untilDate" placeholder="DD/MM/AAAA" aria-describedby="errorsUntilDate" data-toggle="datetimepicker" data-target="#untilDate"></form:input>
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
                <div class="col-12 file-container" style="display: none;">
                    <c:if test="${trainForm.classifierType == 'POLARITY'}">
                        <spring:bind path="positivesFile">
                            <div class="custom-file mb-3">
                                <label for="positivesFile" class="custom-file-label">Comentarios positivos</label>
                                <input type="file" class="custom-file-input" name="positivesFile" id="positivesFile" lang="es">
                            </div>
                        </spring:bind>
                        <spring:bind path="negativesFile">
                            <div class="custom-file mb-3">
                                <label for="negativesFile" class="custom-file-label">Comentarios negativos</label>
                                <input type="file" class="custom-file-input" name="negativesFile" id="negativesFile" lang="es">
                            </div>
                        </spring:bind>
                        <c:if test="${trainForm.neutralClassification}">
                            <spring:bind path="neutralsFile">
                                <div class="custom-file mb-3">
                                    <label for="neutralsFile" class="custom-file-label">Comentarios neutrales</label>
                                    <input type="file" class="custom-file-input" name="neutralsFile" id="neutralsFile" lang="es">
                                </div>
                            </spring:bind>
                        </c:if>
                    </c:if>
                    <c:if test="${trainForm.classifierType == 'OPINION'}">
                        <spring:bind path="subjectivesFile">
                            <div class="custom-file mb-3">
                                <label for="subjectivesFile" class="custom-file-label">Comentarios subjetivos</label>
                                <input type="file" class="custom-file-input" name="subjectivesFile" id="subjectivesFile" lang="es">
                            </div>
                        </spring:bind>
                        <spring:bind path="objectivesFile">
                            <div class="custom-file mb-3">
                                <label for="objectivesFile" class="custom-file-label">Comentarios objetivos</label>
                                <input type="file" class="custom-file-input" name="objectivesFile" id="objectivesFile" lang="es">
                            </div>
                        </spring:bind>
                    </c:if>
                </div>
                <div class="col-12 text-dataset-container" style="display: none;">
                    <c:if test="${trainForm.classifierType == 'POLARITY'}">
                        <spring:bind path="positivesText">
                            <div class="form-group ${status.error ? "has-error" : ""}">
                                <form:label path="positivesText">Comentarios positivos</form:label>
                                <form:textarea path="positivesText" cssClass="form-control" rows="5"></form:textarea>
                                <form:errors path="positivesText" cssClass="help-block" id="errorsPositivesText"></form:errors>
                            </div>
                        </spring:bind>
                        <spring:bind path="negativesText">
                            <div class="form-group ${status.error ? "has-error" : ""}">
                                <form:label path="negativesText">Comentarios negativos</form:label>
                                <form:textarea path="negativesText" cssClass="form-control" rows="5"></form:textarea>
                                <form:errors path="negativesText" cssClass="help-block" id="errorsNegativesText"></form:errors>
                            </div>
                        </spring:bind>
                        <c:if test="${trainForm.neutralClassification}">
                            <spring:bind path="neutralsText">
                                <div class="form-group ${status.error ? "has-error" : ""}">
                                    <form:label path="neutralsText">Comentarios neutrales</form:label>
                                    <form:textarea path="neutralsText" cssClass="form-control" rows="5"></form:textarea>
                                    <form:errors path="neutralsText" cssClass="help-block" id="errorsNeutralsText"></form:errors>
                                </div>
                            </spring:bind>
                        </c:if>
                    </c:if>
                    <c:if test="${trainForm.classifierType == 'OPINION'}">
                        <spring:bind path="subjectivesText">
                            <div class="form-group ${status.error ? "has-error" : ""}">
                                <form:label path="subjectivesText">Comentarios subjetivos</form:label>
                                <form:textarea path="subjectivesText" cssClass="form-control" rows="5"></form:textarea>
                                <form:errors path="subjectivesText" cssClass="help-block" id="errorsSubjectivesText"></form:errors>
                            </div>
                        </spring:bind>
                        <spring:bind path="objectivesText">
                            <div class="form-group ${status.error ? "has-error" : ""}">
                                <form:label path="objectivesText">Comentarios objetivos</form:label>
                                <form:textarea path="objectivesText" cssClass="form-control" rows="5"></form:textarea>
                                <form:errors path="objectivesText" cssClass="help-block" id="errorsObjectivesText"></form:errors>
                            </div>
                        </spring:bind>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
    <!-- Fin fuentes de comentarios -->
    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-lg btn-block">Entrenar modelo</button>
        </div>
    </div>
    <form:hidden path="lang"></form:hidden>
    <form:hidden path="classifierType"></form:hidden>
    <form:hidden path="modelLocation"></form:hidden>
    <form:hidden path="adapterClass"></form:hidden>
    <form:hidden path="neutralClassification"></form:hidden>
    <form:hidden path="sourceClass" value="" id="sourceClass"></form:hidden>
</form:form>

<%@ include file="../_js.jsp"%>
<link rel="stylesheet" href="${path}/webjars/tempusdominus-bootstrap-4/5.0.0-alpha.16/build/css/tempusdominus-bootstrap-4.min.css" />
<link rel="stylesheet" href="${path}/webjars/select2/4.0.3/css/select2.min.css" />
<link rel="stylesheet" href="${path}/css/select2-bootstrap.min.css" />
<script type="text/javascript" src="${path}/webjars/select2/4.0.3/js/select2.min.js"></script>
<script type="text/javascript" src="${path}/webjars/select2/4.0.3/js/i18n/es.js"></script>
<script type="text/javascript" src="${path}/webjars/momentjs/2.20.1/min/moment-with-locales.min.js"></script>
<script type="text/javascript" src="${path}/webjars/tempusdominus-bootstrap-4/5.0.0-alpha.16/build/js/tempusdominus-bootstrap-4.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    $(document).ready(function () {
        // Select para elegir película y encontrar su identificador en IMDB
        renderIMDBSelect();
        // Listener. Al seleccionar la película pasamos el imdbID al input de la búsqueda
        $('.imdb-select').on('select2:selecting', function(e) {
            $('#term').val(e.params.args.data.id);
        });

        /* Inicializar selectores de fecha */
        $(function () {
            $('#sinceDate').datetimepicker({
                viewmode: 'months',
                format: 'DD/MM/YYYY',
                locale: 'es'
            });
            $('#untilDate').datetimepicker({
                viewmode: 'months',
                format: 'DD/MM/YYYY',
                locale: 'es'
            });
        });
    });

    $.when(getCorporaSources("${trainForm.lang}"))
        .done(function(_commentSources) {
            generateExtraSources(_commentSources);
            renderSourcesButton(_commentSources, "${trainForm.lang}");
            // El primer elemento de las fuentes de comentarios es el seleccionado por defecto:
            $("input#sourceClass").val(_commentSources[0].adapterClass);
            /* Eventlistener para la acción al seleccionar una fuente de comentarios */
            $('.source-button').click(function () {
                var sourceName = $(this).get(0).dataset.name;
                var selectedSource = $.grep(_commentSources, function (source) {
                    return sourceName === source.name;
                })[0];
                renderSourceOptions(selectedSource, "${trainForm.lang}");
                $("input#sourceClass").val(selectedSource.adapterClass);
            });
        })
        .fail(function () {
            console.error("Error recuperando fuentes para comentarios.");
        });
</script>

<%@ include file="../_footer.jsp"%>
