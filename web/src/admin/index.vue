<template>
  <div :class="classObj" class="app-wrapper admin-wrapper" :style="{'--current-color': '#4f7cff'}">
    <sidebar v-if="!sidebar.hide" class="sidebar-container"/>
    <div :class="{sidebarHide:sidebar.hide}" class="main-container">
      <app-main/>
    </div>
  </div>
</template>

<script>
import { AppMain, Sidebar } from './components'
import ResizeMixin from './mixin/ResizeHandler'
import { mapState } from 'vuex'
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'Layout',
  components: {
    AppMain,
    Sidebar,
  },
  mixins: [ResizeMixin],
  computed: {
    ...mapState({
      sideTheme: state => state.settings.sideTheme,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
    }),
    classObj() {
      return {
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile'
      }
    },
    variables() {
      return variables;
    }
  },
  methods: {
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', { withoutAnimation: false })
    }
  }
}
</script>

<style lang="scss" scoped>
  @import "~@/assets/styles/mixin.scss";
  @import "~@/assets/styles/variables.scss";

  .app-wrapper {
    @include clearfix;
    position: relative;
    height: 100%;
    width: 100%;
    min-height: 100vh;
    overflow: hidden;
    background:
      linear-gradient(135deg, rgba(247, 250, 255, .96), rgba(237, 242, 249, .92) 54%, rgba(226, 235, 245, .96)),
      #eef3f8;

    &.mobile.openSidebar {
      position: fixed;
      top: 0;
    }
  }

  .drawer-bg {
    background: #000;
    opacity: 0.3;
    width: 100%;
    top: 0;
    height: 100%;
    position: absolute;
    z-index: 999;
  }

</style>
<style lang="scss">
#app .admin-wrapper {
  --admin-sidebar-width: 236px;
  --admin-bg: #eef3f8;
  --admin-panel: rgba(255, 255, 255, .78);
  --admin-panel-solid: #ffffff;
  --admin-border: rgba(28, 42, 58, .1);
  --admin-border-strong: rgba(28, 42, 58, .16);
  --admin-text: #1c2635;
  --admin-muted: #687386;
  --admin-primary: #4f7cff;
  --admin-primary-soft: rgba(79, 124, 255, .12);
  --admin-danger: #e15b64;
  --admin-warning: #d98d2d;
  --admin-success: #1f9a73;

  .main-container {
    min-height: 100vh;
    margin-left: var(--admin-sidebar-width);
    background: transparent;
    transition: margin-left .28s ease;
  }

  .sidebar-container {
    top: 14px;
    bottom: 14px;
    left: 14px;
    width: 208px !important;
    height: calc(100% - 28px);
    overflow: hidden;
    border: 1px solid rgba(255, 255, 255, .54);
    border-radius: 24px;
    background:
      linear-gradient(160deg, rgba(255, 255, 255, .82), rgba(255, 255, 255, .46) 46%, rgba(241, 247, 255, .68)),
      rgba(244, 248, 255, .76) !important;
    backdrop-filter: blur(26px) saturate(1.24);
    -webkit-backdrop-filter: blur(26px) saturate(1.24);
    box-shadow:
      0 28px 70px rgba(31, 45, 61, .14),
      inset 0 1px 0 rgba(255, 255, 255, .74);
  }

  .sidebar-container .el-scrollbar,
  .sidebar-container .el-scrollbar__wrap,
  .sidebar-container .el-scrollbar__view,
  .sidebar-container .el-menu {
    background: transparent !important;
  }

  .sidebar-container .el-scrollbar {
    height: calc(100% - 164px);
  }

  .sidebar-container .el-menu {
    padding: 10px 16px 18px;
  }

  .sidebar-container .el-menu-item,
  .sidebar-container .el-submenu__title {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 48px;
    line-height: 48px;
    margin: 8px 0;
    padding: 0 12px !important;
    color: rgba(35, 48, 68, .76) !important;
    border-radius: 14px;
    border: 0 !important;
    background: transparent !important;
    font-size: 20px;
    font-weight: 800;
    letter-spacing: 0;
    transition: color .18s ease, background .18s ease, box-shadow .18s ease, transform .18s ease;

    i,
    .svg-icon {
      display: none !important;
    }

    span {
      width: 100%;
      letter-spacing: 0;
      text-align: center;
    }

    &:hover {
      color: #1f2c3f !important;
      background: rgba(79, 124, 255, .10) !important;
      transform: scale(1.03);
      box-shadow:
        0 12px 26px rgba(79, 124, 255, .10),
        inset 0 1px 0 rgba(255, 255, 255, .5);
    }
  }

  .sidebar-container .el-menu-item.is-active,
  .sidebar-container .el-submenu.is-active > .el-submenu__title {
    color: #ffffff !important;
    background:
      linear-gradient(135deg, rgba(79, 124, 255, .92), rgba(72, 168, 226, .62)) !important;
    box-shadow: inset 0 1px 0 rgba(255, 255, 255, .22);

    i,
    .svg-icon {
      color: #ffffff;
    }
  }

  .sidebar-container .el-submenu__icon-arrow {
    display: none !important;
  }

  .sidebar-container .nest-menu .el-menu-item {
    min-width: 0 !important;
    height: 38px;
    line-height: 38px;
    margin-left: 8px;
    padding-left: 18px !important;
    font-size: 13px;
    border-radius: 12px;
  }

  .sidebar-container .theme-dark .nest-menu .el-menu-item,
  .sidebar-container .theme-dark .el-submenu .el-menu-item {
    color: rgba(35, 48, 68, .68) !important;
    background: transparent !important;
  }

  .sidebar-container .theme-dark .nest-menu .el-menu-item:hover,
  .sidebar-container .theme-dark .el-submenu .el-menu-item:hover {
    color: #1f2c3f !important;
    background: rgba(79, 124, 255, .12) !important;
  }

  .sidebar-container .theme-dark .nest-menu .el-menu-item.is-active,
  .sidebar-container .theme-dark .el-submenu .el-menu-item.is-active {
    color: #ffffff !important;
    background:
      linear-gradient(135deg, rgba(79, 124, 255, .92), rgba(72, 168, 226, .62)) !important;
  }

  .app-main {
    box-sizing: border-box;
    color: var(--admin-text);
    padding-top: 24px;
    scrollbar-color: rgba(112, 124, 141, .42) transparent;
  }

  .app-container {
    min-height: calc(100vh - 24px);
    padding: 0 24px 28px;
  }

  .app-container > .el-row.el-row--flex:first-child {
    align-items: center;
    min-height: 64px;
    margin-bottom: 16px;
    padding: 12px 14px;
    border: 1px solid var(--admin-border);
    border-radius: 18px;
    background: var(--admin-panel);
    backdrop-filter: blur(22px) saturate(1.22);
    -webkit-backdrop-filter: blur(22px) saturate(1.22);
    box-shadow: 0 16px 44px rgba(31, 45, 61, .07);
  }

  .app-container > .el-row:not(.el-row--flex):first-child {
    display: flex;
    gap: 18px;
  }

  .app-container > .el-row:not(.el-row--flex):first-child > .el-col {
    flex: 1 1 0;
    width: auto;
    margin-left: 0 !important;
  }

  .setting-panel {
    padding: 0 24px 28px;
  }

  .setting-panel .setting-card {
    margin: 0 !important;
  }

  .el-card,
  .el-table {
    border-color: var(--admin-border) !important;
    border-radius: 18px;
    background: var(--admin-panel);
    backdrop-filter: blur(18px) saturate(1.16);
    -webkit-backdrop-filter: blur(18px) saturate(1.16);
    box-shadow: 0 18px 48px rgba(31, 45, 61, .08);
  }

  .el-card {
    overflow: hidden;
  }

  .el-card__header {
    border-bottom: 1px solid var(--admin-border);
    background: linear-gradient(180deg, rgba(248, 251, 255, .86), rgba(255, 255, 255, .62));
    color: var(--admin-text);
    font-weight: 800;
  }

  .el-table {
    overflow: hidden;
    color: #263244;
    font-size: 13px;
  }

  .el-table,
  .el-table__expanded-cell,
  .el-table th.el-table__cell,
  .el-table tr,
  .el-table td.el-table__cell {
    background-color: transparent;
  }

  .el-table::before,
  .el-table::after {
    display: none;
  }

  .el-table th.el-table__cell {
    height: 48px;
    background: rgba(246, 248, 251, .82);
    color: #526071;
    font-weight: 800;
    border-bottom: 1px solid var(--admin-border);
  }

  .el-table td.el-table__cell {
    height: 54px;
    border-bottom: 1px solid rgba(28, 42, 58, .07);
  }

  .el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell {
    background: rgba(250, 252, 255, .72);
  }

  .el-table__body tr:hover > td.el-table__cell {
    background: #f3f7ff !important;
  }

  .el-input__inner,
  .el-textarea__inner,
  .el-select .el-input__inner {
    border-color: rgba(28, 42, 58, .14);
    border-radius: 12px;
    background: rgba(255, 255, 255, .92);
    color: var(--admin-text);
    transition: border-color .18s ease, box-shadow .18s ease;
  }

  .el-input__inner:focus,
  .el-textarea__inner:focus,
  .el-select .el-input.is-focus .el-input__inner {
    border-color: rgba(79, 124, 255, .72);
    box-shadow: 0 0 0 3px rgba(79, 124, 255, .11);
  }

  .el-input-group__append,
  .el-input-group__prepend {
    border-color: rgba(28, 42, 58, .14);
    background: #f6f8fb;
    border-radius: 0 12px 12px 0;
  }

  .el-button {
    border-radius: 12px;
    font-weight: 700;
    letter-spacing: 0;
    transition: transform .16s ease, box-shadow .16s ease, border-color .16s ease, background .16s ease;

    &:hover {
      transform: translateY(-1px);
    }
  }

  .el-button--small {
    padding: 9px 13px;
  }

  .el-button.is-plain {
    background: #ffffff;
    border-color: rgba(28, 42, 58, .12);
  }

  .el-button--primary:not(.is-plain):not(.is-disabled),
  .el-button--primary.is-plain:not(.is-disabled):hover,
  .el-button--primary.is-plain:not(.is-disabled):focus {
    border-color: rgba(79, 124, 255, .78);
    background: linear-gradient(135deg, #4f7cff, #49a7e9);
    color: #ffffff;
    box-shadow: 0 10px 22px rgba(79, 124, 255, .22);
  }

  .el-button--success.is-plain:not(.is-disabled):hover,
  .el-button--success.is-plain:not(.is-disabled):focus {
    border-color: rgba(31, 154, 115, .62);
    background: #1f9a73;
    color: #ffffff;
  }

  .el-button--danger.is-plain:not(.is-disabled):hover,
  .el-button--danger.is-plain:not(.is-disabled):focus {
    border-color: rgba(225, 91, 100, .62);
    background: #e15b64;
    color: #ffffff;
  }

  .el-button--warning.is-plain:not(.is-disabled):hover,
  .el-button--warning.is-plain:not(.is-disabled):focus {
    border-color: rgba(217, 141, 45, .64);
    background: #d98d2d;
    color: #ffffff;
  }

  .el-button.is-disabled,
  .el-button.is-disabled:hover,
  .el-button.is-disabled:focus {
    color: #8a95a5 !important;
    border-color: rgba(28, 42, 58, .10) !important;
    background: rgba(255, 255, 255, .48) !important;
    box-shadow: none !important;
    transform: none !important;
  }

  .el-button--text {
    padding: 0 4px;
    color: var(--admin-primary);
    transform: none !important;
  }

  .el-tag {
    border-radius: 999px;
    font-weight: 700;
  }

  .el-pagination {
    margin-top: 18px;
    text-align: right;
  }

  .el-pagination .btn-prev,
  .el-pagination .btn-next,
  .el-pager li {
    border-radius: 10px;
    background: #ffffff;
    box-shadow: 0 8px 18px rgba(31, 45, 61, .05);
  }

  .el-dialog {
    overflow: hidden;
    border-radius: 20px;
    background: rgba(255, 255, 255, .94);
    box-shadow: 0 28px 90px rgba(15, 23, 42, .28);
  }

  .el-dialog__header {
    padding: 18px 22px;
    border-bottom: 1px solid var(--admin-border);
    background: #f7f9fc;
  }

  .el-dialog__title {
    color: var(--admin-text);
    font-size: 16px;
    font-weight: 800;
  }

  .el-dialog__body {
    padding: 22px;
  }

  .el-dialog__footer {
    padding: 14px 22px 20px;
    background: #fbfcfe;
  }

  .ivu-avatar {
    box-shadow: inset 0 0 0 1px rgba(255, 255, 255, .56), 0 8px 18px rgba(31, 45, 61, .12);
  }

  .el-tree {
    color: var(--admin-text);
    background: transparent;
  }

  .el-tree-node__content {
    height: 34px;
    border-radius: 10px;
  }

  .el-tree-node__content:hover {
    background: rgba(79, 124, 255, .08);
  }

  .el-switch__core {
    border-radius: 999px;
  }
}
</style>
