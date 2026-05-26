# 单元测试运行指南

## 运行所有测试

```bash
# 运行所有单元测试
mvn test

# 运行指定模块的测试
cd backend/user-service
mvn test

# 运行指定类
mvn test -Dtest=SysUserServiceTest

# 运行指定方法
mvn test -Dtest=SysUserServiceTest#testGetByUsername
```

## 查看测试报告

测试报告生成在 `target/surefire-reports/` 目录

```bash
# 查看文本报告
cat target/surefire-reports/*.txt

# 查看 XML 报告 (可用于 CI/CD)
cat target/surefire-reports/*.xml
```

## 测试覆盖率

```bash
# 使用 JaCoCo 生成覆盖率报告
mvn clean test jacoco:report

# 查看覆盖率报告
open backend/user-service/target/site/jacoco/index.html
```

## 添加新测试

测试类应放在 `src/test/java/` 对应包路径下

示例：

```java
package com.charging.user.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyServiceTest {

    @Test
    void testMyFeature() {
        // Arrange
        // Act
        // Assert
        assertTrue(true);
    }
}
```

## 测试规范

1. 测试类名以 `Test` 结尾
2. 测试方法使用 `@Test` 注解
3. 使用有意义的测试方法名
4. 每个测试方法只测试一个功能点
5. 使用 AAA 模式 (Arrange-Act-Assert)
6. 避免测试之间的依赖
