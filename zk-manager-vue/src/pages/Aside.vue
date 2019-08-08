<template>
  <el-aside width="22%">
    <div class="home-aside">
        <div class="home-aside-input">
            <el-input
                placeholder="请输入内容"
                v-model="input"
                @change="searchChange"
                size="small"
            >
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
        </div>
        <div class="home-aside-tree">
            <el-tree
                ref="tree"
                :data="data"
                :props="defaultProps"
                @node-click="handleNodeClick"
                node-key="path"
                highlight-current
                :render-content="renderContent"
                :filter-node-method="filterNode"
            ></el-tree>
        </div>
    </div>
  </el-aside>
</template>

<script>
export default {
  name: 'Aside',
  data () {
    return {
      input: '',
      nodePath: '/',
      data: [],
      defaultProps: {
        children: 'children',
        label: 'label',
        icon: 'icon',
        path: 'path'
      }
    }
  },
  computed: {
    zkNode () {
      return this.$store.state.host
    }
  },
  watch: {
    zkNode (val) {
      this.load()
    }
  },
  mounted () {
    this.load()
  },
  methods: {
    filterNode (value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    searchChange (input) {
      this.$refs.tree.filter(input)
    },
    handleNodeClick () {
    //   console.log(this.$refs.tree.getCurrentNode().label)
      // console.log(this.$refs.tree.getCurrentNode().path)
      this.nodePath = this.$refs.tree.getCurrentNode().path
      this.$store.commit('changeNodePath', this.nodePath)

      const request = {'node': this.$store.state.host, 'path': this.nodePath}
      this.$axios.post('/api/getNodeDetail', request).then(res => {
        const data = res.data
        if (data.status === 'fail') {
          this.$message({
            type: 'error',
            message: 'Error:' + data.msg
          })
        } else {
          this.$store.commit('changeNodeStat', data.stat)
          this.$store.commit('changeNodeData', data.data)
        }
      })
    },
    renderContent: function (h, { node, data, store }) {
      return (
        <span>
          <i class={data.icon}></i>
          <span style="padding-left: 4px;">{node.label}</span>
        </span>
      )
    },
    load () {
      this.$axios.get('/api/getAllZkNodes', {
        params: {
          node: this.$store.state.host
        }
      }).then(response => {
        const data = response.data
        if (data.status === 'success') {
          this.data = [data.allZkNode]
        } else {
          this.$message({
            type: 'error',
            message: 'Error:' + data.msg
          })
        }
      })
    }
  }
}
</script>

<style lang="stylus" scoped>
  .el-aside
    color: #333
    line-height: 10px
    border-radius: 6px
    margin: 1px
    .home-aside
      margin: 5px
      padding: 3px
      border: 1px solid #DCDFE6
      border-radius: 6px
      .home-aside-input
        padding: 2px
        overflow: hidden
        font-size: 14px
        height: 0
        padding-bottom: 20%
      .home-aside-tree
        padding: 0px 3px
        font-size: 14px
</style>
