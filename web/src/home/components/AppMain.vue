<template>
  <section class="app-main" @wheel="wheelHandler">
    <div :key="homeRenderKey"
         class="home-shell"
         :class="{
           'is-focus-mode': activeCategoryId,
           'is-empty': homeCards.length<=0,
           'is-repainting-home': repaintingHome
         }">
      <div class="category-main"
           :class="getCategoryStateClass(category)"
           v-for="(category, index) in homeCards"
           :key="category.id">
        <div class="anchor-point" :class="{'anchor-point-first':index===0}" :id="category.id"></div>
        <div class="category-title">
          <span class="category-title-marker"></span>
          <span class="category-title-text" v-text="getCategoryTitle(category)"></span>
          <small v-text="getCardCount(category) + ' 项'"></small>
        </div>
        <el-tabs v-if="Array.isArray(category.children)&&category.children.length>1" :value="category.children[0].id">
          <el-tab-pane lazy v-for="child in category.children" :key="child.id" :label="child.name" :name="child.id">
            <category-children :datas="child.cards"></category-children>
          </el-tab-pane>
        </el-tabs>
        <category-children v-else-if="Array.isArray(category.children)&&category.children.length===1"
                           :datas="category.children[0].cards"></category-children>
        <category-children v-else :datas="category.cards"></category-children>
      </div>
      <el-empty v-if="homeCards.length<=0" class="empty-state" description="没有任何数据"></el-empty>
    </div>
    <el-backtop v-if="!activeCategoryId" target=".app-main"></el-backtop>
  </section>
</template>

<script>

import {mapGetters} from "vuex";

export default {
  name: 'AppMain',
  components: {
    'category-children': () => import('@/home/components/category-children.vue'),
  },
  data() {
    return {
      homeRenderKey: 0,
      repaintingHome: false,
    };
  },
  computed: {
    ...mapGetters([
      'homeCards',
    ]),
    activeCategoryId() {
      return this.$store.state.home.activeCategoryId;
    },
  },
  watch: {
    activeCategoryId(nextId, prevId) {
      if (!nextId && prevId) {
        this.restoreHomeMode();
      }
    },
  },
  methods: {
    getCategoryStateClass(category) {
      if (!this.activeCategoryId) {
        return '';
      }
      return category.id === this.activeCategoryId ? 'is-category-focused' : 'is-category-muted';
    },
    getCategoryTitle(category) {
      if (Array.isArray(category.children) && category.children.length === 1) {
        return `${category.name} / ${category.children[0].name}`;
      }
      return category.name;
    },
    getCards(category) {
      if (Array.isArray(category.children) && category.children.length > 0) {
        return category.children.reduce((cards, child) => {
          if (Array.isArray(child.cards)) {
            cards.push(...child.cards);
          }
          return cards;
        }, []);
      }
      return Array.isArray(category.cards) ? category.cards : [];
    },
    getCardCount(category) {
      return this.getCards(category).length;
    },
    wheelHandler(event) {
      if (this.activeCategoryId) {
        event.preventDefault();
      }
    },
    restoreHomeMode() {
      this.cancelHomeScrollAnimation();
      this.homeRenderKey += 1;
      this.repaintingHome = true;
      this.$nextTick(() => {
        this.$el.scrollTop = 0;
        this.$el.getBoundingClientRect();
        requestAnimationFrame(() => {
          this.$el.scrollTop = 0;
          requestAnimationFrame(() => {
            this.repaintingHome = false;
            this.$el.scrollTop = 0;
          });
        });
      });
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
.app-main {
  height: calc(100vh - 88px);
  width: 100%;
  position: relative;
  overflow: auto;
  background: transparent;
}

.home-shell {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 1900px;
  margin: 0;
  padding: 18px 34px 48px 48px;
}

.home-shell.is-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  max-width: none;
  padding: 0 48px 0 48px;
}

.home-shell.is-focus-mode {
  padding-top: 122px;
  padding-bottom: 960px;
}

.home-shell.is-repainting-home {
  .category-main {
    opacity: 1 !important;
    filter: none !important;
    transform: translateZ(0);
    transition: none !important;
  }

  ::v-deep .nav-card,
  ::v-deep .nav-card-inner {
    transform: translateZ(0);
    transition: none !important;
  }
}

.category-main {
  margin-top: 26px;
  transform-origin: center top;
  transition: opacity .18s ease;

  &:first-child {
    margin-top: 6px;
  }

  &.is-category-focused {
    opacity: 1;
    filter: none;
    transform: none;
  }

  &.is-category-muted {
    opacity: .24;
    pointer-events: none;
  }

  &.is-category-muted:hover {
    opacity: .24;
  }

  .anchor-point {
    position: relative;
    top: -14px;
  }
  .anchor-point-first{
    top: -22px;
  }

  .el-tabs {
    ::v-deep .el-tabs__header {
      margin: 12px 0 0;

      .el-tabs__nav-wrap::after {
        display: none;
      }

      .el-tabs__active-bar {
        display: none;
      }

      .el-tabs__nav {
        padding: 4px;
        background:
          linear-gradient(140deg, rgba(255, 255, 255, .16), rgba(255, 255, 255, .04)),
          rgba(13, 20, 20, .3);
        border: 1px solid rgba(255, 255, 255, .28);
        border-radius: 20px;
        height: 40px;
        box-shadow: inset 0 1px 0 rgba(255, 255, 255, .22), 0 12px 28px rgba(0, 0, 0, .14);
        -webkit-backdrop-filter: blur(24px) saturate(1.4);
        backdrop-filter: blur(24px) saturate(1.4);

        .el-tabs__item {
          border-radius: 16px;
          text-align: center;
          padding: 0 16px;
          height: 30px;
          line-height: 30px;
          color: rgba(255, 255, 255, .66);
          font-weight: 600;
        }

        .el-tabs__item.is-active {
          background-color: rgba(255, 255, 255, .2);
          color: #ffffff;
        }
      }
    }
  }

  .category-title {
    display: flex;
    align-items: center;
    gap: 10px;
    color: rgba(255, 255, 255, .92);
    font-size: 22px;
    font-weight: 800;

    .category-title-marker {
      width: 8px;
      height: 30px;
      border-radius: 8px;
      background: rgba(237, 112, 64, .9);
      box-shadow: 0 10px 26px rgba(237, 112, 64, .28);
    }

    .category-title-text {
      line-height: 1;
    }

    small {
      color: rgba(255, 255, 255, .68);
      font-size: 12px;
      font-weight: 700;
      background: rgba(18, 25, 24, .24);
      border: 1px solid rgba(255, 255, 255, .24);
      border-radius: 14px;
      padding: 5px 10px;
      -webkit-backdrop-filter: blur(16px) saturate(1.45);
      backdrop-filter: blur(16px) saturate(1.45);
    }
  }
}

.empty-state {
  min-height: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  transform: translateY(-16px);

  ::v-deep .el-empty__image {
    width: 190px;
    opacity: .96;
    filter:
      drop-shadow(0 24px 46px rgba(0, 0, 0, .24))
      drop-shadow(0 0 22px rgba(255, 255, 255, .18));
  }

  ::v-deep .el-empty__description {
    margin-top: 20px;
  }

  ::v-deep .el-empty__description p {
    color: rgba(255, 255, 255, .82);
    font-size: 18px;
    line-height: 1.2;
    font-weight: 800;
    letter-spacing: 0;
    text-shadow:
      0 2px 12px rgba(0, 0, 0, .34),
      0 0 18px rgba(255, 255, 255, .16);
  }
}

.blink-box {
  animation: glow 800ms ease-out infinite alternate;
}

@keyframes glow {
  0% {
    border-color: red;
    box-shadow: 0 0 5px rgba(255, 0, 0, .2), inset 0 0 5px rgba(255, 0, 0, .1), 0 0 0 red;
  }
  100% {
    border-color: red;
    box-shadow: 0 0 20px rgba(255, 0, 0, .6), inset 0 0 10px rgba(255, 0, 0, .4), 0 0 0 red;
  }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
//.el-popup-parent--hidden {
//  .fixed-header {
//    padding-right: 6px;
//  }
//}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: transparent;
}

::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, .22);
  border-radius: 6px;
}

@media (max-width: 900px) {
  .home-shell {
    padding: 18px 14px 32px;
  }
}

@media (max-width: 520px) {
  .category-main .category-title {
    font-size: 16px;
  }
}
</style>
