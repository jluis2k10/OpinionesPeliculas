<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>
<div class="row">
    <div class="col-xs-12">
        <c:if test="${!empty comments}">
            <ol>
                <c:forEach var="comment" items="${comments}">
                    <li>${comment.value.comment}
                        <p>${comment.value.tokenizedComment}</p>
                        <p><strong>${comment.value.predictedSentiment}</strong>, ${comment.value.sentimentScore}, <strong>${comment.value.predictedSubjectivity}</strong>, ${comment.value.subjectivityScore}</p>
                    </li>
                </c:forEach>
            </ol>
        </c:if>
    </div>
</div>
<%@ include file="_js.jsp"%>
<%@ include file="_footer.jsp"%>
