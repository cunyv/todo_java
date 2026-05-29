# Spring Boot 待办事项列表应用

一个基于Spring Boot的待办事项管理应用，使用Thymeleaf作为模板引擎，H2作为内存数据库。

## 功能特性

- ✅ 添加新的待办事项
- ✅ 编辑待办事项
- ✅ 删除待办事项
- ✅ 标记待办事项为已完成/未完成
- ✅ 搜索待办事项
- ✅ 按状态筛选（全部/未完成/已完成）
- ✅ 响应式设计，支持移动端

## 技术栈

- **后端**: Spring Boot 2.7.0
- **前端**: Thymeleaf + CSS
- **数据库**: H2 (内存数据库)
- **构建工具**: Maven
- **Java版本**: 11

## 快速开始

### 前提条件

- Java 11 或更高版本
- Maven 3.6 或更高版本

### 运行步骤

1. **克隆或下载项目**
   ```bash
   cd todo-app
   ```

2. **使用Maven运行项目**
   ```bash
   mvn spring-boot:run
   ```

3. **访问应用**
   - 打开浏览器访问: http://localhost:8081
   - H2数据库控制台: http://localhost:8081/h2-console
     - JDBC URL: `jdbc:h2:mem:tododb`
     - 用户名: `sa`
     - 密码: (留空)

### 打包为JAR文件

```bash
mvn clean package
java -jar target/todo-app-0.0.1-SNAPSHOT.jar
```

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/example/todo/
│   │       ├── TodoApplication.java      # 主应用类
│   │       ├── Todo.java                 # 实体类
│   │       ├── TodoRepository.java       # 数据访问层
│   │       ├── TodoService.java          # 业务逻辑层
│   │       └── TodoController.java       # 控制器层
│   └── resources/
│       ├── templates/                    # Thymeleaf模板
│       │   ├── index.html               # 主页面
│       │   └── edit.html                # 编辑页面
│       ├── static/css/                   # 静态资源
│       │   └── style.css                # 样式文件
│       └── application.properties        # 配置文件
└── test/                                 # 测试代码
```

## API接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/` | 显示所有待办事项 |
| POST | `/todos` | 创建新的待办事项 |
| POST | `/todos/{id}/toggle` | 切换待办事项状态 |
| POST | `/todos/{id}/delete` | 删除待办事项 |
| GET | `/todos/{id}/edit` | 显示编辑页面 |
| POST | `/todos/{id}` | 更新待办事项 |
| GET | `/completed` | 显示已完成事项 |
| GET | `/pending` | 显示未完成事项 |
| GET | `/search?keyword=` | 搜索待办事项 |

## 自定义配置

在 `application.properties` 中可以修改以下配置:

```properties
# 修改端口号
server.port=8080

# 使用文件数据库 (替换H2内存数据库)
# spring.datasource.url=jdbc:h2:file:./data/tododb
```

## 扩展建议

1. **添加用户认证**: 集成Spring Security
2. **使用持久化数据库**: 替换为MySQL或PostgreSQL
3. **添加REST API**: 创建RESTful接口供前端调用
4. **添加分类功能**: 为待办事项添加分类标签
5. **添加截止日期**: 支持设置任务截止时间
6. **添加优先级**: 支持设置任务优先级

## 许可证

MIT License