<template>
  <el-dialog title="登录" :visible.sync="show"
             width="400px"
             class="glass-login-wrapper"
             custom-class="glass-login-dialog"
             append-to-body
             :before-close="beforeClose"
             :close-on-click-modal="false">
    <div slot="title" class="login-dialog-title">
      <span class="login-dialog-title-mark">
        <i class="el-icon-lock"></i>
      </span>
      <span>欢迎登录</span>
    </div>
    <el-form ref="form"
             class="login-dialog-form"
             :model="form"
             :rules="formRules"
             @keyup.enter.native="loginHandler()"
             @submit.native.prevent>
      <el-row>
        <el-col :span="24">
          <el-form-item label="" prop="username">
            <el-input clearable v-model="form.username" placeholder="请输入账户" v-trim>
              <template slot="prepend">
                <i class="el-icon-user"></i>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="" prop="password">
            <el-input clearable
                      show-password
                      v-model="form.password"
                      v-trim
                      placeholder="请输入密码">
              <template slot="prepend">
                <i class="el-icon-lock"></i>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="">
            <el-checkbox v-model="rememberMe">记住账号</el-checkbox>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="">
            <el-button type="primary"
                       :loading="loading"
                       class="login-button"
                       round @click="loginHandler">登 录
            </el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </el-dialog>
</template>

<script>
// 是否显示登录弹窗

export let isRelogin = {show: false};

export default {
  name: "login-dialog-index",
  data() {
    return {
      show: false,
      loading: false,
      rememberMe: false,
      form: {
        username: '',
        password: '',
      },
      formRules: {
        username: [
          {required: true, message: '账号不能为空', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '密码不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  mounted() {
    this.show = true;
    this.init();
  },
  methods: {
    init() {
      delete localStorage.password;
      delete localStorage.autoLogin;
      if (localStorage.rememberMe === 'true') {
        this.form.username = localStorage.username;
        this.rememberMe = true;
      }
    },
    loginHandler() {
      this.loading = true;
      this.$refs.form.validate(valid => {
        if (!valid) {
          this.loading = false;
          return;
        }
        if (this.rememberMe) {
          localStorage.username = this.form.username;
          localStorage.rememberMe = this.rememberMe;
        } else {
          delete localStorage.username;
          delete localStorage.rememberMe;
        }
        delete localStorage.password;
        delete localStorage.autoLogin;
        let cardIds = null;
        if (localStorage.hasOwnProperty('starCardIds')) {
          if (localStorage.starCardIds) {
            cardIds = localStorage.starCardIds.split(',');
          } else {
            cardIds = [];
          }
        }
        this.$http.post(`/api/v1/quick/login?returnUrl=${encodeURIComponent(window.location.href)}`, {...this.form, cardIds: cardIds}).then(() => {
          window.location.reload();
          isRelogin.show = false;
          this.show = false;
          delete localStorage.userLogout;
        }).finally(() => {
          this.loading = false;
        })
      });
    },
    beforeClose() {
      isRelogin.show = false
      this.show = false;
    }
  }
}
</script>

<style scoped lang="scss">
.login-button {
  width: 100%;
  height: 46px;
  margin-top: 2px;
  border: 1px solid rgba(255, 255, 255, .38);
  border-radius: 18px;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, .34), rgba(255, 255, 255, .12) 42%, rgba(237, 112, 64, .38)),
    rgba(237, 112, 64, .58);
  box-shadow:
    0 18px 38px rgba(237, 112, 64, .24),
    inset 0 1px 0 rgba(255, 255, 255, .54),
    inset 0 -1px 0 rgba(255, 255, 255, .16);
  color: #ffffff;
  font-size: 16px;
  font-weight: 800;
  letter-spacing: 0;
  text-shadow: 0 1px 12px rgba(0, 0, 0, .18);
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease, background .18s ease;

  &:hover,
  &:focus {
    transform: translateY(-2px);
    border-color: rgba(255, 255, 255, .76);
    background:
      linear-gradient(135deg, rgba(255, 255, 255, .46), rgba(255, 255, 255, .18) 42%, rgba(237, 112, 64, .5)),
      rgba(237, 112, 64, .68);
    box-shadow:
      0 0 0 4px rgba(255, 255, 255, .09),
      0 24px 52px rgba(237, 112, 64, .3),
      inset 0 1px 0 rgba(255, 255, 255, .68);
  }
}

.login-dialog-form {
  ::v-deep .el-form-item {
    margin-bottom: 18px;
  }

  ::v-deep .el-input-group__prepend {
    width: 44px;
    padding: 0;
    color: rgba(255, 255, 255, .78);
    text-align: center;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, .2), rgba(255, 255, 255, .08)),
      rgba(255, 255, 255, .08);
    border: 1px solid rgba(255, 255, 255, .22);
    border-right: none;
    border-radius: 18px 0 0 18px;
    box-shadow:
      inset 0 1px 0 rgba(255, 255, 255, .22);
  }

  ::v-deep .el-input__inner {
    height: 46px;
    color: rgba(255, 255, 255, .94);
    font-size: 15px;
    font-weight: 700;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, .16), rgba(255, 255, 255, .06)),
      rgba(255, 255, 255, .08);
    border: 1px solid rgba(255, 255, 255, .22);
    border-radius: 0 18px 18px 0;
    box-shadow:
      inset 0 1px 0 rgba(255, 255, 255, .18),
      inset 0 -1px 0 rgba(255, 255, 255, .04);
    transition: border-color .18s ease, box-shadow .18s ease, background .18s ease;

    &::placeholder {
      color: rgba(226, 238, 234, .52);
      font-weight: 600;
    }

    &:focus {
      border-color: rgba(255, 255, 255, .58);
      background:
        linear-gradient(145deg, rgba(255, 255, 255, .22), rgba(255, 255, 255, .08)),
        rgba(255, 255, 255, .12);
      box-shadow:
        0 0 0 3px rgba(255, 255, 255, .08),
        0 0 28px rgba(237, 112, 64, .16),
        inset 0 1px 0 rgba(255, 255, 255, .28);
    }
  }

  ::v-deep .el-input__suffix {
    color: rgba(255, 255, 255, .64);
  }

  ::v-deep .el-checkbox {
    margin-right: 22px;
    color: rgba(236, 244, 241, .78);
    font-weight: 700;
  }

  ::v-deep .el-checkbox__inner {
    width: 17px;
    height: 17px;
    background: rgba(255, 255, 255, .1);
    border-color: rgba(255, 255, 255, .34);
    border-radius: 6px;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, .18);
  }

  ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
    background: rgba(237, 112, 64, .86);
    border-color: rgba(255, 255, 255, .64);
  }

  ::v-deep .el-checkbox__input.is-checked + .el-checkbox__label {
    color: #ffffff;
  }

  ::v-deep .el-form-item__error {
    padding-top: 6px;
    color: #ffc3b4;
    font-weight: 700;
  }
}
</style>

<style lang="scss">
.glass-login-wrapper.el-dialog__wrapper {
  background:
    radial-gradient(circle at 50% 20%, rgba(255, 255, 255, .12), transparent 34%),
    rgba(2, 6, 7, .34);
  -webkit-backdrop-filter: blur(10px) saturate(1.2);
  backdrop-filter: blur(10px) saturate(1.2);
}

.glass-login-dialog.el-dialog {
  width: 430px !important;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, .42);
  border-radius: 30px;
  background:
    linear-gradient(148deg, rgba(255, 255, 255, .24), rgba(255, 255, 255, .065) 44%, rgba(255, 255, 255, .12)),
    rgba(8, 15, 15, .48);
  box-shadow:
    0 38px 96px rgba(0, 0, 0, .42),
    inset 0 1px 0 rgba(255, 255, 255, .52),
    inset 0 -1px 0 rgba(255, 255, 255, .08);
  -webkit-backdrop-filter: blur(52px) saturate(1.78) contrast(1.08);
  backdrop-filter: blur(52px) saturate(1.78) contrast(1.08);
}

.glass-login-dialog.el-dialog::before {
  content: "";
  position: absolute;
  inset: -1px;
  z-index: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 18% 8%, rgba(255, 255, 255, .34), transparent 34%),
    radial-gradient(circle at 85% 16%, rgba(142, 204, 226, .26), transparent 28%),
    radial-gradient(circle at 18% 86%, rgba(237, 112, 64, .26), transparent 32%);
  opacity: .92;
}

.glass-login-dialog.el-dialog::after {
  content: "";
  position: absolute;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  border-radius: inherit;
  background:
    linear-gradient(120deg, rgba(255, 255, 255, .18), transparent 38%),
    linear-gradient(290deg, rgba(255, 255, 255, .1), transparent 32%);
}

.glass-login-dialog .el-dialog__header,
.glass-login-dialog .el-dialog__body {
  position: relative;
  z-index: 1;
}

.glass-login-dialog .el-dialog__header {
  padding: 28px 30px 12px;
  background: transparent !important;
  background-color: transparent !important;
  border-bottom: none;
}

.glass-login-dialog .el-dialog__body {
  padding: 12px 30px 30px;
  background: transparent !important;
  background-color: transparent !important;
}

.glass-login-dialog .el-dialog__headerbtn {
  top: 24px;
  right: 24px;
  width: 34px;
  height: 34px;
  border: 1px solid rgba(255, 255, 255, .22);
  border-radius: 50%;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, .2), rgba(255, 255, 255, .06)),
    rgba(255, 255, 255, .08);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, .22);
  transition: transform .18s ease, border-color .18s ease, background .18s ease;
}

.glass-login-dialog .el-dialog__headerbtn:hover {
  transform: scale(1.06);
  border-color: rgba(255, 255, 255, .48);
  background:
    linear-gradient(145deg, rgba(255, 255, 255, .3), rgba(255, 255, 255, .1)),
    rgba(255, 255, 255, .12);
}

.glass-login-dialog .el-dialog__close {
  color: rgba(255, 255, 255, .78);
  font-weight: 800;
}

.glass-login-dialog .login-dialog-title {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #ffffff;
  font-size: 24px;
  line-height: 1;
  font-weight: 900;
  letter-spacing: 0;
  text-shadow: 0 0 18px rgba(255, 255, 255, .22);
}

.glass-login-dialog .login-dialog-title-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  color: rgba(218, 103, 58, .98);
  border: 1px solid rgba(255, 255, 255, .58);
  border-radius: 50%;
  background:
    radial-gradient(circle at 32% 24%, rgba(255, 255, 255, .96), rgba(255, 255, 255, .62) 42%, rgba(255, 244, 235, .48));
  box-shadow:
    0 0 0 4px rgba(255, 255, 255, .08),
    0 18px 36px rgba(0, 0, 0, .22),
    inset 0 1px 0 rgba(255, 255, 255, .88);
}
</style>
