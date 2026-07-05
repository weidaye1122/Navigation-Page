const nickname = document.querySelector('.nickname');
const username = document.querySelector('.username');
const password = document.querySelector('.password');
const confirmPassword = document.querySelector('.confirm-password');
const btnLoading = document.querySelector('.btn-loading');
const submitBtn = document.querySelector('.submit-btn');
const loginError = document.querySelector('.login-error');
window.onload = function () {
    if (loginError && loginError.innerHTML !== '') {
        loginError.className = 'login-error opacity1';
    }
};

function handleLoginSubmit() {
    if (username.value === '') {
        showError('请输入账号');
        return false;
    }
    if (password.value === '') {
        showError('请输入密码');
        return false
    }
    setLoading(true);
    return true;
}

function handleSetupSubmit(e) {
    e.preventDefault();
    if (nickname.value === '') {
        showError('请输入管理员用户名');
        return false;
    }
    if (username.value === '') {
        showError('请输入管理员账号');
        return false;
    }
    if (password.value === '') {
        showError('请输入管理员密码');
        return false;
    }
    if (password.value.length < 6 || password.value.length > 100) {
        showError('密码长度需在6到100个字符之间');
        return false;
    }
    if (confirmPassword.value === '') {
        showError('请再次输入管理员密码');
        return false;
    }
    if (password.value !== confirmPassword.value) {
        showError('两次输入的密码不一致');
        return false;
    }
    setLoading(true);
    fetch('/api/v1/setup/admin', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({
            nickname: nickname.value,
            username: username.value,
            password: password.value,
            confirmPassword: confirmPassword.value
        })
    }).then(res => {
        if (res.ok) {
            showError('管理员创建成功，请登录');
            setTimeout(() => {
                location.href = '/login';
            }, 800);
            return;
        }
        return res.text().then(text => {
            throw new Error(text || '管理员创建失败');
        });
    }).catch(err => {
        showError(err.message || '管理员创建失败');
        setLoading(false);
    });
    return false;
}

function inputFocus() {
    if (loginError) {
        loginError.className = 'login-error opacity0';
    }
}

function showError(message) {
    loginError.className = 'login-error opacity1';
    loginError.innerHTML = message;
}

function setLoading(loading) {
    btnLoading.style.display = loading ? 'block' : 'none';
    submitBtn.classList.toggle('is-loading', loading);
    submitBtn.style.cursor = loading ? 'default' : 'pointer';
}
