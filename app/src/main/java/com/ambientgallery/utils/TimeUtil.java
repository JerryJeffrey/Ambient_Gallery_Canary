package com.ambientgallery.utils;

import android.os.Bundle;
import android.os.Message;

import com.ambientgallery.R;

import java.util.Calendar;

public class TimeUtil {
    public static Message getTimeMessageBundle() {
        Calendar cal = Calendar.getInstance();
        Message message = new Message();
        Bundle bundle = new Bundle();

        bundle.putInt("date", cal.get(Calendar.DATE));
        bundle.putInt("dayR", resolveDayR(cal.get(Calendar.DAY_OF_WEEK)));
        bundle.putInt("period", cal.get(Calendar.AM_PM));//AM=0, PM=1
        bundle.putInt("hour12", cal.get(Calendar.HOUR));//12h
        bundle.putInt("hour24", cal.get(Calendar.HOUR_OF_DAY));//24h
        bundle.putInt("minute", cal.get(Calendar.MINUTE));
        bundle.putInt("second", cal.get(Calendar.SECOND));

        message.setData(bundle);
        return message;
    }

    public static String intToFormattedString(int num) {
        if (num < 10) {
            return ("0" + num);
        } else {
            return String.valueOf(num);
        }
    }

    private static int resolveDayR(int day) {
        switch (day) {
            case (1):
                return R.string.day_sun;
            case (2):
                return R.string.day_mon;
            case (3):
                return R.string.day_tue;
            case (4):
                return R.string.day_wed;
            case (5):
                return R.string.day_thu;
            case (6):
                return R.string.day_fri;
            case (7):
                return R.string.day_sat;
            default:
                return R.string.empty;
        }
    }
}
