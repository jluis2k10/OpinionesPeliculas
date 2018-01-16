<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<c:set var="path" value="${pageContext.request.contextPath}" />
<sec:authentication var="user" property="principal" />
<!DOCTYPE html>
<head>
    <link type="text/css" rel="stylesheet" href="${path}/css/bootstrap.min.css"  media="screen,projection"/>
    <link type="text/css" rel="stylesheet" href="${path}/css/custom.css"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <%-- Etiquetas con información sobre el token csrf para utilizarlo en ajax POSTs --%>
    <meta name="_csrf" content="${_csrf.token}"/>
    <meta name="_csrf_header" content="${_csrf.headerName}"/>
    <title>${title}</title>
</head>
<body>
<div class="cover"><div id="loader"></div></div>
<nav class="navbar navbar-default navbar-static-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse" aria-expanded="false">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${path}/">Logo</a>
        </div>

        <div class="collapse navbar-collapse" id="navbar-collapse">
            <ul class="nav navbar-nav">
                <sec:authorize access="isAuthenticated()">
                    <li><a href="${path}/models">Mis modelos</a></li>
                    <li><a href="${path}/models/create">Crear modelo</a></li>
                </sec:authorize>
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <sec:authorize access="isAnonymous()">
                    <li><a href="${path}/registro">Registro</a></li>
                    <li><a href="${path}/login">Login</a></li>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <li><a href="#" onclick="document.getElementById('logout-form').submit()">Logout</a></li>
                    <form action="${path}/logout" method="POST" id="logout-form" style="display: none;">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                    <li><a>Hola ${user.username}</a></li>
                </sec:authorize>
            </ul>
        </div>
    </div>
</nav>
<div class="container main-content">