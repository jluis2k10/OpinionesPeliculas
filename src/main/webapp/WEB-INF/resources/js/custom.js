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

/* Rellenar el seleccionable de los adaptadores disponibles para el análisis de sentimiento */
function populateSentiment(adapters) {
    $select = $("#sentimentAdapter");
    $select.empty();                       // Borrar opciones anteriores si las hubiera
    var adaptersPerLanguage = new Array(); // Array con los adaptadores compatibles con el idioma seleccionado

    $.each(adapters, function (index, adapter) {
        var adapterLanguages = adapter.lang.split(",");
        $.each(adapterLanguages, function (index2, adapterLanguage) {
            if (adapterLanguage === $("input[name='lang']:checked").val() &&
                (hasModelForSelectedLanguage(adapter) || !adapter.models_enabled)) {
                adaptersPerLanguage.push(adapter);
                $select.append(new Option(adapter.name, adapter.class));
            }
        });
    });
    populateSentimentModels(adaptersPerLanguage[0]); // Rellenar select de los modelos para el primer adaptador
    makeSentimentOptions(adaptersPerLanguage[0]);    // Construir las opciones para el primer adaptador
}

/* Mostrar/ocultar select con los modelos del analizador de sentimiento */
function populateSentimentModels(adapter) {
    $select = $("#sentimentModel");
    $select.empty(); // Primero borramos las opciones anteriores (se suponen de otro adaptador)
    if (typeof adapter !== "undefined" && adapter.models_enabled && hasModelForSelectedLanguage(adapter)) {
        $.each(adapter.models, function (index, model) {
            if (model.lang === $("input[name='lang']:checked").val())
                $select.append(new Option(model.name, model.location));
        });
        $(".sentimentModel-container").show();
    } else {
        $(".sentimentModel-container").hide();
    }
}

/* Devuelve cierto si el adaptador contiene modelos para el idioma seleccionado */
function hasModelForSelectedLanguage(adapter) {
    var result = false;
    $.each(adapter.models, function (index, model) {
        result = result || model.lang === $("input[name='lang']:checked").val();
    });
    return result;
}

function makeSentimentOptions(adapter) {
    $container = $(".sentiment-container");
    $(".sentiment-option").remove();    // Borramos todas las opciones anteriores que puedan existir
    if (typeof adapter === "undefined") // Salimos si no hay adaptador disponible
        return;
    if (adapter.parameters.length > 0) {
        $.each(adapter.parameters, function (index, parameter) {
            switch (parameter.type) {
                case 'radio':
                    makeRadioOptions($container, parameter);
                    break;
                case 'select':
                    makeSelectOptions($container, parameter);
                    break;
                case 'number':
                    makeNumberOptions($container, parameter);
                    break;
                case 'text':
                    makeTextOptions($container, parameter);
                    break;
                default:
                    break;
            }
        });
    }
}

function makeRadioOptions(container, parameter) {
    var optionDiv = $("<div class='col-xs-3 sentiment-option'></div>");
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);
    var optionHeaderPar = $("<p></p>");
    var optionHeaderTitle = $("<strong></strong>").text(parameter.name);
    optionHeaderTitle.appendTo(optionHeaderPar);
    optionHeaderPar.appendTo(innerDiv);

    $.each(parameter.options, function (index, option) {
        var optionLabel = $("<label class='radio-inline'></label>");
        var input = $("<input />").attr({
            id: option.value,
            name: parameter.id,
            value: option.value,
            type: "radio"
        });
        if (index === 0)
            input.attr("checked", "checked");
        input.appendTo(optionLabel);
        optionLabel.append(" " + option.name + "&nbsp;");
        optionLabel.appendTo(innerDiv);
    });
    optionDiv.appendTo(container);
}

function makeSelectOptions(container, parameter) {
    var optionDiv = $("<div class='col-xs-3 sentiment-option'></div>");

    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var select = $("<select></select>").attr({
        id: parameter.id,
        class: "form-control",
        name: parameter.id
    });
    select.appendTo(innerDiv);

    $.each(parameter.options, function (index, option) {
        var option = $("<option></option>").attr("value", option.value).text(option.name);
        option.appendTo(select);
    });

    optionDiv.appendTo(container);
}

function makeNumberOptions(container, parameter) {
    var optionDiv = $("<div class='col-xs-3 sentiment-option'></div>");
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var input = $("<input />").attr({
        type: 'number',
        id: parameter.id,
        class: 'form-control',
        name: parameter.id,
        value: parameter.default
    });
    input.appendTo(innerDiv);

    $.each(parameter.options, function(index, option) {
        if (option.name === "min")
            input.attr("min", option.value);
        else if (option.name === "max")
            input.attr("max", option.value);
    });

    optionDiv.appendTo(container);
}

function makeTextOptions(container, parameter) {
    var optionDiv = $("<div class='col-xs-3 sentiment-option'></div>");
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var input = $("<input />").attr({
        type: 'text',
        id: parameter.id,
        class: 'form-control',
        name: parameter.id,
        value: parameter.default
    });
    input.appendTo(innerDiv);

    optionDiv.appendTo(container);
}