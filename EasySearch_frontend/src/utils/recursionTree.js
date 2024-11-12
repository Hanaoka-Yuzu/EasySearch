import { getCount, getCause, getCourtDetail } from '@/api/search'
import store from '@/store/index'

let recursionTree = {}
const res = await getCount()
recursionTree = res.data
const res2 = await getCause('')
recursionTree.cause = res2.data.cause

const createRecursionTree = async (obj) => {
  if (typeof obj !== 'object' || Object.keys(obj).length === 0) {
    console.log('到最底层了')
    return
  }
  for (const key in obj) {
    if (key === 'cause') {
      for (const key2 in obj[key]) {
        const res = await getCause(key2)
        // obj[key][key2 + 'number'] = obj[key][key2]
        const t = obj[key][key2]
        obj[key][key2] = res.data
        obj[key][key2].number = t
        createRecursionTree(obj[key][key2])
      }
    }
    if (key === 'court_province') {
      for (const key2 in obj[key]) {
        const res = await getCourtDetail(key2)
        // obj[key][key2 + 'number'] = obj[key][key2]
        const t = obj[key][key2]
        obj[key][key2] = res.data
        obj[key][key2].number = t
        createRecursionTree(obj[key][key2])
      }
    }
  }
}

export const setVuexRecursionTree = () => {
  createRecursionTree(recursionTree)

  setTimeout(() => {
    console.log(recursionTree)
  }, 10000)
  store.commit('level/setRecursionTree', recursionTree)
}
