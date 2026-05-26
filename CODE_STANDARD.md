# 充电桩微服务平台 - 代码规范

## 一、Java 代码规范

### 1. 命名规范

#### 类命名
```java
// ✅ 正确
public class ChargingOrderService {}
public class PaymentRefundController {}
public class OrderCalculateRequest {}

// ❌ 错误
public class chargingOrderService {}
public class Payment_refund {}
public class req {}
```

#### 方法命名
```java
// ✅ 正确
public ChargingOrder getById(Long id);
public void createOrder(OrderRequest request);
public boolean canReview(Long userId, Long orderId);

// ❌ 错误
public ChargingOrder get(Long id);
public void order(OrderRequest request);
public boolean check(Long id1, Long id2);
```

#### 变量命名
```java
// ✅ 正确
private Long userId;
private BigDecimal totalAmount;
private List<ChargingOrder> orders;

// ❌ 错误
private Long uid;
private BigDecimal money;
private List list;
```

#### 常量命名
```java
// ✅ 正确
private static final Integer STATUS_PENDING = 0;
private static final BigDecimal DEFAULT_SERVICE_PRICE = new BigDecimal("0.5");

// ❌ 错误
private static final int status = 0;
private static final BigDecimal price = new BigDecimal("0.5");
```

### 2. 代码格式

#### 类结构顺序
```java
public class ClassName {
    // 1. 静态常量
    private static final int CONSTANT = 1;
    
    // 2. 静态变量
    private static Logger log;
    
    // 3. 实例变量
    private Long id;
    
    // 4. 构造函数
    public ClassName() {}
    
    // 5. 公有方法
    public void publicMethod() {}
    
    // 6. 受保护方法
    protected void protectedMethod() {}
    
    // 7. 私有方法
    private void privateMethod() {}
}
```

#### 空行规则
```java
// ✅ 正确
public class Demo {
    private static final int A = 1;
    
    private Long id;
    
    public Demo() {}
    
    public void method1() {
        // 逻辑代码
    }
    
    public void method2() {
        // 逻辑代码
    }
}

// ❌ 错误：过多空行或没有空行
```

#### 行宽限制
- 单行代码不超过 120 字符
- 超过 120 字符需换行

```java
// ✅ 正确
List<ChargingOrder> orders = chargingOrderMapper.selectList(
    new LambdaQueryWrapper<ChargingOrder>()
        .eq(ChargingOrder::getUserId, userId)
        .orderByDesc(ChargingOrder::getCreatedAt)
);
```

### 3. 注释规范

#### 类注释
```java
/**
 * 充电订单服务实现类
 * 
 * @author MonkeyCode
 * @since 2026-01-26
 */
@Service
public class ChargingOrderServiceImpl implements ChargingOrderService {
}
```

#### 方法注释
```java
/**
 * 创建充电订单
 * 
 * @param request 订单创建请求
 * @return 订单 ID
 * @throws BusinessException 当库存不足时抛出
 */
public Long createOrder(OrderRequest request) {
}
```

#### 避免无意义注释
```java
// ✅ 正确：解释为什么
if (occupationMinutes <= 30) {
    // 30 分钟内免费停放
    return BigDecimal.ZERO;
}

// ❌ 错误：重复代码
i++; // i 加 1
```

### 4. 异常处理

#### 使用自定义异常
```java
// ✅ 正确
if (order == null) {
    throw new BusinessException("订单不存在");
}

// ❌ 错误
if (order == null) {
    throw new RuntimeException("error");
}
```

#### 避免吞掉异常
```java
// ✅ 正确
try {
    paymentService.pay(order);
} catch (PaymentException e) {
    log.error("支付失败，订单号：{}", orderNumber, e);
    throw new BusinessException("支付失败");
}

// ❌ 错误
try {
    paymentService.pay(order);
} catch (Exception e) {
    // 什么都不做
}
```

### 5. 事务管理

```java
// ✅ 正确：在 Service 层添加事务
@Transactional(rollbackFor = Exception.class)
public void createOrder(OrderRequest request) {
    // 业务逻辑
}

// ❌ 错误：在 Controller 层添加事务
@PostMapping("/create")
@Transactional
public Result create(@RequestBody OrderRequest request) {
}
```

---

## 二、前端代码规范

### 1. 小程序规范

#### 目录结构
```
pages/
  order/
    order.js      // 逻辑
    order.json    // 配置
    order.wxml    // 结构
    order.wxss    // 样式
```

#### WXML 规范
```xml
<!-- ✅ 正确 -->
<view class="order-item" wx:for="{{orders}}" wx:key="orderId">
  <text class="order-no">{{item.orderNumber}}</text>
</view>

<!-- ❌ 错误：缺少 wx:key -->
<view wx:for="{{orders}}">
  <text>{{item.orderNumber}}</text>
</view>
```

#### JS 规范
```javascript
// ✅ 正确：使用 async/await
Page({
  async loadOrders() {
    try {
      const result = await orderApi.getOrderList();
      this.setData({ orders: result.records });
    } catch (err) {
      console.error('加载订单失败', err);
    }
  },
  
  // 使用箭头函数
  onCancel(e) {
    const orderId = e.currentTarget.dataset.id;
    this.doCancel(orderId);
  },
  
  // 抽取长方法
  formatDateTime(datetime) {
    if (!datetime) return '-';
    const date = new Date(datetime);
    return `${date.getMonth() + 1}-${date.getDate()}`;
  }
});

// ❌ 错误
Page({
  loadOrders() {
    orderApi.getOrderList().then(res => {
      this.setData({ orders: res.records });
    }).catch(err => {
      console.error(err);
    });
  }
});
```

#### 样式规范
```css
/* ✅ 正确：使用 rpx 单位，BEM 命名 */
.order-item {
  background: #FFFFFF;
  border-radius: 12rpx;
  padding: 24rpx;
}

.order-item__header {
  display: flex;
  justify-content: space-between;
}

.order-item--active {
  border-color: #4DABF7;
}

/* ❌ 错误：嵌套过深 */
.container .content .list .item .header .title {
  color: #333;
}
```

### 2. Vue 规范

#### 组件结构
```vue
<template>
  <div class="refund-manage">
    <!-- 结构 -->
  </div>
</template>

<script setup>
// 1. 导入
import { ref, onMounted } from 'vue'

// 2. 响应式数据
const loading = ref(false)
const tableData = ref([])

// 3. 方法
const fetchData = async () => {}

// 4. 生命周期
onMounted(() => {
  fetchData()
})
</script>

<style scoped>
// 样式
</style>
```

#### 命名规范
```javascript
// ✅ 正确
const queryForm = reactive({})
const handleSearch = () => {}
const confirmDelete = async () => {}

// ❌ 错误
const form = reactive({})
const search = () => {}
const del = () => {}
```

---

## 三、数据库规范

### 1. 表命名
```sql
-- ✅ 正确
charging_order          -- 业务前缀 + 功能
payment_refund          -- 业务前缀 + 功能
sys_user                -- 系统表前缀

-- ❌ 错误
order                   -- 缺少业务前缀
t_order                 -- 无意义前缀
orders                  -- 复数
```

### 2. 字段命名
```sql
-- ✅ 正确
user_id                 -- 下划线分隔
order_number            -- 有意义命名
created_at              -- 标准时间字段
is_deleted              -- 布尔值

-- ❌ 错误
userId                  -- 驼峰命名
no                      -- 无意义
createTime              -- 风格不统一
deleted                 -- 含义不明确
```

### 3. 索引规范
```sql
-- ✅ 正确
INDEX idx_user_id (user_id)                    -- 普通索引
UNIQUE KEY uk_order_number (order_number)      -- 唯一索引
INDEX idx_status_created (status, created_at)  -- 复合索引

-- 索引命名规则
-- idx_{字段名}      普通索引
-- uk_{字段名}       唯一索引
-- idx_{字段 1}_{字段 2} 复合索引
```

### 4. SQL 编写
```sql
-- ✅ 正确
SELECT 
    order_id,
    user_id,
    total_amount,
    status
FROM charging_order
WHERE user_id = #{userId}
  AND status = 1
ORDER BY created_at DESC
LIMIT #{offset}, #{limit};

-- ❌ 错误
SELECT * FROM charging_order WHERE user_id = 1;
```

---

## 四、Git 提交规范

### Commit Message 格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式
- `refactor`: 重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具

### 示例
```bash
# ✅ 正确
git commit -m "feat(order): 添加订单取消功能"
git commit -m "fix(payment): 修复退款金额计算错误"
git commit -m "docs: 更新 API 文档"
git commit -m "style(ui): 优化按钮样式"

# ❌ 错误
git commit -m "update"
git commit -m "fix bug"
git commit -m "123"
```

### 分支命名
```bash
# 功能分支
260126-feat-charging-scan-flow
260126-feat-payment-refund

# Bug 修复
260126-fix-order-calculation

# 紧急修复
hotfix/payment-gateway-issue
```

---

## 五、API 接口规范

### RESTful 风格
```
GET    /order              # 获取订单列表
GET    /order/{id}         # 获取订单详情
POST   /order              # 创建订单
PUT    /order/{id}         # 更新订单
DELETE /order/{id}         # 删除订单
POST   /order/{id}/cancel  # 执行操作
```

### 响应格式
```json
{
  "code": 200,
  "data": {},
  "msg": "success"
}
```

### 错误码规范
```
200 - 成功
400 - 参数错误
401 - 未授权
403 - 禁止访问
404 - 不存在
500 - 服务器错误
```

---

## 六、安全检查清单

### 代码安全
- [ ] SQL 注入防护 (使用预编译)
- [ ] XSS 防护 (输入过滤)
- [ ] CSRF 防护 (Token 验证)
- [ ] 敏感信息加密 (密码/密钥)
- [ ] 日志脱敏 (不输出密码/手机号)

### 接口安全
- [ ] 身份验证 (JWT Token)
- [ ] 权限校验
- [ ] 参数校验
- [ ] 频率限制
- [ ] 数据权限

---

## 七、性能优化

### 数据库优化
- [ ] 查询字段明确，不用 SELECT *
- [ ] 使用索引，避免全表扫描
- [ ] 分页查询，避免大数据量
- [ ] 批量操作，减少交互次数

### 缓存使用
- [ ] 热点数据缓存
- [ ] 缓存过期时间
- [ ] 缓存穿透处理
- [ ] 缓存更新策略

### 前端优化
- [ ] 图片压缩
- [ ] 按需加载
- [ ] 防抖节流
- [ ] 组件缓存

---

**版本**: v1.3.0  
**更新**: 2026-01-26
