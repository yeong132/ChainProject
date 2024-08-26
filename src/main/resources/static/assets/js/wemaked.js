// 알림창 시간 표시

function timeAgo(timestamp) {
    const now = new Date();
    const notificationTime = new Date(timestamp);
    const diffInSeconds = Math.floor((now - notificationTime) / 1000);

    const minutes = Math.floor(diffInSeconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (minutes < 1) return '방금 전';
    if (minutes < 60) return `${minutes}분 전`;
    if (hours < 24) return `${hours}시간 전`;
    return `${days}일 전`;
}

document.addEventListener("DOMContentLoaded", function () {
    const notificationTimes = document.querySelectorAll('.notification-time');

    notificationTimes.forEach(function (element) {
        const timestamp = element.getAttribute('data-timestamp');
        if (timestamp) {
            element.textContent = timeAgo(new Date(timestamp));
        }
    });
});


// <button onClick="chatOpenPopup()">메신저</button>

let stompClient = null; // 클라이언트 객체 생성
const empNo = sessionStorage.getItem('empNo');
const chatAlarm = document.querySelector('#chatAlarm');

// 소켓 연결
document.addEventListener('DOMContentLoaded', function () {
    if (empNo) {
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
    }
});

// 소켓 연결(로그인) 성공
function onConnected() {
    // 구독 설정 및 소켓 통신 초기화
    stompClient.subscribe(`/user/${empNo}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // 접속한 사용자 등록: 사용자 컨트롤러로 사용자 정보 전달
    stompClient.send("/app/user.addUser", {}, JSON.stringify(empNo));

    findAndDisplayConnectedUsers().then(); // 연결된 사용자 찾고 대화 목록 불러오기
    displayUnreadMessages(); // 로그인 시 읽지 않은 메시지 불러오기
}

// 소켓 연결 끊김
function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

// 수신 메시지 처리
async function onMessageReceived(payload) {
    // console.log("수신메시지 페이뤄드----");
    // console.log(payload);
    // await findAndDisplayConnectedUsers(); // 연결된 사용자 목록 업데이트 및 사용자 연결 확인
    //
    // const message = JSON.parse(payload.body);
    //
    // // 메시지를 수신한 사용자가 선택된 사용자이거나 수신자가 나 자신인 경우에만 메시지를 표시
    // if (!(selectedEmpNo && selectedEmpNo === message.senderEmpNo)) {
    //     console.log("수신메시지 안에 --selectedEmpNo && selectedEmpNo === message.senderEmpNo--")
    //     // displayMessage(message.senderEmpNo, message.chatContent); // 메시지 출력
    // } else {
    //     console.log("수신메시지 안에 알람 함수----")
    //     updateUserNotification(message.senderEmpNo); // 선택된 사용자가 아닌 경우 알림 증가
    // }
}

// 전체 연결된 사용자 목록을 가져옴
async function findAndDisplayConnectedUsers() {
    // const connectedUsersResponse = await fetch(`/chat/activeUsers?empNo=${empNo}`); // 사원의 대화 목록 호출
    // let connectedUsers = await connectedUsersResponse.json(); // JSON 형식으로 변환된 응답 데이터
    //
    // connectedUsers.forEach(user => { // 목록에 있는 사용자는 유지, 새롭게 로그인한 사용자는 추가
    //     let listItem = user.empNo; // empNo사원번호
    //
    //     if (!listItem) { // 신규 사원 추가(not null)
    //         appendUserElement(user);
    //     } else { // 이미 존재하는 사용자의 알림 값 유지(null)
    //         chatAlarm.textContent = chatAlarm.textContent || '0'; // 현재 값 유지
    //     }
    // });
}

// 로그인 시 읽지 않은 메시지 불러오기
async function displayUnreadMessages() {
    // const unreadMessagesResponse = await fetch(`/messages/unread?recipientEmpNo=${empNo}`);
    // let unreadMessages = await unreadMessagesResponse.json();
    //
    // unreadMessages.forEach(message => {
    //     updateUserNotification(message.senderEmpNo); // 안 읽음 채팅 알림
    // });
}

// 안 읽은 채팅 수 카운트
function updateUserNotification(senderEmpNo) {
    // if (empNo == senderEmpNo) {
    // 현재 알림 숫자 값을 가져오고, 증가
    let currentCount = parseInt(chatAlarm.textContent) || 0;

    chatAlarm.textContent = (++currentCount).toString(); // 증가된 숫자 설정
    chatAlarm.classList.remove('hidden');
    // }
}

// 방 목록 추가
// function appendUserElement(user) {
//     console.log("목록에 방 추가 empNo:", user.empNo, "이름 성:", user.firstName + " " + user.lastName, "Rank:", user.rankName);
//
//     const listItem = document.createElement('div');
//     listItem.classList.add('chat_room_item');
//     listItem.id = addEmpNoPrefix(user.empNo);
//
//     const roomImg = document.createElement('div');
//     roomImg.className = "room_img";
//     const img = document.createElement("img");
//     img.src = "/assets/img/보노보노.png";
//     img.alt = `${user.lastName}${user.firstName}`; // 사용자 이름 불러와야 함
//     roomImg.appendChild(img);
//
//     const roomInfo = document.createElement("ul");
//     const roomName = document.createElement("li");
//     roomName.className = "room_name";
//     roomName.textContent = `${user.lastName}${user.firstName} ${user.rankName}님`; // 사용자 이름 불러와야 함
//     const roomContent = document.createElement("li");
//     roomContent.className = "room_content"; // 최근 메시지 표시
//     roomContent.textContent = "새로운 메시지가 없습니다.";
//     roomInfo.appendChild(roomName);
//     roomInfo.appendChild(roomContent);
//
//     const roomAlarm = document.createElement('span');
//     roomAlarm.textContent = '0'; // 메시지 번호
//     roomAlarm.classList.add('room_alarm', 'hidden'); // 메시지 알림 숨김
//
//     listItem.appendChild(roomImg);
//     listItem.appendChild(roomInfo);
//     listItem.appendChild(roomAlarm);
//
//     listItem.addEventListener('dblclick', roomItemClick); // 채팅방 선택
//
//     chatRoomUsersList.appendChild(listItem);
//
//     // 최신 메시지 가져와서 표시
//     // updateLatestMessage(user.empNo);
// }

// 메신저 팝업창 오픈
function chatOpenPopup() {
    let width = 700;
    let height = 550;
    let left = Math.ceil((window.screen.width - width) / 2);
    let top = Math.ceil((window.screen.height - height) / 2);
    const chatUrl = `/chatting?empNo=${empNo}`; // 사원번호 전달

    // 팝업창 옵션
    popupSize = window.open(chatUrl, 'chat', `toolbar=no, menubar=no, scrollbars=no, resizable=no, width=${width}, height=${height}, left=${left}, top=${top}`);

    // 팝업창 크기 고정
    if (popupSize) {
        popupSize.onresize = () => {
            popupSize.resizeTo(width, height);
        }
    }
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


