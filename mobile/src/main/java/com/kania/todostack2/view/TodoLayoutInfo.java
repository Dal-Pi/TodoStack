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
    public static final int BASICDIVIDERLINE_HEIGHT = 2;
    public static final int DATEDIVIDERLINE_HEIGHT = 3;
    public static final int TASKBASELINE_HEIGHT = 3;

    //reduce size
    public static final int DATETEXT_MARGIN_HEIGHT = 5;

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

    //gapWidth
    public int subjectGap;
    //gapHeight
    public int stackTodoGap;
    public int dateTodoGap;
    public int delayedTodoGap;

    //public int taskTodoTop; //it is 0
    public int dateTodoTop;
    public int subjectTop;
    public int delayedTodoTop;

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

        dateTodoTop = -1;
        subjectTop = -1;
        delayedTodoTop = -1;

        refreshEachViewSize(subjectCount, taskCount, dateTodoCount, delayedTodoCount);
    }

    public boolean refreshEachViewSize(int subjectCount, int taskCount,
                                    int dateTodoCount, int delayedTodoCount) {
        if (subjectCount <= 0 || (taskCount + dateTodoCount + delayedTodoCount) <= 0) {
            return false;
        }
        dateWidth = (layoutWidth * PERCENT_DATEWIDTH) / 100;
//        subjectHeight = (layoutHeight * PERCENT_SUBJECTHEIGHT) / 100;
        int exceptGapHeight = layoutHeight
                - ((dateTodoCount + 1) * DATEDIVIDERLINE_HEIGHT)
                - ((taskCount + delayedTodoCount) * BASICDIVIDERLINE_HEIGHT);
        todoHeight = exceptGapHeight / (taskCount + dateTodoCount + delayedTodoCount + 1); //1 is subject
        subjectHeight = todoHeight;
        stackTodoGap = BASICDIVIDERLINE_HEIGHT;
        dateTodoGap = DATEDIVIDERLINE_HEIGHT;
        delayedTodoGap = BASICDIVIDERLINE_HEIGHT;
        subjectGap = (layoutWidth * PERCENT_SUBJECTGAPWIDTH) / 100;
        todoWidth = ((layoutWidth - dateWidth) / subjectCount) - subjectGap;

        return true;
    }

    /**
     *
     * @param subjectSequence sequence of subject, first item sequence is 0
     * @return ViewPosition of subject
     */
    public ViewPosition getSubjectPosition(int subjectSequence) {
        int left = dateWidth + (todoWidth * subjectSequence) + (subjectGap * (subjectSequence + 1));
        int top;
        if (subjectTop <= 0) {
            initCommonValues();
        }
        top = subjectTop;
        return new ViewPosition(left, top, left + todoWidth, top + todoHeight);
    }

    public ViewPosition getTaskTodoPosition(int subjectSequence, int taskSequence) {
        int revSeq = taskCount - taskSequence; //reverse sequence
        int left = dateWidth + (todoWidth * subjectSequence) + (subjectGap * (subjectSequence + 1));
        int top = ((todoHeight + stackTodoGap) * revSeq);
        return new ViewPosition(left, top, left + todoWidth, top + todoHeight);
    }

    public ViewPosition getDateTodoPosition(int subjectSequence, int diffDate) {
        if (subjectTop <= 0) {
            initCommonValues();
        }
        int left = dateWidth + (todoWidth * subjectSequence) + (subjectGap * (subjectSequence + 1));
        int top = subjectTop - ((todoHeight + dateTodoGap) * (diffDate + 1));
        return new ViewPosition(left, top, left + todoWidth, top + todoHeight);
    }

    public ViewPosition getDelayedTodoPosition(int subjectSequence, int delayedTodoSequence) {
        int left = dateWidth + (todoWidth * subjectSequence) + (subjectGap * (subjectSequence + 1));
        int top = delayedTodoTop + ((todoHeight + delayedTodoGap) * (delayedTodoSequence - 1));
        return new ViewPosition(left, top, left + todoWidth, top + todoHeight);
    }

    public ViewPosition getDateTextPosition(int diffDate) {
        if (subjectTop <= 0) {
            initCommonValues();
        }
        int left = 0;
        int top = subjectTop - ((todoHeight + dateTodoGap) * (diffDate + 1));
        return new ViewPosition(left, top + DATETEXT_MARGIN_HEIGHT,
                left + dateWidth, top + todoHeight - DATETEXT_MARGIN_HEIGHT);
    }

    public void initCommonValues() {
        dateTodoTop = ((todoHeight + stackTodoGap) * taskCount) + TASKBASELINE_HEIGHT;
        subjectTop = dateTodoTop + ((todoHeight + dateTodoGap) * dateTodoCount);
        delayedTodoTop = subjectTop + subjectHeight + delayedTodoGap;
    }
}
