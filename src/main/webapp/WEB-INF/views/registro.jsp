<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<form:form method="post" modelAttribute="account" action="${path}/registro">
    <div class="row">
        <fieldset class="col-xs-12">
            <legend>Registro de nuevo usuario</legend>
            <spring:bind path="userName">
                <div class="col-xs-6 form-group ${status.error ? "has-error" : ""}">
                    <form:label path="userName">Nombre de usuario</form:label>
                    <form:input path="userName" type="text" cssClass="form-control" aria-describedby="errorsUserName"></form:input>
                    <form:errors path="userName" cssClass="help-block" id="errorsUserName"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="email">
                <div class="col-xs-6 form-group ${status.error ? "has-error" : ""}">
                    <form:label path="email">Dirección Email</form:label>
                    <form:input path="email" type="text" cssClass="form-control" aria-describedby="errorsEmail"></form:input>
                    <form:errors path="email" cssClass="help-block" id="errorsEmail"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="password">
                <div class="col-xs-6 form-group ${status.error ? "has-error" : ""}">
                    <form:label path="password">Contraseña</form:label>
                    <form:password path="password" cssClass="form-control" aria-describedby="errorsPassword"></form:password>
                    <form:errors path="password" cssClass="help-block" id="errorsPassword"></form:errors>
                </div>
            </spring:bind>
            <spring:bind path="passwordConfirm">
                <div class="col-xs-6 form-group ${status.error ? "has-error" : ""}">
                    <form:label path="passwordConfirm">Repetir Contraseña</form:label>
                    <form:password path="passwordConfirm" cssClass="form-control" aria-describedby="errorsPasswordConfirm"></form:password>
                    <form:errors path="passwordConfirm" cssClass="help-block" id="errorsPasswordConfirm"></form:errors>
                </div>
            </spring:bind>
        </fieldset>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <button type="submit" class="btn btn-primary">Registrar</button>
        </div>
    </div>
</form:form>

<%@ include file="_js.jsp"%>

<%@ include file="_footer.jsp"%>