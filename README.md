# 对账服务项目

## 项目概述

本项目是一个基于Spring Boot和MyBatis开发的对账服务，提供灵活的数据查询和对账功能。该服务支持查询任意表、条件查询（List层为OR，Map内为AND）以及分页查询，为业务系统提供强大的数据核对能力。

## 技术栈

- **后端框架**: Spring Boot 2.7.15
- **ORM框架**: MyBatis 2.3.1
- **数据库**: MySQL（生产环境）、H2（测试环境）
- **构建工具**: Maven
- **Java版本**: 1.8

## 核心功能

### 1. 动态表查询
- 支持查询任意数据表的指定字段
- 提供参数验证和SQL注入防护

### 2. 条件查询
- 灵活的条件组合：List中的条件组为OR关系，Map中的条件为AND关系
- 支持等值查询、IN条件查询

### 3. 分页查询
- 提供高效的分页查询功能
- 支持获取总记录数

### 4. 单元测试
- 完善的测试用例，确保功能正确性
- 使用H2内存数据库进行测试

## 项目结构

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── DynamicQueryServiceApplication.java  # 应用主类
│   │   ├── mapper/                              # MyBatis映射接口
│   │   └── service/                             # 业务逻辑层
│   │       └── impl/
│   └── resources/
│       ├── application.properties               # 应用配置
│       └── mapper/                              # MyBatis XML映射文件
└── test/
    ├── java/com/example/
    │   └── service/                            # 单元测试
    └── resources/
        └── application-test.properties         # 测试配置
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+

### 安装与运行

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd syn
   ```

2. **配置数据库**
   修改 `src/main/resources/application.properties` 文件，配置数据库连接信息。

3. **构建项目**
   ```bash
   mvn clean package
   ```

4. **运行项目**
   ```bash
   java -jar target/syn-0.0.1-SNAPSHOT.jar
   ```

### 运行测试

```bash
mvn test
```

## 使用示例

### 1. 查询任意表
```java
List<String> columns = Arrays.asList("id", "name", "age");
List<Map<String, Object>> results = dynamicQueryService.queryTable("test_user", columns);
```

### 2. 条件查询
```java
List<String> columns = Arrays.asList("id", "name", "age");
List<Map<String, Object>> conditions = new ArrayList<>();

Map<String, Object> condition1 = new HashMap<>();
condition1.put("id", 1);
condition1.put("age", 25);

Map<String, Object> condition2 = new HashMap<>();
condition2.put("id", 3);
condition2.put("age", 35);

conditions.add(condition1);
conditions.add(condition2);

// (id=1 AND age=25) OR (id=3 AND age=35)
List<Map<String, Object>> results = dynamicQueryService.queryWithConditions("test_user", columns, conditions);
```

### 3. 分页查询
```java
List<String> columns = Arrays.asList("id", "name", "age");
// 从第0条开始，查询2条记录
List<Map<String, Object>> results = dynamicQueryService.queryWithPagination("test_user", columns, 0, 2);

// 获取总记录数
int totalCount = dynamicQueryService.getTotalCount("test_user");
```

## 注意事项

1. 本服务提供了动态查询功能，请确保在生产环境中对表名和列名进行严格验证
2. 建议使用事务管理来确保数据一致性
3. 对于大数据量查询，请合理使用分页功能避免内存溢出

## 许可证

[MIT](LICENSE)
