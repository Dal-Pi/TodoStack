package com.kania.todostack2.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.data.TodoData;

import java.util.List;

/**
 * Created by user on 2016-02-23.
 */
public class EachTabFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView mRecyclerView;
    private List<TodoData> mTodoList;
    private DetailTodoListAdapter mAdapter;

    public EachTabFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EachTabFragment newInstance(int sectionNumber, List<TodoData> todoList) {
        EachTabFragment fragment = new EachTabFragment();
        fragment.setData(todoList);
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    private void setData(List<TodoData> todoList) {
        this.mTodoList = todoList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todolist, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.todolist_recycler);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new DetailTodoListAdapter(getActivity(), mTodoList);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        return false;
                    }
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        //TODO swipe action needed
                        mAdapter.notifyDataSetChanged();
                        clearView(mRecyclerView, viewHolder);
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        return rootView;
    }
}

