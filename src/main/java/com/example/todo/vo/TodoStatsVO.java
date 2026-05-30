package com.example.todo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoStatsVO {
    private long todoCount;
    private long inProgressCount;
    private long doneCount;
    private long totalCount;
}
