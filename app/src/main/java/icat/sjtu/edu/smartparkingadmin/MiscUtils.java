package icat.sjtu.edu.smartparkingadmin;

import android.text.format.DateFormat;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by outdo on 2015/9/28.
 */
public class MiscUtils {
    static private boolean isSameDay(Calendar c1, Calendar c2) {
        boolean sameYear = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
        boolean sameMonth = c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
        boolean sameDay = c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
        return sameDay && sameMonth && sameYear;
    }



    static public CharSequence getTimeDescription(Date d) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d);
        if(isSameDay(c1, c2)) {
            return DateFormat.format("今天HH:mm", d);
        } else {
            c2.add(Calendar.DATE, -1);
            if(isSameDay(c1, c2)) {
                return DateFormat.format("昨天HH:mm", d);
            } else {
                return DateFormat.format("MM月dd日", d);
            }
        }
    }

//    static public String getTimeDescription(Date d1, Date d2) {
//        Calendar c1 = Calendar.getInstance();
//        Calendar c2 = Calendar.getInstance();
//        c1.setTime(d1);
//        c2.setTime(d2);
//        if(isSameDay(c1, c2)) {
//            return DateFormat.format("今天H:mm", d1).toString;
//        } else {
//            c2.add(Calendar.DATE, -1);
//            if(isSameDay(c1, c2)) {
//                return DateFormat.format("昨天H:mm", d1).toString();
//            } else {
//                c2.add(Calendar.DATE, 2);
//                if(isSameDay(c1, c2)) {
//                    return DateFormat.format("明日H:mm", d1).toString();
//                }else {
//                    return DateFormat.format("MM月dd日", d1).toString();
//                }
//            }
//        }
//    }
    static public String getTimeDescription(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if(isSameDay(c1, c2)) {
            return "今日"+d1.getHours()+":"+d1.getMinutes();
        } else {
            c2.add(Calendar.DATE, -1);
            if(isSameDay(c1, c2)) {
                return "昨日"+d1.getHours()+":"+d1.getMinutes();
            } else {
                c2.add(Calendar.DATE, 2);
                if(isSameDay(c1, c2)) {
                    return "明日"+d1.getHours()+":"+d1.getMinutes();
                }else {
                    return DateFormat.format("MM月dd日", d1).toString();
                }
            }
        }
    }
    static public String getDistanceDescription(int dis) {
        if( dis <= 1000 ) {
            return dis+"米";
        } else {
            double f = dis / 1000;
            return (new java.text.DecimalFormat("#.00").format(f)) + "公里";
        }
    }

    static private String getTimeAhead(long diffSec) {
        if( diffSec < 60*60 ) {
            return (diffSec/60) + "分钟";
        } else {
            return diffSec / (3600) + "小时" + ((diffSec % 36000 / 60 > 0) ? ((diffSec % 3600 / 60) + "分钟") : "");
        }
    }

    static public String getTimeDiffDescription(Date d1, Date d2) {

        long millis1 = d1.getTime();
        long millis2 = d2.getTime();
        if ( millis1 > millis2 ) {
            long diffSec = (millis1 - millis2) / 1000;
            //Log.d("MIscUtils", "ahead: " + diffSec);
            if ( diffSec <= 60 * 60 *8 ) {
                return getTimeAhead(diffSec) + "后";
            } else {
                return getTimeDescription(d1, d2);
            }
        } else {
            long diffSec = (millis2 - millis1) / 1000;
            if ( diffSec <= 60 * 60 *8 ) {
                return getTimeAhead(diffSec) + "前";
            } else {
                return getTimeDescription(d1, d2);
            }
        }
    }
}