package com.kania.todostack2.view;

import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.presenter.IControllerMediator;
import com.kania.todostack2.presenter.TodoStackPresenter;
import com.kania.todostack2.provider.ColorProvider;
import com.kania.todostack2.util.TodoDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends AppCompatActivity implements IViewAction, View.OnClickListener{

    private final int DURATION_ANIMATION = 500;

    private IControllerMediator mediator;

    private Toolbar toolbarActionBar;
    private Button btnFab;

    private LinearLayout layoutControllerContainer;
    private RelativeLayout controllerInputTodo;
    private RelativeLayout controllerInputSubject;
    private LinearLayout controllerViewTodo;
    private RelativeLayout controllerViewSubject;

    private RelativeLayout layoutFabBar;

    private EditText editYear;
    private EditText editMonth;
    private EditText editDay;
    private CheckBox checkTask;
    private EditText editTodoName;

    private Button btnCalendar;

    private EditText editSubject;

    private Button btnDone3;

    private TodoLayout todoLayout;

    private TextView textGuide;

    private int fabOriginLocationLeft;
    private int fabOriginLocationTop;
    private int fabOriginLocationRight;
    private int fabOriginLocationBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbarActionBar = (Toolbar) findViewById(R.id.main_layout_action_bar);
        setSupportActionBar(toolbarActionBar);

        mediator = new TodoStackPresenter(this);
        mediator.setMediator(this);

        initControlView();
    }

    private void initControlView() {
        btnFab = (Button) findViewById(R.id.main_btn_fab);
        btnFab.setOnClickListener(this);

        layoutControllerContainer = (LinearLayout) findViewById(R.id.main_layout_control_container);

        controllerInputTodo = (RelativeLayout) findViewById(R.id.main_layout_todo_input_mode);
        controllerInputSubject = (RelativeLayout) findViewById(R.id.main_layout_subject_input_mode);
        controllerViewTodo = (LinearLayout) findViewById(R.id.main_layout_todo_viewer_mode);
        controllerViewSubject = (RelativeLayout) findViewById(R.id.main_layout_subject_viewer_mode);

        layoutFabBar = (RelativeLayout) findViewById(R.id.main_layout_fab_bar);

        editYear = (EditText) findViewById(R.id.main_edit_input_year);
        editMonth = (EditText) findViewById(R.id.main_edit_input_month);
        editDay = (EditText) findViewById(R.id.main_edit_input_day);
        checkTask = (CheckBox) findViewById(R.id.main_cb_input_task);
        editTodoName = (EditText) findViewById(R.id.main_edit_input_todo_name);
        btnCalendar = (Button) findViewById(R.id.main_btn_input_calendar);
        btnCalendar.setOnClickListener(this);

        editSubject = (EditText) findViewById(R.id.main_edit_input_subject_name);

        btnDone3 = (Button) findViewById(R.id.main_btn_viewer_info_3_or_more);

        todoLayout = (TodoLayout) findViewById(R.id.main_vg_todo_layout);
        todoLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                todoLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mediator.initTodoLayout(todoLayout.getWidth(), todoLayout.getHeight());
            }
        });
        textGuide = (TextView) findViewById(R.id.main_text_guide_text);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.add_subject:
                mediator.selectMenuAddSubject();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_fab:
                mediator.clickFloatingActionButton(getBundleFromVisibleLayout());
                break;
            case R.id.main_btn_input_calendar:
                getDateFromDatePicker();
                break;
        }
    }

    private Bundle getBundleFromVisibleLayout() {
        Bundle bundle = new Bundle();
        if (isViewVisible(controllerInputTodo)) {
            //TODO
            bundle.putString(TodoStackContract.TodoEntry.TODO_NAME,
                    editTodoName.getText().toString());
            //subject order set on presenter
            String dateString = String.format("%4s%2s%2s", editYear.getText().toString(),
                    editMonth.getText().toString(), editDay.getText().toString());
            bundle.putString(TodoStackContract.TodoEntry.DATE, dateString);
            bundle.putInt(TodoStackContract.TodoEntry.TYPE,
                    checkTask.isChecked() ?
                            TodoData.TODO_DB_TYPE_TASK : TodoData.TODO_DB_TYPE_ALLDAY);
            //fast input case input 00:00
            bundle.putString(TodoStackContract.TodoEntry.TIME_FROM,
                    TodoStackContract.TodoEntry.DATEFORMAT_TIME_DEFAULT);
            bundle.putString(TodoStackContract.TodoEntry.TIME_TO,
                    TodoStackContract.TodoEntry.DATEFORMAT_TIME_DEFAULT);
            bundle.putString(TodoStackContract.TodoEntry.LOCATION, "");
        }
        if (isViewVisible(controllerInputSubject)) {
            bundle.putString(TodoStackContract.SubjectEntry.SUBJECT_NAME,
                    editSubject.getText().toString());
            //TODO add about color
            bundle.putInt(TodoStackContract.SubjectEntry.COLOR,
                    ColorProvider.getInstance().getRandomColor());
        }
        return bundle;
    }

    @Override
    public void onBackPressed() {
        mediator.clickBackPressSoftButton();
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

    @Override
    public void setActionBarText(String title, int color) {
        toolbarActionBar.setTitle(title);
        toolbarActionBar.setTitleTextColor(color);
        setSupportActionBar(toolbarActionBar);
    }

    @Override
    public void setAllControllerGone() {
        if (controllerInputTodo.getVisibility() == View.VISIBLE) {
            editTodoName.setText("");
            hideInputMethod(editTodoName);
        } else if (controllerInputSubject.getVisibility() == View.VISIBLE) {
            editSubject.setText("");
            checkTask.setChecked(false);
            hideInputMethod(editSubject);
        }
        controllerInputTodo.setVisibility(View.GONE);
        controllerInputSubject.setVisibility(View.GONE);
        controllerViewTodo.setVisibility(View.GONE);
        controllerViewSubject.setVisibility(View.GONE);
    }

    @Override
    public void setInputTodoVisible(int color) {
        if (controllerInputTodo.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            controllerInputTodo.setVisibility(View.VISIBLE);
        }
        //present today
        Calendar calendar = Calendar.getInstance();
        String year = "" + calendar.get(Calendar.YEAR);
        editYear.setText(year);
        String month = "" + (calendar.get(Calendar.MONTH) + 1);
        editMonth.setText(month);
        String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
        editDay.setText(day);
        btnCalendar.setTextColor(color);
        checkTask.setTextColor(color);
    }

    @Override
    public void setInputSubjectVisible() {
        if (controllerInputSubject.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            controllerInputSubject.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setFabToInputTodo(String action, int color, boolean needMove) {
        if (needMove) {
            if (isViewVisible(controllerInputTodo)) {
                setFabThemeWithMoveUp(action, color);
            }
        } else {
            setFabTheme(action, color);
        }
    }

    @Override
    public void setFabToInputSubject(String action, int color, boolean needMove) {
        if (needMove) {
            if (isViewVisible(controllerInputSubject)) {
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
        btnFab.setText(action);
        Drawable bgButton = btnFab.getBackground();
        if (bgButton != null) {
            bgButton.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public void setFabThemeWithMoveUp(final String action, final int color) {
        Animation animation =
                new TranslateAnimation(0, 0, 0, layoutFabBar.getTop() - btnFab.getTop());
        animation.setDuration(DURATION_ANIMATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btnFab.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnFab.setClickable(true);
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) btnFab.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 1);
                btnFab.setLayoutParams(params);
                setFabTheme(action, color);
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
                btnFab.setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnFab.setClickable(true);
                RelativeLayout.LayoutParams params =
                        (RelativeLayout.LayoutParams) btnFab.getLayoutParams();
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 1);
                btnFab.setLayoutParams(params);
                setFabTheme(action, color);
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
        todoLayout.removeAllViews();
        //TODO need to be improve logic
        mediator.refreshTodoLayout(todoLayout.getWidth(), todoLayout.getHeight());
    }

    @Override
    public void setTextViewsOnTodoLayout(ArrayList<TextView> alTextView) {
        for (TextView tv : alTextView) {
            todoLayout.addView(tv);
        }
    }

    @Override
    public void setTextViewOnTodoLayout(TextView textView) {
        todoLayout.addView(textView);
    }

    @Override
    public void refreshTodoLayout() {
        todoLayout.invalidate();
    }

    @Override
    public void setGuideText(String guideText) {
        setGuideText(guideText, getResources().getColor(R.color.color_normal_state));
    }

    @Override
    public void setGuideText(String guideText, int color) {
        textGuide.setText(guideText);
        textGuide.setTextColor(color);
    }

    @Override
    public void finishActivity() {
        finish();
    }

    private void hideInputMethod(EditText edit) {
        InputMethodManager inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edit.getWindowToken(),0);
    }
}
