function populateAdapters(path) {
    // Recuperar adaptadores disponibles
    var url = path + "/api/subjectivity-adapters";
    if ($("input[name='classifierType']:checked").val() === "polarity")
        url = path + "/api/sentiment-adapters";
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        timeout: 5000,
        success: function(data) {
            adapters = data;
            var $adapterSelect = $("#adapterSelect");
            $adapterSelect.empty();
            $.each(adapters, function(index, adapter) {
                if (adapter.model_creation) {
                    $adapterSelect.append(new Option(adapter.name, adapter.class));
                }
            });
            // Construir el formulario para los parámetros disponibles para la creación del modelo
            populateModelCreationParameters(adapters);
            return adapters;
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.error("Request: " + JSON.stringify(XMLHttpRequest) + "\n\nStatus: " + textStatus + "\n\nError: " + errorThrown);
            return null;
        }
    });
}

function populateModelCreationParameters(adapters) {
    $(".parameters-container").empty();
    var adapterClass = $("#adapterSelect").val();
    $("#adapterClass").val(adapterClass); // Aprovechamos para cambiar el valor en el input oculto del nuevo modelo
    var selectedAdapter = $.grep(adapters, function (adapter, index) {
        return (adapter.class === adapterClass);
    })[0];

    // Cambiar también los idiomas posibles para el modelo en función de los aceptados por el adaptador
    var avaliableLanguages = selectedAdapter.lang.split(",");
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
    $.each(selectedAdapter.model_creation_parameters, function (index, parameter) {
        var $fieldset = $("<fieldset class='col-xs-12'></fieldset>");
        var $legend = $("<legend>" + parameter.name + "</legend>");
        $legend.appendTo($fieldset);
        var $parameterHTML = makeParameter(parameter, "");
        $parameterHTML.appendTo($fieldset);
        $fieldset.appendTo($(".parameters-container"));
        var $select = $fieldset.find('select, input:checked');
        attachOptionParameters($select, adapters);
    })
}

function attachOptionParameters($select, adapters) {
    var $fieldset = $select.closest('fieldset');
    $fieldset.find("div.option-parameter").remove();

    var adapterClass = $("#adapterSelect").val();
    var selectedAdapter = $.grep(adapters, function (adapter, index) {
        return (adapter.class === adapterClass);
    })[0];
    var adapterParameter = $.grep(selectedAdapter.model_creation_parameters, function(parameter, index) {
        return (parameter.id === $select.attr("id") || parameter.id === $select.attr("name"));
    })[0];
    var selectedOption = $.grep(adapterParameter.options, function(option, index) {
        return (option.value === $select.val());
    })[0];

    if (selectedOption.parameters) {
        $.each(selectedOption.parameters, function(index, parameter) {
            var $parameterHTML = makeParameter(parameter, "option-parameter");
            $parameterHTML.appendTo($fieldset);
        });
    }
}

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

function makeSelectParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-xs-3 form-group " + cssClass + "");
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

function makeTextParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-xs-3 form-group " + cssClass + "");
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

function makeNumberParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-xs-3 form-group " + cssClass + "");
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

function makeDoubleParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-xs-3 form-group " + cssClass + "");
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

function makeRadioParameter(parameter, cssClass) {
    var $innerDiv = $("<div></div>").attr("class", "col-xs-3 form-group " + cssClass + "");
    var $optionHeaderPar = $("<p></p>");
    var $optionHeaderTitle = $("<strong></strong>").text(parameter.name);
    $optionHeaderTitle.appendTo($optionHeaderPar);
    $optionHeaderPar.appendTo($innerDiv);
    $.each(parameter.options, function (index, option) {
        var $radioLabel = $("<label class='radio-inline'></label>");
        var $radio = $("<input />").attr({
            id: option.value,
            name: parameter.id,
            value: option.value,
            type: "radio"
        });
        if (parameter.default === option.value) {
            $radio.prop("checked", true);
        }
        $radio.appendTo($radioLabel);
        $radioLabel.append(" " + option.name + "&nbsp;");
        $radioLabel.appendTo($innerDiv);
    });
    return $innerDiv;
}

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