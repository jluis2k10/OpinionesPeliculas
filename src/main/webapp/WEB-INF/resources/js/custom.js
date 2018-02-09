/* Crear enlaces para el botón con las fuentes de comentarios disponibles */
function makeSourcesButton(sources) {
    $.each(sources, function (index, source) {
        $(" #sources-dropdown").append('<a href="#" class="dropdown-item source-button" data-name="' + source.name + '">' + source.name + '</a>');
    });
    var first_link = $("#sources-dropdown a:first-child");
    makeSourceOptions(first_link.get(0), sources);
}

/* Recuperar las fuentes de comentarios disponibles */
function getCommentSources(lang) {
    var url = "/api/comments-source";
    if (lang != undefined)
        url += "?lang=" + lang;
    return Promise.resolve($.ajax({
        type: "GET",
        contentType: "application/json",
        url: url,
        timeout: 5000
    }));
}

/* Recuperar los adaptadores para el análisis de sentimiento */
function getSentimentAdapters() {
    return Promise.resolve($.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/api/sentiment-adapters",
        timeout: 5000
    }));
}

/* Recuperar los adaptadores para el análisis de subjetividad */
function getSubjectivityAdapters(){
    return Promise.resolve($.ajax({
        type: "GET",
        contentType: "application/json",
        url: "/api/subjectivity-adapters",
        timeout: 5000
    }));
}

/* Añadir de forma manual dos fuentes de comentarios: subir archivos y escribir comentarios */
function genExtraSources(sources) {
    var uploadFile = {
        name:               "Archivo de Datasets",
        adapterClass:       "FileDataset",
        sinceDateEnabled:   false,
        untilDateEnabled:   false,
        limitEnabled:       false,
        imdbIDEnabled:      false,
        language: {
            Español:        "es",
            Inglés:         "en"
        },
        cleanTweet:         false
    };
    var textDataset = {
        name:               "Escribir Datasets",
        adapterClass:       "TextDataset",
        sinceDateEnabled:   false,
        untilDateEnabled:   false,
        limitEnabled:       false,
        imdbIDEnabled:      false,
        language: {
            Español:        "es",
            Inglés:         "en"
        },
        cleanTweet:         false
    };
    sources.push(uploadFile, textDataset);
    return sources;
}

/* Averiguar qué fuente de comentarios es la seleccionada y dar la orden de construir
 * las opciones asociadas a dicha fuente */
function makeSourceOptions(e, sources) {
    $(".source-option").remove(); // Eliminar opciones extra previas si existen
    $(".sources-dropdown").html(e.dataset.name);
    $("#source").val(e.dataset.name);
    $.each(sources, function (i, source) {
        if (source.name === e.dataset.name) {
            constructSourceOptions(source);
        }
    });
}

/* Construir opciones disponibles para una fuente de comentarios */
function constructSourceOptions(source) {
    $("#sourceClass").val(source.adapterClass);
    if (source.imdbIDEnabled) {
        $("#term").attr("readonly", "readonly");
        $(".imdbID-container").show();
        $("span.select2-container").width("100%");
    } else {
        $("#term").removeAttr("readonly");
        $(".imdbID-container").hide();
    }
    if (source.limitEnabled) {
        $(".limit-container").show();
    } else {
        $(".limit-container").hide();
    }
    if (source.sinceDateEnabled){
        $(".sinceDate-container").show();
    } else {
        $(".sinceDate-container").hide();
    }
    if (source.untilDateEnabled){
        $(".untilDate-container").show();
    } else {
        $(".untilDate-container").hide();
    }
    if (source.cleanTweet) {
        $(".cleanTweet-container").show();
    } else {
        $(".cleanTweet-container").hide();
    }
    if (source.chooseLanguage) {
        $(".language-container").show();
    } else {
        $(".language-container").hide();
    }
    // Construir select con los idiomas disponibles
    $current_lang = $("#lang option:selected").val();
    $("#lang").find('option').remove();
    $.each(source.languages, function (key, val) {
        $("#lang").append($("<option></option>")
            .attr("value", val)
            .text(key));
        if (val === $current_lang)
            $("#lang").val(val);
    });

    // Construir parámetros extra
    if (source.extra_parameters.length > 0) {
        $container = $(".sources-container");
        $.each(source.extra_parameters, function (index, parameter) {
            switch (parameter.type.toLowerCase()) {
                case 'radio':
                    makeRadioOptions($container, parameter, "source", "");
                    break;
                case 'select':
                    makeSelectOptions($container, parameter, "source", "");
                    break;
                case 'number':
                    makeNumberOptions($container, parameter, "source", "");
                    break;
                case 'text':
                    makeTextOptions($container, parameter, "source", "");
                    break;
                default:
                    break;
            }
        });
    }

    // Para la página de entrenamiento de modelos tenemos dos botones más
    if (source.adapterClass === "FileDataset") {
        $("#term").attr("readonly", "readonly");
        $(".datasets-container").show();
        $(".text-datasets").hide();
        $(".file-datasets").show();
    } else if (source.adapterClass === "TextDataset") {
        $("#term").attr("readonly", "readonly");
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
            if (adapterLanguage === $("#lang option:selected").val() &&
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
            if (model.lang === $("#lang option:selected").val())
                $select.append(new Option(model.name, model.location));
        });
        $container.show();
    } else {
        $container.hide();
    }
}

/* Dar la orden de construir cada una de las opciones disponibles para un adaptador */
function makeAdapterOptions(adapterType, adapter) {
    if (adapterType === "subjectivity") {
        $container = $(".subjectivity-container");
        $(".subjectivity-option").remove(); // Borramos todas las opciones anteriores que puedan existir
    } else {
        $container = $(".sentiment-container");
        $(".sentiment-option").remove();    // Borramos todas las opciones anteriores que puedan existir
    }
    if (typeof adapter === "undefined") // Salimos si no hay adaptador disponible
        return;
    if (typeof adapter.parameters != "undefined" && adapter.parameters.length > 0) {
        $.each(adapter.parameters, function (index, parameter) {
            switch (parameter.type.toLowerCase()) {
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
        result = result || model.lang === $("#lang option:selected").val();
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

/* Crear div para el contenedor de opciones */
function makeOptionDiv(adapterType) {
    if (adapterType == "subjectivity")
        return $("<div class='col-3 subjectivity-option subjectivity-item'></div>");
    else if (adapterType == "sentiment")
        return $("<div class='col-3 sentiment-option'></div>");
    else
        return $("<div class='col-3 source-option'></div>");
}

/* Crear opciones tipo Radio */
function makeRadioOptions(container, parameter, adapterType, adapterID) {
    var optionDiv = makeOptionDiv(adapterType);
    var optionHeader = $("<p></p>").text(parameter.name);
    optionHeader.appendTo(optionDiv);

    $.each(parameter.options, function (index, option) {
        var innerDiv = $("<div></div>").addClass("custom-control custom-radio custom-control-inline");
        var input = $("<input />").attr({
            id: parameter.id + option.value,
            name: adapterID + parameter.id,
            value: option.value,
            type: "radio",
            class: "custom-control-input"
        });
        var label = $("<label></label>").attr({
            for: parameter.id + option.value,
            class: "custom-control-label"
        }).text(option.name);
        if (parameter.default === option.value)
            input.attr("checked", "checked");
        input.appendTo(innerDiv);
        label.appendTo(innerDiv);
        innerDiv.appendTo(optionDiv);
    });

    optionDiv.appendTo(container);
}

/* Crear opciones tipo select */
function makeSelectOptions(container, parameter, adapterType, adapterID) {
    var optionDiv = makeOptionDiv(adapterType);
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", adapterID + parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var select = $("<select></select>").attr({
        id: adapterID + parameter.id,
        class: "form-control",
        name: adapterID + parameter.id
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
    var optionDiv = makeOptionDiv(adapterType);
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", adapterID + parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var input = $("<input />").attr({
        type: 'number',
        id: adapterID + parameter.id,
        class: 'form-control',
        name: adapterID + parameter.id,
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
    var optionDiv = makeOptionDiv(adapterType);
    var innerDiv = $("<div class='form-group'></div>");
    innerDiv.appendTo(optionDiv);

    var label = $("<label></label>").attr("for", adapterID + parameter.id).text(parameter.name);
    label.appendTo(innerDiv);

    var input = $("<input />").attr({
        type: 'text',
        id: adapterID + parameter.id,
        class: 'form-control',
        name: adapterID + parameter.id,
        value: parameter.default
    });
    input.appendTo(innerDiv);
    optionDiv.appendTo(container);
}

function alertMsg(alertType, msg) {
    $alertDiv = $("<div>");
    $alertDiv.addClass("alert alert-dismissible fade show");
    $alertDiv.addClass("alert-" + alertType);
    $alertDiv.attr("role", "alert");

    $closeButton = $("<button>");
    $closeButton.addClass("close");
    $closeButton.attr({
        "aria-label": "Cerrar",
        "data-dismiss": "alert"
    });
    $closeButton.append("<span aria-hidden='true'>&times;</span>");

    $alertDiv.append($closeButton);
    $alertDiv.append(msg);

    $(".main-content").prepend($alertDiv);
}

/* Localización datatables */
function datatablesLocalization() {
    var loc = {
        "sProcessing":     "Procesando...",
        "sLengthMenu":     "Mostrar _MENU_ entradas",
        "sZeroRecords":    "No se encontraron resultados",
        "sEmptyTable":     "Ningún dato disponible en esta tabla",
        "sInfo":           "Mostrando entradas _START_ a _END_ de un total de _TOTAL_ entradas",
        "sInfoEmpty":      "Mostrando 0 entradas",
        "sInfoFiltered":   "(filtrado de un total de _MAX_ entradas)",
        "sInfoPostFix":    "",
        "sSearch":         "Buscar:",
        "sUrl":            "",
        "sInfoThousands":  ",",
        "sLoadingRecords": "Cargando...",
        "oPaginate": {
            "sFirst":    "Primero",
            "sLast":     "Último",
            "sNext":     "Siguiente",
            "sPrevious": "Anterior"
        },
        "oAria": {
            "sSortAscending":  ": Activar para ordenar la columna de manera ascendente",
            "sSortDescending": ": Activar para ordenar la columna de manera descendente"
        }
    };
    return loc;
}