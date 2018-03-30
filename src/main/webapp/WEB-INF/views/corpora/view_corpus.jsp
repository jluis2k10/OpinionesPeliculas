<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../_header.jsp"%>

<h4 class="mb-3">Corpus: ${corpus.name}</h4>
<div class="row">
    <div class="col-12">
        <button id="toggleGraphs" class="btn btn-primary btn-sm mr-2" type="button">Mostrar gráficas</button>
        <button id="toggleComments" class="btn btn-primary btn-sm" type="button">Mostrar comentarios</button>
    </div>
</div>
<div class="row">
    <div id="graphs" class="col-12 collapse show">
        gráficas
    </div>
    <div id="comments" class="col-12 collapse">
        comentarios
    </div>
</div>

<%@ include file="../_js.jsp"%>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    $(document).ready(function () {

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

</script>

<%@ include file="../_footer.jsp"%>
