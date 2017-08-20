<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<form:form method="post" modelAttribute="searchForm" action="results">
    <!-- Grupo para Opciones de Fuente de Comentarios -->
    <div class="row">
        <fieldset class="col-xs-12">
            <legend>Opciones de búsqueda</legend>
            <spring:bind path="searchTerm">
                <div class="col-xs-12 ${status.error ? "has-error" : ""}">
                    <div class="input-group form-group">
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
        </fieldset>
    </div>

    <!-- Grupo para Análisis de Subjetividad -->
    <div class="row">
        <fieldset class="col-xs-12 subjectivity-container">
            <legend>Análisis de Subjetividad</legend>
            <spring:bind path="classifySubjectivity">
                <div class="col-xs-12">
                    <div class="form-group">
                        <p><strong>¿Analizar subjetividad/objetividad?</strong></p>
                        <label class="radio-inline">
                            <form:radiobutton path="classifySubjectivity" id="true" value="true"></form:radiobutton> Sí
                        </label>
                        <label class="radio-inline">
                            <form:radiobutton path="classifySubjectivity" id="false" value="false" checked="checked"></form:radiobutton> No
                        </label>
                    </div>
                </div>
            </spring:bind>
            <div class="subjectivity-form-container" style="display: none;">
                <spring:bind path="subjectivityAdapter">
                    <div class="col-xs-6">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="subjectivityAdapter">Analizador</form:label>
                            <form:select path="subjectivityAdapter" cssClass="form-control"></form:select>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="subjectivityModel">
                    <div class="col-xs-6 subjectivityModel-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="subjectivityModel">Modelo</form:label>
                            <form:select path="subjectivityModel" cssClass="form-control"></form:select>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="discardNonSubjective">
                    <div class="col-xs-12">
                        <div class="form-group">
                            <p><strong>¿Descartar comentarios no subjetivos?</strong></p>
                            <label class="radio-inline">
                                <form:radiobutton path="discardNonSubjective" id="true" value="true" checked="checked"></form:radiobutton> Sí
                            </label>
                            <label class="radio-inline">
                                <form:radiobutton path="discardNonSubjective" id="false" value="false"></form:radiobutton> No
                            </label>
                        </div>
                    </div>
                </spring:bind>
                <div class="col-xs-12 clearfix"></div>
            </div>
        </fieldset>
    </div>

    <!-- Grupo para Análisis de Sentimiento -->
    <div class="row">
        <fieldset class="col-xs-12 sentiment-container">
            <legend>Análisis de Sentimiento</legend>
            <spring:bind path="sentimentAdapter">
                <div class="col-xs-6">
                    <div class="form-group ${status.error ? "has-error" : ""}">
                        <form:label path="sentimentAdapter">Analizador</form:label>
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
            <div class="col-xs-12 clearfix"></div>
        </fieldset>
    </div>
    <div class="row">
        <form:hidden path="sourceClass" value="" id="sourceClass"></form:hidden>
        <div class="col-xs-12">
            <button type="submit" class="btn btn-primary">Enviar</button>
        </div>
    </div>
</form:form>

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
<script type="text/javascript" src="${path}/js/custom.js"></script>

<script>
    $sentimentAdapters = null;
    $subjectivityAdapters = null;

    $(document).ready(function() {
        /* Recuperar las posibles fuentes de comentarios */
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/api/comments-source",
            timeout: 5000,
            success: function(data) {
                makeSourcesButton(data);
                var first_link = $("#sources-dropdown li:first-child a");
                makeSourcesOptions(first_link.get(0));
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.error("Request: " + JSON.stringify(XMLHttpRequest) + "\n\nStatus: " + textStatus + "\n\nError: " + errorThrown);
            }
        });

        /* Mostrar/ocultar formulario de subjetividad en función de si se debe analizar o no la subjetividad */
        if ($("input[name='classifySubjectivity']").get(0).checked) {
            $(".subjectivity-form-container").show();
        }

        /* Recuperar los adaptadores para el análisis de subjetividad */
        $.ajax({
            type: "GET",
            contentType: "application/json",
            url: "${path}/api/subjectivity-adapters",
            timeout: 5000,
            success: function(data) {
                $subjectivityAdapters = data;
                if ($("input[name='classifySubjectivity']").get(0).checked) {
                    populateAdapters("subjectivity", data);
                }
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
                populateAdapters("sentiment", data);
            },
            error: function (XMLHttpRequest, textStatus, errorThrown) {
                console.error("Request: " + JSON.stringify(XMLHttpRequest) + "\n\nStatus: " + textStatus + "\n\nError: " + errorThrown);
            }
        });
    });

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

    /* Acción al seleccionar el idioma */
    $("input[name='lang']").change(function() {
        populateAdapters("sentiment", $sentimentAdapters);
        if ($("input[name='classifySubjectivity']").get(0).checked) {
            populateAdapters("subjectivity", $subjectivityAdapters);
        };
    });

    /* Acción al seleccionar si se deben analizar subjetividad o no */
    $("input[name='classifySubjectivity']").change(function() {
        if (this.checked && this.value === "true") {
            populateAdapters("subjectivity", $subjectivityAdapters);
            $(".subjectivity-form-container").show();
        } else {
            $(".subjectivity-form-container").hide();
        }
    });

    /* Acción al seleccionar una opción del select con los adaptadores para el análisis de sentimiento */
    $("#sentimentAdapter").change(function () {
        $selected = $(this).find("option:selected")[0];
        $adapterClass = $selected.value;
        $adapter = $.grep($sentimentAdapters, function(e) {
            return e.class === $adapterClass;
        });
        populateModels("sentiment", $adapter[0]);       // Rellener el select con los modelos del adaptador
        makeAdapterOptions("sentiment", $adapter[0]);   // Construir las opciones para el adaptador
    });
</script>
<%@ include file="_footer.jsp"%>