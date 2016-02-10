package com.kania.todostack2.view;

import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import android.widget.Toast;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.presenter.IControllerMediator;
import com.kania.todostack2.presenter.SubjectColorSelectDialog;
import com.kania.todostack2.presenter.TodoStackPresenter;
import com.kania.todostack2.util.SubjectNameUpdateDialog;
import com.kania.todostack2.util.TodoDatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends AppCompatActivity implements IViewAction, View.OnClickListener{

    private final int DURATION_ANIMATION = 500;

    private IControllerMediator mMediator;

    private Toolbar toolbarActionBar;
    private Button btnFab;

    private LinearLayout layoutControllerContainer;
    private RelativeLayout controllerInputTodo;
    private RelativeLayout controllerInputSubject;
    private LinearLayout controllerViewTodo;
    private LinearLayout controllerViewSubject;

    private RelativeLayout layoutFabBar;

    private EditText editYear;
    private EditText editMonth;
    private EditText editDay;
    private CheckBox checkTask;
    private EditText editTodoName;
    private Button btnCalendar;

    private EditText editSubjectName;
    private Button btnSubjectColor;

    private Button btnChangeSubjectName;
    private Button btnChangeSubjectcolor;
    private Button btnDeleteSubject;
    private Button btnMoveLeft;
    private Button btnMoveRight;



    private Button btnDone3;

    private TodoLayout todoLayout;

    private TextView textGuide;

    private boolean bViewClickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbarActionBar = (Toolbar) findViewById(R.id.main_layout_action_bar);
        setSupportActionBar(toolbarActionBar);

        mMediator = new TodoStackPresenter(this);
        mMediator.setMediator(this);

        initControlView();
    }

    private void initControlView() {
        btnFab = (Button) findViewById(R.id.main_btn_fab);
        btnFab.setOnClickListener(this);

        layoutControllerContainer = (LinearLayout) findViewById(R.id.main_layout_control_container);

        controllerInputTodo = (RelativeLayout) findViewById(R.id.main_layout_todo_input_mode);
        controllerInputSubject = (RelativeLayout) findViewById(R.id.main_layout_subject_input_mode);
        controllerViewTodo = (LinearLayout) findViewById(R.id.main_layout_todo_viewer_mode);
        controllerViewSubject = (LinearLayout) findViewById(R.id.main_layout_subject_viewer_mode);

        layoutFabBar = (RelativeLayout) findViewById(R.id.main_layout_fab_bar);

        editYear = (EditText) findViewById(R.id.main_edit_input_year);
        editMonth = (EditText) findViewById(R.id.main_edit_input_month);
        editDay = (EditText) findViewById(R.id.main_edit_input_day);
        checkTask = (CheckBox) findViewById(R.id.main_cb_input_task);
        editTodoName = (EditText) findViewById(R.id.main_edit_input_todo_name);
        btnCalendar = (Button) findViewById(R.id.main_btn_input_calendar);
        btnCalendar.setOnClickListener(this);

        editSubjectName = (EditText) findViewById(R.id.main_edit_input_subject_name);
        btnSubjectColor = (Button) findViewById(R.id.main_btn_input_subject_color);
        btnSubjectColor.setOnClickListener(this);

        btnChangeSubjectName = (Button) findViewById(R.id.main_btn_edit_subject_name);
        btnChangeSubjectName.setOnClickListener(this);
        btnChangeSubjectcolor = (Button) findViewById(R.id.main_btn_edit_subject_color);
        btnChangeSubjectcolor.setOnClickListener(this);
        btnDeleteSubject = (Button) findViewById(R.id.main_btn_subject_delete);
        btnDeleteSubject.setOnClickListener(this);
        btnMoveLeft = (Button) findViewById(R.id.main_btn_subject_left);
        btnMoveLeft.setOnClickListener(this);
        btnMoveRight = (Button) findViewById(R.id.main_btn_subject_right);
        btnMoveRight.setOnClickListener(this);

        btnDone3 = (Button) findViewById(R.id.main_btn_viewer_info_3_or_more);

        todoLayout = (TodoLayout) findViewById(R.id.main_vg_todo_layout);
        todoLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                todoLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mMediator.initTodoLayout(todoLayout.getWidth(), todoLayout.getHeight());
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
                mMediator.selectMenuAddSubject();
                break;
            case R.id.action_settings:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (bViewClickable) {
            switch (v.getId()) {
                case R.id.main_btn_fab:
                    if (checkVaildData()) {
                        mMediator.clickFloatingActionButton(getBundleFromVisibleLayout());
                    }
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
                    //TODO implement after add removing Todos
                    break;
                case R.id.main_btn_subject_left:
                    mMediator.moveSubjectOrder(true);
                    break;
                case R.id.main_btn_subject_right:
                    mMediator.moveSubjectOrder(false);
                    break;
            }
        }
    }

    private boolean checkVaildData() {
        if (isViewVisible(controllerInputTodo)) {
            if (!checkVaildTodoDate())
                return false;
            String name = editTodoName.getText().toString();
            if (!checkVaildName(name))
                return false;
        }
        if (isViewVisible(controllerInputSubject)) {
            String name = editSubjectName.getText().toString();
            if (!checkVaildName(name))
                return false;
        }
        return true;
    }

    private boolean checkVaildTodoDate() {
        boolean ret = false;
        String year = editYear.getText().toString();
        String month = editMonth.getText().toString();
        String day = editDay.getText().toString();
        if("".equals(year) || "".equals(month) || "".equals(day)){
            Toast.makeText(this, "There is a empty space.", Toast.LENGTH_SHORT).show();
        }
        else{
            int nYearCheck = Integer.parseInt(year);
            int nMonthCheck = Integer.parseInt(month);
            int nDateCheck = Integer.parseInt(day);

            if (nYearCheck > 0) {
                int nRangeDate = -1;
                switch (nMonthCheck) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        nRangeDate = 31;
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        nRangeDate = 30;
                        break;
                    case 2:
                        if( ( (nYearCheck%4 == 0) && (nYearCheck%100 != 0) ) || (nYearCheck%400 == 0) ){
                            nRangeDate = 29;
                        }
                        else{
                            nRangeDate = 28;
                        }
                        break;
                    default:
                        Toast.makeText(this, "Do not execute! Input vaild Month(1 ~ 12)", Toast.LENGTH_SHORT).show();
                        break;
                }
                if( (nDateCheck > 0) && (nDateCheck <= nRangeDate) ){
                    ret = true;
                } else {
                    Toast.makeText(this, "Do not execute! Input vaild Day(1 ~ last day each month)", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Do not execute! Input vaild Year.", Toast.LENGTH_SHORT).show();
            }
        }

        return ret;
    }

    private boolean checkVaildName(String name) {
        boolean ret;
        if (!"".equalsIgnoreCase(name.trim())) {
            ret = true;
        } else {
            ret = false;
            Toast.makeText(this, "Do not execute! Name is empty.", Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    private Bundle getBundleFromVisibleLayout() {
        Bundle bundle = new Bundle();
        if (isViewVisible(controllerInputTodo)) {
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
                    editSubjectName.getText().toString());
            ColorTag colorTag = (ColorTag) btnSubjectColor.getTag();
            if (colorTag != null) {
                bundle.putInt(TodoStackContract.SubjectEntry.COLOR, colorTag.color);
                btnSubjectColor.setTag(null);
            } else {
                bundle.putInt(TodoStackContract.SubjectEntry.COLOR,
                        getResources().getColor(R.color.colorAccent));
            }

        }
        return bundle;
    }

    @Override
    public void onBackPressed() {
        mMediator.clickBackPressSoftButton();
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
                        btnSubjectColor.setTextColor(color);
                        ColorTag colorTag = new ColorTag();
                        colorTag.color = color;
                        btnSubjectColor.setTag(colorTag);
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
    public void setViewClickEnable(boolean enable) {
        bViewClickable = enable;
    }

    @Override
    public void setAllControllerGone() {
        if (controllerInputTodo.getVisibility() == View.VISIBLE) {
            editTodoName.setText("");
            checkTask.setChecked(false);
            hideInputMethod(editTodoName);
        } else if (controllerInputSubject.getVisibility() == View.VISIBLE) {
            editSubjectName.setText("");
            hideInputMethod(editSubjectName);
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
            //present today
            Calendar calendar = Calendar.getInstance();
            String year = "" + calendar.get(Calendar.YEAR);
            editYear.setText(year);
            String month = "" + (calendar.get(Calendar.MONTH) + 1);
            editMonth.setText(month);
            String day = "" + calendar.get(Calendar.DAY_OF_MONTH);
            editDay.setText(day);
        }
        btnCalendar.setTextColor(color);
        checkTask.setTextColor(color);
    }

    @Override
    public void setInputSubjectVisible() {
        if (controllerInputSubject.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            controllerInputSubject.setVisibility(View.VISIBLE);
            btnSubjectColor.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    public void setViewSubjectVisible(int color, boolean leftEnable, boolean RightEnable) {
        if (controllerViewSubject.getVisibility() != View.VISIBLE) {
            setAllControllerGone();
            controllerViewSubject.setVisibility(View.VISIBLE);
        }
        btnChangeSubjectName.setTextColor(color);
        btnChangeSubjectcolor.setTextColor(color);
        btnDeleteSubject.setTextColor(color);
        btnMoveLeft.setEnabled(leftEnable);
        btnMoveLeft.setTextColor(
                leftEnable ? color : getResources().getColor(R.color.color_lightgray));
        btnMoveRight.setEnabled(RightEnable);
        btnMoveRight.setTextColor(
                RightEnable ? color : getResources().getColor(R.color.color_lightgray));
    }

    @Override
    public void setFab(String action, int color, boolean needMove) {
        if (needMove) {
            if (isViewVisible(controllerInputTodo) ||
                    isViewVisible(controllerInputSubject) ||
                    isViewVisible(controllerViewSubject)) {
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
            btnFab.setText(action);
        }
        Drawable bgButton = btnFab.getBackground();
        if (bgButton != null) {
            bgButton.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public void setFabThemeWithMoveUp(final String action, final int color) {
        Log.d("TodoStack",
                "[setFabThemeWithMoveUp] call! action = " + action + " / color = " + color);
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
        mMediator.refreshTodoLayout(todoLayout.getWidth(), todoLayout.getHeight());
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

    class ColorTag {
        int color;
    }
}
