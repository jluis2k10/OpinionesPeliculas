<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="_header.jsp"%>
<h2>Mis búsquedas</h2>
<sec:authorize access="hasRole('ADMIN')">
    <ul class="nav nav-tabs">
        <li class="active"><a data-toggle="tab" href="#mysearches">Mis búsquedas</a></li>
        <li><a data-toggle="tab" href="#userssearches">Búsquedas de usuarios</a></li>
    </ul>
    <div class="tab-content">
        <div id="mysearches" class="tab-pane fade in active">
            <table id="my-searches" class="table table-striped table-bordered data-table" width="100%" cellpadding="0">
                <thead>
                <tr>
                    <th></th>
                    <th>ID</th>
                    <th>Término</th>
                    <th>Fuente</th>
                    <th>Idioma</th>
                    <th>Fecha</th>
                    <th>Coment.</th>
                    <th>Op.</th>
                </tr>
                </thead>
            </table>
        </div>
        <div id="userssearches" class="tab-pane fade">
            <table id="users-searches" class="table table-striped table-bordered data-table" width="100%" cellpadding="0">
                <thead>
                <tr>
                    <th></th>
                    <th>ID</th>
                    <th>Término</th>
                    <th>Fuente</th>
                    <th>Idioma</th>
                    <th>Fecha</th>
                    <th>Coment.</th>
                    <th>Usuario</th>
                    <th>Op.</th>
                </tr>
                </thead>
            </table>
        </div>
    </div>
</sec:authorize>
<sec:authorize access="!hasRole('ADMIN')">
    <div class="row">
        <c:choose>
            <c:when test="${mySearches.size() > 0}">
                <table id="my-searches" class="table table-striped table-bordered data-table" width="100%" cellpadding="0">
                    <thead>
                        <tr>
                            <th></th>
                            <th>ID</th>
                            <th>Término</th>
                            <th>Fuente</th>
                            <th>Idioma</th>
                            <th>Fecha</th>
                            <th>Coment.</th>
                            <th>Op.</th>
                        </tr>
                    </thead>
                </table>
            </c:when>
            <c:otherwise>
                <div class="col-xs-12">
                    <h3>No tienes búsquedas guardadas.</h3>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</sec:authorize>
<!-- Modal confirmación borrado -->
<div id="modal-confirm" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="alertdialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="confirmLabel">Advertencia</h4>
            </div>
            <div class="modal-body">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-danger delete-confirm">Borrar</button>
            </div>
        </div>
    </div>
</div>

<%@ include file="_js.jsp"%>

<link rel="stylesheet" href="${path}/css/dataTables.bootstrap.min.css" />
<script type="text/javascript" src="${path}/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="${path}/js/dataTables.bootstrap.min.js"></script>
<script type="text/javascript" src="${path}/js/custom.js"></script>

<script>
    $(document).ready(function() {
        /* Recuperar token csrf para incluirlo como cabecera en cada envío ajax */
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

        // Inicialización de modal con mensaje de confirmación de borrado
        $("#modal-confirm").modal({
            keyboard: true,
            show: false
        });

        // Localización de DataTables
        var tableLocale = datatablesLocalization();

        var tableMySearches = $("#my-searches").DataTable({
            language: tableLocale,
            ajax: {
                url: "api/searches",
                dataSrc: "searches"
            },
            columns: [
                {
                    className: "details-control",
                    orderable: false,
                    data: null,
                    defaultContent: "",
                    "render": function () {
                        return '<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>';
                    }
                },
                {data: "id"},
                {data: "term"},
                {data: "source"},
                {data: "lang"},
                {data: "created"},
                {data: "total_comments"},
                {
                    data: null,
                    defaultContent: "",
                    orderable: false,
                    "render": function(search) {
                        return render_search_options(search.id, true);
                    }
                }
            ],
            "order": [[1, 'asc']]
        });

        var tableUsersSearches = $("#users-searches").DataTable({
            language: tableLocale,
            ajax: {
                url: "api/searches",
                dataSrc: "users_searches"
            },
            columns: [
                {
                    className: "details-control",
                    orderable: false,
                    data: null,
                    defaultContent: "",
                    "render": function () {
                        return '<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>';
                    }
                },
                {data: "id"},
                {data: "term"},
                {data: "source"},
                {data: "lang"},
                {data: "created"},
                {data: "total_comments"},
                {data: "owner"},
                {
                    data: null,
                    defaultContent: "",
                    orderable: false,
                    "render": function(search) {
                        return render_search_options(search.id, false);
                    }
                }
            ],
            "order": [[1, 'asc']]
        });

        // Listener para abrir/cerrar detalles en la tabla
        $(".data-table tbody").on("click", "td.details-control", function () {
            var tr = $(this).closest("tr");
            var span = tr.find("span.glyphicon");

            var datatableRow = tableMySearches.row(tr);
            if (tr.closest("table").get(0).id === "users-searches")
                datatableRow = tableUsersSearches.row(tr);

            if (datatableRow.child.isShown()) {
                datatableRow.child.hide();
                tr.removeClass("shown");
                span.first().removeClass("glyphicon-minus-sign");
                span.first().addClass("glyphicon-plus-sign");
            } else {
                datatableRow.child(render_details(datatableRow.data())).show();
                tr.addClass("shown");
                span.first().removeClass("glyphicon-plus-sign");
                span.first().addClass("glyphicon-minus-sign");

            }
        });

        /* Acción al hacer click en el botón de borrar búsqueda */
        $(".data-table tbody").on("click", "button.delete-search", function (e) {
            var modal = $('#modal-confirm').modal('toggle');
            $deleteBtn = $(e.target);
            $row = $deleteBtn.closest("tr");
            $row.addClass("selected");
            $searchID = $row.find("td").eq(1).html();
            $searchTerm = $row.find("td").eq(2).html();
            $searchSource = $row.find("td").eq(3).html();
            modal.find(".modal-body").html("<p>¿Desea borrar la búsqueda <strong>" + $searchID +
                " (" + $searchTerm + " - " + $searchSource + ")</strong> ? No hay vuelta atrás.</p>");
            modal.find(".delete-confirm").data({
                "searchid": $searchID,
                "searchTerm": $searchTerm
            });
        });

        /* Acción al hacer click en el botón de confirmación de borrar modelo en el modal */
        $(".delete-confirm").click(function (e) {
            var modal = $('#modal-confirm');
            $deleteBtn = $(e.target);

            var table = tableMySearches;
            var selectedRow = $(".data-table tbody").find("tr.selected");
            if (selectedRow.closest("table").get(0).id === "users-searches")
                table = tableUsersSearches;

            $.when(deleteSearch($deleteBtn.data("searchid")))
                .always(function () {
                    modal.modal('toggle');
                })
                .done(function () {
                    table.row(".selected").remove().draw(false);
                    alertMsg("success", "Búsqueda <strong>" + $deleteBtn.data("searchid") + " (" +  $deleteBtn.data("searchTerm") + ")</strong> eliminado correctamente.");
                })
                .fail(function () {
                    alertMsg("danger", "No se ha podido eliminar la búsqueda <strong>" + $deleteBtn.data("searchid") + " (" +  $deleteBtn.data("searchTerm") + ")</strong>.");
                })
        });

        /* Acción al esconderse el modal */
        $("#modal-confirm").on('hidden.bs.modal', function (e) {
            $selectedRow = $(".data-table tbody").find("tr.selected");
            $selectedRow.removeClass("selected");
        });

    });

    function render_details(data) {
        return '<table class="table table-responsive" cellpadding="4" cellspacing="0" border="0" style="padding-left: 50px;">' +
                '<tr>' +
                    '<td width="300">Creado</td>' +
                    '<td>' + data.created + '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td>Actualizado</td>' +
                    '<td>' + (data.updated ? data.updated : '-') + '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td>Clase para Fuente de Comentarios</td>' +
                    '<td>' + data.source_class + '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td>Clase para Análisis de Sentimiento</td>' +
                    '<td>' + data.sentiment_adapter+ '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td>Modelo para Análisis de Sentimiento</td>' +
                    '<td>' + (data.sentiment_model ? data.sentiment_model : '-') + '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td>Análisis de Subjetividad</td>' +
                    '<td>' + data.subjectivity + '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td>Clase para Análisis de Subjetividad</td>' +
                    '<td>' + (data.subjectivity_adapter ? data.subjectivity_adapter : '-') + '</td>' +
                '</tr>' +
                '<tr>' +
                    '<td>Modelo para Análisis de Subjetividad</td>' +
                    '<td>' + (data.subjectivity_model ? data.subjectivity_model : '-') + '</td>' +
                '</tr>' +
               '</table>';
    }

    function render_search_options(searchID, mysearch) {
        var updateButton = "";
        if (mysearch) {
            updateButton = '<a href="/searches/update/' + searchID + '" class="btn btn-default btn-xs" type="button" title="Actualizar">' +
                                    '<span class="glyphicon glyphicon-repeat"></span>' +
                               '</a>';
        }
        return '<div class="btn-group btn-group-xs" role="group">' +
                    updateButton +
                    '<a href="/searches/' + searchID + '" class="btn btn-default btn-xs" type="button" title="Ver resultados">' +
                        '<span class="glyphicon glyphicon-search"></span>' +
                    '</a>' +
                    '<button class="btn btn-danger btn-xs delete-search" type="button" title="Eliminar">' +
                        '<span class="glyphicon glyphicon-trash"></span>' +
                    '</button>' +
               '</div>';
    }

    /* Petición AJAX POST para eliminar un modelo del usuario */
    function deleteSearch(id) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: "/searches/delete",
            data: JSON.stringify(id),
            timeout: 5000
        }));
    }

</script>

<%@ include file="_footer.jsp"%>