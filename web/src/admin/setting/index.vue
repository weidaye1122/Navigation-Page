<template>
  <div class="setting-panel setting-page">
    <div class="setting-page-header">
      <div>
        <h2>系统设置</h2>
        <p>管理站点展示信息和数据维护操作。</p>
      </div>
    </div>
    <div class="setting-page-grid">
      <base-setting class="setting-card"></base-setting>
      <data-backup class="setting-card"></data-backup>
    </div>
  </div>
</template>

<script>
export default {
  name: "settings-index",
  components: {
    'base-setting': () => import('@/admin/setting/base-setting.vue'),
    'data-backup': () => import('@/admin/setting/data-backup.vue'),
  }
}
</script>

<style lang="scss" scoped>
.setting-panel {
  padding: 0 24px 28px;
}

.setting-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  padding: 18px 22px;
  border: 1px solid rgba(28, 42, 58, .1);
  border-radius: 18px;
  background: rgba(255, 255, 255, .78);
  box-shadow: 0 16px 44px rgba(31, 45, 61, .07);

  h2 {
    margin: 0;
    color: #1c2635;
    font-size: 20px;
    line-height: 1.2;
    font-weight: 900;
    letter-spacing: 0;
  }

  p {
    margin: 7px 0 0;
    color: #687386;
    font-size: 13px;
    line-height: 1.4;
  }
}

.setting-page-grid {
  display: grid;
  grid-template-columns: minmax(360px, 520px) minmax(420px, 1fr);
  gap: 18px;
  align-items: stretch;
}

.setting-card {
  min-width: 0;
  height: 100%;
  display: flex;
  flex-direction: column;

  ::v-deep .btn-save {
    float: right;
    padding: 3px 0;
    font-size: 16px;
    font-weight: 900;
  }

  ::v-deep .card-title {
    margin: 0 0 5px 0;
  }

  ::v-deep .card-title > span {
    font-size: 18px;
    font-weight: 900;
    line-height: 1.2;
  }

  ::v-deep .el-card__body {
    flex: 1;
  }
}

@media (max-width: 1100px) {
  .setting-page-grid {
    grid-template-columns: 1fr;
  }
}
</style>
