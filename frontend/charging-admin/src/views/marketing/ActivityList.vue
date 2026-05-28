<template>
  <div class="activity-list">
    <h2>营销活动</h2>
    <el-button type="primary" @click="createActivity">创建活动</el-button>
    <el-table :data="activities">
      <el-table-column prop="name" label="活动名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column>
        <template #default="{ row }">
          <el-button @click="launch(row.id)" v-if="row.status === 'draft'">上线</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
export default {
  data() { return { activities: [{ id: 1, name: '新用户优惠', type: 'discount', status: 'active' }] } },
  methods: {
    createActivity() { alert('创建活动') },
    launch(id) { alert('上线活动' + id) }
  }
}
</script>
