<template>
  <div>
    <el-container direction="vertical" class="main-container">
      <home-header></home-header>
      <el-container>
        <home-aside></home-aside>
        <home-content></home-content>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import HomeAside from '@/pages/Aside'
import HomeHeader from '@/pages/Header'
import HomeContent from '@/pages/Content'
export default {
  name: 'Home',
  components: {
    HomeHeader,
    HomeAside,
    HomeContent
  },
  data () {
    return {}
  }
}
</script>

<style lang="stylus" scoped>
  .main-container
    height: 500px
</style>
