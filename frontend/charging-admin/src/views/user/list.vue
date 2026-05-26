<template>
  <div class="user-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="userId" label="用户 ID" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="真实姓名" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const tableData = ref([
  {
    userId: 1,
    username: 'admin',
    realName: '超级管理员',
    phone: '13800138000',
    email: 'admin@charging.com',
    status: 1
  }
])

const handleEdit = (row) => {
  console.log('Edit user:', row)
}

const handleDelete = (row) => {
  console.log('Delete user:', row)
}
</script>

<style scoped lang="scss">
.user-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
