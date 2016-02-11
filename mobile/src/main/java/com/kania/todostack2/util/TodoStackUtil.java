package com.kania.todostack2.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by user on 2016-02-11.
 */
public class TodoStackUtil {

    public static boolean checkVaildName(Context context, String name) {
        boolean ret;
        if (!"".equalsIgnoreCase(name.trim())) {
            ret = true;
        } else {
            ret = false;
            Toast.makeText(context, "Do not execute! Name is empty.", Toast.LENGTH_SHORT).show();
        }

        return ret;
    }

    public static boolean checkVaildTodoDate(
            Context context, String year, String month, String day) {
        boolean ret = false;

        if("".equals(year) || "".equals(month) || "".equals(day)){
            Toast.makeText(context, "There is a empty space.", Toast.LENGTH_SHORT).show();
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
                                "Do not execute! Input vaild Month(1 ~ 12)",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                if( (nDateCheck > 0) && (nDateCheck <= nRangeDate) ){
                    ret = true;
                } else {
                    Toast.makeText(context,
                            "Do not execute! Input vaild Day(1 ~ last day each month)",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context,
                        "Do not execute! Input vaild Year.", Toast.LENGTH_SHORT).show();
            }
        }

        return ret;
    }
}
