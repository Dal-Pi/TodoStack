package com.kania.todostack2.presenter;

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
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.provider.TodoProvider;

import java.util.ArrayList;

/**
 * Created by user on 2016-02-01.
 */
public class SelectSubjectDialog extends DialogFragment {

    private Callback mCallback;

    public interface Callback {
        void onSelectSubject(int order);
    }

    public SelectSubjectDialog() {
        setCallback(new SelectSubjectDialog.Callback() {
            @Override
            public void onSelectSubject(int order) {
                //empty callback
            }
        });
    }

    public static SelectSubjectDialog newInstance(Callback callback) {
        SelectSubjectDialog dialog = new SelectSubjectDialog();
        dialog.setCallback(callback);
        return dialog;
    }

    private void setCallback(Callback callback) {
        mCallback = callback;
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
        View subjectSelectView = inflater.inflate(R.layout.dialog_select_subject, container, false);
        ListView subjectListView =
                (ListView) subjectSelectView.findViewById(R.id.dialog_select_subject_list);
        ArrayList<SubjectData> subjects = TodoProvider.getInstance(getActivity()).getAllSubject();
        SubjectTextButtonAdapter adapter = new SubjectTextButtonAdapter(getActivity(), subjects);
        subjectListView.setAdapter(adapter);
        Button btnCancel =
                (Button) subjectSelectView.findViewById(R.id.dialog_select_subject_button_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return subjectSelectView;
    }

    public class SubjectTextButtonAdapter extends BaseAdapter {
        Context mContext;
        ArrayList<SubjectData> mSubjects;

        public SubjectTextButtonAdapter(Context context, ArrayList<SubjectData> subjects) {
            mContext = context;
            this.mSubjects = subjects;
        }
        @Override
        public int getCount() {
            return mSubjects.size();
        }

        @Override
        public Object getItem(int position) {
            return mSubjects.get(position);
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
            SubjectData sd = mSubjects.get(position);
            TextView tv = (TextView) convertView.findViewById(R.id.text_button);
            tv.setText(sd.subjectName);
            tv.setTextColor(sd.color);
            SelectSubjectListItemHolder holder = new SelectSubjectListItemHolder();
            holder.order = sd.order;
            tv.setTag(holder);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectSubjectListItemHolder holder =
                            (SelectSubjectListItemHolder) v.getTag();
                    mCallback.onSelectSubject(holder.order);
                    dismiss();
                }
            });
            return convertView;
        }

        class SelectSubjectListItemHolder {
            int order;
        }
    }
}
