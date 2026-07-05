<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
const APPLE_TOUCH_ICON_PATH = '/apple-touch-icon.png';
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
        this.setHeadLink('shortcut icon', '/favicon.ico');
        this.setHeadLink('apple-touch-icon', APPLE_TOUCH_ICON_PATH, '180x180');
        this.setHeadLink('apple-touch-icon-precomposed', APPLE_TOUCH_ICON_PATH, '180x180');
        this.setMeta('apple-mobile-web-app-title', this.$store.state.settings.title);
      })
    },
    setHeadLink(rel, href, sizes = '') {
      let link = document.head.querySelector(`link[rel="${rel}"]`);
      if (!link) {
        link = document.createElement('link');
        link.rel = rel;
        document.head.appendChild(link);
      }
      if (sizes) {
        link.setAttribute('sizes', sizes);
      } else {
        link.removeAttribute('sizes');
      }
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
