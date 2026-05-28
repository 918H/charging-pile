# 开发流程规范

## Git 工作流

### 分支命名

```
主分支：main
开发分支：develop
功能分支：YYMMDD-feat-功能描述
修复分支：YYMMDD-fix-问题描述
紧急修复：hotfix-问题描述
```

### 提交规范

```bash
# 格式：<type>(<scope>): <subject>

# type 类型
feat:     新功能
fix:      Bug 修复
docs:     文档更新
style:    代码格式
refactor: 重构
test:     测试
chore:    构建/工具

# 示例
feat(statistics): 添加数据统计服务
fix(user): 修复用户登录验证问题
docs: 更新 README 文档
refactor(payment): 重构支付流程
```

### 提交流程

```bash
# 1. 创建功能分支
git checkout -b 260528-feat-user-manage

# 2. 开发并提交
git add .
git commit -m "feat(user): 实现用户管理功能"

# 3. 推送分支
git push -u origin 260528-feat-user-manage

# 4. 创建 Merge Request

# 5. 代码审查后合并
```

## 开发环境配置

### 1. 安装依赖

```bash
# Java 17
sdk install java 17.0.1-tem

# Maven
brew install maven

# Node.js
nvm install 18

# Docker
brew install --cask docker
```

### 2. 启动基础设施

```bash
cd deploy && ./start.sh
```

### 3. IDE 配置

**IntelliJ IDEA**:
- 安装插件: Lombok, MyBatisX, RestfulToolkit
- 编码格式：UTF-8
- 代码风格：Google Java Style

**VS Code**:
- 安装插件: Volar, ESLint, Prettier

## 后端开发规范

### 项目结构

```
service-name/
├── src/main/java/com/charging/service/
│   ├── ServiceApplication.java    # 启动类
│   ├── controller/                # 控制器
│   ├── service/                   # 服务接口
│   │   └── impl/                  # 服务实现
│   ├── mapper/                    # 数据访问层
│   └── entity/                    # 实体类
├── src/main/resources/
│   ├── application.yaml           # 应用配置
│   ├── bootstrap.yaml             # 启动配置
│   └── logback.xml                # 日志配置
└── pom.xml                        # Maven 配置
```

### 编码规范

#### Controller

```java
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    
    @GetMapping("/{id}")
    public R<User> getUser(@PathVariable Long id) {
        return R.ok(userService.findById(id));
    }
}
```

#### Service

```java
public interface UserService {
    User findById(Long id);
    List<User> findAll();
    User create(User user);
}
```

#### ServiceImpl

```java
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    
    @Override
    public User findById(Long id) {
        log.info("查询用户：{}", id);
        return userMapper.selectById(id);
    }
}
```

#### Entity

```java
@Data
@TableName("user_user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    private String phone;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

### 日志规范

```java
// 正确
log.info("查询用户：userId={}", userId);
log.error("查询失败", e);

// 错误
log.info("查询用户：" + userId);
System.out.println("debug");
```

## 前端开发规范

### 项目结构

```
src/
├── api/              # API 请求
├── assets/           # 静态资源
├── components/       # 公共组件
├── views/            # 页面组件
├── utils/            # 工具函数
└── router/           # 路由配置
```

### 组件规范

```vue
<template>
  <div class="user-list">
    <el-table :data="list">
      <el-table-column prop="name" label="姓名" />
    </el-table>
  </div>
</template>

<script>
export default {
  name: 'UserList',
  data() {
    return {
      list: []
    }
  },
  methods: {
    async load() {
      this.list = await getUserList()
    }
  },
  mounted() {
    this.load()
  }
}
</script>

<style scoped>
.user-list {
  padding: 20px;
}
</style>
```

### API 调用

```javascript
// api/user.js
import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}
```

## 数据库开发规范

### SQL 编写规范

```sql
-- 正确：使用反引号，字段名小写
SELECT `id`, `username`, `create_time`
FROM `user_user`
WHERE `status` = 'active'
ORDER BY `create_time` DESC
LIMIT 0, 10;

-- 错误：未使用反引号，大小写混乱
Select Id,UserName from User_User where Status='active';
```

### 迁移脚本命名

```
V{版本号}__{描述}.sql

示例:
V1__user.sql
V2__charging_order.sql
V4__new_features.sql
```

### 索引创建规范

```sql
-- 命名：idx_表名_字段名
CREATE INDEX `idx_user_phone` ON `user_user`(`phone`);

-- 联合索引
CREATE INDEX `idx_order_user_status` ON `charging_order`(`user_id`, `status`);
```

## 代码审查清单

### 后端

- [ ] 代码格式符合规范
- [ ] 日志输出正确
- [ ] 异常处理完善
- [ ] 参数校验完整
- [ ] SQL 无注入风险
- [ ] 敏感信息不输出
- [ ] 单元测试通过

### 前端

- [ ] 组件命名规范
- [ ] 无 console.log
- [ ] 样式作用域正确
- [ ] 响应式布局适配
- [ ] 无内存泄漏
- [ ] 用户交互友好

## 测试规范

### 单元测试

```java
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    
    @Test
    void testGetUserById() {
        User user = userService.findById(1L);
        assertNotNull(user);
    }
}
```

### API 测试

```bash
# 使用 curl 测试
curl http://localhost:8081/user/1

# 使用 Postman 测试
# 导入 API_TEST_COLLECTION.md 中的用例
```

## 部署流程

### 开发环境

```bash
cd deploy && ./start.sh
```

### 生产环境

```bash
# 1. 构建
mvn clean package -Pprod

# 2. 构建镜像
docker build -t charging-pile/user-service:1.0.0 .

# 3. 推送镜像
docker push charging-pile/user-service:1.0.0

# 4. 部署
kubectl apply -f k8s/prod/
```

## 故障处理流程

### 1. 问题定位

```bash
# 查看日志
docker-compose logs [service-name]

# 查看监控
# Grafana: http://localhost:3000

# 查看链路追踪
# SkyWalking / Zipkin
```

### 2. 问题分类

- **P0**: 核心功能不可用 - 立即处理
- **P1**: 部分功能受影响 - 2 小时内处理
- **P2**: 轻微问题 - 24 小时内处理
- **P3**: 优化建议 - 排期处理

### 3. 复盘文档

```markdown
# 事故复盘

## 时间
- 发现：2026-05-28 10:00
- 恢复：2026-05-28 11:30

## 影响
- 影响功能：用户登录
- 影响用户：约 1000 人

## 原因
[详细分析]

## 解决
[处理步骤]

## 预防
[改进措施]
```

---

**版本**: 1.0.0  
**更新**: 2026-05-28
