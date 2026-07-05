import http from '@/plugins/http';

const storageSetting = JSON.parse(localStorage.getItem('layout-setting')) || ''
const APP_ICON_PATH = '/app-icon.png'
const state = {
  title: process.env.VUE_APP_TITLE,
  sideTheme: storageSetting.sideTheme || 'theme-dark',
  showSettings: false,
  cutOverSpeed: 10,
  logoPath: APP_ICON_PATH,
  logoToFavicon: true,
  showDoc: false,
  menuDefaultOpen: storageSetting.menuDefaultOpen === 'true',
  menuAccordion: storageSetting.menuAccordion === 'true',
  cardIconShape: storageSetting.cardIconShape || 'circle',
  cardSize: storageSetting.cardSize || 'small',
  version: {},
  cardTypes: [
    {
      id: 'default',
      name: '普通卡片',
    },
    {
      id: 'zip',
      name: '静态网站',
    },
    {
      id: 'dynamic-http',
      name: 'http动态卡片',
    },
    {
      id: 'dynamic-sql',
      name: 'sql动态卡片',
    },
  ],
}
const mutations = {
  CHANGE_SETTING: (state, {key, value}) => {
    if (state.hasOwnProperty(key)) {
      state[key] = value
    }
  },
  CHANGE_VERSION: (state, version) => {
    state.version = version;
  }
}

const actions = {
  // 修改布局设置
  changeSetting({commit}, data) {
    commit('CHANGE_SETTING', data)
  },
  // 加载配置
  loadSettings({commit}) {
    return http.get('/api/v1/setting').then(res => {
      const pick = (camelKey, snakeKey) => res[camelKey] !== undefined ? res[camelKey] : res[snakeKey];
      commit('CHANGE_SETTING', {key: 'title', value: pick('navName', 'nav_name')});
      commit('CHANGE_SETTING', {key: 'cutOverSpeed', value: pick('cutOverSpeed', 'cut_over_speed')});
      commit('CHANGE_SETTING', {key: 'logoPath', value: APP_ICON_PATH});
      commit('CHANGE_SETTING', {key: 'logoToFavicon', value: true});
      commit('CHANGE_SETTING', {key: 'showDoc', value: pick('showDoc', 'show_doc')});
      return res;
    });
  },
  // 加载版本信息
  loadVersion({commit}) {
    return http.get('/api/v1/app/version').then(res => {
      commit('CHANGE_VERSION', res);
      return res;
    });
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
