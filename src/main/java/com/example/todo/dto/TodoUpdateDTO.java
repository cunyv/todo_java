package com.example.todo.dto;

import lombok.Data;

@Data
public class TodoUpdateDTO {
    private String title;
    private String description;
    private String status;
}
