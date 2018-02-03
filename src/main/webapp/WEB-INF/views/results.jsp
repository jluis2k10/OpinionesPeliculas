<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>
<!-- Guardar búsqueda -->
<sec:authorize access="isAuthenticated()">
    <div class="row">
        <div class="col-xs-12">
            <button type="button" class="btn btn-primary save-search">Guardar búsqueda</button>
        </div>
    </div>
</sec:authorize>
<div class="row">
    <div class="col-xs-12">
        <c:if test="${!empty search.comments}">
            <ol>
                <c:forEach var="comment" items="${search.comments}">
                    <li>${comment.comment}
                        <p>${comment.tokenizedComment}</p>
                        <p><strong>${comment.sentiment}</strong>, ${comment.sentimentScore}, <strong>${comment.subjectivity}</strong>, ${comment.subjectivityScore}</p>
                    </li>
                </c:forEach>
            </ol>
        </c:if>
    </div>
</div>
<%@ include file="_js.jsp"%>
<script type="text/javascript" src="${path}/js/custom.js"></script>
<script>
    $(document).ready(function() {
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
