<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<form:form method="post" modelAttribute="modelForm" action="${path}/models/create" enctype="multipart/form-data">
    <!-- Seleccionar tipo de clasificador -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Tipo de Clasificador</h5>
            <div class="row">
                <div class="col-3" style="padding: 8px 15px;">
                    <div class="custom-control custom-radio custom-control-inline">
                        <input class="custom-control-input" type="radio" name="classifierType" id="classifyPolarity" checked="checked" value="polarity">
                        <label class="custom-control-label" for="classifyPolarity">Polaridad</label>
                    </div>
                    <div class="custom-control custom-radio custom-control-inline">
                        <input class="custom-control-input" type="radio" name="classifierType" id="classifySubjectivity" value="subjectivity">
                        <label class="custom-control-label" for="classifySubjectivity">Subjetividad</label>
                    </div>
                </div>
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
                <spring:bind path="language">
                    <div class="col-3">
                        <div class="form-group">
                            <form:label path="language">Idioma</form:label>
                            <form:select path="language" cssClass="form-control">
                                <form:option value="es">Español</form:option>
                                <form:option value="en">Inglés</form:option>
                            </form:select>
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
                <spring:bind path="open">
                    <div class="col-2">
                        <p>Público</p>
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="open" id="openYes" value="true" cssClass="custom-control-input"/>
                            <form:label path="open" for="openYes" cssClass="custom-control-label">Sí</form:label>
                        </div>
                        <div class="custom-control custom-radio custom-control-inline">
                            <form:radiobutton path="open" id="openNo" value="false" checked="checked" cssClass="custom-control-input"/>
                            <form:label path="open" for="openNo" cssClass="custom-control-label">No</form:label>
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
                <spring:bind path="psText">
                    <div class="col-12 text-datasets form-group ${status.error ? "has-error" : ""}">
                        <form:label path="psText">Comentarios positivos</form:label>
                        <form:textarea path="psText" cssClass="form-control" rows="5"></form:textarea>
                        <form:errors path="psText" cssClass="help-block" id="errorsPsText"></form:errors>
                    </div>
                </spring:bind>
                <spring:bind path="noText">
                    <div class="col-12 text-datasets form-group ${status.error ? "has-error" : ""}">
                        <form:label path="noText">Comentarios negativos</form:label>
                        <form:textarea path="noText" cssClass="form-control" rows="5"></form:textarea>
                        <form:errors path="noText" cssClass="help-block" id="errorsNoText"></form:errors>
                    </div>
                </spring:bind>
                <div class="col-12 file-datasets">
                    <spring:bind path="psFile">
                        <div class="custom-file mb-3">
                            <input type="file" class="custom-file-input" name="psFile" id="psFile" lang="es">
                            <label class="custom-file-label" for="psFile"></label>
                        </div>
                    </spring:bind>
                    <spring:bind path="noFile">
                        <div class="custom-file mb-3">
                            <input type="file" class="custom-file-input" name="noFile" id="noFile" lang="es">
                            <label class="custom-file-label" for="noFile"></label>
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

<%@ include file="_js.jsp"%>
<script type="text/javascript" src="${path}/js/create_models.js"></script>

<script>
    $(document).ready(function(){
        constructClassifiersSelect();
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

        // Cambiar texto de etiquetas para los datasets en función del tipo de clasificador (polaridad/subjetividad)
        switchDatasetTags();
    });

    /* Acción al cambiar el tipo de dataset a utilizar (texto/archivos) */
    $("input[name='textDataset']").change(function() {
        $(".text-datasets").toggle();
        $(".file-datasets").toggle();
    });
</script>


<%@ include file="_footer.jsp"%>