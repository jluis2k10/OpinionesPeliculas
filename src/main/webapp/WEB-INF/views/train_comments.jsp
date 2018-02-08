<%@ page import="es.uned.entities.CommentWithSentiment" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.stream.Collectors" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="_header.jsp"%>
<%
    List<CommentWithSentiment> myComments = (List<CommentWithSentiment>) request.getAttribute("comments");
    List<String> commentsJSON = new ArrayList<>();
    for(CommentWithSentiment comment : myComments) {
        commentsJSON.add(comment.toJSON().toString());
    }
%>
<c:if test="${!empty comments}">
<div class="row">
    <div id="comments" class="col-12">
        <ul id="comments-list" class="list-group mb-3"></ul>
        <div class="row">
            <div class="col-12 col-md-6">
                <label>
                    Mostrar
                    <select class="form-control form-control-sm pages-size" id="page-size">
                        <option>5</option>
                        <option>10</option>
                        <option>25</option>
                        <option>50</option>
                        <option>100</option>
                    </select>
                    comentarios
                </label>
            </div>
            <div id="comments-pagination" class="col-12 col-md-6"></div>
        </div>
    </div>
</div>
</c:if>

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
<script type="text/javascript" src="${path}/js/pagination.js"></script>
<script type="text/javascript" src="${path}/js/readmore.js"></script>
<script>
    var positivesOrSubjectives = new Map();
    var negativesOrObjectives = new Map();

    $(document).ready(function () {
        myPagination(5);
    });

    function myPagination(size) {
        $("#comments-pagination").pagination({
            dataSource: <%=commentsJSON%>,
            locator: 'comments',
            pageSize: size,
            callback: function (comments, pagination) {
                formatComments(comments, $("#comments-list"), pagination);
                generateReadMore();
                $("li.list-group-item").addClass("d-flex"); // Hack! si agrego la clase antes de generar los "read more" se renderiza mal
                addVoteListeners();
            },
            ulClassName: "pagination justify-content-end"
        });
    };

    function formatComments(comments, container, pagination) {
        container.empty();
        var index = 0 + pagination.pageSize * (pagination.pageNumber - 1);
        comments.forEach(function (comment) {
            var $listItem = $('<li></li>').addClass('list-group-item list-group-item-action flex-row align-items-start');

            var $voteDiv = $('<div></div>').addClass('vote');
            if (positivesOrSubjectives.has(index))
                $('<i class="arrow up voted text-primary" data-feather="arrow-up" data-index="' + index + '"></i>').appendTo($voteDiv);
            else
                $('<i class="arrow up" data-feather="arrow-up" data-index="' + index + '"></i>').appendTo($voteDiv);
            if (negativesOrObjectives.has(index))
                $('<i class="arrow down voted text-danger" data-feather="arrow-down" data-index="' + index + '"></i>').appendTo($voteDiv);
            else
                $('<i class="arrow down" data-feather="arrow-down" data-index="' + index + '"></i>').appendTo($voteDiv);
            $voteDiv.appendTo($listItem);

            var $mainContent = $('<p></p>').addClass('card-text readmore train').html(comment.comment);
            $mainContent.appendTo($listItem)

            $listItem.appendTo(container);
            index++;
        });
        feather.replace({
            width: 20,
            height: 20
        });
    }

    function generateReadMore() {
        $("p.readmore").readmore({
            speed: 75,
            moreLink: '<a href="#" class="readmore train" title="Leer más"><i data-feather="plus-circle"></i></a>"',
            lessLink: '<a href="#" class="readmore train" title="Leer menos"><i data-feather="minus-circle"></i></a>"',
            afterToggle: function (trigger, element, expanded) {
                feather.replace({
                    height: 24,
                    width: 24
                });
            }
        });
        feather.replace({
            height: 24,
            width: 24
        });
    }

    // Listener voto positivo/subjetivo (click en las flechas)
    function addVoteListeners() {
        $("svg.arrow").on("click", function (e) {
            var icon = e.currentTarget;
            // 2 posibilidades: click para hacer un voto o click para deshacerlo
            if ($(icon).hasClass("voted")) {
                unVote(icon);
            } else {
                vote(icon);
            }
            console.log(positivesOrSubjectives);
            console.log(negativesOrObjectives);
        });
    }

    // Listener para selección de comentarios por página
    $("#page-size").change(function () {
        $("#comments-pagination").pagination('destroy');
        myPagination(this.value);
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