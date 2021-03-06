<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="_header.jsp"%>

<h4>
    <a href="#" id="goBack" title="Análisis de Opinión"><i class="fas fa-chevron-left"></i></a> Resultados del análisis
    <small class="text-muted">
        <a href="#charts" data-toggle="collapse" aria-controls="sharedChart" aria-expanded="true">[Mostrar/Ocultar]</a>
    </small>
</h4>

<!-- Gráficas -->
<div id="charts" class="row collapse show mb-4">
    <div id="sharedChart" class="col-12 mb-4"></div>
    <div id="domainChart" class="col-12"></div>
</div>

<!-- Comentarios -->
<div class="row">
    <div id="comments" class="col-12">
        <c:if test="${!empty corpus.comments}">
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
        </c:if>
    </div>
</div>

<!-- Guardar Corpus -->
<sec:authorize access="isAuthenticated()">
    <div class="row">
        <div class="col-12">
            <button type="submit" class="btn btn-primary btn-lg btn-block save-corpus"><i class="fas fa-save"></i> Guardar Corpus</button>
        </div>
    </div>
</sec:authorize>

<%@ include file="_js.jsp"%>

<script type="text/javascript" src="${path}/js/pagination.js"></script>
<script type="text/javascript" src="${path}/js/readmore.js"></script>
<%--<script type="text/javascript" src="webjars/momentjs/2.20.1/min/moment-with-locales.min.js"></script>--%>
<script type="text/javascript" src="${path}/js/zingchart/zingchart.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>
<script type="text/javascript" src="${path}/js/graphs.js"></script>
<script>
    //moment.locale('es');
    var corpus = ${corpus.toJson(true, true, true).toString()};
    $(document).ready(function () {
        myPagination(5, corpus, $("#comments-list"), false);
        renderSharedChart('sharedChart', corpus);
        renderDomainChart('domainChart', corpus);
    });

    // Listener para el botón de "volver"
    $('a#goBack').click(function (e) {
        e.preventDefault();
        $('<form>', {
            method: 'post',
            action: '?action=back',
            html: $('<input>', {
                type: 'hidden',
                name: '_csrf',
                value: "${_csrf.token}"
            })
        }).appendTo($('body')).submit();
    });

    // Listener para selección de comentarios por página
    $("#page-size").change(function () {
        $("#comments-pagination").pagination('destroy');
        myPagination(this.value, ${corpus.toJson(true, false, false).toString()}, $("#comments-list"), false);
    });

    // Listener para el botón de Guardar Corpus
    $('button.save-corpus').click(function (e) {
        e.preventDefault();
        $('#modal-saveCorpus').modal();
    });
</script>

<sec:authorize access="isAuthenticated()">
    <%@include file="_modal_save.jsp"%>
</sec:authorize>

<%@ include file="_footer.jsp"%>