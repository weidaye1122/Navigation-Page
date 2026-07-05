<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}"
       :style="{ backgroundColor: variables.menuBackground }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/">
        <h1 class="sidebar-title" :style="{ color: variables.logoTitleColor }">{{ title }} </h1>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/">
        <h1 class="sidebar-title" :style="{ color: variables.logoTitleColor }">{{ title }} </h1>
      </router-link>
    </transition>
  </div>
</template>

<script>
import variables from '@/assets/styles/variables.scss'

export default {
  name: 'SidebarLogo',
  props: {
    collapse: {
      type: Boolean,
      required: true
    }
  },
  computed: {
    variables() {
      return variables;
    },
    sideTheme() {
      return this.$store.state.settings.sideTheme
    },
    title() {
      return this.$store.state.settings.title;
    }
  },
}
</script>

<style lang="scss" scoped>
.sidebarLogoFade-enter-active {
  transition: opacity 1.5s;
}

.sidebarLogoFade-enter,
.sidebarLogoFade-leave-to {
  opacity: 0;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 88px;
  padding: 20px 14px 10px;
  line-height: 1;
  background: transparent !important;
  text-align: center;
  overflow: visible;

  & .sidebar-logo-link {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 56px;
    width: 100%;
    padding: 0;
    color: inherit;
    text-decoration: none;
    transition: transform .18s ease, filter .18s ease;

    &:hover {
      transform: scale(1.035);
      filter: drop-shadow(0 0 12px rgba(79, 124, 255, .18));
    }

    & .sidebar-title {
      display: inline-block;
      min-width: 0;
      margin: 0;
      color: #233044 !important;
      font-weight: 900;
      line-height: 1.1;
      font-size: 22px;
      font-family: Inter, Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      letter-spacing: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  &.collapse {
    padding: 14px 8px 10px;

    .sidebar-logo-link {
      justify-content: center;
      padding: 0;
    }

    .sidebar-title {
      display: none;
    }
  }
}
</style>
