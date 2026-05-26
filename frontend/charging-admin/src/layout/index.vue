<template>
  <div class="layout-container">
    <el-container>
      <el-aside width="210px">
        <div class="logo">
          <span>充电桩管理平台</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/charging/list">
            <el-icon><Lightning /></el-icon>
            <span>充电桩管理</span>
          </el-menu-item>
          <el-menu-item index="/order/list">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="/user/list">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-content">
            <div class="breadcrumb">
              <span>欢迎使用充电桩后台管理系统</span>
            </div>
            <div class="user-info">
              <el-dropdown>
                <span class="user-name">
                  <el-avatar size="small" icon="user" />
                  管理员
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleLogout">
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { HomeFilled, Lightning, Document, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const handleLogout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

.el-aside {
  background-color: #304156;
  color: #fff;
  overflow-x: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    font-weight: bold;
    color: #fff;
    background-color: #2b3a4b;
  }

  .el-menu {
    border-right: none;
  }
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
    padding: 0 20px;

    .user-info {
      .user-name {
        display: flex;
        align-items: center;
        gap: 8px;
        cursor: pointer;
      }
    }
  }
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>
