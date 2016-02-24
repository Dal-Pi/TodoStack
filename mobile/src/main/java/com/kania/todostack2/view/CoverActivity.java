package com.kania.todostack2.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    private LinearLayout llCover;
    private ProgressBar pbCover;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TodoStack", "[lifecycle][Cover] onCreate : " + this.hashCode());
        super.onCreate(savedInstanceState);

        mPresenter = new TodoStackCoverPresenter(this,
                new TodoStackCoverPresenter.ProgressFinishCallback() {
                    @Override
                    public void finishProgress() {
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.left_out);
                    }
                });

        setContentView(R.layout.activity_cover);

        llCover = (LinearLayout) findViewById(R.id.cover_ll_whole_cover);
        pbCover = (ProgressBar) findViewById(R.id.pb_cover);

        //from widget
        Intent intent = getIntent();
        if (intent != null) {
            mPresenter.setTodoIdFromWidget(intent);
            int bgColor = mPresenter.getCoverColorFromIntent(intent);
            setBackgroundColor(bgColor);
        } else {
            setBackgroundColor(ColorProvider.getInstance().getRandomColor());
        }

        mPresenter.notifyStartCoverProgress();
    }

    @Override
    public void onBackPressed() {
        //block backpress button for loading
    }

    public void setBackgroundColor(int color) {
        if (llCover == null)
            return;
        llCover.setBackgroundColor(color);
    }

    //for test


    @Override
    protected void onResume() {
        Log.i("TodoStack", "[lifecycle][Cover] onResume : " + this.hashCode());
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("TodoStack", "[lifecycle][Cover] onNewIntent : " + this.hashCode());
        super.onNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        Log.i("TodoStack", "[lifecycle][Cover] onDestroy : " + this.hashCode());
        super.onDestroy();
    }
}
