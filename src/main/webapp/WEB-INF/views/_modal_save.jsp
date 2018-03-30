<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Modal Guardar Corpus -->
<div id="modal-saveCorpus" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header bg-light">
                <h5 class="modal-title" id="confirmLabel">Guardar Corpus y resultados del análisis</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Cerrar">
                    <span aria-hidden="true"><i class="far fa-times-circle"></i></span>
                </button>
            </div>
            <div class="modal-body">
                <form id="save-corpus" name="save-corpus" method="post" action="${path}/save-corpus">
                    <div class="form-group">
                        <label for="corpus-title" class="col-form-label">Título/nombre del Corpus:</label>
                        <input type="text" class="form-control" id="corpus-title" name="corpus-title" value="${corpus.name}">
                        <div class="invalid-feedback"></div>
                    </div>
                    <div class="form-group">
                        <label for="corpus-description" class="col-form-label">Descripción:</label>
                        <textarea class="form-control" id="corpus-description" name="corpus-description">${corpus.description}</textarea>
                    </div>
                    <div class="d-flex flex-row align-content-center flex-wrap">
                        <div class="p-1">¿Corpus público?</div>
                        <div class="p-1">
                            <label class="switch">
                                <c:choose>
                                    <c:when test="${true eq corpus.isPublic}">
                                        <input type="checkbox" checked="checked" name="corpus-public">
                                    </c:when>
                                    <c:otherwise>
                                        <input type="checkbox" name="corpus-public">
                                    </c:otherwise>
                                </c:choose>
                                <span class="slider round"></span>
                            </label>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer bg-light">
                <button type="button" class="btn btn-secondary" data-dismiss="modal"><i class="fas fa-ban"></i> Cancelar</button>
                <button type="button" class="btn btn-primary delete-confirm"><i class="fas fa-save"></i> Guardar</button>
            </div>
        </div>
    </div>
</div>
<script>
    $('button.delete-confirm').click(function (e) {
        e.preventDefault();
        $(this).prop("disabled", true);
        $.when(saveCorpus())
            .done(function (_response) {
                if (_response.status === "error") {
                    $('#corpus-title').addClass('is-invalid');
                    $('.invalid-feedback').html(_response.message);
                }
                else if (_response.status === "success") {
                    $('#corpus-title').removeClass("is-invalid");
                    $('.invalid-feedback').html("");
                    showFlashMessage('success', _response.message);
                    $('#modal-saveCorpus').modal('hide');
                }
                $('button.delete-confirm').prop("disabled", false);
            })
            .fail(function (_response) {
                showFlashMessage('danger', "<strong>Atención</strong>: no se ha podido guardar el Corpus.");
            })
    })

    function saveCorpus() {
        return Promise.resolve($.ajax({
            type: "POST",
            data: $('form#save-corpus').serialize(),
            url: ctx + "/save-corpus",
            timeout: 15000
        }));
    }
</script>