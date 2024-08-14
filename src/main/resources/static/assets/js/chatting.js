let stompClient = null;
let activeChatRoomNo = null;

document.addEventListener('DOMContentLoaded', function() {
    // 페이지 전체에 오른쪽 마우스 클릭 기본 이벤트 막기
    document.addEventListener('contextmenu', function(event) {
        event.preventDefault();
    });

    webSocketConnect(); // 소켓 연결
    chatRooms(); // 채팅방 접속

    const chatChatting = document.getElementById('chatChatting'); // 채팅창 영역
    const chatInput = document.getElementById('chatInput'); // 채팅방 입력창(textarea)
    const sendButton = document.getElementById('sendButton'); // 채팅 전송 버튼
    const backButton = document.getElementById('backButton'); // 채팅방 닫기 버튼

    // 채팅창 전송버튼: 입력값 변경 시 버튼 활성화/비활성화 처리
    chatInput.addEventListener('input', toggleSendButton);

    // 채팅 전송
    sendButton.addEventListener('click', sendMessage); // 마우스 클릭 시
    chatInput.addEventListener('keypress', function(e) { // 엔터
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

    // 채팅방 목록 불러오기
    const empNo = 1000; // 현재 로그인한 사원의 번호
    fetch(`/api/chat/rooms/${empNo}`)
        .then(response => response.json())
        .then(chatRooms => {
            renderChatRooms(chatRooms);
        })
        .catch(error => console.error('Error fetching chat rooms:', error));

    // 오른쪽 마우스 클릭 관련 이벤트 초기화
    initializeContextMenu();
});

// 채팅방 목록을 렌더링하는 함수
function renderChatRooms(chatRooms) {
    const chatList = document.querySelector('#v-pills-chatrooms .chat_list');
    chatList.innerHTML = ''; // 기존 목록 초기화

    chatRooms.forEach(room => {
        const chatRoomElement = document.createElement('div');
        chatRoomElement.className = 'chat_room';
        chatRoomElement.dataset.roomId = room.chatRoomNo;

        chatRoomElement.innerHTML = `
            <span class="chat_alarm">${room.unreadCount || ''}</span>
            <div class="room_img">
                <img src="/assets/img/보노보노.png" alt="프로필사진">
            </div>
            <ul>
                <li class="room_name">${room.roomName}</li>
                <li class="room_content">최근 메시지 불러오는 중...</li> <!-- 초기 상태로 로딩 중 메시지 표시 -->
            </ul>
        `;

        chatList.appendChild(chatRoomElement);

        // 채팅방의 최신 메시지를 가져와서 room_content에 표시
        fetch(`/api/chat/messages/${room.chatRoomNo}`)
            .then(response => response.json())
            .then(messages => {
                const lastMessage = messages.length > 0 ? messages[messages.length - 1] : null;
                const roomContentElement = chatRoomElement.querySelector('.room_content');
                if (lastMessage) {
                    roomContentElement.textContent = lastMessage.chatContent; // 최신 메시지를 room_content에 표시
                } else {
                    roomContentElement.textContent = "메시지가 없습니다."; // 메시지가 없을 때의 표시
                }
            })
            .catch(error => console.error('Error fetching last chat message:', error));

        // 채팅방 클릭 이벤트 추가
        chatRoomElement.addEventListener('dblclick', function () {
            openChatRoom(chatRoomElement);
        });
    });
}

// 선택한 채팅방을 열고 메시지를 불러오는 함수
function openChatRoom(roomElement) {
    activeChatRoomNo = roomElement.dataset.roomId; // 활성화된 채팅방 ID 업데이트

    // 채팅방 이름
    document.getElementById('chatRoomName').textContent = roomElement.querySelector('.room_name').textContent;

    fetch(`/api/chat/messages/${activeChatRoomNo}`)
        .then(response => response.json())
        .then(messages => {
            renderChatMessages(messages);
        })
        .catch(error => console.error('Error fetching chat messages:', error));

    document.querySelector('.info_text').style.display = 'none'; // 안내 문구 숨기기
    document.getElementById('chatChatting').classList.add('active'); // 채팅창 활성화

    // 채팅창 전송버튼: 초기 상태 비활성화
    const sendButton = document.getElementById('sendButton');
    sendButton.classList.add('disabled'); // 클래스 추가
    sendButton.disabled = true; // 버튼 비활성화
}

// 채팅 메시지를 렌더링하는 함수
function renderChatMessages(messages) {
    let chatContent = document.querySelector(`#chatRoom${activeChatRoomNo}`);

    // 해당 채팅방의 콘텐츠 영역이 없으면 생성
    if (!chatContent) {
        chatContent = document.createElement('div');
        chatContent.id = `chatRoom${activeChatRoomNo}`;
        chatContent.className = 'chat_content';

        // #chatChatting의 두 번째 자식 요소로 삽입
        const chatChatting = document.getElementById('chatChatting');
        if (chatChatting.children.length >= 2) {
            chatChatting.insertBefore(chatContent, chatChatting.children[1]);
        } else {
            chatChatting.appendChild(chatContent);
        }
    }

    chatContent.innerHTML = ''; // 기존 메시지 초기화

    messages.forEach(message => {
        const messageElement = document.createElement('p');
        messageElement.className = message.empNo === 1000 ? 'chat_num chat_to' : 'chat_num chat_from';
        messageElement.textContent = `${message.empNo}: ${message.chatContent}`;
        chatContent.appendChild(messageElement);
    });

    // 스크롤을 맨 밑으로 이동
    chatContent.scrollTop = chatContent.scrollHeight;
}



// 소켓 연결 함수
function webSocketConnect() {
    console.log('소켓연결1');

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

// 소켓 연결 성공
function onConnected() {
    console.log("WebSocket 서버 연결");
    stompClient.subscribe('/topic/public', onMessageReceived);
}

// 소켓 연결 에러
function onError(error) {
    console.error('Could not connect to WebSocket server. Please refresh this page to try again!', error);
    alert("WebSocket 연결에 실패했습니다. 페이지를 새로고침하세요.");
}




// 메시지 전송
function sendMessage() {
    console.log('소켓연결4 샌드 메시지');

    const chatInputContent = chatInput.value.trim();

    if (chatInputContent && stompClient) {
        const chatMessage = {
            chatNo: 1,
            chatRoomNo: activeChatRoomNo, // 현재 활성화된 채팅방 ID
            empNo: 1000, // 사원 번호
            chatContent: chatInputContent,
            chatIsRead: true
        };

        try {
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        } catch (error) {
            console.error("메시지 전송에 실패했습니다.", error);
            alert("메시지 전송에 실패했습니다. 다시 시도해주세요.");
        }

        chatInput.value = ''; // 채팅 입력창 초기화
        toggleSendButton(); // 입력값 초기화 후 버튼 비활성화
    }
}

// 채팅창 전송버튼 활성화/비활성화 처리
function toggleSendButton() {
    if (chatInput.value.trim() === '') { // 입력값이 없을 때
        sendButton.classList.add('disabled');
        sendButton.disabled = true; // 버튼 비활성화
    } else { // 입력값이 있을 때
        sendButton.classList.remove('disabled');
        sendButton.disabled = false; // 버튼 활성화
    }
}

// 새 채팅 전송 시, 말풍선 추가
function onMessageReceived(payload) {
    console.log('소켓연결3 메시지 리시버');

    const message = JSON.parse(payload.body);

    if (message.chatRoomNo == activeChatRoomNo) { // '==' 사용
        const chatContent = document.querySelector(`#chatRoom${activeChatRoomNo}`); // 채팅방id 받을 수 있게 수정해야함
        const messageElement = document.createElement('p');

        messageElement.className = 'chat_num chat_to';
        // 보낸 메시지 말풍선 내용(사원번호: 내용)
        messageElement.textContent = `${message.empNo}: ${message.chatContent}`;
        chatContent.appendChild(messageElement);

        // 읽음 처리
        stompClient.send("/app/chat.readMessage", {}, JSON.stringify({ chatNo: message.chatNo }));

        // 마지막 메시지를 방 목록(room_content)에 업데이트
        const roomElement = document.querySelector(`.chat_room[data-room-id="${message.chatRoomNo}"] .room_content`);
        if (roomElement) {
            roomElement.textContent = message.chatContent;
        }

        // 스크롤을 맨 밑으로 이동
        chatContent.scrollTop = chatContent.scrollHeight;
    } else {
        // 다른 채팅방일 경우, 읽지 않은 메시지로 표시
        const roomElement = document.querySelector(`.chat_room[data-room-id="${message.chatRoomNo}"] .chat_alarm`);
        if (roomElement) {
            roomElement.textContent = parseInt(roomElement.textContent || "0") + 1;
        }
    }
}

// 채팅 닫기 함수
function closeChat() {
    const chatChatting = document.getElementById('chatChatting');
    chatChatting.classList.remove('active'); // 채팅창 비활성화
    document.querySelector('.info_text').style.display = 'flex'; // 안내 문구 보이기
}

// 채팅방 목록 - 방 선택 이벤트
function chatRooms() {
    document.querySelectorAll('.chat_room').forEach(room => {
        room.addEventListener('dblclick', function() {
            openChatRoom(this);
        });
    });
}



// 오른쪽 마우스 클릭 관련 이벤트 초기화 함수
function initializeContextMenu() {
    document.querySelectorAll('.chat_list .chat_room').forEach(chatRoom => {
        chatRoom.dataset.favorite = chatRoom.dataset.favorite || 'false';

        chatRoom.addEventListener('contextmenu', function(event) {
            event.preventDefault();

            // 기존의 모든 드롭다운 메뉴를 숨김
            hideDropdownMenu();

            // 현재 클릭된 채팅방을 선택 상태로 설정
            document.querySelectorAll('.chat_list .chat_room').forEach(room => {
                room.removeAttribute('data-selected');
            });
            chatRoom.setAttribute('data-selected', 'true');

            // 커스텀 드롭다운 메뉴 선택
            const dropdownMenu = document.querySelector('.chat_menu_container .dropdown-menu');
            const chatRoomExit = document.getElementById("chatRoomExit");

            // 드롭다운 메뉴 표시
            dropdownMenu.style.display = 'block';
            dropdownMenu.style.left = `${event.pageX}px`;
            dropdownMenu.style.top = `${event.pageY}px`;

            // 현재 채팅방의 즐겨찾기 상태에 따라 텍스트 변경
            const favoriteMenuItem = dropdownMenu.querySelector('.chatroomFavorite');
            if (chatRoom.dataset.favorite === 'true') {
                favoriteMenuItem.textContent = '즐겨찾기 해제';
            } else {
                favoriteMenuItem.textContent = '즐겨찾기 등록';
            }

            // 채팅방 나가기
            chatRoomExit.addEventListener('click', function() {
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
        const currentChatRoom = document.querySelector('.chat_room[data-selected="true"]');
        if (currentChatRoom) {
            const isFavorite = currentChatRoom.dataset.favorite === 'true';
            currentChatRoom.dataset.favorite = isFavorite ? 'false' : 'true';

            // 즐겨찾기 상태에 따라 버튼 텍스트 변경
            this.textContent = isFavorite ? '즐겨찾기 등록' : '즐겨찾기 해제';

            // chatHeader의 favoriteButton과 상태 연동
            const favoriteButton = currentChatRoom.querySelector('.favoriteButton');
            if (favoriteButton) {
                favoriteButton.querySelector('i').classList = isFavorite ? 'bi bi-star-fill' : 'bi bi bi-star'; // 선택에 따라 아이콘 변경
            }

            // 서버로 즐겨찾기 상태를 전송하여 저장 (이 기능을 구현하려면 추가적인 API 작업 필요)
            // sendFavoriteStatusToServer(currentChatRoom.dataset.roomId, !isFavorite);
        }
    });
}

// 드룹다운 메뉴 숨김
function hideDropdownMenu() {
    const dropDownMenu = document.querySelector('.chat_menu_container .dropdown-menu');
    if (dropDownMenu) {
        dropDownMenu.style.display = 'none';
    }
}

// 채팅방 생성 모달창 관련 이벤트
document.addEventListener('DOMContentLoaded', function() {
    const chatModal = document.getElementById("chatModal"); // 채팅방 생성 모달창
    const closeButton = document.getElementById("closeButton"); // 모달창 닫기 버튼
    const confirmButton = document.getElementById("confirmCreateRoom"); // 모달창 확인 버튼
    let selectedEmpName = ""; // 클릭된 직원의 이름을 저장할 변수

    const chatHeader = document.getElementById("chatHeader");
    const chatRoomName = document.getElementById("chatRoomName");
    const chatContent = document.querySelector(".chat_content");

    let activeRoomContentElement = null;

    // 직원 리스트 클릭 이벤트 추가
    document.querySelectorAll(".dep_emps_list li").forEach(item => {
        item.addEventListener("click", function() {
            selectedEmpName = this.textContent.trim();
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
        chatModal.style.display = "none";

        let newChatRoom = document.createElement("div");
        newChatRoom.className = "chat_room";
        newChatRoom.dataset.roomId = "new"; // 실제 ID로 변경 필요

        let roomImg = document.createElement("div");
        roomImg.className = "room_img";
        let img = document.createElement("img");
        img.src = "/assets/img/보노보노.png"; // 기본 이미지 또는 선택된 직원의 프로필 사진으로 변경 가능
        img.alt = "프로필사진";
        roomImg.appendChild(img);

        let roomInfo = document.createElement("ul");
        let roomName = document.createElement("li");
        roomName.className = "room_name";
        roomName.textContent = `${selectedEmpName}님`; // 선택한 직원의 이름으로 설정
        let roomContent = document.createElement("li");
        roomContent.className = "room_content";
        roomContent.textContent = "새로운 메시지가 없습니다."; // 초기 상태

        roomInfo.appendChild(roomName);
        roomInfo.appendChild(roomContent);

        newChatRoom.appendChild(roomImg);
        newChatRoom.appendChild(roomInfo);

        document.querySelector("#v-pills-chatrooms .chat_list").appendChild(newChatRoom);

        newChatRoom.addEventListener("click", function() {
            openChatRoom(selectedEmpName, roomContent);
        });

        openChatRoom(selectedEmpName, roomContent);

        function openChatRoom(empName, roomContentElement) {
            chatRoomName.textContent = empName;

            chatContent.innerHTML = `<div class="chat_num chat_to">환영합니다! ${empName}님과의 채팅방입니다.</div>`;

            activeRoomContentElement = roomContentElement;
            chatChatting.classList.add('active');
        }
    }
});

// 조직도-부서 누르면 펼침, 닫힘
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.org_dep').forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            let empList = link.parentElement;

            if (empList.classList.contains('open')) {
                empList.classList.remove('open');
                empList.querySelector('.dep_emps_list').style.maxHeight = null;
            } else {
                empList.classList.add('open');
                let content = empList.querySelector('.dep_emps_list');
                content.style.maxHeight = content.scrollHeight + 'px';
            }
        });
    });
});

// 채팅창에서 즐찾 버튼 on/off
document.addEventListener('DOMContentLoaded', function() {
    const favoriteButton = document.getElementById('favoriteButton');
    favoriteButton.addEventListener('click', function() {
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
document.addEventListener('DOMContentLoaded', function() {
    const searchInputOrg = document.getElementById('searchInputOrg');
    const searchInputFavorRoom = document.getElementById('searchInputFavorRoom');
    const searchInputChatRoom = document.getElementById('searchInputChatRoom');

    searchInputOrg.addEventListener('input', function() { // 검색 입력이 변경될 때마다 실행
        const filter = this.value.toLowerCase();

        document.querySelectorAll('.org_dep_list > li').forEach(dep => {
            const depName = dep.querySelector('.org_dep').textContent.toLowerCase();
            const empNames = dep.querySelectorAll('.dep_emps_list li');

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
    });

    searchInputFavorRoom.addEventListener('input', function() {
        const filter = this.value.toLowerCase();

        document.querySelectorAll('#v-pills-favorites .chat_room').forEach(room => {
            const roomName = room.querySelector('.room_name').textContent.toLowerCase();
            room.style.display = roomName.includes(filter) ? '' : 'none';
        });
    });

    searchInputChatRoom.addEventListener('input', function() {
        const filter = this.value.toLowerCase();

        document.querySelectorAll('#v-pills-chatrooms .chat_room').forEach(room => {
            const roomName = room.querySelector('.room_name').textContent.toLowerCase();
            room.style.display = roomName.includes(filter) ? '' : 'none';
        });
    });
});