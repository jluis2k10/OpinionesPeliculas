<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="_header.jsp"%>
<h2>Mis modelos de análisis</h2>
<ul class="nav nav-tabs">
    <li class="active"><a data-toggle="tab" href="#usentiment">Sentimiento</a></li>
    <li><a data-toggle="tab" href="#usubjectivity">Subjetividad</a></li>
    <sec:authorize access="hasRole('ADMIN')">
        <li><a data-toggle="tab" href="#adminsentiment">Todos Sentimiento</a></li>
        <li><a data-toggle="tab" href="#adminsubjectivity">Todos Subjetividad</a></li>
    </sec:authorize>
</ul>
<div class="tab-content">
    <div id="usentiment" class="tab-pane fade in active">
        <c:choose>
            <c:when test="${not empty sentimentModels}">
                <table id="userSentimentModels" class="table table-striped table-bordered data-table" width="100%" cellpadding="0">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Idioma</th>
                        <th>Clase del Adaptador</th>
                        <th>Opciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="model" items="${sentimentModels}">
                        <tr>
                            <td>${model.id}</td>
                            <td>${model.name}</td>
                            <td>${model.language}</td>
                            <td>${model.adapterClass}</td>
                            <td>
                                <div class="btn-group btn-group-xs">
                                    <c:if test="${model.trainable}">
                                        <a href="${path}/models/train/${model.id}" class="btn btn-default btn-xs" title="Entrenar">
                                            <span class="glyphicon glyphicon-cog"></span>
                                        </a>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${model.open}">
                                            <button type="button" class="btn btn-default isopen btn-xs" title="Hacer privado" data-modelid="${model.id}">
                                                <span class="glyphicon glyphicon-eye-open"></span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-default isopen btn-xs" title="Hacer público"  data-modelid="${model.id}">
                                                <span class="glyphicon glyphicon-eye-close"></span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    </button>
                                    <button type="button" class="btn btn-danger delete-model btn-xs" title="Eliminar" data-modelid="${model.id}">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <h3>Sin modelos para análisis de sentimiento</h3>
            </c:otherwise>
        </c:choose>
    </div>
    <div id="usubjectivity" class="tab-pane fade">
        <c:choose>
            <c:when test="${not empty subjectivityModels}">
                <table id="userSubjectivityModels" class="table table-striped table-bordered data-table" width="100%" cellpadding="0">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Idioma</th>
                        <th>Clase del Adaptador</th>
                        <th>Opciones</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="model" items="${subjectivityModels}">
                        <tr>
                            <td>${model.id}</td>
                            <td>${model.name}</td>
                            <td>${model.language}</td>
                            <td>${model.adapterClass}</td>
                            <td>
                                <div class="btn-group btn-group-xs">
                                    <c:if test="${model.trainable}">
                                        <a href="${path}/models/train/${model.id}" class="btn btn-default btn-xs" title="Entrenar">
                                            <span class="glyphicon glyphicon-cog"></span>
                                        </a>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${model.open}">
                                            <button type="button" class="btn btn-default isopen btn-xs" title="Hacer privado" data-modelid="${model.id}">
                                                <span class="glyphicon glyphicon-eye-open"></span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-default isopen btn-xs" title="Hacer público"  data-modelid="${model.id}">
                                                <span class="glyphicon glyphicon-eye-close"></span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    </button>
                                    <button type="button" class="btn btn-danger delete-model btn-xs" title="Eliminar" data-modelid="${model.id}">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <h3>Sin modelos para análisis de subjetividad</h3>
            </c:otherwise>
        </c:choose>
    </div>
    <sec:authorize access="hasRole('ADMIN')">
        <div id="adminsentiment" class="tab-pane fade">
            <c:choose>
                <c:when test="${not empty allSentimentModels}">
                    <table id="allSentimentModels" class="table table-striped table-bordered data-table" width="100%" cellpadding="0">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Idioma</th>
                            <th>Clase del Adaptador</th>
                            <th>Usuario</th>
                            <th>Opciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="model" items="${allSentimentModels}">
                            <tr>
                                <td>${model.id}</td>
                                <td>${model.name}</td>
                                <td>${model.language}</td>
                                <td>${model.adapterClass}</td>
                                <td>${model.owner.userName}</td>
                                <td><div class="btn-group btn-group-xs">
                                    <c:if test="${model.trainable}">
                                        <a href="${path}/models/train/${model.id}" class="btn btn-default btn-xs" title="Entrenar">
                                            <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
                                        </a>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${model.open}">
                                            <button type="button" class="btn btn-default isopen btn-xs" title="Hacer privado" data-modelid="${model.id}">
                                                <span class="glyphicon glyphicon-eye-open"></span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-default isopen btn-xs" title="Hacer público"  data-modelid="${model.id}">
                                                <span class="glyphicon glyphicon-eye-close"></span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    </button>
                                    <button type="button" class="btn btn-danger delete-model btn-xs" title="Eliminar" data-modelid="${model.id}">
                                        <span class="glyphicon glyphicon-trash"></span>
                                    </button>
                                </div></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <h3>Sin modelos para análisis de sentimiento</h3>
                </c:otherwise>
            </c:choose>
        </div>
        <div id="adminsubjectivity" class="tab-pane fade">
            <c:choose>
                <c:when test="${not empty allSubjectivityModels}">
                    <table id="allSubjectivityModels" class="table table-striped table-bordered data-table" width="100%" cellpadding="0">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Idioma</th>
                            <th>Clase del Adaptador</th>
                            <th>Usuario</th>
                            <th>Opciones</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="model" items="${allSubjectivityModels}">
                            <tr>
                                <td>${model.id}</td>
                                <td>${model.name}</td>
                                <td>${model.language}</td>
                                <td>${model.adapterClass}</td>
                                <td>${model.owner.userName}</td>
                                <td>
                                    <div class="btn-group btn-group-xs">
                                        <c:if test="${model.trainable}">
                                            <a href="${path}/models/train/${model.id}" class="btn btn-default btn-xs" title="Entrenar">
                                                <span class="glyphicon glyphicon-cog"></span>
                                            </a>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${model.open}">
                                                <button type="button" class="btn btn-default isopen btn-xs" title="Hacer privado" data-modelid="${model.id}">
                                                    <span class="glyphicon glyphicon-eye-open"></span>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="btn btn-default isopen btn-xs" title="Hacer público" data-modelid="${model.id}">
                                                    <span class="glyphicon glyphicon-eye-close"></span>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                        </button>
                                        <button type="button" class="btn btn-danger delete-model btn-xs" title="Eliminar" data-modelid="${model.id}">
                                            <span class="glyphicon glyphicon-trash"></span>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <h3>Sin modelos para análisis de subjetividad</h3>
                </c:otherwise>
            </c:choose>
        </div>
    </sec:authorize>
</div>
<!-- Modal confirmación borrado -->
<div id="modal-confirm" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="alertdialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="confirmLabel">Advertencia</h4>
            </div>
            <div class="modal-body">
                <p>¿Desea borrar el modelo <strong><strong>? No hay vuelta atrás.</p>
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
<script type="text/javascript" src="${path}/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${path}/js/dataTables.bootstrap.min.js"></script>
<script type="text/javascript" src="${path}/js/custom.js"></script>

<script>
    var table = null;
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

        // Inicializar datatables
        var localized = datatablesLocalization();
        table = $('.data-table').DataTable({
            language: localized
        });

        /* Acción al esconderse el modal */
        $("#modal-confirm").on('hidden.bs.modal', function (e) {
            $selectedRow = $(".data-table tbody").find("tr.selected");
            $selectedRow.removeClass("selected");
        });
    });

    // Acción al hacer click en los botones para hacer el modelo público/privado
    $(".isopen").click(function (e) {
        $button = $(e.target);
        $span = $button.find("span");
        $spanclass = $span.attr("class");

        $button.prop("disabled", true);
        $span.removeClass().addClass("glyphicon glyphicon-refresh");

        $.when(switchOpen($button.data("modelid")))
            .always(function () {
                $button.prop("disabled", false);
            })
            .done(function () {
                if ($spanclass === "glyphicon glyphicon-eye-open") {
                    $span.removeClass().addClass("glyphicon glyphicon-eye-close");
                    $button.prop("title", "Hacer público");
                } else {
                    $span.removeClass().addClass("glyphicon glyphicon-eye-open");
                    $button.prop("title", "Hacer privado");
                }
            })
            .fail(function () {
                $span.removeClass().addClass($spanclass);
                alertMsg("danger", "No se ha podido cambiar el estado del modelo indicado.");
            });
    });

    /* Acción al hacer click en el botón de borrar modelo de en el listado de modelos de usuario */
    $(".delete-model").click(function (e) {
        var modal = $('#modal-confirm').modal('toggle');
        $deleteBtn = $(e.target);
        $row = $deleteBtn.closest("tr");
        $row.addClass("selected");
        $modelName = $row.find("td").eq(1).html();
        modal.find(".modal-body").html("<p>¿Desea borrar el modelo <strong>" + $modelName + "</strong>? No hay vuelta atrás.</p>");
        modal.find(".delete-confirm").data({
            "modelid": $deleteBtn.data("modelid"),
            "modelname": $modelName
        });
    });

    /* Acción al hacer click en el botón de confirmación de borrar modelo en el modal */
    $(".delete-confirm").click(function (e) {
        var modal = $('#modal-confirm');
        $deleteBtn = $(e.target);
        $.when(deleteModel($deleteBtn.data("modelid")))
            .always(function () {
                modal.modal('toggle');
            })
            .done(function () {
                table.row(".selected").remove().draw(false);
                alertMsg("success", "Modelo <strong>" + $deleteBtn.data("modelname") + "</strong> eliminado correctamente.");
            })
            .fail(function () {
                alertMsg("danger", "No se ha podido eliminar el modelo <strong>" + $deleteBtn.data("modelname") + "</strong>.");
            })
    });

    /* Petición AJAX POST para cambiar el estado (público/privado) de un modelo */
    function switchOpen(id) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: "/models/switchModelOpen",
            data: JSON.stringify(id),
            timeout: 5000
        }));
    }

    /* Petición AJAX POST para eliminar un modelo del usuario */
    function deleteModel(id) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: "models/deleteModel",
            data: JSON.stringify(id),
            timeout: 5000
        }));
    }

</script>

<%@ include file="_footer.jsp"%>