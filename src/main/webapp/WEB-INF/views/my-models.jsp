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
                            <th>${model.id}</th>
                            <th>${model.name}</th>
                            <th>${model.language}</th>
                            <th>${model.adapterClass}</th>
                            <th>
                                <div class="btn-group">
                                    <c:if test="${model.trainable}">
                                        <a href="${path}/models/train/${model.id}" class="btn btn-default" aria-label="Left Align" title="Entrenar">
                                            <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
                                        </a>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${model.open}">
                                            <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer privado">
                                                <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer público">
                                                <span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    </button>
                                    <button type="button" class="btn btn-danger" aria-label="Left Align" title="Eliminar">
                                        <span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>
                                    </button>
                                </div>
                            </th>
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
                            <th>${model.id}</th>
                            <th>${model.name}</th>
                            <th>${model.language}</th>
                            <th>${model.adapterClass}</th>
                            <th>
                                <div class="btn-group">
                                    <c:if test="${model.trainable}">
                                        <a href="${path}/models/train/${model.id}" class="btn btn-default" aria-label="Left Align" title="Entrenar">
                                            <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
                                        </a>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${model.open}">
                                            <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer privado">
                                                <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer público">
                                                <span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    </button>
                                    <button type="button" class="btn btn-danger" aria-label="Left Align" title="Eliminar">
                                        <span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>
                                    </button>
                                </div>
                            </th>
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
                                <th>${model.id}</th>
                                <th>${model.name}</th>
                                <th>${model.language}</th>
                                <th>${model.adapterClass}</th>
                                <th>${model.owner.userName}</th>
                                <th><div class="btn-group">
                                    <c:if test="${model.trainable}">
                                        <a href="${path}/models/train/${model.id}" class="btn btn-default" aria-label="Left Align" title="Entrenar">
                                            <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
                                        </a>
                                    </c:if>
                                    <c:choose>
                                        <c:when test="${model.open}">
                                            <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer privado">
                                                <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer público">
                                                <span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                    </button>
                                    <button type="button" class="btn btn-danger" aria-label="Left Align" title="Eliminar">
                                        <span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>
                                    </button>
                                </div></th>
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
                                <th>${model.id}</th>
                                <th>${model.name}</th>
                                <th>${model.language}</th>
                                <th>${model.adapterClass}</th>
                                <th>${model.owner.userName}</th>
                                <th>
                                    <div class="btn-group">
                                        <c:if test="${model.trainable}">
                                            <a href="${path}/models/train/${model.id}" class="btn btn-default" aria-label="Left Align" title="Entrenar">
                                                <span class="glyphicon glyphicon-cog" aria-hidden="true"></span>
                                            </a>
                                        </c:if>
                                        <c:choose>
                                            <c:when test="${model.open}">
                                                <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer privado">
                                                    <span class="glyphicon glyphicon-eye-open" aria-hidden="true"></span>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button type="button" class="btn btn-default" aria-label="Left Align" title="Hacer público">
                                                    <span class="glyphicon glyphicon-eye-close" aria-hidden="true"></span>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                        </button>
                                        <button type="button" class="btn btn-danger" aria-label="Left Align" title="Eliminar">
                                            <span class="glyphicon glyphicon-remove-circle" aria-hidden="true"></span>
                                        </button>
                                    </div>
                                </th>
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

<%@ include file="_js.jsp"%>

<link rel="stylesheet" href="${path}/css/dataTables.bootstrap.min.css" />
<script type="text/javascript" src="${path}/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${path}/js/dataTables.bootstrap.min.js"></script>

<script>
    $(document).ready(function() {
        $('.data-table').DataTable({
            language: {
                "sProcessing":     "Procesando...",
                "sLengthMenu":     "Mostrar _MENU_ entradas",
                "sZeroRecords":    "No se encontraron resultados",
                "sEmptyTable":     "Ningún dato disponible en esta tabla",
                "sInfo":           "Mostrando entradas _START_ a _END_ de un total de _TOTAL_ entradas",
                "sInfoEmpty":      "Mostrando 0 entradas",
                "sInfoFiltered":   "(filtrado de un total de _MAX_ entradas)",
                "sInfoPostFix":    "",
                "sSearch":         "Buscar:",
                "sUrl":            "",
                "sInfoThousands":  ",",
                "sLoadingRecords": "Cargando...",
                "oPaginate": {
                    "sFirst":    "Primero",
                    "sLast":     "Último",
                    "sNext":     "Siguiente",
                    "sPrevious": "Anterior"
                },
                "oAria": {
                    "sSortAscending":  ": Activar para ordenar la columna de manera ascendente",
                    "sSortDescending": ": Activar para ordenar la columna de manera descendente"
                }
            }
        });
    });
</script>

<%@ include file="_footer.jsp"%>