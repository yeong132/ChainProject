// 1. main.js : 새로고침시, 초기 차트 설정 및 라디오 갱신
document.addEventListener('DOMContentLoaded', function () {
    // 초기 차트 설정 및 연도 선택기 이벤트 리스너 설정 추가
    const initialChart = ChartModuleHome.initChart(); // 초기 차트 데이터 제공
    initialChart.render();

    $.ajax({
        url: '/chart/data',
        method: 'GET',
        dataType: 'json',
        success: function (response) {
            console.log('서버로부터 받은 데이터:', response);

            const chartData = response.charts;
            const projectData = response.projects;

            // 연도 선택기 초기화
            populateYearSelector('year-selector', chartData);

            // 페이지 로드 시 home 1번 라디오 버튼의 값으로 차트를 업데이트
            ChartModuleHome.updateChart(initialChart, '1', new Date().getFullYear(), chartData, projectData);

            // 차트 라디오 버튼 이벤트 리스너 설정
            EventListenerModule.attachChartRadioListeners(initialChart, chartData, projectData);

            // 연도 선택기 이벤트 리스너 설정
            EventListenerModule.attachYearSelectorListener(initialChart, chartData, projectData);
        },
        error: function (xhr, status, error) {
            console.error('차트 데이터를 가져오는데 실패했습니다:', error);
        }
    });
});


// 연도 선택기를 초기화하는 함수
function populateYearSelector(selectorId, chartData) {
    const years = new Set();
    chartData.forEach(entity => {
        const year = new Date(entity.chartStartDate).getFullYear();
        years.add(year);
    });

    const selector = document.getElementById(selectorId);
    years.forEach(year => {
        const option = document.createElement('option');
        option.value = year;
        option.textContent = year;
        selector.appendChild(option);
    });

    // 기본값을 현재 연도로 설정
    const currentYear = new Date().getFullYear();
    if (years.has(currentYear)) {
        selector.value = currentYear;
    }
}

// 2. chartModule.js : 라디오 차트 생성 및 업데이트
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

    function updateChart(chart, category, year, chartEntities) {
        const yearlyData = calculateProgressData(chartEntities, false).yearlyData;
        const dataForYear = yearlyData[year];
        if (dataForYear) {
            let newData = [];
            let colors = [];
            let achievedCounts = [];

            if (category === '1') {
                // '부서' 월간 달성률 (누적 달성률)
                newData = dataForYear.monthlyData;
                achievedCounts = dataForYear.achievedCounts;
                colors = ['#93e6b7']; // 연두색 통일
            } else if (category === '2') {
                // '부서' 월별 진행률 (각 진행도에 따른 비율 계산)
                const distributionData = calculateProgressDistribution(chartEntities.filter(entity => entity.chartCategory === '부서')).yearlyData[year];
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
            console.error('유효하지 않은 데이터: 해당 년도의 데이터가 없습니다.');
        }
    }

    return {
        initChart,
        updateChart
    };
})();
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

    function updateChart(chart, category, year, chartEntities) {
        const yearlyData = calculateProgressData(chartEntities, false).yearlyData;
        const dataForYear = yearlyData[year];
        if (dataForYear) {
            let newData = [];
            let colors = [];
            let achievedCounts = [];

            if (category === '3') {
                // '개인' 월간 달성률 (누적 달성률)
                newData = dataForYear.monthlyData;
                achievedCounts = dataForYear.achievedCounts;
                colors = ['#eed348']; // 노란색 통일
            } else if (category === '4') {
                // '개인' 월별 진행률 (각 진행도에 따른 비율 계산)
                const distributionData = calculateProgressDistribution(chartEntities.filter(entity => entity.chartCategory === '개인')).yearlyData[year];
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
            console.error('유효하지 않은 데이터: 해당 년도의 데이터가 없습니다.');
        }
    }

    return {
        initChart,
        updateChart
    };
})();

// 3. eventListenerModule.js : 라디오 버튼에 맞는 탭 차트 업데이트
const EventListenerModule = (function (ChartModuleHome, ChartModuleProfile) {
    function attachChartRadioListeners(chart, chartEntities, projectData) {
        document.querySelectorAll('.form-check-input').forEach(input => {
            input.addEventListener('change', event => {
                const tab = event.target.dataset.tab;
                const category = event.target.value;
                const selectedYear = document.querySelector('#year-selector').value; // 연도 선택

                if (tab === 'home') {
                    ChartModuleHome.updateChart(chart, category, selectedYear, chartEntities, projectData);
                } else if (tab === 'profile') {
                    ChartModuleProfile.updateChart(chart, category, selectedYear, chartEntities, projectData);
                }
            });
        });
    }

    function attachYearSelectorListener(chart, chartEntities, projectData) {
        document.getElementById('year-selector').addEventListener('change', event => {
            const selectedYear = event.target.value;
            const activeRadio = document.querySelector('.form-check-input[data-tab].form-check-input:checked');
            const category = activeRadio ? activeRadio.value : '1'; // 기본값 '1' (home tab의 첫 번째 옵션)

            const tab = activeRadio.dataset.tab;

            if (tab === 'home') {
                ChartModuleHome.updateChart(chart, category, selectedYear, chartEntities, projectData);
            } else if (tab === 'profile') {
                ChartModuleProfile.updateChart(chart, category, selectedYear, chartEntities, projectData);
            }
        });
    }

    return {
        attachChartRadioListeners,
        attachYearSelectorListener
    };
})(ChartModuleHome, ChartModuleProfile);


// 4. chartDataCalculation.js : 라디오 차트 계산
function calculateProgressData(chartEntities, isCumulative) {
    const yearlyData = {};

    chartEntities.forEach(entity => {
        const startDate = new Date(entity.chartStartDate);
        const year = startDate.getFullYear();
        const month = startDate.getMonth();

        if (!yearlyData[year]) {
            yearlyData[year] = {monthlyData: Array(12).fill(0), totalCounts: Array(12).fill(0), achievedCounts: Array(12).fill(0)};
        }

        yearlyData[year].totalCounts[month] += 1;
        if (entity.noticePinned) {
            yearlyData[year].achievedCounts[month] += 1;
        }

        if (isCumulative) {
            let cumulativeTotal = 0;
            let cumulativeAchieved = 0;

            for (let i = 0; i < 12; i++) {
                cumulativeTotal += yearlyData[year].totalCounts[i];
                cumulativeAchieved += yearlyData[year].achievedCounts[i];
                yearlyData[year].monthlyData[i] = cumulativeTotal > 0 ? (cumulativeAchieved / cumulativeTotal) * 100 : 0;
            }
        } else {
            for (let i = 0; i < 12; i++) {
                yearlyData[year].monthlyData[i] = yearlyData[year].totalCounts[i] > 0 ? (yearlyData[year].achievedCounts[i] / yearlyData[year].totalCounts[i]) * 100 : 0;
            }
        }
    });

    return {yearlyData};
}
function calculateProgressDistribution(chartEntities) {
    const yearlyData = {};

    chartEntities.forEach(entity => {
        const startDate = new Date(entity.chartStartDate);
        const year = startDate.getFullYear();
        const month = startDate.getMonth(); // 월 인덱스 (0 = January)

        if (!yearlyData[year]) {
            yearlyData[year] = {monthlyData: Array.from({length: 12}, () => Array(6).fill(0)), totalCounts: Array(12).fill(0)};
        }

        yearlyData[year].totalCounts[month] += 1; // 각 월의 목표 개수 증가

        const progressIndex = Math.floor(entity.chartProgress / 20); // 진행도를 20% 단위로 나누기
        if (progressIndex >= 0 && progressIndex < 6) {
            yearlyData[year].monthlyData[month][progressIndex] += 1; // 각 진행도 구간에 목표 추가
        }
    });

    for (const year in yearlyData) {
        for (let i = 0; i < 12; i++) {
            if (yearlyData[year].totalCounts[i] > 0) {
                for (let j = 0; j < 6; j++) {
                    yearlyData[year].monthlyData[i][j] = (yearlyData[year].monthlyData[i][j] / yearlyData[year].totalCounts[i]) * 100; // 비율 계산
                }
            }
        }
    }

    return {yearlyData};
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



