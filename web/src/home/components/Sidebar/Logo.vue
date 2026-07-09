<template>
  <div class="sidebar-logo-container" :class="{'collapse':collapse}" :style="{ backgroundColor: menuBackground }">
    <transition name="sidebarLogoFade">
      <router-link v-if="collapse" key="collapse" class="sidebar-logo-link" to="/" @click.native="restoreHomeView">
        <div class="sidebar-logo-copy">
          <h1 class="sidebar-title" :style="{ color: logoTitleColor }">{{ title }}</h1>
          <p class="sidebar-version">{{ versionLabel }}</p>
        </div>
      </router-link>
      <router-link v-else key="expand" class="sidebar-logo-link" to="/" @click.native="restoreHomeView">
        <div class="sidebar-logo-copy">
          <h1 class="sidebar-title" :style="{ color: logoTitleColor }">{{ title }}</h1>
          <p class="sidebar-version">{{ versionLabel }}</p>
        </div>
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
    menuBackground(){
      return this.$store.state.settings.sideTheme === 'theme-dark' ? variables.menuBackground : variables.menuLightBackground
    },
    logoTitleColor(){
      return this.$store.state.settings.sideTheme === 'theme-dark' ? variables.logoTitleColor : variables.logoLightTitleColor
    },
    title() {
      return this.$store.state.settings.title;
    },
    versionLabel() {
      const currentVersion = this.$store.state.settings.version.currentVersion || '1.0.1';
      return `V ${String(currentVersion).replace(/^v/i, '')}`;
    }
  },
  methods: {
    restoreHomeView() {
      this.cancelHomeScrollAnimation();
      const restore = () => {
        this.$store.commit('home/SET_ACTIVE_CATEGORY', '');
        this.$nextTick(() => {
          this.resetHomeScroll();
          requestAnimationFrame(this.resetHomeScroll);
        });
      };
      this.clearSearchBeforeSidebarAction().then(restore).catch(restore);
    },
    clearSearchBeforeSidebarAction() {
      if (!this.$store.state.home.searchKeywords) {
        return Promise.resolve();
      }
      return this.$store.dispatch('home/clearSearchAndLoadHomeCards');
    },
    resetHomeScroll() {
      const scroller = document.querySelector('.app-main');
      if (scroller) {
        scroller.scrollTop = 0;
      }
    },
    cancelHomeScrollAnimation() {
      if (window.__teamNavHomeScrollFrame) {
        cancelAnimationFrame(window.__teamNavHomeScrollFrame);
      }
      window.__teamNavHomeScrollFrame = null;
      window.__teamNavHomeScrollToken = (window.__teamNavHomeScrollToken || 0) + 1;
    },
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
  height: 132px;
  background: transparent !important;
  text-align: center;
  overflow: visible;
  border-bottom: 1px solid rgba(255, 255, 255, .08);

  & .sidebar-logo-link {
    position: relative;
    display: grid;
    place-items: center;
    height: 100%;
    width: 100%;
    padding: 0 14px;
    box-sizing: border-box;
    color: inherit;
    text-decoration: none;
    transition: transform .18s ease, filter .18s ease;
    outline: none;

    & .sidebar-logo-copy {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 8px;
      min-height: 100%;
      transform: translateY(0);
    }

    &::before {
      display: none;
    }

    &::after {
      display: none;
    }

    &:hover {
      transform: none;
      filter: none;

      .sidebar-title {
        color: #ffffff !important;
        transform: scale(1.045);
        text-shadow:
          0 0 9px rgba(255, 255, 255, .32),
          0 0 18px rgba(255, 255, 255, .18),
          0 0 28px rgba(142, 204, 226, .12);
      }
    }

    &:focus:not(:hover),
    &:active:not(:hover) {
      transform: none;
      filter: none;

      &::before,
      &::after {
        opacity: 0;
      }

      .sidebar-title {
        text-shadow: none;
        transform: none;
      }
    }

    & .sidebar-title {
      position: relative;
      z-index: 1;
      display: inline-block;
      min-width: 0;
      max-width: 100%;
      margin: 0;
      color: #fff;
      font-weight: 800;
      line-height: 1.15;
      font-size: 24px;
      font-family: Inter, Avenir, Helvetica Neue, Arial, Helvetica, sans-serif;
      text-align: center;
      vertical-align: middle;
      overflow: visible;
      text-overflow: clip;
      white-space: nowrap;
      transform-origin: center;
      transition: color .18s ease, text-shadow .18s ease, transform .18s ease;
    }

    & .sidebar-version {
      margin: 0;
      color: rgba(236, 244, 241, .64);
      font-size: 12px;
      line-height: 1;
      font-weight: 700;
      letter-spacing: .12em;
      text-transform: uppercase;
      white-space: nowrap;
    }

  }

  &.collapse {
    text-align: center;

    .sidebar-logo-link {
      justify-content: center;
      padding: 0;
    }

    .sidebar-title {
      display: none;
    }

    .sidebar-version {
      display: none;
    }

  }
}
</style>
