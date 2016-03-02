package com.kania.todostack2.provider;

import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 2016-01-10.
 */
public class ColorProvider {

    public static final String COLOR_STRING_DEFAULT_BLACK = "#FF000000";
    public static final String COLOR_STRING_DEFAULT_GRAY = "#FF888888";
    public static final String COLOR_STRING_TODAY = "#FFFF0000";
    public static final String COLOR_SOLID_PATH = COLOR_STRING_DEFAULT_GRAY;
    public static final String COLOR_DASH_PATH = COLOR_STRING_DEFAULT_GRAY;

    private static String[] preloadedcolorStrings = {
            "#FFFF96E6",
            "#FF9696E1",
            "#FF4B6EE1",
            "#FF41CDB9",
            "#FFFF7F7F",
            "#FF64DC73",
            "#FFB4D25A",
            "#FFCDBE46",
            "#FFFFB446",
            "#FF555555"
    };

    private static ArrayList<Integer> alColors;

    public static ColorProvider instance;

    private ColorProvider() {
        alColors = new ArrayList<Integer>();
    }

    public static ColorProvider getInstance() {
        if (instance == null) {
            instance = new ColorProvider();
        }
        return instance;
    }

    public static int getColor(String colorString) {
        return Color.parseColor(colorString);
    }

    public static int getDefaultColor() {
//        return Color.parseColor(COLOR_STRING_DEFAULT_BLACK);
        return Color.parseColor(COLOR_STRING_DEFAULT_GRAY);
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

    public static int getRandomColor() {
        Random random = new Random();
        String randomColorString =
                preloadedcolorStrings[random.nextInt(preloadedcolorStrings.length)];
        Log.d("TodoStack", "random color is " + randomColorString);
        return Color.parseColor(randomColorString);
    }

    public static ArrayList<String> getAllColor() {
        ArrayList<String> colors = new ArrayList<String>();
        for (String colorString : preloadedcolorStrings) {
            colors.add(colorString);
        }

        return colors;
    }
}
