---
name: springboot-dev-standards
description: Spring Boot 项目技术栈与编码规范，涵盖 MyBatis-Plus、Sa-Token、Lombok、分层架构、DTO/VO 等约束
---

# Spring Boot 项目开发规范

## 技术栈

| 项目 | 选型 | 说明 |
|---|---|---|
| 语言 | Java 11+ | |
| 框架 | Spring Boot 2.7.x | |
| 模板引擎 | Thymeleaf | 服务端渲染 |
| ORM | MyBatis-Plus 3.5.x | **禁止使用 JPA / Hibernate** |
| 数据库 | MySQL 8.0 | 字符集 utf8mb4 |
| 认证 | Sa-Token | **禁止使用 Spring Security** |
| 工具库 | Lombok | 消除样板代码 |
| 构建 | Maven | |

## 分层架构

严格按以下分层，禁止跨层调用：

```
Controller  →  Facade  →  Service  →  Mapper  →  DB
                ↓
            DTO / VO 转换
```

### 各层职责与约束

| 层 | 包名 | 职责 | 约束 |
|---|---|---|---|
| **Mapper** | `mapper/` | 继承 `BaseMapper<T>`，纯数据库访问 | 无业务逻辑 |
| **Service** | `service/` + `service/impl/` | 继承 `IService<T>` / `ServiceImpl<M, T>` | **仅调用自己的 Mapper**，不调其他 Service，不含业务逻辑 |
| **Facade** | `facade/` | 业务逻辑编排，Entity ↔ DTO/VO 转换 | 可调多个 Service |
| **Controller** | `controller/` | 接收请求，返回视图 | **仅调 Facade**，不直接调 Service |
| **DTO** | `dto/` | 接收前端参数 | Controller 方法入参 |
| **VO** | `vo/` | 返回前端数据 | Controller 方法返回到模板 |
| **Entity** | `entity/` | 数据库表映射 | **禁止直接暴露给前端** |
| **枚举** | `enums/` | 枚举类型 | |
| **配置** | `config/` | Sa-Token 拦截器、异常处理等 | |

### 数据流向

```
前端 ──DTO──→ Controller ──DTO──→ Facade ──Entity──→ Service ──→ Mapper ──→ DB
                                                                        │
前端 ←──VO─── Controller ←──VO─── Facade ←──Entity── Service ←─────────┘
```

## Lombok 规范

### 实体类 / VO / DTO

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("xxx")
public class XxxEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    // ...
}
```

### Service / Facade / Controller / Component

使用 `@RequiredArgsConstructor` 替代构造器注入：

```java
@Service
@RequiredArgsConstructor
public class XxxServiceImpl extends ServiceImpl<XxxMapper, XxxEntity> implements XxxService {
    private final XxxMapper xxxMapper;
    // 禁止手写构造器，禁止 @Autowired
}
```

```java
@Component
@RequiredArgsConstructor
public class XxxFacade {
    private final XxxService xxxService;
    private final YyyService yyyService;
}
```

```java
@Controller
@RequiredArgsConstructor
public class XxxController {
    private final XxxFacade xxxFacade;
}
```

## 代码注释规范

### JavaDoc 注释

所有 public 类、接口、方法必须写 JavaDoc，说明用途、参数含义、返回值：

```java
/**
 * 待办事项业务门面，负责待办相关的业务编排与 Entity/VO 转换。
 */
@Component
@RequiredArgsConstructor
public class TodoFacade {

    /**
     * 分页查询当前用户的待办列表。
     *
     * @param userId 用户 ID
     * @param page   页码，从 1 开始
     * @param size   每页条数
     * @return 待办 VO 分页结果
     */
    public Page<TodoVO> getTodoPage(Long userId, int page, int size) {
        // ...
    }
}
```

### 关键逻辑注释

在以下位置必须写行内注释，帮助他人快速理解代码意图：

- **业务规则**：为什么这样做，而非怎么做
- **非显而易见的逻辑**：边界处理、兼容性处理、特殊分支
- **复杂算法 / 状态流转**：每一步的含义
- **外部依赖调用**：调用了什么、为什么调用

```java
public void processOrder(Long orderId) {
    Order order = orderService.getById(orderId);

    // 已完成的订单不允许重复处理
    if (order.getStatus() == OrderStatus.COMPLETED) {
        throw new BusinessException("订单已完成，不可重复处理");
    }

    // 扣减库存（需确保事务内执行，避免超卖）
    stockService.deduct(order.getSkuId(), order.getQuantity());

    // 订单状态流转：待支付 → 已支付 → 已发货
    // 跳过已取消的订单，避免误操作
    if (order.getStatus() != OrderStatus.CANCELLED) {
        order.setStatus(OrderStatus.PAID);
        orderService.updateById(order);
    }
}
```

### 禁止的注释

```java
// 错误：注释只是重复代码本身
i++; // i 加 1

// 错误：注释掉的代码残留
// oldService.doSomething();
// anotherOldCall();

// 正确：注释说明"为什么"
i++; // 跳过表头行
```

## 禁止魔法数字

代码中严禁出现含义不明的数字字面量，必须使用枚举或常量替代。

### 错误示例

```java
// 错误：魔法数字，无法直观理解含义
if (order.getStatus() == 3) { ... }
if (user.getRole() == 1) { ... }
if (score >= 90) { ... }
Thread.sleep(5000);
```

### 正确示例 — 使用枚举

```java
// 定义枚举
@Getter
@AllArgsConstructor
public enum OrderStatus {
    PENDING(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消");

    private final int code;
    private final String displayName;
}

// 使用枚举
if (order.getStatus() == OrderStatus.COMPLETED) { ... }
```

### 正确示例 — 使用常量

对于不属于业务枚举的数值（如超时时间等），使用常量：

```java
public class AppConstants {
    /** 请求超时时间（毫秒） */
    public static final long REQUEST_TIMEOUT_MS = 5000L;

    private AppConstants() {}
}
```

## MyBatis-Plus 规范

### Mapper

```java
@Mapper
public interface XxxMapper extends BaseMapper<XxxEntity> {
}
```

### Service 接口

```java
public interface XxxService extends IService<XxxEntity> {
}
```

### Service 实现

```java
@Service
public class XxxServiceImpl extends ServiceImpl<XxxMapper, XxxEntity> implements XxxService {
}
```

### 查询

根据查询复杂度选择不同方案：

#### 简单查询 — LambdaQueryWrapper

单表条件查询、排序、分页等简单场景，使用 `LambdaQueryWrapper`，禁止手写 SQL 字符串：

```java
// 正确 — 简单条件查询
xxxService.list(new LambdaQueryWrapper<XxxEntity>()
        .eq(XxxEntity::getUserId, userId)
        .orderByDesc(XxxEntity::getCreatedAt));

// 错误
xxxMapper.selectList("SELECT * FROM xxx WHERE user_id = " + userId);
```

#### 复杂查询 — XML Mapper

多表 JOIN、子查询、聚合统计、动态 SQL 等复杂场景，使用 XML 方式，**返回对象必须是 VO**（禁止返回 Entity）：

**1. Mapper 接口定义方法**

```java
@Mapper
public interface XxxMapper extends BaseMapper<XxxEntity> {
    List<XxxVO> selectXxxDetailList(@Param("query") XxxQueryDTO query);
}
```

**2. XML 映射文件**（`src/main/resources/mapper/XxxMapper.xml`）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.xxx.mapper.XxxMapper">

    <select id="selectXxxDetailList" resultType="com.example.xxx.vo.XxxVO">
        SELECT
            a.id,
            a.title,
            b.name AS categoryName,
            a.created_at
        FROM xxx a
        LEFT JOIN category b ON a.category_id = b.id
        <where>
            <if test="query.userId != null">
                AND a.user_id = #{query.userId}
            </if>
            <if test="query.status != null">
                AND a.status = #{query.status}
            </if>
        </where>
        ORDER BY a.created_at DESC
    </select>

</mapper>
```

**3. 在 Facade 层调用**

```java
@Component
@RequiredArgsConstructor
public class XxxFacade {
    private final XxxMapper xxxMapper;

    public List<XxxVO> getXxxDetailList(XxxQueryDTO query) {
        return xxxMapper.selectXxxDetailList(query);
    }
}
```

#### 选择原则

| 场景 | 方案 | 说明 |
|---|---|---|
| 单表 CRUD、简单条件 | `LambdaQueryWrapper` | 类型安全，代码简洁 |
| 多表 JOIN | XML Mapper → VO | SQL 可读性好，返回 VO |
| 聚合统计（COUNT/SUM/GROUP BY） | XML Mapper → VO | 复杂 SQL 在 XML 中更清晰 |
| 动态条件拼接（大量 `<if>`） | XML Mapper → VO | XML 动态 SQL 比 Wrapper 更易维护 |

## Sa-Token 规范

### 登录

```java
StpUtil.login(user.getId());
```

### 获取当前用户

```java
Long userId = StpUtil.getLoginIdAsLong();
```

### 退出

```java
StpUtil.logout();
```

### 拦截器配置

```java
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error");
    }
}
```

### 异常处理

使用 `HandlerExceptionResolver`（非 `@ControllerAdvice`），因为 Sa-Token 的 `NotLoginException` 从拦截器抛出，`@ControllerAdvice` 无法捕获：

```java
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object handler, Exception ex) {
        if (ex instanceof NotLoginException) {
            return new ModelAndView("redirect:/login");
        }
        // ...
    }
}
```

## DTO / VO 规范

### DTO — 接收前端参数

- 命名：`XxxCreateDTO`、`XxxUpdateDTO`、`LoginDTO`、`RegisterDTO`
- 只包含前端提交的字段
- 使用 `@Data` 注解

```java
@Data
public class TodoCreateDTO {
    private String title;
    private String description;
}
```

### VO — 返回前端数据

- 命名：`XxxVO`、`XxxStatsVO`
- 包含前端展示所需的所有字段
- 可包含派生字段（如 `statusDisplayName`）
- 使用 `@Data @NoArgsConstructor`（禁止 `@AllArgsConstructor`）

```java
@Data
@NoArgsConstructor
public class TodoVO {
    private Long id;
    private String title;
    private TaskStatus status;
    private String statusDisplayName;  // 派生字段
    private LocalDateTime createdAt;
}
```

### 转换

Entity ↔ DTO/VO 转换在 **Facade 层**完成，Controller 不做转换。禁止使用构造器转换。

根据字段差异程度选择方式：

#### 字段基本一致 — 对象拷贝

当 Entity 和 VO 字段大部分相同、仅有少量额外字段时，先用 `BeanUtils.copyProperties` 拷贝，再补充差异字段：

```java
private TodoVO toTodoVO(Todo todo) {
    if (todo == null) return null;
    // 同名字段批量拷贝
    TodoVO vo = new TodoVO();
    BeanUtils.copyProperties(todo, vo);
    // 补充派生字段
    vo.setStatusDisplayName(todo.getStatus().getDisplayName());
    return vo;
}
```

#### 字段差异较大 — setter 赋值

当 Entity 和 VO 字段差异较多、或需要大量派生计算时，逐字段 setter 赋值更清晰：

```java
private TodoVO toTodoVO(Todo todo) {
    if (todo == null) return null;
    TodoVO vo = new TodoVO();
    vo.setId(todo.getId());
    vo.setTitle(todo.getTitle());
    vo.setStatus(todo.getStatus());
    vo.setStatusDisplayName(todo.getStatus().getDisplayName());
    vo.setCreatedAt(todo.getCreatedAt());
    return vo;
}
```

#### 禁止的写法

```java
// 错误：构造器转换（字段多时顺序易错，且强制 VO 必须有 @AllArgsConstructor）
return new TodoVO(todo.getId(), todo.getTitle(), todo.getStatus(),
                  todo.getStatus().getDisplayName(), todo.getCreatedAt());
```

## 包结构模板

```
com.example.xxx
├── XxxApplication.java
├── entity/
│   └── XxxEntity.java
├── enums/
│   └── XxxEnum.java
├── mapper/
│   └── XxxMapper.java
├── service/
│   ├── XxxService.java
│   └── impl/
│       └── XxxServiceImpl.java
├── facade/
│   └── XxxFacade.java
├── dto/
│   ├── XxxCreateDTO.java
│   └── XxxUpdateDTO.java
├── vo/
│   ├── XxxVO.java
│   └── XxxStatsVO.java
├── controller/
│   └── XxxController.java
└── config/
    ├── SaTokenConfig.java
    └── GlobalExceptionHandler.java
```

## 数据库规范

- 字符集：`utf8mb4`，排序规则：`utf8mb4_unicode_ci`
- 引擎：`InnoDB`
- 主键：`BIGINT AUTO_INCREMENT`
- 表名、字段名：snake_case
- 必须有 `created_at`、`updated_at` 字段
- 初始化脚本放在 `src/main/resources/schema.sql`
