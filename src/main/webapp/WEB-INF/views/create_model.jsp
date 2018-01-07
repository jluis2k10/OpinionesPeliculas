<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<form:form method="post" modelAttribute="modelForm" action="${path}/models/create" enctype="multipart/form-data">
    <div class="row">
        <fieldset class="col-xs-12">
            <legend>Tipo de Clasificador</legend>
            <div class="col-xs-12">
                <div class="col-xs-3 form-group">
                    <p></p>
                    <label class="radio-inline">
                        <input type="radio" name="classifierType" value="polarity" id="classifyPolarity" checked="checked"> Polaridad
                    </label>
                    <label class="radio-inline">
                        <input type="radio" name="classifierType" value="subjectivity" id="classifySubjectivity"> Subjetividad
                    </label>
                </div>
                <div class="col-xs-3 form-group">
                    <select id="adapterSelect" name="adapterSelect" class="form-control"></select>
                </div>
            </div>
        </fieldset>
        <fieldset class="col-xs-12">
            <legend>Nuevo modelo</legend>
            <spring:bind path="name">
                <div class="col-xs-6">
                    <div class="form-group ${status.error ? "has-error" : ""}">
                        <form:label path="name">Nombre del modelo</form:label>
                        <form:input path="name" type="text" min="1" cssClass="form-control" aria-describedby="errorsName"></form:input>
                        <form:errors path="name" cssClass="help-block" id="errorsName"></form:errors>
                    </div>
                </div>
            </spring:bind>
            <spring:bind path="location">
                <div class="col-xs-6">
                    <div class="form-group ${status.error ? "has-error" : ""}">
                        <form:label path="location">Localización </form:label>
                        <a href="javascript:void(0);" data-toggle="popover" data-content="Directorio donde se guardará el modelo creado">[?]</a>
                        <form:input path="location" type="text" min="1" cssClass="form-control" aria-describedby="errorsLocation"></form:input>
                        <form:errors path="location" cssClass="help-block" id="errorsLocation"></form:errors>
                    </div>
                </div>
            </spring:bind>
            <spring:bind path="language">
                <div class="col-xs-3">
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
                <div class="col-xs-2">
                    <div class="form-group">
                        <p><strong>Entrenable</strong></p>
                        <label class="radio-inline">
                            <form:radiobutton path="trainable" value="true" checked="checked"></form:radiobutton> Sí
                        </label>
                        <label class="radio-inline">
                            <form:radiobutton path="trainable" value="false"></form:radiobutton> No
                        </label>
                    </div>
                </div>
            </spring:bind>
            <spring:bind path="open">
                <div class="col-xs-2">
                    <div class="form-group">
                        <p><strong>Público</strong></p>
                        <label class="radio-inline">
                            <form:radiobutton path="open" value="true" checked="checked"></form:radiobutton> Sí
                        </label>
                        <label class="radio-inline">
                            <form:radiobutton path="open" value="false"></form:radiobutton> No
                        </label>
                    </div>
                </div>
            </spring:bind>
            <spring:bind path="description">
                <div class="col-xs-12">
                    <div class="form-group">
                        <form:label path="description">Descripción del modelo</form:label>
                        <form:textarea path="description" cssClass="form-control" rows="5"></form:textarea>
                    </div>
                </div>
            </spring:bind>
            <form:hidden path="adapterClass"></form:hidden>
        </fieldset>
    </div>
    <div class="row parameters-container">

    </div>
    <!-- Subir o introducir Datasets -->
    <div class="row datasets-container">
        <fieldset class="col-xs-12">
            <legend>Datasets</legend>
            <spring:bind path="textDataset">
                <div class="col-xs-3">
                    <div class="form-group">
                        <p></p>
                        <label class="radio-inline">
                            <form:radiobutton path="textDataset" value="true"></form:radiobutton> Texto
                        </label>
                        <label class="radio-inline">
                            <form:radiobutton path="textDataset" value="false"></form:radiobutton> Archivos
                        </label>
                    </div>
                </div>
            </spring:bind>
            <div class="text-datasets">
                <spring:bind path="psText">
                    <div class="col-xs-12 form-group ${status.error ? "has-error" : ""}">
                        <form:label path="psText">Comentarios positivos</form:label>
                        <form:textarea path="psText" cssClass="form-control" rows="5"></form:textarea>
                        <form:errors path="psText" cssClass="help-block" id="errorsPsText"></form:errors>
                    </div>
                </spring:bind>
                <spring:bind path="noText">
                    <div class="col-xs-12 form-group ${status.error ? "has-error" : ""}">
                        <form:label path="noText">Comentarios negativos</form:label>
                        <form:textarea path="noText" cssClass="form-control" rows="5"></form:textarea>
                        <form:errors path="noText" cssClass="help-block" id="errorsNoText"></form:errors>
                    </div>
                </spring:bind>
            </div>
            <div class="file-datasets">
                <spring:bind path="psFile">
                    <div class="col-xs-12 form-group">
                        <label for="psFile">Comentarios positivos</label>
                        <input type="file" name="psFile" id="psFile">
                    </div>
                </spring:bind>
                <spring:bind path="noFile">
                    <div class="col-xs-12 form-group">
                        <label for="noFile">Comentarios negativos</label>
                        <input type="file" name="noFile" id="noFile">
                    </div>
                </spring:bind>
            </div>
        </fieldset>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <button type="submit" class="btn btn-primary">Crear modelo</button>
        </div>
    </div>
</form:form>

<%@ include file="_js.jsp"%>
<script type="text/javascript" src="${path}/js/create_models.js"></script>

<script>
    var classifiers = null;

    $.when(getClassifiers())
        .done(function(_classifiers) {
            classifiers = _classifiers;
            var $adapterSelect = $("#adapterSelect");
            $adapterSelect.empty();
            $.each(classifiers, function(index, classifier) {
                $adapterSelect.append(new Option(classifier.name, classifier.class));
            });
            // Construir el formulario para los parámetros disponibles para la creación del modelo
            populateClassifierParameters(getSelectedClassifier(classifiers));
        })
        .fail(function () {
            console.error("Error recuperando los clasificadors.")
        })

    $(document).ready(function(){
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

    /* Acción al cambiar el tipo de clasificador */
    $("input[name='classifierType']").change(function() {
        switchDatasetTags();
    });

    /* Acción al seleccionar el clasificador */
    $("select[name='adapterSelect']").change(function() {
        populateClassifierParameters(getSelectedClassifier(classifiers));
    });

    /* Acción al seleccionar una de las opciones de los parámetros para crear un nuevo modelo
     * Podemos tener "subparámetros" para las opciones de los parámetros. */
    $(".parameters-container").on('change', 'select, input:checked', function () {
        attachOptionParameters($(this), getSelectedClassifier(classifiers));
    });

    /* Acción al cambiar el tipo de dataset a utilizar (texto/archivos) */
    $("input[name='textDataset']").change(function() {
        $(".text-datasets").toggle();
        $(".file-datasets").toggle();
    });
</script>


<%@ include file="_footer.jsp"%>