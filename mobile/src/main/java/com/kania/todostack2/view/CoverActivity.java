package com.kania.todostack2.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.kania.todostack2.R;
import com.kania.todostack2.presenter.TodoStackCoverPresenter;
import com.kania.todostack2.provider.ColorProvider;

/**
 * Created by user on 2016-01-10.
 */
public class CoverActivity extends Activity{

    private TodoStackCoverPresenter mPresenter;
    private ColorProvider colorProvider;

    private LinearLayout llCover;
    private ProgressBar pbCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cover);

        colorProvider = ColorProvider.getInstance();

        llCover = (LinearLayout) findViewById(R.id.cover_ll_whole_cover);
        pbCover = (ProgressBar) findViewById(R.id.pb_cover);

        setBackgroundColor();

        mPresenter = new TodoStackCoverPresenter(this,
                new TodoStackCoverPresenter.ProgressFinishCallback() {
            @Override
            public void finishProgress() {
                finish();
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
            }
        });
        mPresenter.notifyStartCoverProgress();
    }

    public void setBackgroundColor() {
        if (llCover == null)
            return;
        int color = colorProvider.getRandomColor();
        llCover.setBackgroundColor(color);
    }
}
