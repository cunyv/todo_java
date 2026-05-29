# 待办事项应用使用指南

## 快速启动

### 方法一：使用启动脚本（推荐）
双击运行 `run.bat` 文件，等待应用启动完成。

### 方法二：手动启动
```bash
# 在项目目录下执行
mvn spring-boot:run
```

### 方法三：打包后运行
```bash
# 先打包
mvn clean package

# 运行JAR文件
java -jar target/todo-app-0.0.1-SNAPSHOT.jar
```

## 访问应用

启动成功后，在浏览器中访问：
- **主页面**: http://localhost:8081
- **H2数据库控制台**: http://localhost:8081/h2-console
  - JDBC URL: `jdbc:h2:mem:tododb`
  - 用户名: `sa`
  - 密码: (留空)

## 功能使用说明

### 1. 添加新任务
1. 在页面顶部的"添加新任务"区域
2. 输入任务标题（必填）
3. 输入任务描述（可选）
4. 点击"添加任务"按钮

### 2. 完成任务
- 点击任务左侧的圆圈按钮
- 圆圈变为绿色勾号表示已完成
- 再次点击可取消完成状态

### 3. 编辑任务
1. 点击任务右侧的"编辑"按钮
2. 在编辑页面修改任务信息
3. 可以勾选"已完成"复选框
4. 点击"保存修改"按钮

### 4. 删除任务
1. 点击任务右侧的"删除"按钮
2. 在确认对话框中点击"确定"

### 5. 搜索任务
1. 在搜索框中输入关键词
2. 点击"搜索"按钮
3. 系统会显示包含关键词的任务

### 6. 筛选任务
使用页面上的筛选链接：
- **全部**: 显示所有任务
- **未完成**: 只显示未完成的任务
- **已完成**: 只显示已完成的任务

## 界面说明

### 任务状态
- ○ : 未完成状态
- ✓ : 已完成状态（绿色背景）

### 统计信息
页面底部显示：
- 总任务数
- 已完成任务数
- 未完成任务数

## 技术说明

### 数据存储
- 使用H2内存数据库
- 数据存储在内存中，应用重启后数据会丢失
- 如需持久化存储，请修改 `application.properties` 配置

### 数据库配置
在 `application.properties` 中可以修改数据库配置：

```properties
# 使用文件数据库（数据持久化）
spring.datasource.url=jdbc:h2:file:./data/tododb

# 使用MySQL数据库
# spring.datasource.url=jdbc:mysql://localhost:3306/todo_db
# spring.datasource.username=root
# spring.datasource.password=password
# spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 常见问题

### Q: 应用启动失败怎么办？
A: 检查以下几点：
1. 确保Java 11或更高版本已安装
2. 确保Maven已正确安装
3. 检查8080端口是否被占用

### Q: 如何修改端口号？
A: 在 `application.properties` 中修改：
```properties
server.port=9090
```

### Q: 数据会丢失吗？
A: 默认使用H2内存数据库，重启后数据会丢失。如需持久化，请修改数据库配置。

### Q: 如何查看数据库内容？
A: 访问 http://localhost:8080/h2-console，使用以下配置：
- JDBC URL: `jdbc:h2:mem:tododb`
- 用户名: `sa`
- 密码: (留空)

## 开发说明

### 项目结构
```
src/main/java/com/example/todo/
├── TodoApplication.java      # 主应用类
├── Todo.java                 # 实体类
├── TodoRepository.java       # 数据访问层
├── TodoService.java          # 业务逻辑层
└── TodoController.java       # 控制器层
```

### 扩展建议
1. 添加用户认证
2. 实现REST API
3. 添加任务分类
4. 添加截止日期
5. 添加优先级
6. 使用持久化数据库

## 许可证
MIT License