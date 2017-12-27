<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>

<form:form method="post" action="${path}/login">
    <div class="row">
        <fieldset class="col-xs-12">
            <legend>Login</legend>
            <c:if test="${param.error != null}">
                <p>No existe esa combinación de usuario y contraseña</p>
            </c:if>
            <div class="col-xs-12 form-group">
                <label for="username">Usuario</label>
                <input id="username" type="text" name="username">
            </div>
            <div class="col-xs-12 form-group">
                <label for="password">Contraseña</label>
                <input id="password" type="password" name="password">
            </div>
            <div class="col-xs-12 form-group">
                <label for="rememberme">Recuérdame</label>
                <input id="rememberme" type="checkbox" name="rememberMe" checked="checked" />
            </div>
        </fieldset>
    </div>
    <div class="row">
        <div class="col-xs-12">
            <button type="submit" class="btn btn-primary">Login</button>
        </div>
    </div>
</form:form>

<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>