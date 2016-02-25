package com.kania.todostack2.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.provider.TodoProvider;

import java.util.List;

/**
 * Created by user on 2016-02-25.
 */
public class DetailTodoListAdapter
        extends RecyclerView.Adapter<DetailTodoListAdapter.TodoCardHolder> {

    private Context mContext;

    private TodoProvider mProvider;
    private List<TodoData> mTodoList;

    public DetailTodoListAdapter(Context context, List<TodoData> todoList) {
        this.mContext = context;
        this.mTodoList = todoList;
        mProvider = TodoProvider.getInstance(mContext);
    }

    public static class TodoCardHolder extends RecyclerView.ViewHolder {
        public LinearLayout cardLayout;
        public TextView subject;
        public TextView todoName;
        //TODO need more option

        public TodoCardHolder(View itemView) {
            super(itemView);
            cardLayout = (LinearLayout) itemView.findViewById(R.id.card_item_layout);
            subject = (TextView) itemView.findViewById(R.id.card_item_subject);
            todoName = (TextView) itemView.findViewById(R.id.card_item_todoname);
            //TODO setOnClick
        }
    }

    @Override
    public TodoCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_detail_todo_card, parent, false);

        return new TodoCardHolder(view);
    }

    @Override
    public void onBindViewHolder(TodoCardHolder holder, int position) {
        TodoData td = mTodoList.get(position);
        SubjectData sd = mProvider.getSubjectByOrder(td.subjectOrder);

//        holder.cardLayout.setBackgroundColor(sd.color);
        holder.subject.setText(sd.subjectName);
        holder.subject.setBackgroundColor(sd.color);
        holder.todoName.setText(td.todoName);
    }

    @Override
    public int getItemCount() {
        if (mTodoList != null) {
            return mTodoList.size();
        } else {
            return 0;
        }
    }


}
