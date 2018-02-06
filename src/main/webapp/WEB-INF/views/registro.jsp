<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<div class="signin-container">
    <form:form method="post" modelAttribute="account" action="${path}/registro" cssClass="signin">
        <h1 class="mb-3">Nuevo usuario</h1>
        <spring:bind path="userName">
            <form:label path="userName" cssClass="sr-only">Nombre de usuario</form:label>
            <form:input path="userName" type="text" cssClass="form-control first" aria-describedby="errorsUserName" placeholder="Nombre de usuario"></form:input>
            <form:errors path="userName" cssClass="help-block" id="errorsUserName"></form:errors>
        </spring:bind>
        <spring:bind path="email">
            <form:label path="email" cssClass="sr-only">Dirección Email</form:label>
            <form:input path="email" type="text" cssClass="form-control middle" aria-describedby="errorsEmail" placeholder="Email"></form:input>
            <form:errors path="email" cssClass="help-block" id="errorsEmail"></form:errors>
        </spring:bind>
        <spring:bind path="password">
            <form:label path="password" cssClass="sr-only">Contraseña</form:label>
            <form:password path="password" cssClass="form-control middle" aria-describedby="errorsPassword" placeholder="Contraseña"></form:password>
            <form:errors path="password" cssClass="help-block" id="errorsPassword"></form:errors>
        </spring:bind>
        <spring:bind path="passwordConfirm">
            <form:label path="passwordConfirm" cssClass="sr-only">Repetir Contraseña</form:label>
            <form:password path="passwordConfirm" cssClass="form-control last" aria-describedby="errorsPasswordConfirm" placeholder="Repite Contraseña"></form:password>
            <form:errors path="passwordConfirm" cssClass="help-block" id="errorsPasswordConfirm"></form:errors>
        </spring:bind>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Regístrame</button>
    </form:form>
</div>

<%@ include file="_js.jsp"%>

<%@ include file="_footer.jsp"%>