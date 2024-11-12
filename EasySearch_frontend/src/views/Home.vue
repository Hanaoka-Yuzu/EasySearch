<template>
  <div>
    <div class="header">
      <h1>法律法规</h1>
      <div class="searcharea">
        <el-form>
          <el-form-item class="search_mode" prop="orgManagerId" style="width: 8vw; margin-top: 22px; margin-right: 5px;">
            <el-select v-model="currentMode">
              <el-option
                v-for="item in searchMode"
                :key="item[0]"
                :label="item[0]"
                :value="item[1]"
                @click.native="showDocument"
              />
            </el-select>
          </el-form-item>
        </el-form>
        <el-autocomplete
          class="search_area"
          style="width: 35vw"
          v-model="queryString"
          :fetch-suggestions="querySearch"
          placeholder="请输入内容"
          @select="handleSelect"
        ></el-autocomplete>
        <el-button style="margin-left:5px; height:5.5vh; border-radius: 4px; font-size: 15px" @click="clickNewSearch">新检索</el-button>
        <el-button style="margin-left:5px; height:5.5vh; border-radius: 4px; font-size: 15px" @click="clickSearch">结果中搜索</el-button>
      </div>
    </div>
    <div class="body wrapper">
      <ul class="querymap">
        <span>搜索条件：</span>
        <li v-for="(value, key, index) in queryMap" :key="index">
          <i class="el-icon-close" @click="deleteFromQueryMap(key)"></i>
          <span>{{termLanguageMap[key]}}：{{value}}</span>
        </li>
      </ul>
      <div class="left">
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showClosingYear" class="el-icon-plus" @click="toggleShow('closingyear')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('closingyear')"></i>
            <span class="casetype">终审年份</span>
          </h4>
          <ul v-show="showRecursionTree.showClosingYear" v-for="(value, key, index) in countInfo.closing_year" :key="index">
            <span @click="updateQueryMap('closing_year', key)">{{key}}（{{value}}）</span>
          </ul>
        </div>
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showCourtLevel" class="el-icon-plus" @click="toggleShow('courtlevel')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('courtlevel')"></i>
            <span class="casetype">法院等级</span>
          </h4>
          <ul v-show="showRecursionTree.showCourtLevel" v-for="(value, key, index) in countInfo.court_level" :key="index">
            <span @click="updateQueryMap('court_level', key)">{{key}}（{{value}}）</span>
          </ul>
        </div>
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showCourtProvince" class="el-icon-plus" @click="toggleShow('courtprovince')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('courtprovince')"></i>
            <span class="casetype">法院省份</span>
          </h4>
          <ul v-show="showRecursionTree.showCourtProvince" v-for="(value, key, index) in countInfo.court_province" :key="index">
            <i v-if="!showRecursionTree.showCourt[key]" class="el-icon-plus"  @click="toggleShowCourt(key)"></i>
            <i v-else class="el-icon-minus"  @click="toggleShowCourt(key)"></i>
            <span @click="updateQueryMap('court_province', key)">{{key}}（{{value}}）</span>
            <li v-show="showRecursionTree.showCourt[key]" v-for="(value2, key2, index2) in courtInfo.court[key]" :key="index2" @click="updateQueryMap('court', key2)">{{key2}}（{{value2}}）</li>
          </ul>
        </div>
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showCaseType" class="el-icon-plus" @click="toggleShow('casetype')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('casetype')"></i>
            <span class="casetype">案件类型</span>
          </h4>
          <ul v-show="showRecursionTree.showCaseType" v-for="(value, key, index) in countInfo.case_type" :key="index">
            <span @click="updateQueryMap('case_type', key)">{{key}}（{{value}}）</span>
          </ul>
        </div>
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showCause" class="el-icon-plus" @click="toggleShow('cause')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('cause')"></i>
            <span class="casetype">案件案由</span>
          </h4>
          <recursion-list v-show="showRecursionTree.showCause" v-for="(obj, name, index) in recursionTree['cause']" :key="index" :name="name" :obj = "obj" :term="term"/>
        </div>
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showDocType" class="el-icon-plus" @click="toggleShow('doctype')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('doctype')"></i>
            <span class="casetype">文书类型</span>
          </h4>
          <ul v-show="showRecursionTree.showDocType" v-for="(value, key, index) in countInfo.doc_type" :key="index">
            <span @click="updateQueryMap('doc_type', key)">{{key}}（{{value}}）</span>
          </ul>
        </div>
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showJudicialProcess" class="el-icon-plus" @click="toggleShow('judicialprocess')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('judicialprocess')"></i>
            <span class="casetype">判决过程</span>
          </h4>
          <ul v-show="showRecursionTree.showJudicialProcess" v-for="(value, key, index) in countInfo.judical_process" :key="index">
            <span @click="updateQueryMap('judical_process', key)">{{key}}（{{value}}）</span>
          </ul>
        </div>
        <div class="recursiontree">
          <h4>
            <i v-if="!showRecursionTree.showFilingYear" class="el-icon-plus" @click="toggleShow('filingyear')"></i>
            <i v-else class="el-icon-minus" @click="toggleShow('filingyear')"></i>
            <span class="casetype">发布年份</span>
          </h4>
          <ul v-show="showRecursionTree.showFilingYear" v-for="(value, key, index) in countInfo.filing_year" :key="index">
            <span @click="updateQueryMap('filing_year', key)">{{key}}（{{value}}）</span>
          </ul>
        </div>
      </div>
      <div class="right">
        <div class="batchdownload">
          <div class="batchdownloadinfo" v-if="clickBatchDownload">
            <span>已选择数量：{{batchDownloadNum}}</span>
            <span>
              <i class="el-icon-check" @click="batchDownloadDoc">确认下载</i>
              <i class="el-icon-close" @click="exitBatchDownload">退出</i>
            </span>
          </div>
          <i class="el-icon-download" v-else @click="clickBatchDownload = true">批量下载</i>
        </div>
        <div class="item" v-for="(item, index) in documents" :key=index>
          <div class="t">
            <span>
              <el-checkbox v-if="clickBatchDownload" @change="changeBatchDownloadArr(item.caseOrder)">&nbsp;</el-checkbox>
            </span>
            <span @click="toDetailPage(item.caseOrder)" v-html="highLight(item.caseOrder)">{{item.caseOrder}}</span>
          </div>
          <div class="abstract">
            <div class="caseinfo">
            <div class="casecategory">案件分类：{{item.caseCategory}}</div>
            <div class="casetype">案件类型：{{item.caseType}}</div>
          </div>
          <div class="judicialinfo">
            <div class="judicialprocess">判决过程：{{item.judicalProcess}}</div>
            <div class="doctype">文书类型：{{item.docType}}</div>
          </div>
          <div class="courtinfo">
            <div class="court">法院：{{item.court}}</div>
            <div class="courtlevel">法院级别：{{item.courtLevel}}</div>
            <div class="courtprovince">法院省份：{{item.courtProvince}}</div>
          </div>
          <div class="dateinfo">
            <div class="filingyear">发布年份：{{item.filingYear}}</div>
            <div class="judgementdate">判决日期：{{item.judgmentDate}}</div>
            <div class="closingyear">终审年份：{{item.closingYear}}</div>
          </div>
          </div>
          <div class="operation">
            <i class="el-icon-download" @click="downloadDoc(item.caseOrder)" style="margin-right: 10px; margin-top: 3px">下载docx</i>
            <i class="el-icon-download" @click="downloadTxt(item.caseOrder)" style="margin-top: 3px">下载txt</i>
          </div>
        </div>
        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="pageNo"
            :page-sizes="[5, 10, 15]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totalHit">
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { setVuexRecursionTree } from '@/utils/recursionTree'
import { getCount, getCourtDetail, getDocument, getSearchSuggestion } from '@/api/search'
import { getDetail } from '@/api/detail'
import { debounce } from '@/utils/debounce'
import { Message, Loading, MessageBox } from 'element-ui'
import RecursionList from '@/components/RecursionList.vue'
export default {
  name: 'HomePage',
  components: {
    RecursionList
  },
  data () {
    return {
      showRecursionTree: {
        showClosingYear: false,
        showCourtLevel: false,
        showCourtProvince: false,
        showCourt: {},
        showCaseType: false,
        showCause: false,
        showDocType: false,
        showJudicialProcess: false,
        showFilingYear: false
      },
      countInfo: {},
      courtInfo: { court: {} },
      queryMap: {},
      documents: [],
      pageNo: 1,
      pageSize: 5,
      totalHit: 100,
      queryString: '',
      searchMode: [
        ['相关度排序', null],
        ['判决日期最晚', 'desc'],
        ['判决日期最早', 'asc']
      ],
      currentMode: null,
      searchSuggestion: {},
      formattedSearchSuggestion: [],
      choosedSearchSuggestion: {},
      termLanguageMap: {
        case_order: '文书标题',
        cause: '案由',
        text_whole: '模糊搜索',
        litigant: '诉讼当事人',
        lawyer: '律师',
        law_firm: '律师事务所',
        judge: '法官',
        court: '法院',
        court_level: '法院等级',
        court_province: '法院省份',
        court_city: '法院城市',
        case_type: '案件类型',
        doc_type: '文书类型',
        case_category: '案件分类',
        judical_process: '判决过程',
        filing_year: '发布年份',
        closing_year: '终审年份',
        judgment_date_begin: '判决开始日期',
        judgment_date_end: '判决结束日期'
      },
      term: 'cause',
      clickBatchDownload: false,
      batchDownloadArr: []
    }
  },
  watch: {
    queryMap: {
      deep: true,
      handler () {
        this.pageNo = 1
        this.pageSize = 5
        this.showDocument()
      }
    },
    query: {
      deep: true,
      handler () {
        for (const key in this.query) {
          this.$set(this.queryMap, key, this.query[key])
          this.$store.commit('level/resetQuery')
        }
      }
    }
  },
  computed: {
    ...mapState('level', ['recursionTree', 'query']),
    batchDownloadNum () {
      return this.batchDownloadArr.length
    }
  },
  methods: {
    toggleShow (flag) {
      switch (flag) {
        case 'closingyear':
          this.showRecursionTree.showClosingYear = !this.showRecursionTree.showClosingYear
          break
        case 'courtlevel':
          this.showRecursionTree.showCourtLevel = !this.showRecursionTree.showCourtLevel
          break
        case 'courtprovince':
          this.showRecursionTree.showCourtProvince = !this.showRecursionTree.showCourtProvince
          break
        case 'casetype':
          this.showRecursionTree.showCaseType = !this.showRecursionTree.showCaseType
          break
        case 'cause':
          this.showRecursionTree.showCause = !this.showRecursionTree.showCause
          this.$forceUpdate()
          break
        case 'doctype':
          this.showRecursionTree.showDocType = !this.showRecursionTree.showDocType
          break
        case 'judicialprocess':
          this.showRecursionTree.showJudicialProcess = !this.showRecursionTree.showJudicialProcess
          break
        case 'filingyear':
          this.showRecursionTree.showFilingYear = !this.showRecursionTree.showFilingYear
          break
      }
    },
    async toggleShowCourt (courtProvince) {
      const res = await getCourtDetail(courtProvince)
      this.$set(this.courtInfo.court, courtProvince, res.data.court)
      if (this.showRecursionTree.showCourt[courtProvince]) {
        this.$set(this.showRecursionTree.showCourt, courtProvince, false)
      } else {
        this.$set(this.showRecursionTree.showCourt, courtProvince, true)
      }
    },
    updateQueryMap (key, value) {
      this.$set(this.queryMap, key, value)
    },
    deleteFromQueryMap (key, value) {
      this.$delete(this.queryMap, key)
    },
    async showDocument () {
      this.documents = []
      const loadingInstance = Loading.service({ target: '.right', background: 'rgba(255, 255, 255, 1)' })
      const res = await getDocument(this.pageNo, this.pageSize, this.currentMode, this.queryMap)
      this.documents = res.data.complex
      this.totalHit = res.data.totalHit
      this.exitBatchDownload()
      Message.success('根据条件搜索成功')
      loadingInstance.close()
    },
    handleSizeChange (newSize) {
      this.pageSize = newSize
      this.showDocument()
    },
    handleCurrentChange (newCurrent) {
      this.pageNo = newCurrent
      this.showDocument()
    },
    async collectSearchSuggestion (keyword) {
      const res = await getSearchSuggestion(keyword)
      this.searchSuggestion = res.data
      this.formatSearchSuggestion()
    },
    formatSearchSuggestion () {
      for (const key in this.searchSuggestion) {
        if (this.searchSuggestion[key].length === 0) {
          continue
        }
        for (let i = 0; i < this.searchSuggestion[key].length; i++) {
          const obj = {}
          obj.value = this.searchSuggestion[key][i]
          obj.term = key
          this.formattedSearchSuggestion.push(obj)
        }
      }
    },
    querySearch (queryString, cb) {
      const stringWithoutSpace = this.removeSpace(queryString)
      this.searchSuggestion = {}
      this.formattedSearchSuggestion = []
      this.choosedSearchSuggestion = {}
      if (stringWithoutSpace.length === 0) {
        cb(this.formattedSearchSuggestion)
        return
      }
      if (stringWithoutSpace !== '') {
        debounce(this.collectSearchSuggestion(stringWithoutSpace), 500)
      }
      cb(this.formattedSearchSuggestion)
    },
    handleSelect (item) {
      console.log(item)
      this.choosedSearchSuggestion = item
      this.updateQueryMap(item.term, item.value)
      console.log(this.queryMap)
      this.queryString = ''
      this.choosedSearchSuggestion = {}
    },
    highLight (title) {
      let each
      for (each in this.queryMap) {
        title = title.replace(
          this.queryMap[each],
          '<font style="color:red!important;">' + this.queryMap[each] + '</font>'
        )
      }
      return title
    },
    clickSearch () {
      const stringWithoutSpace = this.removeSpace(this.queryString)
      if (stringWithoutSpace.length === 0) {
        Message.error('搜索关键字不能为空！')
        return
      }
      this.updateQueryMap('text_whole', stringWithoutSpace)
      this.queryString = ''
      this.choosedSearchSuggestion = {}
    },
    clickNewSearch () {
      const stringWithoutSpace = this.removeSpace(this.queryString)
      if (stringWithoutSpace.length === 0) {
        Message.error('搜索关键字不能为空！')
        return
      }
      this.queryMap = {}
      this.updateQueryMap('text_whole', stringWithoutSpace)
      this.queryString = ''
      this.choosedSearchSuggestion = {}
    },
    toDetailPage (caseOrder) {
      const newPage = this.$router.resolve({
        path: '/detail',
        query: {
          caseOrder
        }
      })
      window.open(newPage.href, '_blank')
    },
    async downloadTxt (caseOrder) {
      const res = await getDetail(caseOrder)
      // 创建Blob对象，存储文本数据
      const blob = new Blob([res.data.caseDetail.textWhole], { type: 'text/plain' })

      // 创建下载链接
      const downloadLink = document.createElement('a')
      downloadLink.href = window.URL.createObjectURL(blob)

      // 设置下载文件的文件名
      downloadLink.download = 'data.txt'

      // 将下载链接添加到文档中
      document.body.appendChild(downloadLink)

      // 模拟点击下载链接
      downloadLink.click()

      // 下载完成后移除下载链接
      document.body.removeChild(downloadLink)

      Message.success('下载成功')
    },
    downloadDoc (caseOrder) {
      window.location.href = 'http://172.29.7.210:8087/download/downloadDoc?caseOrder=' + caseOrder + '.docx'
      Message.success('下载成功')
    },
    batchDownloadDoc () {
      let batchDownloadUrl = 'http://172.29.7.210:8087/download/batchDownloadDoc?'
      this.batchDownloadArr.forEach((caseOrder) => {
        caseOrder = 'caseOrder=' + caseOrder + '.docx' + '&&'
        batchDownloadUrl += caseOrder
      })
      batchDownloadUrl = batchDownloadUrl.slice(0, -2)
      console.log(batchDownloadUrl)
      console.log(this.batchDownloadArr)
      MessageBox.confirm('是否确认批量下载？', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        window.location.href = batchDownloadUrl
        Message.success('下载成功')
        this.exitBatchDownload()
      }).catch(() => {
        Message.info('已取消批量下载')
        this.exitBatchDownload()
      })
    },
    removeSpace (str) {
      const regex = /(^\s+)|(\s+$)/g
      return str.replaceAll(regex, '')
    },
    changeBatchDownloadArr (caseOrder) {
      if (this.batchDownloadArr.includes(caseOrder)) {
        const index = this.batchDownloadArr.indexOf(caseOrder)
        this.batchDownloadArr.splice(index, 1)
      } else {
        this.batchDownloadArr.push(caseOrder)
      }
      console.log(this.batchDownloadArr)
    },
    exitBatchDownload () {
      this.batchDownloadArr = []
      this.clickBatchDownload = false
    }
  },
  async created () {
    const res1 = await getCount()
    this.countInfo = res1.data
    this.showDocument()
    setVuexRecursionTree()
  }
}
</script>

<style lang="less" scoped>
.header{
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 0 5% 0 5%;
  height: 16vh;
  width: 100%;
  background-color: #218fc4;
  h1 {
    color: white;
    font-size: 46px;
  }
  .searcharea {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin: 0 auto;
  }
  /deep/ .el-input__inner{
    height: 5.5vh;
    font-size: 15px;
  }
  .search_mode {
    /deep/ .el-input__inner{
      height: 5.5vh;
      font-size: 15px;
    }
  }
}

.body {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  align-content: flex-start;
  margin-bottom: 8vh;
  height: 150vh;
  width: 100%;
  background-color: #f5f5f5;
  white-space: pre-line;

  .querymap {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    flex-wrap: wrap;
    margin-top: 2vh;
    min-height: 8vh;
    width: 100%;
    background-color: white;
    span:first-child {
      margin-left: 2vh;
      color: #9c9c9c;
    }
    li {
      margin: 1vh 2vh 1vh 2vh;
      padding: 0 2vh 0 2vh;
      height: 4vh;
      background-color: #06a9ee;
      color: white;
      line-height: 4vh;
      i {
        cursor: pointer;
        vertical-align: baseline;
      }
      span {
        margin-left: 1vh;
      }
    }
  }

  .left {
    height: 132vh;
    width: 30%;

    .recursiontree {
      margin: 2vh 0 2vh 0;
      padding: 2vh 2vh 2vh 2vh;
      min-height: 10vh;
      width: 90%;
      background-color: white;

      h4 {
        border-bottom: 1px solid black;
        margin-bottom: 1vh;
        i:hover {
          cursor: pointer;
          color: #06a9ee;
        }
        span {
          margin-left: 2vh;
        }
      }

      ul {
        margin-bottom: 1vh;
        padding-left: 2vh;
        i:hover {
          cursor: pointer;
          color: #06a9ee;
        }
        span {
          margin-left: 2vh;
          &:hover {
            color: #06a9ee;
            cursor: pointer;
          }
        }
        li {
          margin-top: 1vh;
          padding-left: 2vh;
          &:hover {
            color: #06a9ee;
            cursor: pointer;
          }
        }
      }

    }
  }

  .right {
    overflow: auto;
    margin-top: 2vh;
    padding: 2vh 2vh 0 2vh;
    width: 60%;
    background-color: white;

    .batchdownload {
      padding-bottom: 2vh;
      border-bottom: 1px dashed black;

      i {
        color: #06a9ee;
      }

      i:hover {
          cursor: pointer;
        }

      .batchdownloadinfo {
        display: flex;
        justify-content: space-between;
        align-items: center;

        span {
          color: #06a9ee;

          i:first-child {
            color: #67c23a;
            margin-right: 2vw;
          }

          i:last-child {
            color: #f56c6c;
          }

          i:hover {
            cursor: pointer;
          }
        }
      }
    }

    .item {
      padding: 2vh 2vh 2vh 2vh;
      height: 30vh;
      width: 100%;
      border-bottom: 1px dashed black;
      &:hover {
        background-color: #f5f5f5;
      }

      .t {
        height: 30%;
        font-size: 20px;

        span:first-child {
          vertical-align: top;
        }

        span:hover {
          cursor: pointer;
          color: #06a9ee;
        }

      }

      .abstract {
        height: 60%;
        color: #9c9c9c;
        font-size: 14px;

        .caseinfo {
          display: flex;
          justify-content: space-between;
          align-items: center;
          height: 25%;
          width: 100%;

          .casecategory {
            width: 40%;
          }

          .casetype {
            width: 40%;
          }
        }

        .judicialinfo {
          display: flex;
          justify-content: space-between;
          align-items: center;
          height: 25%;
          width: 100%;

          .judicialprocess {
            width: 40%;
          }

          .doctype {
            width: 40%;
          }
        }

        .courtinfo {
          display: flex;
          justify-content: space-between;
          align-items: center;
          height: 25%;
          width: 100%;

          .court {
            width: 50%;
          }
          .courtlevel {
            width: 20%;
          }
          .courtprovince {
            width: 20%;
          }
        }

        .dateinfo {
          display: flex;
          justify-content: space-between;
          align-items: center;
          height: 25%;
          width: 100%;

          .filingyear {
            width: 30%;
          }
          .judgementdate {
            width: 30%;
          }
          .closingyear {
            width: 30%;
          }
        }

      }

      .operation {
        display: flex;
        justify-content: flex-end;

        i:hover {
          cursor: pointer;
          color: #06a9ee;
        }
      }

    }

    .pagination {
      display: flex;
      justify-content: center;
      margin: 5vh 0 5vh 0;
    }

  }
}

.wrapper {
  margin: 0 auto;
  width: 90vw;
}
</style>
