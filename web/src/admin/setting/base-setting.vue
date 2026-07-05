<template>
  <el-card>
    <div slot="header" class="card-title">
      <span>站点信息</span>
      <el-button class="btn-save" type="text" @click="saveSetting">保存</el-button>
    </div>
    <el-form ref="settingForm"
             class="site-info-form"
             :model="settingItem"
             label-width="0"
             :rules="settingItemRules" @submit.native.prevent>
      <el-form-item prop="navName" class="site-info-form-item">
        <div class="site-info-item">
          <div class="site-info-copy">
            <strong><span class="site-required">*</span>网站标题</strong>
          </div>
          <el-input class="site-info-control"
                    v-model="settingItem.navName" v-trim
                    clearable
                    maxlength="50"
                    placeholder="网站标题"></el-input>
        </div>
      </el-form-item>
      <el-form-item prop="logoPath" class="site-info-form-item">
        <div class="site-info-item site-logo-item">
          <div class="site-info-copy">
            <strong>网站LOGO</strong>
          </div>
          <div class="site-logo-control">
            <com-icon-uploader v-model="settingItem.logoPath" @remove="onRemoveLogo"></com-icon-uploader>
          </div>
        </div>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
export default {
  name: "base-setting",
  components: {
    'com-icon-uploader': () => import('@/components/com-icon-uploader/index.vue'),
  },
  data() {
    return {
      settingItem: {
        navName: '',
        logoPath: '',
        logoToFavicon: true,
        cutOverSpeed: 10,
        nginxOpen: false,
        nginxUrl: '',
        showDoc: false,
      },
      settingItemRules: {
        navName: [
          {required: true, message: '网站标题不能为空'},
        ]
      }
    }
  },
  mounted() {
    this.loadSetting();
  },
  methods: {
    loadSetting() {
      this.$http.get(`/api/v1/setting`)
        .then(res => {
          this.settingItem = res;
          this.settingItem.cutOverSpeed = res.cutOverSpeed || 10;
          this.settingItem.logoToFavicon = true;
          this.settingItem.nginxOpen = Boolean(res.nginxOpen);
          this.settingItem.showDoc = Boolean(res.showDoc);
        });
    },
    saveSetting() {
      this.$refs.settingForm.validate(valid => {
        if (valid) {
          this.$http.patch(`/api/v1/setting`, {...this.settingItem, logoToFavicon: true})
            .then(() => {
              this.$modal.msgSuccess('保存成功');
              setTimeout(() => {
                location.reload();
              }, 1000);
            });
        }
      });
    },
    onRemoveLogo(url) {
      // 只有一个，不用判断url，直接移除
      this.settingItem.logoPath = '';
    },
  }
}
</script>

<style scoped lang="scss">
.site-info-form {
  padding-top: 0;
}

.site-info-form-item {
  margin-bottom: 14px;
}

.site-info-form-item:last-child {
  margin-bottom: 0;
}

.site-info-form ::v-deep .el-form-item__content {
  margin-left: 0 !important;
  line-height: normal;
}

.site-info-form ::v-deep .el-form-item__error {
  left: 126px;
  padding-top: 5px;
}

.site-info-item {
  display: grid;
  grid-template-columns: 110px minmax(0, 1fr);
  align-items: center;
  gap: 18px;
  min-height: 74px;
  padding: 16px 18px;
  border: 1px solid rgba(28, 42, 58, .08);
  border-radius: 16px;
  background: rgba(255, 255, 255, .58);
}

.site-logo-item {
  min-height: 88px;
}

.site-info-copy {
  display: flex;
  min-width: 0;
  flex-direction: column;

  strong {
    color: #1c2635;
    font-size: 16px;
    line-height: 1.2;
    font-weight: 900;
    letter-spacing: 0;
  }
}

.site-required {
  display: inline-block;
  margin-right: 4px;
  color: #f56c6c;
}

.site-info-control {
  max-width: 420px;
}

.site-info-form ::v-deep .el-input__inner {
  height: 44px;
  font-size: 16px;
  font-weight: 800;
}

.site-logo-control {
  display: flex;
  align-items: center;
  min-height: 70px;
}
</style>
