import http from '@/plugins/http';
import {buildStarCard, saveStarCard, loadCardDynamicContent} from "@/utils";

let homeCardsRequestSerial = 0;
const HOME_CARDS_RETRY_DELAYS = [800, 1600];

const state = {
  menus: [],
  datas: [],
  openIds: [],
  activeCategoryId: '',
  searchKeywords: '',
  loadingHomeCards: true,
  homeCardsLoaded: false,
  homeCardsLoadError: false,
}
const mutations = {
  CHANGE_DATA: (state, res) => {
    const menus = Array.isArray(res.menus) ? res.menus : [];
    state.menus = menus;
    state.datas = Array.isArray(res.datas) ? res.datas : [];
    state.openIds = menus.filter(it => Array.isArray(it.children) && it.children.length > 0).map(it => it.id);
    state.homeCardsLoaded = true;
  },
  SET_ACTIVE_CATEGORY: (state, id) => {
    state.activeCategoryId = id || '';
  },
  SET_SEARCH_KEYWORDS: (state, keywords) => {
    state.searchKeywords = keywords || '';
  },
  SET_HOME_CARDS_LOADING: (state, loading) => {
    state.loadingHomeCards = loading;
  },
  SET_HOME_CARDS_LOAD_ERROR: (state, hasError) => {
    state.homeCardsLoadError = hasError;
    state.homeCardsLoaded = state.homeCardsLoaded || hasError;
  },
  LOAD_STAR_CARD: state => {
    const starCategory = buildStarCard(state.datas);
    if (state.datas.find(item => item.id === '1')) {
      state.menus.splice(0, 1);
      state.datas.splice(0, 1);
    }
    if (starCategory) {
      state.menus.splice(0, 0, starCategory);
      state.datas.splice(0, 0, starCategory);
    }
  }
}

function wait(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

function fetchHomeCards(searchKeywords, attempt = 0) {
  return http.get(`/api/v1/card/tree?keywords=${encodeURIComponent(searchKeywords)}`).catch(error => {
    if (attempt >= HOME_CARDS_RETRY_DELAYS.length) {
      return Promise.reject(error);
    }
    return wait(HOME_CARDS_RETRY_DELAYS[attempt]).then(() => fetchHomeCards(searchKeywords, attempt + 1));
  });
}

const actions = {
  clearSearchAndLoadHomeCards({commit, dispatch}) {
    commit('SET_SEARCH_KEYWORDS', '');
    return dispatch('loadHomeCards', '');
  },
  setStarCard({commit}, card) {
    const star = card.star;
    if (star) {
      if (localStorage.starCardIds) {
        const arr = localStorage.starCardIds.split(',');
        arr.splice(arr.indexOf(card.id), 1);
        localStorage.starCardIds = arr.join(',');
      }
    } else {
      saveStarCard([card.id]);
    }
    commit('LOAD_STAR_CARD');
    const params = localStorage.starCardIds ? localStorage.starCardIds.split(',') : [];
    http.patch('/api/v1/user/star/card', params);
    return Promise.resolve(!star);
  },
  // 加载
  loadHomeCards({commit}, keywords) {
    const requestSerial = ++homeCardsRequestSerial;
    const searchKeywords = keywords || '';
    commit('SET_HOME_CARDS_LOADING', true);
    commit('SET_HOME_CARDS_LOAD_ERROR', false);
    return fetchHomeCards(searchKeywords).then(res => {
      if (requestSerial !== homeCardsRequestSerial) {
        return res;
      }
      commit('CHANGE_DATA', res);
      commit('LOAD_STAR_CARD');
      commit('SET_HOME_CARDS_LOAD_ERROR', false);
      commit('SET_HOME_CARDS_LOADING', false);
      loadCardDynamicContent(state.datas);
      return res;
    }).catch(error => {
      if (requestSerial === homeCardsRequestSerial) {
        commit('SET_HOME_CARDS_LOAD_ERROR', true);
        commit('SET_HOME_CARDS_LOADING', false);
      }
      return Promise.reject(error);
    });
  },
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
