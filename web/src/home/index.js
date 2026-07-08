/*
 * @author tuituidan
 * @date 2023/9/24
 */
import ResizeMixin from './mixin/ResizeHandler'
import {mapState} from 'vuex'
import variables from '@/assets/styles/variables.scss'

const DESKTOP_DESIGN_WIDTH = 1920;
const DESKTOP_DESIGN_HEIGHT = 1080;
const DESKTOP_PORTRAIT_WIDTH = 1080;
const DESKTOP_PORTRAIT_HEIGHT = 1920;
const TABLET_MIN_EDGE = 700;
const TABLET_MAX_EDGE = 1500;
const TABLET_LANDSCAPE_DESIGN_WIDTH = 1706;
const TABLET_PORTRAIT_MIN_SCALE = 0.72;
const TABLET_PORTRAIT_MAX_SCALE = 0.82;
const TABLET_LANDSCAPE_MIN_SCALE = 0.66;
const TABLET_LANDSCAPE_MAX_SCALE = 0.78;

export default {
  name: 'home-index',
  components: {
    'app-main': () => import('@/home/components/AppMain'),
    'navbar': () => import('@/home/components/Navbar'),
    'sidebar': () => import('@/home/components/Sidebar'),
  },
  mixins: [ResizeMixin],
  data() {
    return {
      desktopScale: 1,
      desktopPortrait: false,
      tabletFill: false,
      desktopDesignWidth: DESKTOP_DESIGN_WIDTH,
      desktopDesignHeight: DESKTOP_DESIGN_HEIGHT,
      todayAccessStats: {
        visitCount: 0,
        loginCount: 0,
      },
      accessStatsTimer: null,
    };
  },
  computed: {
    ...mapState({
      sideTheme: state => state.settings.sideTheme,
      sidebar: state => state.app.sidebar,
      device: state => state.app.device,
      activeCategoryId: state => state.home.activeCategoryId,
    }),
    classObj() {
      return {
        hideSidebar: !this.sidebar.opened,
        openSidebar: this.sidebar.opened,
        withoutAnimation: this.sidebar.withoutAnimation,
        mobile: this.device === 'mobile',
        'is-desktop-portrait': this.desktopPortrait && this.device !== 'mobile',
        'is-tablet-fill': this.tabletFill && this.device !== 'mobile',
        'focus-category-mode': !!this.$store.state.home.activeCategoryId
      }
    },
    variables() {
      return variables;
    },
    desktopScaleStyle() {
      return {
        '--desktop-scale': this.desktopScale,
        '--desktop-design-width': `${this.desktopDesignWidth}px`,
        '--desktop-design-height': `${this.desktopDesignHeight}px`,
      };
    }
  },
  mounted() {
    if (localStorage.username
      && localStorage.password
      && localStorage.autoLogin === 'true'
      && !localStorage.userLogout
      && !this.$store.getters.loginUser.id) {
      this.$store.dispatch('settings/loadVersion');
    }
    this.$store.dispatch('home/loadHomeCards', '');
    this.recordHomeVisit();
    this.scheduleAccessStatsRefresh();
    this.updateDesktopScale();
    window.addEventListener('resize', this.updateDesktopScale);
    if (window.visualViewport) {
      window.visualViewport.addEventListener('resize', this.updateDesktopScale);
    }
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.updateDesktopScale);
    if (window.visualViewport) {
      window.visualViewport.removeEventListener('resize', this.updateDesktopScale);
    }
    if (this.accessStatsTimer) {
      clearTimeout(this.accessStatsTimer);
    }
  },
  methods: {
    recordHomeVisit() {
      this.$http.post('/api/v1/access-statistics/visit').then(res => {
        this.todayAccessStats = res || this.todayAccessStats;
      }).catch(() => {});
    },
    refreshHomeAccessStats() {
      this.$http.get('/api/v1/access-statistics/summary').then(res => {
        this.todayAccessStats = res || this.todayAccessStats;
      }).catch(() => {});
    },
    scheduleAccessStatsRefresh() {
      if (this.accessStatsTimer) {
        clearTimeout(this.accessStatsTimer);
      }
      const now = new Date();
      const next = new Date(now);
      next.setHours(24, 0, 5, 0);
      const delay = Math.max(1000, next.getTime() - now.getTime());
      this.accessStatsTimer = setTimeout(() => {
        this.refreshHomeAccessStats();
        this.scheduleAccessStatsRefresh();
      }, delay);
    },
    updateDesktopScale() {
      const viewport = window.visualViewport;
      const width = Math.round((viewport && viewport.width) || window.innerWidth || document.documentElement.clientWidth || DESKTOP_DESIGN_WIDTH);
      const height = Math.round((viewport && viewport.height) || window.innerHeight || document.documentElement.clientHeight || DESKTOP_DESIGN_HEIGHT);
      this.desktopPortrait = height > width;
      if (this.isTabletFillViewport(width, height)) {
        const tabletScale = this.getTabletFillScale(width, height);
        this.tabletFill = true;
        this.desktopScale = tabletScale;
        this.desktopDesignWidth = Math.round(width / tabletScale);
        this.desktopDesignHeight = Math.round(height / tabletScale);
        return;
      }
      this.tabletFill = false;
      const designWidth = this.desktopPortrait ? DESKTOP_PORTRAIT_WIDTH : DESKTOP_DESIGN_WIDTH;
      const designHeight = this.desktopPortrait ? DESKTOP_PORTRAIT_HEIGHT : DESKTOP_DESIGN_HEIGHT;
      this.desktopDesignWidth = designWidth;
      this.desktopDesignHeight = designHeight;
      const nextScale = Math.min(width / designWidth, height / designHeight);
      this.desktopScale = Number((nextScale || 1).toFixed(5));
    },
    isTabletFillViewport(width, height) {
      const isCoarsePointer = window.matchMedia && window.matchMedia('(pointer: coarse)').matches;
      const touchPoints = navigator.maxTouchPoints || navigator.msMaxTouchPoints || 0;
      const userAgent = navigator.userAgent || '';
      const isiPad = /iPad/.test(userAgent) || (/Macintosh/.test(userAgent) && touchPoints > 1);
      const minEdge = Math.min(width, height);
      const maxEdge = Math.max(width, height);
      const isTabletSize = minEdge >= TABLET_MIN_EDGE && maxEdge <= TABLET_MAX_EDGE;
      const isIPadPreviewSize = minEdge >= 700 && minEdge <= 1100 && maxEdge >= 1000 && maxEdge <= TABLET_MAX_EDGE;
      return isTabletSize && (isCoarsePointer || isiPad || touchPoints > 1 || isIPadPreviewSize);
    },
    getTabletFillScale(width, height) {
      const isPortrait = height > width;
      const baseWidth = isPortrait ? DESKTOP_PORTRAIT_WIDTH : TABLET_LANDSCAPE_DESIGN_WIDTH;
      const minScale = isPortrait ? TABLET_PORTRAIT_MIN_SCALE : TABLET_LANDSCAPE_MIN_SCALE;
      const maxScale = isPortrait ? TABLET_PORTRAIT_MAX_SCALE : TABLET_LANDSCAPE_MAX_SCALE;
      const nextScale = width / baseWidth;
      return Number(Math.min(maxScale, Math.max(minScale, nextScale)).toFixed(5));
    },
    handleClickOutside() {
      this.$store.dispatch('app/closeSideBar', {withoutAnimation: false})
    }
  }
}
