package com.kania.todostack2.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
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
import com.kania.todostack2.presenter.LoadingTodoTask;
import com.kania.todostack2.presenter.UpdateTodoTask;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.util.TodoDoneDialog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 2016-02-23.
 */
public class EachTabFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static final int SORT_ORDER_CREATE = 1;
    public static final int SORT_ORDER_UPDATE = 2;
    public static final int SORT_ORDER_DUEDATE = 3;

    public final String TAG_DIALOG_DONE_TODO = "done_todo";

    private RecyclerView mRecyclerView;
    private List<TodoData> mTodoList;
    private DetailTodoListAdapter mAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.SimpleCallback mSimpleItemTouchCallback;

    private int mSortOrder = SORT_ORDER_CREATE;

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

        sortTodoList(mSortOrder);
        mAdapter = new DetailTodoListAdapter(getActivity(), mTodoList);
        mRecyclerView.setAdapter(mAdapter);

        setTodoCardCallback();

        return rootView;
    }

    private void setTodoCardCallback() {
        mSimpleItemTouchCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                      RecyclerView.ViewHolder target) {
                    return false;
                }
                @Override
                public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    showTodoDoneDialog(viewHolder);
                }
            };
        mItemTouchHelper = new ItemTouchHelper(mSimpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showTodoDoneDialog(final RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final TodoData td = mAdapter.getItem(position);

        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        DialogFragment dialog = TodoDoneDialog.newInstance(td.id, new TodoDoneDialog.Callback() {
            @Override
            public void onEditTodo(TodoData editedTodo) {
                updateTodo(viewHolder, editedTodo);
            }

            @Override
            public void onDeleteTodo(int id) {
                deleteTodo(viewHolder);
            }
            @Override
            public void onMoveTodo(int id, int moveType) {
                moveTodo(viewHolder, moveType);
            }
            @Override
            public void onCancelSelected() {
                mAdapter.notifyDataSetChanged();
                mSimpleItemTouchCallback.clearView(mRecyclerView, viewHolder);
            }
        });
        dialog.show(ft, TAG_DIALOG_DONE_TODO);
    }

    private void updateTodo(final RecyclerView.ViewHolder viewHolder, TodoData editedTodo) {
        UpdateTodoTask updateTodoTask = new UpdateTodoTask(getActivity(),
                new UpdateTodoTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        LoadingTodoTask refreshTask = new LoadingTodoTask(
                                getActivity(), new LoadingTodoTask.TaskEndCallback() {
                            @Override
                            public void loadFinished() {
                                sortTodoList(mSortOrder);
                                mAdapter.notifyDataSetChanged();
                                mSimpleItemTouchCallback.clearView(mRecyclerView, viewHolder);
                            }
                        });
                        refreshTask.execute();
                    }
                });
        updateTodoTask.setData(editedTodo, UpdateTodoTask.TODO_TASK_UPDATE_TODO);
        updateTodoTask.execute();
    }

    private void deleteTodo(final RecyclerView.ViewHolder viewHolder) {
        final int position = viewHolder.getAdapterPosition();
        final TodoData td = mAdapter.getItem(position);

        UpdateTodoTask deleteTodoTask = new UpdateTodoTask(getActivity(),
                new UpdateTodoTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        LoadingTodoTask refreshTask = new LoadingTodoTask(
                                getActivity(), new LoadingTodoTask.TaskEndCallback() {
                            @Override
                            public void loadFinished() {
                                mAdapter.removeItem(position);
                                mAdapter.notifyItemRemoved(position);
                            }
                        });
                        refreshTask.execute();
                    }
                });
        deleteTodoTask.setData(td, UpdateTodoTask.TODO_TASK_DELETE_TODO);
        deleteTodoTask.execute();
    }

    private void moveTodo(final RecyclerView.ViewHolder viewHolder, int moveType) {
        final int position = viewHolder.getAdapterPosition();
        final TodoData td = mAdapter.getItem(position);

        UpdateTodoTask moveTodoTask = new UpdateTodoTask(getActivity(),
                new UpdateTodoTask.TaskEndCallback() {
                    @Override
                    public void updateFinished() {
                        LoadingTodoTask refreshTask = new LoadingTodoTask(
                                getActivity(), new LoadingTodoTask.TaskEndCallback() {
                            @Override
                            public void loadFinished() {
                                sortTodoList(mSortOrder);
                                mAdapter.notifyDataSetChanged();
                                mSimpleItemTouchCallback.clearView(mRecyclerView, viewHolder);
                            }
                        });
                        refreshTask.execute();
                    }
                });
        moveTodoTask.setData(td, moveType);
        moveTodoTask.execute();
    }

    public void setSortOrder(int sortOrder) {
        mSortOrder = sortOrder;
        sortTodoList(mSortOrder);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void sortTodoList(int sortOrder) {
        Comparator<TodoData> comparator = null;
        switch (sortOrder) {
            case SORT_ORDER_CREATE:
                //be first if it made last
                comparator = new Comparator<TodoData>() {
                    @Override
                    public int compare(TodoData lhs, TodoData rhs) {
                        if (lhs.created > rhs.created)
                            return -1;
                        else if (lhs.created < rhs.created)
                            return 1;
                        else
                            return 0;
                    }
                };
                break;
            case SORT_ORDER_UPDATE:
                //be first if it made last
                comparator = new Comparator<TodoData>() {
                    @Override
                    public int compare(TodoData lhs, TodoData rhs) {
                        if (lhs.lastUpdated > rhs.lastUpdated)
                            return -1;
                        else if (lhs.lastUpdated < rhs.lastUpdated)
                            return 1;
                        else
                            return 0;
                    }
                };
                break;
            case SORT_ORDER_DUEDATE:
                //be first if it made first
                comparator = new Comparator<TodoData>() {
                    @Override
                    public int compare(TodoData lhs, TodoData rhs) {
                        if (lhs.date < rhs.date)
                            return -1;
                        else if (lhs.date > rhs.date)
                            return 1;
                        else
                            return 0;
                    }
                };
                break;
        }
        if (comparator != null) {
            Collections.sort(mTodoList, comparator);
        }
    }
}

