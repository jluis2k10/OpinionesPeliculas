zingchart.defineModule('toolbar-zoom', 'plugin', function(chartJson){
    /* Create a reference to the "toolbar-zoom" object */
    var optionsObj = chartJson["toolbar-zoom"];

    if (optionsObj["visible"] == false)
        return chartJson; // Salimos sin hacer nada

    /*
    * If the 'labels' array of objects already exists, do nothing.
    * If it does not exist, initialize it as an empty array.
    * We do this to avoid obliteration of any existing labels.
    */
    chartJson.labels = chartJson.labels ? chartJson.labels : [];

    /* Push the toolbar label objects */
    chartJson.labels.push({
            "type": "rectangle",
            "id": "zoomin",
            "width": 30,
            "height": 30,
            "background-color": "#ddd",
            "background-image": "https://cdn4.iconfinder.com/data/icons/miu/22/editor_zoom-in_-20.png",
            "background-repeat": "no-repeat",
            "cursor": "hand",
            "margin-top":10,
            "margin-left":50,
            "margin-right":"auto",
            "border-width": 1,
            "border-color": "#aaa",
            "border-radius-top-left": 5,
            "border-radius-bottom-left": 5,
            "z-index": 1
        },
        {
            "type": "rectangle",
            "id": "zoomout",
            "width": 30,
            "height": 30,
            "background-color": "#ddd",
            "background-image": "https://cdn4.iconfinder.com/data/icons/miu/22/editor_zoom-out_-20.png",
            "background-repeat": "no-repeat",
            "cursor": "hand",
            "margin-top":10,
            "margin-left":80,
            "margin-right":"auto",
            "border-width": 1,
            "border-color": "#aaa",
            "z-index": 1
        },
        {
            "type": "rectangle",
            "id": "viewall",
            "width": 30,
            "height": 30,
            "background-color": "#ddd",
            "background-image": "https://cdn1.iconfinder.com/data/icons/freeline/32/eye_preview_see_seen_view-20.png",
            "background-repeat": "no-repeat",
            "cursor": "hand",
            "margin-top":10,
            "margin-left":110,
            "margin-right":"auto",
            "border-width": 1,
            "border-color": "#aaa",
            "border-radius-top-right": 5,
            "border-radius-bottom-right": 5,
            "z-index": 1
        });

    /*
    * Add label_click event listener, use the clicked label's
    * id in a switch
    */
    zingchart.label_click = function (p) {
        switch (p.labelid) {
            case "zoomin":
                zingchart.exec(p.id, "zoomin");
                break;
            case "zoomout":
                zingchart.exec(p.id, "zoomout");
                break;
            case "viewall":
                zingchart.exec(p.id, "viewall");
                break;
        }
    };

    /*
    * If the "background-color" attr exists, loop over each label and
    * modify the background-color on those with certain "id" values.
    */
    if (optionsObj["background-color"]){
        for (var n in chartJson["labels"]){
            var labelObj = chartJson["labels"][n];
            if ( (labelObj["id"] == "zoomin") || (labelObj["id"] == "zoomout") || (labelObj["id"] == "viewall") ){
                labelObj["background-color"] = optionsObj["background-color"];
            }
        }
    }
    /* Same thing as above, but for border-color.  */
    if (optionsObj["border-color"]){
        for (var n in chartJson["labels"]){
            var labelObj = chartJson["labels"][n];
            if ( (labelObj["id"] == "zoomin") || (labelObj["id"] == "zoomout") || (labelObj["id"] == "viewall") ){
                labelObj["border-color"] = optionsObj["border-color"];
            }
        }
    }

    return chartJson;
});

zingchart.defineModule('expand-time-chart', 'plugin', function (chartJson) {

    chartJson.labels = chartJson.labels ? chartJson.labels : [];
    chartJson.labels.push({
        type: 'rectangle',
        id: 'expand',
        x: '50%',
        y: 40,
        text: 'Expandir / Contraer',
        cursor: 'hand',
        fontSize: 14,
        anchor: 'c'
    });

    zingchart.label_click = function (p) {
        // Hay que añadir de nuevo los eventos para el zoom, por lo visto al añadir
        // un listener nuevo se pisan los anteriores
        switch (p.labelid) {
            case "expand":
                if (chartJson["my-series"].expanded) {
                    zingchart.exec(p.id, 'setseriesvalues', {
                        values: [
                            chartJson["my-series"].series.positives.shorten,
                            chartJson["my-series"].series.negatives.shorten,
                            chartJson["my-series"].series.percentages.shorten
                        ]
                    });
                    // Escala únicamente con etiquetas correspondientes a los días que tienen datos (comentarios)
                    setChartScaleXLabel(chartJson, chartJson["my-series"].series.labels, false);
                    zingchart.exec(p.id, 'setdata', {
                        data: chartJson
                    });
                }
                else {
                    zingchart.exec(p.id, 'setseriesvalues', {
                        values: [
                            chartJson["my-series"].series.positives.expanded,
                            chartJson["my-series"].series.negatives.expanded,
                            chartJson["my-series"].series.percentages.expanded
                        ]
                    });
                    // Escala vacía (se muestran todos los días desde el primer comentario hasta el último, aunque estén vacíos)
                    setChartScaleXLabel(chartJson, null, true);
                    zingchart.exec(p.id, 'setdata', {
                        data: chartJson
                    });
                }
                break;
            case "zoomin":
                zingchart.exec(p.id, "zoomin");
                break;
            case "zoomout":
                zingchart.exec(p.id, "zoomout");
                break;
            case "viewall":
                zingchart.exec(p.id, "viewall");
                break;
            default:
                break;
        }
    };

    return chartJson;
});

/**
 * Cambiamos campos del objeto graph (opciones para crear un gráfico en
 * zingchart)
 * @param chart         Objeto de configuración sobre el que haremos los cambios
 * @param label         Valores para las etiquetas de la escala en el eje X
 * @param isExpanded    Si se trata de un gráfico "expandido" (que muestra todas las
 *                      fechas aunque no contengan datos) o no
 */
function setChartScaleXLabel(chart, label, isExpanded) {
    chart["my-series"].expanded = isExpanded;
    chart["scale-x"].labels = label;
}

function renderSharedChart(container, corpus) {
    var opinionAnalyses = $.grep(corpus.analyses, function (analysis) {
        return analysis.type === "opinion";
    });
    var polarityAnalyses = $.grep(corpus.analyses, function (analysis) {
        return analysis.type === "polarity";
    });
    var commentHashes = $.map(corpus.comments, function(comment) {
        return comment.hash;
    });
    var opinionSeries = getAnalysesSeries(opinionAnalyses, commentHashes);
    var polaritySeries = getAnalysesSeries(polarityAnalyses, commentHashes);
    var opinionChartConfig = lineChartConfig(opinionSeries, "Índice de Subjetividad");
    var polarityChartConfig = lineChartConfig(polaritySeries, "Índice de Positividad");
    var sharedChartConfig;
    var graphHeight = 350;
    if (polaritySeries.length > 0 && opinionSeries.length > 0) {
        sharedChartConfig = {
            layout: "2x1",
            graphset: [polarityChartConfig, opinionChartConfig]
        };
        graphHeight = 700;
    }
    else if (polaritySeries.length > 0 && opinionSeries.length == 0) {
        sharedChartConfig = {
            layout: "1x1",
            graphset: [polarityChartConfig]
        };
    }
    else if (polaritySeries.length == 0 && opinionSeries.length > 0) {
        sharedChartConfig = {
            layout: "1x1",
            graphset: [opinionChartConfig]
        };
    }
    else {
        $(container).append($('<div>', {
            class: "col-12 alert alert-warning",
            role: "alert",
            html: "No se han ejecutado análisis para el Corpus."
        }));
        return;
    }
    zingchart.render({
        id: container,
        data: sharedChartConfig,
        height: graphHeight,
        width: "100%",
        modules: 'toolbar-zoom'
    });
}

function lineChartConfig(series, yAxisLabel) {
    var titleText = (yAxisLabel === 'Índice de Positividad') ? "ANÁLISIS DE SENTIMIENTO" : "ANÁLISIS DE OPINIÓN";
    return {
        title: {
            text: titleText,
            fontSize: 16,
            fontColor: 'gray',
            adjustLayout: true
        },
        'toolbar-zoom': {
            'background-color': '#FFFFFF #D0D7E1',
            'border-color': '#ACAFB6',
            'visible': (yAxisLabel === 'Índice de Positividad')
        },
        type: 'line',
        zoom: {
            shared: true
        },
        legend: {
            layout: 'x3',
            overflow: 'scroll',
            maxItems: 3,
            align: 'center',
            verticalAlign: 'top',
            backgroundColor: 'none',
            borderWidth: 0,
            item: {
                cursor: 'hand'
            },
            marker: {
                type: 'circle',
                borderWidth: 0,
                cursor: 'hand'
            }
        },
        plotarea: {
            margin: 'dynamic 70'
        },
        plot: {
            tooltip: {
                visible: false
            }
        },
        scaleX: {
            shared: true,
            zooming: true,
            zoomTo: [0, 35],
            minValue: 1,
            step: 1,
            'max-items': 10
        },
        scrollX:{

        },
        scaleY: {
            values: '0:100:10',
            label: {
                text: yAxisLabel,
                fontSize: "14px"
            },
            format: "%v%",
            markers: [{
                type:"line",
                range:[50],
                lineStyle: "dashed",
                lineWidth: 2,
                lineColor:"green"
            }]
        },
        crosshairX: {
            shared: true,
            plotLabel: {
                text: '<span style="color:%color">%t:</span> %v%',
                fontSize: '14px'
            },
            scaleLabel: {
                text: 'Comentario #%v'
            }
        },
        series: series
    };
}

function getAnalysesSeries(analyses, commentHashes) {
    var colorNames = Object.keys(window.chartColors);
    var series = [];

    $.each(analyses, function(i, analysis) {
        var colorName = colorNames[i % colorNames.length];
        var color = window.chartColors[colorName];
        var classifierName = analysis.classifier;
        if (analysis.language_model != null)
            classifierName += "<br /><em>" + analysis.language_model + "</em>";
        var serie = {
            text: classifierName,
            values: [],
            lineColor: color,
            marker: {
                backgroundColor: color
            }
        };

        var recordsMap = new Map();
        $.each(analysis.records, function(j, record) {
            recordsMap.set(record.comment_hash, record);
        });
        $.each(commentHashes, function(k, hash) {
            if (recordsMap.has(hash)) {
                var value = 0.0;
                if (analysis.type === "opinion")
                    value = Math.round(((recordsMap.get(hash).subjectiveScore) * 100) * 100) / 100;
                else if (analysis.type === "polarity")
                    value = Math.round(((recordsMap.get(hash).positiveScore) * 100) * 100) / 100;
                serie.values.push(value);
            }
            else
                serie.values.push(null);
        });
        series.push(serie);
    });
    return series;
}

function renderPieChart(containerID, comments) {
    var pieSeries = getPieSeries(comments);
    var myConfig = {
        type: 'pie',
        title: {
            text: "ANÁLISIS DE SENTIMIENTO",
            fontSize: 16,
            fontColor: 'gray'
        },
        scaleR:{
            refAngle: 180 //relative to the starting 90 degree position.
        },
        legend: {
            x: '65%',
            y: '25%',
            borderWidth: 1,
            borderColor: 'gray',
            borderRadius: '5px',
            header: {
                text: "Leyenda",
                fontFamily: 'Georgia',
                fontSize: 12,
                fontcolor: '#3333cc',
                fontWeight: 'normal'
            },
            marker: {
                type: 'circle'
            },
            toggleAction: 'remove',
            minimize: false,
            icon: {
                lineColor: '#9999ff'
            },
            maxItems: 8,
            overflow: 'scroll'
        },
        plot: {
            tooltip: {
                text: "%t: %v (%npv%)",
                fontColor: 'black',
                fontFamily: 'Arial',
                textAlpha: 1,
                backgroundColor: 'white',
                alpha: 0.7,
                borderWidth: 1,
                borderColor: '#cccccc',
                lineStyle: 'dotted',
                borderRadius: '10px',
                padding: '10%',
                placement: 'node:center',
            },
            valueBox: {
                placement: 'in',
                text: '%npv %',
                fontSize: '15px',
                textAlpha: 1,
                rules: [
                    {
                        rule: '%v === 0',
                        text: ''
                    }
                ]
            }
        },
        plotarea: {
            marginRight: '30%',
            marginTop: '15%'
        },
        series: pieSeries
    };

    zingchart.render({
        id : containerID,
        data : myConfig,
        height: 300,
        width: '100%'
    });
}

function getPieSeries(comments) {
    var positives = $.grep(comments, function (comment, index) {
        return comment.polarity === "Positive";
    }).length;
    var negatives = $.grep(comments, function(comment, index) {
        return comment.polarity === "Negative";
    }).length;
    var neutrals = $.grep(comments, function(comment, index) {
        return comment.polarity === "Neutral";
    }).length;
    return [
        {
            values: [positives],
            backgroundColor: window.chartColors['green'],
            text: "Positivos"
        },
        {
            values: [negatives],
            backgroundColor: window.chartColors['red'],
            text: "Negativos"
        },
        {
            values: [neutrals],
            backgroundColor: window.chartColors['grey'],
            text: "Neutrales"
        }
    ];
}

function renderBarChart(containerID, comments) {
    var barSeries = getBarSeries(comments);
    var myConfig = {
        type: 'hbar',
        title: {
            text: "VISIÓN GLOBAL",
            fontSize: 16,
            fontColor: 'gray'
        },
        plot: {
            stacked: true
        },
        plotarea: {
            marginLeft: 'dynamic'
        },
        legend: {
            x: '75%',
            y: '5%',
            minimize: true,
            icon: {
                lineColor: "gray"
            },
            borderWidth: 1,
            borderColor: 'gray',
            borderRadius: '5px',
            header: {
                text: "Leyenda",
                fontFamily: 'Georgia',
                fontSize: 12,
                fontcolor: '#3333cc',
                fontWeight: 'normal'
            },
            marker: {
                type: 'circle'
            },
            toggleAction: 'remove'
        },
        scaleX: {
            label: {
                text: 'Sentimiento',
                fontSize: 14,
                fontColor: 'gray',
                fontWeight: 'bold'
            },
            labels: ['Positivos', 'Negativos', 'Neutrales', 'N/A']
        },
        scaleY: {
            label: {
                text: 'Número de Comentarios',
                fontSize: 14,
                fontColor: 'gray',
                fontWeight: 'bold'
            }
        },
        series: barSeries
    };

    zingchart.render({
        id : containerID,
        data : myConfig,
        height: 300,
        width: '100%'
    });
}

function getBarSeries(comments) {
    var subjectives = $.grep(comments, function (comment) {
        return comment.opinion === 'Subjective';
    });
    var objectives = $.grep(comments, function (comment) {
        return comment.opinion === 'Objective';
    });
    var undetermined = $.grep(comments, function (comment) {
        return comment.opinion == null;
    });
    var subjectivesSerie = [
        nullIfZero($.grep(subjectives, function (subjective) {
            return subjective.polarity === 'Positive';
        }).length),
        nullIfZero($.grep(subjectives, function (subjective) {
            return subjective.polarity === 'Negative';
        }).length),
        nullIfZero($.grep(subjectives, function (subjective) {
            return subjective.polarity === 'Neutral';
        }).length),
        nullIfZero($.grep(subjectives, function (subjective) {
            return subjective.polarity == null;
        }).length)
    ];
    var objectivesSerie = [
        nullIfZero($.grep(objectives, function (objective) {
            return objective.polarity === 'Positive';
        }).length),
        nullIfZero($.grep(objectives, function (objective) {
            return objective.polarity === 'Negative';
        }).length),
        nullIfZero($.grep(objectives, function (objective) {
            return objective.polarity === 'Neutral';
        }).length),
        nullIfZero($.grep(objectives, function (objective) {
            return objective.polarity == null;
        }).length),
    ]
    var undeterminedSerie = [
        nullIfZero($.grep(undetermined, function (undet) {
            return undet.polarity === 'Positive';
        }).length),
        nullIfZero($.grep(undetermined, function (undet) {
            return undet.polarity === 'Negative';
        }).length),
        nullIfZero($.grep(undetermined, function (undet) {
            return undet.polarity === 'Neutral';
        }).length),
        nullIfZero($.grep(undetermined, function (undet) {
            return undet.polarity == null;
        }).length)
    ]

    return [
        {
            values: subjectivesSerie,
            backgroundColor: window.chartColors['green'],
            text: "Subjetivos"
        },
        {
            values: objectivesSerie,
            backgroundColor: window.chartColors['red'],
            text: "Objetivos"
        },
        {
            backgroundColor: undeterminedSerie,
            backgroundColor: window.chartColors['grey'],
            text: "Sin analizar"
        }
    ]
}

function renderTimeEvoChart(containerID, comments) {
    var series = getTimeSeries(comments);
    var myConfig = {
        'my-series': {
            series: series,
            expanded: false
        },
        type: 'mixed',
        'toolbar-zoom': {
            'background-color': '#FFFFFF #D0D7E1',
            'border-color': '#ACAFB6'
        },
        'expand-time-chart' : {},
        title: {
            text: "EVOLUCIÓN EN EL TIEMPO",
            fontSize: 16,
            fontColor: 'gray'
        },
        plot: {
            tooltip: {
                visible: false
            }
        },
        scaleX: {
            minValue: series.positives.expanded[0,0],
            labels: series.labels,
            transform: {
                type: 'date',
                all: '%d/%m/%y'
            },
            zooming: true,
            zoomTo: [0, 59]
        },
        preview: {

        },
        scrollX: {

        },
        crosshairX: {
            lineWidth: '100%',
            alpha: 0.3,
            plotLabel: {
                text: '<span style="color:%color;">%t</span>: %v',
                fontSize: '14px',
                multiple: false,
                placement: 'node-top',
                exact: true,
                trigger: 'hover'
            },
            scaleLabel: {
                transform: {
                    type: 'date',
                    all: '%D, %dd/%M/%Y'
                }
            }
        },
        scaleY: {
            maxValue: series.maxSum,
            minValue: -series.maxSum
        },
        scaleY2: {
            stackType: '100%',
            offsetStart: '50%',
            guide: {
                visible: false
            }
        },
        series: [{
            type: 'bar',
            id: 'positives',
            text: 'Comentarios Positivos',
            values: series.positives.shorten,
            backgroundColor: window.chartColors['green'],
            stacked: true,
            scales: 'scaleX, scaleY'
        }, {
            type: 'bar',
            id: 'negatives',
            text: 'Comentarios Negativos',
            values: series.negatives.shorten,
            backgroundColor: window.chartColors['red'],
            stacked: true,
            scales: 'scaleX, scaleY'
        }, {
            type: 'line',
            id: 'percentages',
            text: 'Índice de Positividad (%)',
            lineColor: window.chartColors['purple'],
            marker: {
                backgroundColor: window.chartColors['purple']
            },
            values: series.percentages.shorten,
            scales: 'scaleX, scaleY2'
        }]
    };

    zingchart.render({
        id : containerID,
        data : myConfig,
        height: 400,
        width: '100%',
        modules: 'toolbar-zoom,expand-time-chart'
    });
}

function getTimeSeries(comments) {
    var prevDate = 0, totalPositives = 0, totalNegatives = 0;
    var positivesMap = new Map(), negativesMap = new Map(), percentageMap = new Map();
    $.each(comments, function (i, comment) {
        // Ignoramos los comentarios clasificados neutrales o están sin clasificar
        if (comment.polarity === "Neutral" || comment.polarity == null)
            return true;

        var ISOdate = comment.date.split("/").reverse().join("-");
        var timeStamp = Date.parse(ISOdate);

        if (timeStamp === prevDate) {
            if (comment.polarity === "Positive") {
                totalPositives++;
                positivesMap.set(timeStamp, positivesMap.get(timeStamp) + 1);
            }
            else {
                totalNegatives++;
                negativesMap.set(timeStamp, negativesMap.get(timeStamp) - 1);
            }

        }
        else {
            prevDate = timeStamp;
            if (comment.polarity === "Positive") {
                totalPositives++;
                positivesMap.set(timeStamp, 1);
                negativesMap.set(timeStamp, 0);
            }
            else {
                totalNegatives++;
                positivesMap.set(timeStamp, 0);
                negativesMap.set(timeStamp, -1);
            }
        }
        var percentage = Math.round((totalPositives / (totalPositives + totalNegatives)) * 100);
        percentageMap.set(timeStamp, percentage);
    });

    // Convertimos los Maps a arrays 2D que es lo que necesita Zingchart
    // 'expanded': en el gráfico mostraremos todas las fechas en el eje X, contengan datos o no
    // 'shorten': en el gráfico NO mostraremos fechas que no contengan datos
    // labels: Etiquetas para el eje X del grafo, en formato unix timestamp
    // maxSum: el mayor número de comentarios (negativos o positivos) que se tienen en una de las fechas
    var positivesSeries = {expanded: [], shorten: []};
    var negativesSeries = {expanded: [], shorten: []};
    var percentagesSeries = {expanded: [], shorten: []};
    var labels = [];
    var maxSum = 0;
    positivesMap.forEach(function (val, key, map) {
        maxSum = Math.max(maxSum, val, - negativesMap.get(key));
        labels.push(key);
        positivesSeries.expanded.push([key, val]);
        negativesSeries.expanded.push([key, negativesMap.get(key)]);
        percentagesSeries.expanded.push([key, percentageMap.get(key)]);
        positivesSeries.shorten.push(val);
        negativesSeries.shorten.push(negativesMap.get(key));
        percentagesSeries.shorten.push(percentageMap.get(key));
    });
    return {
        positives: positivesSeries,
        negatives: negativesSeries,
        percentages: percentagesSeries,
        labels: labels,
        maxSum: maxSum
    };
}

function nullIfZero(number) {
    return number === 0 ? null : number;
}