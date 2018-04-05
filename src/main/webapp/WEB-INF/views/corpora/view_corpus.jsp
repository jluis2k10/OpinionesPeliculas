<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../_header.jsp"%>

<h4 class="mb-3">Corpus: ${corpus.name} <small>${corpus.comments.size()} comentarios</small></h4>
<div class="row">
    <div class="col-12">
        <button id="toggleGraphs" class="btn btn-primary btn-sm mr-2" type="button">Mostrar gráficas</button>
        <button id="toggleComments" class="btn btn-primary btn-sm" type="button">Mostrar comentarios</button>
    </div>
</div>
<div id="graphs" class="collapse show">
    <div class="row d-flex flex-row pt-3">
        <div id="pie" class="col-6"></div>
        <div id="bar" class="col-6"></div>
        <div id="shared" class="col-12"></div>
        <div id="time" class="col-12"></div>
    </div>
</div>
<div id="comments" class="collapse">
    <div class="row d-flex flex-row">
        <div class="col-12">
            comentarios
        </div>
    </div>
</div>

<%@ include file="../_js.jsp"%>
<script type="text/javascript" src="${path}/js/zingchart/zingchart.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>
<script type="text/javascript" src="${path}/js/graphs.js"></script>

<script>
    var corpus = ${corpus.toJson(true, true).toString()};

    $(document).ready(function () {
        renderPieChart('pie', corpus.comments);
        renderBarChart('bar', corpus.comments);
        renderSharedChart('shared', corpus);
        if ($.grep(corpus.analyses, function (analysis) {
            return analysis.type === "polarity";
        }).length > 0)
            renderTimeEvoChart('time', corpus.comments);
    });

    // Listeners para toggle de las gráficas/comentarios
    $('#toggleGraphs').on('click', function () {
        $('#comments').collapse('hide');
        $('#graphs').collapse('show');
    });
    $('#toggleComments').on('click', function () {
        $('#graphs').collapse('hide');
        $('#comments').collapse('show');
    });

    // Listeners para los clicks en las etiquetas de las gráficas
    /*zingchart.bind(null, 'label_click', function(e) {

    });*/

</script>

<%@ include file="../_footer.jsp"%>
