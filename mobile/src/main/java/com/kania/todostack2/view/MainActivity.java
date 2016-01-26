package com.kania.todostack2.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.presenter.IControllerMediator;
import com.kania.todostack2.presenter.TodoStackPresenter;
import com.kania.todostack2.provider.ColorProvider;
import com.kania.todostack2.util.TodoDatePickerDialog;

import java.util.ArrayList;

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn_fab:
                mediator.clickFloatingActionButton(getBundleFromVisibleView());
                break;
            case R.id.main_btn_input_calendar:
                getDateFromDatePicker();
                break;
        }
    }

    private Bundle getBundleFromVisibleView() {
        Bundle bundle = new Bundle();
        if (isViewVisible(controllerInputTodo)) {
            //TODO
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

    @Override
    public void setTextViewOnTodoLayout(ArrayList<TextView> alTextView) {
        for (TextView tv : alTextView) {
            todoLayout.addView(tv);
        }
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
}
