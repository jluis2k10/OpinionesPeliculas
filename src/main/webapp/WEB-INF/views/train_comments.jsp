<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>
<div class="row">
    <div class="col-12">
    <c:if test="${!empty comments}">
        <ul class="list-group mb-3">
            <c:forEach var="comment" items="${comments}" varStatus="status">
                <li class="list-group-item list-group-item-action flex-column align-items-start">
                    <div class="vote">
                        <i class="arrow up" data-feather="arrow-up" data-index="${status.index}"></i>
                        <i class="arrow down" data-feather="arrow-down" data-index="${status.index}"></i>
                    </div>
                    <p>${comment.comment}</p>
                </li>
            </c:forEach>
        </ul>
    </c:if>
    </div>
</div>
<div class="row">
    <div class="col-12">
        <form:form method="post" modelAttribute="trainForm" enctype="multipart/form-data">
            <form:hidden path="adapterType" id="adapterType"></form:hidden>
            <form:hidden path="sourceClass" id="sourceClass" value="TextDataset"></form:hidden>
            <form:hidden path="term"></form:hidden>
            <form:hidden path="modelLocation"></form:hidden>
            <form:hidden path="adapterClass"></form:hidden>
            <form:hidden path="psText" id="psText"></form:hidden>
            <form:hidden path="noText" id="noText"></form:hidden>
            <button type="submit" class="btn btn-primary btn-lg btn-block">Entrenar</button>
        </form:form>
    </div>
</div>
<%@ include file="_js.jsp"%>
<script>
    feather.replace({
        width: 20,
        height: 20
    });
    $(document).ready(function() {
        if ($("#adapterType").val() === "SUBJECTIVITY") {
            $(".text-ok").text(" Subjetivo");
            $(".text-ko").text(" Objetivo");
        }
    });
    var positivesOrSubjectives = new Map();
    var negativesOrObjectives = new Map();

    // Listener voto positivo/subjetivo
    $("svg.arrow").on("click", function (e) {
        var icon = e.currentTarget;
        // 2 posibilidades: click para hacer un voto o click para deshacerlo
        if ($(icon).hasClass("voted")) {
            unVote(icon);
        } else {
            vote(icon);
        }
    });

    // Contabilizar nuevo voto
    function vote(icon) {
        var sibling = $(icon).siblings();
        // Eliminar posible voto previo
        if ($(sibling).hasClass("voted")) {
            $(sibling).removeClass("voted text-primary text-danger");
            if ($(sibling).hasClass("up"))
                positivesOrSubjectives.delete($(sibling).data("index"));
            else
                negativesOrObjectives.delete($(sibling).data("index"));
        }
        // Contabilizar voto
        var comment = $(icon).parent().parent().find("p").html().trim();
        if ($(icon).hasClass("up")) {
            $(icon).addClass("voted text-primary");
            positivesOrSubjectives.set($(icon).data("index"), comment)
        } else {
            $(icon).addClass("voted text-danger");
            negativesOrObjectives.set($(icon).data("index"), comment);
        }
    }

    // Deshacer voto
    function unVote(icon) {
        $(icon).removeClass("voted text-primary text-danger");
        if ($(icon).hasClass("up"))
            positivesOrSubjectives.delete($(icon).data("index"));
        else
            negativesOrObjectives.delete($(icon).data("index"));
    }

    /* Acción al enviar el formulario.
       Añadimos los Mapas a un campo de texto oculto del formulario antes de enviarlo. */
    $("#trainForm").submit(function(e) {
        e.preventDefault();
        positivesOrSubjectives.forEach(function (comment) {
            $("#psText").val( $("#psText").val() + comment + "\n" );
        });
        negativesOrObjectives.forEach(function (comment) {
            $("#noText").val( $("#noText").val() + comment + "\n" );
        });
        $(this).unbind("submit").submit();
    })
</script>

<%@ include file="_footer.jsp"%>