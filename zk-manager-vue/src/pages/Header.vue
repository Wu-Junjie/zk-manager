<template>
  <el-header height=100px>
    <div class="header">
      <font class="header-font">
        <i class="el-icon-s-home">Zookeeper Manager</i>
      </font>
      <div class="header-node">
        <el-select v-model="host" filterable placeholder="请选择节点" size="small" @change="change" class="node-select">
          <el-option
            v-for="item in nodeOptions"
            :key="item.host"
            :label="item.label"
            :value="item.host"
            loading
          >
          </el-option>
        </el-select>
        <el-button type="info" size="small" icon="el-icon-plus" plain class="node-add" @click="add">Add</el-button>
      </div>
    </div>
  </el-header>
</template>

<script>
export default {
  name: 'Header',
  data () {
    return {
      host: this.$store.state.host,
      nodeOptions: [
        {
          host: 'localhost:2181',
          label: 'localhost:2181'
        }
      ]
    }
  },
  inject: ['reload'],
  methods: {
    change () {
      this.$store.commit('changeZkHost', this.host)
      // console.log(this.host)
      this.reload()
    },
    add () {
      this.$prompt('Please Enter Zookeeper Host&Port', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }).then(({ value }) => {
        if (!value) {
          this.$message({
            type: 'info',
            message: 'Host cannot be null'
          })
        } else {
          this.$message({
            type: 'success',
            message: 'Your ZK Host: ' + value
          })
          this.host = value
          this.nodeOptions.push({host: value, label: value})
          const addNode = {'node': this.host}
          this.$axios.post('/api/addNode', addNode).then(res => {
            if (res.data === true) {
              this.$store.commit('changeZkHost', value)
              this.reload()
            }
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '取消输入'
        })
      })
    }
  },
  mounted () {
    this.$axios.get('/api/getNodeList').then((response) => {
      this.nodeOptions = response.data
    })
  }
}
</script>

<style lang="stylus" scoped>
  .el-header
    background-color: #B3C0D1
    color: #333
    text-align: left
    border-radius: 6px
    border-bottom: 5px
    height: 100px
    .header
      padding: 5px
      margin: 0px auto
      line-height: 90px
      .header-font
        margin: 0 auto
        font-family: Helvetica
        font-size: 22px
        height: 40px
      .header-node
        margin: 0 auto
        float: right
        height: 50px
        width: 300px
        .node-select
          height: 10px
        .node-add
          padding: 10px
          height: 32px
</style>
