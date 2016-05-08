package com.kania.todostack2.view;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.presenter.DetailTodoListPresenter;
import com.kania.todostack2.presenter.SubjectColorSelectDialog;
import com.kania.todostack2.presenter.TodoStackAdapter;
import com.kania.todostack2.presenter.UpdateSubjectTask;
import com.kania.todostack2.presenter.UpdateTodoTask;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.util.SubjectDeleteDialog;
import com.kania.todostack2.util.SubjectNameUpdateDialog;
import com.kania.todostack2.util.SubjectSelectDialog;
import com.kania.todostack2.util.TodoDatePickerDialog;
import com.kania.todostack2.util.TodoDoneDialog;
import com.kania.todostack2.util.TodoSelectDialog;
import com.kania.todostack2.util.TodoStackUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends AppCompatActivity implements ITodoLayoutMediator,
        View.OnClickListener, View.OnLongClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private final int DURATION_ANIMATION = 300;

    private final int NAV_MENU_ITEM_ID_ALL = -1;

//    private IControllerMediator mMediator;

    private Toolbar toolbarActionBar;
    private FloatingActionButton btnFab;

    private LinearLayout mControllerInputTodo;
    private LinearLayout mControllerInputSubject;
    private LinearLayout mControllerViewTodo;
    private LinearLayout mControllerViewSubject;

    private CheckBox mCheckTask;
    private EditText mEditTodoName;
    private Button mBtnCalendar;
    private Button mBtnAddTodo;

    private EditText mEditSubjectName;
    private Button mBtnSubjectColor;
    private Button mBtnAddSubject;

    private Button mBtnChangeSubjectName;
    private Button mBtnChangeSubjectcolor;
    private Button mBtnDeleteSubject;
    private Button mBtnMoveLeft;
    private Button mBtnMoveRight;

    private TextView mTextTodos;
    private Button mBtnDoneTodos;

    private TodoLayout mTodoLayout;

    private TextView mTextGuide;

    private String mTodoIdFromWidget;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mToggle;

    //[20160503] simplify
    public final String TAG_DIALOG_SELECT_SUBJECT = "select_subject";
    public final String TAG_DIALOG_DELETE_SUBJECT = "delete_subject";
    public final String TAG_DIALOG_SELECT_TODO = "select_todo";
    public final String TAG_DIALOG_DONE_TODO = "done_todo";

    public final String TODO_DIVIDER = " / ";

    //TODO need to sperate another class
    private int mSelectedSubjectOrder = -1;
    private String mSelectedTodoIds = "";
    private int mSelectedSubjectColor = 0;
    private Date mSelectedTodoDate = null;

    private int mMode = MODE_NO_SELECTION;
    private DialogFragment mDialogNowViewing;

    private TodoStackAdapter mTodoLayoutAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TodoStack", "[lifecycle][Main] onCreate : " + this.hashCode());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initDrawer();

        initControlView();

        //from widget through cover
        getTodoIdAndSet(getIntent());
    }

    public Context getActivityContext() {
        return this;
    }

    public SubjectData getSelectedSubjectData() {
        return TodoProvider.getInstance(getApplicationContext()).
                getSubjectByOrder(mSelectedSubjectOrder);
    }

    public boolean isViewVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    private void initDrawer() {
        toolbarActionBar = (Toolbar) findViewById(R.id.main_layout_action_bar);
        setSupportActionBar(toolbarActionBar);

        mNavigationView = (NavigationView) findViewById(R.id.main_nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        mToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbarActionBar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);

        mDrawerLayout.setDrawerListener(mToggle);
        mToggle.syncState();

    }

    public void putSubjectsOnDrawer(ArrayList<SubjectData> subjects) {
        ArrayList<SubjectData> ViewAllSubjectList = new ArrayList<SubjectData>();
        SubjectData allSubject = new SubjectData();
        allSubject.subjectName = getResources().getString(R.string.nav_menu_all_todo);
        allSubject.order = NAV_MENU_ITEM_ID_ALL;
        allSubject.color = getResources().getColor(R.color.colorAccent);
        ViewAllSubjectList.add(allSubject);
        ListView viewAllList = (ListView) findViewById(R.id.main_nav_list_all);
        DrawerSubjectListAdapter viewAllAdapter =
                new DrawerSubjectListAdapter(this, ViewAllSubjectList);
        viewAllList.setAdapter(viewAllAdapter);
        viewAllList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object tag = view.getTag();
                if (tag instanceof DrawerSubjectListAdapter.SelectSubjectListItemHolder) {
                    final int order = ((DrawerSubjectListAdapter.SelectSubjectListItemHolder) tag).order;
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            launchDetailTodoListActivity(order, -1);
                        }
                    }, DURATION_ANIMATION);
                }
            }
        });

        ListView subjectList = (ListView) findViewById(R.id.main_nav_list_sub);
        DrawerSubjectListAdapter SubjectAdapter = new DrawerSubjectListAdapter(this, subjects);
        subjectList.setAdapter(SubjectAdapter);
        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object tag = view.getTag();
                if (tag instanceof DrawerSubjectListAdapter.SelectSubjectListItemHolder) {
                    final int order = ((DrawerSubjectListAdapter.SelectSubjectListItemHolder) tag).order;
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            launchDetailTodoListActivity(order, -1);
                        }
                    }, DURATION_ANIMATION);
                }
            }
        });
    }

    /**
     *
     * @param order
     * @param todoType TodoData type, default -1
     */
    private void launchDetailTodoListActivity(int order, int todoType) {
        //init controllviews
        changeMode(MODE_NO_SELECTION);

        Intent detailIntent = new Intent(this, DetailTodoListActivity.class);
        int startType = DetailTodoListPresenter.TODO_TYPE_ALL;
        detailIntent.putExtra(TodoStackContract.SubjectEntry.ORDER, order);
        switch (todoType) {
            case TodoData.TODO_DB_TYPE_ALLDAY:
            case TodoData.TODO_DB_TYPE_PERIOD:
                startType = DetailTodoListPresenter.TODO_TYPE_ALLDAY;
                break;
            case TodoData.TODO_DB_TYPE_TASK:
                startType = DetailTodoListPresenter.TODO_TYPE_TASK;
                break;
        }
        detailIntent.putExtra(DetailTodoListActivity.START_PAGE, startType);
        detailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(detailIntent);
        overridePendingTransition(R.anim.right_in, R.anim.left_half_out);
    }

    private void initControlView() {
        btnFab = (FloatingActionButton)findViewById(R.id.main_btn_fab);
        btnFab.setOnClickListener(this);

        mControllerInputTodo = (LinearLayout)findViewById(R.id.main_layout_todo_input_mode);
        mControllerInputSubject = (LinearLayout)findViewById(R.id.main_layout_subject_input_mode);
        mControllerViewTodo = (LinearLayout)findViewById(R.id.main_layout_todo_viewer_mode);
        mControllerViewSubject = (LinearLayout)findViewById(R.id.main_layout_subject_viewer_mode);

        mCheckTask = (CheckBox)findViewById(R.id.main_cb_input_task);
        mEditTodoName = (EditText)findViewById(R.id.main_edit_input_todo_name);
        mBtnCalendar = (Button)findViewById(R.id.main_btn_input_calendar);
        mBtnCalendar.setOnClickListener(this);
        mBtnAddTodo = (Button)findViewById(R.id.main_btn_input_todo_add);
        mBtnAddTodo.setOnClickListener(this);

        mEditSubjectName = (EditText)findViewById(R.id.main_edit_input_subject_name);
        mBtnSubjectColor = (Button)findViewById(R.id.main_btn_input_subject_color);
        mBtnSubjectColor.setOnClickListener(this);
        mBtnAddSubject = (Button)findViewById(R.id.main_btn_subject_add);
        mBtnAddSubject.setOnClickListener(this);

        mBtnChangeSubjectName = (Button)findViewById(R.id.main_btn_edit_subject_name);
        mBtnChangeSubjectName.setOnClickListener(this);
        mBtnChangeSubjectcolor = (Button)findViewById(R.id.main_btn_edit_subject_color);
        mBtnChangeSubjectcolor.setOnClickListener(this);
        mBtnDeleteSubject = (Button)findViewById(R.id.main_btn_subject_delete);
        mBtnDeleteSubject.setOnClickListener(this);
        mBtnMoveLeft = (Button)findViewById(R.id.main_btn_subject_left);
        mBtnMoveLeft.setOnClickListener(this);
        mBtnMoveRight = (Button)findViewById(R.id.main_btn_subject_right);
        mBtnMoveRight.setOnClickListener(this);

        mTextTodos = (TextView)findViewById(R.id.main_text_view_todo);
//        mTextTodos.setLinksClickable(true);
        mTextTodos.setMovementMethod(LinkMovementMethod.getInstance());
        mBtnDoneTodos = (Button)findViewById(R.id.main_btn_view_todo_done);
        mBtnDoneTodos.setOnClickListener(this);

        mTodoLayout = (TodoLayout)findViewById(R.id.main_vg_todo_layout);
        mTodoLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("TodoStack", "timing test - onGlobalLayout");
                mTodoLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                refresh();
            }
        });
        mTextGuide = (TextView)findViewById(R.id.main_text_guide_text);



    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("TodoStack", "[lifecycle][Main] onNewIntent : " + this.hashCode());
        super.onNewIntent(intent);

        getTodoIdAndSet(intent);
    }

    private void getTodoIdAndSet(Intent intent) {
        mTodoIdFromWidget = intent.getStringExtra(TodoStackContract.TodoEntry._ID);
        Log.d("TodoStack", "[getTodoIdAndSet] id from widget = " + mTodoIdFromWidget);

//        if (mTodoIdFromWidget != null && !"".equalsIgnoreCase(mTodoIdFromWidget)) {
//            setTodoIdNowViewing(mTodoIdFromWidget);
//            mTodoIdFromWidget = "";
//        }
    }

    public void setTodoIdNowViewing(String todoId) {
        mSelectedTodoIds = todoId;
        TodoViewInfo infoFromWidget = new TodoViewInfo(TodoViewInfo.TYPE_DATE_TODO, mSelectedTodoIds, true);
        mSelectedSubjectOrder = getSelectedSubjectOrderFromTag(infoFromWidget);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_overflow, menu);
        return true;
    }



    //simplify [start]
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d("TodoStack", "[onOptionsItemSelected] called");

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (id) {
            case R.id.add_subject:
                changeMode(MODE_ADD_SUBJECT);
                break;
            case R.id.action_settings:
                //TODO go settings
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.i("TodoStack", "[lifecycle][Main] onResume : " + this.hashCode());
        super.onResume();

        //TODO at first time, is called with mTodoLayout's size is 0x0
        refresh();
    }

    private void refresh() {
        //from widget intent
        if (mMode == MODE_NO_SELECTION) {
            if (mTodoIdFromWidget != null && !"".equalsIgnoreCase(mTodoIdFromWidget)) {
                setTodoIdNowViewing(mTodoIdFromWidget);
                mTodoIdFromWidget = "";
                mMode = MODE_VIEW_TODO;
            }
        }

        if (mTodoLayoutAdapter == null) {
            mTodoLayoutAdapter = new TodoStackAdapter(this, mTodoLayout, this, this);
        } else {
            mTodoLayoutAdapter.resetLayoutSize(mTodoLayout.getWidth(), mTodoLayout.getHeight());
        }
        mTodoLayoutAdapter.notifyDataSetChanged();
        changeMode(mMode);
        //to navigation drawer
        putSubjectsOnDrawer(TodoProvider.getInstance(getApplicationContext()).getAllSubject());
    }

    @Override
    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag == null) { //normal buttons in Activity
            switch (v.getId()) {
                case R.id.main_btn_fab:
                    changeModeByDataIsEmpty();
                    break;
                case R.id.main_btn_input_calendar:
                    getDateFromDatePicker();
                    break;
                case R.id.main_btn_input_todo_add:
                    if (checkVaildData()) {
                        insertTodo();
                    }
                    break;
                case R.id.main_btn_input_subject_color:
                    getColorFromColorDialog();
                    break;
                case R.id.main_btn_subject_add:
                    if (checkVaildData()) {
                        insertSubject();
                    }
                    break;
                case R.id.main_btn_edit_subject_name:
                    editSubjectName();
                    break;
                case R.id.main_btn_edit_subject_color:
                    editSubjectColor();
                    break;
                case R.id.main_btn_subject_delete:
                    deleteSubject();
                    break;
                case R.id.main_btn_subject_left:
                    moveSubjectOrder(true);
                    break;
                case R.id.main_btn_subject_right:
                    moveSubjectOrder(false);
                    break;
                case R.id.main_btn_view_todo_done:
                    showTodoSelectDialog();
                    break;
            }
        } else { // todoLayout view click
            if (tag != null && tag instanceof TodoViewInfo) {
                mSelectedSubjectOrder = getSelectedSubjectOrderFromTag(tag);
                mSelectedTodoIds = ((TodoViewInfo) tag).id;
                int type = ((TodoViewInfo) tag).type;
                if (type == TodoViewInfo.TYPE_SUBJECT) {
                    changeMode(MODE_ADD_TODO);
                } else if (type == TodoViewInfo.TYPE_DELAYED_TODO
                        || type == TodoViewInfo.TYPE_TASK
                        || type == TodoViewInfo.TYPE_DATE_TODO) {
                    changeMode(MODE_VIEW_TODO);
                } else if (type == TodoViewInfo.TYPE_VIEW_ALL_TASK) {
                    launchDetailTodoListActivity(mSelectedSubjectOrder, TodoData.TODO_DB_TYPE_TASK);
                } else if (type == TodoViewInfo.TYPE_VIEW_ALL_DELAYED_TODO) {
                    launchDetailTodoListActivity(mSelectedSubjectOrder,
                            TodoData.TODO_DB_TYPE_ALLDAY);
                }
            }
        }
    }

    private int getSelectedSubjectOrderFromTag(Object tag) {
        int ret = -1;
        if (tag != null && tag instanceof TodoViewInfo) {
            int type = ((TodoViewInfo) tag).type;
            if (type == TodoViewInfo.TYPE_SUBJECT
                    || type == TodoViewInfo.TYPE_VIEW_ALL_TASK
                    || type == TodoViewInfo.TYPE_VIEW_ALL_DELAYED_TODO) {
                ret = Integer.parseInt(((TodoViewInfo) tag).id);
            } else if (type == TodoViewInfo.TYPE_DELAYED_TODO
                    || type == TodoViewInfo.TYPE_TASK
                    || type == TodoViewInfo.TYPE_DATE_TODO) {
                String combinedId = ((TodoViewInfo) tag).id;
                String[] stringIds = combinedId.split(TodoViewInfo.DELIMITER_ID);
                TodoData td = TodoProvider.getInstance(getApplicationContext()).getTodoById(
                        Integer.parseInt(stringIds[0]));
                ret = td.subjectOrder;
            }
        }
//        Log.d("TodoStack", "[getSelectedSubjectOrderFromTag] ret = " + ret);
        return ret;
    }

    @Override
    public boolean onLongClick(View v) {
        Object tag = v.getTag();
        if (tag != null && tag instanceof TodoViewInfo) {
            mSelectedSubjectOrder = getSelectedSubjectOrderFromTag(tag);
            changeMode(MODE_VIEW_SUBJECT);
            return true;
        } else {
            return false;
        }
    }

    public void changeModeByDataIsEmpty() {
        if (TodoProvider.getInstance(getApplicationContext()).getSubjectCount() == 0) {
            changeMode(MODE_ADD_SUBJECT);
        } else {
            showSubjectSelectDialog();
        }
    }

    private void showSubjectSelectDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment dialog = SubjectSelectDialog.newInstance(
                new SubjectSelectDialog.Callback() {
                    @Override
                    public void onSelectSubject(int order) {
                        mSelectedSubjectOrder = order;
                        changeMode(MODE_ADD_TODO);
                    }
                });
        dialog.show(ft, TAG_DIALOG_SELECT_SUBJECT);
        mDialogNowViewing = dialog;
    }

    public void setActionBarText(String title, int color) {
        toolbarActionBar.setTitle(title);
        toolbarActionBar.setTitleTextColor(color);
        setSupportActionBar(toolbarActionBar);
    }

    public void setGuideText(String guideText) {
        setGuideText(guideText, getResources().getColor(R.color.color_normal_state));
    }

    public void setGuideText(String guideText, int color) {
        mTextGuide.setText(guideText);
        mTextGuide.setTextColor(color);
    }

    private void hideInputMethod(EditText edit) {
        InputMethodManager inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }

    @Override
    public void changeMode(int mode) {
        Resources res = getResources();
        if (mode == MODE_NO_SELECTION) {
            //init saved values
            mSelectedSubjectOrder = -1;
            mSelectedTodoIds = "";
            setAllControllerGone();
            setActionBarText(res.getString(R.string.app_name),
                    res.getColor(R.color.colorAccent));
            setGuideText(res.getString(R.string.guide_text_suggest_select_subject));
        }
        else if (mode == MODE_ADD_TODO) {
            SubjectData sd = getSelectedSubjectData();
            setInputTodoVisible();
            setActionBarText(
                    res.getString(R.string.adding_text_on_new_todo) + " " + sd.subjectName,
                    sd.color);
            setGuideText(res.getString(R.string.guide_text_mode_input_todo));
        }
        else if (mode == MODE_ADD_SUBJECT) {
            setInputSubjectVisible();
            setActionBarText(res.getString(R.string.title_text_on_new_subject),
                    res.getColor(R.color.color_normal_state));
            setGuideText(res.getString(R.string.guide_text_mode_input_subject));
        }
        else if (mode == MODE_VIEW_TODO) {
            SubjectData sd = getSelectedSubjectData();
            setViewTodoVisible();
            setActionBarText(
                    res.getString(R.string.adding_text_view_todo) + " " + sd.subjectName,
                    sd.color);
            setGuideText(res.getString(R.string.guide_text_mode_view_todo));
        }
        else if (mode == MODE_VIEW_SUBJECT) {
            SubjectData sd = getSelectedSubjectData();
            setViewSubjectVisible();
            setActionBarText(sd.subjectName, sd.color);
            setGuideText(res.getString(R.string.guide_text_mode_view_subject));
        }

        mMode = mode;
    }

    public void setAllControllerGone() {
        if (mControllerInputTodo.getVisibility() == View.VISIBLE) {
            mEditTodoName.setText("");
            mCheckTask.setChecked(false);
            hideInputMethod(mEditTodoName);
        } else if (mControllerInputSubject.getVisibility() == View.VISIBLE) {
            mEditSubjectName.setText("");
            hideInputMethod(mEditSubjectName);
        }
        mControllerInputTodo.setVisibility(View.GONE);
        mControllerInputSubject.setVisibility(View.GONE);
        mControllerViewTodo.setVisibility(View.GONE);
        mControllerViewSubject.setVisibility(View.GONE);
    }

    public void setInputTodoVisible() {
        int subjectColor = getSelectedSubjectData().color;
        if (mControllerInputTodo.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            mControllerInputTodo.setVisibility(View.VISIBLE);
            //present today
            Calendar calendar = Calendar.getInstance();
            mSelectedTodoDate = calendar.getTime();

            mBtnCalendar.setText(TodoStackUtil.getFomatedDateSimple(this, calendar.getTime()));
        }
        mCheckTask.setTextColor(subjectColor);
        mEditTodoName.setTextColor(subjectColor);
        mBtnCalendar.setTextColor(subjectColor);
        mBtnAddTodo.setTextColor(subjectColor);
    }

    public void setInputSubjectVisible() {
        if (mControllerInputSubject.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            mControllerInputSubject.setVisibility(View.VISIBLE);
        }
        mSelectedSubjectColor = getResources().getColor(R.color.color_normal_state);
        setInputSubjectLayoutColor();
    }

    public void setInputSubjectLayoutColor() {
        mBtnSubjectColor.setTextColor(mSelectedSubjectColor);
        mEditSubjectName.setTextColor(mSelectedSubjectColor);
        mBtnAddSubject.setTextColor(mSelectedSubjectColor);
    }

    public void setViewSubjectVisible() {
        if (mSelectedSubjectOrder < 0)
            return;

        TodoProvider todoProvider = TodoProvider.getInstance(getApplicationContext());
        int subjectCount = todoProvider.getSubjectCount();
        int subjectColor = todoProvider.getSubjectByOrder(mSelectedSubjectOrder).color;
        boolean leftEnable = !(mSelectedSubjectOrder <= 0);
        boolean rightEnable = !(mSelectedSubjectOrder >= (subjectCount - 1));
        if (mControllerViewSubject.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            mControllerViewSubject.setVisibility(View.VISIBLE);
        }
        mBtnChangeSubjectName.setTextColor(subjectColor);
        mBtnChangeSubjectcolor.setTextColor(subjectColor);
        mBtnDeleteSubject.setTextColor(subjectColor);
        mBtnMoveLeft.setEnabled(leftEnable);
        mBtnMoveLeft.setTextColor(subjectColor);
        mBtnMoveRight.setEnabled(rightEnable);
        mBtnMoveRight.setTextColor(subjectColor);
    }

    public void setViewTodoVisible() {
        int subjectColor = getSelectedSubjectData().color;
        if ("".equalsIgnoreCase(mSelectedTodoIds))
            return;

        if (mControllerViewTodo.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            mControllerViewTodo.setVisibility(View.VISIBLE);
        }
        //TODO need to change color following todos
        mBtnDoneTodos.setTextColor(subjectColor);

        mTextTodos.setText(getSpannableStringFromTodos(mSelectedTodoIds));
    }

    private SpannableString getSpannableStringFromTodos(String todoIds) {
        final TodoProvider provider = TodoProvider.getInstance(getApplicationContext());
        String todoString = "";
        String[] ids = todoIds.split(TodoViewInfo.DELIMITER_ID);

        for (int i = 0; i < ids.length; ++i) {
            TodoData td = provider.getTodoById(Integer.parseInt(ids[i]));
            if (i == 0)
                todoString += td.todoName;
            else
                todoString += TODO_DIVIDER + td.todoName;
        }
        SpannableString ret = new SpannableString(todoString);
        int pos = 0;
        for (int i = 0; i < ids.length; ++i) {
            final TodoData td = provider.getTodoById(Integer.parseInt(ids[i]));
            final SubjectData sd = provider.getSubjectByOrder(td.subjectOrder);
            ret.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    //TODO launch detail info dialog ? temp launch done dialog
                    showTodoDoneDialog(td.id);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
//                    Log.d("TodoStack", "sd color = " + sd.color);
                    ds.setColor(sd.color);
                    ds.setUnderlineText(false);
                }
            }, pos, pos + td.todoName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            pos += td.todoName.length() + TODO_DIVIDER.length();
        }
        return ret;
    }

    private void showTodoDoneDialog(int todoId) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        final DialogFragment dialog =
                TodoDoneDialog.newInstance(todoId, new TodoDoneDialog.Callback() {
            @Override
            public void onDeleteTodo(int id) {
                Context context = getApplicationContext();
                UpdateTodoTask deleteTodoTask = new UpdateTodoTask(getActivityContext(),
                        new UpdateTodoTask.TaskEndCallback() {
                            @Override
                            public void updateFinished() {
                                mTodoLayoutAdapter.notifyDataSetChanged();
                                changeMode(MODE_NO_SELECTION);
                            }
                        });
                deleteTodoTask.setData(TodoProvider.getInstance(context).getTodoById(id),
                        UpdateTodoTask.TODO_TASK_DELETE_TODO);
                deleteTodoTask.execute();
            }

            @Override
            public void onMoveTodo(int id, int moveType) {
                UpdateTodoTask moveTodoTask = new UpdateTodoTask(getActivityContext(),
                        new UpdateTodoTask.TaskEndCallback() {
                            @Override
                            public void updateFinished() {
                                mTodoLayoutAdapter.notifyDataSetChanged();
                                changeMode(MODE_NO_SELECTION);
                            }
                        });
                moveTodoTask.setData(TodoProvider.
                        getInstance(getApplicationContext()).getTodoById(id), moveType);
                moveTodoTask.execute();
            }

            @Override
            public void onCancelSelected() {
                //do nothing
            }
        });
        dialog.show(ft, TAG_DIALOG_DONE_TODO);
        mDialogNowViewing = dialog;
    }

    //simplify [end]

    private boolean checkVaildData() {
        if (isViewVisible(mControllerInputTodo)) {
            if (!TodoStackUtil.checkVaildTodoDate(this, mSelectedTodoDate))
                return false;
            String name = mEditTodoName.getText().toString();
            if (!TodoStackUtil.checkVaildName(this, name))
                return false;
        }
        if (isViewVisible(mControllerInputSubject)) {
            String name = mEditSubjectName.getText().toString();
            if (!TodoStackUtil.checkVaildName(this, name))
                return false;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        switch (itemId) {
            case 0:
                Toast.makeText(MainActivity.this, "all!", Toast.LENGTH_SHORT).show();
                break;
            case 200:
                Toast.makeText(MainActivity.this, "200!", Toast.LENGTH_SHORT).show();
                break;
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (mMode != MODE_NO_SELECTION) {
                changeMode(MODE_NO_SELECTION);
            } else {
                finish();
            }
        }
    }

    private void getDateFromDatePicker() {
        final String dialogTag = TodoDatePickerDialog.class.getSimpleName();
        DialogFragment dialog =
                TodoDatePickerDialog.newInstance(new TodoDatePickerDialog.Callback() {
                    @Override
                    public void onDateSet(Date date) {
                        mSelectedTodoDate = date;
                        mBtnCalendar.setText(TodoStackUtil.
                                getFomatedDateSimple(getApplicationContext(), mSelectedTodoDate));
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    private void insertTodo() {
        UpdateTodoTask insertTodoTask =
                new UpdateTodoTask(getActivityContext(), new UpdateTodoTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        mTodoLayoutAdapter.notifyDataSetChanged();
                        changeMode(MODE_NO_SELECTION);
                    }
                });
        insertTodoTask.setData(makeTodoData(), UpdateTodoTask.TODO_TASK_ADD_TODO);
        insertTodoTask.execute();
    }

    private TodoData makeTodoData() {
        TodoData todo = new TodoData();

        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(mSelectedTodoDate);
        targetDate.set(Calendar.HOUR_OF_DAY, 0);
        targetDate.set(Calendar.MINUTE, 0);
        targetDate.set(Calendar.SECOND, 0);
        targetDate.set(Calendar.MILLISECOND, 0);

        todo.todoName = mEditTodoName.getText().toString();
        todo.subjectOrder = mSelectedSubjectOrder;
        todo.date = targetDate.getTimeInMillis();
        todo.type = mCheckTask.isChecked() ?
                TodoData.TODO_DB_TYPE_TASK : TodoData.TODO_DB_TYPE_ALLDAY;
        todo.timeFrom = TodoData.TIME_NOT_EXIST;
        todo.timeTo = TodoData.TIME_NOT_EXIST;
        todo.location = "";

        return todo;
    }

    private void getColorFromColorDialog() {
        final String dialogTag = SubjectColorSelectDialog.class.getSimpleName();
        DialogFragment dialog =
                SubjectColorSelectDialog.newInstance(new SubjectColorSelectDialog.Callback() {
                    @Override
                    public void onSelectColor(int color) {
                        mSelectedSubjectColor = color;
                        setInputSubjectLayoutColor();
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    private void insertSubject() {
        UpdateSubjectTask insertSubjectTask =
                new UpdateSubjectTask(getActivityContext(), new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        mTodoLayoutAdapter.notifyDataSetChanged();
                        changeMode(MODE_NO_SELECTION);
                    }
                });
        insertSubjectTask.setData(
                makeNewSubjectData(), UpdateSubjectTask.SUBJECT_TASK_ADD_SUBJECT);
        insertSubjectTask.execute();
    }

    private SubjectData makeNewSubjectData() {
        SubjectData subject = new SubjectData();
        subject.subjectName = mEditSubjectName.getText().toString();
        subject.color = mSelectedSubjectColor;
        subject.order = TodoProvider.getInstance(getApplicationContext()).getSubjectCount();

        return subject;
    }

    private void editSubjectName() {
        final String dialogTag = SubjectNameUpdateDialog.class.getSimpleName();
        DialogFragment dialog =
                SubjectNameUpdateDialog.newInstance(new SubjectNameUpdateDialog.Callback() {
                    @Override
                    public void onEditName(String name) {
                        changeSubjectName(name);
                    }
                });
        ((SubjectNameUpdateDialog) dialog).setOriginName(toolbarActionBar.getTitle().toString());
        dialog.show(getFragmentManager(), dialogTag);
    }

    public void changeSubjectName(String name) {
        UpdateSubjectTask updateSubjectTask = new UpdateSubjectTask(getActivityContext(),
                new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        mTodoLayoutAdapter.notifyDataSetChanged();
                        changeMode(MODE_VIEW_SUBJECT);
                    }
                });
        SubjectData sd = getSelectedSubjectData();
        sd.subjectName = name;
        updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MODIFY_NAME);
        updateSubjectTask.execute();
    }

    private void editSubjectColor() {
        final String dialogTag = SubjectColorSelectDialog.class.getSimpleName();
        DialogFragment dialog =
                SubjectColorSelectDialog.newInstance(new SubjectColorSelectDialog.Callback() {
                    @Override
                    public void onSelectColor(int color) {
                        changeSubjectColor(color);
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    public void changeSubjectColor(int color) {
        UpdateSubjectTask updateSubjectTask = new UpdateSubjectTask(getActivityContext(),
                new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        mTodoLayoutAdapter.notifyDataSetChanged();
                        changeMode(MODE_VIEW_SUBJECT);
                    }
                });
        SubjectData sd = getSelectedSubjectData();
        sd.color = color;
        updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MODIFY_COLOR);
        updateSubjectTask.execute();
    }

    public void deleteSubject() {
        TodoProvider provider = TodoProvider.getInstance(getApplicationContext());
        if (mSelectedSubjectOrder >= 0
                && mSelectedSubjectOrder < (provider.getSubjectCount() - 1)) {
            Toast.makeText(this, getResources().getString(R.string.toast_waring_not_end_subject),
                    Toast.LENGTH_LONG).show();
        } else if (mSelectedSubjectOrder == (provider.getSubjectCount() - 1)) {
            showSubjectDeleteDialog();
        } else {
            Log.e("TodoStack", "[deleteSubject] Invalid subject number : "
                    + mSelectedSubjectOrder);
        }
    }

    private void showSubjectDeleteDialog() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment dialog = SubjectDeleteDialog.newInstance(mSelectedSubjectOrder,
                new SubjectDeleteDialog.Callback() {
                    @Override
                    public void onSelectDelete(int order) {
                        TodoProvider provider = TodoProvider.getInstance(getApplicationContext());
                        if (provider.getTodoCount(mSelectedSubjectOrder) == 0) {
                            UpdateSubjectTask deletesubjectTask =
                                    new UpdateSubjectTask(getActivityContext(),
                                            new UpdateSubjectTask.TaskEndCallback() {
                                                @Override
                                                public void updateFinished() {
                                                    mTodoLayoutAdapter.notifyDataSetChanged();
                                                    changeMode(MODE_NO_SELECTION);
                                                }
                                            });
                            deletesubjectTask.setData(
                                    provider.getSubjectByOrder(mSelectedSubjectOrder),
                                    UpdateSubjectTask.SUBJECT_TASK_DELETE_SUBJECT);
                            deletesubjectTask.execute();
                        } else {
                            Toast.makeText(getActivityContext(),
                                    getResources().getString(R.string.toast_waring_remained_todos),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        dialog.show(ft, TAG_DIALOG_DELETE_SUBJECT);
        mDialogNowViewing = dialog;
    }

    public void moveSubjectOrder(final boolean isLeft) {
        Log.d("TodoStack", "[moveSubjectOrder] target order = " + mSelectedSubjectOrder
                + "direction = " + (isLeft ? "left" : "right"));
        UpdateSubjectTask updateSubjectTask = new UpdateSubjectTask(getActivityContext(),
                new UpdateSubjectTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        mSelectedSubjectOrder += isLeft ? UpdateSubjectTask.DIRECTION_LEFT :
                                UpdateSubjectTask.DIRECTION_RIGHT;
                        mTodoLayoutAdapter.notifyDataSetChanged();
                        changeMode(MODE_VIEW_SUBJECT);
                    }
                });
        SubjectData sd = getSelectedSubjectData();
        if (isLeft) {
            updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MOVE_LEFT);
        } else {
            updateSubjectTask.setData(sd, UpdateSubjectTask.SUBJECT_TASK_MOVE_RIGHT);
        }
        updateSubjectTask.execute();
    }

    private void showTodoSelectDialog() {
        //if id has only one id, skip dialog
        String[] sIds = mSelectedTodoIds.split(TodoViewInfo.DELIMITER_ID);
        if (sIds.length == 1) {
            showTodoDoneDialog(Integer.parseInt(sIds[0]));
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment dialog = TodoSelectDialog.newInstance(mSelectedTodoIds,
                    new TodoSelectDialog.Callback() {
                        @Override
                        public void onSelectTodo(int id) {
                            showTodoDoneDialog(id);
                        }
                    });
            dialog.show(ft, TAG_DIALOG_SELECT_TODO);
            mDialogNowViewing = dialog;
        }
    }



    //for test

    @Override
    protected void onDestroy() {
        Log.i("TodoStack", "[lifecycle][Main] onDestroy : " + this.hashCode());
        super.onDestroy();
    }

    class DrawerSubjectListAdapter extends BaseAdapter {
        Context mContext;
        ArrayList<SubjectData> mSubjects;

        public DrawerSubjectListAdapter(Context context, ArrayList<SubjectData> subjects) {
            mContext = context;
            mSubjects = subjects;
        }

        @Override
        public int getCount() {
            return mSubjects.size();
        }

        @Override
        public Object getItem(int position) {
            return mSubjects.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            }
            SubjectData sd = mSubjects.get(position);
            TextView tv = (TextView) convertView.findViewById(android.R.id.text1);
            tv.setText(sd.subjectName);
            tv.setTextColor(sd.color);
            SelectSubjectListItemHolder holder = new SelectSubjectListItemHolder();
            holder.order = sd.order;
            tv.setTag(holder);

            return convertView;
        }

        class SelectSubjectListItemHolder {
            int order;
        }
    }
}
