import { Message } from 'element-ui'

// 定制请求的实例

// 导入axios
import axios from 'axios'

import router from '@/router'
import store from '@/store'
// 定义一个变量,记录公共的前缀  ,  baseURL
const baseURL = '/api'
const instance = axios.create({
  baseURL
})
// 添加请求拦截器
instance.interceptors.request.use(
  // 请求前加配置项
  config => {
    const token = store.state.user.userInfo.token

    if (token) {
      config.headers.Authorization = token
    }

    return config
  },
  // 请求发生错误的回调
  err => {
    Promise.reject(err)
  }
)

// 添加响应拦截器
instance.interceptors.response.use(
  // 响应状态码为2开头
  result => {
    if (result.data.code === 200 || result.data.code === 40005) {
      return result.data
    } else {
      Message.error(result.data.message ? result.data.message : '操作失败')
      return Promise.reject(result.data)
    }
  },
  // 响应状态码非2开头
  err => {
    console.log(err)
    // 响应状态码为401则强制跳转到登录页
    if (err.response.status === 401) {
      Message.error('未登录，请先登录')
      router.push('/login')
    }
    Message.error('服务异常')
    return Promise.reject(err)// 异步的状态转化成失败的状态
  }
)

export default instance
