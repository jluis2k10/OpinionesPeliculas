<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../_header.jsp"%>

<h4 class="mb-3">Mi Corpora</h4>

<table id="corpora-list" class="table table-striped table-sm table-bordered" width="100%" cellpadding="0">
    <thead>
    <tr>
        <th width="15px"></th>
        <th width="180px">Nombre</th>
        <th>Descripción</th>
        <th width="60px">Idioma</th>
        <th width="80px">Creado</th>
        <th width="80px">Ops.</th>
    </tr>
    </thead>
</table>

<!-- Modal confirmación borrado -->
<div id="modal-confirm" class="modal fade" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="alertdialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="confirmLabel">Advertencia</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <p>¿Desea borrar el corpus <strong></strong>? No hay vuelta atrás.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                <button type="button" class="btn btn-danger delete-confirm">Borrar</button>
            </div>
        </div>
    </div>
</div>

<%@ include file="../_js.jsp"%>

<link rel="stylesheet" href="webjars/datatables/1.10.16/css/dataTables.bootstrap4.min.css" />
<script type="text/javascript" src="webjars/datatables/1.10.16/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="webjars/datatables/1.10.16/js/dataTables.bootstrap4.min.js"></script>
<script type="text/javascript" src="${path}/js/common.js"></script>

<script>
    var renderedTable;
    $(document).ready(function () {
        $.when(getUserCorpora())
            .done(function (_corpora) {
                renderedTable = renderCorporaTable(_corpora);
            })
            .fail(function () {
                showFlashMessage("danger", "Error recuperando corpora de usuario.")
            })
    });

    function renderCorporaTable(corpora) {
        return $("#corpora-list").DataTable({
            language: datatablesLocalization(),
            data: corpora,
            autoWidth: false,
            columns: [
                {
                    className: "details-control",
                    orderable: false,
                    data: null,
                    defaultContent: "",
                    "render": function () {
                        return '<span class="open-details"><i class="fas fa-plus-circle"></i></span>';
                    }
                },
                {
                    data: null,
                    defaultContent: "",
                    "render": function (corpus) {
                        return '<a href="' + ctx + '/corpora/view/' + corpus.id + '" title="Ver resultados">' + corpus.name + '</a>';
                    }
                },
                {
                    data: null,
                    defaultContent: "",
                    className: "oneliner",
                    "render": function (corpus) {
                        return (typeof corpus.description != 'undefined') ? corpus.description : "" ;
                    }
                },
                {data: "lang"},
                {data: "created"},
                {
                    orderable: false,
                    data: null,
                    defaultContent: "",
                    "render": function (corpus) {
                        return renderCorpusQuickOptions(corpus);
                    }
                }
            ],
            columnDefs: [
                {type: 'date-euro', targets: 3}
            ],
            "order": [[1, 'desc']]
        });
    }

    function renderCorpusQuickOptions(corpus) {
        var togglePublicText = "Hacer Público";
        var togglePublicHtml = '<i class="fas fa-eye-slash"></i>';
        if (corpus.is_public) {
            togglePublicText = "Hacer Privado";
            togglePublicHtml = '<i class="fas fa-eye"></i>';
        }
        var btnGroup = $('<div>', {
            class: "btn-group btn-group-sm",
            role: "group"
        }).append($('<button>', {
            class: "btn btn-secondary btn-sm btn-switchPublic",
            type: "button",
            "data-corpusid": corpus.id,
            title: togglePublicText,
            html: togglePublicHtml
        })).append($('<a>', {
            href: ctx + "/corpora/edit/" + corpus.id,
            class: "btn btn-secondary btn-sm btn-editCorpus",
            type: "button",
            "data-corpusid": corpus.id,
            title: "Editar Corpus",
            html: "<i class=\"fas fa-edit\"></i>"
        })).append($('<button>', {
            class: "btn btn-danger btn-sm btn-deleteCorpus",
            type: "button",
            "data-corpusid": corpus.id,
            title: "Borrar Corpus",
            html: '<i class="far fa-trash-alt"></i>'
        }));
        return btnGroup.prop('outerHTML');
    }

    // Listener para abrir/cerrar detalles en la tabla
    $("#corpora-list").on("click", "td.details-control", function () {
        var tr = $(this).closest("tr");
        var iconSpan = tr.find('span.open-details');
        var datatableRow = renderedTable.row(tr);

        if (datatableRow.child.isShown()) {
            datatableRow.child.hide();
            tr.removeClass("shown");
            iconSpan.empty();
            iconSpan.append('<i class="fas fa-plus-circle"></i></i>');
        } else {
            datatableRow.child(render_details(datatableRow.data())).show();
            var analysesTable = datatableRow.child().find('table.analyses-table');
            createAnalysesDataTable(analysesTable, datatableRow.data());
            tr.addClass("shown");
            iconSpan.empty();
            iconSpan.append('<i class="fas fa-minus-circle"></i>');
        }
    });

    // Evitar enlace a # en los popovers de los detalles del análisis
    $('#corpora-list').on('click', 'a.details-popover', function (e) {
        e.preventDefault();
    });

    function render_details(corpus) {
        var updated = (typeof corpus.updated != "undefined") ? corpus.updated : '&ndash;';
        var actions = $('<span>').append($('<a>', {
            class: "btn btn-primary btn-sm mr-2",
            href: "${path}/corpora/add-comments/" + corpus.id,
            type: "button",
            html: "Añadir Comentarios"
        })).append($('<a>', {
            class: "btn btn-primary btn-sm mr-2",
            href: "${path}/corpora/add-opinion-analysis/" + corpus.id,
            type: "button",
            html: "Nuevo/s Análisis de Opinión"
        })).append($('<a>', {
            class: "btn btn-primary btn-sm",
            href: "${path}/corpora/add-polarity-analysis/" + corpus.id,
            type: "button",
            html: "Nuevo/s Análisis de Polaridad"
        })).prop('outerHTML');
        var description = (typeof corpus.description != 'undefined') ? corpus.description : '';
        var details = '<dl class="row px-4 mb-2">' +
            '<dt class="col-2">Comentarios</dt>' +
            '<dd class="col-10">' + corpus.total_comments + '</dd>' +
            '<dt class="col-2">Descripción</dt>' +
            '<dd class="col-10">' + description + '</dd>' +
            '<dt class="col-2">Actualizado</dt>' +
            '<dd class="col-10">' + updated + '</dd>' +
            '<dt class="col-2">Acciones</dt>' +
            '<dd class="col-10">' + actions + '</dd>' +
            '</dl>';
        var analysesTable = (corpus.analyses.length > 0) ? renderAnalysesTable() : '';
        return details + analysesTable;
    }

    function renderAnalysesTable() {
        var card = $('<div>', {
            class: "card mx-4 bg-light border-info mb-3"
        }).append($('<div>', {
            class: "card-body"
        }).append($('<table>', {
            class: 'table table-striped table-sm table-bordered analyses-table',
            width: '98%',
            cellpadding: '0'
        }).append($('<thead>', {
            html: '<th>Tipo de A.</th>' +
            '<th>Librería</th>' +
            '<th>Modelo</th>' +
            '<th>Ejecutado</th>' +
            '<th>#Coment.</th>' +
            '<th width="40px">Ops.</th>'
        }))));
        return card.prop('outerHTML');
    }

    function createAnalysesDataTable(table, corpus) {
        if (corpus.analyses.length == 0)
            return;
        var analysesTable = table.DataTable({
            language: datatablesLocalization(),
            data: corpus.analyses,
            dom: '<"row"<"toolbar col-6"><"col-6"f>><"row"rt>',
            paging: false,
            info: false,
            autoWidth: false,
            columns: [
                {
                    data: null,
                    defaultContent: "",
                    "render": function (analysis) {
                        return renderAnalysisDetails(analysis);
                    }
                },
                {data: "classifier"},
                {data: "language_model"},
                {
                    data: null,
                    defaultContent: "",
                    "render": function (analysis) {
                        return (analysis.updated != null) ? analysis.updated : analysis.created;
                    }
                },
                {data: "total_records"},
                {
                    orderable: false,
                    data: null,
                    defaultContent: "",
                    "render": function (analysis) {
                        return renderAnalysisOps(analysis);
                    }
                }
            ],
            columnDefs: [
                {type: 'date-euro', targets: 3}
            ],
            "order": [[0, 'asc']]
        });
        $("div.toolbar").html("<h5>Análisis ejecutados sobre este Corpus</h5>");
        $('a.details-popover').popover({
            content: function () {
                var tr = $(this).closest("tr");
                var analysis = analysesTable.row(tr).data();
                return renderPopoverContent(analysis);
            },
            html: true,
            trigger: "focus"
        });
    }

    function renderAnalysisDetails(analysis) {
        var analysisType = (analysis.type === "polarity") ? "Polaridad " : "Opinión ";
        var detailsIcon = $('<a>', {
            href: "#",
            class: "details-popover",
            data: {toggle: "popover"},
            title: "Detalles del Análisis",
            html: '<i class="fas fa-info-circle"></i>'
        });
        return analysisType + detailsIcon.prop('outerHTML');
    }

    function renderPopoverContent(analysis) {
        var content = $('<span>');
        if (analysis.type === "polarity" && analysis.opinions_only)
            content.append($('<p>', {
                class: 'mb-1',
                html: '<strong>Analizar sólo opiniones</strong> &ndash; Sí'
            }));
        else if (analysis.type === "polarity" && !analysis.opinions_only)
            content.append($('<p>', {
                class: 'mb-1',
                html: '<strong>Analizar sólo opiniones</strong> &ndash; No'
            }));
        if (analysis.stop_words_deletion)
            content.append($('<p>', {
                class: 'mb-1',
                html: '<strong>Eliminar <em>stop-words</em></strong> &ndash; Sí'
            }));
        else
            content.append($('<p>', {
                class: 'mb-1',
                html: '<strong>Eliminar <em>stop-words</em></strong> &ndash; No'
            }));
        $.each(analysis.options, function (key, val) {
            content.append($('<p>', {
                class: 'mb-1',
                html: '<strong>' + key + '</strong> &ndash; ' + val
            }));
        });
        return content;
    }

    function renderAnalysisOps(analysis) {
        var btnGroup = $('<div>', {
            class: "btn-group btn-group-sm",
            role: "group"
        }).append($('<button>', {
            class: "btn btn-secondary btn-sm btn-rerunAnalysis",
            type: "button",
            "data-analysisid": analysis.id,
            title: "Ejecutar de nuevo",
            html: '<i class="fas fa-sync-alt"></i>'
        })).append($('<button>', {
            class: "btn btn-danger btn-sm btn-deleteAnalysis",
            type: "button",
            "data-analysisid": analysis.id,
            title: "Eliminar Análisis",
            html: '<i class="far fa-trash-alt"></i>'
        }));
        return btnGroup.prop('outerHTML');
    }

    // Listener acción cambiar corpus público/privado
    $('#corpora-list').on('click', '.btn-switchPublic', function () {
        $button = $(this);
        $button.prop('disabled', true);
        $.when(switchCorpusPublic($button.data('corpusid')))
            .always(function () {
                $button.prop('disabled', false);
            })
            .done(function () {
                if ($button.find("svg").hasClass("fa-eye")) {
                    $button.empty();
                    $button.html("<i class='fas fa-eye-slash'></i>");
                    $button.prop("title", "Hacer privado");
                    showFlashMessage("success", "El corpus es ahora público.");
                } else {
                    $button.empty();
                    $button.html("<i class='fas fa-eye'></i>");
                    $button.prop("title", "Hacer público");
                    showFlashMessage("success", "El corpus es ahora privado.");
                }
            })
            .fail(function () {
                showFlashMessage("danger", "<strong>Error:</strong> no se ha podido cambiar la visibilidad del corpus.")
            })
    });

    // Listener acción borrar corpus
    $('#corpora-list').on('click', '.btn-deleteCorpus', function() {
        var corpusid = $(this).data('corpusid');
        $row = $(this).closest('tr');
        $row.addClass('selected');
        var corpusName = $row.find('td').eq(1).find('a').eq(0).text();
        var modal = $('#modal-confirm').modal('toggle');
        modal.find(".modal-body").html("<p>¿Desea borrar el corpus " + corpusid +", <strong>" + corpusName + "</strong>? No hay vuelta atrás.</p>");
        modal.find(".delete-confirm").data({
            "type": "corpus",
            "corpusid": corpusid,
            "corpusname": corpusName
        });
    });

    // Listener acción borrar análisis
    $('#corpora-list').on('click', '.btn-deleteAnalysis', function () {
        var analysisid = $(this).data('analysisid');
        $row = $(this).closest('tr');
        $row.addClass('selected');
        var library = $row.find('td').eq(1).text();
        var languageModel = $row.find('td').eq(2).text();
        languageModel = languageModel != "" ? " (" + languageModel + ")" : "";
        var modal = $('#modal-confirm').modal('toggle');
        modal.find('.modal-body').html(
            '<p>¿Desea eliminar el análisis <strong>' + library + languageModel + '</strong>? No hay vuelta atrás.'
        );
        modal.find('.delete-confirm').data({
            'type': 'analysis',
            'analysisid': analysisid,
            'analysisname': library + languageModel
        });
    });

    // Listener acción re-ejecutar análisis
    $('#corpora-list').on('click', '.btn-rerunAnalysis', function () {
        var analysisid = $(this).data('analysisid');
        showLoading('Re-ejecutando análisis');
        $.when(rerunAnalysis(analysisid))
            .always(function () {
                hideLoading();
            })
            .done(function () {
                showFlashMessage('success', "Análisis re-ejecutado correctamente.");
            })
            .fail(function () {
                showFlashMessage('danger', 'No se ha podido re-ejecutar el análisis indicado.');
            });
    });

    // Listener confirmación de borrar corpus/análisis
    $('.delete-confirm').on('click', function(e) {
        $confirmButton = $(this);
        $confirmButton.addClass('disabled');
        if ($confirmButton.data('type') === 'corpus') {
            var corpusName = $(this).data('corpusname');
            $.when(deleteCorpus($(this).data('corpusid')))
                .always(function () {
                    $confirmButton.removeClass('disabled');
                    $('#modal-confirm').modal('toggle');
                })
                .done(function () {
                    renderedTable.row('.selected').remove().draw(false);
                    showFlashMessage("success", "Corpus <strong>" + corpusName + "</strong> eliminado correctamente.");
                })
                .fail(function () {
                    showFlashMessage("danger", "No se ha podido eliminar el corpus <strong>" + corpusName + "</strong>.");
                })
        }
        else if ($confirmButton.data('type') === 'analysis') {
            var analysisName = $(this).data('analysisname');
            // Recuperamos el ojbeto DataTable para poder eliminar la fila de la tabla
            var tableID = $('tr.selected').closest('table').attr('id');
            var dataTable = $('#'+tableID).DataTable();
            $.when(deleteAnalysis($(this).data('analysisid')))
                .always(function () {
                    $confirmButton.removeClass('disabled');
                    $('#modal-confirm').modal('toggle');
                })
                .done(function () {
                    dataTable.row('.selected').remove().draw(false);
                    showFlashMessage("success", "Análisis <strong>" + analysisName + "</strong> eliminado correctamente.");
                })
                .fail(function () {
                    showFlashMessage("danger", "No se ha podido eliminar el análisis <strong>" + analysisName + "</strong>.");
                })
        }
    });

    // Eliminar clase .selected de la fila de la tabla tras ocultar el modal
    $('#modal-confirm').on('hidden.bs.modal', function () {
        $('tr.selected').removeClass('selected');
    });

    function deleteCorpus(corpusid) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: ctx + "/corpora/delete-corpus",
            data: JSON.stringify(corpusid),
            timeout: 5000
        }));
    }

    function switchCorpusPublic(corpusid) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: ctx + "/corpora/switchPublic",
            data: JSON.stringify(corpusid),
            timeout: 5000
        }));
    }

    function deleteAnalysis(analysisid) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: ctx + '/corpora/delete-analysis',
            data: JSON.stringify(analysisid),
            timeout: 5000
        }));
    }

    function rerunAnalysis(analysisid) {
        return Promise.resolve($.ajax({
            type: "POST",
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            url: ctx + "/corpora/rerun-analysis",
            data: JSON.stringify(analysisid),
            timeout: 5000
        }));
    }
</script>

<%@ include file="../_footer.jsp"%>
