<template>
  <div class="coupon-manage">
    <el-card class="filter-card">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="模板 ID">
          <el-input v-model="queryForm.templateId" placeholder="请输入模板 ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button type="success" @click="showCreateDialog">批量生成</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="100" />
        <el-table-column prop="name" label="名称" width="200" show-overflow-tooltip />
        <el-table-column prop="couponType" label="类型" width="100">
          <template #default="{ row }">
            {{ couponTypeText[row.couponType] }}
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="discount" label="折扣" width="80">
          <template #default="{ row }">
            {{ row.discount }}折
          </template>
        </el-table-column>
        <el-table-column prop="threshold" label="门槛" width="100">
          <template #default="{ row }">
            ¥{{ row.threshold?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column label="有效期" width="180">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }} 至 {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTypeMap[row.status]">
              {{ statusTextMap[row.status] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="showDistributeDialog(row)">
              发放
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

    <el-dialog v-model="createDialogVisible" title="批量生成优惠券" width="500px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="模板 ID" required>
          <el-input
            v-model="createForm.templateId"
            type="number"
            placeholder="请输入优惠券模板 ID"
          />
        </el-form-item>
        <el-form-item label="生成数量" required>
          <el-input-number
            v-model="createForm.count"
            :min="1"
            :max="1000"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="success" @click="confirmCreate">生成</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="distributeDialogVisible" title="发放优惠券" width="500px">
      <el-form :model="distributeForm" label-width="100px">
        <el-form-item label="优惠券 ID">
          <el-input v-model="distributeForm.couponId" disabled />
        </el-form-item>
        <el-form-item label="用户 ID" required>
          <el-input
            v-model="distributeForm.userId"
            type="number"
            placeholder="请输入用户 ID"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="distributeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmDistribute">发放</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const createDialogVisible = ref(false)
const distributeDialogVisible = ref(false)

const queryForm = reactive({
  templateId: ''
})

const createForm = reactive({
  templateId: '',
  count: 100
})

const distributeForm = reactive({
  couponId: '',
  userId: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const couponTypeText = ['满减', '折扣', '无门槛']
const statusTextMap = ['未开始', '进行中', '已结束']
const statusTypeMap = ['info', 'success', 'info']

const fetchData = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/coupon/coupon/batch/list',
      method: 'GET',
      params: { templateId: queryForm.templateId }
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

const showCreateDialog = () => {
  createForm.templateId = ''
  createForm.count = 100
  createDialogVisible.value = true
}

const confirmCreate = async () => {
  if (!createForm.templateId) {
    ElMessage.warning('请输入模板 ID')
    return
  }
  
  try {
    await request({
      url: '/coupon/coupon/batch/create',
      method: 'POST',
      params: {
        templateId: createForm.templateId,
        count: createForm.count
      }
    })
    ElMessage.success('生成成功')
    createDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '操作失败')
  }
}

const showDistributeDialog = (row) => {
  distributeForm.couponId = row.id
  distributeForm.userId = ''
  distributeDialogVisible.value = true
}

const confirmDistribute = async () => {
  if (!distributeForm.userId) {
    ElMessage.warning('请输入用户 ID')
    return
  }
  
  try {
    await request({
      url: '/coupon/coupon/batch/distribute',
      method: 'POST',
      params: {
        couponId: distributeForm.couponId,
        userId: distributeForm.userId
      }
    })
    ElMessage.success('发放成功')
    distributeDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '操作失败')
  }
}

const handleView = (row) => {
  ElMessageBox.alert(
    `优惠券详情:<br/>
    ID: ${row.id}<br/>
    名称：${row.name}<br/>
    类型：${couponTypeText[row.couponType]}<br/>
    金额：¥${row.amount}<br/>
    门槛：¥${row.threshold}<br/>
    有效期：${row.startTime} 至 ${row.endTime}`,
    '优惠券详情',
    { dangerouslyUseHTMLString: true }
  )
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.substring(0, 10)
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.coupon-manage {
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
