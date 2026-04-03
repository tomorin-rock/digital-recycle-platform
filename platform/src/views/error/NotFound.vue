<template>
  <div class="not-found-page">
    <div class="content">
      <h1>404</h1>
      <p>页面不存在</p>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.content {
  text-align: center;
}

.content h1 {
  font-size: 120px;
  color: #409EFF;
  margin-bottom: 20px;
  line-height: 1;
}

.content p {
  font-size: 24px;
  color: #606266;
  margin-bottom: 30px;
}
</style>
