<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
const DEFAULT_ICON_PATH = '/favicon.ico';
const LEGACY_DEFAULT_LOGO_PATH = '/assets/images/logo.png';

export default {
  name: "App",
  metaInfo() {
    return {
      title: this.$store.state.settings.title,
    }
  },
  mounted() {
    this.loadSettings();
    this.loadUserInfo();
  },
  methods: {
    loadUserInfo() {
      this.$store.dispatch('user/GetUserInfo');
    },
    loadSettings() {
      this.$store.dispatch('settings/loadSettings').then(res => {
        const iconPath = this.normalizeIconPath(res.logoPath);
        this.setHeadLink('icon', iconPath);
        this.setHeadLink('shortcut icon', iconPath);
        this.setHeadLink('apple-touch-icon', iconPath);
        this.setHeadLink('apple-touch-icon-precomposed', iconPath);
        this.setMeta('apple-mobile-web-app-title', this.$store.state.settings.title);
      })
    },
    normalizeIconPath(iconPath) {
      return iconPath && iconPath !== LEGACY_DEFAULT_LOGO_PATH ? iconPath : DEFAULT_ICON_PATH;
    },
    setHeadLink(rel, href) {
      let link = document.head.querySelector(`link[rel="${rel}"]`);
      if (!link) {
        link = document.createElement('link');
        link.rel = rel;
        document.head.appendChild(link);
      }
      link.removeAttribute('sizes');
      link.href = href;
    },
    setMeta(name, content) {
      let meta = document.head.querySelector(`meta[name="${name}"]`);
      if (!meta) {
        meta = document.createElement('meta');
        meta.name = name;
        document.head.appendChild(meta);
      }
      meta.content = content || '';
    }
  }
};
</script>
