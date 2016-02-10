package com.kania.todostack2.presenter;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kania.todostack2.R;
import com.kania.todostack2.provider.ColorProvider;

import java.util.ArrayList;

/**
 * Created by user on 2016-02-01.
 */
public class SubjectColorSelectDialog extends DialogFragment {

    private Callback mCallback;

    public interface Callback {
        void onSelectColor(int color);
    }

    public SubjectColorSelectDialog() {
        setCallback(new SubjectColorSelectDialog.Callback() {
            @Override
            public void onSelectColor(int order) {
                //empty callback
            }
        });
    }

    public static SubjectColorSelectDialog newInstance(Callback callback) {
        SubjectColorSelectDialog dialog = new SubjectColorSelectDialog();
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
        View colorSelectView = inflater.inflate(R.layout.dialog_select_color, container, false);
        ListView colorListView =
                (ListView) colorSelectView.findViewById(R.id.dialog_list_select_color);
        ArrayList<String> colors = ColorProvider.getInstance().getAllColor();
        ColorTextButtonAdapter adapter = new ColorTextButtonAdapter(getActivity(), colors);
        colorListView.setAdapter(adapter);
        Button btnCancel =
                (Button) colorSelectView.findViewById(R.id.dialog_btn_select_color_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return colorSelectView;
    }

    public class ColorTextButtonAdapter extends BaseAdapter {
        Context mContext;
        ArrayList<String> mColors;

        public ColorTextButtonAdapter(Context context, ArrayList<String> colors) {
            mContext = context;
            this.mColors = colors;
        }
        @Override
        public int getCount() {
            return mColors.size();
        }

        @Override
        public Object getItem(int position) {
            return mColors.get(position);
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
            TextView tv = (TextView) convertView.findViewById(R.id.text_button);
            String colorString = mColors.get(position);
            int color = Color.parseColor(colorString);
            tv.setText(colorString);
            tv.setTextColor(color);
            SelectColorListItemHolder holder = new SelectColorListItemHolder();
            holder.color = color;
            tv.setTag(holder);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectColorListItemHolder holder =
                            (SelectColorListItemHolder) v.getTag();
                    mCallback.onSelectColor(holder.color);
                    dismiss();
                }
            });
            return convertView;
        }

        class SelectColorListItemHolder {
            int color;
        }
    }
}
