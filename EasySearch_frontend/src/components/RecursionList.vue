<template>
  <div class="box">
    <ul>
      <i v-show="obj[term]" v-if="!show" class="el-icon-plus"  @click="toggleShow"></i>
      <i v-show="obj[term]" v-else class="el-icon-minus"  @click="toggleShow"></i>
      <span @click="query">{{name}}（{{obj.number}}）</span>
      <recursion-list v-show="show" v-for="(obj, name, index) in obj[term]" :key="index" :name="name" :obj = "obj" :term="term"/>
    </ul>
  </div>
</template>

<script>
import RecursionList from './RecursionList.vue'
export default {
  name: 'RecursionList',
  components: {
    RecursionList
  },
  props: ['name', 'obj', 'term'],
  data () {
    return {
      show: false
    }
  },
  methods: {
    toggleShow () {
      this.show = !this.show
    },
    query () {
      const obj = {}
      obj[this.term] = this.name
      this.$store.commit('level/setQuery', obj)
    }
  }
}
</script>

<style lang="less" scoped>
.box {
  background-color: white;

  ul {
    margin-top: 1vh;
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
  }

}
</style>
