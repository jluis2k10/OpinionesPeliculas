<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="_header.jsp"%>

<h3>Actualizando búsqueda <strong>${search.id}</strong></h3>
<h5 class="mb-3 text-muted">
    <c:choose>
        <c:when test="${empty search.title}">
            <em>${search.term}</em> en ${search.source}
        </c:when>
        <c:otherwise>
            ${search.term} en ${search.source}: <em>${search.title}</em>
        </c:otherwise>
    </c:choose>
</h5>
<form:form method="post" modelAttribute="search">
    <!-- Grupo para Opciones de Fuente de Comentarios -->
    <div class="card mb-4 border-secondary bg-light">
        <div class="card-body">
            <h5 class="card-title">Opciones de actualización</h5>
            <div class="row">
                <spring:bind path="limit">
                    <div class="col-3 limit-container" style="display: none;">
                        <div class="form-group ${status.error ? "has-error" : ""}">
                            <form:label path="limit">Comentarios a recuperar (máx.)</form:label>
                            <form:input path="limit" type="number" min="1" cssClass="form-control" id="limit" aria-describedby="errorsLimit"></form:input>
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
            </div>
        </div>
    </div>
    <div class="row mb-4">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-lg btn-block">Actualizar</button>
        </div>
    </div>
    <form:hidden path="id" />
</form:form>
<div class="card mb-4 text-white bg-info">
    <div class="card-header">
        <strong>Parámetros de la búsqueda</strong>
    </div>
    <div class="card-body">
        <dl class="row mb-4">
            <dt class="col-3">Idioma:</dt>
            <dd class="col-9">${search.lang}</dd>
            <dt class="col-3">Comentarios recuperados:</dt>
            <dd class="col-9">${search.comments.size()}</dd>
            <dt class="col-3">Creada:</dt>
            <dd class="col-9"><fmt:formatDate value="${search.created}" pattern="dd/MM/yyyy HH:mm" /></dd>
            <c:if test="${!empty search.updated}">
                <dt class="col-3">Última actualización:</dt>
                <dd class="col-9"><fmt:formatDate value="${search.updated}" pattern="dd/MM/yyyy HH:mm" /></dd>
            </c:if>
        </dl>
        <dl class="row mb-4">
            <dt class="col-3">Fuente:</dt>
            <dd class="col-9">${search.source}</dd>
            <dt class="col-3">Clase del adaptador:</dt>
            <dd class="col-9"><samp>${search.sourceClass}</samp></dd>
            <dt class="col-3">Limpiar comentarios:</dt>
            <dd class="col-9">
                <c:choose>
                    <c:when test="${search.cleanTweet}">Sí</c:when>
                    <c:otherwise>No</c:otherwise>
                </c:choose>
            </dd>
            <dt class="col-3">Eliminar <em>stop-words</em>:</dt>
            <dd class="col-9">
                <c:choose>
                    <c:when test="${search.delStopWords}">Sí</c:when>
                    <c:otherwise>No</c:otherwise>
                </c:choose>
            </dd>
            <c:if test="${search.sourceExtraParams.size() > 0}">
                <dt class="col-3">Parámetros extra:</dt>
                <dd class="col-9 mb-0">
                    <dl class="row mb-0">
                        <c:forEach var="parameter" items="${search.sourceExtraParams}">
                            <dt class="col-3">${parameter.key.toString()}:</dt>
                            <dd class="col-9">${parameter.value.toString()}</dd>
                        </c:forEach>
                    </dl>
                </dd>
            </c:if>
        </dl>
        <dl class="row mb-4">
            <dt class="col-3">Análisis de sentimiento:</dt>
            <dd class="col-9"><samp>${search.sentimentAdapter}</samp></dd>
            <c:if test="${search.sentimentModel != null}">
                <dt class="col-3">Modelo de análisis:</dt>
                <dd class="col-9">${search.sentimentModel.name}</dd>
            </c:if>
            <c:if test="${search.sentimentExtraParams.size() > 0}">
                <dt class="col-3">Parámetros:</dt>
                <dd class="col-9 mb-0">
                    <dl class="row mb-0">
                        <c:forEach var="parameter" items="${search.sentimentExtraParams}">
                            <dt class="col-3">${parameter.key.toString()}:</dt>
                            <dd class="col-9">${parameter.value.toString()}</dd>
                        </c:forEach>
                    </dl>
                </dd>
            </c:if>
        </dl>
        <dl class="row">
            <c:choose>
                <c:when test="${search.classifySubjectivity}">
                    <dt class="col-3">Análisis de subjetividad:</dt>
                    <dd class="col-9"><samp>${search.subjectivityAdapter}</samp></dd>
                    <dt class="col-3">Descartar no subjetivos:</dt>
                    <dd class="col-9">
                        <c:choose>
                            <c:when test="${search.discardNonSubjective}">Sí</c:when>
                            <c:otherwise>No</c:otherwise>
                        </c:choose>
                    </dd>
                    <c:if test="${search.subjectivityModel != null}">
                        <dt class="col-3">Modelo de análisis:</dt>
                        <dd class="col-9">${search.subjectivityModel.name}</dd>
                    </c:if>
                    <c:if test="${search.subjectivityExtraParams.size() > 0}">
                        <dt class="col-3">Parámetros:</dt>
                        <dd class="col-9 mb-0">
                            <dl class="row mb-0">
                                <c:forEach var="parameter" items="${search.subjectivityExtraParams}">
                                    <dt class="col-3">${parameter.key.toString()}:</dt>
                                    <dd class="col-9">${parameter.value.toString()}</dd>
                                </c:forEach>
                            </dl>
                        </dd>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <dt class="col-3">Análisis de subjetividad:</dt>
                    <dd class="col-9">No</dd>
                </c:otherwise>
            </c:choose>
        </dl>
    </div>
</div>

<%@ include file="_js.jsp"%>
<link rel="stylesheet" href="${path}/css/bootstrap-datetimepicker.min.css" />
<script type="text/javascript" src="/webjars/momentjs/2.20.1/min/moment-with-locales.min.js"></script>
<script type="text/javascript" src="${path}/js/bootstrap-datetimepicker.min.js"></script>
<script type="text/javascript" src="${path}/js/custom.js"></script>

<script>
    $(document).ready(function () {
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
    });

    $.when(getCommentSources('${search.lang}', '${search.sourceClass}'))
        .done(function (commentSource) {
            if (commentSource[0].limitEnabled)
                $(".limit-container").show();
            if (commentSource[0].sinceDateEnabled)
                $(".sinceDate-container").show();
            if (commentSource[0].untilDateEnabled)
                $(".untilDate-container").show();
        })
        .fail(function () {
            console.log("Error");
        });
</script>

<%@ include file="_footer.jsp"%>