<template>
  <div class="pile-monitor">
    <h2>设备监控</h2>
    <el-table :data="piles">
      <el-table-column prop="pileId" label="充电桩 ID" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'charging' ? 'success' : 'info'">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="power" label="功率 (kW)" />
      <el-table-column prop="health" label="健康度" />
    </el-table>
  </div>
</template>
<script>
export default {
  data() { return { piles: [{ pileId: 1, status: 'charging', power: 7.5, health: 95 }] } }
}
</script>
