<template>
  <div class="app">
    <div class='header'>
      <span>文书详情</span>
    </div>
    <div class="body">
      <div class="detail" v-if="detail.caseOrder">
        <h1 class="title">{{detail.caseOrder}}</h1>
        <hr>
        <div class="abstract">
          <div class="caseinfo">
            <div class="casecategory">案件分类：{{detail.caseCategory}}</div>
            <div class="casetype">案件类型：{{detail.caseType}}</div>
          </div>
          <div class="judicialinfo">
            <div class="judicialprocess">判决过程：{{detail.judicalProcess}}</div>
            <div class="doctype">文书类型：{{detail.docType}}</div>
          </div>
          <div class="courtinfo">
            <div class="court">法院：{{detail.court}}</div>
            <div class="courtlevel">法院级别：{{detail.courtLevel}}</div>
            <div class="courtprovince">法院省份：{{detail.courtProvince}}</div>
          </div>
          <div class="dateinfo">
            <div class="filingyear">发布年份：{{detail.filingYear}}</div>
            <div class="judgementdate">判决日期：{{detail.judgmentDate}}</div>
            <div class="closingyear">终审年份：{{detail.closingYear}}</div>
          </div>
          <div class="operation">
            <span @click="downloadDoc(detail.caseOrder)" style="margin-right: 10px">下载docx文件</span>
            <span @click="downloadTxt" style="margin-right: 10px">下载txt文件</span>
            <span @click="drawer = true" style="background-color: #4caf50">相关推荐</span>
          </div>
        </div>
        <div class="text">
          {{formattedText}}
        </div>
        <div class="drawer">
          <el-drawer
            :visible.sync="drawer"
            :direction="direction"
            size="60vw">
            <template #title>
              <h4>文书推荐</h4>
            </template>
            <div class="item" v-for="(item, index) in recommend" :key="index">
              <i @click="toRecommendDetailPage(item)">{{index + 1}}.&nbsp;&nbsp;{{item}}</i>
            </div>
          </el-drawer>
        </div>
      </div>
      <div class="detail" v-else>
        <el-empty description="正在获取中"></el-empty>
      </div>
    </div>
  </div>
</template>

<script>
import { Message } from 'element-ui'
import { getDetail, getRecommend } from '@/api/detail'
export default {
  name: 'DocumentDetail',
  data () {
    return {
      detail: {},
      recommend: [],
      drawer: false,
      direction: 'rtl'
    }
  },
  computed: {
    formattedText () {
      // 将文本中的空格替换成换行符
      return this.detail.textWhole.replace(/\s/g, '\n\n')
    }
  },
  async created () {
    const res = await getDetail(this.$route.query.caseOrder)
    this.detail = res.data.caseDetail
    const res2 = await getRecommend(this.$route.query.caseOrder)
    this.recommend = res2.data.recommend
    console.log(this.recommend)
  },
  methods: {
    showData () {
      console.log(this.detail)
    },
    downloadTxt () {
      // 创建Blob对象，存储文本数据
      const blob = new Blob([this.detail.textWhole], { type: 'text/plain' })

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
    toRecommendDetailPage (caseOrder) {
      caseOrder = caseOrder.trim()
      const newPage = this.$router.resolve({
        path: '/detail',
        query: {
          caseOrder
        }
      })
      window.open(newPage.href, '_blank')
    }
  }
}
</script>

<style scoped lang="less">
.header {
  display: flex;
  justify-content: center;
  height: 8vh;
  width: 100%;
  background-color: #ffffff;

  span {
    line-height: 8vh;
    font-size: 25px;
    font-weight: 700;
  }
}

.body {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
  height: 150vh;
  width: 100%;
  background-color: #f5f5f5;
  white-space: pre-line;

  .detail {
    margin-top: 2vh;
    padding: 5vh 5vh 2vh 5vh;
    overflow: auto;
    width: 90%;
    height: 148vh;
    background-color: #fff;

    .title {
      text-align: center;
      font-size: 35px;
      font-weight: 700;
    }

    hr {
      margin-top: 5vh;
      border-width: 2px;
    }

    .abstract {
      margin-top: 5vh;
      padding: 2vh 5vh 2vh 5vh;
      height: 20%;
      background-color: #f7f7f7;
      color: #9c9c9c;

      .caseinfo {
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 20%;
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
        height: 20%;
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
        height: 20%;
        width: 100%;

        .court {
          width: 30%;
        }
        .courtlevel {
          width: 30%;
        }
        .courtprovince {
          width: 30%;
        }
      }

      .dateinfo {
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 20%;
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

      .operation {
        display: flex;
        justify-content: flex-end;
        align-items: center;
        height: 20%;
        width: 100%;
        span {
          padding: 5px 5px 5px 5px;
          background-color: #218fc4;
          color: white;
          &:hover {
            text-decoration: underline;
            cursor: pointer;
          }
        }
      }
    }

    .text {
      margin-top: 2vh;
      line-height: 190%;
    }

    .drawer {
      h4 {
        font-size: 25px;
        font-weight: 700;
      }
      .item {
        padding: 2vh 2vh 2vh 5vh;
        border-top: 1px dashed rgba(0, 0, 0, 0.3);

        &:last-child {
          border-bottom: 1px dashed rgba(0, 0, 0, 0.3);
        }

        &:hover {
          background-color: #f5f5f5;
        }

        i {
          color: black;
        }

        i:hover {
          color: #06a9ee;
          cursor: pointer;
        }
      }
    }

    &::-webkit-scrollbar {
      width: 1px;
    }
  }

}

i {
  color: #4bc1b5;
}
</style>
