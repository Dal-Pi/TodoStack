package com.kania.todostack2.util;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.kania.todostack2.R;

/**
 * Created by user on 2016-02-09.
 */
public class SubjectNameUpdateDialog extends DialogFragment{
    private Callback mCallback;

    private String mOriginName = "";

    public interface Callback {
        void onEditName(String newName);
    }

    public SubjectNameUpdateDialog() {
        setCallback(new SubjectNameUpdateDialog.Callback() {
            @Override
            public void onEditName(String newName) {
                //empty callback
            }
        });
    }

    public static SubjectNameUpdateDialog newInstance(Callback callback) {
        SubjectNameUpdateDialog dialog = new SubjectNameUpdateDialog();
        dialog.setCallback(callback);
        return dialog;
    }

    private void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void setOriginName(String name) {
        mOriginName = name;
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
        View updateNameView =
                inflater.inflate(R.layout.dialog_update_subject_name, container, false);
        final EditText editName =
                (EditText) updateNameView.findViewById(R.id.dialog_edit_update_subject_name);
        editName.setText(mOriginName);
        Button btnEdit =
                (Button) updateNameView.findViewById(R.id.dialog_btn_update_subject_name_edit);
        btnEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                if (TodoStackUtil.checkVaildName(getActivity(), name)) {
                    mCallback.onEditName(name);
                    dismiss();
                }
            }
        });
        Button btnCancel =
                (Button) updateNameView.findViewById(R.id.dialog_btn_update_subject_name_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return updateNameView;
    }
}
