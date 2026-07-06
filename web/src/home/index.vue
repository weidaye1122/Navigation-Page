<template>
  <div class="home-scale-viewport"
       :class="{
         'is-desktop-scale': device !== 'mobile',
         'is-tablet-fill': tabletFill && device !== 'mobile'
       }"
       :style="desktopScaleStyle">
    <div :class="classObj" class="app-wrapper home-wrapper" :style="{'--current-color': '#409EFF'}">
      <sidebar class="sidebar-container"/>
      <div :class="{sidebarHide:sidebar.hide}" class="main-container">
        <div class="fixed-header">
          <navbar/>
        </div>
        <app-main />
      </div>
      <div class="home-access-stats">
        <span>今日访问 {{ todayAccessStats.visitCount || 0 }}</span>
        <span>今日登录 {{ todayAccessStats.loginCount || 0 }}</span>
      </div>
    </div>
  </div>
</template>

<script src="./index.js"></script>

<style lang="scss" scoped>
@import "~@/assets/styles/mixin.scss";
@import "~@/assets/styles/variables.scss";

.home-scale-viewport {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 100vh;
  overflow: hidden;
  background: #070b0b;
}

.home-scale-viewport.is-desktop-scale::before {
  content: "";
  position: fixed;
  inset: -46px;
  z-index: 0;
  background-image: url('~@/assets/images/login-background.jpg');
  background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;
  filter: blur(24px) brightness(.68) saturate(1.24);
  transform: scale(1.1);
  transform-origin: center;
}

.home-scale-viewport.is-desktop-scale::after {
  content: "";
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    linear-gradient(115deg, rgba(5, 9, 9, .45), rgba(11, 18, 17, .16) 42%, rgba(4, 8, 8, .58)),
    radial-gradient(circle at 52% 5%, rgba(238, 132, 62, .22), transparent 30%),
    radial-gradient(circle at 92% 80%, rgba(82, 141, 164, .16), transparent 32%);
}

.home-scale-viewport.is-desktop-scale .home-wrapper {
  z-index: 1;
  background: transparent;
}

.home-scale-viewport.is-desktop-scale .home-wrapper::before,
.home-scale-viewport.is-desktop-scale .home-wrapper::after {
  display: none;
}

.app-wrapper {
  @include clearfix;
  position: relative;
  height: 100%;
  min-height: 100vh;
  width: 100%;
  overflow: hidden;
  isolation: isolate;
  background: #070b0b;
  --home-glass-bg: rgba(8, 15, 15, .38);
  --home-glass-bg-soft: rgba(8, 15, 15, .28);
  --home-glass-bg-strong: rgba(9, 16, 16, .48);
  --home-glass-border: rgba(255, 255, 255, .34);
  --home-glass-border-strong: rgba(255, 255, 255, .48);
  --home-glass-highlight: rgba(255, 255, 255, .16);
  --home-text: rgba(255, 255, 255, .94);
  --home-text-muted: rgba(226, 238, 234, .7);

  &.mobile.openSidebar {
    position: fixed;
    top: 0;
  }
}

.home-wrapper::before {
  content: "";
  position: fixed;
  inset: -46px;
  z-index: 0;
  background-image: url('~@/assets/images/login-background.jpg');
  background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;
  filter: blur(24px) brightness(.68) saturate(1.24);
  transform: scale(1.1);
  transform-origin: center;
}

.home-wrapper::after {
  content: "";
  position: fixed;
  inset: 0;
  z-index: 0;
  pointer-events: none;
  background:
    linear-gradient(115deg, rgba(5, 9, 9, .45), rgba(11, 18, 17, .16) 42%, rgba(4, 8, 8, .58)),
    radial-gradient(circle at 52% 5%, rgba(238, 132, 62, .22), transparent 30%),
    radial-gradient(circle at 92% 80%, rgba(82, 141, 164, .16), transparent 32%);
}

.home-wrapper > .main-container,
.home-wrapper > .sidebar-container,
.home-wrapper > .drawer-bg {
  position: relative;
  z-index: 1;
}

.drawer-bg {
  background: rgba(24, 33, 30, .48);
  width: 100%;
  top: 0;
  height: 100%;
  position: absolute;
  z-index: 999;
}

.fixed-header {
  position: relative;
  z-index: 9;
  width: 100%;
  transition: width 0.28s;
}

.home-access-stats {
  position: absolute;
  left: 288px;
  bottom: 20px;
  z-index: 12;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  height: 34px;
  padding: 0 14px;
  color: rgba(255, 255, 255, .84);
  border: 1px solid rgba(255, 255, 255, .18);
  border-radius: 17px;
  background: rgba(8, 15, 15, .32);
  box-shadow:
    0 14px 36px rgba(0, 0, 0, .2),
    inset 0 1px 0 rgba(255, 255, 255, .18);
  -webkit-backdrop-filter: blur(24px) saturate(1.4);
  backdrop-filter: blur(24px) saturate(1.4);
  font-size: 12px;
  font-weight: 800;
  pointer-events: none;
}

//.hideSidebar .fixed-header {
//  width: calc(100% - 54px);
//}

.sidebarHide .fixed-header {
  width: 100%;
}

.mobile .fixed-header {
  width: 100%;
}
</style>
<style lang="scss">
body {
  overflow: hidden;
}

#app .home-scale-viewport.is-desktop-scale {
  position: fixed;
  inset: 0;
  width: 100vw;
  height: 100vh;
  min-height: 0;
  overflow: hidden;

  > .home-wrapper {
    position: absolute;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    min-height: 0;
    transform: none;
    transform-origin: left top;
  }
}

#app .home-scale-viewport.is-desktop-scale.is-tablet-fill {
  width: 100vw;
  height: 100vh;

  > .home-wrapper {
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    transform: none;
    transform-origin: left top;
  }
}

#app .home-wrapper:not(.mobile) {
  .main-container {
    height: 100%;
    margin-left: 272px;
  }

  .fixed-header {
    position: absolute;
    top: 16px;
    left: 48px;
    right: 34px;
    z-index: 12;
    display: flex;
    width: auto;
    pointer-events: none;
  }

  .app-main {
    height: 100%;
  }

  .home-shell {
    max-width: none;
    padding-top: 100px;
  }

  .fixed-header .navbar {
    width: 100%;
    margin: 0;
    pointer-events: auto;
  }

  .category-children,
  .large-layout-container {
    grid-template-columns: repeat(6, minmax(0, 1fr));
  }

  &.hideSidebar {
    .main-container {
      margin-left: 272px;
    }

    .sidebar-container {
      width: 232px !important;
    }
  }

  .sidebar-container {
    top: 16px;
    bottom: 16px;
    left: 16px;
    width: 232px !important;
    height: calc(100% - 32px);
    border: 1px solid var(--home-glass-border);
    border-radius: 34px;
    background:
      linear-gradient(152deg, rgba(255, 255, 255, .18), rgba(255, 255, 255, .035) 42%, rgba(255, 255, 255, .08)),
      var(--home-glass-bg-strong) !important;
    box-shadow:
      0 34px 92px rgba(0, 0, 0, .36),
      inset 0 1px 0 rgba(255, 255, 255, .38),
      inset 0 -1px 0 rgba(255, 255, 255, .08);
    -webkit-backdrop-filter: blur(46px) saturate(1.7) contrast(1.08);
    backdrop-filter: blur(46px) saturate(1.7) contrast(1.08);
  }

  .sidebar-container .has-logo,
  .sidebar-container .el-scrollbar,
  .sidebar-container .el-scrollbar__wrap,
  .sidebar-container .el-scrollbar__view {
    background: transparent !important;
  }

  .sidebar-container.has-logo .el-scrollbar {
    height: calc(100% - 164px);
  }

  .sidebar-container .el-menu {
    padding: 10px 18px 22px;
    background: transparent !important;
  }

  .sidebar-container .el-menu-item,
  .sidebar-container .el-submenu__title {
    position: relative;
    display: flex;
    align-items: center;
    gap: 11px;
    height: 58px;
    line-height: 1;
    margin: 10px 0;
    padding: 0 8px !important;
    color: rgba(236, 244, 241, .82) !important;
    background: transparent !important;
    border: none;
    font-size: 20px;
    font-weight: 700;
    border-radius: 0;
    box-shadow: none;
    -webkit-backdrop-filter: none;
    backdrop-filter: none;
    transition: background .2s ease, color .2s ease, transform .2s ease;

    &::before {
      display: none;
    }

    &::after {
      display: none;
    }

    .svg-icon {
      position: relative;
      z-index: 1;
      flex: 0 0 auto;
      margin-right: 0 !important;
      width: 34px;
      height: 34px;
      padding: 9px;
      color: rgba(255, 255, 255, .76);
      background:
        linear-gradient(145deg, rgba(255, 255, 255, .15), rgba(255, 255, 255, .055)),
        rgba(255, 255, 255, .09);
      border: 1px solid rgba(255, 255, 255, .16);
      border-radius: 50%;
      box-shadow:
        inset 0 1px 0 rgba(255, 255, 255, .18),
        0 10px 22px rgba(0, 0, 0, .14);
      box-sizing: border-box;
      transition: color .18s ease, background .18s ease, border-color .18s ease, box-shadow .18s ease, transform .18s ease;
    }

    span {
      position: relative;
      z-index: 1;
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: rgba(236, 244, 241, .64);
      transform-origin: left center;
      transition: color .18s ease, text-shadow .18s ease, transform .18s ease;
    }

    .el-submenu__icon-arrow {
      right: 14px;
      margin-top: -5px;
      color: rgba(236, 244, 241, .42);
    }

    &:hover {
      color: #ffffff !important;
      background: transparent !important;
      transform: none;

      .svg-icon {
        color: rgba(255, 255, 255, .96);
        background:
          radial-gradient(circle at 34% 28%, rgba(255, 255, 255, .38), rgba(255, 255, 255, .18) 42%, rgba(255, 255, 255, .1)),
          rgba(255, 255, 255, .14);
        border-color: rgba(255, 255, 255, .3);
        transform: scale(1.06);
        box-shadow:
          0 0 0 4px rgba(255, 255, 255, .045),
          0 0 20px rgba(255, 255, 255, .1),
          0 12px 26px rgba(0, 0, 0, .2),
          inset 0 1px 0 rgba(255, 255, 255, .26);
      }

      span {
        color: rgba(255, 255, 255, .88);
        transform: scale(1.025);
        text-shadow: 0 0 12px rgba(255, 255, 255, .14);
      }
    }
  }

  .sidebar-container .el-menu-item.is-active,
  .sidebar-container .el-menu-item.is-home-active,
  .sidebar-container .el-submenu.is-active > .el-submenu__title {
    color: #f8fffb !important;
    background: transparent !important;
    border-color: transparent;
    box-shadow: none;
    transform: none;

    &::before {
      display: none;
    }

    &::after {
      display: none;
    }

    .svg-icon {
      color: rgba(218, 103, 58, .96);
      background:
        radial-gradient(circle at 34% 28%, rgba(255, 255, 255, 1), rgba(255, 255, 255, .68) 44%, rgba(255, 244, 235, .5));
      border-color: rgba(255, 255, 255, .82);
      transform: scale(1.06);
      box-shadow:
        0 0 0 4px rgba(255, 255, 255, .055),
        0 0 24px rgba(255, 255, 255, .16),
        0 14px 28px rgba(0, 0, 0, .24),
        inset 0 0 0 1px rgba(237, 112, 64, .16),
        inset 0 1px 0 rgba(255, 255, 255, .86);
    }

    span {
      color: #ffffff;
      transform: scale(1.025);
      text-shadow:
        0 0 16px rgba(255, 255, 255, .28),
        0 0 24px rgba(237, 112, 64, .16);
    }
  }
}

#app .home-wrapper.mobile {
  --mobile-sidebar-width: 104px;
  --mobile-sidebar-offset: 116px;

  .main-container {
    height: 100%;
    width: calc(100% - var(--mobile-sidebar-offset));
    margin-left: var(--mobile-sidebar-offset) !important;
  }

  &.hideSidebar {
    .main-container {
      margin-left: var(--mobile-sidebar-offset) !important;
    }

    .sidebar-container {
      pointer-events: auto;
      transform: none;
    }
  }

  .fixed-header {
    position: absolute;
    top: 8px;
    left: 8px;
    right: 8px;
    z-index: 12;
    display: flex;
    width: auto;
    pointer-events: none;
  }

  .app-main {
    height: 100%;
  }

  .fixed-header .navbar {
    width: 100%;
    margin: 0;
    pointer-events: auto;
  }

  .home-shell {
    width: 100%;
    max-width: none;
    padding: 74px 8px 36px;
  }

  .home-shell.is-focus-mode {
    padding-top: 74px;
    padding-bottom: 520px;
  }

  .category-main {
    margin-top: 18px;

    &:first-child {
      margin-top: 4px;
    }
  }

  .category-main .category-title {
    gap: 4px;
    font-size: 18px;

    .category-title-marker {
      width: 4px;
      height: 24px;
      margin-left: -8px;
    }

    small {
      padding: 4px 8px;
      font-size: 10px;
    }
  }

  .category-children,
  .large-layout-container {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px;
    padding: 6px 0 8px;
  }

  .category-children .nav-card,
  .large-layout-container .nav-card {
    height: 58px;
    border-radius: 18px;

    .el-card__body {
      padding: 7px 8px;
    }

    .nav-card-icon {
      width: 28px !important;
      height: 28px !important;
      line-height: 28px !important;
    }

    .nav-card-content {
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: calc(100% - 36px);
      margin-left: 6px;
    }

    .card-title {
      font-size: 12.5px !important;
      line-height: 1.12;
    }

    .card-content {
      margin-top: 2px;
      font-size: 9px !important;
      line-height: 1.15;
    }
  }

  .sidebar-container {
    top: 8px;
    bottom: 8px;
    left: 8px;
    width: var(--mobile-sidebar-width) !important;
    height: calc(100% - 16px);
    border: 1px solid var(--home-glass-border);
    border-radius: 22px;
    background:
      linear-gradient(152deg, rgba(255, 255, 255, .18), rgba(255, 255, 255, .035) 42%, rgba(255, 255, 255, .08)),
      var(--home-glass-bg-strong) !important;
    box-shadow:
      0 20px 54px rgba(0, 0, 0, .36),
      inset 0 1px 0 rgba(255, 255, 255, .36),
      inset 0 -1px 0 rgba(255, 255, 255, .08);
    -webkit-backdrop-filter: blur(34px) saturate(1.6);
    backdrop-filter: blur(34px) saturate(1.6);
  }

  .sidebar-container .has-logo,
  .sidebar-container .el-scrollbar,
  .sidebar-container .el-scrollbar__wrap,
  .sidebar-container .el-scrollbar__view,
  .sidebar-container .el-menu {
    background: transparent !important;
  }

  .sidebar-container .sidebar-logo-container {
    height: 50px;
    line-height: 50px;
  }

  .sidebar-container .sidebar-logo-link {
    padding: 5px 4px 0;
  }

  .sidebar-container .sidebar-title {
    max-width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 10.8px;
    line-height: 1.1;
  }

  .sidebar-container.has-logo .el-scrollbar {
    height: calc(100% - 96px);
  }

  .sidebar-container .el-menu {
    padding: 4px 6px 10px;
  }

  .sidebar-container .el-menu-item,
  .sidebar-container .el-submenu__title {
    display: flex;
    align-items: center;
    gap: 5px;
    height: 42px;
    line-height: 1;
    margin: 4px 0;
    padding: 0 4px !important;
    color: rgba(236, 244, 241, .78) !important;
    background: transparent !important;
    border: none;
    font-size: 11.5px;
    font-weight: 800;
    box-shadow: none;

    .svg-icon {
      flex: 0 0 auto;
      width: 24px;
      height: 24px;
      margin-right: 0 !important;
      padding: 6px;
      color: rgba(255, 255, 255, .76);
      background:
        linear-gradient(145deg, rgba(255, 255, 255, .15), rgba(255, 255, 255, .055)),
        rgba(255, 255, 255, .09);
      border: 1px solid rgba(255, 255, 255, .16);
      border-radius: 50%;
      box-sizing: border-box;
    }

    span {
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      color: rgba(236, 244, 241, .68);
    }
  }

  .sidebar-container .el-menu-item.is-active,
  .sidebar-container .el-menu-item.is-home-active,
  .sidebar-container .el-submenu.is-active > .el-submenu__title {
    color: #ffffff !important;
    background: transparent !important;

    .svg-icon {
      color: rgba(218, 103, 58, .96);
      background:
        radial-gradient(circle at 34% 28%, rgba(255, 255, 255, 1), rgba(255, 255, 255, .68) 44%, rgba(255, 244, 235, .5));
      border-color: rgba(255, 255, 255, .82);
    }

    span {
      color: #ffffff;
    }
  }

  .sidebar-user-panel {
    left: 7px;
    right: 7px;
    bottom: 8px;
  }

  .sidebar-user-trigger {
    height: 34px;
    padding: 0 8px;
    border-radius: 16px;
  }

  .sidebar-user-name {
    max-width: 78px;
    font-size: 11px;
  }

  .home-access-stats {
    left: var(--mobile-sidebar-offset);
    bottom: 12px;
    height: 28px;
    padding: 0 8px;
    gap: 6px;
    font-size: 10px;
  }
}

#app .home-wrapper.is-tablet-fill:not(.mobile):not(.is-desktop-portrait) {
  .category-children,
  .large-layout-container {
    grid-template-columns: repeat(6, minmax(0, 1fr));
  }
}

@media (min-width: 2000px) {
  #app .home-wrapper:not(.mobile):not(.is-desktop-portrait) {
    .category-children,
    .large-layout-container {
      grid-template-columns: repeat(7, minmax(0, 1fr));
    }
  }
}

#app .home-wrapper.is-tablet-fill:not(.mobile) {
  .category-children .nav-card .card-title,
  .large-layout-container .card-title {
    font-size: 16px !important;
  }

  .category-children .nav-card .card-content,
  .large-layout-container .card-content {
    font-size: 10px !important;
  }
}

#app .home-wrapper.is-desktop-portrait:not(.mobile) {
  .main-container {
    margin-left: 272px;
  }

  .app-main {
    height: 100%;
  }

  .home-shell {
    width: 100%;
    max-width: none;
    padding: 20px 28px 56px 36px;
  }

  .home-shell:not(.is-focus-mode) {
    padding-top: 104px;
  }

  .home-shell.is-focus-mode {
    padding-top: 122px;
    padding-bottom: 1120px;
  }

  .category-children,
  .large-layout-container {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .category-children .nav-card {
    height: 72px;
  }

  .sidebar-container {
    top: 16px;
    bottom: 16px;
    height: calc(100% - 32px);
  }
}
</style>
