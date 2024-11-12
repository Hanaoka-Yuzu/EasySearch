import instance from '@/utils/request'

export const getDetail = (caseOrder) =>
  instance.get('/search/detail', {
    params: {
      caseOrder
    }
  })

export const getRecommend = (caseOrder) =>
  instance.get('/search/recommend', {
    params: {
      caseOrder
    }
  })
