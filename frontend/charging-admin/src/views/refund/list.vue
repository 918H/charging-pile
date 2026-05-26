<template>
  <div class="refund-manage">
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="退款单号">
          <el-input v-model="queryForm.refundNumber" placeholder="请输入退款单号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已打款" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="refundNumber" label="退款单号" width="180" />
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column prop="userId" label="用户 ID" width="100" />
        <el-table-column prop="refundAmount" label="退款金额" width="100">
          <template #default="{ row }">
            ¥{{ row.refundAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="refundType" label="退款类型" width="100">
          <template #default="{ row }">
            {{ refundTypeText[row.refundType] }}
          </template>
        </el-table-column>
        <el-table-column prop="refundReason" label="退款原因" width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status]">
              {{ statusTextMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button 
              v-if="row.status === 0"
              type="success" 
              size="small"
              @click="handleApprove(row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 0"
              type="danger" 
              size="small"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status === 1"
              type="primary" 
              size="small"
              @click="handleProcess(row)"
            >
              打款
            </el-button>
            <el-button type="info" size="small" @click="handleView(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="退款单号">{{ currentData?.refundNumber }}</el-descriptions-item>
        <el-descriptions-item label="订单号">{{ currentData?.orderNumber }}</el-descriptions-item>
        <el-descriptions-item label="用户 ID">{{ currentData?.userId }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">¥{{ currentData?.refundAmount?.toFixed(2) }}</el-descriptions-item>
        <el-descriptions-item label="退款类型">{{ refundTypeText[currentData?.refundType] }}</el-descriptions-item>
        <el-descriptions-item label="退款原因">{{ currentData?.refundReason }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTypeMap[currentData?.status]">
            {{ statusTextMap[currentData?.status] }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item 
          v-if="currentData?.status === 2" 
          label="拒绝原因"
        >
          {{ currentData?.rejectReason }}
        </el-descriptions-item>
        <el-descriptions-item 
          v-if="currentData?.status >= 1" 
          label="审核人"
        >
          {{ currentData?.auditorId }}
        </el-descriptions-item>
        <el-descriptions-item 
          v-if="currentData?.status >= 1" 
          label="审核时间"
        >
          {{ formatDateTime(currentData?.auditedAt) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectDialogVisible" title="拒绝退款" width="500px">
      <el-input
        v-model="rejectReason"
        type="textarea"
        :rows="4"
        placeholder="请输入拒绝原因"
        maxlength="200"
        show-word-limit
      />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="processDialogVisible" title="执行打款" width="500px">
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="交易 ID" required>
          <el-input
            v-model="processForm.transactionId"
            placeholder="请输入支付平台交易 ID"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmProcess">确认打款</el-button>
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
const rejectDialogVisible = ref(false)
const processDialogVisible = ref(false)
const dialogTitle = ref('退款详情')
const currentData = ref(null)
const rejectReason = ref('')
const currentRefundId = ref(null)

const queryForm = reactive({
  refundNumber: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const refundTypeText = ['其他', '充错电桩', '充电失败', '计费异常', '重复支付', '其他原因']
const statusTextMap = ['待审核', '已通过', '已拒绝', '已打款']
const statusTypeMap = ['warning', 'success', 'danger', 'success']

const processForm = reactive({
  transactionId: ''
})

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      status: queryForm.status
    }
    
    let url = '/payment/payment/refund/user/list'
    if (!queryForm.status) {
      url = '/payment/payment/refund/pending'
    }
    
    const res = await request({
      url: url,
      method: 'GET',
      params
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
  dialogTitle.value = '退款详情'
  dialogVisible.value = true
}

const handleApprove = (row) => {
  ElMessageBox.confirm('确定要通过该退款申请吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await request({
        url: `/payment/payment/refund/${row.refundId}/approve`,
        method: 'POST',
        params: { auditorId: 1 }
      })
      ElMessage.success('审核通过')
      fetchData()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

const handleReject = (row) => {
  currentRefundId.value = row.refundId
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写拒绝原因')
    return
  }
  
  try {
    await request({
      url: `/payment/payment/refund/${currentRefundId.value}/reject`,
      method: 'POST',
      params: { 
        auditorId: 1,
        reason: rejectReason.value.trim()
      }
    })
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleProcess = (row) => {
  currentRefundId.value = row.refundId
  processForm.transactionId = ''
  processDialogVisible.value = true
}

const confirmProcess = async () => {
  if (!processForm.transactionId.trim()) {
    ElMessage.warning('请填写交易 ID')
    return
  }
  
  try {
    await request({
      url: `/payment/payment/refund/${currentRefundId.value}/process`,
      method: 'POST',
      params: { 
        transactionId: processForm.transactionId.trim()
      }
    })
    ElMessage.success('打款成功')
    processDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
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
.refund-manage {
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
