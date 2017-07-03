<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>


<form:form method="post" modelAttribute="searchForm">
    <div class="row">
        <spring:bind path="searchTerm">
            <div class="col-xs-12 ${status.error ? "has-error" : ""}">
                <div class="input-group">
                    <div class="input-group-btn">
                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Origen <span class="caret"></span></button>
                        <ul class="dropdown-menu" id="sources-dropdown"></ul>
                    </div>
                    <form:input path="searchTerm" type="text" cssClass="form-control" placeholder="Término de búsqueda" aria-describedby="errorsSearchTerm"></form:input>
                    <div class="input-group-addon source-placeholder"></div>
                </div>
                <form:errors path="searchTerm" cssClass="help-block" id="errorsSearchTerm"></form:errors>
            </div>
        </spring:bind>
        <spring:bind path="limit">
            <div class="col-xs-3 limit-container" style="display: none;">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="limit">Comentarios a recuperar (máx.)</form:label>
                    <form:input path="limit" type="number" min="1" cssClass="form-control" id="limit" value="50" aria-describedby="errorsLimit"></form:input>
                    <form:errors path="limit" cssClass="help-block" id="errorsLimit"></form:errors>
                </div>
            </div>
        </spring:bind>
        <spring:bind path="sinceDate">
            <div class="col-xs-3 sinceDate-container" style="display: none;">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="sinceDate">Desde</form:label>
                    <form:input path="sinceDate" type="text" cssClass="form-control" id="sinceDate" placeholder="DD/MM/AAAA" aria-describedby="errorsSinceDate"></form:input>
                    <form:errors path="sinceDate" cssClass="help-block" id="errorsSinceDate"></form:errors>
                </div>
            </div>
        </spring:bind>
        <spring:bind path="untilDate">
            <div class="col-xs-3 untilDate-container" style="display: none;">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="untilDate">Hasta</form:label>
                    <form:input path="untilDate" type="text" cssClass="form-control" id="untilDate" placeholder="DD/MM/AAAA" aria-describedby="errorsUntilDate"></form:input>
                    <form:errors path="untilDate" cssClass="help-block" id="errorsUntilDate"></form:errors>
                </div>
            </div>
        </spring:bind>
        <spring:bind path="lang">
            <div class="col-xs-3 language-container ${status.error ? "has-error" : ""}" style="display: none;">
                <div class="form-group">
                    <p><strong>Idioma</strong></p>
                    <label class="radio-inline">
                        <form:radiobutton path="lang" id="en" value="en" checked="checked"></form:radiobutton> Inglés
                    </label>
                    <label class="radio-inline">
                        <form:radiobutton path="lang" id="es" value="es"></form:radiobutton> Español
                    </label>
                </div>
            </div>
        </spring:bind>
        <div class="col-xs-3 imdbID-container" style="display: none;">
            <label for="imdbID">Película</label>
            <select class="imdb-select form-control" id="imdbID">
                <option value=""></option>
            </select>
        </div>
    </div>
    <div class="row sentiment-container">
        <spring:bind path="sentimentAdapter">
            <div class="col-xs-6">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="sentimentAdapter">Analizador de Sentimiento</form:label>
                    <form:select path="sentimentAdapter" cssClass="form-control"></form:select>
                </div>
            </div>
        </spring:bind>
        <spring:bind path="sentimentModel">
            <div class="col-xs-6 sentimentModel-container" style="display: none;">
                <div class="form-group ${status.error ? "has-error" : ""}">
                    <form:label path="sentimentModel">Modelo</form:label>
                    <form:select path="sentimentModel" cssClass="form-control"></form:select>
                </div>
            </div>
        </spring:bind>
        <div class="col-xs-12" style="display: none;"></div>
    </div>
    <div class="row">
        <form:hidden path="sourceClass" value="" id="sourceClass"></form:hidden>
        <div class="col-xs-12">
            <button type="submit" class="btn btn-primary">Enviar</button>
        </div>
    </div>
</form:form>
<div class="row">
    <div class="col-xs-12">
        <c:if test="${!empty comments}">
            <ol>
                <c:forEach var="comment" items="${comments}">
                    <li>${comment.value.comment} (<strong>${comment.value.predictedSentiment}</strong>, ${comment.value.sentimentScore})
                        <p>${comment.value.tokenizedComment}</p>
                    </li>
                </c:forEach>
            </ol>
        </c:if>
    </div>
</div>

<%@ include file="_js.jsp"%>
<link rel="stylesheet" href="${path}/css/bootstrap-datetimepicker.min.css" />
<link rel="stylesheet" href="${path}/css/select2.min.css" />
<link rel="stylesheet" href="${path}/css/select2-bootstrap.min.css" />
<script type="text/javascript" src="${path}/js/select2.min.js"></script>
<script type="text/javascript" src="${path}/js/select2.es.js"></script>
<script type="text/javascript" src="${path}/js/moment-with-locales.min.js"></script>
<script type="text/javascript" src="${path}/js/transition.js"></script>
<script type="text/javascript" src="${path}/js/collapse.js"></script>
<script type="text/javascript" src="${path}/js/bootstrap-datetimepicker.min.js"></script>

<script>
    /* Select para elegir película y encontrar su identificador en IMDB */
    $('.imdb-select').select2({
        theme: "bootstrap",
        placeholder: "Título de película",
        language: "es",
        ajax: {
            url: "${path}/api/imdb-lookup",
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

    // Al seleccionar la película pasamos el imdbID al input de la búsqueda
    $('.imdb-select').on('select2:selecting', function(e) {
        $('#searchTerm').val(e.params.args.data.id);
    });
</script>
<script>
    $sentimentAdapters = null;
    $( document ).ready(function() {
        /* Recuperar las posibles fuentes de comentarios */
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/api/comments-source",
            timeout: 5000,
            success: function(data) {
                makeSourcesButton(data);
                var first_link = $("#sources-dropdown li:first-child a");
                makeOptions(first_link.get(0));
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.error("Request: " + JSON.stringify(XMLHttpRequest) + "\n\nStatus: " + textStatus + "\n\nError: " + errorThrown);
            }
        });

        /* Recuperar los adaptadores para el análisis de sentimiento */
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/api/sentiment-adapters",
            timeout: 5000,
            success: function(data) {
                $sentimentAdapters = data;
                populateSentiment($sentimentAdapters);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.error("Request: " + JSON.stringify(XMLHttpRequest) + "\n\nStatus: " + textStatus + "\n\nError: " + errorThrown);
            }
        });
    });

    /* Inicializar selectores de fecha */
    $(function () {
        $('#sinceDate').datetimepicker({
            format: 'DD/MM/YYYY',
            locale: 'es'
        });
        $('#untilDate').datetimepicker({
            format: 'DD/MM/YYYY',
            locale: 'es'
        });
    });

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

    /* Acción al seleccionar el idioma */
    $("input[name='lang']").change(function() {
        populateSentiment($sentimentAdapters);
    });

    /* Acción al seleccionar una opción del select con los adaptadores para el análisis de sentimiento */
    $("#sentimentAdapter").change(function () {
        $selected = $(this).find("option:selected")[0];
        $adapterClass = $selected.value;
        $adapter = $.grep($sentimentAdapters, function(e) {
            return e.class === $adapterClass;
        });
        populateSentimentModels($adapter[0]); // Rellener el select con los modelos del adaptador
        makeSentimentOptions($adapter[0]);    // Construir las opciones para el adaptador
    });

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
        $(".sentiment-option").remove(); // Borramos todas las opciones anteriores que puedan existir
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

</script>
<%@ include file="_footer.jsp"%>