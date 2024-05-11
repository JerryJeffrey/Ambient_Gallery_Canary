package com.ambientgallery.viewables;

import static com.ambientgallery.utils.AnimateUtil.ongoingAnimators;
import static com.ambientgallery.utils.AnimateUtil.viewOpacity;
import static com.ambientgallery.utils.AnimateUtil.viewPosition;
import static com.ambientgallery.utils.AnimateUtil.viewRotation;
import static com.ambientgallery.utils.AnimateUtil.viewScale;
import static com.ambientgallery.utils.BitmapUtil.decodeSampledBitmap;
import static com.ambientgallery.utils.DimensUtil.dp2px;
import static com.ambientgallery.utils.DimensUtil.getDisplayMetrics;
import static com.ambientgallery.utils.DimensUtil.px2dp;
import static com.ambientgallery.utils.ExeptionUtil.convertExceptionMessage;
import static com.ambientgallery.utils.FileUtil.getFileNameList;
import static com.ambientgallery.utils.FileUtil.path;
import static com.ambientgallery.utils.SharedPrefsUtil.initPrefs;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsFloat;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsInt;
import static com.ambientgallery.utils.TimeUtil.getTimeMessageBundle;
import static com.ambientgallery.utils.TimeUtil.intToFormattedString;
import static com.ambientgallery.utils.WindowFeatureUtil.allowCutoutDisplay;
import static com.ambientgallery.utils.WindowFeatureUtil.allowSleeping;
import static com.ambientgallery.utils.WindowFeatureUtil.goImmersive;
import static com.ambientgallery.utils.WindowFeatureUtil.preventSleeping;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.ambientgallery.R;
import com.ambientgallery.components.DisplayDimensions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    ArrayList<String> imagesList;
    private View rootView, bgContainer, refreshContainer, refreshIcon, statusNight, textContainer, topContainer, bottomContainer, topShader, bottomShader;
    private TextView textSub, textMain, hint;
    private ImageView bgLower, bgUpper;
    private ImageButton settingsButton, ambientButton;
    private Timer timer;
    private ContentResolver contentResolver;
    private Window window;
    private WindowManager windowManager;
    private Context context;
    private Sensor lightSensor;
    private SensorManager sensorManager;
    private boolean appInit = false, inNormal = true, inAmbient = false, buttonsVisible = true, buttonsInvisible = false, dragStarted = false, dragEnded = false, upperImgVisible = true;
    private float nudgeX, nudgeY, touchStartY;
    private int currentTime, imageListIndex, currentOrientation;
    public String currentPath;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        window = getWindow();
        windowManager = getWindowManager();
        context = getApplicationContext();
        contentResolver = this.getContentResolver();

        preventSleeping(window);
        allowCutoutDisplay(window);

        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.main_root_view);
        textSub = findViewById(R.id.main_text_sub);
        textMain = findViewById(R.id.main_text_main);
        settingsButton = findViewById(R.id.main_button_settings);
        ambientButton = findViewById(R.id.main_button_ambient);
        bgContainer = findViewById(R.id.main_bg_container);
        bgLower = findViewById(R.id.main_bg_lower);
        bgUpper = findViewById(R.id.main_bg_upper);
        topContainer = findViewById(R.id.main_top_container);
        bottomContainer = findViewById(R.id.main_bottom_container);
        textContainer = findViewById(R.id.main_text_container);
        topShader = findViewById(R.id.main_top_shader);
        bottomShader = findViewById(R.id.main_bottom_shader);
        statusNight = findViewById(R.id.main_status_night);
        refreshContainer = findViewById(R.id.main_refresh_container);
        refreshIcon = findViewById(R.id.main_refresh_icon);

        hint = findViewById(R.id.main_hint);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        initPrefs(context, "MainPrefs");

        //permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            refreshIcon.setLayerType(View.LAYER_TYPE_HARDWARE, null);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        currentOrientation = windowManager.getDefaultDisplay().getRotation();
        setSafeArea();
        OrientationEventListener orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (currentOrientation != windowManager.getDefaultDisplay().getRotation()) {
                    setSafeArea();
                    currentOrientation = windowManager.getDefaultDisplay().getRotation();
                }
            }
        };
        if (orientationEventListener.canDetectOrientation()) orientationEventListener.enable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //get shared prefs
        prefs = context.getSharedPreferences("MainPrefs", Context.MODE_PRIVATE);
        rootView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //initialize touch start position
                    touchStartY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    //if not in image switching process
                    if (!ongoingAnimators.containsKey(refreshContainer.getId() + "opacity") && !ongoingAnimators.containsKey(bgUpper.getId() + "opacity")) {
                        //get touch position
                        float currentY = event.getRawY();
                        float y = px2dp(context, currentY - touchStartY);
                        //update start position to minimum value of single touch
                        if (touchStartY > currentY) touchStartY = currentY;
                        //calculate drag percentages
                        float startPercent = y / prefsInt(prefs, "dragStartSensitivity");
                        float endPercent = (y - prefsInt(prefs, "dragStartSensitivity")) / prefsInt(prefs, "dragEndSensitivity");
                        //actions when drag percentages changed
                        if (endPercent >= 1) {
                            dragEnded = true;
                            refreshIcon.setRotation(15 * (endPercent - 1));
                            refreshContainer.setAlpha(1f);
                            refreshIcon.setAlpha(1f);
                            refreshContainer.setTranslationY(dp2px(context, 6 * (endPercent - 1)));
                        } else if (startPercent >= 1) {
                            dragStarted = true;
                            dragEnded = false;
                            refreshIcon.setRotation(45 * (endPercent - 1));
                            refreshContainer.setAlpha(endPercent);
                            refreshIcon.setAlpha(0.5f);
                            refreshContainer.setTranslationY(dp2px(context, 12 * (endPercent - 1)));
                        } else {
                            refreshIcon.setRotation(0);
                            refreshContainer.setAlpha(0);
                            refreshContainer.setTranslationY(0);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (dragEnded) {
                        setImage(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                viewPosition(refreshContainer, 0, dp2px(context, -12), 1, 1, prefsInt(prefs, "animationDuration_instant"));
                                viewOpacity(refreshContainer, 0, 1, 1, prefsInt(prefs, "animationDuration_instant"));
                                viewRotation(refreshIcon, -45, 1, 1, prefsInt(prefs, "animationDuration_instant"));
                            }
                        });
                        //play refresh animation
                        viewRotation(refreshIcon, 360, 1, 1, prefsInt(prefs, "animationDuration_normal"));
                        viewOpacity(refreshContainer, 1, 1, 1, prefsInt(prefs, "animationDuration_short"));
                        viewPosition(refreshContainer, 0, 0, 1, 1, prefsInt(prefs, "animationDuration_short"));
                    } else if (dragStarted) {
                        //play reset refresh button animation
                        viewRotation(refreshIcon, -45, 1, 1, prefsInt(prefs, "animationDuration_instant"));
                        viewOpacity(refreshContainer, 0, 1, 1, prefsInt(prefs, "animationDuration_instant"));
                        viewPosition(refreshContainer, 0, dp2px(context, -12), 1, 1, prefsInt(prefs, "animationDuration_instant"));
                    } else {
                        v.performClick();
                    }
                    //reset touch event status
                    dragStarted = false;
                    dragEnded = false;
            }
            //whether the touch event is consumed
            return true;
        });
        rootView.setOnClickListener(v -> {
            currentTime = 0;
            if (inAmbient) {
                //actions when clicked in ambient mode
                leaveAmbient();
            } else {
                //actions when clicked in normal display mode
                showActionButtons();
            }
        });
        settingsButton.setOnClickListener(v -> {
            if (!buttonsInvisible) {
                currentTime = 0;
                hideActionButtons();
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("currentPath", currentPath);
                startActivity(intent);
            }
        });
        settingsButton.setOnLongClickListener(v -> {
            Toast.makeText(MainActivity.this, getText(R.string.button_settings), Toast.LENGTH_SHORT).show();
            currentTime = 0;
            return true;
        });
        ambientButton.setOnLongClickListener(v -> {
            Toast.makeText(MainActivity.this, getText(R.string.button_ambient_mode), Toast.LENGTH_SHORT).show();
            currentTime = 0;
            return true;
        });
        ambientButton.setOnClickListener(v -> {
            if (!buttonsInvisible) {
                goAmbient();
                hideActionButtons();
            }
        });
        //listen to sensor if exist
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        }
        goImmersive(window);

        getDisplayMetrics(windowManager);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            try {
                imagesList = getFileNameList();
            } catch (IOException e) {
                hint.setText(convertExceptionMessage(context, e));
            }
            if (appInit) {
                leaveAmbient();
            } else {
                setImage(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        refreshIcon.setAlpha(.5f);
                        refreshIcon.setRotation(-45);
                        viewOpacity(refreshContainer, 1, 1, 1, prefsInt(prefs, "animationDuration_short"));
                        viewRotation(refreshIcon, 0, 1, 1, prefsInt(prefs, "animationDuration_short"));
                        viewPosition(refreshContainer, 0, 0, 1, 1, prefsInt(prefs, "animationDuration_short"), new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                viewOpacity(refreshContainer, 0, 0, 0, prefsInt(prefs, "animationDuration_instant"));
                                viewRotation(refreshIcon, -45, 0, 0, prefsInt(prefs, "animationDuration_instant"));
                                viewPosition(refreshContainer, 0, dp2px(context, -12), 0, 0, prefsInt(prefs, "animationDuration_instant"));
                            }
                        });
                    }
                });
                refreshContainer.setTranslationY(dp2px(context, -12));
                refreshContainer.setAlpha(0);
                statusNight.setTranslationX(dp2px(context, -6));
                statusNight.setAlpha(0);
                textMain.setAlpha(prefsFloat(prefs, "textNormalOpacity"));
                textSub.setAlpha(prefsFloat(prefs, "textNormalOpacity"));
                bgContainer.setAlpha(prefsFloat(prefs, "bgNormalOpacity"));
                appInit = true;
            }
        }

        //create periodic task which runs every 1 second
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeMessageHandler.sendMessage(getTimeMessageBundle());
                updateTime();
            }
        }, 0, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //cancel sensor listeners created in onResume()
        sensorManager.unregisterListener(this);

        //cancel timer created in onResume()
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //cancel handler to avoid memory leak
        timeMessageHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //cancel handler to avoid memory leak
        timeMessageHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float value = event.values[0];
        if (type == Sensor.TYPE_LIGHT) {//actions when ambient light sensor reacts
            if (value <= prefsFloat(prefs, "nightStartBrightness")) {
                allowSleeping(window);
                viewPosition(statusNight, 0, 0, 1, 1, prefsInt(prefs, "animationDuration_instant"));
                viewOpacity(statusNight, 1, 1, 1, prefsInt(prefs, "animationDuration_instant"));
            }
            if (value > prefsFloat(prefs, "nightEndBrightness")) {
                preventSleeping(window);
                viewPosition(statusNight, dp2px(context, -6), 0, 1, 1, prefsInt(prefs, "animationDuration_instant"));
                viewOpacity(statusNight, 0, 1, 1, prefsInt(prefs, "animationDuration_instant"));
            }
        }
    }


    private final Handler timeMessageHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            //get contents in message bundles
            int date = message.getData().getInt("date"), dayR = message.getData().getInt("dayR"), dayPeriod = message.getData().getInt("period"), hour24 = message.getData().getInt("hour24"), minute = message.getData().getInt("minute"), second = message.getData().getInt("second"), hour;

            String timeFormat = Settings.System.getString(contentResolver, Settings.System.TIME_12_24), dayPeriodText;
            if (timeFormat != null && timeFormat.equals("12")) {//null: default time format
                hour = message.getData().getInt("hour12");
                if (dayPeriod == 1) {//PM
                    dayPeriodText = getString(R.string.pm);
                } else {//AM
                    dayPeriodText = getString(R.string.am);
                }
            } else {
                hour = hour24;
                dayPeriodText = getString(R.string.empty);
            }

            textSub.setText(intToFormattedString(date) + " " + getString(dayR) + " " + dayPeriodText);
            textMain.setText(intToFormattedString(hour) + ":" + intToFormattedString(minute));

            //calculate main text translating value, requires 24h time format for Y axis
            nudgeX = dp2px(context, 30 - Math.abs(minute + second / 60f - 30));
            nudgeY = dp2px(context, 2 * (Math.abs(hour24 + minute / 60f + second / 3600f - 12) - 12));

            //hide buttons
            if (buttonsVisible && !buttonsInvisible && currentTime == prefsInt(prefs, "hideButtonTimeout")) {
                hideActionButtons();
            }
            if (currentTime == prefsInt(prefs, "ambientTimeout") && !inAmbient && inNormal) {
                goAmbient();
            }

            //set translation of main text
            if (inAmbient && !inNormal) {
                viewPosition(textContainer, nudgeX, nudgeY, 0f, 1f, 0);
            }

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //switch background and reset current time
                if (inAmbient && !inNormal && currentTime == prefsInt(prefs, "switchImageTimeout")) {
                    setImage(null);
                }
            }
            return true;
        }
    });

    private void setImage(AnimatorListenerAdapter customListener) {
        DisplayDimensions dimensions = getDisplayMetrics(getWindowManager());
        if (imagesList != null && imagesList.size() > 0) {
            int newIndex;
            if (imagesList.size() > 1) {
                do {
                    newIndex = (int) (Math.random() * imagesList.size());
                } while (imageListIndex == newIndex);
                imageListIndex = newIndex;
            }
            new Thread(() -> {
                currentPath = path + imagesList.get(imageListIndex);
                Bitmap bitmap = decodeSampledBitmap(currentPath, dimensions.width, dimensions.height, prefsInt(prefs, "inSampleLevel"));
                runOnUiThread(() -> {
                    if (upperImgVisible) {//points lower layer
                        bgLower.setImageBitmap(bitmap);
                    } else {//points upper layer
                        bgUpper.setImageBitmap(bitmap);
                    }
                    switchImageLayer(customListener);
                });
            }).start();
        }
    }

    private void switchImageLayer(AnimatorListenerAdapter listener) {
        View operateView;
        int switchAnimationDuration = prefsInt(prefs, "animationDuration_long");
        if (upperImgVisible) {//at upper
            operateView = bgLower;
            viewOpacity(bgUpper, 0f, 0f, 1f, switchAnimationDuration);
            upperImgVisible = false;
        } else {//at lower
            operateView = bgUpper;
            viewOpacity(bgUpper, 1f, 0f, 1f, switchAnimationDuration);
            upperImgVisible = true;
        }
        operateView.setScaleX(prefsFloat(prefs, "switchImageScale"));
        operateView.setScaleY(prefsFloat(prefs, "switchImageScale"));
        viewScale(operateView, 1f, 1f, 0.5f, 1f, switchAnimationDuration, listener);
        currentTime = 0;
    }

    private void goAmbient() {
        inNormal = false;
        viewOpacity(bgContainer, prefsFloat(prefs, "bgAmbientOpacity"), 1f / 3, 1f, prefsInt(prefs, "animationDuration_normal"));
        viewOpacity(textContainer, prefsFloat(prefs, "textAmbientOpacity"), 1f / 3, 1f, prefsInt(prefs, "animationDuration_normal"));
        viewOpacity(new View[]{textSub, topShader, bottomShader}, 0, 1f / 3, 1f, prefsInt(prefs, "animationDuration_normal"));
        viewPosition(textContainer, nudgeX, nudgeY, 1f / 3, 1f, prefsInt(prefs, "animationDuration_normal"));
        viewPosition(textSub, -nudgeX, nudgeY, 1f / 3, 1f, prefsInt(prefs, "animationDuration_normal"), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                inAmbient = true;
            }
        });
        currentTime = 0;
    }

    private void leaveAmbient() {
        inAmbient = false;
        viewOpacity(new View[]{bgContainer, topShader, bottomShader}, prefsFloat(prefs, "bgNormalOpacity"), 1, 1, prefsInt(prefs, "animationDuration_short"));
        viewOpacity(new View[]{textContainer, textSub}, prefsFloat(prefs, "textNormalOpacity"), 1, 1, prefsInt(prefs, "animationDuration_short"));
        viewPosition(new View[]{textContainer, textSub}, 0, 0, 1, 1, prefsInt(prefs, "animationDuration_short"), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                inNormal = true;
            }
        });
    }

    private void showActionButtons() {
        buttonsInvisible = false;
        viewPosition(topContainer, 0, 0, 1, 1f, prefsInt(prefs, "animationDuration_instant"));
        viewOpacity(topContainer, 1f, 1, 1f, prefsInt(prefs, "animationDuration_instant"), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                buttonsVisible = true;
            }
        });
    }

    private void hideActionButtons() {
        buttonsVisible = false;
        viewPosition(topContainer, 0, dp2px(context, -12), 1, 1, prefsInt(prefs, "animationDuration_instant"));
        viewOpacity(topContainer, 0, 1, 1, prefsInt(prefs, "animationDuration_instant"), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                buttonsInvisible = true;
            }
        });

    }

    public void updateTime() {//live
        if (currentTime < prefsInt(prefs, "ambientTimeout") || currentTime < prefsInt(prefs, "switchImageTimeout")) {
            currentTime += 1;
        }
    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            hint.setText(getString(R.string.empty));
        } else {
            hint.setText(getString(R.string.hint_storage_permission_explanation_1) + path + getString(R.string.hint_storage_permission_explanation_2));
        }
    });

    private void setSafeArea() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            View decorView = getWindow().getDecorView();
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null) {
                DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                if (displayCutout != null) {
                    setContentMargin(displayCutout.getSafeInsetLeft(), displayCutout.getSafeInsetTop(), displayCutout.getSafeInsetRight(), displayCutout.getSafeInsetBottom());
                }
            }
        }
    }

    private void setContentMargin(int left, int top, int right, int bottom) {
        //get margins
        ConstraintLayout.LayoutParams topParams = (ConstraintLayout.LayoutParams) topContainer.getLayoutParams(), bottomParams = (ConstraintLayout.LayoutParams) bottomContainer.getLayoutParams(), refreshParams = (ConstraintLayout.LayoutParams) refreshContainer.getLayoutParams();
        //set margins
        topParams.leftMargin = left;
        topParams.topMargin = top;
        topParams.rightMargin = right;
        bottomParams.leftMargin = left;
        bottomParams.rightMargin = right;
        bottomParams.bottomMargin = bottom;
        refreshParams.leftMargin = left;
        refreshParams.topMargin = top;
        refreshParams.rightMargin = right;
        //apply changes
        topContainer.setLayoutParams(topParams);
        bottomContainer.setLayoutParams(bottomParams);
        refreshContainer.setLayoutParams(refreshParams);
    }
}