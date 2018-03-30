<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>
<h4 class="mb-3">
    <a href="#" id="goBack" title="Generar Corpus"><i class="fas fa-chevron-left"></i></a> Análisis de Opinión
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
            <th>Opinión</th>
            <th>Fecha</th>
        </tr>
        </thead>
    </table>
</div>
<form:form method="post" modelAttribute="opinionForm" action="${path}/opinion-analysis">
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
            <button type="submit" class="btn btn-success btn-lg btn-block" value="next">Siguiente <i class="fas fa-angle-right"></i> <small><em>Análisis de Polaridad</em></small></button>
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
    var corpus = ${corpus.toJson(true).toString()};
    $(document).ready(function () {
        // Recuperar clasificadores disponibles
        $.when(getOpinionClassifiers("${corpus.lang}"))
            .done(function(_classifiers) {
                classifiers = _classifiers;
                if (classifiers.length > 0)
                    renderClassifierForm(classifiers, 0);
                else
                    $(".classifiers").append(
                        $('<div>', {
                            class: "alert alert-warning",
                            role: "alert",
                            html: "Sin clasificadores disponibles para análisis de opinion."
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
                        if (comment.opinion === "Subjective")
                            return '<strong class="text-success">Sí</strong>';
                        else if (comment.opinion === "Objective")
                            return '<strong class="text-danger">No</strong>';
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

    // Listener para los botones de añadir/eliminar clasificador
    $('.classifiers').on('click', '.add-classifier', function () {
        renderClassifierForm(classifiers, index++);
    });
    $('.classifiers').on('click', '.remove-classifier', function () {
        $(this).closest('.classifier-item').remove();
    });

    // Listener para el botón de "volver"
    $('a#goBack').click(function (e) {
        e.preventDefault();
        $('#opinionForm').attr('action', "?action=back").submit();
    });

    // Listeners para los diferentes botones de enviar formulario
    $('#opinionForm button[type="submit"]').click(function (e) {
        e.preventDefault();
        if ($(this).attr("value") === "save-corpus") {
            $('#modal-saveCorpus').modal();
        }
        else if ($(this).attr("value") === "analyse") {
            showLoading("Realizando análisis de Opinión");
            $('#opinionForm').attr('action', "?action=analyse").submit();
        }
        else if ($(this).attr("value") === "next") {
            if ($('input[name="execute"]').is(':checked'))
                showLoading("Realizando análisis de Opinión");
            $('#opinionForm').attr('action', "?action=next").submit();
        }
    });
</script>

<sec:authorize access="isAuthenticated()">
    <%@include file="_modal_save.jsp"%>
</sec:authorize>

<%@ include file="_footer.jsp"%>