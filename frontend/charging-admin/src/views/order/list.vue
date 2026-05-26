<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>订单列表</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索订单号"
            style="width: 200px"
            prefix-icon="Search"
          />
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="orderNumber" label="订单号" />
        <el-table-column prop="userId" label="用户 ID" />
        <el-table-column prop="pileName" label="充电桩" />
        <el-table-column prop="totalAmount" label="金额" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const searchQuery = ref('')

const tableData = ref([])

const getStatusType = (status) => {
  const types = {
    0: 'warning',
    1: 'primary',
    2: 'success',
    3: 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    0: '待支付',
    1: '充电中',
    2: '已完成',
    3: '已取消'
  }
  return texts[status] || '未知'
}

const handleView = (row) => {
  console.log('View order:', row)
}
</script>

<style scoped lang="scss">
.order-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
