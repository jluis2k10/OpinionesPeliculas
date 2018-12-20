<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<h4 class="mb-3">
    Generar Corpus
    <c:if test="${not empty corpus.comments}">
        <small class="text-muted">
            <a href="#corpus-wrapper" data-toggle="collapse" aria-controls="corpus-wrapper" aria-expanded="false">[Mostrar/Ocultar]</a> Corpus (${corpus.comments.size()} comentarios, ${corpus.lang})
        </small>
    </c:if>
</h4>
<c:if test="${not empty corpus.comments}">
    <div class="col-12 collapse" id="corpus-wrapper">
        <table id="generated-corpus" class="table table-striped table-sm table-bordered data-table" width="100%" cellpadding="0">
            <thead>
            <tr>
                <th>Fuente</th>
                <th>Comentario</th>
                <th>Fecha</th>
            </tr>
            </thead>
        </table>
    </div>
    <script>
        var corpus = ${corpus.toJson(true, false, false).toString()};
    </script>
</c:if>

<form:form method="post" modelAttribute="sourceForm" action="${path}" enctype="multipart/form-data">
<div class="card mb-4 border-secondary bg-light">
    <div class="card-body">
        <h5 class="card-title mb-4">Fuente de Comentarios</h5>
        <div class="row sources-container">
            <spring:bind path="term">
                <div class="col-12 input-group form-group">
                    <div class="input-group-prepend">
                        <button class="btn btn-primary dropdown-toggle sources-dropdown" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Origen</button>
                        <div id="sources-dropdown" class="dropdown-menu"></div>
                    </div>
                    <form:input path="term" type="text" cssClass="form-control" cssErrorClass="form-control is-invalid" placeholder="Término de búsqueda" aria-describedby="errorsTerm"></form:input>
                    <form:errors path="term" cssClass="invalid-feedback" id="errorsTerm"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="file">
                <div class="col-9 file-container" style="display: none;">
                    <p class="mb-2">Desde archivo (1 comentario por línea)</p>
                    <div class="custom-file mb-3 file-datasets">
                        <form:input path="file" type="file" cssClass="custom-file-input" cssErrorClass="custom-file-input is-invalid" lang="es" aria-describedby="errorsFile"></form:input>
                        <label class="custom-file-label" for="file">Subir Dataset</label>
                        <form:errors path="file" cssClass="invalid-feedback" id="errorsFile"></form:errors>
                    </div>
                </div>
            </spring:bind>
            <spring:bind path="limit">
                <div class="col-3 limit-container form-group" style="display: none;">
                    <form:label path="limit">Comentarios a recuperar (máx.)</form:label>
                    <form:input path="limit" type="number" min="1" cssClass="form-control" cssErrorClass="form-control is-invalid" id="limit" value="50" aria-describedby="errorsLimit"></form:input>
                    <form:errors path="limit" cssClass="invalid-feedback" id="errorsLimit"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="sinceDate">
                <div class="col-3 sinceDate-container form-group" style="display: none;">
                    <form:label path="sinceDate">Desde</form:label>
                    <form:input path="sinceDate" type="text" cssClass="form-control datetimepicker-input" cssErrorClass="form-control datetimepicker-input is-invalid" id="sinceDate" placeholder="DD/MM/AAAA" aria-describedby="errorsSinceDate" data-toggle="datetimepicker" data-target="#sinceDate"></form:input>
                    <form:errors path="sinceDate" cssClass="invalid-feedback" id="errorsSinceDate"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="untilDate">
                <div class="col-3 untilDate-container form-group" style="display: none;">
                    <form:label path="untilDate">Hasta</form:label>
                    <form:input path="untilDate" type="text" cssClass="form-control datetimepicker-input" cssErrorClass="form-control datetimepicker-input is-invalid" id="untilDate" placeholder="DD/MM/AAAA" aria-describedby="errorsUntilDate" data-toggle="datetimepicker" data-target="#untilDate"></form:input>
                    <form:errors path="untilDate" cssClass="invalid-feedback" id="errorsUntilDate"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="lang">
                <div class="col-3 language-container form-group" style="display: none;">
                    <form:label path="lang">Idioma</form:label>
                    <form:select path="lang" cssClass="form-control" cssErrorClass="form-control is-invalid" aria-describedby="errorsLang">
                        <form:option value="NONE" label="--Selecciona--"></form:option>
                    </form:select>
                    <form:errors path="lang" cssClass="invalid-feedback" id="errorsLang"></form:errors>
                </div>
            </spring:bind>
            <div class="col-3 imdbID-container" style="display: none;">
                <label for="imdbID">Película</label>
                <select class="imdb-select form-control" id="imdbID">
                    <option value=""></option>
                </select>
            </div>
        </div>
    </div>
</div>
<div class="row">
    <c:choose>
        <c:when test="${not empty corpus.comments}">
            <div class="col-4">
                <sec:authorize access="isAuthenticated()">
                    <button type="submit" class="btn btn-primary btn-lg btn-block" value="save-corpus"><i class="fas fa-save"></i> Guardar Corpus</button>
                </sec:authorize>
            </div>
            <div class="col-4">
                <button type="submit" class="btn btn-primary btn-lg btn-block" value="get-comments"><i class="fas fa-comments"></i> Recuperar más comentarios</button>
            </div>
            <div class="col-4">
                <button type="submit" class="btn btn-success btn-lg btn-block" value="next">Siguiente <i class="fas fa-angle-right"></i> <small><em>Análisis de Opinión</em></small></button>
            </div>
        </c:when>
        <c:otherwise>
            <div class="col-4">

            </div>
            <div class="col-4">
                <button type="submit" class="btn btn-primary btn-lg btn-block" value="get-comments"><i class="fas fa-comments"></i> Recuperar comentarios</button>
            </div>
            <div class="col-4">
                <button type="submit" class="btn btn-secondary btn-lg btn-block" value="next" disabled="disabled">Siguiente <i class="fas fa-angle-right"></i> <small><em>Análisis de Dominio</em></small></button>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<form:hidden path="source" value="" id="source"></form:hidden>
<form:hidden path="sourceAdapter" value="" id="sourceAdapter"></form:hidden>
</form:form>

<%@ include file="_js.jsp"%>

<link rel="stylesheet" href="webjars/select2/4.0.3/css/select2.min.css" />
<link rel="stylesheet" href="${path}/css/select2-bootstrap.min.css" />
<link rel="stylesheet" href="webjars/tempusdominus-bootstrap-4/5.0.0-alpha.16/build/css/tempusdominus-bootstrap-4.min.css" />
<link rel="stylesheet" href="webjars/datatables/1.10.16/css/dataTables.bootstrap4.min.css" />
<script type="text/javascript" src="webjars/select2/4.0.3/js/select2.min.js"></script>
<script type="text/javascript" src="webjars/select2/4.0.3/js/i18n/es.js"></script>
<script type="text/javascript" src="webjars/momentjs/2.20.1/min/moment-with-locales.min.js"></script>
<script type="text/javascript" src="webjars/tempusdominus-bootstrap-4/5.0.0-alpha.16/build/js/tempusdominus-bootstrap-4.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.16/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.16/js/dataTables.bootstrap4.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>
<script>
    var corpusLang = ("${corpus.lang}" != "" ? "${corpus.lang}" : null);

    $(document).ready(function () {
        $.when(getCorporaSources(corpusLang))
            .done(function (_sources) {
                renderSourcesButton(_sources, corpusLang);
                /* Eventlistener para la acción al seleccionar una fuente de comentarios */
                $('.source-button').click(function () {
                    var sourceName = $(this).get(0).dataset.name;
                    var selectedSource = $.grep(_sources, function (source) {
                        return sourceName === source.name;
                    })[0];
                    renderSourceOptions(selectedSource, corpusLang);
                });
            })
            .fail(function() {
                console.error("Error recuperando fuentes de comentarios para Corpus.");
            })

        // Select para elegir película y encontrar su identificador en IMDB
        renderIMDBSelect();
        // Listener. Al seleccionar la película pasamos el imdbID al input de la búsqueda
        $('.imdb-select').on('select2:selecting', function(e) {
            $('#term').val(e.params.args.data.id);
        });

        /* Inicializar selectores de fecha */
        $(function () {
            $('#sinceDate').datetimepicker({
                viewmode: 'months',
                format: 'DD/MM/YYYY',
                locale: 'es'
            });
            $('#untilDate').datetimepicker({
                viewmode: 'months',
                format: 'DD/MM/YYYY',
                locale: 'es'
            });
        });

        /* Datatable con los comentarios recuperados */
        if (typeof corpus != 'undefined') {
            $.fn.dataTableExt.oStdClasses.sWrapper = 'mb-4 dataTables_wrapper container-fluid dt-bootstrap4';
            var corpusTable = $("#generated-corpus").DataTable({
                language: datatablesLocalization(),
                data: corpus.comments,
                autoWidth: false,
                columns: [
                    {data: "source", width: "90px"},
                    {data: "content"},
                    {data: "date", width: "100px"}
                ],
                columnDefs: [
                    {type: 'date-euro', targets: 2}
                ],
                "order": [[2, 'desc']]
            });
        }
    });
    // Listeners para los diferentes botones de enviar formulario
    $('#sourceForm button[type="submit"]').click(function (e) {
        e.preventDefault();
        if ($(this).attr("value") === "save-corpus") {
            $('#modal-saveCorpus').modal();
        }
        else if ($(this).attr("value") === "get-comments") {
            showLoading("Recuperando comentarios desde " + $('.sources-dropdown').html());
            $('#sourceForm').attr('action', "?action=get-comments").submit();
        }
        else if ($(this).attr("value") === "next") {
            $('#sourceForm').attr('action', ctx + "/domain-analysis").submit();
        }
    });
</script>

<sec:authorize access="isAuthenticated()">
    <%@include file="_modal_save.jsp"%>
</sec:authorize>

<%@ include file="_footer.jsp"%>