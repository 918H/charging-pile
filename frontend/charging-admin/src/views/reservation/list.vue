<template>
  <div class="reservation-page">
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="用户 ID">
          <el-input v-model="queryForm.userId" placeholder="请输入用户 ID" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="待使用" :value="0" />
            <el-option label="已取消" :value="1" />
            <el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="reservationId" label="预约 ID" width="100" />
        <el-table-column prop="userId" label="用户 ID" width="100" />
        <el-table-column prop="pileId" label="充电桩 ID" width="100" />
        <el-table-column prop="slotId" label="槽位 ID" width="80" />
        <el-table-column label="预约时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.reservationTime) }}
          </template>
        </el-table-column>
        <el-table-column label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="durationMinutes" label="时长 (分钟)" width="100" />
        <el-table-column prop="estimatedFee" label="预估费用" width="100">
          <template #default="{ row }">
            ¥{{ row.estimatedFee?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status]">
              {{ statusTextMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0"
              type="danger" 
              size="small"
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button type="primary" size="small" @click="handleView(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="预约 ID">{{ currentData?.reservationId }}</el-descriptions-item>
        <el-descriptions-item label="用户 ID">{{ currentData?.userId }}</el-descriptions-item>
        <el-descriptions-item label="充电桩 ID">{{ currentData?.pileId }}</el-descriptions-item>
        <el-descriptions-item label="槽位 ID">{{ currentData?.slotId }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">
          {{ formatDateTime(currentData?.reservationTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="使用时间段">
          {{ formatDateTime(currentData?.startTime) }} 至 {{ formatDateTime(currentData?.endTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="预约时长">{{ currentData?.durationMinutes }} 分钟</el-descriptions-item>
        <el-descriptions-item label="预估费用">¥{{ currentData?.estimatedFee?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTypeMap[currentData?.status]">
            {{ statusTextMap[currentData?.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item 
          v-if="currentData?.status === 1" 
          label="取消原因"
        >
          {{ currentData?.cancelReason }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('预约详情')
const currentData = ref(null)

const queryForm = reactive({
  userId: null,
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const statusTextMap = ['待使用', '已取消', '已完成']
const statusTypeMap = ['warning', 'info', 'success']

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/order/reservation/user/list',
      method: 'GET',
      params: {
        userId: queryForm.userId,
        status: queryForm.status
      }
    })
    tableData.value = res.data || []
    pagination.total = tableData.value.length
  } catch (error) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handlePageChange = () => {
  fetchData()
}

const handleView = (row) => {
  currentData.value = row
  dialogTitle.value = '预约详情'
  dialogVisible.value = true
}

const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/order/reservation/${row.reservationId}/cancel`,
        method: 'POST',
        params: { reason: '管理员取消' }
      })
      ElMessage.success('取消成功')
      fetchData()
    } catch (error) {
      ElMessage.error('取消失败')
    }
  })
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.reservation-page {
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
