<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../_header.jsp"%>
<c:if test="${!empty corpus.comments}">
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
            <form:hidden path="classifierType" id="classifierType"></form:hidden>
            <form:hidden path="sourceClass" id="sourceClass" value="TextDataset"></form:hidden>
            <form:hidden path="term"></form:hidden>
            <form:hidden path="modelLocation"></form:hidden>
            <form:hidden path="adapterClass"></form:hidden>
            <form:hidden path="neutralClassification"></form:hidden>
            <form:hidden path="positivesText" id="positivesText"></form:hidden>
            <form:hidden path="negativesText" id="negativesText"></form:hidden>
            <form:hidden path="neutralsText" id="neutralsText"></form:hidden>
            <form:hidden path="subjectivesText" id="subjectivesText"></form:hidden>
            <form:hidden path="objectivesText" id="objectivesText"></form:hidden>
            <button type="submit" class="btn btn-primary btn-lg btn-block">Entrenar</button>
        </form:form>
    </div>
</div>
<%@ include file="../_js.jsp"%>
<script type="text/javascript" src="${path}/js/pagination.js"></script>
<script type="text/javascript" src="${path}/js/readmore.js"></script>
<script>
    var corpus = ${corpus.toJson(true, false, false)}
    var positivesOrSubjectives = new Map();
    var negativesOrObjectives = new Map();
    var neutrals = new Map();

    $(document).ready(function () {
        myPagination(5);
    });

    function myPagination(size) {
        $("#comments-pagination").pagination({
            dataSource: corpus,
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
        var psTitle, noTitle;
        if (${trainForm.classifierType.classifierType eq "Polarity"}) {
            psTitle = "Positivo";
            noTitle = "Negativo";
        } else {
            psTitle = "Subjetivo";
            noTitle = "Objectivo";
        }

        comments.forEach(function (comment) {
            var $listItem = $('<li></li>').addClass('list-group-item flex-row align-items-start');

            var $voteDiv = $('<div></div>').addClass('votes nav flex-column');
            if (positivesOrSubjectives.has(index))
                $('<a class="vote up voted text-primary" title="' + psTitle + '" data-index="' + index + '"><i class="fas fa-caret-up align-bottom"></i></a>').appendTo($voteDiv);
            else
                $('<a class="vote up" title="' + psTitle + '" data-index="' + index + '"><i class="fas fa-caret-up align-bottom"></i></a>').appendTo($voteDiv);

            if (${trainForm.neutralClassification}) {
                if (neutrals.has(index))
                    $('<a class="vote neutral voted text-warning" title="Neutral" data-index="' + index + '"><i class="fas fa-minus align-middle"></i></a>').appendTo($voteDiv);
                else
                    $('<a class="vote neutral" title="Neutral" data-index="' + index + '"><i class="fas fa-minus align-middle"></i></a>').appendTo($voteDiv);
            }

            if (negativesOrObjectives.has(index))
                $('<a class="vote down voted text-danger" title="' + noTitle + '" data-index="' + index + '"><i class="fas fa-caret-down align-top"></i></a>').appendTo($voteDiv);
            else
                $('<a class="vote down" title="' + noTitle + '" data-index="' + index + '"><i class="fas fa-caret-down align-top"></i></a>').appendTo($voteDiv);
            $voteDiv.appendTo($listItem);

            var $mainContent = $('<p></p>').addClass('card-text readmore train').html(comment.content.replace(/(\r\n|\n|\r)/g, "<br />"));
            $mainContent.appendTo($listItem);

            $listItem.appendTo(container);
            index++;
        });
    }

    function generateReadMore() {
        $("p.readmore").readmore({
            speed: 75,
            moreLink: '<a href="#" class="readmore ml-auto align-self-baseline" title="Leer más"><i class="far fa-plus-square fa-lg"></i></a>"',
            lessLink: '<a href="#" class="readmore ml-auto align-self-baseline" title="Leer menos"><i class="far fa-minus-square fa-lg"></i></a>"',
        });
    }

    // Listener voto positivo/subjetivo (click en las flechas)
    function addVoteListeners() {
        $("a.vote").on("click", function (e) {
            var icon = e.currentTarget;
            // 2 posibilidades: click para hacer un voto o click para deshacerlo
            if ($(icon).hasClass("voted"))
                unVote(icon);
            else
                vote(icon);
        });
    }

    // Listener para selección de comentarios por página
    $("#page-size").change(function () {
        $("#comments-pagination").pagination('destroy');
        myPagination(this.value);
    });

    // Contabilizar nuevo voto
    function vote(icon) {
        $(icon).siblings().each(function () {
            var sibling = $(this).get(0);
            // Eliminar posible voto previo
            if ($(sibling).hasClass("voted")) {
                $(sibling).removeClass("voted text-primary text-danger text-warning");
                if ($(sibling).hasClass("up")) // Positivo o Subjetivo
                    positivesOrSubjectives.delete($(sibling).data("index"));
                else if ($(sibling).hasClass("down")) // Negativo u Objetivo
                    negativesOrObjectives.delete($(sibling).data("index"));
                else // Neutral
                    neutrals.delete($(sibling).data("index"));
            }
        });
        // Contabilizar voto
        var comment = $(icon).parent().parent().find("p").html().trim();
        if ($(icon).hasClass("up")) { // Positivos/Subjetivos
            $(icon).addClass("voted text-primary");
            positivesOrSubjectives.set($(icon).data("index"), comment)
        } else if ($(icon).hasClass("down")) { //Negativos/Objetivos
            $(icon).addClass("voted text-danger");
            negativesOrObjectives.set($(icon).data("index"), comment);
        } else { // Neutrales
            $(icon).addClass("voted text-warning");
            neutrals.set($(icon).data("index"), comment);
        }
    }

    // Deshacer voto
    function unVote(icon) {
        $(icon).removeClass("voted text-primary text-danger text-warning");
        if ($(icon).hasClass("up"))
            positivesOrSubjectives.delete($(icon).data("index"));
        else if ($(icon).hasClass("down"))
            negativesOrObjectives.delete($(icon).data("index"));
        else
            neutrals.delete($(icon).data("index"));
    }

    /* Acción al enviar el formulario.
       Añadimos los Mapas a un campo de texto oculto del formulario antes de enviarlo. */
    $("#trainForm").submit(function(e) {
        e.preventDefault();
        if (${trainForm.classifierType.classifierType eq "Polarity"}) {
            positivesOrSubjectives.forEach(function (comment) {
                $("#positivesText").val( $("#positivesText").val() + comment + "\n");
            });
            negativesOrObjectives.forEach(function (comment) {
                $("#negativesText").val( $("#negativesText").val() + comment + "\n");
            });
            neutrals.forEach(function (comment) {
               $("#neutralsText").val( $("#neutralsText").val() + comment + "\n");
            });
        }
        else {
            positivesOrSubjectives.forEach(function (comment) {
                $("#subjectivesText").val( $("#subjectivesText").val() + comment + "\n");
            });
            negativesOrObjectives.forEach(function (comment) {
                $("#objectivesText").val( $("#objectivesText").val() + comment + "\n");
            });
        }
        $(this).unbind("submit").submit();
    })
</script>

<%@ include file="../_footer.jsp"%>