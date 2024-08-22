'use strict'; /* 스크립트 사용을 알림: 엄격 모드(오류 찾기에 유용) */

const chatRoomUsersList = document.querySelector('#v-pills-chatrooms .chat_list');// 사용자(방) 목록 영역
const chatRoom= document.querySelector('#chatRoom'); // 채팅방 영역
const chatArea = document.querySelector('#chatArea'); // 채팅 영역
const messageInput = document.querySelector('#messageInput'); // 메시지 입력창
const sendButton = document.querySelector('.send_button'); // 채팅 전송 버튼
const backButton = document.querySelector('#backButton'); // 채팅방 닫기 버튼
const connectingElement = document.querySelector('.connecting'); // 연결
// const logout = document.querySelector('#logout'); // 로그아웃

const chatModal = document.getElementById('chatModal'); // 모달 창
const closeButton = document.getElementById('closeButton'); // 모달 창 닫기 버튼
const confirmCreateRoomButton = document.getElementById('confirmCreateRoom'); // 채팅방 생성 확인 버튼

let stompClient = null; // 클라이언트 객체 생성
const empNo = sessionStorage.getItem('empNo') || new URLSearchParams(window.location.search).get('empNo'); // 접속 사원

let selectedEmpNo = null; // 선택된 사원
let selectedEmployeeInfo = null; // 선택된 사원의 정보

document.addEventListener('DOMContentLoaded', function() {
    // 조직도: 사원 클릭 시, 새 채팅방 생성
    document.querySelectorAll('.dep_emps_list span').forEach(link => {
        link.addEventListener('click', handleEmployeeClick);
    });

    // 조직도: 부서 클릭 시 펼침/닫힘
    document.querySelectorAll('.org_dep').forEach(link => {
        link.addEventListener('click', toggleDepartment);
    });

    // 검색 필터
    setupSearchFilters();
});

// 새 채팅방: 사원 선택 시, 모달 창 열기, 채팅방 생성
function handleEmployeeClick(event) {
    event.preventDefault();
    const link = event.currentTarget;
    const linkText = link.textContent.trim().split(' ');  // 공백을 기준으로 텍스트 분리

    selectedEmployeeInfo = {  // 선택된 사원의 정보 저장: 사원번호, 풀네임
        empNo: link.getAttribute('data-empno'),
        rankName: linkText[0],
        // fullName: linkText.slice(1).join(' ')
        fullName: linkText[1]
    };

    // 모달 창 열기
    chatModal.classList.remove('hidden');
    // 모달 창 닫기
    closeButton.onclick = closeModal;
    window.onclick = function(e) {
        if (e.target == chatModal) {
            closeModal();
        }
    }
    // 모달 창 확인: 채팅방 생성
    confirmCreateRoomButton.onclick = function() {
        closeModal();
        roomItemClick(selectedEmployeeInfo);
    }
}

// 모달 닫기
function closeModal() {
    chatModal.classList.add('hidden');
}

// 조직도: 부서 펼침/닫힘 토글
function toggleDepartment(event) {
    event.preventDefault();
    const link = event.currentTarget;
    const empList = link.parentElement;

    if (empList.classList.contains('open')) {
        empList.classList.remove('open');
        empList.querySelector('.dep_emps_list').style.maxHeight = null;
    } else {
        empList.classList.add('open');
        const content = empList.querySelector('.dep_emps_list');
        content.style.maxHeight = content.scrollHeight + 'px';
    }
}

// 검색 필터
function setupSearchFilters() {
    const searchInputOrg = document.getElementById('searchInputOrg');
    // const searchInputFavorRoom = document.getElementById('searchInputFavorRoom'); // 즐겨찾기
    const searchInputChatRoom = document.getElementById('searchInputChatRoom');

    // 조직도
    searchInputOrg.addEventListener('input', function () {
        applyFilter(searchInputOrg, '.org_dep_list > li', '.org_dep', '.dep_emps_list li');
    });

    // 즐겨찾기
    // searchInputFavorRoom.addEventListener('input', function () {
    //     applyRoomFilter(searchInputFavorRoom, '#v-pills-favorites .chat_room_item');
    // });

    // 방 목록
    searchInputChatRoom.addEventListener('input', function () {
        applyRoomFilter(searchInputChatRoom, '#v-pills-chatrooms .chat_room_item');
    });
}

// 검색 필터: 조직도
function applyFilter(inputElement, listSelector, nameSelector, itemSelector) {
    const filter = inputElement.value.toLowerCase();

    document.querySelectorAll(listSelector).forEach(dep => {
        const depName = dep.querySelector(nameSelector).textContent.toLowerCase();
        const empNames = dep.querySelectorAll(itemSelector);

        let depVisible = depName.includes(filter);
        let empVisible = false;

        empNames.forEach(emp => {
            const empName = emp.textContent.toLowerCase();
            emp.style.display = empName.includes(filter) || depVisible ? '' : 'none';
            if (emp.style.display !== 'none') empVisible = true;
            // if (empName.includes(filter) || depVisible) {
            //     empVisible = true;
            //     emp.style.display = '';
            // } else {
            //     emp.style.display = 'none';
            // }
        });

        if (depVisible || empVisible) {
            dep.style.display = '';
            dep.classList.add('open');
            dep.querySelector('.dep_emps_list').style.maxHeight = dep.querySelector('.dep_emps_list').scrollHeight + 'px';
        } else {
            dep.style.display = 'none';
        }

        // if (filter === '') {
        if (!filter) {
            dep.classList.remove('open');
            dep.querySelector('.dep_emps_list').style.maxHeight = null;
        }
    });
}

// 검색 필터: 방 목록
function applyRoomFilter(inputElement, roomSelector) {
    const filter = inputElement.value.toLowerCase();

    document.querySelectorAll(roomSelector).forEach(room => {
        const roomName = room.querySelector('.room_name').textContent.toLowerCase();
        room.style.display = roomName.includes(filter) ? '' : 'none';
    });
}

// empNo 접두사 추가 함수
function addEmpNoPrefix(empNo) {
    return `empNo${empNo}`;
}

// empNo 접두사 제거 함수
function removeEmpNoPrefix(empNoWithPrefix) {
    return empNoWithPrefix.replace('empNo', '');
}

// // 소켓 연결 ----------- 메인js로 이동
// function connect(event) {
//     empNo = document.querySelector('#empNo').value.trim();
//     // fullname = document.querySelector('#fullname').value.trim();
//
//     if (empNo) { // 사원 입력 확인
//         usernamePage.classList.add('hidden'); // 로그인 페이지 숨김
//         chatPage.classList.remove('hidden'); // 채팅 페이지 띄움
//
//         const socket = new SockJS('/ws');
//         stompClient = Stomp.over(socket);
//
//         stompClient.connect({}, onConnected, onError);
//     } else{} // 예외 처리
//     event.preventDefault(); // 이벤트 전파 방지
// }
//
// // 연결 성공 ----------- 메인js로 이동
// function onConnected() {
//     stompClient.subscribe(`/user/${empNo}/queue/messages`, onMessageReceived); // 특정 사원에게 메시지 수신 및 알림
//     stompClient.subscribe(`/user/public`, onMessageReceived); // 공용 메시지 수신
//
//     // 접속한 사용자 등록: 사용자 컨트롤러로 사용자 정보 전달
//     stompClient.send("/app/user.addUser", {}, JSON.stringify(empNo));
//     // stompClient.send("/app/user.addUser", {}, JSON.stringify({
//     //     empNo: empNo,
//     //     firstName: firstName,
//     //     lastName: lastName,
//     //     rankName: rankName
//     // }));
//
//     findAndDisplayConnectedUsers().then(); // 연결된 사용자 찾고 표시 / 대화 목록 불러오기
//     displayUnreadMessages(); // 로그인 시 읽지 않은 메시지 불러오기
// }

// 전체 연결된 사용자 목록을 가져옴
async function findAndDisplayConnectedUsers() {
    const connectedUsersResponse = await fetch(`/chat/activeUsers?empNo=${empNo}`); // 사원의 대화 목록 호출
    let connectedUsers = await connectedUsersResponse.json(); // JSON 형식으로 변환된 응답 데이터

    // 현재 로그인한 사용자를 목록에서 제외
    // connectedUsers = connectedUsers.filter(user => user.empNo !== empNo);

    connectedUsers.forEach(user => { // 목록에 있는 사용자는 유지, 새롭게 로그인한 사용자는 추가
        let listItem = document.getElementById(addEmpNoPrefix(user.empNo)); // empNo사원번호

        if (!listItem) { // 신규 사원 추가(not null)
            appendUserElement(user);
        } else { // 이미 존재하는 사용자의 알림 값 유지(null)
            const roomAlarm = listItem.querySelector('.room_alarm');
            roomAlarm.textContent = roomAlarm.textContent || '0'; // 현재 값 유지
        }
    });
}

// 방 목록 추가
function appendUserElement(user) {
    console.log("목록에 방 추가 empNo:", user.empNo, "이름 성:", user.firstName + " " + user.lastName, "Rank:", user.rankName);

    const listItem = document.createElement('div');
    listItem.classList.add('chat_room_item');
    listItem.id = addEmpNoPrefix(user.empNo);

    const roomImg = document.createElement('div');
    roomImg.className = "room_img";
    const img = document.createElement("img");
    img.src = "/assets/img/보노보노.png";
    img.alt = `${user.lastName}${user.firstName}`; // 사용자 이름 불러와야 함
    roomImg.appendChild(img);

    const roomInfo = document.createElement("ul");
    const roomName = document.createElement("li");
    roomName.className = "room_name";
    roomName.textContent = `${user.lastName}${user.firstName} ${user.rankName}님`; // 사용자 이름 불러와야 함
    const roomContent = document.createElement("li");
    roomContent.className = "room_content"; // 최근 메시지 표시
    roomContent.textContent = "새로운 메시지가 없습니다.";
    roomInfo.appendChild(roomName);
    roomInfo.appendChild(roomContent);

    const roomAlarm = document.createElement('span');
    roomAlarm.textContent = '0'; // 메시지 번호
    roomAlarm.classList.add('room_alarm', 'hidden'); // 메시지 알림 숨김

    listItem.appendChild(roomImg);
    listItem.appendChild(roomInfo);
    listItem.appendChild(roomAlarm);

    listItem.addEventListener('dblclick', roomItemClick); // 채팅방 선택

    chatRoomUsersList.appendChild(listItem);

    // 최신 메시지 가져와서 표시
    updateLatestMessage(user.empNo);
}

// 최신 메시지를 업데이트하는 함수 추가
async function updateLatestMessage(userId) { // 수신자
const userChatResponse = await fetch(`/messages/${empNo}/${userId}`); // 확인: 발신자/수신자
    let userChat = await userChatResponse.json(); // 채팅 응답 보관

    if (userChat.length > 0) {
        const latestMessage = userChat[userChat.length - 1];
        const chatRoomItem = document.getElementById(addEmpNoPrefix(userId)); // 수신자
        // const chatRoomItem = document.getElementById(empNo);
        const roomContentElement = chatRoomItem.querySelector('.room_content');
        roomContentElement.textContent = latestMessage.chatContent;
    }
}

// 선택한 채팅방 활성화
function roomItemClick(event) {
    document.querySelectorAll('.chat_room_item').forEach(item => {
        item.classList.remove('active'); // 모든 사람의 채팅창 가림
    });
    document.querySelector('.info_text').classList.add('hidden');

// clickedUser.classList.add('active'); // 현재 또는 클릭한 사용자만 활성화 추가

    const clickedUser = event.currentTarget;

    if(clickedUser){ // 기존 대화 목록
        const linkText = clickedUser.querySelector('.room_name').textContent.trim().split(' ');

        selectedEmpNo = removeEmpNoPrefix(clickedUser.getAttribute('id')); // 선택한 사용자id
        document.getElementById('chatRoomName').textContent = clickedUser.querySelector('.room_name').textContent; // 채팅방 이름 설정

        const roomAlarm = clickedUser.querySelector('.room_alarm');  // 선택된 사용자의 메시지 알림 제거
        roomAlarm.classList.add('hidden');
        roomAlarm.textContent = '0';

        markMessagesAsRead(selectedEmpNo); // 서버에 알림 초기화를 요청
    } else { // 신규 대화
        selectedEmpNo = event.empNo;
        document.getElementById('chatRoomName').textContent = `${event.fullName} ${event.rankName}님`;
    }

    chatRoom.classList.remove('hidden'); // 채팅창 활성화

    fetchAndDisplayUserChat().then(); // 사용자 채팅 오픈
}

// 채팅 확인 시, 알림 초기화
async function markMessagesAsRead(userId) {
    // 아이디 넘어가는 값 확인
    await fetch(`/chatrooms/markAsRead`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            senderEmpNo: empNo,
            recipientEmpNo: userId
        })
    });
}

// 채팅창 메시지 표시
function displayMessage(senderEmpNo, chatContent) {
    const messageContainer = document.createElement('div');

    messageContainer.classList.add('chat_message'); // 메시지 클래스 지정

    // 보낸 사람과 닉네임이 동일한지 확인
    if (senderEmpNo == empNo) { // '==' 로 사용(===금지)
        messageContainer.classList.add('chat_sender'); // 발신자
    } else {
        messageContainer.classList.add('chat_receiver'); // 수신자
    }

    const message = document.createElement('p'); // 메시지 내용 담을 요소
    message.textContent = chatContent; // 메시지 내용
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);

    chatArea.scrollTop = chatArea.scrollHeight; // 최신 스크롤 표시
}

// 사용자 채팅 가져오고 표시
async function fetchAndDisplayUserChat() {
    // 사용자 채팅 응답 상수 가져오기
    const userChatResponse = await fetch(`/messages/${empNo}/${selectedEmpNo}`);
    const userChat = await userChatResponse.json(); // 채팅 응답 보관

    chatArea.innerHTML = ''; // 채팅 영역 비움

    if (userChat.length > 0) {
        // 채팅 내용 표시
        userChat.forEach(chat => {
            displayMessage(chat.senderEmpNo, chat.chatContent);
        });

        const latestMessage = userChat[userChat.length - 1]; // 최신 메시지를 가져옴
        // 선택된 채팅방의 최신 메시지를 room_content에 표시
        const activeChatRoom = document.getElementById(addEmpNoPrefix(selectedEmpNo));
        activeChatRoom.querySelector('.room_content').textContent = latestMessage.chatContent;
    }
}

// 메시지 전송 처리
function sendMessage(event) {
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) { // 메시지 내용, 클라이언트 존재 확인
        const chatMessage = { // 메시지 발신자, 수신자, 내용, 시간 변수에 저장
            // senderEmpNo: nickname,
            // recipientEmpNo: selectedUserId,
            senderEmpNo: empNo,
            recipientEmpNo: selectedEmpNo,
            chatContent: messageContent,
            chatSentTime: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage)); // 저장한 메시지 정보 서버로 전달

        // displayMessage(nickname, messageContent); // 표시 메시지 호출: 닉네임, 메시지값 보이기
        displayMessage(empNo, messageContent);
        messageInput.value = ''; // 채팅 입력창 초기화
    }
    // event.preventDefault(); // 이벤트 전파 방지
}

// 안 읽은 채팅 수 카운트
function updateUserNotification(senderEmpNo) {
    const notifiedUser = document.querySelector(`#${addEmpNoPrefix(senderEmpNo)}`);

    if (notifiedUser) {
        const roomAlarm = notifiedUser.querySelector('.room_alarm');
        // 현재 알림 숫자 값을 가져오고, 증가
        let currentCount = parseInt(roomAlarm.textContent) || 0;

        roomAlarm.textContent = (++currentCount).toString(); // 증가된 숫자 설정
        roomAlarm.classList.remove('hidden');
    }
}

// 로그인 시 읽지 않은 메시지 불러오기
async function displayUnreadMessages() {
    const unreadMessagesResponse = await fetch(`/messages/unread?recipientEmpNo=${empNo}`);
    let unreadMessages = await unreadMessagesResponse.json();

    console.log("로그인 한 사원 ------");
    console.log(empNo);

    unreadMessages.forEach(message => {

        updateUserNotification(message.senderEmpNo); // 안 읽음 채팅 알림
        // 메시지를 수신한 사용자가 선택된 사용자이거나 수신자가 나 자신인 경우에만 메시지를 표시
        if (selectedEmpNo && selectedEmpNo === message.senderEmpNo) {
            displayMessage(message.senderEmpNo, message.chatContent); // 메시지 출력
        }
    });
}

// 수신 메시지 처리
async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers(); // 연결된 사용자 목록 업데이트 및 사용자 연결 확인

    const message = JSON.parse(payload.body);

    // 메시지를 수신한 사용자가 선택된 사용자이거나 수신자가 나 자신인 경우에만 메시지를 표시
    if (selectedEmpNo && selectedEmpNo === message.senderEmpNo) {
        displayMessage(message.senderEmpNo, message.chatContent); // 메시지 출력
    } else {
        updateUserNotification(message.senderEmpNo); // 선택된 사용자가 아닌 경우 알림 증가
    }

    // 선택된 사용자 활성화 처리
    if (selectedEmpNo) {
        document.querySelector(`#${addEmpNoPrefix(selectedEmpNo)}`).classList.add('active');
    } else {
        chatRoom.classList.add('hidden'); // 선택된 사용자가 아니면 채팅방 숨김
    }
}

// 채팅 닫기 함수
function closeChat() {
    chatRoom.classList.add('hidden'); // 채팅창 비활성화
    document.querySelector('.info_text').classList.remove('hidden'); // 안내 문구 활성화
}

// 로그아웃 처리
function onLogout() { // 로그아웃 유저 정보 전송
    // stompClient.send("/app/user.disconnectUser", {}, JSON.stringify({
    //     empNo: empNo
        // fullName: fullname,
        // status: 'OFFLINE'
    // }));
    if (stompClient) {
        stompClient.send("/app/user.disconnectUser", {}, JSON.stringify(empNo));
        stompClient.disconnect();
    }
    window.location.reload(); // 페이지 리로드
}

// 메시지 전송
sendButton.addEventListener('click', sendMessage);
messageInput.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        sendMessage();
    }
});
// 채팅 닫기
backButton.addEventListener('click', closeChat);
document.addEventListener('keydown', function escListener(e) {
    if (e.key === 'Escape') {
        closeChat();
        document.removeEventListener('keydown', escListener); // ESC 키 이벤트 리스너 제거 (메모리 누수 방지)
    }
});
// logout.addEventListener('click', onLogout, true); // 로그아웃 클릭 시, 로그아웃
// window.onbeforeunload = () => onLogout(); // 페이지 로드 시, 로그아웃
