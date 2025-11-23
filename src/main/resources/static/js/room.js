let carInfo = null;
let stompClient = null;

function connectWebSocket() {
    const socket = new SockJS('/car-ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
        stompClient.subscribe('/sub/race/log', function (event) {
            const data = event.body;
            showLogMessage(data);
        });

        stompClient.subscribe('/sub/race/result', function (event) {
            const data = JSON.parse(event.body);
            showResult(data);
        })

        stompClient.subscribe('/sub/race/participants', function (event) {
            const data = JSON.parse(event.body);
            showParticipantList(data);
        });
    }, function (error) {
        console.error('❌ 에러 발생: ', error);
    });
}

function disconnectWebSocket() {
    if (stompClient && stompClient.connected) {
        stompClient.disconnect(() => {
            console.log('🔌 웹소켓 연결 해제');
        });
    }
}

function showLogMessage(data) {
    const logMessage = document.getElementById('log-message');
    if (!logMessage) {
        return;
    }

    const p = document.createElement('p');
    console.log(data);
    p.textContent = data;

    logMessage.appendChild(p);
    logMessage.scrollTop = logMessage.scrollHeight;

    if (typeof data === 'string' && data.includes('최종 우승자')) {
        openWinnerOverlay(data);
    }
}

function openWinnerOverlay(message) {
    const overlay = document.getElementById('winner-overlay');
    const winnerText = document.getElementById('winner-text');

    if (!overlay || !winnerText) {
        return;
    }

    winnerText.textContent = message;
    overlay.classList.remove('hidden');
}

async function closeWinnerOverlay() {
    try {
        const overlay = document.getElementById('winner-overlay');
        if (!overlay) {
            return;
        }

        const response = await fetch('/api/car/reset/position', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                carName: carInfo.name
            })
        });
        const result = await response.json();

        if (!result.success) {
            console.error(result.message);
            return;
        }

        overlay.classList.add('hidden');
        showHostSection();
    } catch (error) {
        console.error(error);
    }
}

function showResult(data) {
    const trackList = document.getElementById('car-track-list');
    if (!trackList) {
        return;
    }

    trackList.innerHTML = '';

    data.forEach((item) => {
        const li = document.createElement('li');
        li.className = 'track-item';

        const nameSpan = document.createElement('span');
        nameSpan.className = 'car-name';
        nameSpan.textContent = item.carName;

        const positionSpan = document.createElement('span');
        positionSpan.className = 'car-position';
        positionSpan.textContent = '🚗'.repeat(item.carPosition);

        li.appendChild(nameSpan);
        li.appendChild(positionSpan);
        trackList.appendChild(li);
    });
}

function showParticipantList(data) {
    const participantList = document.getElementById('participant-list');
    if (!participantList) {
        return;
    }

    console.log(data);

    participantList.innerHTML = '';

    data.forEach(item => {
        const p = document.createElement('p');
        if (item.isHost) {
            p.textContent = '👑 ' + item.name;
        }
        else {
            p.textContent = '🚗 ' + item.name;
        }
        participantList.appendChild(p);
    });

    const me = data.find(p => p.name === carInfo.name);
    if (me) {
        carInfo.isHost = me.isHost;
    }

    showHostSection();
}

async function startRace(event) {
    event.preventDefault();

    const hostSection = document.getElementById('host-only');
    if (hostSection) {
        hostSection.style.display = 'none';
    }

    const tryCount = document.getElementById('try-count').value.trim();
    const errorEl = document.getElementById('errorMessage');

    errorEl.textContent = '';

    try {
        const response = await fetch('/api/race/start', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                carName: carInfo.name,
                tryCount: tryCount
            })
        });

        const result = await response.json();

        if (!result.success) {
            let message = '요청 처리 중 오류가 발생했습니다.';

            if (Array.isArray(result.data) && result.data.length > 0 && result.data[0].message) {
                message = result.data[0].message;
            } else if (result.message) {
                message = result.message;
            }

            errorEl.textContent = message;
        }
    } catch (error) {
        console.error(error);
    }
}

async function loadInitParticipants() {
    try {
        const response = await fetch('/api/car/participants');
        const result = await response.json();

        if (!result.success) {
            console.error(result.message);
            return;
        }

        showParticipantList(result.data);
    } catch (error) {
        console.error(error);
    }
}

async function exitRoom() {
    try {
        const response = await fetch('/api/race/leave', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                carName: carInfo.name
            })
        });

        const result = await response.json();

        if (!result.success) {
            console.error(result.message);
            return;
        }

        disconnectWebSocket();

        sessionStorage.removeItem('carInfo');

        window.location.href = '/';
    } catch (error) {
        console.error(error);
    }
}

function showHostSection() {
    const hostSection = document.getElementById('host-only');
    if (!hostSection) {
        return;
    }

    if (carInfo.isHost) {
        hostSection.style.display = 'flex';
    }
    else {
        hostSection.style.display = 'none';
    }
}

window.addEventListener('load', function () {
    connectWebSocket();

    const carInfoJson = sessionStorage.getItem('carInfo');
    if (!carInfoJson) {
        alert('자동차 정보가 없습니다. 다시 입장해주세요.');
        window.location.href = '/';
        return;
    }

    carInfo = JSON.parse(carInfoJson);

    showHostSection();

    showLogMessage(`${carInfo.name}님이 입장했습니다.`);

    loadInitParticipants();


    const stayButton = document.getElementById('winner-stay-button');
    const exitButton = document.getElementById('winner-exit-button');

    if (stayButton) {
        stayButton.addEventListener('click', function () {
            closeWinnerOverlay();
        });
    }

    if (exitButton) {
        exitButton.addEventListener('click', function () {
            closeWinnerOverlay();
            exitRoom();
        });
    }
});
