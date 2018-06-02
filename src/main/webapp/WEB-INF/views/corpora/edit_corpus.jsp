<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../_header.jsp"%>

<h4 class="mb-3">Editar corpus</h4>
<form:form method="post" modelAttribute="editCorpusForm" action="${path}/corpora/edit/${editCorpusForm.id}">
    <div class="list-group">
        <div class="list-group-item flex-column align-items-start">
            <div class="row">
                <div class="form-group col-4">
                    <spring:bind path="name">
                        <form:label path="name">Nombre</form:label>
                        <form:input path="name" type="text" cssClass="form-control" cssErrorClass="form-control is-invalid" value="${editCorpusForm.name}"></form:input>
                        <form:errors path="name" cssClass="is-invalid"></form:errors>
                    </spring:bind>
                </div>
                <div class="form-group col-12">
                    <spring:bind path="description">
                        <form:label path="description">Descripción</form:label>
                        <form:textarea path="description" cssClass="form-control" cssErrorClass="form-control is-invalid" value="${editCorpusForm.description}"></form:textarea>
                        <form:errors path="description" cssClass="is-invalid"></form:errors>
                    </spring:bind>
                </div>
            </div>
            <div class="row">
                <div class="col-4">
                    <button type="submit" class="btn btn-primary btn-lg btn-block"><i class="fas fa-save"></i> Guardar Cambios</button>
                </div>
            </div>
        </div>
    </div>
    <form:hidden path="id"></form:hidden>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
</form:form>

<%@ include file="../_js.jsp"%>

<script type="text/javascript" src="${path}/js/common.js"></script>

<%@ include file="../_footer.jsp"%>