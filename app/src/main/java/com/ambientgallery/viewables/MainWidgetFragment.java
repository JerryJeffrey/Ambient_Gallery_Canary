package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.GO_AMBIENT_TIMEOUT;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.SWITCH_IMAGE_TIMEOUT;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.utils.TimeUtil.getTimeMessageBundle;
import static com.ambientgallery.utils.TimeUtil.intToFormattedString;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;

import java.util.Timer;
import java.util.TimerTask;

public class MainWidgetFragment extends Fragment {
    Timer timer;
    ContentResolver contentResolver;
    SharedPreferences prefs;
    TextView textMain, textSub;
    int currentTime;
    private final Handler timeMessageHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            //get contents in message bundles
            int date = message.getData().getInt("date");
            int dayR = message.getData().getInt("dayR");
            int dayPeriod = message.getData().getInt("period");
            int hour24 = message.getData().getInt("hour24");
            int minute = message.getData().getInt("minute");
            int second = message.getData().getInt("second");
            int hour;

            String timeFormat = Settings.System.getString(contentResolver, Settings.System.TIME_12_24), dayPeriodText;
            if (timeFormat != null && timeFormat.equals("12")) {//null: default time format
                hour = message.getData().getInt("hour12");
                if (dayPeriod == 1) {//PM
                    dayPeriodText = getString(R.string.time_pm);
                } else {//AM
                    dayPeriodText = getString(R.string.time_am);
                }
            } else {
                hour = hour24;
                dayPeriodText = getString(R.string.empty);
            }
            textSub.setText(intToFormattedString(date) + " " + getString(dayR) + " " + dayPeriodText);
            textMain.setText(intToFormattedString(hour) + ":" + intToFormattedString(minute));
            return true;
        }
    });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_main_widget, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeMessageHandler.sendMessage(getTimeMessageBundle());
                if (currentTime < prefsInt(prefs, GO_AMBIENT_TIMEOUT) || currentTime < prefsInt(prefs, SWITCH_IMAGE_TIMEOUT)) {
                    currentTime += 1;
                }
            }
        }, 0, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        //cancel timer created in onResume()
        if (timer != null) {
            timer.cancel();
        }
    }
}
