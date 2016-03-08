package com.kania.todostack2.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.TodoStackContract;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.util.TodoStackUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        public LinearLayout mCardLayout;
        public LinearLayout mTodoNameLayout;
        public TextView mTodoName;
        public TextView mSubject;
        public TableRow mDueDateRow;
        public TextView mDueDate;
        public TableRow mTimeRow;
        public TextView mTime;
        public TableRow mLocationRow;
        public TextView mLocation;
        public TextView mCreated;
        public TextView mLastUpdated;

        public int id;
        //TODO need more option

        public TodoCardHolder(View itemView) {
            super(itemView);
            mCardLayout = (LinearLayout) itemView.findViewById(R.id.card_item_layout);
            mTodoNameLayout = (LinearLayout) itemView.findViewById(R.id.card_item_todoname_layout);
            mTodoName = (TextView) itemView.findViewById(R.id.card_item_todoname);
            mSubject = (TextView) itemView.findViewById(R.id.card_item_subject);
            mDueDateRow = (TableRow) itemView.findViewById(R.id.card_item_due_date_row);
            mDueDate = (TextView) itemView.findViewById(R.id.card_item_due_date);
            mTimeRow = (TableRow) itemView.findViewById(R.id.card_item_time_row);
            mTime = (TextView) itemView.findViewById(R.id.card_item_time);
            mLocationRow = (TableRow) itemView.findViewById(R.id.card_item_location_row);
            mLocation = (TextView) itemView.findViewById(R.id.card_item_location);
            mCreated = (TextView) itemView.findViewById(R.id.card_item_created);
            mLastUpdated = (TextView) itemView.findViewById(R.id.card_item_last_updated);
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
        TodoData td = mProvider.getTodoById(mTodoList.get(position).id);
        SubjectData sd = mProvider.getSubjectByOrder(td.subjectOrder);

        holder.mTodoNameLayout.setBackgroundColor(sd.color);
        holder.mTodoName.setText(td.todoName);

        holder.mSubject.setText(sd.subjectName);
        holder.mSubject.setTextColor(sd.color);

        if (td.type == TodoData.TODO_DB_TYPE_TASK) {
            //TODO hide row
//            holder.mDueDate.setText("(it is task)");
            holder.mDueDateRow.setVisibility(View.GONE);
            holder.mTimeRow.setVisibility(View.GONE);
        } else {
            Calendar targetDate = Calendar.getInstance();
            targetDate.setTime(TodoStackUtil.getDateFromTodoDate(td.date));
            holder.mDueDateRow.setVisibility(View.VISIBLE);
            holder.mDueDate.setText(TodoStackUtil.getFomatedDate(mContext, targetDate.getTime()));

            if ("".equalsIgnoreCase(td.timeFrom)) {
                //TODO hide row
//                holder.mTime.setText("(time is empty)");
                holder.mTimeRow.setVisibility(View.GONE);
            } else {
                holder.mTimeRow.setVisibility(View.VISIBLE);
                holder.mTime.setText(TodoStackUtil.getFomatedTime(
                        mContext, targetDate.getTime(), td.timeFrom, td.timeTo));
            }
        }

        if ("".equalsIgnoreCase(td.location)) {
            //TODO hide row
//            holder.mLocation.setText("(location is empty)");
            holder.mLocationRow.setVisibility(View.GONE);
        } else {
            holder.mLocation.setText(td.location);
        }

        holder.mCreated.setText("(TBD)");
        holder.mLastUpdated.setText("(TBD)");

        holder.id = td.id;
    }

    @Override
    public int getItemCount() {
        if (mTodoList != null) {
            return mTodoList.size();
        } else {
            return 0;
        }
    }

    public TodoData getItem(int position) {
        return mTodoList.get(position);
    }

    public void removeItem(int position) {
        if (position < mTodoList.size()) {
            mTodoList.remove(position);
        } else {
            Log.e("TodoStack", "[removeItem]invalid position value!");
        }
    }

}
