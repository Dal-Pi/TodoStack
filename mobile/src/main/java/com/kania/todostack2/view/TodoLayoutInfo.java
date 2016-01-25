package com.kania.todostack2.view;

/**
 * Created by user on 2016-01-20.
 */
public class TodoLayoutInfo {

    //percent of layout
    public static final int PERCENT_DATEWIDTH = 10;
    public static final int PERCENT_SUBJECTHEIGHT = 5;
    public static final int PERCENT_SUBJECTGAPWIDTH = 2;
    //percent of each view
    public static final int PERCENT_TODOGAPHEIGHT = 10;
    //pixel of line
    public static final int DATEDIVIDERLINE_HEIGHT = 1;
    public static final int TASKBASELINE_HEIGHT = 2;

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

    public int subjectGap;
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

    public boolean refreshEachViewSize(int subjectCount, int taskCount,
                                    int dateTodoCount, int delayedTodoCount) {
        if (subjectCount <= 0 || (taskCount + dateTodoCount + delayedTodoCount) <= 0) {
            return false;
        }
        dateWidth = (layoutWidth * PERCENT_DATEWIDTH) / 100;
        subjectHeight = (layoutHeight * PERCENT_SUBJECTHEIGHT) / 100;
        todoWidth = (layoutWidth - dateWidth) / subjectCount;
        todoHeight = (layoutHeight - (subjectHeight * subjectCount))
                / (taskCount + dateTodoCount + delayedTodoCount);
        stackTodoGap = (todoHeight * PERCENT_TODOGAPHEIGHT) / 100;
        dateTodoGap = stackTodoGap + DATEDIVIDERLINE_HEIGHT;
        subjectGap = (layoutWidth * PERCENT_SUBJECTGAPWIDTH) / 100;

        //renew removing gap
        todoWidth -= subjectGap;
        return true;
    }

    /**
     *
     * @param subjectSequence sequence of subject, first item sequence is 0
     * @return ViewPosition of subject
     */
    public ViewPosition getSubjectPosition(int subjectSequence) {
        int left = dateWidth + (todoWidth * subjectSequence) + (subjectGap * subjectSequence);
        int top = ((todoHeight + stackTodoGap) * taskCount) + TASKBASELINE_HEIGHT +
                dateTodoGap + ((todoHeight + dateTodoGap) * dateTodoCount);
        return new ViewPosition(left, top, left + todoWidth, top + todoHeight);
    }
}
