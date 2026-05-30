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

使用 `LambdaQueryWrapper`，禁止手写 SQL 字符串：

```java
// 正确
xxxService.list(new LambdaQueryWrapper<XxxEntity>()
        .eq(XxxEntity::getUserId, userId)
        .orderByDesc(XxxEntity::getCreatedAt));

// 错误
xxxMapper.selectList("SELECT * FROM xxx WHERE user_id = " + userId);
```

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
- 使用 `@Data @NoArgsConstructor @AllArgsConstructor`

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoVO {
    private Long id;
    private String title;
    private TaskStatus status;
    private String statusDisplayName;  // 派生字段
    private LocalDateTime createdAt;
}
```

### 转换

Entity ↔ DTO/VO 转换在 **Facade 层**完成，Controller 不做转换：

```java
// Facade 中
private TodoVO toTodoVO(Todo todo) {
    if (todo == null) return null;
    return new TodoVO(todo.getId(), todo.getTitle(), todo.getStatus(),
                      todo.getStatus().getDisplayName(), todo.getCreatedAt());
}
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
