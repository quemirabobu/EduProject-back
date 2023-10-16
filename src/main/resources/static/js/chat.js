var stompClient = null;

function connect() {
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/lecture/123', function(chatMessage) {
            showMessage(chatMessage.body);
        });
        let token = sessionStorage.getItem('Authorization');
        stompClient.send("/app/sendMsg/123/addUser", {'Authorization': 'Bearer ' + token});
    });
}

function disconnect() {
    if (stompClient !== null) {
        let token = sessionStorage.getItem('Authorization');
        stompClient.send("/app/sendMsg/123/leave", {'Authorization': 'Bearer ' + token});
        stompClient.disconnect();
    }
    console.log("Disconnected");

    // Reset the buttons
    // document.getElementById('connect').disabled = false;
    document.getElementById('send').disabled = true;
}


// 페이지 로드 후 접속시, 사용자 이름을 가져옵니다.
$(document).ready(function() {
    // sessionStorage에서 token 값을 가져옵니다.
    let token = sessionStorage.getItem('Authorization');

    if (token) {
        handleConnectClick();
    } else {
        console.error("Token not found in sessionStorage.");
    }


    document.getElementById('msg').addEventListener('keyup', function(event) {
        // Enter 키의 keyCode는 13입니다.
        if (event.keyCode === 13) {
            // 이 부분은 입력 필드에서 Enter 키를 누를 때마다 실행됩니다.
            event.preventDefault(); // Enter 키의 기본 동작(예: 폼 전송)을 방지합니다.
            sendMessage(); // 메시지 전송 함수 호출
        }
    });


    $("form").submit(function(event) {
        event.preventDefault();
    });
});

function sendMessage() {
    let messageContent = document.getElementById('msg').value.trim();

    if(messageContent && stompClient) {
        var chatMessage = {
            content: messageContent
        };

        let token = sessionStorage.getItem('Authorization');
        stompClient.send("/app/sendMsg/123", {'Authorization': 'Bearer ' + token}, JSON.stringify(chatMessage));
    }

    // 입력 필드를 비웁니다.
    document.getElementById("msg").value = "";
}

function showMessage(chatMessage) {
    var messageList = document.getElementById('message-list');
    var messageElement = document.createElement('li');
    var user = JSON.parse(chatMessage).sender;
    var msg = JSON.parse(chatMessage).content;
    messageElement.style.listStyle = 'none';
    if (user != null) {
        messageElement.innerHTML = user + ": " + msg;
    }else {
        messageElement.innerHTML = msg;
    }

    messageList.appendChild(messageElement);

    var userListDiv = document.getElementById('userList');
    var userList = JSON.parse(chatMessage).userList;

    userListDiv.innerHTML = '';

    if (userList != null) {
        userList.forEach(function(user) {
            var userElement = document.createElement('li');
            userElement.innerHTML  = user;
            userListDiv.appendChild(userElement);
        });
    }


}

// Connect to the websocket upon page load

function handleConnectClick() {
    connect();

    document.getElementById('send').disabled = false;
}


