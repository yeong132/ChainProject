// 1. main.js : 새로고침시, 초기 차트 설정 및 라디오 갱신
document.addEventListener('DOMContentLoaded', function () {
    // ChartModuleHome 1번 값으로 초기 차트 설정
    const initialChart = ChartModuleHome.initChart(); // 초기 차트 데이터 제공
    initialChart.render();

    // 서버에서 차트 데이터를 가져와서 적용
    $.ajax({
        url: '/chart/data',
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            console.log('서버로부터 받은 데이터:', data);

            // 페이지 로드 시 home 1번 라디오 버튼의 값으로 차트를 업데이트
            ChartModuleHome.updateChart(initialChart, '1', data);

            // 차트 라디오 버튼 이벤트 리스너 설정
            EventListenerModule.attachChartRadioListeners(initialChart, data);
        },
        error: function (xhr, status, error) {
            console.error('차트 데이터를 가져오는데 실패했습니다:', error);
        }
    });
});

// 2. chartModule.js : 라디오 차트 생성 및 업데이트
// 라디오 1번 2번
const ChartModuleHome = (function () {
    function initChart() {
        return new ApexCharts(document.querySelector("#barChart"), {
            series: [],
            chart: {
                type: 'bar',
                height: 350,
                stacked: true,
                toolbar: {
                    show: false
                }
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
                    formatter: function (value, {seriesIndex, dataPointIndex, w}) {
                        const actualCount = w.globals.series[seriesIndex].data[dataPointIndex];
                        return `${actualCount}건`;
                    }
                }
            }
        });
    }

    function updateChart(chart, category, chartEntities) {
        if (chartEntities && chartEntities.length > 0) {
            let newData = [];
            let colors = [];
            let achievedCounts = [];

            if (category === '1') {
                // '부서' 월간 달성률 (누적 달성률)
                const progressData = calculateProgressData(chartEntities.filter(entity => entity.chartCategory === '부서'), false);
                newData = progressData.monthlyData;
                achievedCounts = progressData.achievedCounts;
                colors = ['#93e6b7']; // 연두색 통일
            } else if (category === '2') {
                // '부서' 월별 진행률 (각 진행도에 따른 비율 계산)
                const distributionData = calculateProgressDistribution(chartEntities.filter(entity => entity.chartCategory === '부서'));
                newData = distributionData.monthlyData;
                achievedCounts = distributionData.totalCounts; // 여기서 achievedCounts는 totalCounts로 대체됩니다.

                const series = [
                    {name: '0%', data: newData.map(item => item[0])},
                    {name: '20%', data: newData.map(item => item[1])},
                    {name: '40%', data: newData.map(item => item[2])},
                    {name: '60%', data: newData.map(item => item[3])},
                    {name: '80%', data: newData.map(item => item[4])},
                    {name: '100%', data: newData.map(item => item[5])}
                ];

                colors = ['#f16fc7', '#eed348', '#93e6b7', '#e4b8ff', '#58d68d', '#3498db'];

                chart.updateOptions({
                    series: series,
                    colors: colors,
                    tooltip: {
                        y: {
                            formatter: function (value, {dataPointIndex}) {
                                return `${achievedCounts[dataPointIndex]}건`; // 실제 데이터 개수 출력
                            }
                        }
                    }
                });
                return;
            } else {
                console.error('유효하지 않은 데이터: 카테고리를 찾을 수 없습니다');
                return;
            }

            chart.updateOptions({
                series: [{
                    data: newData
                }],
                colors: colors,
                tooltip: {
                    y: {
                        formatter: function (value, {dataPointIndex}) {
                            return `${achievedCounts[dataPointIndex]}건`; // 실제 데이터 개수 출력
                        }
                    }
                }
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
// 라디오 3번 4번
const ChartModuleProfile = (function () {
    function initChart() {
        return new ApexCharts(document.querySelector("#barChart"), {
            series: [],
            chart: {
                type: 'bar',
                height: 350,
                stacked: true,
                toolbar: {
                    show: false
                }
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
                    formatter: function (value, {seriesIndex, dataPointIndex, w}) {
                        const actualCount = w.globals.series[seriesIndex].data[dataPointIndex];
                        return `${actualCount}건`;
                    }
                }
            }
        });
    }

    function updateChart(chart, category, chartEntities) {
        if (chartEntities && chartEntities.length > 0) {
            let newData = [];
            let colors = [];
            let achievedCounts = [];

            if (category === '3') {
                // '개인' 월간 달성률 (누적 달성률)
                const progressData = calculateProgressData(chartEntities.filter(entity => entity.chartCategory === '개인'), false);
                newData = progressData.monthlyData;
                achievedCounts = progressData.achievedCounts;
                colors = ['#eed348']; // 노란색 통일
            } else if (category === '4') {
                // '개인' 월별 진행률 (각 진행도에 따른 비율 계산)
                const distributionData = calculateProgressDistribution(chartEntities.filter(entity => entity.chartCategory === '개인'));
                newData = distributionData.monthlyData;
                achievedCounts = distributionData.totalCounts; // 여기서 achievedCounts는 totalCounts로 대체됩니다.

                const series = [
                    {name: '0%', data: newData.map(item => item[0])},
                    {name: '20%', data: newData.map(item => item[1])},
                    {name: '40%', data: newData.map(item => item[2])},
                    {name: '60%', data: newData.map(item => item[3])},
                    {name: '80%', data: newData.map(item => item[4])},
                    {name: '100%', data: newData.map(item => item[5])}
                ];

                colors = ['#f16fc7', '#eed348', '#93e6b7', '#e4b8ff', '#58d68d', '#3498db'];

                chart.updateOptions({
                    series: series,
                    colors: colors,
                    tooltip: {
                        y: {
                            formatter: function (value, {dataPointIndex}) {
                                return `${achievedCounts[dataPointIndex]}건`; // 실제 데이터 개수 출력
                            }
                        }
                    }
                });
                return;
            } else {
                console.error('유효하지 않은 데이터: 카테고리를 찾을 수 없습니다');
                return;
            }

            chart.updateOptions({
                series: [{
                    data: newData
                }],
                colors: colors,
                tooltip: {
                    y: {
                        formatter: function (value, {dataPointIndex}) {
                            return `${achievedCounts[dataPointIndex]}건`; // 실제 데이터 개수 출력
                        }
                    }
                }
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

// 3. eventListenerModule.js : 라디오 버튼에 맞는 탭 차트 업데이트
const EventListenerModule = (function (ChartModuleHome, ChartModuleProfile) {
    function attachChartRadioListeners(chart, chartEntities) {
        document.querySelectorAll('.form-check-input').forEach(input => {
            input.addEventListener('change', event => {
                const tab = event.target.dataset.tab;
                const category = event.target.value;

                if (tab === 'home') {
                    ChartModuleHome.updateChart(chart, category, chartEntities);
                } else if (tab === 'profile') {
                    ChartModuleProfile.updateChart(chart, category, chartEntities);
                }
            });
        });
    }

    return {
        attachChartRadioListeners
    };
})(ChartModuleHome, ChartModuleProfile);

// 4. chartDataCalculation.js : 라디오 차트 계산
function calculateProgressData(chartEntities, isCumulative) {
    const monthlyData = Array(12).fill(0);
    const totalCounts = Array(12).fill(0);
    const achievedCounts = Array(12).fill(0);

    chartEntities.forEach(entity => {
        const startDate = new Date(entity.chartStartDate);
        const month = startDate.getMonth();

        totalCounts[month] += 1;

        if (entity.noticePinned) {
            achievedCounts[month] += 1;
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

    return {monthlyData, achievedCounts};
}

function calculateProgressDistribution(chartEntities) {
    const monthlyData = Array.from({length: 12}, () => Array(6).fill(0));
    const totalCounts = Array(12).fill(0);

    chartEntities.forEach(entity => {
        const startDate = new Date(entity.chartStartDate);
        const month = startDate.getMonth(); // 월 인덱스 (0 = January)

        totalCounts[month] += 1; // 각 월의 목표 개수 증가

        const progressIndex = Math.floor(entity.chartProgress / 20); // 진행도를 20% 단위로 나누기
        if (progressIndex >= 0 && progressIndex < 6) {
            monthlyData[month][progressIndex] += 1; // 각 진행도 구간에 목표 추가
        }
    });

    for (let i = 0; i < 12; i++) {
        if (totalCounts[i] > 0) {
            for (let j = 0; j < 6; j++) {
                monthlyData[i][j] = (monthlyData[i][j] / totalCounts[i]) * 100; // 비율 계산
            }
        }
    }

    return {monthlyData, totalCounts};
}


// 5. modalModule.js : 모달 차트 관리 (모달 초기화)
const ModalModule = (function () {

    function showCreateChartModal() {
        const goalChartModal = new bootstrap.Modal(document.getElementById('goalChartModal'));
        goalChartModal.show();
    }

    function showCompareChartModal() {
        const compareChartModal = new bootstrap.Modal(document.getElementById('compareChartModal'));
        compareChartModal.show();
    }

    return {
        showCreateChartModal,
        showCompareChartModal
    };
})();






// 6. 비교 차트 모달
const GoalComparisonModule = (function () {
    let selectedGoals = []; // 선택된 목표를 담을 배열
    let compareChart = null;
    let allGoals = []; // 모든 목표를 저장할 배열

// 선택 항목 초기화 함수
    function resetSelection() {
        selectedGoals = []; // 선택한 목표 목록 초기화
        document.querySelectorAll('.goal-checkbox').forEach(checkbox => {
            checkbox.checked = false; // 모든 체크박스 해제
        });
        updateSelectedGoalsList(); // 선택한 목표 목록 업데이트

        if (compareChart) {
            compareChart.destroy(); // 기존 차트가 있으면 파괴
            compareChart = null; // 차트를 초기화
        }
    }

    // 체크박스 리스너 초기화 함수
    function initGoalCheckboxListeners() {
        document.querySelectorAll('.goal-checkbox').forEach(checkbox => {
            checkbox.addEventListener('change', function () {
                const goalId = parseInt(this.value); // goalId를 숫자로 변환

                if (this.checked) {
                    if (selectedGoals.length < 3) {
                        selectedGoals.push(goalId);
                    } else {
                        this.checked = false;
                        alert('최대 3개의 목표만 선택할 수 있습니다.');
                    }
                } else {
                    selectedGoals = selectedGoals.filter(id => id !== goalId);
                }

                updateSelectedGoalsList();  // 선택한 목표 목록 업데이트
            });
        });
    }

    // 선택한 목표 목록을 업데이트하는 함수
    function updateSelectedGoalsList() {
        const selectedGoalsList = document.getElementById('selectedGoals');
        selectedGoalsList.innerHTML = ''; // 기존 내용을 지움

        selectedGoals.forEach(goalId => {
            $.ajax({
                url: `/chart/detail/${goalId}`, // 각 goalId에 대해 개별 요청
                method: 'GET',
                success: function (response) {
                    const goalName = response.chartName; // 예시로 가져온 정보 중 이름을 사용
                    const listItem = document.createElement('li');
                    listItem.textContent = goalName;
                    selectedGoalsList.appendChild(listItem);
                },
                error: function (xhr, status, error) {
                    console.error('차트 정보를 가져오는 데 실패했습니다:', error);
                }
            });
        });
    }

    // 선택한 목표들을 비교하는 함수
    function compareSelectedGoals() {
        if (selectedGoals.length === 0) {
            alert('비교할 목표를 선택하세요.');
            return;
        }

        const goalIds = selectedGoals.map(goalId => goalId);
        $.ajax({
            url: '/chart/compare',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(goalIds), // goalIds 배열을 서버로 전송
            success: function (response) {
                renderComparisonChart(response);
            },
            error: function () {
                alert('목표 데이터를 불러오는 데 실패했습니다.');
            }
        });
    }

    // 차트 설정
    function renderComparisonChart(response) {
        const comparisonData = response.map(chart => chart.chartProgress);
        const goalNames = response.map(chart => chart.chartName);
        const goalDurations = response.map(chart => `${chart.chartStartDate} ~ ${chart.chartEndDate}`);

        const chartElement = document.getElementById('compareChart');

        if (compareChart) {
            compareChart.destroy();
        }

        compareChart = new ApexCharts(chartElement, {
            series: [{
                name: 'Progress',
                data: comparisonData
            }],
            chart: {
                type: 'bar',
                height: 350,
                toolbar: {
                    show: false
                }
            },
            colors: ['#386ad7', '#ce1c6a', '#c0b434'], // 막대 색상 지정
            xaxis: {
                categories: goalNames
            },
            plotOptions: {
                bar: {
                    horizontal: false,
                    columnWidth: '50%',
                    distributed: true // 각 막대의 색상을 다르게 지정
                }
            },
            dataLabels: {
                enabled: false
            },
            yaxis: {
                title: {
                    text: ''
                },
                max: 100
            },
            tooltip: {
                y: {
                    formatter: function (value, {dataPointIndex}) {
                        return `${goalDurations[dataPointIndex]}`; // 툴팁에 기간 출력
                    }
                }
            },
            legend: {
                show: true,
                markers: {
                    fillColors: ['#386ad7', '#ce1c6a', '#c0b434'] // 범례 색상 지정
                }
            }
        });

        compareChart.render();
    }

    // 검색어를 기반으로 목표 목록을 필터링하는 함수
    function filterGoals(query) {
        const filteredGoals = allGoals.filter(goal => goal.chartName.toLowerCase().includes(query.toLowerCase()));
        renderGoalList(filteredGoals);
    }

    // 필터링된 목표 목록을 렌더링하는 함수
    function renderGoalList(goals) {
        const goalTableBody = document.getElementById('goalTableBody');
        goalTableBody.innerHTML = ''; // 기존 내용을 지움

        goals.forEach(chart => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td><input class="form-check-input goal-checkbox" type="checkbox" value="${chart.chartNo}" data-goal-name="${chart.chartName}"></td>
                <td>${chart.chartName}</td>
                <td>${chart.chartCategory}</td>
                <td>${chart.chartProgress}%</td>
                <td>${chart.chartStartDate}</td>
                <td>${chart.chartEndDate}</td>
            `;
            goalTableBody.appendChild(tr);
        });

        // 체크박스 리스너 다시 초기화
        initGoalCheckboxListeners();
    }

    function initGoalComparison() {
        initGoalCheckboxListeners(); // 처음 페이지 로드 시 체크박스 리스너 초기화

        document.getElementById('compareButton').addEventListener('click', compareSelectedGoals);

        document.getElementById('searchGoal').addEventListener('input', function () {
            const query = this.value;
            filterGoals(query); // 검색어에 맞게 목표를 필터링
        });
    }

    // 목표 데이터를 로드하고 allGoals에 저장하는 함수
    function loadGoals(page) {
        $.ajax({
            url: `/chart/goals?page=${page}&size=${itemsPerPage}`,
            method: 'GET',
            success: function (response) {
                allGoals = response.content; // 전체 목표 데이터를 저장
                renderGoalList(allGoals); // 전체 목표를 렌더링
            },
            error: function (xhr, status, error) {
                console.error('목표 데이터를 불러오는 데 실패했습니다.', error);
            }
        });
    }

    return {
        initGoalComparison,
        initGoalCheckboxListeners,  // 외부에서 호출할 수 있도록 공개
        compareSelectedGoals,       // 외부에서 호출할 수 있도록 공개
        loadGoals,                  // 외부에서 호출할 수 있도록 공개
        resetSelection              // 선택 초기화 함수를 외부에서 호출할 수 있도록 공개
    };
})();
// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function () {
    GoalComparisonModule.initGoalComparison();

// 취소 버튼 클릭 시 선택 초기화
    document.getElementById('resetButton').addEventListener('click', function () {
        GoalComparisonModule.resetSelection(); // 선택 초기화 함수 호출
    });

    // 목표 데이터를 로드하고 페이지네이션 설정
    $.ajax({
        url: '/chart/goalCount',
        method: 'GET',
        success: function(response) {
            initPagination(response.totalItems); // 전체 아이템 수를 기반으로 페이지네이션 초기화
            GoalComparisonModule.loadGoals(currentPage); // 첫 페이지의 목표 목록 로드
        },
        error: function(xhr, status, error) {
            console.error('목표 데이터를 가져오는데 실패했습니다.', error);
        }
    });
});


// 7. 페이지네이션 관련 변수 및 함수
let currentPage = 1;
const itemsPerPage = 5;

// 페이지네이션 초기화
function initPagination(totalItems) {
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const paginationElement = document.getElementById('pagination');

    paginationElement.innerHTML = ''; // 기존 페이지네이션 초기화

    for (let i = 1; i <= totalPages; i++) {
        const li = document.createElement('li');
        li.classList.add('page-item');
        li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
        li.addEventListener('click', function () {
            currentPage = i;
            loadGoals(currentPage);
        });
        paginationElement.appendChild(li);
    }
}

// 목표 목록 로드
function loadGoals(page) {
    $.ajax({
        url: `/chart/goals?page=${page}&size=${itemsPerPage}`,
        method: 'GET',
        success: function (response) {
            const goalTableBody = document.getElementById('goalTableBody');
            goalTableBody.innerHTML = '';

            response.content.forEach(chart => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td><input class="form-check-input goal-checkbox" type="checkbox" value="${chart.chartNo}" data-goal-name="${chart.chartName}"></td>
                    <td>${chart.chartName}</td>
                    <td>${chart.chartCategory}</td>
                    <td>${chart.chartProgress}%</td>
                    <td>${chart.chartStartDate}</td>
                    <td>${chart.chartEndDate}</td>
                `;
                goalTableBody.appendChild(tr);
            });

            GoalComparisonModule.initGoalCheckboxListeners(); // 체크박스 리스너 다시 초기화
        },
        error: function (xhr, status, error) {
            console.error('목표 데이터를 불러오는 데 실패했습니다.', error);
        }
    });
}



