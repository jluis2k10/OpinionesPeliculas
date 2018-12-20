<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>
<h4 class="mb-3">
    <a href="#" id="goBack" title="Generar Corpus"><i class="fas fa-chevron-left"></i></a> Análisis de Dominio
    <small class="text-muted">
        <a href="#corpus-wrapper" data-toggle="collapse" aria-controls="corpus-wrapper" aria-expanded="false">[Mostrar/Ocultar]</a> Corpus (${corpus.comments.size()} comentarios, ${corpus.lang})
    </small>
</h4>
<div class="col-12 collapse" id="corpus-wrapper">
    <table id="generated-corpus" class="table table-striped table-sm table-bordered data-table" width="100%" cellpadding="0">
        <thead>
        <tr>
            <th>Fuente</th>
            <th>Comentario</th>
            <th>Dominio</th>
            <th>Fecha</th>
        </tr>
        </thead>
    </table>
</div>
<form:form method="post" modelAttribute="domainForm" action="${path}/domain-analysis">
    <div class="d-flex flex-row align-content-center flex-wrap">
        <div class="p-1">Ejecutar análisis</div>
        <div class="p-1">
            <label class="switch">
                <input type="checkbox" checked="checked" name="execute">
                <span class="slider round"></span>
            </label>
        </div>
    </div>
    <div class="collapsible">
        <div class="list-group classifiers mb-5"></div>
    </div>
    <div class="row">
        <div class="col-4">
            <sec:authorize access="isAuthenticated()">
                <button type="submit" class="btn btn-primary btn-lg btn-block" value="save-corpus"><i class="fas fa-save"></i> Guardar Corpus</button>
            </sec:authorize>
        </div>
        <div class="col-4">
            <button type="submit" class="btn btn-primary btn-lg btn-block" value="analyse"><i class="fas fa-cogs"></i> Ejecutar Análisis</button>
        </div>
        <div class="col-4">
            <button type="submit" class="btn btn-success btn-lg btn-block" value="next">Siguiente <i class="fas fa-angle-right"></i> <small><em>Análisis de Opinión</em></small></button>
        </div>
    </div>
</form:form>

<%@ include file="_js.jsp"%>

<link rel="stylesheet" href="webjars/datatables/1.10.16/css/dataTables.bootstrap4.min.css" />
<script type="text/javascript" src="webjars/datatables/1.10.16/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.16/js/dataTables.bootstrap4.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    var classifiers;
    var index = 1;
    var corpus = ${corpus.toJson(true, false, false).toString()};
    $(document).ready(function () {
        // Aviso de que el Corpus ya tiene un análisis de dominio ejecutado sobre él
        if ($('input[name="execute"]').val() === "on" && corpus.domain_analysis) {
            showFlashMessage('warning', 'ATENCIÓN: este Corpus ya cuenta con un análisis de dominio.<br />' +
                'Ejecutar un nuevo análisis de dominio <strong>eliminará los resultados del análisis actual</strong>.');
        }
        // Recuperar clasificadores disponibles
        $.when(getDomainClassifiers("${corpus.lang}", false))
            .done(function(_classifiers) {
                classifiers = _classifiers;
                if (classifiers.length > 0)
                    renderClassifierForm(classifiers, false, 0);
                else
                    $(".classifiers").append(
                        $('<div>', {
                            class: "alert alert-warning",
                            role: "alert",
                            html: "Sin clasificadores disponibles para análisis de dominio."
                        })
                    );
            })

        // Datatable con los comentarios recuperados
        $.fn.dataTableExt.oStdClasses.sWrapper = 'mb-4 dataTables_wrapper container-fluid dt-bootstrap4';
        var corpusTable = $("#generated-corpus").DataTable({
            language: datatablesLocalization(),
            data: corpus.comments,
            autoWidth: false,
            columns: [
                {data: "source", width: "90px"},
                {data: "content"},
                {data: function (comment) {
                        if (comment.domain != null)
                            return comment.domain;
                        return 'N/A';
                    }, width: "80px"},
                {data: "date", width: "100px"}
            ],
            columnDefs: [
                {type: 'date-euro', targets: 3}
            ],
            "order": [[3, 'desc']]
        });

        // Collapsible para formulario de análisis
        $('.collapsible').collapse({
        });
        $('input[name="execute"]').change(function () {
            $('.collapsible').collapse('toggle');
            if ($('button[value="analyse"]').attr('disabled'))
                $('button[value="analyse"]').removeAttr('disabled');
            else
                $('button[value="analyse"]').attr('disabled', '');
        });
    });

    // Listener para el botón de "volver"
    $('a#goBack').click(function (e) {
        e.preventDefault();
        $('#domainForm').attr('action', "?action=back").submit();
    });

    // Listeners para los diferentes botones de enviar formulario
    $('#domainForm button[type="submit"]').click(function (e) {
        e.preventDefault();
        if ($(this).attr("value") === "save-corpus") {
            $('#modal-saveCorpus').modal();
        }
        else if ($(this).attr("value") === "analyse") {
            showLoading("Realizando análisis de Dominio");
            $('#domainForm').attr('action', "?action=analyse").submit();
        }
        else if ($(this).attr("value") === "next") {
            if ($('input[name="execute"]').is(':checked'))
                showLoading("Realizando análisis de Dominio");
            $('#domainForm').attr('action', "?action=next").submit();
        }
    });
</script>

<sec:authorize access="isAuthenticated()">
    <%@include file="_modal_save.jsp"%>
</sec:authorize>

<%@ include file="_footer.jsp"%>