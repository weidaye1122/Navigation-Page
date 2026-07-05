<template>
  <div class="navbar" style="display: flex">
    <carousel-flip-notice class="carousel-flip-notice"></carousel-flip-notice>
    <div v-if="device!=='mobile' && showDoc" class="right-menu">
      <doc class="right-menu-item hover-effect"/>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'

export default {
  components: {
    'doc': () => import('@/home/components/doc'),
    'carousel-flip-notice': () => import('@/home/components/carousel-flip-notice'),
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'device',
      'showDoc'
    ]),
  },
}
</script>

<style lang="scss" scoped>
.navbar {
  display: flex;
  height: 64px;
  overflow: hidden;
  position: relative;
  align-items: center;
  margin: 16px 18px 0 0;
  padding: 0 14px;
  background:
    linear-gradient(136deg, rgba(255, 255, 255, .18), rgba(255, 255, 255, .04) 48%, rgba(255, 255, 255, .08)),
    var(--home-glass-bg);
  border: 1px solid var(--home-glass-border);
  border-radius: 26px;
  box-shadow:
    0 22px 62px rgba(0, 0, 0, .28),
    inset 0 1px 0 rgba(255, 255, 255, .36),
    inset 0 -1px 0 rgba(255, 255, 255, .08);
  -webkit-backdrop-filter: blur(42px) saturate(1.7) contrast(1.08);
  backdrop-filter: blur(42px) saturate(1.7) contrast(1.08);

  .hamburger-container {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    width: 58px;
    float: left;
    cursor: pointer;
    color: rgba(255, 255, 255, .78);
    transition: background .2s ease, color .2s ease;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      color: #ffffff;
      background: rgba(255, 255, 255, .12);
    }
  }

  .carousel-flip-notice {
    flex: auto;
    margin: 0 18px 0 2px;
    color: rgba(255, 255, 255, .78);

    ::v-deep a,
    ::v-deep span,
    ::v-deep div {
      color: inherit;
    }
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 60px;
    display: flex;
    align-items: center;
    margin-left: auto;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0 8px;
      height: 40px;
      font-size: 18px;
      color: rgba(255, 255, 255, .76);
      vertical-align: text-bottom;
      border-radius: 18px;

      &.hover-effect {
        cursor: pointer;
        transition: background .2s ease, color .2s ease;

        &:hover {
          color: #ffffff;
          background: rgba(255, 255, 255, .12);
        }
      }
    }

  }
}

@media (max-width: 900px) {
  .navbar {
    .carousel-flip-notice {
      display: none;
    }
  }
}
</style>
