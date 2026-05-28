<template>
  <div class="ticket-list">
    <h2>客服工单</h2>
    <el-table :data="tickets">
      <el-table-column prop="ticketNo" label="工单号" />
      <el-table-column prop="userId" label="用户" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="priority" label="优先级" />
    </el-table>
  </div>
</template>
<script>
export default {
  data() { return { tickets: [{ ticketNo: 'T001', userId: 1, status: 'pending', priority: 'high' }] } }
}
</script>
