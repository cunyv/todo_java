package com.example.todo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.todo.entity.Todo;
import com.example.todo.entity.User;
import com.example.todo.enums.TaskStatus;
import com.example.todo.service.TodoService;
import com.example.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final TodoService todoService;

    @Override
    public void run(String... args) throws Exception {
        Long count = userService.count(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "admin")
        );

        if (count == 0) {
            User admin = new User("admin", "123456");
            userService.save(admin);
            Long userId = admin.getId();
            System.out.println("✓ 已创建默认用户: admin / 123456");

            todoService.save(new Todo("学习数据库设计", "学习关系型数据库设计原则和SQL优化", TaskStatus.TODO, userId));
            todoService.save(new Todo("准备面试", "复习Java基础知识、Spring框架和设计模式", TaskStatus.TODO, userId));
            todoService.save(new Todo("写技术博客", "总结学习过程中的心得体会，分享技术经验", TaskStatus.TODO, userId));
            todoService.save(new Todo("学习Spring Boot", "完成Spring Boot基础教程，包括依赖注入、数据访问等", TaskStatus.IN_PROGRESS, userId));
            todoService.save(new Todo("开发REST API", "为待办事项应用添加RESTful接口", TaskStatus.IN_PROGRESS, userId));
            todoService.save(new Todo("搭建项目环境", "配置Maven、创建项目结构、添加依赖", TaskStatus.DONE, userId));
            todoService.save(new Todo("设计数据库表", "设计todos表结构，包含id、title、description、status等字段", TaskStatus.DONE, userId));

            System.out.println("✓ 已为admin用户初始化7个示例待办事项");
        }
    }
}
