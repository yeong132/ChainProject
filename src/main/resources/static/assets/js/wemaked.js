function toggleStar(element,event) {
    event.stopPropagation(); // 클릭 이벤트가 부모 요소로 전파되지 않도록 함


    if (element.classList.contains('bi-star')) {
        element.classList.remove('bi-star');
        element.classList.add('bi-star-fill');
    } else {
        element.classList.remove('bi-star-fill');
        element.classList.add('bi-star');
    }
}

// 저장 확인용 알림
    function showSuccessAlert() {
    alert('저장되었습니다.');
}

// 체크박스 진행도가 차는 자스-
function updateProgress() {// 모든 체크박스를 선택
    let checkboxes = document.querySelectorAll('.form-check-input');

    // 프로그레스 바 요소 선택
    let progressBar = document.getElementById('progress-bar');
    // 총 진행률 초기화
    let totalProgress = 0;
    // 각 체크박스를 반복하며 체크된 항목의 value 값을 더함
    checkboxes.forEach(function (checkbox) {
        if (checkbox.checked) {
            // 체크된 체크박스의 value 값을 가져와서 숫자로 변환하여 totalProgress에 더함
            console.log('Checkbox value:', checkbox.value);
            totalProgress += Number(checkbox.getAttribute('value')); // value 속성 명시적으로 가져오기
        }
    });
    // 콘솔에 총 진행률 출력
    console.log('총 진행률:', totalProgress);
    // 프로그레스 바의 너비를 총 진행률 값으로 설정
    progressBar.style.width = totalProgress + '%';
    // 프로그레스 바의 aria-valuenow 속성을 총 진행률 값으로 설정
    progressBar.setAttribute('aria-valuenow', totalProgress);
    // 프로그레스 바의 텍스트를 총 진행률 값으로 설정
    progressBar.innerText = totalProgress + '%';
}