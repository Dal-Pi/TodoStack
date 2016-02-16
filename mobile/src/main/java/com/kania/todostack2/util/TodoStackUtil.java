package com.kania.todostack2.util;

import android.content.Context;
import android.widget.Toast;

import com.kania.todostack2.R;
import com.kania.todostack2.view.TextViewInfo;

import java.util.Calendar;

/**
 * Created by user on 2016-02-11.
 */
public class TodoStackUtil {

    public static boolean checkVaildName(Context context, String name) {
        boolean ret;
        if ("".equalsIgnoreCase(name.trim())) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.toast_waring_name_empty),
                    Toast.LENGTH_SHORT).show();
            ret = false;
        } else if (name.contains(TextViewInfo.DELIMITER_ID)) {
            Toast.makeText(context,
                    context.getResources().getString(R.string.toast_waring_input_slush),
                    Toast.LENGTH_SHORT).show();
            ret = false;
        } else {
            ret = true;
        }

        return ret;
    }

    public static boolean checkVaildTodoDate(
            Context context, String year, String month, String day) {
        boolean ret = false;

        if("".equals(year) || "".equals(month) || "".equals(day)){
            Toast.makeText(context,
                    context.getResources().getString(R.string.toast_waring_date_empty),
                    Toast.LENGTH_SHORT).show();
        }
        else{
            int nYearCheck = Integer.parseInt(year);
            int nMonthCheck = Integer.parseInt(month);
            int nDateCheck = Integer.parseInt(day);

            if (nYearCheck > 0) {
                int nRangeDate = -1;
                switch (nMonthCheck) {
                    case 1:
                    case 3:
                    case 5:
                    case 7:
                    case 8:
                    case 10:
                    case 12:
                        nRangeDate = 31;
                        break;
                    case 4:
                    case 6:
                    case 9:
                    case 11:
                        nRangeDate = 30;
                        break;
                    case 2:
                        if( ( (nYearCheck%4 == 0) && (nYearCheck%100 != 0) )
                                || (nYearCheck%400 == 0) ){
                            nRangeDate = 29;
                        }
                        else{
                            nRangeDate = 28;
                        }
                        break;
                    default:
                        Toast.makeText(context,
                                context.getResources().getString(R.string.toast_waring_wrong_month),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                if( (nDateCheck > 0) && (nDateCheck <= nRangeDate) ){
                    ret = true;
                } else {
                    Toast.makeText(context,
                            context.getResources().getString(R.string.toast_waring_wrong_day),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context,
                        context.getResources().getString(R.string.toast_waring_wrong_year),
                        Toast.LENGTH_SHORT).show();
            }
        }

        return ret;
    }

    public static int campareDate(Calendar target, Calendar today) {
        int diffDays;
        target.set(Calendar.HOUR_OF_DAY, 0);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);
        target.set(Calendar.MILLISECOND, 0);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        diffDays = (int) ((target.getTimeInMillis() - today.getTimeInMillis())
                / (1000 * 60 * 60 * 24));

        return diffDays;
    }
}
