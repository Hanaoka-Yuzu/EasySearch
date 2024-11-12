import instance from '@/utils/request'

export const getCount = () =>
  instance.get('/search/count')

export const getCourtDetail = (province) =>
  instance.get('/search/count/court', {
    params: {
      province
    }
  })

export const getCause = (parentCause) =>
  instance.get('/search/count/cause', {
    params: {
      parentCause
    }
  })

export const getDocument = (pageNo, pageSize, sortByJudgmentDate, queryMap) =>
  instance.post('/search/complex', {
    pageNo: pageNo,
    pageSize: pageSize,
    sortByJudgmentDate: sortByJudgmentDate,
    queryMap: queryMap
  })

export const getSearchSuggestion = (keyword) =>
  instance.get('/search/suggestion', {
    params: {
      keyword
    }
  })
