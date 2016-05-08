package com.kania.todostack2.provider;

import android.content.Context;
import android.util.Log;

/**
 * Created by user on 2016-01-25.
 */
public class TodoStackSettingValues {
    public static int DEFAULT_VISIBLE_TASK_COUNT = 10;
    public static int DEFAULT_VISIBLE_DATE_COUNT = 21;
    public static int DEFAULT_VISIBLE_DELAYED_COUNT = 10;

    public static TodoStackSettingValues instance;

    private Context mContext;

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

    public int getVisivleTaskCount() {
        //TODO this is stub
        return DEFAULT_VISIBLE_TASK_COUNT;
    }
    public int getVisivleDateCount() {
        //TODO this is stub
        return DEFAULT_VISIBLE_DATE_COUNT;
    }
    public int getVisivleDelayedCount() {
        //TODO this is stub
        return DEFAULT_VISIBLE_DELAYED_COUNT;
    }
}
