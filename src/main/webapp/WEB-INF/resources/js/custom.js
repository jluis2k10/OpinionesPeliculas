/* Recuperar token csrf para incluirlo como cabecera en cada envío ajax */
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
});

/* Crear enlaces para el botón con las fuentes de comentarios disponibles */
function makeSourcesButton(sources) {
    $.each(sources, function (index, source) {
        $(" #sources-dropdown").append('<a href="#" class="dropdown-item source-button" data-name="' + source.name + '">' + source.name + '</a>');
    });
    var first_link = $("#sources-dropdown a:first-child");
    makeSourceOptions(first_link.get(0), sources);
}

/* Recuperar las fuentes de comentarios disponibles */
function getCommentSources(lang, adapter) {
    return Promise.resolve($.ajax({
        type: "POST",
        data: JSON.stringify({
            lang: (typeof lang !== 'undefined' ? lang : null),
            adapter: (typeof adapter !== 'undefined' ? adapter : null)
        }),
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        url: window.location.protocol + "//" + window.location.host + "/api/comments-source",
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
    $("#updateable").val(source.updateable);
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
        $select = $("select[name='subjectivityModel.id']");
        $container = $(".subjectivityModel-container");
    } else {
        $container = $(".sentimentModel-container");
        $select = $("select[name='sentimentModel.id']");
    }
    $select.empty(); // Primero borramos las opciones anteriores (se suponen de otro adaptador)
    if (typeof adapter !== "undefined" && adapter.models_enabled && hasModelForSelectedLanguage(adapter)) {
        $.each(adapter.models, function (index, model) {
            if (model.lang === $("#lang option:selected").val())
                $select.append(new Option(model.name, model.id));
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

/*
    GRÁFICOS CON chart.js
 */
function renderPie(search, container) {
    var sentimentArray = generateSentimentArray(search.comments);
    if (search.subjectivity === "Sí")
        var extSentimentArray = generateExtSentimentArray(search.comments);

    return new Chart(container, {
        type: 'pie',
        data: {
            labels: ["Positivo", "Negativo", "Neutral"],
            datasets: pieDatasets(sentimentArray, extSentimentArray)
        },
        options: {
            responsive: true,
            legend: {
                display: true,
                onClick: function (e, legendItem) {
                    var index = legendItem.index;
                    var chart = this.chart;
                    var i, ilen, meta;

                    for (i = 0, ilen = (chart.data.datasets || []).length; i < ilen; ++i) {
                        meta = chart.getDatasetMeta(i);
                        if (i == 0) {
                            // Dataset de polaridad (3 elementos)
                            meta.data[index].hidden = !meta.data[index].hidden;
                        }
                        else {
                            // Dataset de subjetividad (6 elementos, 2 por cada uno de polaridad)
                            meta.data[2*index].hidden = !meta.data[2*index].hidden;
                            meta.data[2*index + 1].hidden = !meta.data[2*index + 1].hidden;
                        }
                    }

                    chart.update();
                }
            },
            tooltips: {
                callbacks: {
                    label: function (tooltipItem, data) {
                        var dataset = data.datasets[tooltipItem.datasetIndex];
                        var index = tooltipItem.index;
                        var percentage = calculatePercentage(dataset.data, index);
                        return dataset.labels[index] + ': ' + dataset.data[index] + percentage;
                    }
                }
            }
        }
    });
}

function renderBar(search, container) {
    var sentimentArray = generateSentimentArray(search.comments);
    if (search.subjectivity === "Sí")
        var extSentimentArray = generateExtSentimentArray(search.comments);

    if (typeof extSentimentArray === 'undefined') {
        return new Chart(container, {
            type: 'horizontalBar',
            data: {
                labels: ["Positivo", "Negativo", "Neutral"],
                datasets: [
                    {
                        data: sentimentArray,
                        label: "Polaridad",
                        backgroundColor: ["rgba(40,167,69,0.5)", "rgba(220,53,69,0.5)", "rgba(108,117,125,0.5)"],
                        borderColor: ["#28a745", "#dc3545", "#6c757d"],
                        borderWidth: 1
                    }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    xAxes: [
                        {
                            ticks: {
                                beginAtZero: true
                            }
                        }
                    ]
                }
            }
        });
    }
    else {
        return new Chart(container, {
            type: "horizontalBar",
            data: {
                labels: ["Positivos", "Negativos", "Neutrales"],
                labels: ["Positivos", "Negativos", "Neutrales"],
                datasets: [
                    {
                        data: [extSentimentArray[0], extSentimentArray[2], extSentimentArray[4]],
                        label: "Subjetivos",
                        backgroundColor: ["rgba(40,167,69,0.7)", "rgba(220,53,69,0.7)", "rgba(108,117,125,0.7)"],
                        borderColor: ["#28a745", "#dc3545", "#6c757d"],
                        borderWidth: 1
                    },
                    {
                        data: [extSentimentArray[1], extSentimentArray[3], extSentimentArray[5]],
                        label: "Objetivos",
                        backgroundColor: ["rgba(40,167,69,0.4)", "rgba(220,53,69,0.4)", "rgba(108,117,125,0.4)"],
                        borderColor: ["#28a745", "#dc3545", "#6c757d"],
                        borderWidth: 1
                    }
                ]
            },
            options: {
                responsive: true,
                scales: {
                    xAxes: [{
                        ticks: {
                            beginAtZero: true
                        },
                        stacked: true
                    }],
                    yAxes: [{
                        stacked: true
                    }]
                }
            }
        });
    }
}

function renderTime(search, container) {
    var timeData = timeDataset(search.comments);
    var labels = [];
    for (var i = 0; i < timeData.length; i++) {
        labels[i] = moment(timeData[i].t);
    }
    console.log(labels);

    return new Chart(container, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                type: 'line',
                label: 'Evolución temporal',
                pointRadius: 2,
                fill: false,
                lineTension: 0,
                borderWidth: 2,
                data: timeData
            }]
        },
        options: {
            scales: {
                xAxes: [{
                    type: 'time',
                    distribution: 'series',
                    ticks: {
                        source: 'labels'
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display: true,
                        labelString: 'Closing price ($)'
                    }
                }]
            }
        }

    });
}

function timeDataset(comments) {
    var dataset = [];
    $.each(comments, function (index, comment) {
        var date = moment(comment.date, "DD/MM/YYYY");
        var value = 0;
        if (comment.sentiment === "Positive")
            value++;
        else if (comment.sentiment === "Negative")
            value--;
        if (index === 0) {
            dataset.push({
                t: date.valueOf(),
                y: value
            });
        }
        else {
            if (date.valueOf() === dataset[dataset.length - 1].t) {
                // Estamos en el mismo día que el comentario anterior
                dataset[dataset.length - 1].y += value;
            }
            else {
                // Estamos en un día posterior
                dataset.push({
                    t: date.valueOf(),
                    y: value
                });
            }
        }
    });
    console.log(dataset);
    return dataset;
}

function pieDatasets(sentimentArray, extSentimentArray) {
    var dataset = [];
    dataset[0] = {
        data: sentimentArray,
        labels: ["Positivo", "Negativo", "Neutral"],
        backgroundColor: ["#28a745", "#dc3545", "#6c757d"]
    };
    if (typeof extSentimentArray != 'undefined') {
        dataset[1] = {
            data: extSentimentArray,
            labels: ["Subjetivo", "Objetivo", "Subjetivo", "Objetivo", "Subjetivo", "Objetivo"],
            backgroundColor: ["#28a745", "#28a745", "#dc3545", "#dc3545", "#6c757d", "#6c757d"]
        }
    }
    return dataset;
}

function generateSentimentArray(comments) {
    var sentimentArray = [0, 0, 0];
    var POS = 0;
    var NEG = 1;
    var NEU = 2;

    $.each(comments, function (index, comment) {
        if (comment.sentiment === "Positive")
            sentimentArray[POS]++;
        else if (comment.sentiment === "Negative")
            sentimentArray[NEG]++;
        else
            sentimentArray[NEU]++;
    });

    return sentimentArray;
}

function generateExtSentimentArray(comments) {
    var extSentimentArray = [0 ,0, 0, 0, 0, 0];
    var POS_SUB = 0;
    var POS_OBJ = 1;
    var NEG_SUB = 2;
    var NEG_OBJ = 3;
    var NEU_SUB = 4;
    var NEU_OBJ = 5;

    $.each(comments, function (index, comment) {
        if (comment.sentiment === "Positive" && comment.subjectivity === "Subjective")
            extSentimentArray[POS_SUB]++;
        else if (comment.sentiment === "Positive" && comment.subjectivity === "Objective")
            extSentimentArray[POS_OBJ]++;
        else if (comment.sentiment === "Negative" && comment.subjectivity === "Subjective")
            extSentimentArray[NEG_SUB]++;
        else if (comment.sentiment === "Negative" && comment.subjectivity === "Objective")
            extSentimentArray[NEG_OBJ]++;
        else if (comment.sentiment === "Neutral" && comment.subjectivity === "Subjective")
            extSentimentArray[NEU_SUB]++;
        else
            extSentimentArray[NEU_OBJ]++;
    });

    return extSentimentArray;
}

function calculatePercentage(array, index) {
    var total = 0;
    if (array.length == 3) { // Positivos, megativos, neutrales
        total = array.reduce(function (prev, curr) {
            return prev + curr;
        }, 0);
    } else { // Positivos subjetivos, positivos objetivos, negativos subjetivos...
        total = array[index] + array[index + 1 - 2 * (index % 2)];
    }
    var perc = (array[index]/total) * 100;
    return " (" + (Math.round(perc * 10) / 10) + "%)";
}