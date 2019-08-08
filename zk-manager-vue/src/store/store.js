import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

let preUser = ''
let prePass = ''
let preHost = ''
try {
  if (localStorage.username) {
    preUser = localStorage.username
  }
  if (localStorage.password) {
    prePass = localStorage.password
  }
  if (localStorage.host) {
    preHost = localStorage.host
  }
} catch (e) {}

export default new Vuex.Store({
  state: {
    username: preUser,
    password: prePass,
    host: preHost,
    nodePath: '/',
    stat: {},
    data: ''
  },
  mutations: {
    changeUserInfo (state, form) {
      state.username = form.username
      state.password = form.password
      try {
        localStorage.username = form.username
        localStorage.password = form.password
      } catch (e) {}
    },
    cleanUserInfo (state) {
      state.username = ''
      state.password = ''
      try {
        localStorage.username = ''
        localStorage.password = ''
      } catch (e) {}
    },
    changeZkHost (state, host) {
      state.host = host
      try {
        localStorage.host = host
      } catch (e) {}
    },
    changeNodePath (state, nodePath) {
      state.nodePath = nodePath
    },
    changeNodeStat (state, stat) {
      state.stat = stat
    },
    changeNodeData (state, data) {
      state.data = data
    }
  }
})
