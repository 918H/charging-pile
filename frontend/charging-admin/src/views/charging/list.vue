<template>
  <div class="charging-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>充电桩列表</span>
          <el-button type="primary" @click="handleAdd">新增充电桩</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="pileName" label="充电桩名称" />
        <el-table-column prop="pileNumber" label="编号" />
        <el-table-column prop="locationName" label="位置" />
        <el-table-column prop="totalSlots" label="充电口总数" />
        <el-table-column prop="powerType" label="电源类型" />
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
    pileId: 1,
    pileName: '科技园充电站',
    pileNumber: 'PILE001',
    locationName: '深圳市南山区科技园',
    totalSlots: 10,
    powerType: 'DC',
    status: 1
  }
])

const handleAdd = () => {
  console.log('Add new charging pile')
}

const handleEdit = (row) => {
  console.log('Edit:', row)
}

const handleDelete = (row) => {
  console.log('Delete:', row)
}
</script>

<style scoped lang="scss">
.charging-list {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
