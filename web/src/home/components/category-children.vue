<template>
  <div class="category-children" :class="{'large-layout-container': cardSize==='large'}">
    <div class="card-sm card-md card-xl card-xxl" :class="{'is-starred': card.star}" v-for="card in datas" :key="card.id">
      <el-card class="nav-card"
               :class="{'is-link-card': card.url}"
               shadow="hover"
               @click.native="cardClickHandler(card)">
        <span v-if="card.star" class="nav-card-star"><i class="el-icon-star-on"></i></span>
        <div class="nav-card-inner">
          <ivu-avatar v-if="card.icon.src"
                      :src="card.icon.src"
                      :shape="cardIconShape"
                      class="nav-card-icon"></ivu-avatar>
          <ivu-avatar v-else
                      class="nav-card-icon"
                      :shape="cardIconShape"
                      :style="{background: card.icon.color}">{{ card.icon.text }}
          </ivu-avatar>
          <div class="nav-card-content">
            <div v-text="card.title" class="card-title text-ellipsis"></div>
            <div v-if="card.type.startsWith('dynamic') && !card.content" class="card-content text-ellipsis">
              <i class="el-icon-loading"></i>正在加载...
            </div>
            <div v-else v-text="card.content" class="card-content text-ellipsis"></div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import {mapGetters} from "vuex";

export default {
  name: "category-children",
  props: {
    datas: {
      type: Array
    }
  },
  computed: {
    ...mapGetters([
      'cardIconShape',
      'cardSize',
    ]),
  },
  methods: {
    cardClickHandler(card) {
      if (card.url) {
        // 页面中引入外网的链接资源，会产生一个新的http请求。为了安全（URL里可能包含用户信息），
        // 浏览器一般都会给这写请求头加上表示来源的referrer 字段，让目标站判断来源地址
        window.open(card.url,'_blank','noopener=yes,noreferrer=yes');
      }
    },
    showQrcodeHandler(url) {
      return `${process.env.VUE_APP_BASE_API}/api/v1/qrcode?url=${encodeURIComponent(url)}`;
    },
    downloadAttachment(id) {
      window.open(`${process.env.VUE_APP_BASE_API}/api/v1/attachment/${id}/actions/download`);
    },
    starHandler(card) {
      this.$store.dispatch('home/setStarCard', card).then(star => {
        card.star = star;
        this.$modal.msgSuccess(star ? '已添加到个人常用' : '已移出个人常用');
      });
    },
  }
}
</script>


<style lang="scss" scoped>
.category-children {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(238px, 1fr));
  gap: 12px;
  padding: 8px 0 6px;

  .nav-card {
    position: relative;
    height: 68px;
    overflow: hidden;
    border: 1px solid var(--home-glass-border-strong);
    border-radius: 22px;
    background:
      linear-gradient(145deg, rgba(255, 255, 255, .2), rgba(255, 255, 255, .04) 45%, rgba(255, 255, 255, .08)),
      var(--home-glass-bg);
    box-shadow:
      0 26px 66px rgba(0, 0, 0, .3),
      inset 0 1px 0 rgba(255, 255, 255, .38),
      inset 0 -1px 0 rgba(255, 255, 255, .08);
    -webkit-backdrop-filter: blur(44px) saturate(1.72) contrast(1.08);
    backdrop-filter: blur(44px) saturate(1.72) contrast(1.08);
    cursor: default;
    transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease, background-color .18s ease;

    ::v-deep .el-card__body {
      height: 100%;
      padding: 8px 12px;
    }

    &::before {
      content: "";
      position: absolute;
      inset: -1px;
      pointer-events: none;
      border-radius: inherit;
      background:
        linear-gradient(104deg, rgba(255, 255, 255, .62), rgba(255, 255, 255, .18) 34%, rgba(255, 255, 255, .36) 64%, rgba(255, 255, 255, .1)),
        radial-gradient(circle at 18% 50%, rgba(237, 112, 64, .5), transparent 42%),
        radial-gradient(circle at 84% 22%, rgba(142, 204, 226, .48), transparent 38%);
      opacity: 0;
      transition: opacity .18s ease;
    }

    &::after {
      content: "";
      position: absolute;
      inset: 0;
      pointer-events: none;
      background:
        linear-gradient(112deg, rgba(255, 255, 255, .24), transparent 38%),
        radial-gradient(circle at 82% 8%, rgba(255, 255, 255, .2), transparent 34%);
      opacity: .76;
      transition: opacity .18s ease;
    }

    &.is-link-card {
      cursor: pointer;
    }

    .nav-card-inner {
      position: relative;
      z-index: 1;
      display: flex;
      align-items: center;
      height: 100%;
      min-width: 0;
    }

    .nav-card-icon {
      vertical-align: middle;
      flex: 0 0 auto;
      width: 36px !important;
      height: 36px !important;
      line-height: 36px !important;
      border: 1px solid rgba(255, 255, 255, .36);
      box-shadow:
        0 14px 28px rgba(0, 0, 0, .24),
        inset 0 1px 0 rgba(255, 255, 255, .22);
      font-weight: 800;
      transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease;
    }

    .nav-card-content {
      min-width: 0;
      width: calc(100% - 54px);
      margin-left: 9px
    }

    .card-title {
      color: rgba(255, 255, 255, .95);
      font-size: 17px;
      line-height: 1.15;
      font-weight: 800;
    }

    .card-content {
      font-size: 12px;
      color: rgba(226, 255, 244, .72);
      display: block;
      margin-top: 3px;
      font-weight: 600;
    }

    .text-ellipsis {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

  }

  .nav-card:hover {
    transform: translateY(-4px) scale(1.012);
    box-shadow:
      0 0 0 2px rgba(255, 255, 255, .34),
      0 0 0 5px rgba(255, 255, 255, .1),
      0 0 42px rgba(255, 255, 255, .28),
      0 20px 52px rgba(237, 112, 64, .28),
      0 38px 92px rgba(0, 0, 0, .42),
      inset 0 1px 0 rgba(255, 255, 255, .72),
      inset 0 0 0 1px rgba(255, 255, 255, .32),
      inset 0 -1px 0 rgba(255, 255, 255, .24);
    border-color: rgba(255, 255, 255, .94);
    background:
      linear-gradient(145deg, rgba(255, 255, 255, .48), rgba(255, 255, 255, .15) 45%, rgba(255, 255, 255, .24)),
      var(--home-glass-bg-strong);

    &::before {
      opacity: .9;
    }

    &::after {
      opacity: 1;
      background:
        linear-gradient(112deg, rgba(255, 255, 255, .42), transparent 42%),
        linear-gradient(270deg, rgba(255, 255, 255, .22), transparent 34%),
        radial-gradient(circle at 82% 8%, rgba(255, 255, 255, .32), transparent 34%);
    }

    .nav-card-icon {
      transform: scale(1.08);
      border-color: rgba(255, 255, 255, .72);
      box-shadow:
        0 0 0 3px rgba(255, 255, 255, .12),
        0 14px 30px rgba(0, 0, 0, .28),
        inset 0 1px 0 rgba(255, 255, 255, .38);
    }

    .card-title {
      text-shadow: 0 0 16px rgba(255, 255, 255, .28);
    }
  }
}

.large-layout-container {
  grid-template-columns: repeat(auto-fill, minmax(238px, 1fr));

  .nav-card {
    height: 78px;
  }

  .card-title {
    font-size: 18px !important;
  }
  .card-content {
    font-size: 13px !important;
  }
  .nav-card-icon{
    width: 40px !important;
    height: 40px !important;
    line-height: 40px !important;
  }
}

.card-sm {
  min-width: 0;
}

@media (min-width: 768px) {
  .card-md {
    min-width: 0;
  }
  .large-layout-container .card-md {
    min-width: 0;
  }
}


@media (min-width: 1200px) {
  .card-xl {
    min-width: 0;
  }
  .large-layout-container .card-xl {
    min-width: 0;
  }
}

@media (min-width: 1680px) {
  .card-xxl {
    min-width: 0;
  }
  .large-layout-container .card-xxl {
    min-width: 0;
  }
}

.nav-card-star {
  position: absolute;
  top: 12px;
  right: 14px;
  z-index: 1;
  color: #d9852f;
  font-size: 14px;
  filter: drop-shadow(0 4px 8px rgba(217, 133, 47, .28));
}

@media (max-width: 620px) {
  .category-children {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .category-children .nav-card {
    height: 68px;

    ::v-deep .el-card__body {
      padding: 8px 12px;
    }
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

.my-popover {
  background-color: rgba(31, 42, 39, .94);
  color: white;
  max-width: 500px;
  border: 1px solid rgba(255, 255, 255, .08);
  border-radius: 8px;
  box-shadow: 0 18px 44px rgba(24, 33, 30, .28);

  .popper__arrow {
    border-bottom-color: rgba(31, 42, 39, .94) !important;
  }

  .popper__arrow::after {
    border-bottom-color: rgba(31, 42, 39, .94) !important;
  }

  .card-star {
    position: absolute;
    right: 5px;
    top: 5px;
    padding: 3px;
    cursor: pointer;
  }

  .card-star.el-icon-star-on {
    color: #f0be5d;
    font-weight: bolder;
  }
}
</style>
