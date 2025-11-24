async function login(event) {
    event.preventDefault();

    const carName = document.getElementById('carName').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorEl = document.getElementById('error-message');

    errorEl.textContent = '';

    if (!carName || !password) {
        errorEl.textContent = "자동차 이름과 비밀번호를 모두 입력해주세요.";
        return;
    }

    try {
        const response = await fetch(endpoint.car.register, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                carName: carName,
                password: password
            })
        });

        const result = await response.json();

        if (!response.ok || !result.success) {
            let message = '요청 처리 중 오류가 발생했습니다.';

            if (Array.isArray(result.data) && result.data.length > 0 && result.data[0].message) {
                message = result.data[0].message;
            } else if (result.message) {
                message = result.message;
            }

            errorEl.textContent = message;
            return;
        }

        sessionStorage.setItem('carInfo', JSON.stringify(result.data));

        const nameInput = document.getElementById('carName');
        const passwordInput = document.getElementById('password');
        const loginBtn = document.getElementById('login-button');

        if (nameInput && passwordInput && loginBtn) {
            nameInput.disabled = true;
            passwordInput.disabled = true;
            loginBtn.disabled = true;
        }

        if (loginBtn) {
            loginBtn.classList.add('disabled-login-button');
        }

        await getCarHistory(carName);
    } catch (error) {
        console.error(error);
        alert(error);
    }
}

async function getCarHistory(carName) {
    const carInfoSection = document.getElementById('car-info-section');
    const carNameEl = document.getElementById('info-car-name');
    const historyEl = document.getElementById('info-car-history');

    if (!carInfoSection || !carNameEl || !historyEl) {
        return;
    }

    try {
        const param = encodeURIComponent(carName);
        const response = await fetch(endpoint.car.history(param), {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const result = await response.json();

        if (!result.success) {
            console.error(result.message ?? '전적 조회 중 오류가 발생했습니다.');
            return;
        }

        const data = result.data;

        const historyValue = `${data.winCount}승 ${data.loseCount}패`;

        carNameEl.textContent = carName;
        historyEl.textContent = historyValue;
        carInfoSection.style.display = 'block';
    } catch (error) {
        console.error(error);
    }
}

async function enterRoom() {
    const enterErrorEl = document.getElementById('enter-error-message');
    if (!enterErrorEl) {
        return;
    }

    enterErrorEl.textContent = '';
    const carInfoJson = sessionStorage.getItem('carInfo');

    if (!carInfoJson) {
        enterErrorEl.textContent = '로그인 후 입장할 수 있습니다.';
        return;
    }

    const carInfo = JSON.parse(carInfoJson);

    try {
        const response = await fetch(endpoint.race.enter, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                carName: carInfo.name
            })
        });

        const result = await response.json();

        if (!response.ok || !result.success) {
            let message = '입장 처리 중 오류가 발생했습니다.';
            console.error(result);

            if (Array.isArray(result.data) && result.data.length > 0 && result.data[0].message) {
                message = result.data[0].message;
            } else if (result.message) {
                message = result.message;
            }

            enterErrorEl.textContent = message;
            return;
        }

        sessionStorage.setItem('carInfo', JSON.stringify(result.data));
        window.location.href = '/room';
    } catch (error) {
        console.error(error);
        enterErrorEl.textContent = '입장 요청 중 오류가 발생했습니다.';
    }
}
