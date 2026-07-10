<template>
  <div class="navbar">
    <div class="search-panel">
      <el-input
        ref="searchInput"
        v-model="searchKeywords"
        class="home-search-input"
        autocomplete="off"
        clearable
        name="NavigationPage-home-search"
        placeholder="搜索"
        @clear="exitSearch(true)"
        @keydown.native.esc.stop.prevent="exitSearch"
        @input="searchInputHandler">
        <i slot="prefix" class="el-icon-search el-input__icon"></i>
      </el-input>
    </div>
    <div class="nav-middle">
      <div class="datetime-pill">
        <i class="el-icon-time datetime-icon"></i>
        <span v-text="currentDateTime"></span>
      </div>
    </div>
    <div class="right-tools">
      <doc v-if="device!=='mobile' && showDoc" class="top-action hover-effect"/>
    </div>
  </div>
</template>

<script>
import {mapGetters} from 'vuex'

export default {
  components: {
    'doc': () => import('@/home/components/doc'),
  },
  data() {
    return {
      currentDateTime: '',
      clockTimer: null,
      searchTimer: null,
    };
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'device',
      'showDoc'
    ]),
    searchKeywords: {
      get() {
        return this.$store.state.home.searchKeywords;
      },
      set(value) {
        this.$store.commit('home/SET_SEARCH_KEYWORDS', value);
      },
    },
  },
  watch: {
    searchKeywords(nextKeywords, prevKeywords) {
      if (!nextKeywords && prevKeywords && this.searchTimer) {
        clearTimeout(this.searchTimer);
        this.searchTimer = null;
      }
    },
  },
  mounted() {
    this.updateCurrentDateTime();
    this.clockTimer = setInterval(this.updateCurrentDateTime, 1000);
  },
  beforeDestroy() {
    if (this.searchTimer) {
      clearTimeout(this.searchTimer);
    }
    if (this.clockTimer) {
      clearInterval(this.clockTimer);
    }
  },
  methods: {
    updateCurrentDateTime() {
      const now = new Date();
      const weekList = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
      const year = now.getFullYear();
      const month = this.padTime(now.getMonth() + 1);
      const day = this.padTime(now.getDate());
      const week = weekList[now.getDay()];
      const hours = this.padTime(now.getHours());
      const minutes = this.padTime(now.getMinutes());
      const seconds = this.padTime(now.getSeconds());
      this.currentDateTime = `${year}年${month}月${day}日 ${week} ${hours}:${minutes}:${seconds}`;
    },
    padTime(value) {
      return String(value).padStart(2, '0');
    },
    searchInputHandler() {
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
      }
      this.searchTimer = setTimeout(() => {
        this.searchTimer = null;
        this.searchHomeCards();
      }, 180);
    },
    searchHomeCards() {
      this.cancelHomeScrollAnimation();
      this.$store.commit('home/SET_ACTIVE_CATEGORY', '');
      this.$store.dispatch('home/loadHomeCards', this.searchKeywords.trim()).then(() => {
        this.$nextTick(() => {
          const scroller = document.querySelector('.app-main');
          if (scroller) {
            scroller.scrollTop = 0;
          }
        });
      }).catch(() => {});
    },
    exitSearch(forceReload) {
      if (this.searchTimer) {
        clearTimeout(this.searchTimer);
        this.searchTimer = null;
      }
      const hadKeywords = !!this.searchKeywords;
      if (hadKeywords) {
        this.searchKeywords = '';
      }
      if (hadKeywords || forceReload) {
        this.searchHomeCards();
      }
      this.blurSearchInput();
    },
    blurSearchInput() {
      if (this.$refs.searchInput) {
        this.$refs.searchInput.blur();
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
.navbar {
  display: flex;
  height: 64px;
  overflow: hidden;
  position: relative;
  align-items: center;
  gap: 12px;
  margin: 16px 18px 0 0;
  padding: 0 12px;
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

  .search-panel {
    display: flex;
    align-items: center;
    flex: 1 1 auto;
    width: auto;
    min-width: 0;
    height: 46px;
    overflow: hidden;
    border: 1px solid rgba(255, 255, 255, .16);
    border-radius: 23px;
    background: rgba(255, 255, 255, .08);
    transition: background .18s ease, border-color .18s ease, box-shadow .18s ease;

    &:focus-within {
      border-color: rgba(255, 255, 255, .34);
      background: rgba(255, 255, 255, .12);
      box-shadow: inset 0 1px 0 rgba(255, 255, 255, .18);
    }
  }

  .nav-middle {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    flex: 0 0 auto;
    min-width: 0;
    height: 46px;
  }

  .datetime-pill {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    flex: 0 0 auto;
    min-width: 0;
    height: 46px;
    padding: 0 4px 0 18px;
    color: rgba(236, 244, 241, .76);
    font-size: 15px;
    font-weight: 800;
    letter-spacing: 0;
    white-space: nowrap;
    box-sizing: border-box;
  }

  .datetime-icon {
    flex: 0 0 auto;
    margin-right: 10px;
    color: rgba(226, 238, 234, .58);
    font-size: 16px;
  }

  .right-tools {
    display: flex;
    align-items: center;
    flex: 0 0 auto;
    gap: 10px;
    margin-left: auto;

    .top-action {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 42px;
      height: 42px;
      font-size: 18px;
      color: rgba(255, 255, 255, .76);
      text-decoration: none;
      vertical-align: text-bottom;
      border: 1px solid rgba(255, 255, 255, .14);
      border-radius: 50%;
      background: rgba(255, 255, 255, .07);
    }

    .top-action.hover-effect {
      cursor: pointer;
      transition: background .2s ease, border-color .2s ease, color .2s ease;
    }

    .top-action.hover-effect:hover {
      color: #ffffff;
      border-color: rgba(255, 255, 255, .3);
      background: rgba(255, 255, 255, .13);
    }
  }

  .home-search-input {
    width: 100%;

    ::v-deep .el-input__inner {
      height: 42px;
      padding-left: 40px;
      padding-right: 34px;
      color: rgba(255, 255, 255, .92);
      border: 0;
      border-radius: 21px;
      background: transparent;
      box-shadow: none;
      font-size: 15px;
      font-weight: 700;
      letter-spacing: 0;
      transition: color .18s ease, background .18s ease;

      &::placeholder {
        color: rgba(226, 238, 234, .58);
      }

      &:focus {
        background: transparent;
      }
    }

    ::v-deep .el-input__inner::-webkit-credentials-auto-fill-button {
      display: none !important;
      visibility: hidden;
      pointer-events: none;
    }

    ::v-deep .el-input__prefix,
    ::v-deep .el-input__suffix {
      color: rgba(226, 238, 234, .64);
    }

    ::v-deep .el-input__icon {
      line-height: 42px;
    }
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  &:focus {
    outline: none;
  }
}

@media (max-width: 900px) {
  .navbar {
    .search-panel {
      flex: 1 1 auto;
      width: auto;
    }

    .right-tools {
      gap: 8px;
    }
  }
}

@media (max-width: 640px) {
  .navbar {
    flex-wrap: nowrap;
    gap: 8px;
    height: 56px;
    padding: 8px;
    border-radius: 22px;

    .search-panel {
      flex: 1 1 0;
      height: 40px;
      min-width: 0;
      border-radius: 20px;
    }

    .nav-middle {
      flex: 0 0 auto;
      height: 40px;
    }

    .datetime-pill {
      height: 40px;
      padding: 0 3px 0 0;
      font-size: 11px;
    }

    .datetime-icon {
      display: none;
    }

    .home-search-input {
      ::v-deep .el-input__inner {
        height: 38px;
        border-radius: 19px;
        font-size: 13px;
      }

      ::v-deep .el-input__icon {
        line-height: 38px;
      }
    }
  }
}
</style>
