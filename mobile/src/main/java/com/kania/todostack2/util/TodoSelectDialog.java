package com.kania.todostack2.util;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.data.TodoData;
import com.kania.todostack2.provider.TodoProvider;
import com.kania.todostack2.view.TextViewInfo;

import java.util.ArrayList;

/**
 * Created by user on 2016-02-01.
 */
public class TodoSelectDialog extends DialogFragment {

    private Callback mCallback;
    private String mIds;

    public interface Callback {
        void onSelectTodo(int id);
    }

    public TodoSelectDialog() {
        setCallback(new TodoSelectDialog.Callback() {
            @Override
            public void onSelectTodo(int id) {
                //empty callback
            }
        });
    }

    public static TodoSelectDialog newInstance(String ids, Callback callback) {
        TodoSelectDialog dialog = new TodoSelectDialog();
        dialog.setCallback(callback);
        dialog.setIds(ids);
        return dialog;
    }

    private void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void setIds(String ids) {
        mIds = ids;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//        setCancelable(false);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View todoSelectView = inflater.inflate(R.layout.dialog_select_todo, container, false);
        ListView todoListView =
                (ListView) todoSelectView.findViewById(R.id.dialog_list_select_todo);
        ArrayList<TodoData> todos = new ArrayList<TodoData>();
        String[] sIds = mIds.split(TextViewInfo.DELIMITER_ID);
        for (String sId : sIds) {
            todos.add(TodoProvider.getInstance(getActivity()).getTodoById(Integer.parseInt(sId)));
        }
        TodoTextButtonAdapter adapter = new TodoTextButtonAdapter(getActivity(), todos);
        todoListView.setAdapter(adapter);
        Button btnCancel =
                (Button) todoSelectView.findViewById(R.id.dialog_btn_select_todo_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return todoSelectView;
    }

    public class TodoTextButtonAdapter extends BaseAdapter {
        Context mContext;
        ArrayList<TodoData> mTodos;

        public TodoTextButtonAdapter(Context context, ArrayList<TodoData> todos) {
            mContext = context;
            this.mTodos = todos;
        }
        @Override
        public int getCount() {
            return mTodos.size();
        }

        @Override
        public Object getItem(int position) {
            return mTodos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_simple_clickable_textview, parent, false);
            }
            TodoData td = mTodos.get(position);
            TextView tv = (TextView) convertView.findViewById(R.id.text_button);
            tv.setText(td.todoName);
            tv.setTextColor(TodoProvider.getInstance(mContext).
                    getSubjectByOrder(td.subjectOrder).color);
            SelectTodoListItemHolder holder = new SelectTodoListItemHolder();
            holder.id = td.id;
            tv.setTag(holder);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectTodoListItemHolder holder =
                            (SelectTodoListItemHolder) v.getTag();
                    mCallback.onSelectTodo(holder.id);
                    dismiss();
                }
            });
            return convertView;
        }

        class SelectTodoListItemHolder {
            int id;
        }
    }
}
