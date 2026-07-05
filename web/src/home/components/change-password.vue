<template>
  <el-dialog title="修改密码" :visible.sync="show" width="430px"
             class="glass-password-wrapper"
             custom-class="glass-password-dialog"
             :close-on-click-modal="false"
             append-to-body>
    <div slot="title" class="password-dialog-title">
      <span class="password-dialog-title-mark">
        <i class="el-icon-key"></i>
      </span>
      <span>修改密码</span>
    </div>
    <el-form ref="form"
             class="password-dialog-form"
             :model="form"
             :rules="rules"
             label-width="0"
             @submit.native.prevent>
      <el-form-item prop="oldPassword">
        <el-input v-model="form.oldPassword" placeholder="请输入旧密码" type="password" show-password v-trim>
          <template slot="prepend">
            <i class="el-icon-lock"></i>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="newPassword">
        <el-input v-model="form.newPassword" placeholder="请输入新密码" type="password" show-password v-trim>
          <template slot="prepend">
            <i class="el-icon-key"></i>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input v-model="form.confirmPassword" placeholder="请确认新密码" type="password" show-password v-trim>
          <template slot="prepend">
            <i class="el-icon-check"></i>
          </template>
        </el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button class="password-submit-button" type="primary" @click="submitForm">保存</el-button>
      <el-button class="password-cancel-button" @click="cancel">关闭</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "change-password",
  data() {
    return {
      // 是否显示弹出层
      show: false,
      // 表单参数
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      // 表单校验
      // 表单校验
      rules: {
        oldPassword: [
          {required: true, message: "旧密码不能为空", trigger: "blur"}
        ],
        newPassword: [
          {required: true, message: "新密码不能为空", trigger: "blur"},
          {min: 6, max: 20, message: "长度在 6 到 20 个字符", trigger: "blur"}
        ],
        confirmPassword: [
          {required: true, message: "确认密码不能为空", trigger: "blur"},
          {
            required: true, validator: (rule, value, callback) => {
              if (this.form.newPassword !== value) {
                callback(new Error("两次输入的密码不一致"));
              } else {
                callback();
              }
            }, trigger: "blur"
          }
        ]
      }
    }
  },
  methods: {
    open() {
      this.show = true;
      this.resetForm();
    },
    // 取消按钮
    cancel() {
      this.show = false;
      this.resetForm();
    },
    resetForm(){
      this.form = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      };
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$http.patch(`/api/v1/user/password`, this.form)
            .then(() => {
              this.$modal.msgSuccess('密码修改成功');
              this.show = false;
            })
        }
      });
    },
  }
}
</script>

<style scoped lang="scss">
.password-dialog-form {
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
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, .22);
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

  ::v-deep .el-form-item__error {
    padding-top: 6px;
    color: #ffc3b4;
    font-weight: 700;
  }
}

.dialog-footer {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.password-submit-button,
.password-cancel-button {
  height: 44px;
  margin: 0 !important;
  border-radius: 17px;
  font-size: 15px;
  font-weight: 800;
  letter-spacing: 0;
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease, background .18s ease;

  &:hover,
  &:focus {
    transform: translateY(-2px);
  }
}

.password-submit-button {
  border: 1px solid rgba(255, 255, 255, .38);
  background:
    linear-gradient(135deg, rgba(255, 255, 255, .34), rgba(255, 255, 255, .12) 42%, rgba(237, 112, 64, .38)),
    rgba(237, 112, 64, .58);
  box-shadow:
    0 18px 38px rgba(237, 112, 64, .24),
    inset 0 1px 0 rgba(255, 255, 255, .54),
    inset 0 -1px 0 rgba(255, 255, 255, .16);
  color: #ffffff;
  text-shadow: 0 1px 12px rgba(0, 0, 0, .18);
}

.password-cancel-button {
  color: rgba(255, 255, 255, .86);
  border: 1px solid rgba(255, 255, 255, .22);
  background:
    linear-gradient(145deg, rgba(255, 255, 255, .16), rgba(255, 255, 255, .06)),
    rgba(255, 255, 255, .08);
}
</style>

<style lang="scss">
.glass-password-wrapper.el-dialog__wrapper {
  background:
    radial-gradient(circle at 50% 20%, rgba(255, 255, 255, .12), transparent 34%),
    rgba(2, 6, 7, .34);
  -webkit-backdrop-filter: blur(10px) saturate(1.2);
  backdrop-filter: blur(10px) saturate(1.2);
}

.glass-password-dialog.el-dialog {
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

.glass-password-dialog.el-dialog::before {
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

.glass-password-dialog.el-dialog::after {
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

.glass-password-dialog .el-dialog__header,
.glass-password-dialog .el-dialog__body,
.glass-password-dialog .el-dialog__footer {
  position: relative;
  z-index: 1;
  background: transparent !important;
  background-color: transparent !important;
}

.glass-password-dialog .el-dialog__header {
  padding: 28px 30px 12px;
  border-bottom: none;
}

.glass-password-dialog .el-dialog__body {
  padding: 12px 30px 8px;
}

.glass-password-dialog .el-dialog__footer {
  padding: 8px 30px 30px;
}

.glass-password-dialog .el-dialog__headerbtn {
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
}

.glass-password-dialog .el-dialog__close {
  color: rgba(255, 255, 255, .78);
  font-weight: 800;
}

.glass-password-dialog .password-dialog-title {
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

.glass-password-dialog .password-dialog-title-mark {
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
