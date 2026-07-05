<template>
  <el-dialog :title="title" :visible.sync="show"
             :close-on-click-modal="false"
             width="600px" append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" @submit.native.prevent>
      <el-form-item label="用户名" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入用户名" maxlength="100" v-trim @blur="nickNameChange"/>
      </el-form-item>
      <el-form-item label="账号" prop="username">
        <el-input v-model="form.username" placeholder="请输入账号" maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item v-if="!form.id" label="密码" prop="password">
        <el-input v-model="form.password" placeholder="请输入密码" type="password" show-password maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item v-if="!form.id" label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" placeholder="请再次输入密码" type="password" show-password maxlength="100" v-trim/>
      </el-form-item>
      <el-form-item label="所属角色">
        <role-select v-model="form.roleIds"></role-select>
      </el-form-item>
      <el-row>
        <el-col :span="12">
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio
                v-for="dict in statusOptions"
                :key="dict.id"
                :label="dict.id"
              >{{ dict.name }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "user-edit",
  components: {
    'role-select': () => import('@/components/role-select/index.vue'),
  },
  data() {
    return {
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      show: false,
      statusOptions: [
        {
          id: '1',
          name: '有效'
        },
        {
          id: '2',
          name: '无效'
        },
      ],
      form: {
        id: '',
        nickname: '',
        username: '',
        password: '',
        confirmPassword: '',
        roleIds: [],
        status: '1',
      },
      rules: {
        nickname: [
          {required: true, message: "用户名不能为空", trigger: "blur"}
        ],
        username: [
          {required: true, message: "账号不能为空", trigger: "blur"}
        ],
        password: [
          {required: true, message: "密码不能为空", trigger: "blur"},
          {min: 6, max: 100, message: "长度在 6 到 100 个字符", trigger: "blur"}
        ],
        confirmPassword: [
          {required: true, message: "确认密码不能为空", trigger: "blur"},
          {
            required: true, validator: (rule, value, callback) => {
              if (this.form.password !== value) {
                callback(new Error("两次输入的密码不一致"));
              } else {
                callback();
              }
            }, trigger: "blur"
          }
        ],
        status: [
          {required: true, message: "状态不能为空", trigger: "blur"}
        ],
      }
    }
  },
  methods: {
    open(item) {
      this.resetForm();
      if (item) {
        this.title = '编辑用户';
        this.form = {...item, roleIds: Array.isArray(item.roles) ? item.roles.map(it => (it.id)) : []};
      } else {
        this.title = '新增用户';
      }
      this.show = true;
    },
    resetForm() {
      this.form = {
        id: '',
        nickname: '',
        username: '',
        password: '',
        confirmPassword: '',
        roleIds: [],
        status: '1',
      };
      this.$nextTick(() => {
        this.$refs.form.clearValidate();
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.$http.save('/api/v1/user', {...this.form})
            .then(() => {
              this.$modal.msgSuccess('保存成功');
              this.show = false;
              this.$emit('refresh');
            })
        }
      });
    },
    nickNameChange() {
      if (!this.form.nickname) {
        return;
      }
      this.$http.get('/api/v1/hanyu-to-pinyin', {params: {hanyu: this.form.nickname}})
        .then(res => {
          this.form.username = res;
        })
    },
    // 取消按钮
    cancel() {
      this.show = false;
    },
  }
}
</script>

<style scoped>

</style>
