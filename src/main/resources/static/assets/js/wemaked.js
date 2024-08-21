// Froala Editor 한국어 적용
var editor = new FroalaEditor('#froala', {
    language: 'ko',
});

// 출퇴근 Modal용
document.addEventListener('DOMContentLoaded', function () {
    function updateTime(elementId) {
        const currentTimeElement = document.getElementById(elementId);
        const now = new Date();
        const hours = now.getHours().toString().padStart(2, '0');
        const minutes = now.getMinutes().toString().padStart(2, '0');
        const seconds = now.getSeconds().toString().padStart(2, '0');
        currentTimeElement.textContent = `현재 시간: ${hours}:${minutes}:${seconds}`;
    }

    function setupModal(modalId, confirmButtonId, cancelButtonId, message, switchInput, checkedState) {
        const modalElement = document.getElementById(modalId);
        const confirmButton = document.getElementById(confirmButtonId);
        const cancelButton = document.getElementById(cancelButtonId);

        $(modalElement).on('show.bs.modal', function () {
            updateTime(modalId === 'attendanceModal' ? 'currentTime' : 'currentTimes');
        });

        confirmButton.addEventListener('click', function () {
            switchInput.checked = checkedState;
            alert(message);
        });

        cancelButton.addEventListener('click', function () {
            switchInput.checked = !checkedState;
        });
    }

    const switchInput = document.getElementById('flexSwitchCheckDefault');
    const commuteIcon = document.getElementById('commuteIcon');

    commuteIcon.addEventListener('click', function () {
        if (switchInput.checked) {
            $('#attendanceModal').modal('show');
        } else {
            $('#leaveworkModal').modal('show');
        }
    });

    setupModal('attendanceModal', 'attendanceConfirmButton', 'attendanceCancelButton', '출근되었습니다!', switchInput, true);
    setupModal('leaveworkModal', 'leaveworkConfirmButton', 'leaveworkCancelButton', '퇴근되었습니다!', switchInput, false);

    setInterval(function () {
        updateTime('currentTime');
        updateTime('currentTimes');
    }, 1000);
});

// 즐겨찾기
function toggleStar(element, event) {
    event.stopPropagation(); // 클릭 이벤트가 부모 요소로 전파되지 않도록 함

    const projectFavoriteInput = document.getElementById('projectFavorite');
    if (element.classList.contains('bi-star')) {
        element.classList.remove('bi-star');
        element.classList.add('bi-star-fill');
        projectFavoriteInput.value = "true";
    } else {
        element.classList.remove('bi-star-fill');
        element.classList.add('bi-star');
        projectFavoriteInput.value = "false";
    }
    // 해당 폼 제출
    element.nextElementSibling.submit();
}

// 저장 확인용 알림
function showSuccessAlert() {
    alert('저장되었습니다.');
}

//  그래프 저장 자스
document.addEventListener('DOMContentLoaded', function () {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            const data = myChart.data.datasets[0].data;
            const index = Array.from(checkboxes).indexOf(checkbox);
            data[index] = checkbox.checked ? parseInt(checkbox.value) : 0;
            myChart.update();
        });
    });
});

// 체크박스 진행도가 차는 자스-
function updateProgress() {
    const checkboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    const totalProgress = Array.from(checkboxes).reduce((acc, checkbox) => acc + parseInt(checkbox.value), 0);
    const progressBar = document.getElementById('progress-bar');
    progressBar.style.width = totalProgress + '%';
    progressBar.setAttribute('aria-valuenow', totalProgress);
    progressBar.textContent = totalProgress + '%';
    // 숨겨진 입력 필드를 총 진행률 값으로 업데이트
    document.getElementById('projectProgress').value = totalProgress;
}


// <button onClick="chatOpenPopup()">메신저</button>


// 메신저 팝업창 오픈
function chatOpenPopup() {
    // 팝업창 크기 설정
    let width = 700;
    let height = 550;
    // 팝업창 위치 설정
    let left = Math.ceil((window.screen.width - width) / 2);
    let top = Math.ceil((window.screen.height - height) / 2);

    // 팝업창 옵션
    popupSize = window.open('/chatting', 'chat', `toolbar=no, menubar=no, scrollbars=no, resizable=no, width=${width}, height=${height}, left=${left}, top=${top}`);

    // 팝업창 크기 고정
    if (popupSize) {
        popupSize.onresize = () => {
            popupSize.resizeTo(width, height);
        }
    }
}

// 테이블에 링크 적용하기
function rowClick(url) {
    window.location.href = url;
}


