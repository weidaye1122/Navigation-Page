<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container"
               @toggleClick="toggleSideBar"/>

    <div class="right-menu" v-if="version.currentVersion">
      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <header-avatar></header-avatar>
          <i class="el-icon-caret-bottom"/>
        </div>
        <el-dropdown-menu slot="dropdown">
          <a href="https://github.com/weidaye1122/Navigation-Page" target="_blank">
            <el-badge is-dot :hidden="!version.hasNewVersion"
                      :title="'最新版本：'+version.remoteVersion">
              <el-dropdown-item>版本：{{ version.currentVersion }}</el-dropdown-item>
            </el-badge>
          </a>
          <a target="_blank" href="https://github.com/weidaye1122/Navigation-Page/issues">
            <el-dropdown-item>问题反馈</el-dropdown-item>
          </a>
          <el-dropdown-item divided @click.native="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'

export default {
  components: {
    Hamburger: () => import('@/components/Hamburger'),
    'header-avatar': () => import('@/components/header-avatar'),
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'version',
    ]),
  },
  mounted() {
    this.$store.dispatch('settings/loadVersion');
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('user/LogOut').then(() => {
          this.$store.dispatch('settings/loadVersion');
        })
      }).catch(() => {
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 56px;
  overflow: visible;
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid rgba(28, 42, 58, .10);
  border-radius: 18px;
  background: rgba(255, 255, 255, .74);
  backdrop-filter: blur(24px) saturate(1.22);
  -webkit-backdrop-filter: blur(24px) saturate(1.22);
  box-shadow:
    0 18px 46px rgba(31, 45, 61, .10),
    inset 0 1px 0 rgba(255, 255, 255, .76);

  .hamburger-container {
    width: 42px;
    height: 42px;
    line-height: 42px;
    margin-left: 8px;
    cursor: pointer;
    border-radius: 14px;
    color: #324054;
    transition: background .18s ease, box-shadow .18s ease, color .18s ease;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      color: #1d2a3a;
      background: rgba(79, 124, 255, .12);
      box-shadow: inset 0 1px 0 rgba(255, 255, 255, .72);
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .topmenu-container {
    position: absolute;
    left: 50px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    display: flex;
    align-items: center;
    gap: 10px;
    height: 100%;
    line-height: 1;
    margin-left: auto;
    padding-right: 12px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      height: 42px;
      font-size: 18px;
      color: #354257;
      border-radius: 14px;
      background: rgba(255, 255, 255, .44);
      box-shadow: inset 0 1px 0 rgba(255, 255, 255, .7);

      &.hover-effect {
        cursor: pointer;
        transition: background .18s ease, box-shadow .18s ease, color .18s ease, transform .18s ease;

        &:hover {
          color: #1e2d40;
          background: rgba(79, 124, 255, .12);
          transform: translateY(-1px);
          box-shadow:
            0 10px 22px rgba(31, 45, 61, .08),
            inset 0 1px 0 rgba(255, 255, 255, .82);
        }
      }
    }

    .avatar-container {
      min-width: 64px;
      margin-right: 0;

      .avatar-wrapper {
        position: relative;
        display: flex;
        align-items: center;
        gap: 8px;
        height: 100%;
        padding: 0 10px 0 4px;

        .user-avatar {
          cursor: pointer;
          width: 34px;
          height: 34px;
          border-radius: 12px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: static;
          font-size: 12px;
          color: #6b7586;
        }
      }
    }
  }
}
</style>
