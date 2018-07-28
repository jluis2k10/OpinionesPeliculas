/* Contexto */
var ctx = $("meta[name='_context']").attr("content");

/* Recuperar token csrf para incluirlo como cabecera en cada envío ajax */
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function (e, xhr, options) {
    xhr.setRequestHeader(header, token);
});

/* Definimos colores para gráficas */
window.chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)'
};

/**
 * Muestra el cover/modal de "Cargando" con el mensaje indicado.
 * @param msg El mensaje a mostrar
 */
function showLoading(msg) {
    $('.loader-content').append('<p class="loader">' + msg  + '</p>');
    $('p.loader').append('<span class="loader__dot">.</span><span class="loader__dot">.</span><span class="loader__dot">.</span></div>');
    $('.cover').show();
}

/**
 * Oculta el cover/modal de "Cargando"
 */
function hideLoading() {
    $('.cover').hide();
    $('p.loader').remove();
}

function showFlashMessage(msgType, message) {
    $('<div>', {
        class: "alert alert-" + msgType + " alert-dismissible fade show",
        role: "alert",
        html: message
    }).append($('<button>', {
        type: "button",
        class: "close",
        "data-dismiss": "alert",
        "aria-label": "cerrar",
        html: '<span aria-hidden="true">&times;</span>'
    })).prependTo($('.main-content'));
}

/**
 * Recuperar fuentes de comentarios para Corpus
 * @param lang Idioma de los comentarios
 * @returns {Promise}
 */
function getCorporaSources(lang) {
    return Promise.resolve($.ajax({
        type: "POST",
        data: JSON.stringify({
            lang: (typeof lang !== 'undefined' ? lang : null)
        }),
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        url: ctx + "/api/corpora-sources",
        timeout: 5000
    }));
}

/**
 * Añadir de forma manual dos fuentes de comentarios extra necesarias para entrenar
 * un modelo de lenguaje: escribir comentarios.
 */

function generateExtraSources(sources) {
    var textDataset = {
        name:               "Escribir Datasets",
        searchTermEnabled:  false,
        adapterClass:       "TextDataset",
        limit:              false,
        sinceDate:          false,
        untilDate:          false,
        chooseLanguage:     false,
        languages: {
            Español:        "es",
            Inglés:         "en"
        },
        imdbIDEnabled:      false,
        fileUpload:         false,
        textDataset:        true,
        options:            []
    };
    sources.push(textDataset);
}

function getOpinionClassifiers(lang, creation) {
    return Promise.resolve($.ajax({
        type: "POST",
        data: JSON.stringify({
            lang: lang,
            creation: creation
        }),
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        url: ctx + "/api/classifiers/opinion",
        timeout: 5000
    }));
}

function getPolarityClassifiers(lang, creation) {
    return Promise.resolve($.ajax({
        type: "POST",
        data: JSON.stringify({
            lang: lang,
            creation: creation
        }),
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        url: ctx + "/api/classifiers/polarity",
        timeout: 5000
    }));
}

function getUserCorpora(withComments, withAnalyses, withRecords) {
    return Promise.resolve($.ajax({
        type: "POST",
        data: JSON.stringify({
            withComments: withComments,
            withAnalyses: withAnalyses,
            withRecords: withRecords
        }),
        dataType: "json",
        contentType: "application/json; charset=UTF-8",
        url: ctx + "/api/user-corpora",
        timeout: 5000
    }))
}


/**
 * Construir el botón con las diferentes fuentes de comentarios para un Corpus.
 * @param sources Fuentes de comentarios para un Corpus
 */
function renderSourcesButton(sources, corpusLang) {
    $.each(sources, function (index, source) {
        $(" #sources-dropdown").append('<a href="#" class="dropdown-item source-button" data-name="' + source.name + '">' + source.name + '</a>');
    });
    var selectedSource = $.grep(sources, function (source) {
        return source.name === $("#sources-dropdown a:first-child").get(0).dataset.name;
    })[0];
    renderSourceOptions(selectedSource, corpusLang);
}

/**
 * Construir el formulario con las opciones disponibles para la fuente de comentarios
 * seleccionada.
 * @param e Elemento seleccionado.
 * @param sources Fuentes de comentarios para un Corpus
 */
function renderSourceOptions(source, corpusLang) {
    $(".option-container").remove();                // Eliminar opciones extra previas si existen
    $(".sources-dropdown").html(source.name);       // Cambiamos el texto del dropdown
    $("#source").val(source.name);                  // Añadimos el nombre de la fuente al campo oculto del formulario
    $("#sourceAdapter").val(source.adapterClass);   // Añadimos la clase del adaptador seleccionado

    // Primero activamos o desactivamos el input para el término de búsdqueda en función del
    // tipo de búsqueda que en el que estemos
    if (source.searchTermEnabled) {
        $("#term").removeAttr("readonly");
    } else {
        $("#term").attr("readonly", "readonly");
    }

    if (source.imdbIDEnabled) {
        $(".imdbID-container").show();
        $("span.select2-container").width("100%");
    } else {
        $(".imdbID-container").hide();
    }
    if (source.fileUpload) {
        $(".file-container").show();
        $("#term").val("");
    } else {
        $(".file-container").hide();
    }
    if (source.textDataset) {
        $(".text-dataset-container").show();
        $("#term").val("");
    } else {
        $(".text-dataset-container").hide();
    }
    if (source.limit) {
        $(".limit-container").show();
    } else {
        $(".limit-container").hide();
    }
    if (source.sinceDate){
        $(".sinceDate-container").show();
    } else {
        $(".sinceDate-container").hide();
    }
    if (source.untilDate){
        $(".untilDate-container").show();
    } else {
        $(".untilDate-container").hide();
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
        if (corpusLang != null && corpusLang === val)
            $("#lang").append($("<option></option>")
                .attr("value", val)
                .text(key));
        else if (corpusLang == null)
            $("#lang").append($("<option></option>")
                .attr("value", val)
                .text(key));
        if (val === $current_lang)
            $("#lang").val(val);
    });
    // Construir parámetros extra
    if (source.options.length > 0) {
        $container = $(".sources-container");
        $.each(source.options, function (index, option) {
            switch (option.type.toLowerCase()) {
                case 'radio':
                    renderRadio($container, option);
                    break;
                case 'select':
                    renderSelect($container, option);
                    break;
                case 'number':
                    renderNumber($container, option);
                    break;
                case 'text':
                    renderText($container, option);
                    break;
                default:
                    break;
            }
        });
    };
}

function renderClassifierForm(classifiers, index) {
    var listItem = $('<div>', {
        class: "list-group-item flex-column align-items-start classifier-item",
        data: {id: index}
    }).append(
        $('<div>', {
            class: "d-flex justify-content-between"
        }).append(
            $('<h5>', {
                class: "mb-1",
                html: "Análisis"
            })
        ),
        $('<div>', {
            class: "row classifier-container"
        }),
        $('<button>', {
            type: "button",
            class: "btn remove-classifier btn-sm btn-danger mr-2",
            html: "<i class='fas fa-times mr-1'></i> Eliminar análisis"
        }),
        $('<button>', {
            type: "button",
            class: "btn add-classifier btn-sm btn-success",
            html: "<i class='fas fa-plus mr-1'></i> Añadir análisis"
        })
    );
    var innerDiv = $('<div>', {class: 'form-group col-3'});
    var label = $('<label></label>')
        .attr('for', "analysis'" + index + "'.adapterClass")
        .html('Clasificador');
    var select = $('<select></select>')
        .addClass('form-control')
        .attr({
            id: "analysis'" + index + "'.adapterClass",
            name: "analysis["+ index + "].adapterClass"
        });
    $.each(classifiers, function (i, classifier) {
        select.append(new Option(classifier.name, classifier.class));
    });
    label.appendTo(innerDiv);
    select.appendTo(innerDiv);
    innerDiv.appendTo($(listItem).find('.classifier-container'));
    listItem.appendTo($('.classifiers'));

    var selectedAdapter = $('select[name="analysis['+ index + '].adapterClass"] option:selected');
    addClassifierSelectListener($('select[name="analysis['+ index + '].adapterClass"]'), classifiers);
    renderClassifierOptions(selectedAdapter, classifiers);
}

function renderClassifierOptions(selectedAdpter, classifiers) {
    var container = selectedAdpter.closest('.classifier-container');
    var index = $(container).closest('.classifier-item').data("id");
    var classifier = $.grep(classifiers, function (c) {
        return c.class === selectedAdpter.val();
    })[0];

    // El clasificador tiene modelos de lenguaje, mostrar select con ellos
    if (classifier.models_enabled && classifier.models.length > 0) {
        var innerDiv = $('<div>', {class: 'form-group col-3 classifier-option'});
        var label = $('<label></label>')
            .attr('for', "analysis'" + index + "'.languageModel")
            .html('Modelo de Lenguaje');
        var select = $('<select></select>')
            .addClass('form-control')
            .attr({
                id: "analysis'" + index + "'.languageModel",
                name: "analysis["+ index + "].languageModel"
            });
        $.each(classifier.models, function (i, model) {
            select.append(new Option(model.name, model.id));
        });
        var hiddenModelName = $('<input>', {
            type: 'hidden',
            class: 'classifier-option',
            value: '',
            id: "analysis'" + index + "'.languageModel",
            name: "analysis["+ index + "].languageModel"
        });
        label.appendTo(innerDiv);
        select.appendTo(innerDiv);
        hiddenModelName.appendTo(innerDiv);
        innerDiv.appendTo(container);

        // Rellenar el hidden select con el primer valor y añadir listener para
        // capturar cambios
        $(hiddenModelName).val($(select).find(':selected').text());
        $(select).change(function () {
            $(hiddenModelName).val($(this).find(':selected').text());
        });
    }

    // Mostrar radio para elegir eliminar/no eliminar Stop Words
    var innerDiv = $('<div>', {class: 'form-group col-3 classifier-option'});
    $('<p>Eliminar <em>Stop-words</em></p>').appendTo(innerDiv);
    var yesOption = $('<div>', {
        class: 'custom-control custom-radio custom-control-inline'
    });
    yesOption.append(
        $('<input>', {
            type: 'radio',
            class: 'custom-control-input',
            value: 'true',
            id: "analysis'" + index + "'Sí.deleteStopWords",
            name: "analysis['" + index + "'].deleteStopWords"
        }),
        $('<label>', {
            class: 'custom-control-label',
            for: "analysis'" + index + "'Sí.deleteStopWords",
            html: 'Sí'
        })
    );
    var noOption = $('<div>', {
        class: 'custom-control custom-radio custom-control-inline'
    });
    noOption.append(
        $('<input>', {
            type: 'radio',
            class: 'custom-control-input',
            value: 'false',
            id: "analysis'" + index + "'No.deleteStopWords",
            name: "analysis['" + index + "'].deleteStopWords",
            checked: 'checked'
        }),
        $('<label>', {
            class: 'custom-control-label',
            for: "analysis'" + index + "'No.deleteStopWords",
            html: 'No'
        })
    );
    yesOption.appendTo(innerDiv);
    noOption.appendTo(innerDiv);
    innerDiv.appendTo(container);

    // Mostrar radio para elegir si analizar sólo opiniones o no (en caso de
    // que el clasificador sea de polaridad
    if (classifier.type === "polarity") {
        var opinionsInnerDiv = $('<div>', {class: 'form-group col-3 classifier-option'});
        $('<p>Analizar <strong>sólo</strong> opiniones</p>').appendTo(opinionsInnerDiv);
        var opinionsOnlyYes = $('<div>', {
            class: 'custom-control custom-radio custom-control-inline'
        });
        opinionsOnlyYes.append(
            $('<input>', {
                type: 'radio',
                class: 'custom-control-input',
                value: 'true',
                id: "analysis'" + index + "'Sí.ignoreObjectives",
                name: "analysis['" + index + "'].ignoreObjectives"
            }),
            $('<label>', {
                class: 'custom-control-label',
                for: "analysis'" + index + "'Sí.ignoreObjectives",
                html: 'Sí'
            })
        );
        var opinionsOnlyNo = $('<div>', {
            class: 'custom-control custom-radio custom-control-inline'
        });
        opinionsOnlyNo.append(
            $('<input>', {
                type: 'radio',
                class: 'custom-control-input',
                value: 'false',
                id: "analysis'" + index + "'No.ignoreObjectives",
                name: "analysis['" + index + "'].ignoreObjectives",
                checked: 'checked'
            }),
            $('<label>', {
                class: 'custom-control-label',
                for: "analysis'" + index + "'No.ignoreObjectives",
                html: 'No'
            })
        );
        opinionsOnlyYes.appendTo(opinionsInnerDiv);
        opinionsOnlyNo.appendTo(opinionsInnerDiv);
        opinionsInnerDiv.appendTo(container);
    }

    // Mostrar parámetros extra
    $.each(classifier.parameters, function (i, parameter) {
        switch(parameter.type.toLowerCase()) {
            case 'radio':
                renderClassifierRadio(container, parameter);
                break;
            case 'select':
                renderClassifierSelect(container, parameter);
                break;
            case 'number':
                renderClassifierNumber(container, parameter);
                break;
            case 'text':
                renderClassifierText(container, parameter);
                break;
            default:
                break;
        }
    });

    // Añadir hidden input con el nombre del clasificador
    $('<input>', {
        class: "classifier-option",
        type: "hidden",
        name: "analysis["+ index +"].classifierName",
        value: classifier.name
    }).appendTo(container);

    // Añadir hidden input con el tipo de clasificador
    $('<input>', {
        class: "classifier-option",
        type: "hidden",
        name: "analysis["+ index +"].classifierType",
        value: classifier.type
    }).appendTo(container);

    // Añadir hidden input con el idioma del corpus/clasificador
    $('<input>', {
        class: "classifier-option",
        type: "hidden",
        name: "analysis["+ index +"].language",
        value: classifier.lang
    }).appendTo(container);
}

function addClassifierSelectListener(select, classifiers) {
    $(select).change(function() {
        var selected = $(this).find("option:selected");
        $(selected.closest('.classifier-container')).children('.classifier-option').remove();
        renderClassifierOptions(selected, classifiers);
    })
}

// Paginación de resultados
function myPagination(size, corpus, container, showDetails) {
    $("#comments-pagination").pagination({
        dataSource: corpus,
        locator: 'comments',
        pageSize: size,
        callback: function (comments, pagination) {
            formatComments(comments, container, pagination, showDetails);
            generateReadMore();
        },
        ulClassName: "pagination justify-content-end"
    });
}

function formatComments(comments, $container, pagination, showDetails) {
    $container.empty();
    comments.forEach(function (comment, i) {
        var commentIndex = (pagination.pageNumber - 1) * pagination.pageSize + i + 1;
        var $listItem = $('<li></li>').addClass('list-group-item flex-column align-items-start');

        // Cabecera del item (fuente y fecha)
        var $headerDiv = $('<div>', {
            class: "d-flex justify-content-between"
        }).appendTo($listItem);
        var $sourceContent = $('<small>', {
            class: "mb-2 text-muted",
            html: "<strong>#" + commentIndex + "</strong> " + comment.source
        }).appendTo($headerDiv);
        if (comment.url != null) {
            $('<a>', {
                href: comment.url,
                class: "ml-3",
                target: "_blank",
                html: comment.url + '<i class="fas fa-external-link-alt ml-1"></i>'
            }).appendTo($sourceContent);
        }
        $('<small>', {
            class: "text-muted",
            html: comment.date
        }).appendTo($headerDiv);

        // Cuerpo del item (comentario)
        $('<p>', {
            class: "card-text mb-2 readmore",
            html: comment.content.replace(/(\r\n|\n|\r)/g, "<br />")
        }).appendTo($listItem);

        // Pie del item (medias de los análisis realizados sobre el comentario)
        var sentiment = "N/A";
        var cssClass;
        var sentimentIcon = '<i class="far fa-question-circle"></i> ';
        var sentimentScore = '';

        // Sentimiento promedio del análisis
        if (comment.polarity === "Positive") {
            sentiment = "Positivo "
            cssClass = "text-success";
            sentimentIcon = '<i class="far fa-thumbs-up"></i> ';
            sentimentScore = '(' + (parseFloat(comment.polarityScore) * 100).toFixed(2) + '%)';
        }
        else if (comment.polarity === "Negative") {
            sentiment = "Negativo ";
            cssClass = "text-danger";
            sentimentIcon = '<i class="far fa-thumbs-down"></i> ';
            sentimentScore = '(' + (parseFloat(comment.polarityScore) * 100).toFixed(2) + '%)';
        } else if (comment.polarity === "Neutral") {
            sentiment = "Neutral ";
            cssClass = "";
            sentimentIcon = '<i class="far fa-meh"></i> ';
            sentimentScore = '(' + (parseFloat(comment.polarityScore) * 100).toFixed(2) + '%)';
        }
        $('<small>', {
            class: cssClass,
            html: '<strong>' + sentimentIcon + sentiment + '</strong>' + sentimentScore
        }).appendTo($listItem);

        // Opinión media del análisis
        if (comment.opinion != null && comment.opinion === "Subjective") {
            $('<small>', {
                html: ' &ndash; <strong>Subjetivo</strong> ' + '(' + (parseFloat(comment.opinionScore) * 100).toFixed(2) + '%)'
            }).appendTo($listItem);
        }
        else if (comment.opinion != null && comment.opinion === "Objective") {
            $('<small>', {
                html: ' &ndash; <strong>Objetivo</strong> ' + '(' + ((1 - parseFloat(comment.opinionScore)) * 100).toFixed(2) + '%)'
            }).appendTo($listItem);
        }

        // Añadir información con los detalles de cada análisis ejecutado
        if (showDetails) {
            var details =  $('<a>', {
                href: "#",
                class: "ml-2 analysis-popover",
                data: {toggle: "popover"},
                title: "Detalles del Análisis",
                html: '<i class="fas fa-info-circle"></i>'
            });
            $(details)
                .popover({
                    content: function () {
                        var res = "";
                        if (comment.sentimentRecords != null && comment.sentimentRecords.length > 0) {
                            res += '<p><strong>Sentimiento:</strong><br/>';
                            comment.sentimentRecords.forEach(function(record, j) {
                                res += record.classifier + ": " + record.record.polarity + " (";
                                res += Math.round10(record.record.score * 100, -2) + "%)<br/>";
                            })
                            res += '</p>';
                        }
                        if (comment.opinionRecords != null && comment.opinionRecords.length > 0) {
                            res += '<p><strong>Opinión:</strong><br/>';
                            comment.opinionRecords.forEach(function(record, j) {
                                res += record.classifier + ": " + record.record.opinion + " (";
                                if (record.record.opinion === "Subjective")
                                    res += Math.round10(record.record.subjectiveScore * 100, -2) + "%)<br/>";
                                else
                                    res += Math.round10(100 - record.record.subjectiveScore * 100, -2) + "%)<br/>";
                            })
                            res += '</p>';
                        }
                        return res;
                    },
                    html: true,
                    trigger: "focus",
                    container: '#comments-list',
                    template: '<div class="popover popover-details" role="tooltip"><div class="arrow"></div><h3 class="popover-header"></h3><div class="popover-body"></div></div>'
                });
            $(details).appendTo($listItem);
        }

        $listItem.appendTo($container);
    });
}

function generateReadMore() {
    $("p.readmore").readmore({
        speed: 75,
        moreLink: '<a href="#" class="readmore results" title="Leer más"><i class="far fa-plus-square fa-lg"></i></a>"',
        lessLink: '<a href="#" class="readmore results" title="Leer menos"><i class="far fa-minus-square fa-lg"></i></a>"',
    });
}

/**
 * Construir Text Input.
 * option.id será la clave del HashMap en Spring.
 * @param container Elemento contenedor del input
 * @param option Valores sobre la opción (clave -> valor)
 */
function renderText(container, option) {
    var optionDiv = $('<div>', {
        class: "col-3 option-container form-group"
    }).append(
        $('<label>', {
            for: option.id,
            text: option.name
        }),
        $('<input>', {
            type: 'text',
            id: "options'"+option.id+"'",
            class: 'form-control',
            name: "options['"+option.id+"']",
            value: option.default
        })
    );
    optionDiv.appendTo(container);
}

/**
 * Construir Number Input.
 * option.id será la clave del HashMap en Spring
 * @param container Elemento contenedor del input
 * @param option Valores sobre la opción (clave -> valor)
 */
function renderNumber(container, option) {
    var optionDiv = $('<div>', {
        class: "col-3 option-container form-group"
    }).append(
        $('<label>', {
            for: option.id,
            text: option.name
        })
    );

    var input = $('<input>', {
        type: 'number',
        id: "options'"+option.id+"'",
        class: 'form-control',
        name: "options['"+option.id+"']",
        value: option.default
    });
    input.appendTo(optionDiv);

    $.each(option.options, function(index, option) {
        if (option.name === "min")
            input.attr("min", option.value);
        else if (option.name === "max")
            input.attr("max", option.value);
    });

    optionDiv.appendTo(container);
}

/**
 * Construir Select Input.
 * option.id será la clave del HashMap en Spring
 * @param container Elemento contenedor del input
 * @param option Valores sobre la opción (clave -> valor)
 */
function renderSelect(container, option) {
    var optionDiv = $('<div>', {
        class: "col-3 option-container form-group"
    }).append(
        $('<label>', {
            for: option.id,
            text: option.name
        })
    );

    var select = $("<select></select>").attr({
        id: "options'"+option.id+"'",
        class: "form-control",
        name: "options['"+option.id+"']"
    });
    select.appendTo(optionDiv);

    $.each(option.options, function (index, option) {
        var opt = $("<option></option>").attr("value", option.value).text(option.name);
        opt.appendTo(select);
    });

    optionDiv.appendTo(container);
}

/**
 * Construir Radio Input.
 * option.id será la clave del HashMap en Spring
 * @param container Elemento contenedor del input
 * @param option Valores sobre la opción (clave -> valor)
 */
function renderRadio(container, option) {
    var optionDiv = $('<div>', {
        class: "col-3 option-container"
    }).append(
        $('<p>', {
            text: option.name
        })
    );

    $.each(option.options, function (index, opt) {
        var innerDiv = $("<div></div>").addClass("custom-control custom-radio custom-control-inline");
        var input = $("<input />").attr({
            id: "options'"+option.id+"'"+opt.name,
            name: "options['"+option.id+"']",
            value: opt.value,
            type: "radio",
            class: "custom-control-input"
        });
        var label = $("<label></label>").attr({
            for: "options'"+option.id+"'"+opt.name,
            class: "custom-control-label"
        }).text(opt.name);
        if (option.default === opt.value)
            input.attr("checked", "checked");
        input.appendTo(innerDiv);
        label.appendTo(innerDiv);
        innerDiv.appendTo(optionDiv);
    });

    optionDiv.appendTo(container);
}

/**
 * Construir Select para elegir ID de película en IMDB
 * con Select2 (https://select2.org/)
 */
function renderIMDBSelect() {
    $('.imdb-select').select2({
        theme: "bootstrap",
        placeholder: "Título de película",
        language: "es",
        ajax: {
            url: ctx + "/api/imdb-lookup",
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

function renderClassifierRadio(container, parameter) {
    var index = $(container).closest('.classifier-item').data("id");
    var outerDiv = $('<div>', {class: 'form-group col-3 classifier-option'});
    $('<p>' + parameter.name + '</p>').appendTo(outerDiv);
    $.each(parameter.options, function (i, option) {
        var innerDiv = $('<div>', {
            class: 'custom-control custom-radio custom-control-inline'
        });
        innerDiv.append(
            $('<input>', {
                type: 'radio',
                class: 'custom-control-input',
                value: option.value,
                id: "analysis'" + index + "'.options'"+parameter.id+"'"+option.value,
                name: "analysis['" + index + "'].options['"+parameter.id+"']"
            }),
            $('<label>', {
                class: 'custom-control-label',
                for: "analysis'" + index + "'.options'"+parameter.id+"'"+option.value,
                html: option.name
            })
        );
        if (parameter.default === option.value)
            $(innerDiv).find('input:radio').each(function() {
                $(this).attr('checked', 'checked');
            });
        innerDiv.appendTo(outerDiv);
    });
    outerDiv.appendTo(container);
}

function renderClassifierSelect(container, parameter) {
    var index = $(container).closest('.classifier-item').data("id");
    var outerDiv = $('<div>', {class: 'form-group col-3 classifier-option'});
    var label = $('<label>', {
        for: "analysis'"+index+"'.options'"+parameter.id+"'",
        html: parameter.name
    });
    var select = $('<select>', {
        class: "form-control",
        id: "analysis'"+index+"'.options'"+parameter.id+"'",
        name: "analysis["+index+"].options["+parameter.id+"]"
    });
    $.each(parameter.options, function (i, option) {
        select.append(
            $('<option>', {
                value: option.value,
                html: option.name
            })
        );
    });
    label.appendTo(outerDiv);
    select.appendTo(outerDiv);
    outerDiv.appendTo(container);
}

function renderClassifierNumber(container, parameter) {
    var index = $(container).closest('.classifier-item').data("id");
    var outerDiv = $('<div>', {class: 'form-group col-3 classifier-option'});

    outerDiv.append(
        $('<label>', {
            for: "analysis'"+index+"'.options'"+parameter.id+"'",
            html: parameter.name
        }),
        $('<input>', {
            class: "form-control",
            type: "number",
            id: "analysis'"+index+"'.options'"+parameter.id+"'",
            name: "analysis["+index+"].options["+parameter.id+"]",
            value: parameter.default
        })
    );

    $.each(parameter.options, function(i, option) {
        $(outerDiv).find(':input[type="number"]').attr(option.name, option.value);
    });

    outerDiv.appendTo(container);
}

function renderClassifierText(container, parameter) {
    var index = $(container).closest('.classifier-item').data("id");
    var outerDiv = $('<div>', {class: 'form-group col-3 classifier-option'});

    outerDiv.append(
        $('<label>', {
            for: "analysis'"+index+"'.options'"+parameter.id+"'",
            html: parameter.name
        }),
        $('<input>', {
            class: "form-control",
            type: "text",
            id: "analysis'"+index+"'.options'"+parameter.id+"'",
            name: "analysis["+index+"].options["+parameter.id+"]",
            value: parameter.default
        })
    );

    outerDiv.appendTo(container);
}

/**
 * Localización españa de datatables
 * @returns {localización}
 */
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

/**
 * Ordenar filas por fecha en datatables
 * https://datatables.net/plug-ins/sorting/date-euro
 */
if (typeof jQuery.fn.dataTableExt != 'undefined') {
    jQuery.extend( jQuery.fn.dataTableExt.oSort, {
        "date-euro-pre": function ( a ) {
            var x;

            if ( $.trim(a) !== '' ) {
                var frDatea = $.trim(a).split(' ');
                var frTimea = (undefined != frDatea[1]) ? frDatea[1].split(':') : [0o0,0o0,0o0];
                var frDatea2 = frDatea[0].split('/');
                x = (frDatea2[2] + frDatea2[1] + frDatea2[0] + frTimea[0] + frTimea[1] + ((undefined != frTimea[2]) ? frTimea[2] : 0)) * 1;
            }
            else {
                x = Infinity;
            }

            return x;
        },

        "date-euro-asc": function ( a, b ) {
            return a - b;
        },

        "date-euro-desc": function ( a, b ) {
            return b - a;
        }
    } );
}

(function(){
    /**
     * Decimal adjustment of a number.
     *
     * @param   {String}    type    The type of adjustment.
     * @param   {Number}    value   The number.
     * @param   {Integer}   exp     The exponent (the 10 logarithm of the adjustment base).
     * @returns {Number}            The adjusted value.
     */
    function decimalAdjust(type, value, exp) {
        // If the exp is undefined or zero...
        if (typeof exp === 'undefined' || +exp === 0) {
            return Math[type](value);
        }
        value = +value;
        exp = +exp;
        // If the value is not a number or the exp is not an integer...
        if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) {
            return NaN;
        }
        // Shift
        value = value.toString().split('e');
        value = Math[type](+(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp)));
        // Shift back
        value = value.toString().split('e');
        return +(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp));
    }

    // Decimal round
    if (!Math.round10) {
        Math.round10 = function(value, exp) {
            return decimalAdjust('round', value, exp);
        };
    }
    // Decimal floor
    if (!Math.floor10) {
        Math.floor10 = function(value, exp) {
            return decimalAdjust('floor', value, exp);
        };
    }
    // Decimal ceil
    if (!Math.ceil10) {
        Math.ceil10 = function(value, exp) {
            return decimalAdjust('ceil', value, exp);
        };
    }
})();


/*
 *  FUNCIONES PARA GESTIÓN DE MODELOS DE LENGUAJE DE LOS ADAPTADORES
 */

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
            console.error("Error recuperando los clasificadores.");
        })
}

/* Recuperar clasificadores de polaridad o de subjetividad disponibles */
function getClassifiers() {
    var lang = $('#language').val();
    if ($("input[name='classifierType']:checked").val() === "POLARITY")
        return getPolarityClassifiers(lang, true);
    else if ($("input[name='classifierType']:checked").val() === "OPINION")
        return getOpinionClassifiers(lang, true);
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

    // Construir los parámetros disponibles para la creación de un nuevo modelo
    $.each(classifier.model_creation_params, function (index, parameter) {
        var $outerDiv = $('<div class="card mb-4 border-secondary bg-light"></div>');
        var $bodyDiv = $('<div class="card-body"></div>');
        var $title = $('<h5 class="card-title mb-4">' + parameter.name + '</h5>');
        var $rowDiv = $('<div class="row"></div>');
        var $parameterHTML = makeParameter(parameter, "root-parameter");

        $parameterHTML.appendTo($rowDiv);
        $title.appendTo($bodyDiv);
        $rowDiv.appendTo($bodyDiv);
        $bodyDiv.appendTo($outerDiv);
        $outerDiv.appendTo($(".parameters-container"));
        var $select = $outerDiv.find('select, input:checked');
        attachOptionParameters($select, classifier);
    })
}

// Añadir listeners para diferentes acciones
function addListeners(classifiers) {
    // Eliminamos listeners previos
    $("input[name='classifierType']").off();
    $("select[name='adapterSelect']").off();
    $(".root-parameter").off();

    // Listener cambiar el tipo de clasificador
    $("input[name='classifierType']").on('change', function() {
        constructClassifiersSelect();
        if ($(this).val() === "POLARITY") {
            $('.polarity-datasets').show();
            $('.opinion-datasets').hide();
        }
        else if ($(this).val() === "OPINION") {
            $('.polarity-datasets').hide();
            $('.opinion-datasets').show();
        }
    });

    // Listener seleccionar el clasificador
    $("select[name='adapterSelect']").on('change', function() {
        populateClassifierParameters(getSelectedClassifier(classifiers));
    });

    /* Acción al seleccionar una de las opciones de los parámetros para crear un nuevo modelo
     * Podemos tener "subparámetros" para las opciones de los parámetros. */
    $(".root-parameter").on('change', 'select, input:checked', function () {
        attachOptionParameters($(this), getSelectedClassifier(classifiers));
    });
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