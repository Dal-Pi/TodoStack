package com.kania.todostack2.util;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.data.SubjectData;
import com.kania.todostack2.provider.TodoProvider;

/**
 * Created by user on 2016-02-12.
 */
public class SubjectDeleteDialog extends DialogFragment{
    private Callback mCallback;
    private int mOrder;

    public interface Callback {
        void onSelectDelete(int order);
    }

    public SubjectDeleteDialog() {
        setCallback(new SubjectDeleteDialog.Callback() {
            @Override
            public void onSelectDelete(int order) {//empty callback
            }
        });
    }

    public static SubjectDeleteDialog newInstance(int order, Callback callback) {
        SubjectDeleteDialog dialog = new SubjectDeleteDialog();
        dialog.setCallback(callback);
        dialog.setIds(order);
        return dialog;
    }

    private void setCallback(Callback callback) {
        mCallback = callback;
    }

    private void setIds(int order) {
        mOrder = order;
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
        View SubjectDeleteView = inflater.inflate(R.layout.dialog_delete_subject, container, false);
        TodoProvider provider = TodoProvider.getInstance(getActivity());
        SubjectData sd = provider.getSubjectByOrder(mOrder);
        TextView textSubjectName =
                (TextView) SubjectDeleteView.findViewById(R.id.dialog_delete_subject_subjectname);
        textSubjectName.setText(sd.subjectName);
        textSubjectName.setTextColor(sd.color);

        Button btnDelete =
                (Button) SubjectDeleteView.findViewById(R.id.dialog_btn_delete_subject_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onSelectDelete(mOrder);
                dismiss();
            }
        });
        btnDelete.setTextColor(sd.color);

        Button btnCancel =
                (Button) SubjectDeleteView.findViewById(R.id.dialog_btn_delete_subject_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnCancel.setTextColor(sd.color);

        return SubjectDeleteView;
    }
}
