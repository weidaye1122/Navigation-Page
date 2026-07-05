<template>
  <el-drawer size="360px"
             custom-class="home-settings-drawer"
             :visible="visible"
             :with-header="false"
             :append-to-body="true"
             :show-close="false">
    <div class="drawer-container">
      <div>
        <div class="setting-drawer-content">
          <div class="setting-drawer-title">
            <h3 class="drawer-title">主题风格设置</h3>
          </div>
          <div class="setting-drawer-block-checbox">
            <div class="setting-drawer-block-checbox-item"
                 :class="{'is-active': sideTheme === 'theme-dark'}"
                 @click="handleTheme('theme-dark')">
              <img src="@/assets/images/dark.svg" alt="dark">
              <div v-if="sideTheme === 'theme-dark'" class="setting-drawer-block-checbox-selectIcon"
                   style="display: block;">
                <i aria-label="图标: check" class="anticon anticon-check">
                  <svg viewBox="64 64 896 896" data-icon="check" width="1em" height="1em" :fill="theme"
                       aria-hidden="true" focusable="false" class="">
                    <path
                      d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"/>
                  </svg>
                </i>
              </div>
            </div>
            <div class="setting-drawer-block-checbox-item"
                 :class="{'is-active': sideTheme === 'theme-light'}"
                 @click="handleTheme('theme-light')">
              <img src="@/assets/images/light.svg" alt="light">
              <div v-if="sideTheme === 'theme-light'" class="setting-drawer-block-checbox-selectIcon"
                   style="display: block;">
                <i aria-label="图标: check" class="anticon anticon-check">
                  <svg viewBox="64 64 896 896" data-icon="check" width="1em" height="1em" :fill="theme"
                       aria-hidden="true" focusable="false" class="">
                    <path
                      d="M912 190h-69.9c-9.8 0-19.1 4.5-25.1 12.2L404.7 724.5 207 474a32 32 0 0 0-25.1-12.2H112c-6.7 0-10.4 7.7-6.3 12.9l273.9 347c12.8 16.2 37.4 16.2 50.3 0l488.4-618.9c4.1-5.1.4-12.8-6.3-12.8z"/>
                  </svg>
                </i>
              </div>
            </div>
          </div>
        </div>
        <el-divider/>
        <h3 class="drawer-title">系统布局配置</h3>

        <div class="drawer-item">
          <span>菜单设置</span>
          <el-checkbox-group v-model="menuSetting">
            <el-checkbox label="open">默认展开</el-checkbox>
            <el-checkbox label="accordion">手风琴</el-checkbox>
          </el-checkbox-group>
        </div>
        <div class="drawer-item">
          <span>卡片图标</span>
          <el-radio-group v-model="cardIconShape">
            <el-radio label="circle">圆形</el-radio>
            <el-radio label="square">方形</el-radio>
          </el-radio-group>
        </div>
        <div class="drawer-item">
          <span>卡片大小</span>
          <el-radio-group v-model="cardSize">
            <el-radio label="small">较小</el-radio>
            <el-radio label="large">较大</el-radio>
          </el-radio-group>
        </div>
        <div class="drawer-actions">
          <el-button size="small" type="primary" plain icon="el-icon-document-add" @click="saveSetting">保存配置</el-button>
          <el-button size="small" plain icon="el-icon-refresh" @click="resetSetting">重置配置</el-button>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script>

export default {
  data() {
    return {
      theme: '#409EFF',
      sideTheme: this.$store.state.settings.sideTheme
    };
  },
  computed: {
    visible: {
      get() {
        return this.$store.state.settings.showSettings
      }
    },
    menuSetting: {
      get() {
        const val = [];
        if (this.$store.state.settings.menuDefaultOpen) {
          val.push('open');
        }
        if (this.$store.state.settings.menuAccordion) {
          val.push('accordion');
        }
        return val;
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'menuDefaultOpen',
          value: val.includes('open'),
        });
        this.$store.dispatch('settings/changeSetting', {
          key: 'menuAccordion',
          value: val.includes('accordion'),
        });
      },
    },
    cardIconShape: {
      get() {
        return this.$store.state.settings.cardIconShape
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'cardIconShape',
          value: val,
        });
      },
    },
    cardSize: {
      get() {
        return this.$store.state.settings.cardSize
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'cardSize',
          value: val,
        });
      },
    },
  },
  methods: {
    handleTheme(val) {
      this.$store.dispatch('settings/changeSetting', {
        key: 'sideTheme',
        value: val
      })
      this.sideTheme = val;
    },
    saveSetting() {
      this.$modal.loading("正在保存到本地，请稍候...");
      this.$cache.local.set(
        "layout-setting",
        `{
            "sideTheme":"${this.sideTheme}",
            "menuDefaultOpen":"${this.$store.state.settings.menuDefaultOpen}",
            "menuAccordion":"${this.$store.state.settings.menuAccordion}",
            "cardIconShape":"${this.$store.state.settings.cardIconShape}",
            "cardSize":"${this.$store.state.settings.cardSize}"
          }`
      );
      setTimeout(() => this.$modal.closeLoading(), 1000)
    },
    resetSetting() {
      this.$modal.loading("正在清除设置缓存并刷新，请稍候...");
      this.$cache.local.remove("layout-setting")
      setTimeout("window.location.reload()", 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.setting-drawer-content {
  .setting-drawer-title {
    margin-bottom: 14px;
    color: rgba(255, 255, 255, .94);
    font-size: 18px;
    line-height: 1.2;
    font-weight: 900;
    text-shadow: 0 1px 14px rgba(0, 0, 0, .26);
  }

  .setting-drawer-block-checbox {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    gap: 16px;
    margin-top: 14px;
    margin-bottom: 22px;

    .setting-drawer-block-checbox-item {
      position: relative;
      width: 72px;
      height: 72px;
      padding: 9px;
      border: 1px solid rgba(255, 255, 255, .24);
      border-radius: 20px;
      cursor: pointer;
      overflow: hidden;
      background:
        linear-gradient(145deg, rgba(255, 255, 255, .18), rgba(255, 255, 255, .06)),
        rgba(255, 255, 255, .08);
      box-shadow:
        0 16px 34px rgba(0, 0, 0, .22),
        inset 0 1px 0 rgba(255, 255, 255, .24);
      transition: border-color .18s ease, box-shadow .18s ease, transform .18s ease, background .18s ease;

      &:hover,
      &.is-active {
        transform: translateY(-2px);
        border-color: rgba(255, 255, 255, .64);
        background:
          linear-gradient(145deg, rgba(255, 255, 255, .28), rgba(255, 255, 255, .1)),
          rgba(255, 255, 255, .12);
        box-shadow:
          0 0 0 4px rgba(255, 255, 255, .08),
          0 22px 46px rgba(0, 0, 0, .28),
          inset 0 1px 0 rgba(255, 255, 255, .42);
      }

      img {
        width: 54px;
        height: 54px;
        border-radius: 14px;
        box-shadow: 0 10px 24px rgba(0, 0, 0, .18);
      }

      .setting-drawer-block-checbox-selectIcon {
        position: absolute;
        right: 9px;
        bottom: 9px;
        display: flex !important;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        padding: 0;
        color: #ffffff;
        border: 1px solid rgba(255, 255, 255, .6);
        border-radius: 50%;
        background: rgba(237, 112, 64, .86);
        font-weight: 700;
        font-size: 13px;
        box-shadow:
          0 10px 22px rgba(237, 112, 64, .24),
          inset 0 1px 0 rgba(255, 255, 255, .44);
      }
    }
  }
}

.drawer-container {
  min-height: 100%;
  padding: 34px 28px;
  font-size: 16px;
  line-height: 1.5;
  word-wrap: break-word;
  color: rgba(255, 255, 255, .88);
  background: transparent;

  .drawer-title {
    margin: 0 0 16px;
    color: rgba(255, 255, 255, .94);
    font-size: 20px;
    line-height: 1.2;
    font-weight: 900;
    text-shadow: 0 1px 14px rgba(0, 0, 0, .26);
  }

  .drawer-item {
    min-height: 62px;
    color: rgba(236, 244, 241, .78);
    font-size: 17px;
    padding: 16px 0;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 14px;
    border-bottom: 1px solid rgba(255, 255, 255, .14);

    > span {
      flex: 0 0 auto;
      font-weight: 800;
      color: rgba(255, 255, 255, .82);
    }
  }

  .drawer-actions {
    display: flex;
    gap: 12px;
    margin-top: 18px;
  }

  .drawer-actions .el-button {
    flex: 1;
  }
}
</style>

<style lang="scss">
.home-settings-drawer.el-drawer {
  overflow: hidden;
  border-left: 1px solid rgba(255, 255, 255, .38);
  border-radius: 28px 0 0 28px;
  background:
    linear-gradient(152deg, rgba(255, 255, 255, .2), rgba(255, 255, 255, .055) 44%, rgba(255, 255, 255, .1)),
    rgba(8, 15, 15, .58);
  box-shadow:
    -30px 0 78px rgba(0, 0, 0, .34),
    inset 1px 0 0 rgba(255, 255, 255, .28),
    inset 0 1px 0 rgba(255, 255, 255, .34);
  -webkit-backdrop-filter: blur(46px) saturate(1.72) contrast(1.08);
  backdrop-filter: blur(46px) saturate(1.72) contrast(1.08);
}

.home-settings-drawer.el-drawer::before {
  content: "";
  position: absolute;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at 16% 8%, rgba(255, 255, 255, .24), transparent 32%),
    radial-gradient(circle at 84% 72%, rgba(237, 112, 64, .16), transparent 36%),
    linear-gradient(120deg, rgba(255, 255, 255, .12), transparent 42%);
}

.home-settings-drawer .el-drawer__body {
  position: relative;
  z-index: 1;
  overflow: auto;
  background: transparent;
}

.home-settings-drawer .el-divider {
  margin: 28px 0;
  background-color: rgba(255, 255, 255, .16);
}

.home-settings-drawer .el-checkbox,
.home-settings-drawer .el-radio {
  margin-right: 0;
  color: rgba(236, 244, 241, .78);
  font-size: 16px;
  font-weight: 800;
}

.home-settings-drawer .el-checkbox + .el-checkbox,
.home-settings-drawer .el-radio + .el-radio {
  margin-left: 18px;
}

.home-settings-drawer .el-checkbox__label,
.home-settings-drawer .el-radio__label {
  color: rgba(236, 244, 241, .8);
  font-size: 16px;
  font-weight: 800;
}

.home-settings-drawer .el-checkbox__inner,
.home-settings-drawer .el-radio__inner {
  width: 18px;
  height: 18px;
  border-color: rgba(255, 255, 255, .36);
  background:
    linear-gradient(145deg, rgba(255, 255, 255, .16), rgba(255, 255, 255, .05)),
    rgba(255, 255, 255, .08);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, .2);
}

.home-settings-drawer .el-checkbox__input.is-checked .el-checkbox__inner,
.home-settings-drawer .el-radio__input.is-checked .el-radio__inner {
  border-color: rgba(255, 255, 255, .7);
  background: rgba(237, 112, 64, .88);
}

.home-settings-drawer .el-checkbox__input.is-checked + .el-checkbox__label,
.home-settings-drawer .el-radio__input.is-checked + .el-radio__label {
  color: #ffffff;
  text-shadow: 0 0 14px rgba(255, 255, 255, .18);
}

.home-settings-drawer .el-button {
  height: 42px;
  padding: 0 12px;
  border: 1px solid rgba(255, 255, 255, .24);
  border-radius: 14px;
  color: rgba(255, 255, 255, .86);
  background:
    linear-gradient(145deg, rgba(255, 255, 255, .16), rgba(255, 255, 255, .05)),
    rgba(255, 255, 255, .08);
  box-shadow:
    0 14px 28px rgba(0, 0, 0, .18),
    inset 0 1px 0 rgba(255, 255, 255, .22);
  font-size: 15px;
  font-weight: 800;
  transition: transform .18s ease, box-shadow .18s ease, border-color .18s ease, background .18s ease;
}

.home-settings-drawer .el-button:hover,
.home-settings-drawer .el-button:focus {
  transform: translateY(-2px);
  border-color: rgba(255, 255, 255, .58);
  color: #ffffff;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, .26), rgba(255, 255, 255, .09)),
    rgba(255, 255, 255, .12);
  box-shadow:
    0 0 0 4px rgba(255, 255, 255, .08),
    0 18px 38px rgba(0, 0, 0, .24),
    inset 0 1px 0 rgba(255, 255, 255, .34);
}

.home-settings-drawer .el-button--primary.is-plain {
  border-color: rgba(255, 255, 255, .42);
  color: #ffffff;
  background:
    linear-gradient(135deg, rgba(255, 255, 255, .34), rgba(255, 255, 255, .12) 42%, rgba(237, 112, 64, .38)),
    rgba(237, 112, 64, .58);
  box-shadow:
    0 18px 38px rgba(237, 112, 64, .22),
    inset 0 1px 0 rgba(255, 255, 255, .48);
}
</style>
