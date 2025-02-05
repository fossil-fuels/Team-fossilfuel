document.getElementById('send-code').addEventListener('click', async function() {
    const email = document.getElementById('email').value;

    if (!email) {
        alert("이메일을 입력해주세요.");
        return;
    }

    try {
        const response = await fetch('/api/send-verification-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        const data = await response.json();

        if (data.success) {
            alert("인증 코드가 이메일로 전송되었습니다.");
            document.getElementById('email').disabled = true; // 이메일 필드 수정 불가능하게 설정
        } else {
            alert("인증 코드 전송 실패: " + (data.message || "알 수 없는 오류"));
        }
    } catch (error) {
        console.error("인증 코드 전송 오류: ", error);
        alert("인증 코드 전송 중 문제가 발생했습니다.");
    }
});

document.getElementById('verify-code').addEventListener('click', async function () {
    const code = document.getElementById('code').value;

    if (!code) {
        alert("인증 코드를 입력해주세요.");
        return;
    }

    try {
        const response = await fetch('/api/verify-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ code })
        });

        const data = await response.json();

        if (data.success) {
            alert("이메일 인증 성공!");
        } else {
            alert("이메일 인증 실패: " + (data.message || "잘못된 코드입니다."));
        }
    } catch (error) {
        console.error("인증 코드 확인 오류: ", error);
        alert("인증 코드 확인 중 문제가 발생했습니다.");
    }
});

document.getElementById('signup').addEventListener('click', async function() {
    const nickname = document.getElementById('nickname').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const grade = document.getElementById('grade').value;

    if (!nickname || !email || !password || !confirmPassword || !grade) {
        alert("모든 필드를 입력해주세요.");
        return;
    }

    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    if (password.length < 6) {
        alert("비밀번호는 최소 6자 이상이어야 합니다.");
        return;
    }

    try {
        // 이메일 중복 체크 요청
        const emailCheckResponse = await fetch('/api/check-email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        const emailCheckData = await emailCheckResponse.json();

        if (!emailCheckData.success) {
            alert("이미 사용 중인 이메일입니다.");
            return;
        }

        // 이메일 인증 여부 확인
        const emailVerifyResponse = await fetch('/api/email-last-verified', {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        const emailVerifyData = await emailVerifyResponse.json();

        if (!emailVerifyData.success) {
            alert("이메일 인증을 먼저 완료해주세요.");
            return;
        }

        const response = await fetch('/api/members/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nickname, email, password, grade })
        });

        const data = await response.json();

        if (data.success) {
            alert("회원가입 성공!");
            window.location.href = '/login.html'; // 로그인 페이지로 이동
        } else {
            alert("회원가입 실패: " + (data.message || "알 수 없는 오류"));
        }
    } catch (error) {
        console.error("회원가입 요청 오류: ", error);
        alert("회원가입 중 문제가 발생했습니다.");
    }
});