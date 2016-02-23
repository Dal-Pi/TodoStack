package com.kania.todostack2.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kania.todostack2.R;

/**
 * Created by user on 2016-02-23.
 */
public class EachTabFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public EachTabFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EachTabFragment newInstance(int sectionNumber) {
        EachTabFragment fragment = new EachTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todolist, container, false);

        return rootView;
    }
}

