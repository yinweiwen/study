/**
 * Created by PengLing on 2016/5/22.
 */
'use strict';

import Highcharts from 'highcharts';
import more from '../../../node_modules/highcharts/highcharts-more';
import loadExporting from '../../../node_modules/highcharts/modules/exporting';
import loadOfflineExporting from '../../../node_modules/highcharts/modules/offline-exporting';

let module = null;

function getHighcharts() {
    if (module != null) {
        return module;
    }

    more(Highcharts);
    loadExporting(Highcharts);
    loadOfflineExporting(Highcharts);

    Highcharts.SparkLine = function (a, b, c) {
        var hasRenderToArg = typeof a === 'string' || a.nodeName,
            options = arguments[hasRenderToArg ? 1 : 0],
            defaultOptions = {
                chart: {
                    renderTo: (options.chart && options.chart.renderTo) || this,
                    backgroundColor: null,
                    borderWidth: 0,
                    type: 'area',
                    margin: [2, 0, 2, 0],
                    height: 20,
                    style: {
                        overflow: 'visible'
                    },
                    skipClone: true
                },
                title: {
                    text: ''
                },
                credits: {
                    enabled: false
                },
                xAxis: {
                    labels: {
                        enabled: false
                    },
                    title: {
                        text: null
                    },
                    startOnTick: false,
                    endOnTick: false,
                    tickPositions: []
                },
                yAxis: {
                    endOnTick: false,
                    startOnTick: false,
                    labels: {
                        enabled: false
                    },
                    title: {
                        text: null
                    },
                    tickPositions: [0]
                },
                legend: {
                    enabled: false
                },
                tooltip: {
                    backgroundColor: null,
                    borderWidth: 0,
                    shadow: false,
                    useHTML: true,
                    hideDelay: 0,
                    shared: true,
                    padding: 0,
                    positioner: function (w, h, point) {
                        return { x: point.plotX - w / 2, y: point.plotY - h };
                    }
                },
                plotOptions: {
                    series: {
                        animation: false,
                        lineWidth: 1,
                        shadow: false,
                        states: {
                            hover: {
                                lineWidth: 1
                            }
                        },
                        marker: {
                            radius: 1,
                            states: {
                                hover: {
                                    radius: 2
                                }
                            }
                        },
                        fillOpacity: 0.25
                    },
                    column: {
                        negativeColor: '#910000',
                        borderColor: 'silver'
                    }
                }
            };

        options = Highcharts.merge(defaultOptions, options);

        return hasRenderToArg ?
            new Highcharts.Chart(a, options, c) :
            new Highcharts.Chart(options, b);
    };

    Highcharts.setOptions({
        global: {
            useUTC: false // 关闭UTC
        },
        lang: {
            contextButtonTitle: '图表菜单',
            printChart: '打印图表',
            downloadJPEG: '下载JPEG图片',
            downloadPDF: '下载PDF文档',
            downloadPNG: '下载PNG图片',
            downloadSVG: '下载SVG矢量图',
            resetZoom: '图形重置',
            resetZoomTitle: '重置缩放比例'
        }
    });

    module = Highcharts;
    return Highcharts;
}

export default getHighcharts;