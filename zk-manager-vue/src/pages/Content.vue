<template>
    <el-main>
        <div class="content">
            <div class="content-path">
                <div>Path: {{this.$store.state.nodePath}}</div>
            </div>
            <div class="content-data">
                <div class="content-detail">
                  <!-- <pre class="">{{this.detail}}</pre> -->
                  <json-viewer
                    :value="detail"
                    :expand-depth="5"
                    copyable
                    class="detail"
                  ></json-viewer>
                </div>
                <div class="content-edit">
                  <el-input
                    type="textarea"
                    :autosize="{ minRows: 4, maxRows: 8}"
                    placeholder="data"
                    v-model="data">
                  </el-input>
                </div>
            </div>
            <div class="content-action">
                <div class="action-input">
                    <el-input
                        placeholder=""
                        v-model="input"
                        size="small"
                        clearable>
                    </el-input>
                </div>
                <div class="action-btn">
                    <el-button type="info" icon="el-icon-delete" @click="handleDelete" size="small" plain>Delete</el-button>
                    <el-button type="info" icon="el-icon-check" @click="handleSave" size="small" plain>Save</el-button>
                </div>
            </div>
        </div>
    </el-main>
</template>

<script>
export default {
  name: 'Content',
  data () {
    return {
      input: '',
      detail: '',
      stat: '',
      data: ''
    }
  },
  inject: ['reload'],
  computed: {
    nodePath () {
      return this.$store.state.nodePath
    },
    nodeDetail () {
      return {'stat': this.$store.state.stat, 'data': this.$store.state.data}
    },
    nodeData () {
      return this.$store.state.data
    }
  },
  watch: {
    nodePath (val) {
      this.input = val
    },
    nodeDetail (val) {
      this.detail = val
    },
    nodeData (val) {
      this.data = JSON.stringify(val)
    }
  },
  methods: {
    handleSave () {
      const host = this.$store.state.host
      const request = {'node': host, 'path': this.input, 'data': this.data}
      this.$axios.post('/api/saveNode', request).then(res => {
        if (res.data.status === 'fail') {
          this.$message({
            type: 'error',
            message: 'Save Data Error'
          })
        }
      })
      this.reload()
    },
    handleDelete () {
      this.$confirm('将删除\'' + this.input + '\'节点，是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.delete()
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Cancled Delete'
        })
      })
    },
    delete () {
      const host = this.$store.state.host
      const request = {'node': host, 'path': this.input}
      this.$axios.post('/api/deleteNode', request).then(response => {
        if (response.data.status === 'success') {
          this.$message({
            type: 'success',
            message: 'Delete Success!'
          })
          this.reload()
          // this.$router.go(0)
          // this.$router.push('/home')
        } else {
          this.$message({
            type: 'error',
            message: 'Delete Error!' + response.data.msg
          })
        }
      })
    }
  }
}
</script>

<style lang="stylus" scoped>
  .el-main
    color: #333
    border-radius: 6px
    margin: 1px
    padding: 0px
    font-size: 16px
    .content
      margin: 5px
      border: 1px solid #DCDFE6
      background: #fff
      border: 1px solid #DCDFE6
      border-radius: 4px
      .content-path
        background-color: #eee
        border-bottom: 1px solid #DCDFE6
        padding: 10px
        line-height: 30px
        padding-left: 20px
        height: 30px
        font-size: 18px
      .content-data
        .content-detail
          border-color: #DCDFE6
          background: #DCDFE6
          border-radius: 4px
          margin: 10px
          padding: 5px
          align: left
          .detail
            font-weight: normal
            background: transparent
            font-family: Helvetica Neue
        .content-edit
          border-color: #DCDFE6
          background: #DCDFE6
          border-radius: 4px
          margin: 10px
      .content-action
        background-color: #eee
        border-top: 1px solid #DCDFE6
        height: 35px
        margin: 0 auto
        line-height: 35px
        overflow: hidden
        padding: 5px
        .action-input
          width: 200px
          float: left
        .action-btn
          float: flex
          margin-left: 210px
</style>
