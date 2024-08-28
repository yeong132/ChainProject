// <button onClick="chatOpenPopup()">메신저</button>
let stompClient = null; // 소켓 클라이언트 객체 생성
const empNo = sessionStorage.getItem('empNo');
const chatAlarm = document.querySelector('#chatAlarm'); // 채팅 알람
let selectedEmpNo = null; // 채팅 선택된 사원
let chatWindow = null; // 팝업창 객체
let unreadMessages = {}; // 안 읽은 메시지를 사용자별로 저장
let logout = document.querySelector("#logout");
let reconnectAttempts = 0; // 소켓 재연결 시도 횟수
let maxReconnectAttempts = 5; // 최대 재연결 시도 횟수
let reconnectInterval = 5000; // 재연결 시도 간격(밀리초)

// 소켓 연결
document.addEventListener('DOMContentLoaded', function() {
    connectSocket(); // 소켓 연결 시도
});

function connectSocket() {
    if (empNo && !stompClient) {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        // 소켓 연결 시도
        stompClient.connect({}, onConnected, onError);
    }
}
// 부모 창이 닫히거나 새로고침될 때 자식 창 닫기
window.addEventListener('beforeunload', function() {
    if (chatWindow && !chatWindow.closed) {
        chatWindow.close();
    }
});

// 소켓 연결(로그인) 성공
function onConnected() {
    reconnectAttempts = 0; // 연결 성공 시, 재연결 시도 횟수 초기화
    stompClient.subscribe(`/user/${empNo}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // 접속한 사용자 등록: 사용자 컨트롤러로 사용자 정보 전달
    stompClient.send("/app/user.addUser", {}, JSON.stringify(empNo));
    console.log("소켓 연결 성공");

    displayUnreadMessages(); // 로그인 시 읽지 않은 메시지 불러오기
}

// 소켓 연결 끊김
function onError() {
    console.error('WebSocket 서버에 연결할 수 없습니다. 다시 연결을 시도하는 중...');
    if (reconnectAttempts < maxReconnectAttempts) {
        reconnectAttempts++;
        setTimeout(() => {
            connectSocket(); // 재연결 시도
        }, reconnectInterval);
    } else {
        console.error('MWebSocket 서버가 종료되었습니다. 재연결하려면 새로고침하세요.');
    }
}

// 수신 메시지 처리
async function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);
    const senderEmpNo = message.senderEmpNo;

    // 현재 선택된 사원의 채팅창이 열려 있을 경우
    if (selectedEmpNo === senderEmpNo) {
        if (chatWindow && !chatWindow.closed) {
            chatWindow.postMessage(message, '*'); // 자식창에 메시지 전달
            // 자식창이 활성화된 경우 알림 수 감소
            if (document.hasFocus()) {
                delete unreadMessages[senderEmpNo];
                updateUserNotification(); // ㅇㅇㅇ자식창에게 전달?
            } else {
                incrementUnreadMessages(senderEmpNo);
            }
        }
    } else {
        // 선택되지 않은 사원의 메시지일 경우 알림 업데이트
        incrementUnreadMessages(senderEmpNo);
    }
    // 자식 창이 활성화되어 있지 않으면 알람 수 업데이트
    if (chatWindow && !chatWindow.closed) {
        chatWindow.postMessage({ type: 'UPDATE_ALARM', unreadMessages }, '*');
    }
}


// 로그인 시 읽지 않은 메시지 불러오기
async function displayUnreadMessages() {
    try {
        const unreadMessagesResponse = await fetch(`/messages/unread?recipientEmpNo=${empNo}`);
        let messages = await unreadMessagesResponse.json();

        // 안 읽은 메시지를 사용자별로 그룹화
        messages.forEach(message => {
            const senderEmpNo = message.senderEmpNo;
            incrementUnreadMessages(senderEmpNo);
        });
        // updateUserNotification();

        // 자식 창이 열려 있을 경우 알람 상태 전달
        if (chatWindow && !chatWindow.closed) {
            chatWindow.postMessage({ type: 'UPDATE_ALARM', unreadMessages }, '*');
        }
    } catch (error) {
        console.error('Error fetching unread messages:', error);
    }
}

// 알림 수 증가 및 업데이트
function incrementUnreadMessages(senderEmpNo) {
    if (!unreadMessages[senderEmpNo]) {
        unreadMessages[senderEmpNo] = 0;
    }
    unreadMessages[senderEmpNo]++;
    updateUserNotification();
}

// 안 읽은 채팅 수 카운트 및 UI 업데이트
function updateUserNotification() {
    let currentCount = 0;

    // 모든 안 읽은 메시지의 합계를 계산
    for (let empNo in unreadMessages) {
        currentCount += unreadMessages[empNo];
    }

    if (currentCount > 0) {
        chatAlarm.textContent = currentCount.toString();
        chatAlarm.classList.remove('hidden');
    } else {
        chatAlarm.textContent = '';
        chatAlarm.classList.add('hidden');
    }
}

// 사용자가 선택한 사원번호 전달
function selectUser(empNo) {
    selectedEmpNo = empNo;

    // 선택한 사용자에 대해 안 읽은 메시지를 초기화
    if (unreadMessages[empNo]) {
        delete unreadMessages[empNo];
        updateUserNotification(empNo);
    }
    console.log("Selected user:", selectedEmpNo);

    // 자식 창이 열려 있을 경우 알람 상태 전달
    if (chatWindow && !chatWindow.closed) {
        chatWindow.postMessage({ type: 'UPDATE_ALARM', unreadMessages }, '*');
    } // 자식창에서 받음ㅇㅇㅇ
}

// 채팅창을 닫을 때 selectedEmpNo 초기화
function resetSelectedEmpNo() {
    selectedEmpNo = null;
}

// 자식 창으로부터 메시지를 받는 이벤트 리스너
window.addEventListener('message', function(event) {
    if (event.data.type === 'SELECT_USER') {
        selectUser(event.data.empNo);
    } else if (event.data.type === 'REQUEST_ALARM_STATUS') {
        // 자식 창에서 알람 상태 요청 시, 현재 상태를 자식 창에 전달
        if (chatWindow && !chatWindow.closed) {
            chatWindow.postMessage({ type: 'UPDATE_ALARM', unreadMessages }, '*');
            console.log("녜 제대로 되고 있어염!");
        }
    } else if (event.data.type === 'RESET_SELECTED_USER') {
        resetSelectedEmpNo();
    } else if (event.data.type === 'RESET_ALARM') {
        // 특정 사원의 알람 초기화 처리
        if (unreadMessages[event.data.empNo]) {
            delete unreadMessages[event.data.empNo];
            updateUserNotification(); // 알람 UI 업데이트
            chatWindow.postMessage({ type: 'UPDATE_ALARM', unreadMessages }, '*');
        }
    }
}, false);
// --------------------/end 메신저---------------------------

// 메신저 팝업창 오픈
function chatOpenPopup() {
    let width = 700;
    let height = 550;
    let left = Math.ceil((window.screen.width - width) / 2);
    let top = Math.ceil((window.screen.height - height) / 2);
    const chatUrl = `/chatting?empNo=${empNo}`; // 사원번호 전달

    // 팝업창 옵션
    chatWindow = window.open(chatUrl, 'chat', `toolbar=no, menubar=no, scrollbars=no, resizable=no, width=${width}, height=${height}, left=${left}, top=${top}`);

    // 팝업창 크기 고정
    if (chatWindow) {
        chatWindow.onresize = () => {
            chatWindow.resizeTo(width, height);
        };

        // 팝업창이 로드된 후에 현재 알람 수를 자식 창에 전달
        let interval = setInterval(() => {
            if (chatWindow && !chatWindow.closed) {
                chatWindow.postMessage({ type: 'INITIAL_ALARM', unreadMessages: unreadMessages }, '*');
                clearInterval(interval); // 메시지 전달 후 interval 멈춤
            }
        }, 500);
    }
}

function onLogout() {
    if (stompClient) {
        stompClient.send("/app/user.disconnectUser", {}, JSON.stringify(empNo));
        stompClient.disconnect();
    }

    if (chatWindow && !chatWindow.closed) {
        chatWindow.postMessage({ type: 'LOGOUT' }, '*');
        setTimeout(() => {
            chatWindow.close(); // 0.5초 후 창을 닫음
        }, 500);
    }
    // 실제 로그아웃 처리
    sessionStorage.clear();
}
// -- 메신저 end


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

// 테이블에 링크 적용하기
function rowClick(url) {
    window.location.href = url;
}

logout.addEventListener('click', onLogout); // 로그아웃 클릭 시, 로그아웃