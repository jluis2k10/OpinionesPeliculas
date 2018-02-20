<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="path" value="${pageContext.request.contextPath}" />
<sec:authentication var="user" property="principal" />
<!DOCTYPE html>
<head>
    <link type="text/css" rel="stylesheet" href="/webjars/bootstrap/4.0.0/css/bootstrap.min.css"  media="screen,projection"/>
    <link type="text/css" rel="stylesheet" href="${path}/css/custom.css"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%-- Etiquetas con información sobre el token csrf para utilizarlo en ajax POSTs --%>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <meta name="_context" content="${pageContext.request.contextPath}"/>
    <title>${title}</title>
</head>
<body>
<div class="cover"><div id="loader"></div></div>
<nav class="navbar navbar-expand-lg navbar-dark bg-secondary">
    <div class="container">
        <a class="navbar-brand" href="${path}/">Logo</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNavDropdown">
            <ul class="navbar-nav mr-auto">
                <sec:authorize access="isAuthenticated()">
                    <li class="nav-item"><a class="nav-link" href="${path}/models">Mis modelos</a></li>
                    <li class="nav-item"><a class="nav-link" href="${path}/models/create">Crear modelo</a></li>
                    <li class="nav-item"><a class="nav-link" href="${path}/searches">Mis Búsquedas</a></li>
                </sec:authorize>
            </ul>
            <ul class="navbar-nav ml-auto">
                <sec:authorize access="isAnonymous()">
                    <li class="nav-item"><a class="nav-link" href="${path}/registro">Registro</a></li>
                    <li class="nav-item"><a class="nav-link" href="${path}/login">Login</a></li>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li class="nav-item"><a class="nav-link" href="#" onclick="document.getElementById('logout-form').submit()">Logout</a></li>
                    <form action="${path}/logout" method="POST" id="logout-form" style="display: none;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                    <li class="nav-item"><a class="nav-link" >Hola ${user.username}</a></li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</nav>
<div class="container main-content">