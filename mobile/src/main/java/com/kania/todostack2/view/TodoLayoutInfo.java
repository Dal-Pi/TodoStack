package com.kania.todostack2.view;

/**
 * Created by user on 2016-01-20.
 */
public class TodoLayoutInfo {

    //percent of layout
    private final int PERCENT_DATEWIDTH = 10;
    private final int PERCENT_SUBJECTHEIGHT = 5;
    private final int PERCENT_SUBJECTGAPWIDTH = 10;
    //percent of each view
    private final int PERCENT_TODOGAPHEIGHT = 10;
    //pixel of line
    private final int DATEDIVIDERLINE_HEIGHT = 1;
    private final int TASKBASELINE_HEIGHT = 2;

    public int subjectCount;
    public int taskCount;
    public int dateTodoCount;
    public int delayedTodoCount;

    public int layoutWidth;
    public int layoutHeight;
    public int dateWidth; //dateHeight == todoHeight
    public int subjectHeight; //subjectWidth == todoWidth
    public int todoWidth;
    public int todoHeight;

    public int dateTodoGap;
    public int stackTodoGap;

    private TodoLayoutInfo() {
        //block default constructor
    }
    public TodoLayoutInfo(int layoutWidth, int layoutHeight,
                          int subjectCount, int taskCount, int dateTodoCount, int delayedTodoCount) {
        this.layoutWidth = layoutWidth;
        this.layoutHeight = layoutHeight;
        this.subjectCount = subjectCount;
        this.taskCount = taskCount;
        this.dateTodoCount = dateTodoCount;
        this.delayedTodoCount = delayedTodoCount;

        refreshEachViewSize(subjectCount, taskCount, dateTodoCount, delayedTodoCount);
    }

    public void refreshEachViewSize(int subjectCount, int taskCount,
                                    int dateTodoCount, int delayedTodoCount) {
        dateWidth = layoutWidth / PERCENT_DATEWIDTH;
        subjectHeight = layoutHeight / PERCENT_SUBJECTHEIGHT;
        todoWidth = (layoutWidth - dateWidth) / subjectCount;
        todoHeight = (layoutHeight - subjectCount) / (taskCount + dateTodoCount + delayedTodoCount);
        stackTodoGap = todoWidth / PERCENT_TODOGAPHEIGHT;
        dateTodoGap = stackTodoGap + DATEDIVIDERLINE_HEIGHT;
    }

    /**
     *
     * @param subjectSequence sequence of subject, first item sequence is 0
     * @return ViewPosition of subject
     */
    public ViewPosition getSubjectPosition(int subjectSequence) {
        int left = dateWidth + (todoWidth * subjectSequence);
        int top = ((todoHeight + stackTodoGap) * taskCount) + TASKBASELINE_HEIGHT +
                dateTodoGap + ((todoHeight + dateTodoGap) * dateTodoCount);
        return new ViewPosition(left, top, left + todoWidth, top + todoHeight);
    }
}
