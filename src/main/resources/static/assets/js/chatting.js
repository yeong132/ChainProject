'use strict'; /* 스크립트 사용을 알림: 엄격 모드(오류 찾기에 유용) */

const usernameForm = document.querySelector('#usernameForm'); // 로그인 작성 폼
const usernamePage = document.querySelector('#username-page'); // 로그인 페이지

const chatPage = document.querySelector('#chatPage'); // 채팅 페이지
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
let nickname = null;
let fullname = null;
let selectedUserId = null; // 선택한 사용자
let selectedEmployeeInfo = null; // 선택된 사원의 정보

document.addEventListener('DOMContentLoaded', function() {
    // 조직도-부서 클릭 시, 새 채팅방 생성
    document.querySelectorAll('.dep_emps_list span').forEach(link => {
        link.addEventListener('click', handleEmployeeClick);
    });

    // 조직도-부서 누르면 펼침, 닫힘
    document.querySelectorAll('.org_dep').forEach(link => {
        link.addEventListener('click', toggleDepartment);
    });

    // 검색 필터
    setupSearchFilters();
});

// 새 채팅방: 사원 클릭-모달 창 열기, 채팅방 생성
function handleEmployeeClick(event) {
    event.preventDefault();
    const link = event.currentTarget;
    const linkText = link.textContent.trim().split(' ');  // 공백을 기준으로 텍스트 분리

    selectedEmployeeInfo = {  // 선택된 사원의 정보 저장: 사원번호, 풀네임
        empNo: link.getAttribute('data-empno'),
        rankName: linkText[0],
        fullName: linkText.slice(1).join(' ')
    };

    // 모달 창 열기
    chatModal.classList.remove('hidden');
    // 모달 창 닫기: x 버튼 or 외부 공간 클릭
    closeButton.onclick = function() {
        chatModal.classList.add('hidden');
    }
    window.onclick = function(e) {
        if (e.target == chatModal) {
            chatModal.classList.add('hidden');
        }
    }
    // 모달 창 확인 버튼 클릭 시 채팅방 생성
    confirmCreateRoomButton.onclick = function() {
        chatModal.classList.add('hidden');
        roomItemClick(selectedEmployeeInfo);
    }
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
    // const searchInputFavorRoom = document.getElementById('searchInputFavorRoom');
    const searchInputChatRoom = document.getElementById('searchInputChatRoom');

    // 조직도
    searchInputOrg.addEventListener('input', function () {
        applyFilter(searchInputOrg, '.org_dep_list > li', '.org_dep', '.dep_emps_list li');
    });

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
            if (empName.includes(filter) || depVisible) {
                empVisible = true;
                emp.style.display = '';
            } else {
                emp.style.display = 'none';
            }
        });

        if (depVisible || empVisible) {
            dep.style.display = '';
            dep.classList.add('open');
            dep.querySelector('.dep_emps_list').style.maxHeight = dep.querySelector('.dep_emps_list').scrollHeight + 'px';
        } else {
            dep.style.display = 'none';
        }

        if (filter === '') {
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


// 소켓 연결 ----------- 메인js로 이동
function connect(event) {
    nickname = document.querySelector('#nickname').value.trim();
    fullname = document.querySelector('#fullname').value.trim();

    if (nickname && fullname) { // 닉네임 입력 확인
        usernamePage.classList.add('hidden'); // 로그인 페이지 숨김
        chatPage.classList.remove('hidden'); // 채팅 페이지 띄움

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault(); // 이벤트 전파 방지
}

// 연결 성공 ----------- 메인js로 이동
function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived); // 사용자에게 메시지 알림, 특정 사용자(nickname)에게 보내는 개별 메시지를 수신
    stompClient.subscribe(`/user/public`, onMessageReceived); // 수신될 모든 메시지 처리(모든 공용 메시지를 수신)

    // 접속한 사용자 등록
    stompClient.send("/app/user.addUser",
        {}, // 사용자 컨트롤러로 사용자 정보 전달
        JSON.stringify({nickName: nickname, fullName: fullname, status: 'ONLINE'})
    );
    // document.querySelector('#connected-user-fullname').textContent = fullname; // 연결된 사용자 풀네임을 들고 옴
    findAndDisplayConnectedUsers().then(); // 연결된 사용자 찾고 표시
    displayUnreadMessages(); // 로그인 시 읽지 않은 메시지 불러오기
}

// 전체 연결된 사용자 목록을 가져옴
async function findAndDisplayConnectedUsers() {
    // const connectedUsersResponse = await fetch('/users'); // 전체 연결된 사용자 목록 요청
    const connectedUsersResponse = await fetch(`/chat/activeUsers?nickname=${nickname}`); // 대화 중인 사용자 목록 호출
    let connectedUsers = await connectedUsersResponse.json(); // JSON 형식으로 변환된 응답 데이터

    // 현재 로그인한 사용자를 목록에서 제외
    // connectedUsers = connectedUsers.filter(user => user.nickName !== nickname);

    connectedUsers.forEach(user => { // 목록에 있는 사용자는 유지, 새롭게 로그인한 사용자는 추가
        let listItem = document.getElementById(user.nickName);

        if (!listItem) {
            // 신규 사용자 추가
            appendUserElement(user);
        } else {
            // 이미 존재하는 사용자의 알림 값 유지
            const roomAlarm = listItem.querySelector('.room_alarm');
            roomAlarm.textContent = roomAlarm.textContent || '0'; // 현재 값 유지
        }
    });
}

// 목록에 방 추가
function appendUserElement(user) {
    const listItem = document.createElement('div');
    listItem.classList.add('chat_room_item');
    listItem.id = user.nickName;

    const roomImg = document.createElement('div');
    roomImg.className = "room_img";
    const img = document.createElement("img");
    img.src = "/assets/img/보노보노.png";
    img.alt = user.fullName;
    roomImg.appendChild(img);

    const roomInfo = document.createElement("ul");
    const roomName = document.createElement("li");
    roomName.className = "room_name";
    roomName.textContent = `${user.fullName}님`; // 선택한 직원의 이름으로 설정
    const roomContent = document.createElement("li");
    roomContent.className = "room_content"; // 최근 메시지 표시
    roomContent.textContent = "새로운 메시지가 없습니다."; // 초기 메시지
    roomInfo.appendChild(roomName);
    roomInfo.appendChild(roomContent);

    const roomAlarm = document.createElement('span');
    roomAlarm.textContent = '0'; // 메시지 번호
    roomAlarm.classList.add('room_alarm', 'hidden'); // 메시지 알림 숨김

    listItem.appendChild(roomImg); // 목록에 이미지 추가
    listItem.appendChild(roomInfo); // 이름 및 최신 메시지 추가
    listItem.appendChild(roomAlarm); // 메시지 알람

    listItem.addEventListener('dblclick', roomItemClick); // 채팅방 선택

    chatRoomUsersList.appendChild(listItem);

    // 최신 메시지 가져와서 표시
    updateLatestMessage(user.nickName);
}

// 최신 메시지를 업데이트하는 함수 추가
async function updateLatestMessage(userId) {
    const userChatResponse = await fetch(`/messages/${nickname}/${userId}`);
    let userChat = await userChatResponse.json(); // 채팅 응답 보관

    if (userChat.length > 0) {
        const latestMessage = userChat[userChat.length - 1];
        const chatRoomItem = document.getElementById(userId);
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

    if(clickedUser){
        selectedUserId = clickedUser.getAttribute('id');
        document.getElementById('chatRoomName').textContent = clickedUser.querySelector('.room_name').textContent; // 채팅방 이름 설정
        // 선택된 사용자의 메시지 알림 제거
        const roomAlarm = clickedUser.querySelector('.room_alarm');
        roomAlarm.classList.add('hidden');
        roomAlarm.textContent = '0';
    } else {
        selectedUserId = event.empNo;
        document.getElementById('chatRoomName').textContent = `${event.fullName} ${event.rankName}님`;
    }
    console.log("추가한 사용자 사원 번호: ---------" + selectedUserId);
    console.log("추가한 사용자 방 이름: ---------" + document.getElementById('chatRoomName').textContent);

    // selectedUserId = clickedUser.getAttribute('id'); // 선택한 사용자id

    chatRoom.classList.remove('hidden'); // 채팅창 활성화

    fetchAndDisplayUserChat().then(); // 사용자 채팅 오픈

    // 서버에 알림 초기화를 요청
    markMessagesAsRead(selectedUserId);
}

async function markMessagesAsRead(userId) {
    // 아이디 넘어가는 값 확인
    // console.log(JSON.stringify({ senderEmpNo: nickname, recipientEmpNo: userId }));

    await fetch(`/chatrooms/markAsRead`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ senderEmpNo: nickname, recipientEmpNo: userId })
    });
}

// 채팅창 대화 영역
function displayMessage(senderEmpNo, chatContent) {
    const messageContainer = document.createElement('div');

    messageContainer.classList.add('chat_message'); // 메시지 클래스 지정

    if (senderEmpNo === nickname) { // 보낸 사람과 닉네임이 동일하면
        messageContainer.classList.add('chat_sender'); // 보낸 사람에 발신자 클래스
    } else {
        messageContainer.classList.add('chat_receiver'); // 받는 사람에 발신자 클래스
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
    const userChatResponse = await fetch(`/messages/${nickname}/${selectedUserId}`);
    let userChat = await userChatResponse.json(); // 채팅 응답 보관

    chatArea.innerHTML = ''; // 채팅 영역 비움

    if (userChat.length > 0) {
        // 채팅 내용 표시
        userChat.forEach(chat => {
            displayMessage(chat.senderEmpNo, chat.chatContent);
        });

        const latestMessage = userChat[userChat.length - 1]; // 최신 메시지를 가져옴

        // 선택된 채팅방의 최신 메시지를 room_content에 표시
        const activeChatRoom = document.getElementById(selectedUserId);
        activeChatRoom.querySelector('.room_content').textContent = latestMessage.chatContent;
    }
}

// 보낸 메시지 처리
function sendMessage(event) {
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) { // 메시지 내용, 클라이언트 존재 확인
        const chatMessage = { // 메시지 발신자, 수신자, 내용, 시간 변수에 저장
            senderEmpNo: nickname,
            recipientEmpNo: selectedUserId,
            chatContent: messageContent,
            chatSentTime: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage)); // 저장한 메시지 정보 서버로 전달

        displayMessage(nickname, messageContent); // 표시 메시지 호출: 닉네임, 메시지값 보이기
        messageInput.value = ''; // 채팅 입력창 초기화
    }
    // event.preventDefault(); // 이벤트 전파 방지
}

// 안 읽은 채팅 수 카운트
function updateUserNotification(senderEmpNo) {
    const notifiedUser = document.querySelector(`#${senderEmpNo}`);

    if (notifiedUser) {
        const roomAlarm = notifiedUser.querySelector('.room_alarm');
        // 현재 알림 숫자 값을 가져오고, 증가
        let currentCount = parseInt(roomAlarm.textContent) || 0;

        currentCount++;

        roomAlarm.textContent = currentCount.toString(); // 증가된 숫자를 설정
        roomAlarm.classList.remove('hidden');
    }
}

// 로그인 시 읽지 않은 메시지 불러오기
async function displayUnreadMessages() {
    const unreadMessagesResponse = await fetch(`/messages/unread?recipientEmpNo=${nickname}`);
    let unreadMessages = await unreadMessagesResponse.json();

    unreadMessages.forEach(message => {
        updateUserNotification(message.senderEmpNo); // 안 읽음 채팅 알림

        // 메시지를 수신한 사용자가 선택된 사용자이거나 수신자가 나 자신인 경우에만 메시지를 표시
        if (selectedUserId && selectedUserId === message.senderEmpNo) {
            displayMessage(message.senderEmpNo, message.chatContent); // 메시지 출력
        }
    });

}

// 수신 메시지 처리
async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers(); // 연결된 사용자 목록 업데이트 및 사용자 연결 확인

    const message = JSON.parse(payload.body);

    // 메시지를 수신한 사용자가 선택된 사용자이거나 수신자가 나 자신인 경우에만 메시지를 표시
    if (selectedUserId && selectedUserId === message.senderEmpNo) { // 사용자 확인
        displayMessage(message.senderEmpNo, message.chatContent); // 메시지 출력
    } else {
        updateUserNotification(message.senderEmpNo); // 선택된 사용자가 아닌 경우 알림을 증가
    }

    // 선택된 사용자 활성화 처리
    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
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
function onLogout() {
    stompClient.send("/app/user.disconnectUser", {}, // 로그아웃 유저 정보 전송
        JSON.stringify({nickName: nickname, fullName: fullname, status: 'OFFLINE'}) // 오프라인
    );
    window.location.reload(); // 페이지 리로드
}

function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

usernameForm.addEventListener('submit', connect, true); // 로그인 폼이 제출되면 소켓 연결
// 메시지 전송: 클릭 or Enter
sendButton.addEventListener('click', sendMessage);
messageInput.addEventListener('keypress', function(e) {
    if (e.key === 'Enter') {
        e.preventDefault();
        sendMessage();
    }
});
// 채팅 닫기: 버튼 클릭 or ESC
backButton.addEventListener('click', closeChat);
document.addEventListener('keydown', function escListener(e) {
    if (e.key === 'Escape') {
        closeChat();
        document.removeEventListener('keydown', escListener); // ESC 키 이벤트 리스너 제거 (메모리 누수 방지)
    }
});
// logout.addEventListener('click', onLogout, true); // 로그아웃 클릭 시, 로그아웃
// window.onbeforeunload = () => onLogout(); // 페이지 로드 시, 로그아웃
