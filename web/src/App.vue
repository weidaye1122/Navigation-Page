<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
const APP_ICON_PATH = '/app-icon.png';
const FAVICON_PATH = '/favicon.png';

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
      this.$store.dispatch('settings/loadSettings').then(() => {
        this.setHeadLink('icon', FAVICON_PATH);
        this.setHeadLink('shortcut icon', FAVICON_PATH);
        this.setHeadLink('apple-touch-icon', APP_ICON_PATH);
        this.setHeadLink('apple-touch-icon-precomposed', APP_ICON_PATH);
        this.setMeta('apple-mobile-web-app-title', this.$store.state.settings.title);
      })
    },
    setHeadLink(rel, href) {
      let link = document.head.querySelector(`link[rel="${rel}"]`);
      if (!link) {
        link = document.createElement('link');
        link.rel = rel;
        document.head.appendChild(link);
      }
      link.removeAttribute('sizes');
      link.type = href.endsWith('.png') ? 'image/png' : '';
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
