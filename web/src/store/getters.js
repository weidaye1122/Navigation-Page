const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  loginUser: state => state.user.userInfo,
  cutOverSpeed: state => state.settings.cutOverSpeed,
  showDoc: state => state.settings.showDoc,
  version: state => state.settings.version,
  homeMenus: state => state.home.menus,
  homeCards: state => state.home.datas,
  homeCardsLoading: state => state.home.loadingHomeCards,
  homeCardsLoaded: state => state.home.homeCardsLoaded,
  homeCardsLoadError: state => state.home.homeCardsLoadError,
  cardIconShape: state => state.settings.cardIconShape,
  cardSize: state => state.settings.cardSize,
  cardTypes: state => state.settings.cardTypes,
  applyCount: state => state.user.applyCount,
}
export default getters
