package com.kania.todostack2.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kania.todostack2.R;
import com.kania.todostack2.presenter.IControllerMediator;
import com.kania.todostack2.presenter.TodoStackPresenter;
import com.kania.todostack2.util.TodoDatePickerDialog;

/**
 * Created by user on 2016-01-14.
 * Activity that present all todos
 */
public class MainActivity extends Activity implements IViewAction, View.OnClickListener{

    private final int DURATION_ANIMATION = 500;

    private IControllerMediator mediator;

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

    private Button btnCalendar;

    private Button btnDone3;

    private FrameLayout todoLayout;

    private int fabOriginLocationLeft;
    private int fabOriginLocationTop;
    private int fabOriginLocationRight;
    private int fabOriginLocationBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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
        editYear.setOnClickListener(this);
        editMonth = (EditText) findViewById(R.id.main_edit_input_month);
        editMonth.setOnClickListener(this);
        editDay = (EditText) findViewById(R.id.main_edit_input_day);
        editDay.setOnClickListener(this);

        btnCalendar = (Button) findViewById(R.id.main_btn_input_calendar);
        btnCalendar.setOnClickListener(this);

        btnDone3 = (Button) findViewById(R.id.main_btn_viewer_info_3_or_more);

        todoLayout = (FrameLayout) findViewById(R.id.main_lf_todo_layout);
        mediator.initTodoLayout(todoLayout.getWidth(), todoLayout.getHeight());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_fab:
                mediator.clickFloatingActionButton();
                break;
            case R.id.main_btn_input_calendar:
                getDateFromDatePicker();
                break;
        }
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
    public void setAllControllerGone() {
        controllerInputTodo.setVisibility(View.GONE);
        controllerInputSubject.setVisibility(View.GONE);
        controllerViewTodo.setVisibility(View.GONE);
        controllerViewSubject.setVisibility(View.GONE);
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
                setFabThemWithMoveUp(action, color);
            }
        } else {
            setFabTheme(action, color);
        }
    }

    @Override
    public void setFabToInputSubject(String action, int color, boolean needMove) {
        if (needMove) {
            if (isViewVisible(controllerInputSubject)) {
                setFabThemWithMoveUp(action, color);
            }
        } else {
            setFabTheme(action, color);
        }
    }

    @Override
    public void setFabToBase(String action, int color, boolean needMove) {
        if (needMove) {
            setFabThemWithMoveDown(action, color);
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

    public void setFabThemWithMoveUp(final String action, final int color) {
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

    public void setFabThemWithMoveDown(final String action, final int color) {
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


//    @Override
//    public void setMode(int mode) {
//        //checking
//        if (controllerInputTodo == null
//                || controllerInputSubject == null
//                || controllerViewTodo == null
//                || controllerViewSubject == null) {
//            Log.e("TodoStack", "there is(are) null controller");
//            return;
//        }
//        setEachControllerVisibility(controllerInputTodo, MODE_ADD_TODO, mode);
//        setEachControllerVisibility(controllerInputSubject, MODE_ADD_SUBJECT, mode);
//        setEachControllerVisibility(controllerViewTodo, MODE_VIEW_TODO, mode);
//        setEachControllerVisibility(controllerViewSubject, MODE_VIEW_SUBJECT, mode);
//    }
//
//    private void setEachControllerVisibility(View view ,int ownMode, int targetMode){
//        boolean isVisible = (view.getVisibility() == View.VISIBLE);
//        boolean isTarget = (ownMode == targetMode);
//
//        if (isVisible && !isTarget) {
//            view.setVisibility(View.INVISIBLE);
//        } else if (!isVisible && isTarget) {
//            view.setVisibility(View.VISIBLE);
//        }
//    }


    @Override
    public void finishActivity() {
        finish();
    }
}
