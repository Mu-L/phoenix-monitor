/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'element', 'form'], function () {
        var admin = layui.admin, $ = layui.$, form = layui.form;
        // 基于准备好的dom，初始化echarts实例
        var getJvmMemoryHeapInfoChart = echarts.init(document.getElementById('get-jvm-memory-heap-info'), 'infographic');
        var getJvmMemoryNonHeapInfoChart = echarts.init(document.getElementById('get-jvm-memory-non-heap-info'), 'infographic');
        var getJvmMemoryPoolInfo = echarts.init(document.getElementById('get-jvm-memory-pool-info'), 'infographic');
        // 浏览器窗口大小发生改变时
        window.addEventListener("resize", function () {
            getJvmMemoryHeapInfoChart.resize();
            getJvmMemoryNonHeapInfoChart.resize();
            getJvmMemoryPoolInfo.resize();
        });
        // 时间条件发生改变
        form.on('select(time1)', function (data) {
            var time = data.value;
            // 发送ajax请求，获取内存使用量数据
            getJvmMemoryInfo(time, 'Heap', 'Heap内存使用量');
            getJvmMemoryInfo(time, 'Non_Heap', 'Non_Heap内存使用量');
        });
        // 内存池图表时间
        var chartTime = 'all';
        // 内存池图表内存类型
        var chartPool = $('#chart option:selected').val();
        // 图表条件发生改变
        form.on('select(chart)', function (data) {
            // 内存池类型
            chartPool = data.value;
            // 发送ajax请求，获取内存使用量数据
            getJvmMemoryInfo(chartTime, chartPool, chartPool + '内存使用量');
        });
        form.on('select(time2)', function (data) {
            // 时间
            chartTime = data.value;
            // 发送ajax请求，获取内存使用量数据
            getJvmMemoryInfo(chartTime, chartPool, chartPool + '内存使用量');
        });

        // 发送ajax请求，获取内存使用量数据
        function getJvmMemoryInfo(time, memoryType, title) {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-jvm-memory/get-jvm-memory-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    instanceId: instanceId, // 应用实例ID
                    time: time, // 时间
                    memoryType: memoryType// 内存类型
                },
                success: function (result) {
                    var data = result.data;
                    // 时间
                    var time = data.map(function (item) {
                        return item.insertTime;
                    });
                    // 使用量
                    var used = data.map(function (item) {
                        return item.used;
                    });
                    // 提交内存量
                    var committed = data.map(function (item) {
                        return item.committed;
                    });
                    // 初始内存值
                    var init = data[0] != undefined ? data[0].init : '无数据';
                    // 最大内存量
                    var max = data[0] != undefined ? data[0].max : '无数据';
                    var option = {
                        title: {
                            text: title,
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: '初始内存值：' + init + '，最大内存量：' + max,
                            subtextStyle: {
                                color: '#BEBEBE'
                            }
                        },
                        // 鼠标移到折线上展示数据
                        tooltip: {
                            trigger: 'axis'
                        },
                        legend: {
                            data: ['使用量', '提交量'],
                            left: 'right',
                        },
                        grid: {
                            left: '150px',
                            right: '10px'
                        },
                        xAxis: [{
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: time
                        }],
                        yAxis: {
                            type: 'value',
                            name: '使用量',
                            axisLabel: {
                                formatter: '{value} Mb'
                            }
                        },
                        // 数据
                        series: [{
                            name: '使用量',
                            data: used,
                            type: 'line',
                            smooth: true,
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#87CEEB'
                                    }, {
                                        offset: 0.5,
                                        color: '#ADD8E6'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#5F9EA0'
                                }
                            }
                        }, {
                            name: '提交量',
                            data: committed,
                            type: 'line',
                            smooth: true,
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#EEE5DE'
                                    }, {
                                        offset: 0.5,
                                        color: '#FFF5EE'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#CDC5BF'
                                }
                            }
                        }]
                    };
                    if (memoryType === 'Heap') {
                        getJvmMemoryHeapInfoChart.setOption(option);
                        window.onresize = getJvmMemoryHeapInfoChart.resize;
                    } else if (memoryType === 'Non_Heap') {
                        getJvmMemoryNonHeapInfoChart.setOption(option);
                        window.onresize = getJvmMemoryNonHeapInfoChart.resize;
                    } else {
                        getJvmMemoryPoolInfo.setOption(option);
                        window.onresize = getJvmMemoryPoolInfo.resize;
                    }
                }
            });
        }

        // 发送ajax请求，获取内存使用量数据
        getJvmMemoryInfo('all', 'Heap', 'Heap内存使用量');
        getJvmMemoryInfo('all', 'Non_Heap', 'Non_Heap内存使用量');
        getJvmMemoryInfo('all', chartPool, chartPool + '内存使用量');
        // 每30秒刷新一次
        window.setInterval(function () {
            // 发送ajax请求，获取内存使用量数据
            getJvmMemoryInfo('all', 'Heap', 'Heap内存使用量');
            getJvmMemoryInfo('all', 'Non_Heap', 'Non_Heap内存使用量');
            getJvmMemoryInfo('all', chartPool, chartPool + '内存使用量');
        }, 1000 * 30);
    });
    e('instanceDetail', {});
});