<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>
<div class="row">
    <div class="col-xs-12">
        <c:if test="${!empty comments}">
            <c:forEach var="comment" items="${comments}" varStatus="status">
                <div class="panel panel-default">
                    <div class="panel-body">
                        ${comment.value.comment}
                    </div>
                    <div class="panel-footer">
                        <button type="button" class="btn btn-default" data-index="${status.index}" onclick="addPosSub(this)">
                            <span class="glyphicon glyphicon-ok" aria-hidden="true"></span><span class="text-ok"> Positivo</span>
                        </button>
                        <button type="button" class="btn btn-default" data-index="${status.index}" onclick="addNegObj(this)">
                            <span class="glyphicon glyphicon-remove" aria-hidden="true"></span><span class="text-ko"> Negativo</span>
                        </button>
                        <button type="button" class="btn btn-default" data-index="${status.index}" onclick="clearAction(this)">
                            <span class="glyphicon glyphicon-retweet" aria-hidden="true"></span><span> Limpiar</span>
                        </button>
                    </div>
                </div>
            </c:forEach>
        </c:if>
    </div>
</div>
<div class="row">
    <div class="col-xs-12">
        <form:form method="post" modelAttribute="trainForm" action="${path}/train" enctype="multipart/form-data">
            <form:hidden path="analysisType" id="analysisType"></form:hidden>
            <form:hidden path="sourceClass" id="sourceClass" value="TextDataset"></form:hidden>
            <form:hidden path="searchTerm"></form:hidden>
            <form:hidden path="modelLocation"></form:hidden>
            <form:hidden path="adapterClass"></form:hidden>
            <form:hidden path="psText" id="psText"></form:hidden>
            <form:hidden path="noText" id="noText"></form:hidden>
            <button type="submit" class="btn btn-primary">Entrenar modelo</button>
        </form:form>
    </div>
</div>
<%@ include file="_js.jsp"%>
<script>
    $(document).ready(function() {
        if ($("#analysisType").val() === "subjectivity") {
            $(".text-ok").text(" Subjetivo");
            $(".text-ko").text(" Objetivo");
        }
    });
    var positivesOrSubjectives = new Map();
    var negativesOrObjectives = new Map();
    function addPosSub(button) {
        // Cambiar estilo del botón
        $(button).removeClass("btn-default");
        $(button).addClass("btn-success");

        // Cambiar estilo del botón hermano
        var siblingButton = $(button).siblings(".btn");
        $(siblingButton).removeClass("btn-danger");
        $(siblingButton).addClass("btn-default");

        var comment = $(button).parent().siblings(".panel-body").text().trim();
        var i = $(button).data("index");
        negativesOrObjectives.delete(i);        // Eliminar comentario del mapa de negativos/objetivos
        positivesOrSubjectives.set(i, comment); // Añadir comentario al mapa de positivos/subjetivos
    }

    function addNegObj(button) {
        // Cambiar estilo del botón
        $(button).removeClass("btn-default");
        $(button).addClass("btn-danger");

        // Cambiar estilo del botón hermano
        var siblingButton = $(button).siblings(".btn");
        $(siblingButton).removeClass("btn-success");
        $(siblingButton).addClass("btn-default");

        var comment = $(button).parent().siblings(".panel-body").text().trim();
        var i = $(button).data("index");
        positivesOrSubjectives.delete(i);      // Eliminar comentario del mapa de positivos/subjetivos
        negativesOrObjectives.set(i, comment); // Añadir comentario al mapa de negativos/objetivos
    }

    function clearAction(button) {
        var siblingButton = $(button).siblings(".btn");
        $(siblingButton).removeClass("btn-success");
        $(siblingButton).removeClass("btn-danger");
        $(siblingButton).addClass("btn-default");
        var i = $(button).data("index");
        positivesOrSubjectives.delete(i);
        negativesOrObjectives.delete(i);
    }

    $("#trainForm").submit(function(e) {
        e.preventDefault();
        for (var positiveOrSubjective of positivesOrSubjectives) {
            var value = $("#psText").val() + positiveOrSubjective[1] + "\n";
            $("#psText").val(value);
        }
        for (var negativeOrSubjective of negativesOrObjectives) {
            var value = $("#noText").val() + negativeOrSubjective[1] + "\n";
            $("#noText").val(value);
        }
        $(this).unbind("submit").submit();
    })
</script>

<%@ include file="_footer.jsp"%>