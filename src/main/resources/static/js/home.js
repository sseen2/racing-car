async function startGame() {
    event.preventDefault();

    const carName = document.getElementById('carName').value.trim();
    const password = document.getElementById('password').value.trim();
    const errorEl = document.getElementById('errorMessage');

    errorEl.textContent = '';

    if (!carName || !password) {
        errorEl.textContent = "자동차 이름과 비밀번호를 모두 입력해주세요.";
        return;
    }

    try {
        const response = await fetch('/api/car/register', {
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

        window.location.href = '/room';
    } catch (error) {
        console.error(error);
        alert(error);
    }
}
