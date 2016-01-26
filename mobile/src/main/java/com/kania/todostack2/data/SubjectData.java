package com.kania.todostack2.data;

/**
 * Created by user on 2016-01-11.
 * for performance, do not use getter or setter method
 */
public class SubjectData {
    //from DB
    public int id;
    public String subjectName;
    public int color;
    public int order;

    //from real time
    public int taskCount;
    public int dateTodoCount;
    public int delayedTodoCount;

    public SubjectData() {
        taskCount = 0;
        dateTodoCount = 0;
        delayedTodoCount = 0;
    }
}
