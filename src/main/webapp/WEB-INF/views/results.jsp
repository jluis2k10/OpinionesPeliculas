<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>
<div class="row">
    <div class="col-xs-12">
        <c:if test="${!empty comments}">
            <ol>
                <c:forEach var="comment" items="${comments}">
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
<%@ include file="_footer.jsp"%>
