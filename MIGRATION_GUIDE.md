# 代码迁移指南：从 Result 到 R

## 快速迁移（推荐）

### 方式 1：批量替换（IDE）

**IntelliJ IDEA**:
1. `Ctrl + Shift + R` (全局替换)
2. Find: `import com.charging.XXX.common.Result;`
3. Replace: `import com.charging.common.core.response.R;`
4. 全部替换

**VS Code**:
1. `Ctrl + Shift + H` (全局替换)
2. 开启正则模式
3. Find: `import com\.charging\.\w+\.common\.Result;`
4. Replace: `import com.charging.common.core.response.R;`
5. 全部替换

### 方式 2：IDE 自动导入

1. 删除所有 `import com.charging.XXX.common.Result;`
2. 在任意使用 `Result` 的地方按 `Alt + Enter` (IDEA) 或 `Ctrl + .` (VS Code)
3. 选择 `Import R from com.charging.common.core.response`

---

## 方法名映射

| 旧方法 | 新方法 | 说明 |
|--------|--------|------|
| `Result.success()` | `R.ok()` | ✅ 无参数 |
| `Result.success(data)` | `R.ok(data)` | ✅ 有数据 |
| `Result.error(msg)` | `R.fail(msg)` | ⚠️ 方法名变更 |
| `Result.error(code, msg)` | `R.fail(code, msg)` | ⚠️ 方法名变更 |
| `Result.error(code, msg, data)` | `R.fail(code, msg, data)` | ⚠️ 方法名变更 |

---

## 迁移步骤

### 步骤 1：备份代码（重要！）

```bash
cd /workspace
git add -A
git commit -m "backup: 迁移前代码备份"
git branch backup-before-migration
```

### 步骤 2：替换导入语句

**文件位置** (共 6 个目录):
```
backend/user-service/src/main/java/com/charging/user/
backend/charging-service/src/main/java/com/charging/charging/
backend/coupon-service/src/main/java/com/charging/coupon/
backend/payment-service/src/main/java/com/charging/payment/
backend/order-service/src/main/java/com/charging/order/
backend/common-service/src/main/java/com/charging/common/
```

**替换规则**:
```java
// 删除旧导入
- import com.charging.user.common.Result;
- import com.charging.charging.common.Result;
- import com.charging.coupon.common.Result;
- import com.charging.payment.common.Result;
- import com.charging.order.common.Result;
- import com.charging.common.common.Result;

// 添加新导入
+ import com.charging.common.core.response.R;
```

### 步骤 3：替换方法调用

**全局搜索替换**:
```
Result.success(  →  R.ok(
Result.error(    →  R.fail(
```

### 步骤 4：删除重复文件

```bash
cd /workspace/backend
rm user-service/src/main/java/com/charging/user/common/Result.java
rm charging-service/src/main/java/com/charging/charging/common/Result.java
rm coupon-service/src/main/java/com/charging/coupon/common/Result.java
rm payment-service/src/main/java/com/charging/payment/common/Result.java
rm order-service/src/main/java/com/charging/order/common/Result.java
rm common-service/src/main/java/com/charging/common/common/Result.java
```

### 步骤 5：删除重复的异常处理器

```bash
rm user-service/src/main/java/com/charging/user/config/GlobalExceptionHandler.java
rm order-service/src/main/java/com/charging/order/config/GlobalExceptionHandler.java
rm charging-service/src/main/java/com/charging/charging/config/GlobalExceptionHandler.java
rm coupon-service/src/main/java/com/charging/coupon/config/GlobalExceptionHandler.java
rm payment-service/src/main/java/com/charging/payment/config/GlobalExceptionHandler.java
rm common-service/src/main/java/com/charging/common/config/GlobalExceptionHandler.java
```

### 步骤 6：编译验证

```bash
cd /workspace/backend
mvn clean compile -DskipTests
```

---

## 迁移示例

### 示例 1：Controller 迁移

**迁移前**:
```java
package com.charging.user.controller;

import com.charging.user.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        UserVO user = userService.getUser();
        return Result.success(user);
    }
    
    @PostMapping("/update")
    public Result<Void> updateProfile(@RequestBody UserDTO user) {
        if (user == null) {
            return Result.error("用户信息不能为空");
        }
        userService.update(user);
        return Result.success();
    }
}
```

**迁移后**:
```java
package com.charging.user.controller;

import com.charging.common.core.response.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @GetMapping("/profile")
    public R<UserVO> getProfile() {
        UserVO user = userService.getUser();
        return R.ok(user);
    }
    
    @PostMapping("/update")
    public R<Void> updateProfile(@RequestBody UserDTO user) {
        if (user == null) {
            return R.fail("用户信息不能为空");
        }
        userService.update(user);
        return R.ok();
    }
}
```

### 示例 2：Service 迁移

**迁移前**:
```java
public Result<Void> recharge(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        return Result.error("充值金额必须大于 0");
    }
    // ... 充值逻辑
    return Result.success();
}
```

**迁移后**:
```java
public R<Void> recharge(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        return R.fail("充值金额必须大于 0");
    }
    // ... 充值逻辑
    return R.ok();
}
```

---

## 验证清单

迁移完成后验证：

- [ ] 导入：所有文件使用 `import com.charging.common.core.response.R;`
- [ ] 方法：所有 `Result.success()` 改为 `R.ok()`
- [ ] 方法：所有 `Result.error()` 改为 `R.fail()`
- [ ] 删除：6 个重复的 `Result.java` 文件已删除
- [ ] 删除：6 个重复的 `GlobalExceptionHandler.java` 已删除
- [ ] 编译：`mvn clean compile` 无错误
- [ ] 测试：单元测试通过
- [ ] 运行：服务正常启动

---

## 常见问题

### Q1: 编译报错 "cannot find symbol: class Result"

**原因**: 还有文件未替换导入

**解决**: 
```bash
grep -r "import.*\.common\.Result" backend/
```
查找未迁移的文件并手动替换

### Q2: 运行时返回 500 错误

**原因**: 可能还有代码使用了旧的 Result 类

**解决**: 
```bash
grep -r "Result\.success\|Result\.error" backend/
```
查找并替换

### Q3: 异常处理不生效

**原因**: 各服务的 `GlobalExceptionHandler` 未删除

**解决**: 删除所有服务中的异常处理器，common-core 中的会自动生效

---

## 回退方案

如需回退到迁移前：

```bash
cd /workspace
git checkout backup-before-migration
```

---

## 自动化脚本（可选）

对于高级用户，可以使用脚本批量替换：

```bash
#!/bin/bash

cd /workspace/backend

# 替换导入
find . -name "*.java" -type f -exec sed -i 's/import com\.charging\.\w\+\.common\.Result;/import com.charging.common.core.response.R;/g' {} \;

# 替换方法调用
find . -name "*.java" -type f -exec sed -i 's/Result\.success(/R.ok(/g' {} \;
find . -name "*.java" -type f -exec sed -i 's/Result\.error(/R.fail(/g' {} \;

# 删除重复文件
rm -f user-service/src/main/java/com/charging/user/common/Result.java
rm -f charging-service/src/main/java/com/charging/charging/common/Result.java
rm -f coupon-service/src/main/java/com/charging/coupon/common/Result.java
rm -f payment-service/src/main/java/com/charging/payment/common/Result.java
rm -f order-service/src/main/java/com/charging/order/common/Result.java
rm -f common-service/src/main/java/com/charging/common/common/Result.java

echo "迁移完成！请运行 mvn clean compile 验证"
```

**注意**: 使用脚本前务必备份代码！

