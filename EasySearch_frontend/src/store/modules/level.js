export default {
  namespaced: true,
  state () {
    return {
      recursionTree: {},
      query: {}
    }
  },
  mutations: {
    setRecursionTree (state, obj) {
      state.recursionTree = obj
    },
    setQuery (state, obj) {
      state.query = obj
      console.log(state.query)
    },
    resetQuery (state) {
      state.query = {}
    }
  },
  actions: {},
  getters: {}
}
