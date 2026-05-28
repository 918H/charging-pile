<template>
  <div class="transaction-list">
    <h2>财务流水</h2>
    <el-table :data="transactions">
      <el-table-column prop="userId" label="用户 ID" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="createTime" label="时间" />
    </el-table>
  </div>
</template>
<script>
export default {
  data() { return { transactions: [{ userId: 1, type: 'recharge', amount: 100, status: 'success', createTime: '2026-05-28' }] } }
}
</script>
