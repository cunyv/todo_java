package com.example.todo;

public enum TaskStatus {
    TODO("待开始"),
    IN_PROGRESS("进行中"),
    DONE("已完成");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}