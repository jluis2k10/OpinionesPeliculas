<%@ page import="es.uned.entities.CommentWithSentiment" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>
<!-- Guardar búsqueda -->
<sec:authorize access="isAuthenticated()">
    <div class="row">
        <div class="col-12">
            <button type="button" class="btn btn-primary save-search">Guardar búsqueda</button>
        </div>
    </div>
</sec:authorize>
<div class="row">
    <div class="col-12">
        <c:if test="${!empty search.comments}">
            <div class="list-group">
            <c:forEach var="comment" items="${search.comments}">
                <%
                    String sentiment = "Positivo";
                    String sentimentIcon = "<i data-feather=\"thumbs-up\"></i>";
                    String subjectivity = "";
                    String cssClass = "text-success";
                    CommentWithSentiment comment = (CommentWithSentiment) pageContext.getAttribute("comment");
                    if (comment.getSentiment().getSentiment().equals("Negative")) {
                        sentiment = "Negativo";
                        sentimentIcon = "<i data-feather=\"thumbs-down\"></i>";
                        cssClass = "text-danger";
                    }
                    else if (comment.getSentiment().getSentiment().equals("Neutral")) {
                        sentiment = "Neutral";
                        sentimentIcon = "<i data-feather=\"minus\"></i>";
                        cssClass = "";
                    }
                    if (comment.getSubjectivity() != null && comment.getSubjectivity().getSubjectivity().equals("Objective"))
                        subjectivity = "&ndash; <strong>Objetivo</strong>";
                    else if (comment.getSubjectivity() != null && comment.getSubjectivity().getSubjectivity().equals("Subjective"))
                        subjectivity = "&ndash; <strong>Subjetivo</strong>";
                %>
                <li class="list-group-item flex-column align-items-start">
                    <div class="d-flex justify-content-between">
                        <small class="mb-2 text-muted">${search.source}: <a href="${comment.sourceURL}" target="_blank">${comment.sourceURL}</a></small>
                        <small class="text-muted">${comment.date.toLocaleString()}</small>
                    </div>
                    <p class="card-text mb-2">${comment.comment}</p>
                    <small class="<%=cssClass%>">
                        <strong><%=sentimentIcon + " " + sentiment%></strong>
                        (<fmt:formatNumber type="number" maxFractionDigits="2" value="${comment.sentimentScore * 100}" />%)
                    </small>
                    <small>
                        <%=subjectivity%>
                        <c:if test="${comment.subjectivityScore > 0}">
                            (<fmt:formatNumber type="number" maxFractionDigits="2" value="${comment.subjectivityScore * 100}" />%)
                        </c:if>
                    </small>
                </li>
            </c:forEach>
            </div>
        </c:if>
    </div>
</div>
<%@ include file="_js.jsp"%>
<script type="text/javascript" src="${path}/js/custom.js"></script>
<script>
    $(document).ready(function() {
        feather.replace({
            height: 16,
            width: 16
        });
        /* Recuperar token csrf para incluirlo como cabecera en cada envío ajax */
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        $(document).ajaxSend(function (e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
    });

    $(".save-search").click(function (e) {
        $button = $(e.target);
        $button.prop("disabled", true);
        $.when(saveSearch())
            .done(function () {
                $button.hide();
                alertMsg("success", "Búsqueda guardada.");
            })
            .fail(function () {
                $button.prop("disabled", false);
                alertMsg("danger", "No se ha podido guardar la búsqueda.");
            });
    });

    function saveSearch() {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            url: "/searches/save",
            timeout: 5000
        }));
    }
</script>
<%@ include file="_footer.jsp"%>
