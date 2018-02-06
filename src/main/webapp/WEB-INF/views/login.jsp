<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>
<div class="signin-container">
    <form:form method="post" action="${path}/login" cssClass="text-center signin">
            <h1 class="mb-3">Acceso Usuarios</h1>
            <c:if test="${param.error != null}">
                <p>No existe esa combinación de usuario y contraseña</p>
            </c:if>
            <label class="sr-only" for="username">Usuario</label>
            <input id="username" class="form-control first" type="text" name="username" placeholder="Usuario">
            <label class="sr-only" for="password">Contraseña</label>
            <input id="password" class="form-control last" type="password" name="password" placeholder="Contraseña">
            <div class="checkbox mb-3">
                <label>
                    <input id="rememberme" value="rememberme" type="checkbox"> Recuérdame
                </label>
            </div>
            <button class="btn btn-lg btn-primary btn-block" type="submit">Acceder</button>
            <p class="mt-5 mb-3 text-muted">© 2018</p>
    </form:form>
</div>
<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>