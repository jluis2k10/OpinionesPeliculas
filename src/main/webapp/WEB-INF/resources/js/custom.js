/* Crear enlaces con las fuentes de comentarios disponibles */
function makeSourcesButton(sources) {
    $.each(sources, function (index, source) {
        $("#sources-dropdown").append("<li><a href='javascript:void(0)' onclick='makeSourcesOptions(this); return false;'" +
            "data-name='"+source.name+"'" +
            " data-adapter='"+source.adapterClass+"'" +
            " data-sinceDate='"+source.sinceDateEnabled+"'" +
            " data-untilDate='"+source.untilDateEnabled+"'" +
            " data-limit='"+source.limitEnabled+"'" +
            " data-imdbID='"+source.imdbIDEnabled+"'" +
            " data-language='"+source.languageEnabled+"'" +
            " data-cleanTweet='"+source.cleanTweet+"'>" + source.name + "</a></li>");
    })
}

/* Crear enlaces con las fuentes de comentarios especiíficas para entrenamiento de analizador
 * Se añaden las opciones de subir datasets o de introducirlos en cajas de texto */
function makeTrainSourcesButton() {
    $("#sources-dropdown").append("<li><a href='javascript:void(0)' onclick='makeSourcesOptions(this); return false;'" +
        "data-name='Subir Datasets'" +
        " data-adapter='FileDataset'" +
        " data-sinceDate='false'" +
        " data-untilDate='false'" +
        " data-limit='false'" +
        " data-imdbID='false'" +
        " data-language='true'" +
        " data-cleanTweet='true'>Subir Datasets</a></li>");
    $("#sources-dropdown").append("<li><a href='javascript:void(0)' onclick='makeSourcesOptions(this); return false;'" +
        " data-name='Introducir Datasets'" +
        " data-adapter='TextDataset'" +
        " data-sinceDate='false'" +
        " data-untilDate='false'" +
        " data-limit='false'" +
        " data-imdbID='false'" +
        " data-language='true'" +
        " data-cleanTweet='true'>Introducir Datasets</a></li>");
}

/* Presentar los elementos del formulario necesarios según la fuente de comentarios */
function makeSourcesOptions(e) {
    $(".source-placeholder").html(e.dataset.name);
    $("#source").val(e.dataset.name);
    $("#sourceClass").val(e.dataset.adapter);
    if (e.dataset.imdbid === "true") {
        $("#term").attr("readonly", "readonly");
        $(".imdbID-container").show();
        $("span.select2-container").width("100%");
    } else {
        $("#term").removeAttr("disabled");
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
    if (e.dataset.cleantweet === "true") {
        $(".cleanTweet-container").show();
    } else {
        $(".cleanTweet-container").hide();
    }

    // Para la página de entrenamiento de modelos tenemos dos botones más
    if (e.dataset.name === "Subir Datasets") {
        $("#term").attr("disabled", "disabled");
        $(".datasets-container").show();
        $(".text-datasets").hide();
        $(".file-datasets").show();
    } else if (e.dataset.name === "Introducir Datasets") {
        $("#tern").attr("disabled", "disabled");
        $(".datasets-container").show();
        $(".file-datasets").hide();
        $(".text-datasets").show();
    } else {
        $(".text-datasets").hide();
        $(".file-datasets").hide();
        $(".datasets-container").hide();
    }
}

/* Rellenar el seleccionable de los adaptadores disponibles para el análisis de subjetividad */
function populateAdapters(adapterType, adapters) {
    if (adapterType === "subjectivity")
        $select = $("#subjectivityAdapter");
    else
        $select = $("#sentimentAdapter");
    $select.empty();                        // Borrar opciones anteriores si las hubiera
    var adaptersPerLanguage = new Array();  // Array con los adaptadores compatibles con el idioma seleccionado
    $.each(adapters, function (index, adapter) {
        var adapterLanguages = adapter.lang.split(",");
        $.each(adapterLanguages, function (index2, adapterLanguage) {
            if (adapterLanguage === $("input[name='lang']:checked").val() &&
                (hasModelForSelectedLanguage(adapter) || !adapter.models_enabled)) {
                adaptersPerLanguage.push(adapter);
                $select.append(new Option(adapter.name, adapter.class));
            }
        })
    });
    populateModels(adapterType, adaptersPerLanguage[0]);     // Rellenar select de los modelos para el primer adaptador
    makeAdapterOptions(adapterType, adaptersPerLanguage[0]); // Construir las opciones para el primer adaptador
}

/* Mostrar/ocultar select con los modelos del analizador de sentimiento */
function populateModels(adapterType, adapter) {
    if (adapterType === "subjectivity") {
        $select = $("#subjectivityModel");
        $container = $(".subjectivityModel-container");
    } else {
        $container = $(".sentimentModel-container");
        $select = $("#sentimentModel");
    }
    $select.empty(); // Primero borramos las opciones anteriores (se suponen de otro adaptador)
    if (typeof adapter !== "undefined" && adapter.models_enabled && hasModelForSelectedLanguage(adapter)) {
        $.each(adapter.models, function (index, model) {
            if (model.lang === $("input[name='lang']:checked").val())
                $select.append(new Option(model.name, model.location));
        });
        $container.show();
    } else {
        $container.hide();
    }
}

function makeAdapterOptions(adapterType, adapter) {
    if (adapterType === "subjectivity") {
        $container = $(".subjectivity-form-container");
        $(".subjectivity-option").remove(); // Borramos todas las opciones anteriores que puedan existir
    } else {
        $container = $(".sentiment-container");
        $(".sentiment-option").remove();    // Borramos todas las opciones anteriores que puedan existir
    }
    if (typeof adapter === "undefined") // Salimos si no hay adaptador disponible
        return;
    if (typeof adapter.parameters != "undefined" && adapter.parameters.length > 0) {
        $.each(adapter.parameters, function (index, parameter) {
            switch (parameter.type) {
                case 'radio':
                    makeRadioOptions($container, parameter, adapterType, adapter.ID);
                    break;
                case 'select':
                    makeSelectOptions($container, parameter, adapterType, adapter.ID);
                    break;
                case 'number':
                    makeNumberOptions($container, parameter, adapterType, adapter.ID);
                    break;
                case 'text':
                    makeTextOptions($container, parameter, adapterType, adapter.ID);
                    break;
                default:
                    break;
            }
        });
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

/* Crear seleccionable para elegir películas y obtener su IMDBID */
function createIMDBSelect(path) {
    $('.imdb-select').select2({
        theme: "bootstrap",
        placeholder: "Título de película",
        language: "es",
        ajax: {
            url: "/api/imdb-lookup",
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
}

/* Crear opciones tipo Radio */
function makeRadioOptions(container, parameter, adapterType, adapterID) {
    var optionDiv = $("<div class='col-xs-3 " + adapterType + "-option'></div>");
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);
    var optionHeaderPar = $("<p></p>");
    var optionHeaderTitle = $("<strong></strong>").text(parameter.name);
    optionHeaderTitle.appendTo(optionHeaderPar);
    optionHeaderPar.appendTo(innerDiv);

    $.each(parameter.options, function (index, option) {
        var optionLabel = $("<label class='radio-inline'></label>");
        var input = $("<input />").attr({
            id: adapterID + "-" + option.value,
            name: adapterID + "-" +parameter.id,
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

/* Crear opciones tipo select */
function makeSelectOptions(container, parameter, adapterType, adapterID) {
    var optionDiv = $("<div class='col-xs-3 " + adapterType + "-option'></div>");

    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var select = $("<select></select>").attr({
        id: adapterID + "-" + parameter.id,
        class: "form-control",
        name: adapterID + "-" + parameter.id
    });
    select.appendTo(innerDiv);

    $.each(parameter.options, function (index, option) {
        var option = $("<option></option>").attr("value", option.value).text(option.name);
        option.appendTo(select);
    });

    optionDiv.appendTo(container);
}

/* Crear input numérico */
function makeNumberOptions(container, parameter, adapterType, adapterID) {
    var optionDiv = $("<div class='col-xs-3 " + adapterType + "-option'></div>");
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var input = $("<input />").attr({
        type: 'number',
        id: adapterID + "-" + parameter.id,
        class: 'form-control',
        name: adapterID + "-" + parameter.id,
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

/* Crear input de texto */
function makeTextOptions(container, parameter, adapterType, adapterID) {
    var optionDiv = $("<div class='col-xs-3 " + adapterType + "-option'></div>");
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var input = $("<input />").attr({
        type: 'text',
        id: adapterID + "-" + parameter.id,
        class: 'form-control',
        name: adapterID + "-" + parameter.id,
        value: parameter.default
    });
    input.appendTo(innerDiv);

    optionDiv.appendTo(container);
}

/* Rellenar seleccionable con los modelos que pueden ser entrenados */
function makeTrainModels(path, userID) {
    var analysisType = $("input[name='analysisType']:checked", "#trainForm").val();
    var getAdaptersURL;
    // Aprovechamos también para cambiar el texto de las etiquetas para los inputs
    if (analysisType === "polarity") {
        getAdaptersURL = path + "/api/sentiment-adapters/" + userID;
        $("label[for='psText']").text("Comentarios positivos");
        $("label[for='noText']").text("Comentarios negativos");
        $("label[for='psFileUpload']").text("Comentarios positivos");
        $("label[for='noFileUpload']").text("Comentarios negativos");
    } else {
        getAdaptersURL = path + "/api/subjectivity-adapters/" + userID;
        $("label[for='psText']").text("Comentarios subjetivos");
        $("label[for='noText']").text("Comentarios objetivos");
        $("label[for='psFileUpload']").text("Comentarios subjetivos");
        $("label[for='noFileUpload']").text("Comentarios objetivos");
    }
    /* Recuperar los adaptadores */
    $.ajax({
        type: "GET",
        contentType: "application/json",
        url: getAdaptersURL,
        timeout: 5000,
        success: function(data) {
            populateTrainModels(data);
            populateAdapterClassInput();
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.error("Request: " + JSON.stringify(XMLHttpRequest) + "\n\nStatus: " + textStatus + "\n\nError: " + errorThrown);
        }
    });
}

/* Rellenar seleccionable de modelos a entrenar */
function populateTrainModels(adapters) {
    console.log(adapters);
    $("#modelLocation").empty() // Reseteamos select;
    var lang = $("input[name='lang']:checked", "#trainForm").val();
    $.each(adapters, function (index, adapter) {
        var adapter_langs = adapter.lang.split(",");
        if ($.inArray(lang, adapter_langs) >= 0 && adapter.models_enabled) {
            var optGroup = $("<optgroup label='" + adapter.name + "'></optgroup>");
            $.each(adapter.models, function(index2, model) {
                console.log(model);
                if (model.lang === lang && model.trainable) {
                    var opt = $("<option value='" + model.location + "' data-adapterclass='" + adapter.class + "'>" + model.name + "</option>");
                    opt.appendTo(optGroup);
                }
            })
            optGroup.appendTo("#modelLocation");
        }
    });
}

/* Rellenar el input oculto que indica la clase del adaptador según el modelo seleccionado */
function populateAdapterClassInput() {
    var selected = $("#modelLocation").find(":selected").data("adapterclass");
    $("#adapterClass").val(selected);
}