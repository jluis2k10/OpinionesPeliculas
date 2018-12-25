function check_empty(array) {
    var empty = true;
    $.each(array, function (i, obj) {
        empty = empty && $.isEmptyObject(obj);
    });
    return empty;
}


/* Definimos colores para gráficas */
window.chartColors = {
    red: 'rgb(255, 99, 132)',
    orange: 'rgb(255, 159, 64)',
    yellow: 'rgb(255, 205, 86)',
    green: 'rgb(75, 192, 192)',
    blue: 'rgb(54, 162, 235)',
    purple: 'rgb(153, 102, 255)',
    grey: 'rgb(201, 203, 207)'
};

zingchart.MODULESDIR = "/js/zingchart/modules/";

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

    return chartJson;
});

zingchart.defineModule('change-scatter', 'plugin', function(chartJson){
    chartJson.labels = chartJson.labels ? chartJson.labels : [];
    chartJson.labels.push({
        type: 'rectangle',
        marginTop: 100,
        marginLeft: 20,
        text: 'Mostrar por:',
        fontSize: 14,
    }, {
        type: 'rectangle',
        id: 'positivos',
        width: 30,
        height: 30,
        backgroundColor: "#ddd",
        backgroundImage: "/img/smile.png",
        backgroundRepeat: "no-repeat",
        cursor: "hand",
        marginTop:95,
        marginLeft:105,
        marginRight:"auto",
        borderWidth: 1,
        borderColor: "#aaa",
        borderTopLeftRadius: 5,
        borderBottomLeftRadius: 5,
        zIndex: 1
    }, {
        type: "rectangle",
        id: 'negativos',
        width: 30,
        height: 30,
        backgroundColor: "#ddd",
        backgroundImage: "/img/frown.png",
        backgroundRepeat: "no-repeat",
        cursor: "hand",
        marginTop:95,
        marginLeft:135,
        marginRight:"auto",
        borderWidth: 1,
        borderColor: "#aaa",
        zIndex: 1
    }, {
        type: "rectangle",
        id: 'neutrales',
        width: 30,
        height: 30,
        backgroundColor: "#ddd",
        backgroundImage: "/img/meh.png",
        backgroundRepeat: "no-repeat",
        cursor: "hand",
        marginTop: 95,
        marginLeft:165,
        marginRight:"auto",
        borderWidth: 1,
        borderColor: "#aaa",
        borderTopLeftRadius: 5,
        borderBottomRightRadius: 5,
        zIndex: 1
    });

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
    chart["scaleX"].labels = label;
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
    if (!check_empty(polaritySeries) && !check_empty(opinionSeries)) {
        sharedChartConfig = {
            layout: "2x1",
            graphset: [polarityChartConfig, opinionChartConfig]
        };
        graphHeight = 700;
    }
    else if (!check_empty(polaritySeries) && check_empty(opinionSeries)) {
        sharedChartConfig = {
            layout: "1x1",
            graphset: [polarityChartConfig]
        };
    }
    else if (check_empty(polaritySeries) && !check_empty(opinionSeries)) {
        sharedChartConfig = {
            layout: "1x1",
            graphset: [opinionChartConfig]
        };
    }
    else {
        sharedChartConfig = {
            layout: "1x1",
            graphset: [polarityChartConfig]
        };
    }
    zingchart.render({
        id: container,
        data: sharedChartConfig,
        height: graphHeight,
        width: "100%",
        modules: 'toolbar-zoom'
    });
    zingchart.bind(container, 'label_click', function(p) {
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
        };
    });
}

function renderDomainChart(container, corpus) {
    if (!corpus.domain_analysis)
        return;
    var domainSeries = getDomainSeries(corpus);
    var domainChartConfig = getDomainChartConfig(domainSeries);
    zingchart.render({
        id: container,
        data: domainChartConfig,
        height: 300,
        width: "100%"
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
        noData:{
            text: "Datos no disponibles",
            backgroundColor: 'white'
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
            visible: !check_empty(series),
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

function getDomainChartConfig(series) {
    return {
        type: "bar",
        noData:{
            text: "Datos no disponibles",
            visible: true
        },
        title: {
            text: "ANÁLISIS DE DOMINIO",
            fontSize: 16,
            fontColor: 'gray'
        },
        tooltip: {
            text: '%t: %v comentarios',
            textAlign: 'left'
        },
        scaleX: {
            visible: false
        },
        scaleY: {
            label: {
                text: 'Comentarios totales',
                fontSize: "14px"
            }
        },
        plotarea: {
            margin: 'dynamic 70'
        },
        legend: {
            layout: 'x5',
            marginTop: '35px',
            overflow: 'page',
            maxItems: 5,
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
        series: series
    }
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
    if (series.length > 0)
        return series;
    return [{}];
}

function getDomainSeries(corpus) {
    var colorNames = Object.keys(window.chartColors);
    var series = [];
    var commentsMap = new Map();
    $.each(corpus.comments, function (i, comment) {
        if (commentsMap.has(comment.domain))
            commentsMap.set(comment.domain, commentsMap.get(comment.domain) + 1);
        else
            commentsMap.set(comment.domain, 1);
    });
    var i = 0;
    // No hay análisis de dominio, devolvemos array vacío
    if (commentsMap.size === 1 && commentsMap.has(null))
        return [];
    commentsMap.forEach(function (value, domain) {
        var color = window.chartColors[colorNames[i % colorNames.length]];
        var serie = {
            text: domain,
            values: [domain != null ? value : null],
            backgroundColor: color
        };
        series.push(serie);
        i++;
    });
    return series;
}

function renderGlobalChart(containerID, comments) {
    var series = getGlobalChartSeries(comments);
    var polaritySeries = series.splice(0, 4);
    var opinionSeries = series;

    var myConfig = {
        layout: '1x2',
        graphset: [
            {
                type: 'pie',
                noData:{
                    text: "Datos no disponibles",
                    visible: check_empty(polaritySeries),
                    backgroundColor: 'white'
                },
                backgroundColor: 'none',
                x: '0%',
                y: '0%',
                scaleR: {
                    refAngle: 180 // relativo a los 90 grados del punto inicial
                },
                legend: {
                    visible: !check_empty(opinionSeries),
                    x: '105%',
                    y: '60%',
                    borderWidth: 1,
                    borderColor: 'gray',
                    borderRadius: '5px',
                    header: {
                        text: "Opinión",
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
                    maxItems: 4,
                    overflow: 'scroll'
                },
                plot: {
                    slice: '70%',
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
                series: opinionSeries
            },
            {
                type: 'pie',
                noData:{
                    text: "Datos no disponibles",
                    visible: check_empty(opinionSeries),
                    backgroundColor: 'white'
                },
                title: {
                    text: "VISIÓN GENERAL",
                    fontSize: 16,
                    fontColor: 'gray',
                    x: '50%'
                },
                backgroundColor: 'none',
                x: '0%',
                y: '0%',
                scale: {
                    sizeFactor: 0.6
                },
                scaleR: {
                    refAngle: 180
                },
                legend: {
                    visible: !check_empty(polaritySeries),
                    x: '105%',
                    y: '15%',
                    borderWidth: 1,
                    borderColor: 'gray',
                    borderRadius: '5px',
                    header: {
                        text: "Polaridad",
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
                    maxItems: 4,
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
                series: polaritySeries
            },
        ]
    };

    zingchart.loadModules('patterns');
    zingchart.render({
        id: containerID,
        data: myConfig,
        height: 300,
        width: '100%'
    });
}

function getGlobalChartSeries(comments) {
    var positives = 0, negatives = 0, neutrals = 0, subjectives = 0, objectives = 0;
    $.each(comments, function (i, comment) {
        if (comment.polarity === "Positive")
            positives++;
        else if (comment.polarity === "Negative")
            negatives++;
        else if (comment.polarity == "Neutral")
            neutrals++;
        if (comment.opinion === "Objective")
            objectives++;
        else if (comment.opinion == "Subjective")
            subjectives++;
    });
    var polarityRest = comments.length - (positives + negatives + neutrals);
    var opinionRest = comments.length - (subjectives + objectives);

    var polaritySeries, opinionSeries;
    if (positives === 0 && negatives === 0 && neutrals === 0) {
        polaritySeries = [{}, {}, {}, {}];
    }
    else {
        polaritySeries = [
            {
                values: [positives > 0 ? positives : null],
                backgroundColor: window.chartColors['green'],
                text: "Positivos"
            },
            {
                values: [negatives > 0 ? negatives : null],
                backgroundColor: window.chartColors['red'],
                text: "Negativos"
            },
            {
                values: [neutrals > 0 ? neutrals : null],
                backgroundColor: window.chartColors['yellow'],
                text: "Neutrales"
            },
            {
                values: [polarityRest > 0 ? polarityRest : null],
                backgroundColor: window.chartColors['grey'],
                text: "Sin analizar"
            }
        ]
    }
    if (subjectives === 0 && objectives === 0) {
        opinionSeries = [{}, {}, {}]
    }
    else {
        opinionSeries = [
            {
                values: [subjectives > 0 ? subjectives : null],
                backgroundColor: window.chartColors['green'],
                backgroundImage: 'PATTERN_SHADE_25',
                text: "Subjetivos"
            },
            {
                values: [objectives > 0 ? objectives : null],
                backgroundColor: window.chartColors['red'],
                backgroundImage: 'PATTERN_SHADE_25',
                text: "Objetivos"
            },
            {
                values: [opinionRest > 0 ? opinionRest : null],
                backgroundColor: window.chartColors['grey'],
                backgroundImage: 'PATTERN_SHADE_25',
                text: "Sin analizar"
            }
        ]
    }
    return polaritySeries.concat(opinionSeries);
}

function renderDomainPieChart(containerID, comments) {
    var domainSeries = getDomainSeries(comments);
    var showLegend = !check_empty(domainSeries);
    var myConfig = {
        type: 'pie',
        noData:{
            text: "Datos no disponibles",
            visible: true,
            backgroundColor: 'none'
        },
        title: {
            text: 'ANÁLISIS DE DOMINIO',
            fontSize: 16,
            fontColor: 'gray'
        },
        scaleR: {
            refAngle: 180
        },
        legend: {
            visible: showLegend,
            x: '65%',
            y: '25%',
            borderWidth: 1,
            borderColro: 'gray',
            borderRadius: '5px',
            header: {
                text: "Dominio",
                fontFamily: 'Georgia',
                fontSize: 12,
                fontColor: '#3333cc',
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
            maxItems: 7,
            overflow: 'scroll'
        },
        plot: {
            tooltip: {
                text: "%t: %v comentarios",
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
                placement: 'out',
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
        series: domainSeries
    };

    var chart = zingchart.render({
        id: containerID,
        data: myConfig,
        height: 300,
        width: '100%'
    });
    //console.log(chart);
}


function renderTimeEvoChart(containerID, comments) {
    var series = getTimeSeries(comments);
    var myConfig = {
        'my-series': {
            series: series,
            expanded: false
        },
        type: 'mixed',
        noData:{
            text: "Datos no disponibles",
            visible: true
        },
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

    zingchart.bind(containerID, 'label_click', function(p) {
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
        };
    });

    zingchart.bind(containerID, 'label_click', function(p) {
        switch (p.labelid) {
            case "expand":
                if (myConfig["my-series"].expanded) {
                    // Escala únicamente con etiquetas correspondientes a los días que tienen datos (comentarios)
                    setChartScaleXLabel(myConfig, myConfig["my-series"].series.labels, false);
                    zingchart.exec(p.id, 'setdata', {
                        data: myConfig
                    });
                    zingchart.exec(p.id, 'setseriesvalues', {
                        values: [
                            myConfig["my-series"].series.positives.shorten,
                            myConfig["my-series"].series.negatives.shorten,
                            myConfig["my-series"].series.percentages.shorten
                        ]
                    });
                }
                else {
                    // Escala vacía (se muestran todos los días desde el primer comentario hasta el último, aunque estén vacíos)
                    setChartScaleXLabel(myConfig, null, true);
                    zingchart.exec(p.id, 'setdata', {
                        data: myConfig
                    });
                    zingchart.exec(p.id, 'setseriesvalues', {
                        values: [
                            myConfig["my-series"].series.positives.expanded,
                            myConfig["my-series"].series.negatives.expanded,
                            myConfig["my-series"].series.percentages.expanded
                        ]
                    });
                }
                break;
        }
    })
}

function getTimeSeries(comments) {
    var prevDate = 0, totalPositives = 0, totalNegatives = 0;
    var positivesMap = new Map(), negativesMap = new Map(), percentageMap = new Map();
    $.each(comments, function (i, comment) {
        // Ignoramos los comentarios clasificados neutrales o sin clasificar
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

function renderScatterChart(containerID, corpus) {
    var series = getScatterSeries(corpus);
    var displaySeries = [];
    $.each(series, function (i, serie) {
        displaySeries.push(serie.positives);
    });
    var myConfig = {
        type: "scatter",
        noData:{
            text: "Datos no disponibles",
            visible: true
        },
        title: {
            text: "DISPERSIÓN SENTIMIENTO/SUBJETIVIDAD",
            fontSize: 16,
            fontColor: 'gray',
            adjustLayout: true
        },
        'change-scatter': {
            fullSeries: series
        },
        plotarea: {
            marginLeft: 'dynamic'
        },
        scaleX: {
            values: '0:100:10',
            label: {
                text: "Índice de Subjetividad",
                fontSize: "14px"
            },
            format: "%v%",
            offsetStart: "0%"
        },
        scaleY: {
            values: '0:100:10',
            label: {
                text: "Índice de Positividad",
                fontSize: "14px"
            },
            format: "%v%",
            offsetStart: "0%"
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
        series: displaySeries
    };

    zingchart.render({
        id : containerID,
        modules: 'change-scatter',
        data : myConfig,
        height: 600,
        width: '100%'
    });

    zingchart.bind(containerID, 'label_click', function(p) {
        displaySeries = [];
        switch (p.labelid) {
            case "positivos":
                $.each(series, function (i, serie) {
                    displaySeries.push(serie.positives);
                });
                zingchart.exec(p.id, 'setseriesdata', {
                    graphid : 0,
                    data : displaySeries
                });
                zingchart.exec(p.id, 'modify', {
                    graphid : 0,
                    data : {
                        scaleY: {
                            label: {
                                text: "Índice de Positividad",
                            },
                        },
                    }
                });
                break;
            case "negativos":
                $.each(series, function (i, serie) {
                    displaySeries.push(serie.negatives);
                });
                zingchart.exec(p.id, 'setseriesdata', {
                    graphid : 0,
                    data : displaySeries
                });
                zingchart.exec(p.id, 'modify', {
                    graphid : 0,
                    data : {
                        scaleY: {
                            label: {
                                text: "Índice de Negatividad",
                            },
                        },
                    }
                });
                break;
            case "neutrales":
                $.each(series, function (i, serie) {
                    displaySeries.push(serie.neutrals);
                });
                zingchart.exec(p.id, 'setseriesdata', {
                    graphid : 0,
                    data : displaySeries
                });
                zingchart.exec(p.id, 'modify', {
                    graphid : 0,
                    data : {
                        scaleY: {
                            label: {
                                text: "Índice de Neutralidad",
                            },
                        },
                    }
                });
                break;
        };
    });
}

function getScatterSeries(corpus) {
    var opinions = [], polarityAnalyses = [];
    var mediaPolarities = new Map();
    $.each(corpus.comments, function (i, comment) {
        if (comment.opinion != null) {
            opinions.push({
                comment_hash: comment.hash,
                opinion_score: Math.round10(comment.opinionScore * 100, -2)
            });
        }
        if (comment.polarity != null) {
            mediaPolarities.set(comment.hash, {
                polarity: comment.polarity,
                positivity: Math.round10(nullIfZero(comment.positivityScore) * 100, -2),
                negativity: Math.round10(nullIfZero(comment.negativityScore) * 100, -2),
                neutrality: Math.round10(nullIfZero(comment.neutralityScore) * 100, -2)
            })
        }
    });
    polarityAnalyses.push({
        classifier_name: "Media",
        language_model: null,
        records: mediaPolarities
    });

    // Creamos mapas para cada análisis de sentimiento
    var pAnalyses = $.grep(corpus.analyses, function (analysis) {
        return analysis.type === "polarity";
    });
    $.each(pAnalyses, function (i, analysis) {
        var analysisPolarities = new Map();
        $.each(analysis.records, function(j, record) {
            analysisPolarities.set(record.comment_hash, {
                polarity: record.polarity,
                positivity: Math.round10(nullIfZero(record.positiveScore) * 100, -2),
                negativity: Math.round10(nullIfZero(record.negativeScore) * 100, -2),
                neutrality: Math.round10(nullIfZero(record.neutralScore) * 100, -2)
            })
        });
        polarityAnalyses.push({
            classifier_name: analysis.classifier,
            language_model: analysis.language_model,
            records: analysisPolarities
        });
    });

    // Creamos las series. Serán de la forma:
    // series[x] = {
    //                  classifier_name: "Clasificador",
    //                  language_model: "Modelo de Lenguaje",
    //                  positives: [
    //                                  [subjectivty_score, positivity_score],
    //                                  [subjectivty_score, positivity_score],
    //                                  ...
    //                             ],
    //                  negatives: [
    //                                  [subjectivty_score, negativity_score],
    //                                  [subjectivty_score, negativity_score],
    //                                  ...
    //                             ],
    //                  neutrals: [
    //                                  [subjectivty_score, neutrality_score],
    //                                  [subjectivty_score, neutrality_score],
    //                                  ...
    //                            ]
    //             }

    var series = [];
    $.each(polarityAnalyses, function (i, polarityAnalysis) {
        var legendText = polarityAnalysis.classifier_name;
        if (polarityAnalysis.language_model != null)
            legendText += "<br /><em>" + polarityAnalysis.language_model + "</em>";
        var serie = {
            classifier_name: polarityAnalysis.classifier_name,
            language_model: polarityAnalysis.language_model,
            positives: {
                values: [],
                legendText: legendText
            },
            negatives: {
                values: [],
                legendText: legendText
            },
            neutrals: {
                values: [],
                legendText: legendText
            }
        };

        $.each(opinions, function (j, opinion) {
            if (polarityAnalysis.records.has(opinion.comment_hash)) {
                serie.positives.values.push([opinion.opinion_score, polarityAnalysis.records.get(opinion.comment_hash).positivity]);
                serie.negatives.values.push([opinion.opinion_score, polarityAnalysis.records.get(opinion.comment_hash).negativity]);
                serie.neutrals.values.push([opinion.opinion_score, polarityAnalysis.records.get(opinion.comment_hash).neutrality]);
            }
        });

        series.push(serie);
    });
    return series;
}

function nullIfZero(number) {
    return number === 0 ? null : number;
}