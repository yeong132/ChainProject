// --------------------------------소켓 연결--------------------------------
let stompClient = null;
let activeChatRoomNo = null;

document.addEventListener('DOMContentLoaded', function(){
    webSocketConnect(); // 소켓 연결
    chatRooms(); // 채팅방 접속
    // sendMessage();

    const chatChatting = document.getElementById('chatChatting'); // 채팅창 영역
    const chatInput = document.getElementById('chatInput'); // 채팅방 입력창(textarea)
    const sendButton = document.getElementById('sendButton'); // 채팅 전송 버튼
    const backButton = document.getElementById('backButton'); // 채팅방 닫기 버튼

    // 채팅창 전송버튼: 입력값 변경 시 버튼 활성화/비활성화 처리
    chatInput.addEventListener('input', toggleSendButton);

    // 채팅 전송
    sendButton.addEventListener('click', sendMessage); // 마우스 클릭 시
    chatInput.addEventListener('keypress', function (e) { // 엔터
        if (e.key === 'Enter') {
            e.preventDefault();
            sendMessage();
        }
    });

    // 채팅 닫기
    backButton.addEventListener('click', closeChat); // 버튼 클릭 시
    document.addEventListener('keydown', function escListener(e) { // ESC 키 눌렀을 때
        if (e.key === 'Escape') {
            closeChat();
            document.removeEventListener('keydown', escListener); // ESC 키 이벤트 리스너 제거 (메모리 누수 방지)
        }
    });
}); // /end DOMContentLoaded add event listener

// 소켓 연결 함수
function webSocketConnect() {
    console.log('소켓연결1');

    const socket = new SockJS('/chatting/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

// 소켓 연결 성공
function onConnected() {
    console.log('소켓연결2');
    console.log("Connected to WebSocket server.~~~~~~~~");
    stompClient.subscribe('/topic/public', onMessageReceived);
}

// 소켓 연결 에러
function onError(error) {
    console.error('Could not connect to WebSocket server. Please refresh this page to try again!', error);
}

// 채팅 입력 데이터 서버로 전송?
function sendMessage() {
    console.log('소켓연결4 샌드 메시지');

    const chatInputContent = chatInput.value.trim();

    if(chatInputContent && stompClient) {
        const chatMessage = {
            chatNo: 123,
            chatRoomNo: activeChatRoomNo,  // 현재 활성화된 채팅방 ID
            empNo: 1000, // 사원 번호 // 실제 사용자 이름으로 대체
            chatContent: chatInputContent,
            // chatSentTime: DateTime,
            chatIsRead: true
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        chatInput.value = ''; // 채팅 입력창 초기화
        toggleSendButton(); // 입력값 초기화 후 버튼 비활성화
    }
}

// 채팅창 전송버튼 활성화/비활성화 처리
function toggleSendButton() {
    if (chatInput.value.trim() === '') { // 입력값이 없을 때
        sendButton.classList.add('disabled');
        sendButton.disabled = true; // 버튼 비활성화
    } else {  // 입력값이 있을 때
        sendButton.classList.remove('disabled');
        sendButton.disabled = false; // 버튼 활성화
    }
}

// 새 채팅 전송 시, 말풍선 추가
function onMessageReceived(payload) {
    console.log('소켓연결3 메시지 리시버');

    const message = JSON.parse(payload.body);

    if (message.chatRoomNo == activeChatRoomNo) { // === 사용 금지
        const chatContent = document.querySelector('#chatRoom' + activeChatRoomNo);// 채팅방id 받을 수 있게 수정해야함
        const messageElement = document.createElement('p');

        messageElement.className = 'chat_num chat_to';
        messageElement.textContent = message.empNo +':'+ message.chatContent;
        chatContent.appendChild(messageElement);

        // 마지막 메시지를 방 목록(room_content)에 업데이트
        const roomElement = document.querySelector(`.chat_room[data-room-id="${message.chatRoomNo}"] .room_content`);
        if (roomElement) {
            roomElement.textContent = message.chatContent;
        }

        // 스크롤을 맨 밑으로 이동
        chatContent.scrollTop = chatContent.scrollHeight;
    }
}

// 채팅 닫기 함수
function closeChat(){
    chatChatting.classList.remove('active'); // 채팅창 비활성화
    document.querySelector('.info_text').style.display = 'flex'; // 안내 문구 보이기
}

// 채팅방 목록 - 방 선택 이벤트
function chatRooms(){
    document.querySelectorAll('.chat_room').forEach(room => {
        room.addEventListener('dblclick', function () {
            openChatRoom(this);
        });
    });
}

// 선택한 채팅방 오른쪽에 출력
function openChatRoom(roomElement) {
    activeChatRoomNo = roomElement.dataset.roomId; // 선택한 방 id 저장

    // 채팅방 이름
    document.getElementById('chatRoomName').textContent = roomElement.querySelector('.room_name').textContent;

    // 대화 상대 사진
    const chatRoomImg = roomElement.querySelector('.room_img img').src;
    // document.getElementById('').textContent = chatRoomImg; // 나중에 이미지, 이름 넣을 공간 만들기

    // 선택한 채팅방
    const selectedChatRoom = document.querySelector('#chatRoom'+activeChatRoomNo); // 채팅방id 받을 수 있게 수정해야함

    document.querySelector('.info_text').style.display = 'none'; // 안내 문구 숨기기

    // 채팅창 스크롤을 맨 밑으로 이동
    selectedChatRoom.scrollTop = selectedChatRoom.scrollHeight;

    // 채팅창 전송버튼: 초기 상태 비활성화
    sendButton.classList.add('disabled'); // 클래스 추가
    sendButton.disabled = true; // 버튼 비활성화

    // 채팅창 활성화: 채팅 헤드, 채팅방, 채팅 입력칸
    chatChatting.classList.add('active');
}


// ---------------------------/end 소켓 연결--------------------------------
//------  오른쪽 마우스 클릭 관련 이벤트 -------
document.addEventListener('DOMContentLoaded', function () {
    // 기본 오른쪽 클릭 드롭다운 메뉴 막기
    document.addEventListener('contextmenu', function (event) {
        event.preventDefault();
    });

    // chat_list에서 커스텀 드롭다운 메뉴 표시
    document.querySelectorAll('.chat_list .chat_room').forEach(chatRoom => {
        // 각 채팅방의 즐겨찾기 상태를 데이터 속성으로 설정 (기본값은 'false')
        chatRoom.dataset.favorite = chatRoom.dataset.favorite || 'false';

        chatRoom.addEventListener('contextmenu', function (event) {
            event.preventDefault();

            // 기존의 모든 드롭다운 메뉴를 숨김
            hideDropdownMenu();

            // 현재 클릭된 채팅방을 선택 상태로 설정
            document.querySelectorAll('.chat_list .chat_room').forEach(room => {
                room.removeAttribute('data-selected');
            });
            chatRoom.setAttribute('data-selected', 'true');

            // 커스텀 드롭다운 메뉴 선택
            let dropdownMenu = document.querySelector('.chat_menu_container .dropdown-menu');
            let chatRoomExit = document.getElementById("chatRoomExit");

            // 드롭다운 메뉴 표시
            dropdownMenu.style.display = 'block';
            dropdownMenu.style.left = `${event.pageX}px`;
            dropdownMenu.style.top = `${event.pageY}px`;

            // 현재 채팅방의 즐겨찾기 상태에 따라 텍스트 변경
            let favoriteMenuItem = dropdownMenu.querySelector('.chatroomFavorite');

            if (chatRoom.dataset.favorite === 'true') {
                favoriteMenuItem.textContent = '즐겨찾기 해제';
            } else {
                favoriteMenuItem.textContent = '즐겨찾기 등록';
            }

            // 채팅방 나가기
            chatRoomExit.addEventListener('click', function(){
                disconnect();
            });

            // 드롭다운 메뉴 외부 클릭 시 메뉴 닫기
            document.addEventListener('click', function hideDropdownMenuListener(e) {
                if (!dropdownMenu.contains(e.target)) {
                    hideDropdownMenu();
                    document.removeEventListener('click', hideDropdownMenuListener);
                }
            });
        });
    });

    // 즐겨찾기 등록/해제 클릭 이벤트 처리
    document.querySelector('.chat_menu_container .dropdown-menu .chatroomFavorite').addEventListener('click', function() {
        // 현재 선택된 채팅방 요소 찾기
        let currentChatRoom = document.querySelector('.chat_room[data-selected="true"]');
        if (currentChatRoom) {
            // 즐겨찾기 상태 토글
            let isFavorite = currentChatRoom.dataset.favorite === 'true';
            currentChatRoom.dataset.favorite = isFavorite ? 'false' : 'true';

            // 즐겨찾기 상태에 따라 버튼 텍스트 변경
            this.textContent = isFavorite ? '즐겨찾기 등록' : '즐겨찾기 해제';

            // chatHeader의 favoriteButton과 상태 연동
            let favoriteButton = currentChatRoom.querySelector('.favoriteButton');
            if (favoriteButton) {
                favoriteButton.querySelector('i').classList = isFavorite ? 'bi bi-star-fill' : 'bi bi bi-star'; // 선택에 따라 아이콘 변경
            }

            // TODO: 여기서 서버로 즐겨찾기 상태를 전송하여 저장
            // sendFavoriteStatusToServer(currentChatRoom.dataset.roomId, !isFavorite);
        }
    });
});

// 드룹다운 메뉴 숨김
function hideDropdownMenu() {
    let dropDownMenu = document.querySelector('.chat_menu_container .dropdown-menu');
    if(dropDownMenu) {
        dropDownMenu.style.display = 'none';
    }
}

// 서버로 즐겨찾기 상태 전송 (데이터베이스 연동 시 사용)
// function sendFavoriteStatusToServer(roomId, isFavorite) {
//     fetch('/update-favorite-status', {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json'
//         },
//         body: JSON.stringify({
//             roomId: roomId,
//             favorite: isFavorite
//         })
//     }).then(response => {
//         if (response.ok) {
//             console.log('즐겨찾기 상태가 성공적으로 업데이트되었습니다.');
//         } else {
//             console.error('즐겨찾기 상태 업데이트 실패.');
//         }
//     });
// }

// ------------- 채팅방 생성 모달창 -----------------
// 모달 관련 변수
let chatModal = document.getElementById("chatModal"); // 채팅방 생성 모달창
let closeButton = document.getElementById("closeButton"); // 모달창 닫기 버튼
let confirmButton = document.getElementById("confirmCreateRoom"); // 모달창 확인 버튼
let selectedEmpName = ""; // 클릭된 직원의 이름을 저장할 변수

// 채팅 관련 변수
let chatHeader = document.getElementById("chatHeader");
let chatRoomName = document.getElementById("chatRoomName");
let chatContent = document.querySelector(".chat_content");

// 현재 활성화된 채팅방을 추적하기 위한 변수
let activeRoomContentElement = null;

// 직원 리스트 클릭 이벤트 추가
let empItems = document.querySelectorAll(".dep_emps_list li");
empItems.forEach(item => {
    item.addEventListener("click", function() {
        // 선택한 직원의 이름 텍스트를 저장
        selectedEmpName = this.textContent.trim();

        // 모달 창 열기
        chatModal.style.display = "block";
    });
});

// 모달 닫기 (x 버튼 클릭 시)
closeButton.onclick = function() {
    chatModal.style.display = "none";
}

// 모달 닫기 (모달 외부 클릭 시)
window.onclick = function(e) {
    if (e.target == chatModal) {
        chatModal.style.display = "none";
    }
}

// 채팅방 생성 확인 버튼 클릭 시
confirmButton.onclick = function() {
    // 모달 창 닫기
    chatModal.style.display = "none";

    // 새로운 채팅방 생성
    let newChatRoom = document.createElement("div");
    newChatRoom.className = "chat_room";
    newChatRoom.dataset.roomId = "new"; // 실제 ID로 변경 필요

    // 채팅방 이미지
    let roomImg = document.createElement("div");
    roomImg.className = "room_img";
    let img = document.createElement("img");
    img.src = "/assets/img/보노보노.png"; // 기본 이미지 또는 선택된 직원의 프로필 사진으로 변경 가능
    // TODO: 이후에 서버에서 프로필 이미지를 받아와 img.src를 업데이트
    // 예시: img.src = `server_endpoint/getProfileImage?name=${selectedEmpName}`;
    img.alt = "프로필사진";
    roomImg.appendChild(img);

    // 채팅방 정보
    let roomInfo = document.createElement("ul");
    let roomName = document.createElement("li");
    roomName.className = "room_name";
    roomName.textContent = selectedEmpName+"님"; // 선택한 직원의 이름으로 설정
    let roomContent = document.createElement("li");
    roomContent.className = "room_content";
    roomContent.textContent = "새로운 메시지가 없습니다."; // 초기 상태

    roomInfo.appendChild(roomName);
    roomInfo.appendChild(roomContent);

    // 새로운 채팅방에 요소 추가
    newChatRoom.appendChild(roomImg);
    newChatRoom.appendChild(roomInfo);

    // 채팅방 목록에 추가
    document.querySelector("#v-pills-chatrooms .chat_list").appendChild(newChatRoom);

    // 채팅방 클릭 이벤트 추가
    newChatRoom.addEventListener("click", function() {
        openChatRoom(selectedEmpName, roomContent);
    });

    // 새로운 채팅방을 자동으로 열기
    openChatRoom(selectedEmpName, roomContent);

    // 채팅방 열기 함수
    function openChatRoom(empName, roomContentElement) {
        // 채팅방 헤더 업데이트
        chatRoomName.textContent = empName;

        // 채팅방 내용 초기화 또는 업데이트
        chatContent.innerHTML = `
        <div class="chat_num chat_to">환영합니다! ${empName}님과의 채팅방입니다.</div>`;

        // 현재 활성화된 채팅방의 room_content 요소 저장
        activeRoomContentElement = roomContentElement;

        // 채팅창 보이기
        chatChatting.style.display = "block";
    }
}
// ------------- /end 모달창 -----------------

// 조직도-부서 누르면 펼침, 닫힘
document.addEventListener('DOMContentLoaded', function () {
    let orgDep = document.querySelectorAll('.org_dep');

    orgDep.forEach(function (link) {
        link.addEventListener('click', function (event) {
            event.preventDefault();
            let empList = link.parentElement;

            // 직원 목록이 열려있는지 학인
            if (empList.classList.contains('open')) {
                // 열려 있으면 닫음
                empList.classList.remove('open');
                empList.querySelector('.dep_emps_list').style.maxHeight = null;
            } else {
                // 닫혀있으면 목록 높이만큼 열림
                empList.classList.add('open');
                let content = empList.querySelector('.dep_emps_list');
                content.style.maxHeight = content.scrollHeight + 'px';
            }
        });
    });
});

// 채팅창에서 즐찾 버튼 on/off
document.addEventListener('DOMContentLoaded', function () {
    const favoriteButton = document.getElementById('favoriteButton');
    favoriteButton.addEventListener('click', function () {
        const icon = this.querySelector('i');
        if (icon.classList.contains('bi-star')) {
            icon.classList.remove('bi-star');
            icon.classList.add('bi-star-fill');
        } else {
            icon.classList.remove('bi-star-fill');
            icon.classList.add('bi-star');
        }
    });
});

// 검색 기능
document.addEventListener('DOMContentLoaded', function () {
    // 검색 입력
    const searchInputOrg = document.getElementById('searchInputOrg');
    const searchInputFavorRoom = document.getElementById('searchInputFavorRoom');
    const searchInputChatRoom = document.getElementById('searchInputChatRoom');

    searchInputOrg.addEventListener('input', function () { // 검색 입력이 변경될 때마다 실행
        const filter = this.value.toLowerCase(); // 검색어를 소문자로 변환하여 검색

        <!-- 조직도 필터 -->
        // 모든 부서 항목 순회
        document.querySelectorAll('.org_dep_list > li').forEach(dep => {
            const depName = dep.querySelector('.org_dep').textContent.toLowerCase(); // 현재 부서 이름을 소문자로 변환
            const empNames = dep.querySelectorAll('.dep_emps_list li'); // 현재 부서의 모든 직원 목록

            let depVisible = depName.includes(filter); // 부서 이름이 검색어를 포함하는지 확인
            let empVisible = false; //직원 목록이 검색어 포함하는지 여부 추적

            // 직원 필터
            empNames.forEach(emp => {
                const empName = emp.textContent.toLowerCase(); // 현재 직원 이름을 소문자로 변환
                if (empName.includes(filter) || depVisible) { // 직원 이름 or 부서명이 검색어에 포함된 경우
                    empVisible = true; // 직원 목록 표시
                    emp.style.display = '';
                } else { // 검색어에 포함되지 않은 경우, 숨김
                    emp.style.display = 'none';
                }
            });

            // 부서 및 직원 목록 표시 상태 조정
            if (depVisible || empVisible){ // 부서 이름 or 직원 목록에 일치한 값이 있는 경우
                dep.style.display = ''; // 부서 표시
                dep.classList.add('open'); // 부서 항목에 open 클래스 추가하여 펼침 상태로 만듦
                dep.querySelector('.dep_emps_list').style.maxHeight = dep.querySelector('.dep_emps_list').scrollHeight + 'px'; // 직원 목록의 최대 높이를 설정해, 목록이 보이도록 함
            }else { // 부서 이름과 직원 목록에 일치한 값이 없는 경우
                dep.style.display = 'none';
            }

            // 검색 입력값이 빈 문자열인 경우 부서 접기
            if (filter === '') {
                dep.classList.remove('open'); // 부서 항목에서 open 클래스 제거하여 접힘 상태로 만듦
                dep.querySelector('.dep_emps_list').style.maxHeight = null; // 직원 목록의 최대 높이 초기화
            }
        });
    });

    // 즐겨찾기 목록 필터링
    searchInputFavorRoom.addEventListener('input', function () {
        const filter = this.value.toLowerCase();

        document.querySelectorAll('#v-pills-favorites .chat_room').forEach(room => {
            const roomName = room.querySelector('#v-pills-favorites .room_name').textContent.toLowerCase();
            room.style.display = roomName.includes(filter) ? '' : 'none';
        });
    });

    // 채팅방 목록 필터링
    searchInputChatRoom.addEventListener('input', function () {
        const filter = this.value.toLowerCase();

        document.querySelectorAll('#v-pills-chatrooms .chat_room').forEach(room => {
            const roomName = room.querySelector('#v-pills-chatrooms .room_name').textContent.toLowerCase();
            room.style.display = roomName.includes(filter) ? '' : 'none';
        });
    });
});