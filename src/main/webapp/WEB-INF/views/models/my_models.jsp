<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ include file="../_header.jsp"%>
<h2>Mis modelos de análisis</h2>
<div class="card">
    <div class="card-header">
        <ul class="nav nav-tabs card-header-tabs" role="tablist">
            <li class="nav-item"><a class="nav-link active" data-toggle="tab" href="#usentiment">Sentimiento</a></li>
            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#uopinion">Opinión</a></li>
            <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#udomain">Dominio</a></li>
            <sec:authorize access="hasRole('ADMIN')">
                <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#adminsentiment">Sentimiento (resto)</a></li>
                <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#adminopinion">Opinión (resto)</a></li>
                <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#admindomain">Dominio (resto)</a></li>
            </sec:authorize>
        </ul>
    </div>
    <div class="card-body">
        <div class="tab-content">
            <div id="usentiment" class="tab-pane fade show active">
                <c:choose>
                    <c:when test="${not empty polarityModels}">
                        <table id="userSentimentModels" class="table table-striped table-bordered table-sm data-table" width="100%" cellpadding="0">
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
                            <c:forEach var="model" items="${polarityModels}">
                                <tr>
                                    <td>${model.id}</td>
                                    <td>${model.name}</td>
                                    <td>${model.language}</td>
                                    <td>${model.adapterClass}</td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <c:if test="${model.trainable}">
                                                <a href="${path}/models/train/${model.id}" class="btn btn-secondary btn-sm" title="Entrenar">
                                                    <i class="fas fa-sliders-h"></i>
                                                </a>
                                            </c:if>
                                            <c:choose>
                                                <c:when test="${model.isPublic}">
                                                    <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer privado" data-modelid="${model.id}">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer público"  data-modelid="${model.id}">
                                                        <i class="fas fa-eye-slash"></i>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                            </button>
                                            <button type="button" class="btn btn-danger btn-sm delete-model" title="Eliminar" data-modelid="${model.id}">
                                                <i class="far fa-trash-alt"></i>
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
            <div id="uopinion" class="tab-pane fade">
                <c:choose>
                    <c:when test="${not empty opinionModels}">
                        <table id="userSubjectivityModels" class="table table-striped table-bordered table-sm data-table" width="100%" cellpadding="0">
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
                            <c:forEach var="model" items="${opinionModels}">
                                <tr>
                                    <td>${model.id}</td>
                                    <td>${model.name}</td>
                                    <td>${model.language}</td>
                                    <td>${model.adapterClass}</td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <c:if test="${model.trainable}">
                                                <a href="${path}/models/train/${model.id}" class="btn btn-secondary btn-sm" title="Entrenar">
                                                    <i class="fas fa-sliders-h"></i>
                                                </a>
                                            </c:if>
                                            <c:choose>
                                                <c:when test="${model.isPublic}">
                                                    <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer privado" data-modelid="${model.id}">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer público"  data-modelid="${model.id}">
                                                        <i class="fas fa-eye-slash"></i>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                            </button>
                                            <button type="button" class="btn btn-danger btn-sm delete-model" title="Eliminar" data-modelid="${model.id}">
                                                <i class="far fa-trash-alt"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <h3>Sin modelos para análisis de opinión</h3>
                    </c:otherwise>
                </c:choose>
            </div>
            <div id="udomain" class="tab-pane fade">
                <c:choose>
                    <c:when test="${not empty domainModels}">
                        <table id="userDomainModels" class="table table-striped table-bordered table-sm data-table" width="100%" cellpadding="0">
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
                            <c:forEach var="model" items="${domainModels}">
                                <tr>
                                    <td>${model.id}</td>
                                    <td>${model.name}</td>
                                    <td>${model.language}</td>
                                    <td>${model.adapterClass}</td>
                                    <td>
                                        <div class="btn-group btn-group-sm" role="group">
                                            <c:if test="${model.trainable}">
                                                <a href="${path}/models/train/${model.id}" class="btn btn-secondary btn-sm" title="Entrenar">
                                                    <i class="fas fa-sliders-h"></i>
                                                </a>
                                            </c:if>
                                            <c:choose>
                                                <c:when test="${model.isPublic}">
                                                    <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer privado" data-modelid="${model.id}">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer público"  data-modelid="${model.id}">
                                                        <i class="fas fa-eye-slash"></i>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                            </button>
                                            <button type="button" class="btn btn-danger btn-sm delete-model" title="Eliminar" data-modelid="${model.id}">
                                                <i class="far fa-trash-alt"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <h3>Sin modelos para análisis de dominio</h3>
                    </c:otherwise>
                </c:choose>
            </div>
            <sec:authorize access="hasRole('ADMIN')">
                <div id="adminsentiment" class="tab-pane fade">
                    <c:choose>
                        <c:when test="${not empty allPolarityModels}">
                            <table id="allSentimentModels" class="table table-striped table-bordered table-sm data-table" width="100%" cellpadding="0">
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
                                <c:forEach var="model" items="${allPolarityModels}">
                                    <tr>
                                        <td>${model.id}</td>
                                        <td>${model.name}</td>
                                        <td>${model.language}</td>
                                        <td>${model.adapterClass}</td>
                                        <td>${model.owner.userName}</td>
                                        <td>
                                            <div class="btn-group btn-group-sm" role="group">
                                                <c:if test="${model.trainable}">
                                                    <a href="${path}/models/train/${model.id}" class="btn btn-secondary btn-sm" title="Entrenar">
                                                        <i class="fas fa-sliders-h"></i>
                                                    </a>
                                                </c:if>
                                                <c:choose>
                                                    <c:when test="${model.isPublic}">
                                                        <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer privado" data-modelid="${model.id}">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer público"  data-modelid="${model.id}">
                                                            <i class="fas fa-eye-slash"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                                </button>
                                                <button type="button" class="btn btn-danger btn-sm delete-model" title="Eliminar" data-modelid="${model.id}">
                                                    <i class="far fa-trash-alt"></i>
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
                <div id="adminopinion" class="tab-pane fade">
                    <c:choose>
                        <c:when test="${not empty allOpinionModels}">
                            <table id="allSubjectivityModels" class="table table-striped table-bordered table-sm data-table" width="100%" cellpadding="0">
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
                                <c:forEach var="model" items="${allOpinionModels}">
                                    <tr>
                                        <td>${model.id}</td>
                                        <td>${model.name}</td>
                                        <td>${model.language}</td>
                                        <td>${model.adapterClass}</td>
                                        <td>${model.owner.userName}</td>
                                        <td>
                                            <div class="btn-group btn-group-sm" role="group">
                                                <c:if test="${model.trainable}">
                                                    <a href="${path}/models/train/${model.id}" class="btn btn-secondary btn-sm" title="Entrenar">
                                                        <i class="fas fa-sliders-h"></i>
                                                    </a>
                                                </c:if>
                                                <c:choose>
                                                    <c:when test="${model.isPublic}">
                                                        <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer privado" data-modelid="${model.id}">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer público"  data-modelid="${model.id}">
                                                            <i class="fas fa-eye-slash"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                                </button>
                                                <button type="button" class="btn btn-danger btn-sm delete-model" title="Eliminar" data-modelid="${model.id}">
                                                    <i class="far fa-trash-alt"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <h3>Sin modelos para análisis de opinión</h3>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div id="admindomain" class="tab-pane fade">
                    <c:choose>
                        <c:when test="${not empty allDomainModels}">
                            <table id="allDomainModels" class="table table-striped table-bordered table-sm data-table" width="100%" cellpadding="0">
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
                                <c:forEach var="model" items="${allDomainModels}">
                                    <tr>
                                        <td>${model.id}</td>
                                        <td>${model.name}</td>
                                        <td>${model.language}</td>
                                        <td>${model.adapterClass}</td>
                                        <td>${model.owner.userName}</td>
                                        <td>
                                            <div class="btn-group btn-group-sm" role="group">
                                                <c:if test="${model.trainable}">
                                                    <a href="${path}/models/train/${model.id}" class="btn btn-secondary btn-sm" title="Entrenar">
                                                        <i class="fas fa-sliders-h"></i>
                                                    </a>
                                                </c:if>
                                                <c:choose>
                                                    <c:when test="${model.isPublic}">
                                                        <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer privado" data-modelid="${model.id}">
                                                            <i class="fas fa-eye"></i>
                                                        </button>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <button type="button" class="btn btn-secondary btn-sm isopen" title="Hacer público"  data-modelid="${model.id}">
                                                            <i class="fas fa-eye-slash"></i>
                                                        </button>
                                                    </c:otherwise>
                                                </c:choose>
                                                </button>
                                                <button type="button" class="btn btn-danger btn-sm delete-model" title="Eliminar" data-modelid="${model.id}">
                                                    <i class="far fa-trash-alt"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <h3>Sin modelos para análisis de dominio</h3>
                        </c:otherwise>
                    </c:choose>
                </div>
            </sec:authorize>
        </div>
    </div>
</div>
<!-- Modal confirmación borrado -->
<div id="modal-confirm" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="alertdialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmLabel">Advertencia</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p>¿Desea borrar el modelo <strong></strong>? No hay vuelta atrás.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-danger delete-confirm">Borrar</button>
            </div>
        </div>
    </div>
</div>

<%@ include file="../_js.jsp"%>

<link rel="stylesheet" href="${path}/webjars/datatables/1.10.16/css/dataTables.bootstrap4.min.css" />
<script type="text/javascript" src="${path}/webjars/datatables/1.10.16/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${path}/webjars/datatables/1.10.16/js/dataTables.bootstrap4.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    var table = null;
    $(document).ready(function() {
        // Inicialización de modal con mensaje de confirmación de borrado
        $("#modal-confirm").modal({
            keyboard: true,
            show: false
        });

        // Inicializar datatables
        table = $('.data-table').DataTable({
            language: datatablesLocalization()
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
        $button.prop("disabled", true);
        $.when(switchOpen($button.data("modelid")))
            .always(function () {
                $button.prop("disabled", false);
            })
            .done(function () {
                if ($button.find("svg").hasClass("fa-eye")) {
                    $button.empty();
                    $button.html("<i class='fas fa-eye-slash'></i>");
                    $button.prop("title", "Hacer privado");
                } else {
                    $button.empty();
                    $button.html("<i class='fas fa-eye'></i>");
                    $button.prop("title", "Hacer público");
                }
            })
            .fail(function () {
                showFlashMessage("danger", "No se ha podido cambiar el estado del modelo indicado.");
            });
    });

    /* Acción al hacer click en el botón de borrar modelo de en el listado de modelos de usuario */
    $(".delete-model").click(function (e) {
        // Info sobre el modelo a eliminar
        $deleteBtn = $(e.target);
        $row = $deleteBtn.closest("tr");
        $modelID = $deleteBtn.data("modelid");
        $modelName = $row.find("td").eq(1).html();

        // Deshabilitamos todos los botones para borrar modelos
        $(".delete-model").prop("disabled", true);

        // Marcamos la fila de la tabla que contiene el modelo seleccionado
        $row.addClass("selected");

        // Recuperar cuántos análisis utilizan el modelo seleccionado y lanzar el modal de confirmación
        $.when(getTotalAnalysis($deleteBtn.data("modelid")))
            .done(function (totalAnalysis) {
                showModalConfirm($modelID, $modelName, parseInt(totalAnalysis));
            })
            .fail(function () {
                showModalConfirm($modelID, $modelName, -1);
            })
    });

    // Mostrar modal pidiendo confirmación de borrado de modelo de lenguaje
    function showModalConfirm(modelID, modelName, totalAnalysis) {
        var modal = $('#modal-confirm').modal('toggle');
        modal.find(".delete-confirm").data({
            "modelid": modelID,
            "modelname": modelName
        });
        var message = "";
        if (totalAnalysis < 0) {
            message = "<p><strong class='text-danger'>ATENCIÓN:</strong> Ocurrió un error recuperando el número de " +
                "análisis que utilizan este modelo.</p>";
        }
        else {
            message = "<p><strong>Este modelo se utiliza en " + totalAnalysis + " análisis.</strong> Al borrarlo se eliminarán también " +
                "todos estos análisis.</p>";
        }
        message += "<p>¿Está seguro de que desea borrar el modelo <strong>" + modelName + "</strong>? No hay vuelta atrás.</p>";
        modal.find(".modal-body").html(message);
    }

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
                showFlashMessage("success", "Modelo <strong>" + $deleteBtn.data("modelname") + "</strong> eliminado correctamente.");
            })
            .fail(function () {
                $('tr.selected').removeClass('selected');
                showFlashMessage("danger", "No se ha podido eliminar el modelo <strong>" + $deleteBtn.data("modelname") + "</strong>.");
            })
    });

    // Acción al ocultar modal de confirmación de borrado (reactivar botones y desmarcar fila)
    $("#modal-confirm").on("hidden.bs.modal", function (e) {
        $(".delete-model").prop("disabled", false);
        $('tr.selected').removeClass('selected');
    });

    /* Petición AJAX POST para cambiar el estado (público/privado) de un modelo */
    function switchOpen(id) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: ctx + "/models/switchModelOpen",
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
            url: ctx + "/models/deleteModel",
            data: JSON.stringify(id),
            timeout: 5000
        }));
    }

    function getTotalAnalysis(modelId) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: ctx + "/models/countAnalysis",
            data: JSON.stringify(modelId),
            timeout: 5000
        }));
    }

</script>

<%@ include file="../_footer.jsp"%>