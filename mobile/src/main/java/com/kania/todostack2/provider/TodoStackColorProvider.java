package com.kania.todostack2.provider;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by user on 2016-01-10.
 */
public class TodoStackColorProvider {

    private static final String COLOR_STRING_DEFAULT_BLACK = "#FFFFFFFF";
    private static final String COLOR_STRING_TODAY = "#FFFF0000";

    private static String[] preloadedcolorStrings = {
            COLOR_STRING_DEFAULT_BLACK,
            "#FFFF0000",
            "#FF00FF00",
            "#FF0000FF"
    };

    private static ArrayList<Integer> alColors;

    public static TodoStackColorProvider instance;

    private TodoStackColorProvider() {
        alColors = new ArrayList<Integer>();
    }

    public static TodoStackColorProvider getInstance() {
        if (instance == null) {
            instance = new TodoStackColorProvider();
        }
        return instance;
    }

    public static int getDefaultColor() {
        return Color.parseColor(COLOR_STRING_DEFAULT_BLACK);
    }

    public static int getTodayColor() {
        return Color.parseColor(COLOR_STRING_TODAY);
    }

    public static ArrayList<Integer> getPreloadedcolors() {
        if (alColors.size() < 0) {
            for (String colorString : preloadedcolorStrings) {
                alColors.add(Color.parseColor(colorString));
            }
        }
        return alColors;
    }
}
