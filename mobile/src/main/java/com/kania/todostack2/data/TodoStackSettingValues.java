package com.kania.todostack2.data;

import android.content.Context;
import android.util.Log;

/**
 * Created by user on 2016-01-25.
 */
public class TodoStackSettingValues {
    public static int DEFAULT_VISIBLE_TASK_COUNT = 7;
    public static int DEFAULT_VISIBLE_DATE_COUNT = 15;
    public static int DEFAULT_VISIBLE_DELAYED_COUNT = 5;

    public static TodoStackSettingValues instance;

    private static Context mContext;

    private TodoStackSettingValues() {

    }
    private TodoStackSettingValues(Context context) {
        mContext = context;
    }

    public static TodoStackSettingValues getInstance(Context ApplicationContext) {
        if (instance == null) {
            Log.d("TodoStack", "In case of creation TodoStackSettingValues");
            instance = new TodoStackSettingValues(ApplicationContext);
        }
        return instance;
    }

    public static int getVisivleTaskCount() {
        //TODO this is stub
        return DEFAULT_VISIBLE_TASK_COUNT;
    }
    public static int getVisivleDateCount() {
        //TODO this is stub
        return DEFAULT_VISIBLE_DATE_COUNT;
    }
    public static int getVisivleDelayedCount() {
        //TODO this is stub
        return DEFAULT_VISIBLE_DELAYED_COUNT;
    }
}
