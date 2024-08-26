    // 서버에서 차트 데이터 가져오기
    document.addEventListener('DOMContentLoaded', function () {
        $.ajax({
            url: '/chart/data', // 차트 데이터를 제공하는 엔드포인트
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                console.log('서버로부터 받은 데이터:', data);

                // 차트 초기화 및 렌더링
                const initialData = ChartModule.initChart([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]);
                initialData.render();

                // 목표 비교 기능 초기화
                EventListenerModule.attachChartRadioListeners(initialData, data);
            },
            error: function(xhr, status, error) {
                console.error('차트 데이터를 가져오는데 실패했습니다:', error);
            }
        });
    });



// 1. chartModule.js
// 데이터 값에 따른 색상을 반환하는 함수
function getColor(value) {
    if (value >= 80) return '#f16fc7';
    if (value >= 60) return '#eed348';
    return '#93e6b7';
}
    const ChartModule = (function () {
        function initChart() {
            return new ApexCharts(document.querySelector("#barChart"), {
                series: [], // 빈 데이터로 초기화
                chart: {
                    type: 'bar',
                    height: 350,
                    stacked: true // 스택드 바 차트로 설정
                },
                plotOptions: {
                    bar: {
                        borderRadius: 5,
                        horizontal: true
                    }
                },
                dataLabels: {
                    enabled: false
                },
                xaxis: {
                    categories: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
                    max: 100
                },
                yaxis: {
                    max: 100
                },
                fill: {
                    opacity: 1
                },
                tooltip: {
                    y: {
                        formatter: function (value, { series, seriesIndex, dataPointIndex, w }) {
                            const percentage = w.config.series[seriesIndex].name;
                            const totalValue = w.globals.seriesTotals[dataPointIndex];
                            const count = totalValue > 0 ? Math.round((value / 100) * totalValue) : 0; // 건수를 계산하여 표시
                            return `${percentage} : ${count}건`;
                        }
                    }
                }

            });
        }

        function updateChart(chart, tab, category, chartEntities) {
            if (chartEntities && chartEntities.length > 0) {
                let newData = [];

                if (tab === 'home' && category === '1') {
                    // 월간 달성률 (누적 달성률)
                    newData = calculateProgressData(chartEntities, false);
                } else if (tab === 'home' && category === '2') {
                    // 월별 진행률 (각 진행도에 따른 비율 계산)
                    newData = calculateProgressDistribution(chartEntities);

                    const series = [
                        { name: '0%', data: newData.map(item => item[0]) },
                        { name: '20%', data: newData.map(item => item[1]) },
                        { name: '40%', data: newData.map(item => item[2]) },
                        { name: '60%', data: newData.map(item => item[3]) },
                        { name: '80%', data: newData.map(item => item[4]) },
                        { name: '100%', data: newData.map(item => item[5]) }
                    ];

                    chart.updateOptions({
                        series: series,
                        colors: ['#f16fc7', '#eed348', '#93e6b7', '#e4b8ff', '#58d68d', '#3498db']
                    });
                    return;
                } else {
                    console.error('유효하지 않은 데이터: 탭 또는 카테고리를 찾을 수 없습니다');
                    return;
                }

                chart.updateOptions({
                    series: [{
                        data: newData,
                        color: function ({ value }) {
                            return getColor(value);
                        }
                    }]
                });
            } else {
                console.error('유효하지 않은 데이터: chartEntities가 정의되지 않았거나 비어 있습니다.');
            }
        }

        return {
            initChart,
            updateChart
        };
    })();



// 2. eventListenerModule.js
const EventListenerModule = (function (ChartModule) {
    // 차트 라디오 버튼에 이벤트 리스너를 붙이는 함수
    function attachChartRadioListeners(chart, chartEntities) {
        document.querySelectorAll('.form-check-input').forEach(input => {
            input.addEventListener('change', event => {
                const tab = event.target.dataset.tab;
                const category = event.target.value;
                ChartModule.updateChart(chart, tab, category, chartEntities);
            });
        });
    }

    return {
        attachChartRadioListeners
    };
})(ChartModule);

// 3. modalModule.js
const ModalModule = (function () {
    // flatpickr 초기화 함수 (기간 설정을 위한 날짜 선택 라이브러리)
    function initFlatpickr() {
        const chartPeriodInput = document.getElementById('chartPeriod');
        const chartStartDateInput = document.getElementById('chartStartDate');
        const chartEndDateInput = document.getElementById('chartEndDate');

        flatpickr(chartPeriodInput, {
            mode: "range",
            dateFormat: "Y-m-d",
            onChange: function (selectedDates, dateStr) {
                const dates = dateStr.split(" to ");
                chartStartDateInput.value = dates[0];
                chartEndDateInput.value = dates[1];
            }
        });
    }

    // 목표 차트 생성 모달 열기
    function showCreateChartModal() {
        const goalChartModal = new bootstrap.Modal(document.getElementById('goalChartModal'));
        goalChartModal.show();
    }

    // 목표 비교 차트 모달 열기
    function showCompareChartModal() {
        const compareChartModal = new bootstrap.Modal(document.getElementById('compareChartModal'));
        compareChartModal.show();
    }

    return {
        initFlatpickr,
        showCreateChartModal,
        showCompareChartModal
    };
})();

// 4. goalComparisonModule.js
const GoalComparisonModule = (function () {
    let selectedGoals = [];

    // 목표 비교 그래프 모달 초기화 및 이벤트 리스너 추가
    function initGoalComparison(goals) {
        const goalList = document.getElementById('goalList');
        goalList.innerHTML = goals.map(goal => `
            <div class="form-check">
                <input class="form-check-input" type="checkbox" value="${goal.id}" id="goal-${goal.id}">
                <label class="form-check-label" for="goal-${goal.id}">${goal.name}</label>
            </div>
        `).join('');

        goals.forEach(goal => {
            document.getElementById(`goal-${goal.id}`).addEventListener('change', function () {
                if (this.checked) {
                    if (selectedGoals.length < 3) {
                        selectedGoals.push(goal);
                    } else {
                        this.checked = false;
                        alert('최대 3개의 목표만 선택할 수 있습니다.');
                    }
                } else {
                    selectedGoals = selectedGoals.filter(g => g.id !== goal.id);
                }

                document.getElementById('selectedGoals').innerHTML = selectedGoals.map(g => `<li>${g.name}</li>`).join('');
            });
        });

        document.getElementById('compareButton').addEventListener('click', function () {
            if (selectedGoals.length === 0) {
                alert('비교할 목표를 선택하세요.');
                return;
            }

            const chartNames = selectedGoals.map(goal => goal.name);
            const progressData = selectedGoals.map(goal => ({name: goal.name, data: goal.progress}));

            const compareChart = new ApexCharts(document.querySelector("#compareChart"), {
                series: progressData,
                chart: {
                    type: 'bar',
                    height: 350
                },
                plotOptions: {
                    bar: {
                        horizontal: false,
                        columnWidth: '55%',
                        endingShape: 'rounded'
                    }
                },
                dataLabels: {
                    enabled: false
                },
                stroke: {
                    show: true,
                    width: 2,
                    colors: ['transparent']
                },
                xaxis: {
                    categories: chartNames,
                },
                yaxis: {
                    title: {
                        text: '% (퍼센트)'
                    }
                },
                fill: {
                    opacity: 1
                },
                tooltip: {
                    y: {
                        formatter: function (val) {
                            return val + "%"
                        }
                    }
                }
            });

            compareChart.render();
        });

        document.getElementById('searchGoal').addEventListener('input', function () {
            const query = this.value.toLowerCase();
            const filteredGoals = goals.filter(goal => goal.name.toLowerCase().includes(query));

            goalList.innerHTML = filteredGoals.map(goal => `
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="${goal.id}" id="goal-${goal.id}">
                    <label class="form-check-label" for="goal-${goal.id}">${goal.name}</label>
                </div>
            `).join('');

            filteredGoals.forEach(goal => {
                document.getElementById(`goal-${goal.id}`).addEventListener('change', function () {
                    if (this.checked) {
                        if (selectedGoals.length < 3) {
                            selectedGoals.push(goal);
                        } else {
                            this.checked = false;
                            alert('최대 3개의 목표만 선택할 수 있습니다.');
                        }
                    } else {
                        selectedGoals = selectedGoals.filter(g => g.id !== goal.id);
                    }

                    document.getElementById('selectedGoals').innerHTML = selectedGoals.map(g => `<li>${g.name}</li>`).join('');
                });
            });
        });
    }

    return {
        initGoalComparison
    };
})();

// 5. main.js
    document.addEventListener('DOMContentLoaded', function () {
        // 차트 초기화 및 이벤트 리스너 설정
        const initialData = ChartModule.initChart([0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]); // 초기 차트 데이터 제공
        initialData.render();

        // 서버에서 차트 데이터를 가져와서 적용
        $.ajax({
            url: '/chart/data',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                console.log('서버로부터 받은 데이터:', data);

                EventListenerModule.attachChartRadioListeners(initialData, data);
            },
            error: function(xhr, status, error) {
                console.error('차트 데이터를 가져오는데 실패했습니다:', error);
            }
        });
    });



// 6. chartDataCalculation.js // 월별 진행도
    function calculateProgressData(chartEntities, isCumulative) {
        const monthlyData = Array(12).fill(0);
        const totalCounts = Array(12).fill(0); // 월별 목표 전체 개수
        const achievedCounts = Array(12).fill(0); // 월별 달성된 목표 개수

        chartEntities.forEach(entity => {
            const startDate = new Date(entity.chartStartDate); // 목표 시작일을 Date 객체로 변환
            const month = startDate.getMonth(); // 시작일의 월을 구함 (0이 January, 11이 December)

            totalCounts[month] += 1; // 해당 월의 전체 목표 개수를 증가시킴

            if (entity.noticePinned) { // 목표가 달성되었는지 여부를 확인 (noticePinned이 true이면 달성된 것)
                achievedCounts[month] += 1; // 해당 월의 달성된 목표 개수를 증가시킴
            }
        });

        if (isCumulative) {
            let cumulativeTotal = 0;
            let cumulativeAchieved = 0;

            for (let i = 0; i < 12; i++) {
                cumulativeTotal += totalCounts[i];
                cumulativeAchieved += achievedCounts[i];
                monthlyData[i] = cumulativeTotal > 0 ? (cumulativeAchieved / cumulativeTotal) * 100 : 0;
            }
        } else {
            for (let i = 0; i < 12; i++) {
                monthlyData[i] = totalCounts[i] > 0 ? (achievedCounts[i] / totalCounts[i]) * 100 : 0;
            }
        }

        return monthlyData;
    }

    function calculateProgressDistribution(chartEntities) {
        const monthlyData = Array.from({ length: 12 }, () => Array(6).fill(0)); // 12개월, 6개 진행도 구간
        const totalCounts = Array(12).fill(0); // 월별 목표 전체 개수

        chartEntities.forEach(entity => {
            const startDate = new Date(entity.chartStartDate);
            const month = startDate.getMonth(); // 시작일의 월을 구함 (0이 January, 11이 December)

            totalCounts[month] += 1; // 해당 월의 전체 목표 개수 증가

            const progressIndex = Math.floor(entity.chartProgress / 20); // 0, 20, 40, 60, 80, 100을 인덱스로 사용
            if (progressIndex >= 0 && progressIndex < 6) { // progressIndex가 0에서 5 사이에 있는지 확인
                monthlyData[month][progressIndex] += 1; // 해당 진행도 구간에 속하는 개수 증가
            }
        });

        for (let i = 0; i < 12; i++) {
            if (totalCounts[i] > 0) {
                for (let j = 0; j < 6; j++) {
                    monthlyData[i][j] = (monthlyData[i][j] / totalCounts[i]) * 100; // 전체 대비 비율로 변환
                }
            }
        }

        return monthlyData;
    }


