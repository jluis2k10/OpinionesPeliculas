<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../_header.jsp"%>
<h4 class="mb-3">
    <a href="${path}/corpora" id="goBack" title="Atrás"><i class="fas fa-chevron-left"></i></a> Ejecutar nuevo análisis de dominio<br/>
    <small class="text-muted">${corpus.name} </small>
    <c:if test="${not empty corpus.comments}">
        <small class="text-muted">
            <a href="#corpus-wrapper" data-toggle="collapse" aria-controls="corpus-wrapper" aria-expanded="false">[Mostrar/Ocultar]</a> ${corpus.comments.size()} comentarios, ${corpus.lang}
        </small>
    </c:if>
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
<form:form method="post" modelAttribute="domainForm" action="${path}/corpora/add-domain-analysis/${corpus.id}">
    <div class="list-group classifiers mb-5"></div>
    <div class="row">
        <div class="col-4">
            <button type="submit" class="btn btn-primary btn-lg btn-block" value="save-corpus"><i class="fas fa-save"></i> Guardar Cambios</button>
        </div>
        <div class="col-4">
            <button type="submit" class="btn btn-primary btn-lg btn-block" value="analyse"><i class="fas fa-cogs"></i> Ejecutar Análisis</button>
        </div>
    </div>
</form:form>

<%@ include file="../_js.jsp"%>

<link rel="stylesheet" href="${path}/webjars/datatables/1.10.16/css/dataTables.bootstrap4.min.css" />
<script type="text/javascript" src="${path}/webjars/datatables/1.10.16/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${path}/webjars/datatables/1.10.16/js/dataTables.bootstrap4.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    var classifiers;
    var corpus = ${corpus.toJson(true, false, false).toString()};
    $(document).ready(function () {
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
    });

    // Listeners para los diferentes botones de enviar formulario
    $('#domainForm button[type="submit"]').click(function (e) {
        e.preventDefault();
        if ($(this).attr("value") === "save-corpus") {
            $('#modal-saveCorpus').modal();
        }
        else if ($(this).attr("value") === "analyse") {
            showLoading("Realizando análisis de Dominio");
            $('#domainForm').submit();
        }
    });
</script>

<sec:authorize access="isAuthenticated()">
    <%@include file="../_modal_save.jsp"%>
</sec:authorize>

<%@ include file="../_footer.jsp"%>
