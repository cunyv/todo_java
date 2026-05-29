package com.example.todo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TodoRepository todoRepository;

    public DataInitializer(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 添加示例待办事项
        if (todoRepository.count() == 0) {
            // 待开始的任务
            todoRepository.save(new Todo("学习数据库设计", "学习关系型数据库设计原则和SQL优化", TaskStatus.TODO));
            todoRepository.save(new Todo("准备面试", "复习Java基础知识、Spring框架和设计模式", TaskStatus.TODO));
            todoRepository.save(new Todo("写技术博客", "总结学习过程中的心得体会，分享技术经验", TaskStatus.TODO));

            // 进行中的任务
            todoRepository.save(new Todo("学习Spring Boot", "完成Spring Boot基础教程，包括依赖注入、数据访问等", TaskStatus.IN_PROGRESS));
            todoRepository.save(new Todo("开发REST API", "为待办事项应用添加RESTful接口", TaskStatus.IN_PROGRESS));

            // 已完成的任务
            todoRepository.save(new Todo("搭建项目环境", "配置Maven、创建项目结构、添加依赖", TaskStatus.DONE));
            todoRepository.save(new Todo("设计数据库表", "设计todos表结构，包含id、title、description、status等字段", TaskStatus.DONE));

            System.out.println("✓ 已初始化7个示例待办事项");
        }
    }
}