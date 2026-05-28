<template>
  <div class="message-list">
    <div class="header">
      <h2>消息中心</h2>
      <div class="actions">
        <el-button type="primary" @click="markAllAsRead">全部标记已读</el-button>
      </div>
    </div>
    <el-table :data="messages">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="isRead" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.isRead ? 'success' : 'danger'">{{ row.isRead ? '已读' : '未读' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" />
    </el-table>
  </div>
</template>
<script>
export default {
  data() { return { messages: [] } },
  methods: {
    load() { this.messages = [
      { id: 1, title: '订单完成', isRead: false, createTime: '2026-05-28 10:00' },
      { id: 2, title: '充值成功', isRead: true, createTime: '2026-05-28 09:00' }
    ]},
    markAllAsRead() { this.messages.forEach(m => m.isRead = true) }
  },
  mounted() { this.load() }
}
</script>
