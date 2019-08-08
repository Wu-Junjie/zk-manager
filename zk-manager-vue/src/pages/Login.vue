<template>
  <div>
    <el-form
      :model="form"
      status-icon
      :rules="rules"
      ref="form"
      label-width="100px"
      class="login-container"
    >
      <h2 class="login_title">登录</h2>
      <el-form-item label="用户名" prop="username">
        <el-input type="text" v-model="form.username" autocomplete="off" ></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input type="password" v-model="form.password" autocomplete="off"></el-input>
      </el-form-item>
      <el-checkbox class="login_remember" v-model="form.rememberFlag" label-position="left">记住密码</el-checkbox>
      <el-form-item>
        <el-button type="primary" @click="submitForm('form')">提交</el-button>
        <el-button @click="resetForm('form')">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import {postRequest} from '../utils/api'
export default {
  data () {
    var validateUser = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入用户名'))
      } else {
        if (this.form.username !== '') {
          callback()
        }
      }
    }
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.form.password !== '') {
          callback()
        }
      }
    }
    return {
      form: {
        username: this.$store.state.username,
        password: this.$store.state.password,
        rememberFlag: true
      },
      rules: {
        username: [{ validator: validateUser, trigger: 'blur' }],
        password: [{ validator: validatePass, trigger: 'blur' }]
      }
    }
  },
  methods: {
    submitForm (form) {
      this.$refs[form].validate((valid) => {
        if (valid) {
          // const loginInfo = {'username':this.fo}
          postRequest('/login', {
            username: this.form.username,
            password: this.form.password
          }).then(res => {
            if (res.data.status === 'success') {
              if (this.form.rememberFlag) {
                this.$store.commit('changeUserInfo', this.form)
              } else {
                this.$store.commit('cleanUserInfo')
              }
              this.$router.replace({path: '/home'})
              this.$notify({
                title: 'Login Success',
                type: 'success',
                duration: 800
              })
            } else {
              this.$notify.error({
                title: 'Login Error',
                message: 'Please enter correct username and password!'
              })
              this.form.username = ''
              this.form.password = ''
              this.$store.commit('cleanUserInfo')
              return false
            }
          })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    resetForm (form) {
      this.$refs[form].resetFields()
    }
  }
}
</script>

<style lang="stylus" scoped>
  .login-container
    border-radius: 15px
    background-clip: padding-box
    margin: 180px auto
    width: 350px
    padding: 35px 35px 15px 35px
    background: #fff
    border: 1px solid #eaeaea
    box-shadow: 0 0 25px #cac6c6
    .login_title
      text-align: center
    .login_remember
      margin: 0px 0px 20px 120px
      text-align: center
</style>
