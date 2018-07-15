<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../_header.jsp"%>

<form:form method="post" modelAttribute="modelForm" action="${path}/models/create" enctype="multipart/form-data">
    <!-- Seleccionar tipo de clasificador -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Tipo de Clasificador</h5>
            <div class="row">
                <div class="col-3" style="padding: 8px 15px;">
                    <div class="custom-control custom-radio custom-control-inline">
                        <input class="custom-control-input" type="radio" name="classifierType" id="classifyPolarity" checked="checked" value="POLARITY">
                        <label class="custom-control-label" for="classifyPolarity">Polaridad</label>
                    </div>
                    <div class="custom-control custom-radio custom-control-inline">
                        <input class="custom-control-input" type="radio" name="classifierType" id="classifyOpinion" value="OPINION">
                        <label class="custom-control-label" for="classifyOpinion">Opinión</label>
                    </div>
                </div>
                <spring:bind path="language">
                    <div class="col-3">
                        <div class="form-group">
                            <form:select path="language" cssClass="form-control">
                                <form:option value="es">Español</form:option>
                                <form:option value="en">Inglés</form:option>
                            </form:select>
                        </div>
                    </div>
                </spring:bind>
                <div class="col-3 form-group">
                    <select id="adapterSelect" name="adapterSelect" class="form-control"></select>
                </div>
            </div>
        </div>
    </div>
    <!-- Rellenar información nuevo modelo -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Nuevo modelo</h5>
            <div class="row">
                <spring:bind path="name">
                    <div class="col-6">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="name">Nombre del modelo</form:label>
                            <form:input path="name" type="text" min="1" cssClass="form-control" aria-describedby="errorsName"></form:input>
                            <form:errors path="name" cssClass="help-block" id="errorsName"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="location">
                    <div class="col-6">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="location">Localización </form:label>
                            <a href="javascript:void(0);" data-toggle="popover" data-content="Directorio donde se guardará el modelo creado">[?]</a>
                            <form:input path="location" type="text" min="1" cssClass="form-control" aria-describedby="errorsLocation"></form:input>
                            <form:errors path="location" cssClass="help-block" id="errorsLocation"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="trainable">
                    <div class="col-2">
                        <p>Entrenable</p>
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="trainable" id="trainableYes" value="true" checked="checked" cssClass="custom-control-input"/>
                            <form:label path="trainable" for="trainableYes" cssClass="custom-control-label">Sí</form:label>
                        </div>
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="trainable" id="trainableNo" value="false" cssClass="custom-control-input"/>
                            <form:label path="trainable" for="trainableNo" cssClass="custom-control-label">No</form:label>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="public">
                    <div class="col-2">
                        <p>Público</p>
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="public" id="publicYes" value="true" cssClass="custom-control-input"/>
                            <form:label path="public" for="publicYes" cssClass="custom-control-label">Sí</form:label>
                        </div>
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="public" id="publicNo" value="false" checked="checked" cssClass="custom-control-input"/>
                            <form:label path="public" for="publicNo" cssClass="custom-control-label">No</form:label>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="description">
                    <div class="col-12">
                        <div class="form-group">
                            <form:label path="description">Descripción del modelo</form:label>
                            <form:textarea path="description" cssClass="form-control" rows="5"></form:textarea>
                        </div>
                    </div>
                </spring:bind>
                <form:hidden path="adapterClass"></form:hidden>
            </div>
        </div>
    </div>

    <!-- Contenedor de parámetros para la creación de clasificadores (se rellena por javascript) -->
    <div class="parameters-container"></div>

    <!-- Subir o introducir Datasets -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Datasets</h5>
            <div class="row">
                <spring:bind path="textDataset">
                    <div class="col-12 mb-3">
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="textDataset" value="true" cssClass="custom-control-input" id="textTrue"/>
                            <form:label path="textDataset" for="textTrue" cssClass="custom-control-label">Texto</form:label>
                        </div>
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="textDataset" value="false" cssClass="custom-control-input" id="fileTrue"/>
                            <form:label path="textDataset" for="fileTrue" cssClass="custom-control-label">Archivos</form:label>
                        </div>
                    </div>
                </spring:bind>
                <div class="polarity-datasets col-12" style="display: none;">
                    <spring:bind path="positivesText">
                        <div class="col-12 text-datasets form-group ${status.error ? "has-error" : ""}">
                            <form:label path="positivesText">Comentarios positivos</form:label>
                            <form:textarea path="positivesText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="positivesText" cssClass="help-block" id="errorsPositivesText"></form:errors>
                        </div>
                    </spring:bind>
                    <spring:bind path="negativesText">
                        <div class="col-12 text-datasets form-group ${status.error ? "has-error" : ""}">
                            <form:label path="negativesText">Comentarios negativos</form:label>
                            <form:textarea path="negativesText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="negativesText" cssClass="help-block" id="errorsNegativesText"></form:errors>
                        </div>
                    </spring:bind>
                    <spring:bind path="neutralsText">
                        <div class="col-12 text-datasets form-group ${status.error ? "has-error" : ""}">
                            <form:label path="neutralsText">Comentarios neutrales</form:label>
                            <form:textarea path="neutralsText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="neutralsText" cssClass="help-block" id="errorsNeutralsText"></form:errors>
                        </div>
                    </spring:bind>
                    <spring:bind path="positivesFile">
                        <div class="custom-file mb-3 col-12 file-datasets">
                            <input type="file" class="custom-file-input" name="positivesFile" id="positivesFile" lang="es">
                            <label class="custom-file-label" for="positivesFile">Comentarios positivos</label>
                        </div>
                    </spring:bind>
                    <spring:bind path="negativesFile">
                        <div class="custom-file mb-3 col-12 file-datasets">
                            <input type="file" class="custom-file-input" name="negativesFile" id="negativesFile" lang="es">
                            <label class="custom-file-label" for="negativesFile">Comentarios negativos</label>
                        </div>
                    </spring:bind>
                    <spring:bind path="neutralsFile">
                        <div class="custom-file mb-3 col-12 file-datasets">
                            <input type="file" class="custom-file-input" name="neutralsFile" id="neutralsFile" lang="es">
                            <label class="custom-file-label" for="neutralsFile">Comentarios neutrales</label>
                        </div>
                    </spring:bind>
                </div>
                <div class="opinion-datasets col-12" style="display: none;">
                    <spring:bind path="subjectivesText">
                        <div class="col-12 text-datasets form-group ${status.error ? "has-error" : ""}">
                            <form:label path="subjectivesText">Comentarios subjetivos</form:label>
                            <form:textarea path="subjectivesText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="subjectivesText" cssClass="help-block" id="errorsSubjectivesText"></form:errors>
                        </div>
                    </spring:bind>
                    <spring:bind path="objectivesText">
                        <div class="col-12 text-datasets form-group ${status.error ? "has-error" : ""}">
                            <form:label path="objectivesText">Comentarios objetivos</form:label>
                            <form:textarea path="objectivesText" cssClass="form-control" rows="5"></form:textarea>
                            <form:errors path="objectivesText" cssClass="help-block" id="errorsObjectivesText"></form:errors>
                        </div>
                    </spring:bind>
                    <spring:bind path="subjectivesFile">
                        <div class="custom-file mb-3 col-12 file-datasets">
                            <input type="file" class="custom-file-input" name="subjectivesFile" id="subjectivesFile" lang="es">
                            <label class="custom-file-label" for="subjectivesFile">Comentarios subjetivos</label>
                        </div>
                    </spring:bind>
                    <spring:bind path="objectivesFile">
                        <div class="custom-file mb-3 col-12 file-datasets">
                            <input type="file" class="custom-file-input" name="objectivesFile" id="objectivesFile" lang="es">
                            <label class="custom-file-label" for="objectivesFile">Comentarios objetivos</label>
                        </div>
                    </spring:bind>
                </div>
            </div>
        </div>
    </div>
    <!-- Enviar formulario -->
    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-lg btn-block">Crear Modelo</button>
        </div>
    </div>
</form:form>

<%@ include file="../_js.jsp"%>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    $(document).ready(function(){
        constructClassifiersSelect();

        // Mostrar los inputs adecuados para los datasets en función del tipo de clasificador seleccionado
        if ($("input[name='classifierType']").val() === "POLARITY") {
            $('.polarity-datasets').show();
            $('.opinion-datasets').hide();
        }
        else if ($("input[name='classifierType']").val() === "OPINION") {
            $('.polarity-datasets').hide();
            $('.opinion-datasets').show();
        }

        // Activar tooltips
        $('[data-toggle="popover"]').popover({
            placement: "right",
            trigger: "focus"
        });

        // Mostrar/ocultar textareas y fileinputs para los datasets
        if ($("input[name='textDataset']").val() === "true") {
            $(".text-datasets").show();
            $(".file-datasets").hide();
        } else {
            $(".text-datasets").hide();
            $(".file-datasets").show();
        }
    });

    /* Acción al cambiar idioma */
    $('#language').change(function () {
        constructClassifiersSelect();
    });

    /* Acción al cambiar el tipo de dataset a utilizar (texto/archivos) */
    $("input[name='textDataset']").change(function() {
        $(".text-datasets").toggle();
        $(".file-datasets").toggle();
    });
</script>


<%@ include file="../_footer.jsp"%>