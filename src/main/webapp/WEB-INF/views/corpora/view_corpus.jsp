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
        <div id="pie" class="col-6 mb-5"></div>
        <div id="bar" class="col-6 mb-5"></div>
        <div id="shared" class="col-12 mb-5"></div>
        <div id="time" class="col-12 mb-5"></div>
        <div id="scatter" class="col-12 mb-5"></div>
    </div>
</div>
<div id="comments" class="collapse pt-3">
    <div class="row d-flex flex-row">
        <div class="col-12">
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
</div>

<%@ include file="../_js.jsp"%>
<script type="text/javascript" src="${path}/js/pagination.js"></script>
<script type="text/javascript" src="${path}/js/readmore.js"></script>
<script type="text/javascript" src="${path}/js/zingchart/zingchart.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>
<script type="text/javascript" src="${path}/js/graphs.js"></script>

<script>
    var corpus = ${corpus.toJson(true, true, true).toString()};
    $(document).ready(function () {
        renderPieChart('pie', corpus.comments);
        renderBarChart('bar', corpus.comments);
        renderSharedChart('shared', corpus);
        // Renderizar gráficas de evolución temporal y de distribución sólo si hay análisis
        // de polaridad ejecutados sobre el corpus
        if ($.grep(corpus.analyses, function (analysis) {
            return analysis.type === "polarity";
        }).length > 0) {
            renderTimeEvoChart('time', corpus.comments);
            // Para la gráfica de distribución también necesitamos que haya análisis de opinión
            if ($.grep(corpus.analyses, function (analysis) {
                return analysis.type === "opinion"
            }).length > 0)
                renderScatterChart('scatter', corpus);
        }

        myPagination(5, ${corpus.toJson(true, false, true).toString()}, $("#comments-list"), true);
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

    // Listener para selección de comentarios por página
    $("#page-size").change(function () {
        $("#comments-pagination").pagination('destroy');
        myPagination(this.value, ${corpus.toJson(true, false, true).toString()}, $("#comments-list"), true);
    });

    // Evitar enlace a # en los popovers de los detalles del análisis
    $('#comments-list').on('click', 'a.analysis-popover', function (e) {
        e.preventDefault();
    });
</script>

<%@ include file="../_footer.jsp"%>
