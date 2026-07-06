<template>
  <div>
    <template v-if="hasOneShowingChild(item.children,item) && (!onlyOneChild.children||onlyOneChild.noShowingChildren)">
      <el-menu-item @click="menuItemClickHandler(item.id)"
                    :index="item.id"
                    :data-menu-id="item.id"
                    :class="{'submenu-title-noDropdown':!isNest, 'is-home-active': item.id === activeCategoryId}">
        <item :icon="onlyOneChild.icon|| item.icon" :title="onlyOneChild.name"/>
      </el-menu-item>
    </template>

    <el-submenu v-else ref="subMenu" :index="item.id" popper-append-to-body>
      <template slot="title">
        <item :icon="item.icon" :title="item.name"/>
      </template>
      <sidebar-item
        v-for="child in item.children"
        :key="child.id"
        :is-nest="true"
        :item="child"
        class="nest-menu"
      />
    </el-submenu>
  </div>
</template>

<script>
import Item from './Item'
import FixiOSBug from './FixiOSBug'

export default {
  name: 'SidebarItem',
  components: {Item},
  mixins: [FixiOSBug],
  props: {
    // route object
    item: {
      type: Object,
      required: true
    },
    isNest: {
      type: Boolean,
      default: false
    },
    basePath: {
      type: String,
      default: ''
    }
  },
  data() {
    this.onlyOneChild = null
    return {}
  },
  computed: {
    activeCategoryId() {
      return this.$store.state.home.activeCategoryId;
    },
  },
  methods: {
    menuItemClickHandler(id) {
      this.cancelHomeScrollAnimation();
      this.$store.commit('home/SET_ACTIVE_CATEGORY', id);
      this.$nextTick(() => {
        const scrollToTitle = () => {
          if (this.$store.state.home.activeCategoryId !== id) {
            return;
          }
          const anchor = document.getElementById(id);
          const category = anchor && anchor.closest('.category-main');
          const title = category && category.querySelector('.category-title');
          const titleText = category && category.querySelector('.category-title-text');
          const scroller = document.querySelector('.app-main');
          const trigger = Array.from(document.querySelectorAll('.sidebar-container .el-menu-item'))
            .find(item => item.getAttribute('data-menu-id') === id);
          const triggerText = trigger && trigger.querySelector('.sidebar-menu-title');

          if (!title || !scroller || !trigger) {
            return;
          }

          const rightTarget = titleText || title;
          const leftTarget = triggerText || trigger;
          const nextScrollTop = this.getAlignedHomeScrollTop(scroller, rightTarget, leftTarget);

          this.animateHomeScroll(scroller, nextScrollTop, id);
        };

        requestAnimationFrame(() => requestAnimationFrame(scrollToTitle));
      });
    },
    cancelHomeScrollAnimation() {
      if (window.__teamNavHomeScrollFrame) {
        cancelAnimationFrame(window.__teamNavHomeScrollFrame);
      }
      window.__teamNavHomeScrollFrame = null;
      window.__teamNavHomeScrollToken = (window.__teamNavHomeScrollToken || 0) + 1;
      return window.__teamNavHomeScrollToken;
    },
    getHomeScrollScale(scroller) {
      const rect = scroller && scroller.getBoundingClientRect();
      if (!rect || !rect.height || !scroller.offsetHeight) {
        return 1;
      }
      const scale = rect.height / scroller.offsetHeight;
      return scale > 0 ? scale : 1;
    },
    getAlignedHomeScrollTop(scroller, rightTarget, leftTarget) {
      const scrollScale = this.getHomeScrollScale(scroller);
      const maxScrollTop = scroller.scrollHeight - scroller.clientHeight;
      const nextScrollTop = scroller.scrollTop + (rightTarget.getBoundingClientRect().top - leftTarget.getBoundingClientRect().top) / scrollScale;
      return Math.max(0, Math.min(maxScrollTop, nextScrollTop));
    },
    settleHomeScroll(scroller, id) {
      requestAnimationFrame(() => {
        if (this.$store.state.home.activeCategoryId !== id) {
          return;
        }
        const anchor = document.getElementById(id);
        const category = anchor && anchor.closest('.category-main');
        const title = category && category.querySelector('.category-title');
        const titleText = category && category.querySelector('.category-title-text');
        const trigger = Array.from(document.querySelectorAll('.sidebar-container .el-menu-item'))
          .find(item => item.getAttribute('data-menu-id') === id);
        const triggerText = trigger && trigger.querySelector('.sidebar-menu-title');
        if (!title || !scroller || !trigger) {
          return;
        }
        scroller.scrollTop = this.getAlignedHomeScrollTop(scroller, titleText || title, triggerText || trigger);
      });
    },
    animateHomeScroll(scroller, targetTop, id) {
      const token = this.cancelHomeScrollAnimation();
      const startTop = scroller.scrollTop;
      const distance = targetTop - startTop;
      const duration = Math.min(460, Math.max(180, Math.abs(distance) * 0.28));
      const startTime = window.performance.now();
      const easeOut = progress => 1 - Math.pow(1 - progress, 3);
      const step = now => {
        if (this.$store.state.home.activeCategoryId !== id || window.__teamNavHomeScrollToken !== token) {
          return;
        }
        const progress = Math.min(1, (now - startTime) / duration);
        scroller.scrollTop = startTop + distance * easeOut(progress);
        if (progress < 1) {
          window.__teamNavHomeScrollFrame = requestAnimationFrame(step);
          return;
        }
        window.__teamNavHomeScrollFrame = null;
        scroller.scrollTop = targetTop;
        this.settleHomeScroll(scroller, id);
      };
      window.__teamNavHomeScrollFrame = requestAnimationFrame(step);
    },
    hasOneShowingChild(children = [], parent) {
      if (!children) {
        children = [];
      }
      const showingChildren = children.filter(item => {
        if (item.hidden) {
          return false
        } else {
          // Temp set(will be used if only has one showing child)
          this.onlyOneChild = item
          return true
        }
      })

      // When there is only one child router, the child router is displayed by default
      if (showingChildren.length === 1) {
        return true
      }

      // Show parent if there are no child router to display
      if (showingChildren.length === 0) {
        this.onlyOneChild = {...parent, path: '', noShowingChildren: true}
        return true
      }

      return false
    },
  }
}
</script>
