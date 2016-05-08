package com.kania.todostack2.provider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.util.TodoStackUtil;
import com.kania.todostack2.view.CoverActivity;

//import from TodoStack1
public class TodoListWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_UPDATE_ACTION =
            "com.kania.todostack2.action.APPWIDGET_UPDATE";
    public TodoListWidgetProvider() {
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //whenever notify with list
        for(int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_listwidget);
            Intent intent = new Intent(context, TodoListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            rv.setRemoteAdapter(R.id.widget_list, intent);

            Intent clickIntent = new Intent(context, CoverActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.widget_list, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
//        Log.d("TodoStack", "[onReceive] action = " + action);
        if(WIDGET_UPDATE_ACTION.equalsIgnoreCase(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName widgetComponent = new ComponentName(context, TodoListWidgetProvider.class);
            int[] widgetIds = appWidgetManager.getAppWidgetIds(widgetComponent);
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.widget_list);
        }
        super.onReceive(context, intent);
    }

    public static class TodoListWidgetService extends RemoteViewsService {
        @Override
        public RemoteViewsFactory onGetViewFactory(Intent intent) {
            return new TodoListFactory(this.getApplicationContext(), intent);
        }
    }
}


class TodoListFactory implements RemoteViewsService.RemoteViewsFactory {
    Context mContext;
    int mAppWidgetId;

    TodoProvider provider;

    ArrayList<TodoData> arTodo;

    SimpleDateFormat sdf;

    public TodoListFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        provider = TodoProvider.getInstance(mContext);
        provider.initData();
        arTodo = new ArrayList<TodoData>();

        sdf = new SimpleDateFormat(TodoStackContract.TodoEntry.DATEFORMAT_DATE);
    }

    public void updateList() {
        arTodo.clear();
        Calendar calendarToday = Calendar.getInstance();
        provider.initData();
        for (TodoData td : provider.getAllTodo()) {
            Calendar targetCalendar = Calendar.getInstance();
            targetCalendar.setTime(new Date(td.date));
            int cmpDiffDays = TodoStackUtil.compareDate(targetCalendar, calendarToday);
            if (cmpDiffDays == 0 && td.type != TodoData.TODO_DB_TYPE_TASK) {
                arTodo.add(td);
            }
        }
    }

    @Override
    public int getCount() {
        return arTodo.size() > 0 ? arTodo.size() : 1;
    }

    @Override
    public long getItemId(int position) {
        return arTodo.size() > 0 ? (arTodo.size() - position) - 1 : 0;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        if (arTodo.size() > 0) {
            //for reverse
            int revPosition = (arTodo.size() - position) - 1;
            rv.setTextViewText(R.id.widget_text_todo, arTodo.get(revPosition).todoName);

            SubjectData sd = provider.getSubjectByOrder(arTodo.get(revPosition).subjectOrder);
            rv.setInt(R.id.widget_img_bg, "setColorFilter", sd.color);

            //for launch activity
            Intent intent = new Intent();
            intent.putExtra(TodoStackContract.TodoEntry._ID, arTodo.get(revPosition).id + "");
            intent.putExtra(TodoStackContract.SubjectEntry.COLOR, sd.color);
            rv.setOnClickFillInIntent(R.id.widget_item_layout, intent);
        } else {
            rv.setTextViewText(R.id.widget_text_todo, mContext.getResources().
                    getString(R.string.widget_empty));
            rv.setInt(R.id.widget_img_bg, "setColorFilter", mContext.getResources().
                    getColor(R.color.color_normal_state));
            rv.setOnClickFillInIntent(R.id.widget_item_layout, new Intent());
        }
        return rv;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onCreate() {
        updateList();
    }

    @Override
    public void onDataSetChanged() {
        updateList();
    }

    @Override
    public void onDestroy() {
    }

}