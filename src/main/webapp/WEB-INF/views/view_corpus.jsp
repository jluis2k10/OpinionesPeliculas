<%@ page import="es.uned.entities.Corpus" %>
<%@ page import="es.uned.entities.Analysis" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>

<!-- Comentarios -->
<div class="row">
    <div id="comments" class="col-12">
        <h4 class="mb-2">Comentarios en el Corpus</h4>
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

<%@ include file="_js.jsp"%>

<script type="text/javascript" src="${path}/js/pagination.js"></script>
<script type="text/javascript" src="${path}/js/readmore.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    $(document).ready(function () {
        $.when(getCorpus(${corpusID}))
            .done(function (_corpus) {
                console.log(_corpus);
                myPagination(5, _corpus, $("#comments-list"));
                $.when(getAnalyses(${corpusID}))
                    .done(function(_analyses) {
                        console.log(_analyses);
                    })
            })
            .fail(function () {
                console.log("error");
            })
    });

    function getCorpus(id) {
        return Promise.resolve($.ajax({
            type: "POST",
            data: {id: id},
            url: ctx + "/corpus/get-corpus",
            timeout: 15000
        }));
    }

    function getAnalyses(id) {
        return Promise.resolve($.ajax({
            type: "POST",
            data: {id: id},
            url: ctx + "/corpus/get-analyses",
            timeout: 15000
        }));
        return;
    }

    // Listener para selección de comentarios por página
    $("#page-size").change(function () {
        $("#comments-pagination").pagination('destroy');
        myPagination(this.value, ${corpus.toJson(true).toString()}, $("#comments-list"));
    });
</script>

<%@ include file="_footer.jsp"%>