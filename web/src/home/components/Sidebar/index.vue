<template>
  <div class="has-logo"
       :style="{ backgroundColor: settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground }">
    <logo :collapse="isCollapse"/>
    <div class="sidebar-access-stats">
      <span>今日访问 {{ todayAccessStats.visitCount || 0 }}</span>
      <span>今日登录 {{ todayAccessStats.loginCount || 0 }}</span>
    </div>
    <el-scrollbar :class="settings.sideTheme" wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground"
        :text-color="settings.sideTheme === 'theme-dark' ? variables.menuColor : variables.menuLightColor"
        :unique-opened="settings.menuAccordion"
        :active-text-color="settings.theme"
        :default-openeds="openMenus"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="item in homeMenus"
          :key="item.id"
          :item="item"
        />
      </el-menu>
    </el-scrollbar>
    <div class="sidebar-user-panel">
      <div v-if="!loginUser.id" class="sidebar-user-trigger" @click="openLogin">
        <div class="sidebar-user-meta">
          <span class="sidebar-user-name" v-text="displayName"></span>
        </div>
      </div>
      <el-dropdown v-else class="sidebar-user-dropdown" trigger="click">
        <div class="sidebar-user-trigger">
          <div class="sidebar-user-meta">
            <span class="sidebar-user-name" v-text="displayName"></span>
          </div>
          <i class="el-icon-arrow-up sidebar-user-arrow"></i>
        </div>
        <el-dropdown-menu slot="dropdown" class="home-user-menu">
          <router-link v-if="loginUser.isAdmin" to="/admin/category">
            <el-dropdown-item>后台管理</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="openChangePassword">修改密码</el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <change-password ref="changePassword"></change-password>
    </div>
  </div>
</template>

<script>
import {mapGetters, mapState} from "vuex";
import Vue from "vue";
import LoginDialog, {isRelogin} from "@/components/login-dialog";
import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import variables from "@/assets/styles/variables.scss";

export default {
  components: {
    SidebarItem,
    Logo,
    'change-password': () => import('@/home/components/change-password'),
  },
  props: {
    todayAccessStats: {
      type: Object,
      default: () => ({
        visitCount: 0,
        loginCount: 0
      })
    }
  },
  computed: {
    ...mapState(["settings"]),
    ...mapGetters([
      'sidebar',
      'homeMenus',
      'loginUser',
    ]),
    displayName() {
      const user = this.loginUser || {};
      return user.nickname || user.username || '未登录';
    },
    openMenus() {
      if (this.$store.state.settings.menuDefaultOpen) {
        return this.$store.state.home.openIds;
      }
      return [];
    },
    activeMenu() {
      const route = this.$route;
      const {meta} = route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return this.$store.state.home.activeCategoryId;
    },
    variables() {
      return variables;
    },
    isCollapse() {
      return !this.sidebar.opened;
    }
  },
  mounted() {
    if (!this.$store.state.settings.version.currentVersion) {
      this.$store.dispatch('settings/loadVersion');
    }
  },
  methods: {
    openLogin() {
      if (isRelogin.show) {
        return;
      }
      isRelogin.show = true;
      const Constructor = Vue.extend(LoginDialog);
      const instance = new Constructor();
      instance.$mount();
      document.body.appendChild(instance.$el);
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
    },
    openChangePassword() {
      this.$refs.changePassword.open();
    }
  }
};
</script>

<style lang="scss" scoped>
.has-logo {
  position: relative;
  height: 100%;
}

.sidebar-user-panel {
  position: absolute;
  left: 18px;
  right: 18px;
  bottom: 18px;
  z-index: 2;
}

.sidebar-access-stats {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  min-height: 32px;
  padding: 2px 14px 0;
  color: rgba(236, 244, 241, .62);
  font-size: 12px;
  line-height: 1.2;
  font-weight: 800;
  white-space: nowrap;
  pointer-events: none;
}

.sidebar-user-dropdown {
  display: block;
  width: 100%;
}

.sidebar-user-trigger {
  display: flex;
  align-items: center;
  width: 100%;
  min-width: 0;
  height: 48px;
  padding: 0 14px;
  color: rgba(255, 255, 255, .88);
  cursor: pointer;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, .28);
  border-radius: 22px;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, .18), rgba(255, 255, 255, .045) 48%, rgba(255, 255, 255, .1)),
    rgba(8, 15, 15, .36);
  box-shadow:
    0 18px 42px rgba(0, 0, 0, .26),
    inset 0 1px 0 rgba(255, 255, 255, .32),
    inset 0 -1px 0 rgba(255, 255, 255, .08);
  -webkit-backdrop-filter: blur(34px) saturate(1.55);
  backdrop-filter: blur(34px) saturate(1.55);
  transition: border-color .18s ease, box-shadow .18s ease, transform .18s ease, background .18s ease;

  &:hover {
    transform: translateY(-2px);
    border-color: rgba(255, 255, 255, .58);
    background:
      linear-gradient(145deg, rgba(255, 255, 255, .26), rgba(255, 255, 255, .07) 48%, rgba(255, 255, 255, .14)),
      rgba(8, 15, 15, .48);
    box-shadow:
      0 0 0 3px rgba(255, 255, 255, .08),
      0 24px 54px rgba(0, 0, 0, .32),
      inset 0 1px 0 rgba(255, 255, 255, .46);
  }
}

.sidebar-user-meta {
  min-width: 0;
  flex: 1 1 auto;
}

.sidebar-user-name {
  display: block;
  max-width: 148px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
  line-height: 1;
  font-weight: 800;
}

.sidebar-user-arrow {
  flex: 0 0 auto;
  margin-left: auto;
  color: rgba(255, 255, 255, .54);
  font-size: 12px;
}
</style>

<style lang="scss">
.home-user-menu.el-dropdown-menu {
  min-width: 136px;
  padding: 9px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, .38);
  border-radius: 18px;
  background:
    linear-gradient(148deg, rgba(255, 255, 255, .24), rgba(255, 255, 255, .07) 46%, rgba(255, 255, 255, .12)),
    rgba(8, 15, 15, .52);
  box-shadow:
    0 26px 62px rgba(0, 0, 0, .34),
    inset 0 1px 0 rgba(255, 255, 255, .46),
    inset 0 -1px 0 rgba(255, 255, 255, .08);
  -webkit-backdrop-filter: blur(42px) saturate(1.7) contrast(1.08);
  backdrop-filter: blur(42px) saturate(1.7) contrast(1.08);
}

.home-user-menu.el-dropdown-menu::before {
  content: "";
  position: absolute;
  inset: 0;
  z-index: -1;
  pointer-events: none;
  background:
    radial-gradient(circle at 18% 18%, rgba(255, 255, 255, .25), transparent 34%),
    radial-gradient(circle at 86% 90%, rgba(237, 112, 64, .18), transparent 36%);
}

.home-user-menu.el-dropdown-menu[x-placement^="top"] .popper__arrow,
.home-user-menu.el-dropdown-menu[x-placement^="bottom"] .popper__arrow {
  border-top-color: rgba(255, 255, 255, .38);
  border-bottom-color: rgba(255, 255, 255, .38);
}

.home-user-menu.el-dropdown-menu[x-placement^="top"] .popper__arrow::after {
  border-top-color: rgba(8, 15, 15, .78);
}

.home-user-menu.el-dropdown-menu[x-placement^="bottom"] .popper__arrow::after {
  border-bottom-color: rgba(8, 15, 15, .78);
}

.home-user-menu .el-dropdown-menu__item {
  height: 42px;
  padding: 0 14px;
  color: rgba(255, 255, 255, .88);
  border-radius: 13px;
  font-size: 17px;
  line-height: 42px;
  font-weight: 800;
  letter-spacing: 0;
  text-shadow: 0 1px 12px rgba(0, 0, 0, .24);
  transition: color .18s ease, background .18s ease, box-shadow .18s ease, transform .18s ease;
}

.home-user-menu .el-dropdown-menu__item:hover,
.home-user-menu .el-dropdown-menu__item:focus {
  color: #ffffff;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, .28), rgba(255, 255, 255, .1)),
    rgba(255, 255, 255, .1);
  box-shadow:
    0 0 0 1px rgba(255, 255, 255, .14),
    inset 0 1px 0 rgba(255, 255, 255, .34);
  transform: translateX(2px);
}

.home-user-menu .el-dropdown-menu__item--divided {
  margin-top: 8px;
  border-top: 1px solid rgba(255, 255, 255, .14);
}

.home-user-menu .el-dropdown-menu__item--divided::before {
  display: none;
}
</style>
