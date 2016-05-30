package com.kania.todostack2.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.presenter.DetailTodoListPresenter;

import java.util.ArrayList;

/**
 * Created by user on 2016-02-23.
 */
public class DetailTodoListActivity extends AppCompatActivity {

    public static final String START_PAGE = "start_page";
    public static final String START_PAGE_ORDER = "start_page_order";

    public static final String TAB_TITLE_ALL = "All";
    public static final String TAB_TITLE_DATE = "Todo";
    public static final String TAB_TITLE_TASK = "Task";


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Toolbar mToolbar;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private int mSubjectOrder;
    private int mStartPage;
    private int mStartPageOrder;

    private DetailTodoListPresenter mPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_todolist);

        Intent fromIntent = getIntent();
        mSubjectOrder = fromIntent.getIntExtra(TodoStackContract.SubjectEntry.ORDER,
                DetailTodoListPresenter.SUBJECT_ORDER_ALL);
        mStartPage = fromIntent.getIntExtra(START_PAGE, DetailTodoListPresenter.TODO_TYPE_ALL);
        mStartPageOrder = fromIntent.getIntExtra(START_PAGE_ORDER, EachTabFragment.SORT_ORDER_CREATE);

        mPresent = new DetailTodoListPresenter(this, mSubjectOrder);

        initController();

        setSubjectInfo(mPresent.getSubjectDataFromOrder(mSubjectOrder));

    }

    private void initController() {
        mToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.detail_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSectionsPagerAdapter.setSortOrder(mStartPageOrder);
        mViewPager.setCurrentItem(mSectionsPagerAdapter.getPositionByType(mStartPage));

        mTabLayout = (TabLayout) findViewById(R.id.detail_tabs);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void setSubjectInfo(SubjectData subject) {
        if (mToolbar != null) {
            mToolbar.setTitle(subject.subjectName);
            mToolbar.setBackgroundColor(subject.color);
            setSupportActionBar(mToolbar);
        }
        if (mTabLayout != null) {
            mTabLayout.setBackgroundColor(subject.color);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.right_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO need to disable Sort by DueDate when All or Task
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_overflow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.right_out);
                return true;
            case R.id.menu_order:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setSingleChoiceItems(getResources().getStringArray(R.array.sort_order),
                        -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mSectionsPagerAdapter.setSortOrder(EachTabFragment.SORT_ORDER_CREATE);
                        }
                        else if (which == 1) {
                            mSectionsPagerAdapter.setSortOrder(EachTabFragment.SORT_ORDER_UPDATE);
                        }
                        else if (which == 2) {
                            mSectionsPagerAdapter.setSortOrder(EachTabFragment.SORT_ORDER_DUEDATE);
                        }
                        dialog.dismiss();
                    }
                });
                (builder.create()).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        ArrayList<EachTabFragment> pages;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            pages = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            EachTabFragment page = EachTabFragment.newInstance(position,
                    mPresent.getTodoList(mSubjectOrder, getPageListType(position)));
            pages.add(page);
            return page;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return TAB_TITLE_ALL;
                case 1:
                    return TAB_TITLE_DATE;
                case 2:
                    return TAB_TITLE_TASK;
            }
            return null;
        }

        public int getPageListType(int position) {
            switch (position) {
                case 0:
                    return DetailTodoListPresenter.TODO_TYPE_ALL;
                case 1:
                    return DetailTodoListPresenter.TODO_TYPE_ALLDAY;
                case 2:
                    return DetailTodoListPresenter.TODO_TYPE_TASK;
            }
            return DetailTodoListPresenter.TODO_TYPE_ALL;
        }

        public int getPositionByType(int type) {
            switch (type) {
                case DetailTodoListPresenter.TODO_TYPE_ALL:
                    return 0;
                case DetailTodoListPresenter.TODO_TYPE_ALLDAY:
                    return 1;
                case DetailTodoListPresenter.TODO_TYPE_TASK:
                    return 2;
            }
            return 0;
        }

        public void setSortOrder(int sortOrder) {
            for (EachTabFragment page : pages) {
                page.setSortOrder(sortOrder);
            }
        }
    }
}
