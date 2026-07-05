import store from '@/store'

const { body } = document
const WIDTH = 768
const PHONE_LANDSCAPE_HEIGHT = 600
const PHONE_LANDSCAPE_MAX_WIDTH = 1000
const TABLET_MIN_EDGE = 700
const TABLET_MAX_EDGE = 1500

export default {
  watch: {
    $route(route) {
      if (this.device === 'mobile' && !this.sidebar.opened) {
        this.$_openSidebarOnDesktop()
      }
    }
  },
  beforeMount() {
    window.addEventListener('resize', this.$_resizeHandler)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.$_resizeHandler)
  },
  mounted() {
    const isMobile = this.$_isMobile()
    if (isMobile) {
      store.dispatch('app/toggleDevice', 'mobile')
      this.$_openSidebarOnDesktop()
    } else {
      store.dispatch('app/toggleDevice', 'desktop')
      this.$_openSidebarOnDesktop()
    }
  },
  methods: {
    // use $_ for mixins properties
    // https://vuejs.org/v2/style-guide/index.html#Private-property-names-essential
    $_isMobile() {
      const rect = body.getBoundingClientRect()
      const isCoarsePointer = window.matchMedia && window.matchMedia('(pointer: coarse)').matches
      const touchPoints = navigator.maxTouchPoints || navigator.msMaxTouchPoints || 0
      const userAgent = navigator.userAgent || ''
      const isiPad = /iPad/.test(userAgent) || (/Macintosh/.test(userAgent) && touchPoints > 1)
      const minEdge = Math.min(rect.width, window.innerHeight)
      const maxEdge = Math.max(rect.width, window.innerHeight)
      const isTabletSize = minEdge >= TABLET_MIN_EDGE && maxEdge <= TABLET_MAX_EDGE
      const isIPadPreviewSize = minEdge >= 700 && minEdge <= 1100 && maxEdge >= 1000 && maxEdge <= TABLET_MAX_EDGE
      const isTablet = isTabletSize && (isCoarsePointer || isiPad || touchPoints > 1 || isIPadPreviewSize)
      if (isTablet) {
        return false
      }
      const isPhoneLandscapeSize = rect.width <= PHONE_LANDSCAPE_MAX_WIDTH && window.innerHeight < PHONE_LANDSCAPE_HEIGHT
      return rect.width < WIDTH || isPhoneLandscapeSize || (isCoarsePointer && window.innerHeight < PHONE_LANDSCAPE_HEIGHT)
    },
    $_resizeHandler() {
      if (!document.hidden) {
        const isMobile = this.$_isMobile()
        store.dispatch('app/toggleDevice', isMobile ? 'mobile' : 'desktop')

        if (isMobile) {
          this.$_openSidebarOnDesktop()
        } else {
          this.$_openSidebarOnDesktop()
        }
      }
    },
    $_openSidebarOnDesktop() {
      if (!store.state.app.sidebar.opened) {
        store.dispatch('app/toggleSideBar')
      }
    },
  }
}
