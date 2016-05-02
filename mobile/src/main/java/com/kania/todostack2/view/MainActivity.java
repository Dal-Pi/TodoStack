package com.kania.todostack2.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.presenter.IControllerMediator;
import com.kania.todostack2.presenter.SubjectColorSelectDialog;
import com.kania.todostack2.presenter.UpdateTodoTask;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.util.SubjectNameUpdateDialog;
import com.kania.todostack2.util.SubjectSelectDialog;
import com.kania.todostack2.util.TodoDatePickerDialog;
import com.kania.todostack2.util.TodoDoneDialog;
import com.kania.todostack2.util.TodoStackUtil;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends AppCompatActivity implements ITodoLayoutMediator,
        View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private final int DURATION_ANIMATION = 500;

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

    private EditText mEditSubjectName;
    private Button mBtnSubjectColor;

    private Button mBtnChangeSubjectName;
    private Button mBtnChangeSubjectcolor;
    private Button mBtnDeleteSubject;
    private Button mBtnMoveLeft;
    private Button mBtnMoveRight;

    private TextView mTextTodos;

    private TodoLayout mTodoLayout;

    private TextView mTextGuide;

    private boolean mNowBusy = false;

    private String mTodoIdFromWidget;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mToggle;

    private boolean mIsTodoLayoutLoaded = false;

    //[20160503] simplify
    public final String TAG_DIALOG_SELECT_SUBJECT = "select_subject";
    public final String TAG_DIALOG_DELETE_SUBJECT = "delete_subject";
    public final String TAG_DIALOG_SELECT_TODO = "select_todo";
    public final String TAG_DIALOG_DONE_TODO = "done_todo";

    public final String TODO_DIVIDER = " / ";

    private int mSelectedSubjectOrder = -1;
    private int mMode = MODE_NO_SELECTION;
    private DialogFragment mDialogNowViewing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TodoStack", "[lifecycle][Main] onCreate : " + this.hashCode());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        mMediator = new TodoStackPresenter(this);
//        mMediator.setTargetView(this);

        initDrawer();

        initControlView();

        //from widget through cover
        getTodoIdAndSetToMediator(getIntent());
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

    @Override
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
                    int order = ((DrawerSubjectListAdapter.SelectSubjectListItemHolder) tag).order;
//                    if (mDrawerLayout != null) {
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                    }
                    mMediator.clickNavigationDrawerItem(order);
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
                    //TODO will remove Handler. It is bad code
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMediator.clickNavigationDrawerItem(order);
                        }
                    }, 300);
                }
            }
        });
    }

    private void initControlView() {
        btnFab = (FloatingActionButton) findViewById(R.id.main_btn_fab);
        btnFab.setOnClickListener(this);

        mControllerInputTodo = (LinearLayout) findViewById(R.id.main_layout_todo_input_mode);
        mControllerInputSubject = (LinearLayout) findViewById(R.id.main_layout_subject_input_mode);
        mControllerViewTodo = (LinearLayout) findViewById(R.id.main_layout_todo_viewer_mode);
        mControllerViewSubject = (LinearLayout) findViewById(R.id.main_layout_subject_viewer_mode);

        mCheckTask = (CheckBox) findViewById(R.id.main_cb_input_task);
        mEditTodoName = (EditText) findViewById(R.id.main_edit_input_todo_name);
        mBtnCalendar = (Button) findViewById(R.id.main_btn_input_calendar);
        mBtnCalendar.setOnClickListener(this);

        mEditSubjectName = (EditText) findViewById(R.id.main_edit_input_subject_name);
        mBtnSubjectColor = (Button) findViewById(R.id.main_btn_input_subject_color);
        mBtnSubjectColor.setOnClickListener(this);

        mBtnChangeSubjectName = (Button) findViewById(R.id.main_btn_edit_subject_name);
        mBtnChangeSubjectName.setOnClickListener(this);
        mBtnChangeSubjectcolor = (Button) findViewById(R.id.main_btn_edit_subject_color);
        mBtnChangeSubjectcolor.setOnClickListener(this);
        mBtnDeleteSubject = (Button) findViewById(R.id.main_btn_subject_delete);
        mBtnDeleteSubject.setOnClickListener(this);
        mBtnMoveLeft = (Button) findViewById(R.id.main_btn_subject_left);
        mBtnMoveLeft.setOnClickListener(this);
        mBtnMoveRight = (Button) findViewById(R.id.main_btn_subject_right);
        mBtnMoveRight.setOnClickListener(this);

        mTextTodos = (TextView) findViewById(R.id.main_text_view_todo);
//        mTextTodos.setLinksClickable(true);
        mTextTodos.setMovementMethod(LinkMovementMethod.getInstance());

        mTodoLayout = (TodoLayout) findViewById(R.id.main_vg_todo_layout);
        mTodoLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d("TodoStack", "timing test - onGlobalLayout");
                mTodoLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mIsTodoLayoutLoaded = true;
                mMediator.initTodoLayout(mTodoLayout.getWidth(), mTodoLayout.getHeight());
            }
        });
        mTextGuide = (TextView) findViewById(R.id.main_text_guide_text);



    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("TodoStack", "[lifecycle][Main] onNewIntent : " + this.hashCode());
        super.onNewIntent(intent);

        getTodoIdAndSetToMediator(intent);
    }

    private void getTodoIdAndSetToMediator(Intent intent) {
        mTodoIdFromWidget = intent.getStringExtra(TodoStackContract.TodoEntry._ID);
        Log.d("TodoStack", "[getTodoIdAndSetToMediator] id from widget = " + mTodoIdFromWidget);

        if (mTodoIdFromWidget != null && !"".equalsIgnoreCase(mTodoIdFromWidget)) {
            mMediator.setTodoIdNowViewing(mTodoIdFromWidget);
            mTodoIdFromWidget = "";
        }
    }

    @Override
    protected void onResume() {
        Log.i("TodoStack", "[lifecycle][Main] onResume : " + this.hashCode());
        super.onResume();
        if (mIsTodoLayoutLoaded) {
            mMediator.initTodoLayout(mTodoLayout.getWidth(), mTodoLayout.getHeight());
            mMediator.setModeByOwnInfo();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_overflow, menu);
        return true;
    }

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
                mMediator.selectMenuAddSubject();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //simplify [start]
    @Override
    public void onClick(View v) {
        if (mNowBusy)
            return;
        switch (v.getId()) {
            case R.id.main_btn_fab:
//                if (checkVaildData()) {
//                    mMediator.clickFloatingActionButton(getBundleFromVisibleLayout());
//                }
                changeModeByDataIsEmpty();
                break;
            case R.id.main_btn_input_calendar:
                getDateFromDatePicker();
                break;
            case R.id.main_btn_input_subject_color:
                getColorFromColorDialog();
                break;
            case R.id.main_btn_edit_subject_name:
                editSubjectName();
                break;
            case R.id.main_btn_edit_subject_color:
                editSubjectColor();
                break;
            case R.id.main_btn_subject_delete:
                mMediator.deleteSubject();
                break;
            case R.id.main_btn_subject_left:
                mMediator.moveSubjectOrder(true);
                break;
            case R.id.main_btn_subject_right:
                mMediator.moveSubjectOrder(false);
                break;
        }
    }

    public void changeModeByDataIsEmpty() {
        if (TodoProvider.getSubjectCount() == 0) {
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

    @Override
    public void changeMode(int mode) {
        switch (mode) {
            case MODE_NO_SELECTION:
                setAllControllerGone();
                break;
            case MODE_ADD_TODO:
                setInputTodoVisible();
                break;
            case MODE_ADD_SUBJECT:
                setInputSubjectVisible();
                break;
            case MODE_VIEW_TODO:
                setViewSubjectVisible();
                break;
            case MODE_VIEW_SUBJECT:
                break;
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
        int subjectColor = TodoProvider.getInstance(this).
                getSubjectByOrder(mSelectedSubjectOrder).color;
        if (mControllerInputTodo.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            mControllerInputTodo.setVisibility(View.VISIBLE);
            //present today
            Calendar calendar = Calendar.getInstance();
            mBtnCalendar.setText(TodoStackUtil.getFomatedDateSimple(this, calendar.getTime()));
        }
        mBtnCalendar.setTextColor(subjectColor);
        mCheckTask.setTextColor(subjectColor);
    }

    public void setInputSubjectVisible() {
        if (mControllerInputSubject.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            mControllerInputSubject.setVisibility(View.VISIBLE);
            mBtnSubjectColor.setTextColor(getResources().getColor(R.color.color_normal_state));
        }
    }

    public void setViewSubjectVisible() {
        TodoProvider todoProvider = TodoProvider.getInstance(this);
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
        mBtnMoveLeft.setTextColor(
                leftEnable ? subjectColor : getResources().getColor(R.color.color_lightgray));
        mBtnMoveRight.setEnabled(rightEnable);
        mBtnMoveRight.setTextColor(
                rightEnable ? subjectColor : getResources().getColor(R.color.color_lightgray));
    }

    public void setViewTodoVisible(SpannableString spannableString) {
        if (mControllerViewTodo.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            mControllerViewTodo.setVisibility(View.VISIBLE);
        }
        mTextTodos.setText(spannableString);
    }

    private SpannableString getSpannableStringFromTodos(ArrayList<Integer> todoIds) {
        final TodoProvider provider = TodoProvider.getInstance(this);
        String todoString = "";
//        String[] ids = info.id.split(TodoViewInfo.DELIMITER_ID);
//        int[] lengths = new int[ids.length];
//        ClickableSpan[] clickableSpans = new ClickableSpan[ids.length];

        for (Integer id : todoIds) {
            TodoData td = provider.getTodoById(id);
            if (todoIds.size() == 1)
                todoString += td.todoName;
            else
                todoString += TODO_DIVIDER + td.todoName;
        }
        SpannableString ret = new SpannableString(todoString);
        int pos = 0;
        for (Integer id : todoIds) {
            final TodoData td = provider.getTodoById(id);
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
                UpdateTodoTask deleteTodoTask = new UpdateTodoTask(context,
                        new UpdateTodoTask.TaskEndCallback() {
                            @Override
                            public void updateFinished() {
                                //TODO restartLoader needed
//                                reloadTodoDataToView(_MODE_NO_SELECTION);
                            }
                        });
                deleteTodoTask.setData(TodoProvider.getInstance(context).getTodoById(id),
                        UpdateTodoTask.TODO_TASK_DELETE_TODO);
                deleteTodoTask.execute();
            }

            @Override
            public void onMoveTodo(int id, int moveType) {
                Context context = getApplicationContext();
                UpdateTodoTask moveTodoTask = new UpdateTodoTask(context,
                        new UpdateTodoTask.TaskEndCallback() {
                            @Override
                            public void updateFinished() {
                                //TODO restartLoader needed
//                                reloadTodoDataToView(_MODE_NO_SELECTION);
                            }
                        });
                moveTodoTask.setData(TodoProvider.getInstance(context).getTodoById(id), moveType);
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
//            String year = editYear.getText().toString();
//            String month = editMonth.getText().toString();
//            String day = editDay.getText().toString();
//            if (!TodoStackUtil.checkVaildTodoDate(this, year, month, day))
//                return false;
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

    private Bundle getBundleFromVisibleLayout() {
        Bundle bundle = new Bundle();
        if (isViewVisible(mControllerInputTodo)) {
            bundle.putString(TodoStackContract.TodoEntry.TODO_NAME,
                    mEditTodoName.getText().toString());
            //subject order set on presenter
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR_OF_DAY, 0);
            date.set(Calendar.MINUTE, 0);
            date.set(Calendar.SECOND, 0);
            date.set(Calendar.MILLISECOND, 0);
//            date.set(Calendar.YEAR, Integer.parseInt(editYear.getText().toString()));
//            date.set(Calendar.MONTH, Integer.parseInt(editMonth.getText().toString()) - 1);
//            date.set(Calendar.DATE, Integer.parseInt(editDay.getText().toString()));
            //debug
//            Log.d("TodoStack", "[getBundleFromVisibleLayout] dateString = " + dateString);
            bundle.putLong(TodoStackContract.TodoEntry.DATE, date.getTimeInMillis());
            bundle.putInt(TodoStackContract.TodoEntry.TYPE,
                    mCheckTask.isChecked() ?
                            TodoData.TODO_DB_TYPE_TASK : TodoData.TODO_DB_TYPE_ALLDAY);
            //fast input case input 00:00
            bundle.putLong(TodoStackContract.TodoEntry.TIME_FROM, TodoData.TIME_NOT_EXIST);
            bundle.putLong(TodoStackContract.TodoEntry.TIME_TO, TodoData.TIME_NOT_EXIST);
            bundle.putString(TodoStackContract.TodoEntry.LOCATION, "");
        } else if (isViewVisible(mControllerInputSubject)) {
            bundle.putString(TodoStackContract.SubjectEntry.SUBJECT_NAME,
                    mEditSubjectName.getText().toString());
            ColorTag colorTag = (ColorTag) mBtnSubjectColor.getTag();
            if (colorTag != null) {
                bundle.putInt(TodoStackContract.SubjectEntry.COLOR, colorTag.color);
                mBtnSubjectColor.setTag(null);
            } else {
                bundle.putInt(TodoStackContract.SubjectEntry.COLOR,
                        getResources().getColor(R.color.colorAccent));
            }

        } else if (isViewVisible(mControllerViewSubject)) {
            //TODO launch all todo fragment
        } else if (isViewVisible(mControllerViewTodo)) {
            bundle.putString(TodoStackContract.TodoEntry._ID,
                    ((TodoViewInfo) mTextTodos.getTag()).id);
        }
        return bundle;
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
            mMediator.clickBackPressSoftButton();
        }
    }

    private void getDateFromDatePicker() {
        final String dialogTag = TodoDatePickerDialog.class.getSimpleName();
        DialogFragment dialog =
                TodoDatePickerDialog.newInstance(new TodoDatePickerDialog.Callback() {
                    @Override
                    public void onDateSet(int year, int monthOfYear, int dayOfMonth) {
                        editYear.setText("" + year);
                        editMonth.setText("" + monthOfYear);
                        editDay.setText("" + dayOfMonth);
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    private void getColorFromColorDialog() {
        final String dialogTag = SubjectColorSelectDialog.class.getSimpleName();
        DialogFragment dialog =
                SubjectColorSelectDialog.newInstance(new SubjectColorSelectDialog.Callback() {
                    @Override
                    public void onSelectColor(int color) {
                        mBtnSubjectColor.setTextColor(color);
                        ColorTag colorTag = new ColorTag();
                        colorTag.color = color;
                        mBtnSubjectColor.setTag(colorTag);
                        setFabTheme(null, color);
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    private void editSubjectName() {
        final String dialogTag = SubjectNameUpdateDialog.class.getSimpleName();
        DialogFragment dialog =
                SubjectNameUpdateDialog.newInstance(new SubjectNameUpdateDialog.Callback() {
                    @Override
                    public void onEditName(String name) {
                        mMediator.changeSubjectName(name);
                    }
                });
        ((SubjectNameUpdateDialog) dialog).setOriginName(toolbarActionBar.getTitle().toString());
        dialog.show(getFragmentManager(), dialogTag);
    }

    private void editSubjectColor() {
        final String dialogTag = SubjectColorSelectDialog.class.getSimpleName();
        DialogFragment dialog =
                SubjectColorSelectDialog.newInstance(new SubjectColorSelectDialog.Callback() {
                    @Override
                    public void onSelectColor(int color) {
                        mMediator.changeSubjectColor(color);
                    }
                });
        dialog.show(getFragmentManager(), dialogTag);
    }

    @Override
    public void setActionBarText(String title, int color) {
        toolbarActionBar.setTitle(title);
        toolbarActionBar.setTitleTextColor(color);
        setSupportActionBar(toolbarActionBar);
    }

    @Override
    public void setNowBusy(boolean nowBusy) {
        mNowBusy = nowBusy;
    }

    @Override
    public boolean getNowBusy() {
        return mNowBusy;
    }



    @Override
    public void setTagOnTodoTextView(TodoViewInfo info) {
        if (mTextTodos != null) {
            mTextTodos.setTag(info);
        }
    }

    @Override
    public void setFab(String action, int color, boolean needMove) {
        if (needMove) {
            if (isViewVisible(mControllerInputTodo) ||
                    isViewVisible(mControllerInputSubject) ||
                    isViewVisible(mControllerViewSubject) ||
                    isViewVisible(mControllerViewTodo)) {
                setFabThemeWithMoveUp(action, color);
            }
        } else {
            setFabTheme(action, color);
        }
    }

    @Override
    public void setFabToBase(String action, int color, boolean needMove) {
        if (needMove) {
            setFabThemeWithMoveDown(action, color);
        } else {
            setFabTheme(action, color);
        }
    }

    public void setFabTheme(String action, int color) {
        if (action != null) {
        }
        Drawable bgButton = btnFab.getBackground();
        if (bgButton != null) {
            bgButton.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public void setFabThemeWithMoveUp(final String action, final int color) {
//        Log.d("TodoStack",
//                "[setFabThemeWithMoveUp] call! action = " + action + " / color = " + color);

        Animation animation =
                new TranslateAnimation(0, 0, 0, layoutFabBar.getTop() - btnFab.getTop());
        animation.setDuration(DURATION_ANIMATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setNowBusy(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) btnFab.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                btnFab.setLayoutParams(params);
                setFabTheme(action, color);
                setNowBusy(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        btnFab.startAnimation(animation);
    }

    public void setFabThemeWithMoveDown(final String action, final int color) {
        Animation animation =
                new TranslateAnimation(0, 0, 0, layoutFabBar.getBottom() - btnFab.getBottom());
        animation.setDuration(DURATION_ANIMATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setNowBusy(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) btnFab.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
                btnFab.setLayoutParams(params);
                setFabTheme(action, color);
                setNowBusy(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        btnFab.startAnimation(animation);
    }


    public boolean isViewVisible(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    @Override
    public void clearTodoLayout() {
        mTodoLayout.removeAllViews();
        //TODO need to be improve logic
        mMediator.refreshTodoLayout(mTodoLayout.getWidth(), mTodoLayout.getHeight());
    }

    @Override
    public void setTextViewsOnTodoLayout(ArrayList<TextView> alTextView) {
        for (TextView tv : alTextView) {
            mTodoLayout.addView(tv);
        }
    }

    @Override
    public void setTextViewOnTodoLayout(TextView textView) {
        mTodoLayout.addView(textView);
    }

    @Override
    public void refreshTodoLayout() {
        mTodoLayout.invalidate();
    }

    @Override
    public void setGuideText(String guideText) {
        setGuideText(guideText, getResources().getColor(R.color.color_normal_state));
    }

    @Override
    public void setGuideText(String guideText, int color) {
        mTextGuide.setText(guideText);
        mTextGuide.setTextColor(color);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    private void hideInputMethod(EditText edit) {
        InputMethodManager inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }



    //TODO change below to using TextViewInfo
    class ColorTag {
        int color;
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
