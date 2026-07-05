<template>
  <div class="has-logo" :style="{ backgroundColor: variables.menuBackground }">
    <logo :collapse="isCollapse"/>
    <el-scrollbar class="theme-dark" wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :background-color="variables.menuBackground"
        :text-color="variables.menuColor"
        :unique-opened="true"
        active-text-color="#409EFF"
        :collapse-transition="false"
        mode="vertical"
      >
        <sidebar-item
          v-for="(route, index) in sidebarRouters"
          :key="route.path  + index"
          :item="route"
          :base-path="route.path"
        />
      </el-menu>
    </el-scrollbar>
    <div class="admin-user-panel">
      <el-dropdown class="admin-user-dropdown" trigger="click">
        <div class="admin-user-trigger">
          <span class="admin-user-name" v-text="displayName"></span>
        </div>
        <el-dropdown-menu slot="dropdown" class="admin-user-menu">
          <el-dropdown-item @click.native="$router.push('/')">前台首页</el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import {mapGetters} from "vuex";
import Logo from "./Logo";
import SidebarItem from "./SidebarItem";
import variables from "@/assets/styles/variables.scss";

export default {
  components: {SidebarItem, Logo},
  data() {
    return {
      sidebarRouters: [
        {
          path: '/admin/category',
          name: 'category',
          meta: {title: '分类管理'},
        },
        {
          path: '/admin/card/list',
          name: 'card',
          meta: {title: '卡片管理'},
        },
        {
          path: '/admin/role',
          name: 'role',
          meta: {title: '角色管理'}
        },
        {
          path: '/admin/user',
          name: 'user',
          meta: {title: '用户管理'}
        },
        {
          path: '/admin/setting',
          name: 'setting',
          meta: {title: '系统设置'}
        },
        {
          path: '/admin/category/history',
          name: 'recycle',
          meta: {title: '回收站'}
        },
      ]
    }
  },
  computed: {
    ...mapGetters(["sidebar", "loginUser"]),
    displayName() {
      const user = this.loginUser || {};
      return user.nickname || user.username || '未登录';
    },
    activeMenu() {
      const route = this.$route;
      const {meta, path} = route;
      // if set path, the sidebar will highlight the path you set
      if (meta.activeMenu) {
        return meta.activeMenu;
      }
      return path;
    },
    variables() {
      return variables;
    },
    isCollapse() {
      return false;
    }
  },
  methods: {
    async logout() {
      this.$confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('user/LogOut').then(() => {
          this.$router.push('/');
          this.$store.dispatch('settings/loadVersion');
        })
      }).catch(() => {
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.has-logo {
  position: relative;
  height: 100%;
}

.admin-user-panel {
  position: absolute;
  left: 18px;
  right: 18px;
  bottom: 18px;
  z-index: 2;
}

.admin-user-dropdown {
  display: block;
  width: 100%;
}

.admin-user-trigger {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  min-width: 0;
  height: 48px;
  padding: 0 16px;
  color: rgba(35, 48, 68, .82);
  cursor: pointer;
  overflow: hidden;
  border: 0;
  border-radius: 18px;
  background: rgba(255, 255, 255, .48);
  box-shadow:
    0 12px 28px rgba(31, 45, 61, .08),
    inset 0 1px 0 rgba(255, 255, 255, .64);
  transition: background .18s ease, box-shadow .18s ease, transform .18s ease, color .18s ease;

  &:hover {
    color: #1f2c3f;
    transform: translateY(-1px);
    background: rgba(255, 255, 255, .66);
    box-shadow:
      0 16px 34px rgba(31, 45, 61, .11),
      inset 0 1px 0 rgba(255, 255, 255, .78);
  }
}

.admin-user-name {
  display: block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 16px;
  line-height: 1;
  font-weight: 900;
  text-align: center;
}
</style>

<style lang="scss">
.admin-user-menu.el-dropdown-menu {
  min-width: 128px;
  padding: 8px;
  overflow: hidden;
  border: 0;
  border-radius: 16px;
  background: rgba(255, 255, 255, .86);
  box-shadow:
    0 22px 54px rgba(31, 45, 61, .18),
    inset 0 1px 0 rgba(255, 255, 255, .76);
  -webkit-backdrop-filter: blur(28px) saturate(1.28);
  backdrop-filter: blur(28px) saturate(1.28);
}

.admin-user-menu .el-dropdown-menu__item {
  height: 38px;
  padding: 0 13px;
  color: #263244;
  border-radius: 12px;
  font-size: 15px;
  line-height: 38px;
  font-weight: 800;
}

.admin-user-menu .el-dropdown-menu__item:hover,
.admin-user-menu .el-dropdown-menu__item:focus {
  color: #1f2c3f;
  background: rgba(79, 124, 255, .1);
}
</style>
