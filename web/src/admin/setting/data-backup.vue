<template>
  <el-card>
    <div slot="header" class="card-title">
      <span>数据维护</span>
    </div>
    <div class="maintenance-list">
      <div class="maintenance-item">
        <div class="maintenance-copy">
          <strong>数据备份</strong>
          <span>导出当前站点数据，用于迁移或还原。</span>
        </div>
        <el-button type="primary"
                   icon="el-icon-download"
                   :loading="backupLoading"
                   size="small"
                   @click="backupHandler">备份数据</el-button>
      </div>
      <div class="maintenance-item">
        <div class="maintenance-copy">
          <strong>备份还原</strong>
          <span>上传 zip 备份包恢复数据。</span>
        </div>
        <file-uploader type="revert"
                       accept="application/zip"
                       :file-list="[]"
                       @file-change="revertUploadSuccess"
                       :limit="1">还原备份</file-uploader>
      </div>
      <div class="maintenance-item">
        <div class="maintenance-copy">
          <strong>书签导入</strong>
          <span>上传浏览器书签 HTML，预览后导入。</span>
        </div>
        <file-uploader type="bookmark"
                       accept="text/html"
                       :file-list="[]"
                       @file-change="bookmarkUploadSuccess"
                       :limit="1">上传书签</file-uploader>
      </div>
      <bookmark-preview ref="refBookmark"></bookmark-preview>
    </div>
  </el-card>
</template>

<script>
export default {
  name: "data-backup",
  components: {
    'com-tip': () => import('@/components/com-tip/index.vue'),
    'file-uploader': () => import('@/components/file-uploader/index.vue'),
    'bookmark-preview': () => import('@/admin/setting/dialog/bookmark-preview.vue'),
  },
  data() {
    return {
      backupLoading: false,
      revertLoading: false,
      revertUrl: `${process.env.VUE_APP_BASE_API}/api/v1/data/revert`,
    }
  },
  methods: {
    backupHandler() {
      this.backupLoading = true
      this.$download.download(`/api/v1/data/backup`)
        .then(() => {
          this.$modal.msgSuccess('备份成功');
        })
        .finally(() => {
          this.backupLoading = false;
        });
    },
    revertUploadSuccess() {
      this.$modal.msgSuccess('还原成功');
      setTimeout(() => {
        location.reload();
      }, 1000);
    },
    bookmarkUploadSuccess(list){
      this.$refs.refBookmark.open(list[0].path);
    }
  }
}
</script>

<style scoped lang="scss">
.maintenance-list {
  display: grid;
  gap: 14px;
}

.maintenance-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  min-height: 74px;
  padding: 16px 18px;
  border: 1px solid rgba(28, 42, 58, .08);
  border-radius: 16px;
  background: rgba(255, 255, 255, .58);
}

.maintenance-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;

  strong {
    color: #1c2635;
    font-size: 16px;
    line-height: 1.2;
    font-weight: 900;
    letter-spacing: 0;
  }

  span {
    margin-top: 6px;
    color: #687386;
    font-size: 14px;
    line-height: 1.35;
    font-weight: 700;
  }
}
</style>
