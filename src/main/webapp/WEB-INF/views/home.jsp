<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<form:form method="post" modelAttribute="searchForm" action="results">
    <!-- Grupo para Opciones de Fuente de Comentarios -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Opciones de búsqueda</h5>
            <div class="row">
                <spring:bind path="term">
                    <div class="col-12 ${status.error ? "has-error" : ""}">
                        <div class="input-group form-group">
                            <div class="input-group-prepend">
                                <button class="btn btn-outline-primary dropdown-toggle sources-dropdown" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Origen</button>
                                <div id="sources-dropdown" class="dropdown-menu">
                                </div>
                            </div>
                            <form:input path="term" type="text" cssClass="form-control" placeholder="Término de búsqueda" aria-describedby="errorsTerm"></form:input>
                        </div>
                        <form:errors path="term" cssClass="help-block" id="errorsTerm"></form:errors>
                    </div>
                </spring:bind>
                <spring:bind path="limit">
                    <div class="col-3 limit-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="limit">Comentarios a recuperar (máx.)</form:label>
                            <form:input path="limit" type="number" min="1" cssClass="form-control" id="limit" value="50" aria-describedby="errorsLimit"></form:input>
                            <form:errors path="limit" cssClass="help-block" id="errorsLimit"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="sinceDate">
                    <div class="col-3 sinceDate-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="sinceDate">Desde</form:label>
                            <form:input path="sinceDate" type="text" cssClass="form-control" id="sinceDate" placeholder="DD/MM/AAAA" aria-describedby="errorsSinceDate"></form:input>
                            <form:errors path="sinceDate" cssClass="help-block" id="errorsSinceDate"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="untilDate">
                    <div class="col-3 untilDate-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="untilDate">Hasta</form:label>
                            <form:input path="untilDate" type="text" cssClass="form-control" id="untilDate" placeholder="DD/MM/AAAA" aria-describedby="errorsUntilDate"></form:input>
                            <form:errors path="untilDate" cssClass="help-block" id="errorsUntilDate"></form:errors>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="lang">
                    <div class="col-3 language-container ${status.error ? "has-error" : ""}" style="display: none;">
                        <div class="form-group">
                            <form:label path="lang">Idioma</form:label>
                            <form:select path="lang" cssClass="form-control">
                                <form:option value="NONE" label="--Selecciona--"></form:option>
                            </form:select>
                        </div>
                    </div>
                </spring:bind>
                <div class="col-3 imdbID-container" style="display: none;">
                    <label for="imdbID">Película</label>
                    <select class="imdb-select form-control" id="imdbID">
                        <option value=""></option>
                    </select>
                </div>
                <div class="col-12">
                    <p class="separator"><span>Pre-procesar resultados</span></p>
                </div>
                <spring:bind path="cleanTweet">
                    <div class="col-3 cleanTweet-container" style="display: none;">
                        <div class="form-group">
                            <p><strong>¿Limpiar Tweets?</strong></p>
                            <label class="radio-inline">
                                <form:radiobutton path="cleanTweet" id="true" value="true"></form:radiobutton> Sí
                            </label>
                            <label class="radio-inline">
                                <form:radiobutton path="cleanTweet" id="false" value="false" checked="checked"></form:radiobutton> No
                            </label>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="delStopWords">
                    <div class="col-3">
                        <div class="form-group">
                            <p><strong>¿Eliminar stop-words?</strong></p>
                            <label class="radio-inline">
                                <form:radiobutton path="delStopWords" id="true" value="true"></form:radiobutton> Sí
                            </label>
                            <label class="radio-inline">
                                <form:radiobutton path="delStopWords" id="false" value="false" checked="checked"></form:radiobutton> No
                            </label>
                        </div>
                    </div>
                </spring:bind>
            </div>
        </div>
    </div>

    <!-- Grupo para Análisis de Sentimiento -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Análisis de Sentimiento</h5>
            <div class="row">
                <spring:bind path="sentimentAdapter">
                <div class="col-6">
                    <div class="form-group ${status.error ? "has-error" : ""}">
                        <form:label path="sentimentAdapter">Analizador</form:label>
                        <form:select path="sentimentAdapter" cssClass="form-control"></form:select>
                    </div>
                </div>
            </spring:bind>
                <spring:bind path="sentimentModel">
                <div class="col-6 sentimentModel-container" style="display: none;">
                    <div class="form-group ${status.error ? "has-error" : ""}">
                        <form:label path="sentimentModel">Modelo</form:label>
                        <form:select path="sentimentModel" cssClass="form-control"></form:select>
                    </div>
                </div>
            </spring:bind>
            </div>
            <div class="row sentiment-container"></div>
        </div>
    </div>

    <!-- Grupo para Análisis de Subjetividad -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title mb-4">Análisis de Subjetividad</h5>
            <div class="row">
                <spring:bind path="classifySubjectivity">
                    <div class="col-6">
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
                <spring:bind path="discardNonSubjective">
                    <div class="subjectivity-item col-6" style="display: none;">
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
                <spring:bind path="subjectivityAdapter">
                    <div class="subjectivity-item col-6" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="subjectivityAdapter">Analizador</form:label>
                            <form:select path="subjectivityAdapter" cssClass="form-control"></form:select>
                        </div>
                    </div>
                </spring:bind>
                <spring:bind path="subjectivityModel">
                    <div class="col-6 subjectivity-item subjectivityModel-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="subjectivityModel">Modelo</form:label>
                            <form:select path="subjectivityModel" cssClass="form-control"></form:select>
                        </div>
                    </div>
                </spring:bind>
            </div>
            <div class="row subjectivity-container"></div>
        </div>
    </div>

    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-lg btn-block">Enviar</button>
        </div>
    </div>
    <form:hidden path="source" value="" id="source"></form:hidden>
    <form:hidden path="sourceClass" value="" id="sourceClass"></form:hidden>
    <form:hidden path="created" value="" id="created"></form:hidden>
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
    var sentimentAdapters = null;
    var subjectivityAdapters = null;

    $.when(getCommentSources(), getSentimentAdapters(), getSubjectivityAdapters())
        .done(function(_commentSources, _sentimentAdapters, _subjectivityAdapters) {
            sentimentAdapters = _sentimentAdapters;
            subjectivityAdapters = _subjectivityAdapters;
            makeSourcesButton(_commentSources);
            populateAdapters("sentiment", sentimentAdapters);
            populateAdapters("subjectivity", subjectivityAdapters);
            /* Añadir eventlistener para la acción al seleccionar una fuente de comentarios */
            $('.source-button').click(function () {
                $.when(makeSourceOptions($(this).get(0), _commentSources))
                    .done(function() {
                        populateAdapters("sentiment", sentimentAdapters);
                        if ($("input[name='classifySubjectivity']").get(0).checked) {
                            populateAdapters("subjectivity", subjectivityAdapters);
                        };
                    })
                    .fail(function() {
                        console.error("Error al hacer click en fuente de comentarios");
                    })
            });
        })
        .fail(function() {
            console.error("Error");
        });

    $(document).ready(function() {
        /* Mostrar/ocultar formulario de subjetividad en función de si se debe analizar o no la subjetividad */
        if ($("input[name='classifySubjectivity']").get(0).checked) {
            $(".subjectivity-item").show();
        }
    });

    // Select para elegir película y encontrar su identificador en IMDB
    createIMDBSelect("${path}");
    // Al seleccionar la película pasamos el imdbID al input de la búsqueda
    $('.imdb-select').on('select2:selecting', function(e) {
        $('#term').val(e.params.args.data.id);
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
    $("#lang").change(function() {
        populateAdapters("sentiment", sentimentAdapters);
        if ($("input[name='classifySubjectivity']").get(0).checked) {
            populateAdapters("subjectivity", subjectivityAdapters);
        };
    });

    /* Acción al seleccionar si se deben analizar subjetividad o no */
    $("input[name='classifySubjectivity']").change(function() {
        if (this.checked && this.value === "true") {
            populateAdapters("subjectivity", subjectivityAdapters);
            $(".subjectivity-item").show();
        } else {
            $("#subjectivityAdapter").empty();
            $("#subjectivityModel").empty();
            $(".subjectivity-item").hide();
        }
    });

    /* Acción al seleccionar una opción del select con los modelos para el análisis de sentimiento */
    $("#sentimentAdapter").change(function () {
        $selected = $(this).find("option:selected")[0];
        $adapterClass = $selected.value;
        $adapter = $.grep(sentimentAdapters, function(e) {
            return e.class === $adapterClass;
        });
        populateModels("sentiment", $adapter[0]);       // Rellener el select con los modelos del adaptador
        makeAdapterOptions("sentiment", $adapter[0]);   // Construir las opciones para el adaptador
    });

    /* Acción al seleccionar una opción del select con los modelos para el análisis de subjetividad */
    $("#subjectivityAdapter").change(function () {
        $selected = $(this).find("option:selected")[0];
        $adapterClass = $selected.value;
        $adapter = $.grep(subjectivityAdapters, function(e) {
            return e.class === $adapterClass;
        });
        populateModels("subjectivity", $adapter[0]);       // Rellener el select con los modelos del adaptador
        makeAdapterOptions("subjectivity", $adapter[0]);   // Construir las opciones para el adaptador
    });

    /* Acción al enviar el formulario */
    $("#searchForm").submit(function (e) {
        $("#created").val(new Date().toLocaleString()); // Fecha de creación de la búsqueda
        //e.preventDefault();
    })
</script>
<%@ include file="_footer.jsp"%>