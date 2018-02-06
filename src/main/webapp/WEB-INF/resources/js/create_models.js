// Construir Input Select con los clasificadores de polaridad o de subjetividad
function constructClassifiersSelect() {
    $.when(getClassifiers())
        .done(function(_classifiers) {
            var $adapterSelect = $("#adapterSelect");
            $adapterSelect.empty();
            $.each(_classifiers, function(index, classifier) {
                $adapterSelect.append(new Option(classifier.name, classifier.class));
            });
            // Construir el formulario para los parámetros disponibles para la creación del modelo
            populateClassifierParameters(getSelectedClassifier(_classifiers));
            addListeners(_classifiers);
        })
        .fail(function () {
            console.error("Error recuperando los clasificadors.");
        })
}

// Añadir listeners para diferentes acciones
function addListeners(classifiers) {
    // Cambiar el tipo de clasificador
    $("input[name='classifierType']").change(function() {
        // Eliminamos listeners previos
        $("input[name='classifierType']").off();
        $("select[name='adapterSelect']").off();
        $(".parameters-container").off();

        constructClassifiersSelect();
        switchDatasetTags();
    });

    // Seleccionar el clasificador
    $("select[name='adapterSelect']").change(function() {
        populateClassifierParameters(getSelectedClassifier(classifiers));
    });

    /* Acción al seleccionar una de las opciones de los parámetros para crear un nuevo modelo
     * Podemos tener "subparámetros" para las opciones de los parámetros. */
    $(".parameters-container").on('change', 'select, input:checked', function () {
        attachOptionParameters($(this), getSelectedClassifier(classifiers));
    });
}

/* Recuperar clasificadores de polaridad o de subjetividad disponibles */
function getClassifiers() {
    var url = "/api/subjectivity-adapters?create_params=true";
    if ($("input[name='classifierType']:checked").val() === "polarity")
        url = "/api/sentiment-adapters?create_params=true";
    return Promise.resolve($.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        timeout: 5000
    }));
}

/* Obtener el clasificador seleccionado por el usuario */
function getSelectedClassifier(classifiers) {
    var adapterClass = $("#adapterSelect").val();
    $("#adapterClass").val(adapterClass); // Aprovechamos para cambiar el valor en el input oculto del nuevo modelo
    var selectedClassifier = $.grep(classifiers, function (classifier, index) {
        return (classifier.class === adapterClass);
    })[0];
    return selectedClassifier;
}

/* Crear formulario con las opciones para construir un nuevo modelo del clasificador seleccionado */
function populateClassifierParameters(classifier) {
    $(".parameters-container").empty();
    if (typeof classifier === 'undefined') return;
    // Cambiar también los idiomas posibles para el modelo en función de los aceptados por el adaptador
    var avaliableLanguages = classifier.lang.split(",");
    var $langSelect = $("#language");
    $langSelect.empty();
    $.each(avaliableLanguages, function(index, lang) {
        var $option = null;
        if (lang === "es")
            $option = $("<option></option>").attr("value", lang).text("Español");
        else if (lang === "en")
            $option = $("<option></option>").attr("value", lang).text("Inglés");
        $option.appendTo($langSelect);
    });

    // Construir los parámetros disponibles para la creación de un nuevo modelo
    $.each(classifier.model_creation_params, function (index, parameter) {
        var $outerDiv = $('<div class="card mb-4 border-secondary bg-light"></div>');
        var $bodyDiv = $('<div class="card-body"></div>');
        var $title = $('<h5 class="card-title mb-4">' + parameter.name + '</h5>');
        var $rowDiv = $('<div class="row"></div>');
        var $parameterHTML = makeParameter(parameter, "");

        $parameterHTML.appendTo($rowDiv);
        $title.appendTo($bodyDiv);
        $rowDiv.appendTo($bodyDiv);
        $bodyDiv.appendTo($outerDiv);
        $outerDiv.appendTo($(".parameters-container"));
        /*var $fieldset = $("<fieldset class='row'></fieldset>");
        var $legend = $("<legend>" + parameter.name + "</legend>");
        $legend.appendTo($fieldset);
        var $parameterHTML = makeParameter(parameter, "");
        $parameterHTML.appendTo($fieldset);
        $fieldset.appendTo($(".parameters-container"));*/
        var $select = $outerDiv.find('select, input:checked');
        attachOptionParameters($select, classifier);
    })
}

/* Crear formulario con parámetros opcionales del clasificador */
function attachOptionParameters($select, classifier) {
    var $rowDiv = $select.closest('div.row');
    $rowDiv.find("div.option-parameter").remove();

    var adapterParameter = $.grep(classifier.model_creation_params, function(parameter, index) {
        return (parameter.id === $select.attr("id") || parameter.id === $select.attr("name"));
    })[0];
    var selectedOption = $.grep(adapterParameter.options, function(option, index) {
        return (option.value === $select.val());
    })[0];

    if (selectedOption.parameters) {
        $.each(selectedOption.parameters, function(index, parameter) {
            var $parameterHTML = makeParameter(parameter, "option-parameter");
            $parameterHTML.appendTo($rowDiv);
        });
    }
}

/* Crear campos de formulario para cada parámetro opcional del clasificador */
function makeParameter(parameter, cssClass) {
    switch (parameter.type) {
        case "select":
            return makeSelectParameter(parameter, cssClass);
        case "text":
            return makeTextParameter(parameter, cssClass);
        case "number":
            return makeNumberParameter(parameter, cssClass);
        case "double":
            return makeDoubleParameter(parameter, cssClass);
        case "radio":
            return makeRadioParameter(parameter, cssClass);
        default:
            return null;
    }
}

/* Crear opción de tipo Select */
function makeSelectParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-3 form-group " + cssClass + "");
    var $label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    var $select = $("<select></select>").attr({
        id: parameter.id,
        name: parameter.id,
        class: "form-control parameter-options"
    });
    $.each(parameter.options, function (index, option) {
        var $option = $("<option></option>").attr("value", option.value).text(option.name);
        if (parameter.default === option.value)
            $option.prop("selected", true);
        $option.appendTo($select);
    });
    $label.appendTo($innerDiv);
    $select.appendTo($innerDiv);
    return $innerDiv;
}

/* Crear opción de tipo Campo de Texto */
function makeTextParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-3 form-group " + cssClass + "");
    var $label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    var $input = $("<input />").attr({
        type: "text",
        id: parameter.id,
        name: parameter.id,
        value: parameter.default,
        class: "form-control parameter-options"
    });
    $label.appendTo($innerDiv);
    $input.appendTo($innerDiv);
    return $innerDiv;
}

/* Crear opción de tipo numérico entero */
function makeNumberParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-3 form-group " + cssClass + "");
    var $label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    var $input = $("<input />").attr({
        type: 'number',
        id: parameter.id,
        name: parameter.id,
        value: parameter.default,
        step: '1',
        class: 'form-control parameter-options',
    });
    $.each(parameter.options, function(index, option) {
        if (option.name === "min")
            $input.attr("min", option.value);
        else if (option.name === "max")
            $input.attr("max", option.value);
    });
    $label.appendTo($innerDiv);
    $input.appendTo($innerDiv);
    return $innerDiv;
}

/* Crear opción de tipo numérico doble (con decimales) */
function makeDoubleParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-3 form-group " + cssClass + "");
    var $label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    var $input = $("<input />").attr({
        type: 'number',
        id: parameter.id,
        name: parameter.id,
        value: parameter.default,
        step: '0.01',
        class: 'form-control parameter-options'
    });
    $.each(parameter.options, function(index, option) {
        if (option.name === "min")
            $input.attr("min", option.value);
        else if (option.name === "max")
            $input.attr("max", option.value);
    });
    $label.appendTo($innerDiv);
    $input.appendTo($innerDiv);
    return $innerDiv;
}

/* Crear opción de tipo doble */
function makeRadioParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-3 " + cssClass + "");
    var $optionHeaderTitle = $("<p></p>").text(parameter.name);
    $optionHeaderTitle.appendTo($innerDiv);
    $.each(parameter.options, function (index, option) {
        var $radioDiv = $("<div></div>").attr("class", "custom-control custom-radio custom-control-inline");
        var $radioInput = $("<input>").attr({
            type: "radio",
            id: option.value,
            name: parameter.id,
            value: option.value,
            class: "custom-control-input"
        });
        var $radioLabel = $("<label></label>").attr({
            for: option.value,
            class: "custom-control-label"
        })
        if (parameter.default === option.value) {
            $radioInput.prop("checked", true);
        }
        $radioLabel.append(option.name);
        $radioInput.appendTo($radioDiv);
        $radioLabel.appendTo($radioDiv);
        $radioDiv.appendTo($innerDiv);
    });
    return $innerDiv;
}

/* Cambiar texto que etiqueta los inputs de los datasets */
function switchDatasetTags() {
    if ($("input[name='classifierType']:checked").val() === "polarity") {
        $("label[for='psText']").text("Comentarios positivos");
        $("label[for='noText']").text("Comentarios negativos");
        $("label[for='psFile']").text("Comentarios positivos");
        $("label[for='noFile']").text("Comentarios negativos");
    } else {
        $("label[for='psText']").text("Comentarios subjetivos");
        $("label[for='noText']").text("Comentarios objetivos");
        $("label[for='psFile']").text("Comentarios subjetivos");
        $("label[for='noFile']").text("Comentarios objetivos");
    }
}