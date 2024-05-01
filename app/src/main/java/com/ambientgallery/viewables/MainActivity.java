package com.ambientgallery.viewables;

import static com.ambientgallery.components.AppStatus.ambientTimeout;
import static com.ambientgallery.components.AppStatus.animationDuration_instant;
import static com.ambientgallery.components.AppStatus.animationDuration_long;
import static com.ambientgallery.components.AppStatus.animationDuration_normal;
import static com.ambientgallery.components.AppStatus.animationDuration_short;
import static com.ambientgallery.components.AppStatus.bgAmbientOpacity;
import static com.ambientgallery.components.AppStatus.bgNormalOpacity;
import static com.ambientgallery.components.AppStatus.currentBrightness;
import static com.ambientgallery.components.AppStatus.currentTime;
import static com.ambientgallery.components.AppStatus.dragEndSensitivity;
import static com.ambientgallery.components.AppStatus.dragStartSensitivity;
import static com.ambientgallery.components.AppStatus.hideButtonTimeout;
import static com.ambientgallery.components.AppStatus.imageListIndex;
import static com.ambientgallery.components.AppStatus.nightBrightness;
import static com.ambientgallery.components.AppStatus.switchImageTimeout;
import static com.ambientgallery.components.AppStatus.textAmbientOpacity;
import static com.ambientgallery.components.AppStatus.textNormalOpacity;
import static com.ambientgallery.components.AppStatus.updateTime;
import static com.ambientgallery.components.AppStatus.upperImgVisible;
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
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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
    private View rootView, bgAmbientView,
            actionButtonContainer,
            statusTimeout, statusRefresh,
            statusLight, statusProximity,
            topContainer, bottomContainer;
    private TextView textSub, textMain;
    private TextView debug, hint;
    private ImageView bgLower, bgUpper;
    private ImageButton settingsButton, ambientButton;
    private Timer timer;
    private ContentResolver contentResolver;
    private Window window;
    private WindowManager windowManager;
    private Context context;
    private Sensor lightSensor, proximitySensor;
    private SensorManager sensorManager;
    private boolean bgInit = false;
    private boolean proximityNear = false, dragStarted = false, dragEnded = false;
    private float nudgeX, nudgeY, touchStartY;


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
        actionButtonContainer = findViewById(R.id.main_button_container_actions);
        settingsButton = findViewById(R.id.main_button_settings);
        ambientButton = findViewById(R.id.main_button_ambient);
        bgAmbientView = findViewById(R.id.main_bg_ambient_operator);
        bgLower = findViewById(R.id.main_bg_lower);
        bgUpper = findViewById(R.id.main_bg_upper);
        topContainer = findViewById(R.id.main_top_container);
        bottomContainer = findViewById(R.id.main_bottom_container);

        statusTimeout = findViewById(R.id.main_status_timeout);
        statusRefresh = findViewById(R.id.main_status_refresh);
        statusLight = findViewById(R.id.main_timeout_status_light);
        statusProximity = findViewById(R.id.main_timeout_status_proximity);

        debug = findViewById(R.id.main_debug);
        hint = findViewById(R.id.main_hint);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        initializeHiddenButton(statusTimeout);
        initializeHiddenButton(statusRefresh);

        setSafeArea(0,100,0,0);

        rootView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStartY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float y = px2dp(context, event.getRawY() - touchStartY);
                    float startPercent = y / dragStartSensitivity;
                    float endPercent = (y - dragStartSensitivity) / dragEndSensitivity;
                    if (endPercent >= 1) {
                        dragEnded = true;
                        viewRotation(statusRefresh, 5 * (endPercent - 1) + 45,
                                1, 1, 0);
                        viewOpacity(statusRefresh, endPercent, 1, 1, 0);
                        viewPosition(statusRefresh, 0, dp2px(context, endPercent),
                                1, 1, 0);
                    } else if (startPercent >= 1) {
                        dragStarted = true;
                        dragEnded = false;
                        viewRotation(statusRefresh, 45 * endPercent, 1, 1, 0);
                        viewOpacity(statusRefresh, endPercent * 0.8f, 1, 1, 0);
                        viewPosition(statusRefresh, 0, dp2px(context, 12) *
                                (endPercent - 1), 1, 1, 0);
                    } else {
                        dragStarted = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    currentTime = 0;
                    viewRotation(statusRefresh, 0, 1, 1, animationDuration_instant);
                    viewOpacity(statusRefresh, 0, 1, 1, animationDuration_instant);
                    viewPosition(statusRefresh, 0, dp2px(context, -12),
                            1, 1, animationDuration_instant);
                    if (dragStarted && dragEnded) {
                        setImage();
                        switchImageLayer(true);
                    } else if (!dragStarted && !dragEnded) {
                        v.performClick();
                    }
                    dragStarted = false;
                    dragEnded = false;
            }
            return true;
        });

        rootView.setOnClickListener(v -> {
            currentTime = 0;
            if (bgAmbientView.getAlpha() < bgNormalOpacity) {
                leaveAmbient();
            }
            if (bgAmbientView.getAlpha() > bgAmbientOpacity) {
                showActionButtons();
            }
        });

        settingsButton.setOnClickListener(v -> {
            if (actionButtonContainer.getAlpha() == 1) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                hideActionButtons();
            }

        });
        ambientButton.setOnClickListener(v -> {
            if (actionButtonContainer.getAlpha() == 1) {
                goAmbient();
                hideActionButtons();
            }
        });
        textMain.setOnClickListener(v -> {
            hideActionButtons();
            if (debug.getVisibility() == View.GONE) {
                debug.setVisibility(View.VISIBLE);
            } else {
                debug.setVisibility(View.GONE);
            }
        });

        //permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //listen to sensor if exist
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        goImmersive(window);

        getDisplayMetrics(windowManager);
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                imagesList = getFileNameList();
            } catch (IOException e) {
                hint.setText(convertExceptionMessage(context, e));
            }
            if (!bgInit) {
                setImage();
                switchImageLayer(false);
                bgInit = true;
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

        //cancel mHandler to avoid memory leak
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
        if (type == Sensor.TYPE_PROXIMITY) {//actions when proximity sensor reacts
            proximityNear = value != proximitySensor.getMaximumRange();
        }
        if (type == Sensor.TYPE_LIGHT) {//actions when ambient light sensor reacts
            currentBrightness = value;
            //when low light
            if (currentBrightness <= nightBrightness) {
            }
            //when enough light
            else {
            }
        }
        setSleepStatus();
    }

    private Handler timeMessageHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @SuppressLint("SetTextI18n")
        @Override
        public boolean handleMessage(@NonNull Message message) {
            //get contents in message bundles
            int date = message.getData().getInt("date"),
                    dayR = message.getData().getInt("dayR"),
                    dayPeriod = message.getData().getInt("period"),
                    hour24 = message.getData().getInt("hour24"),
                    minute = message.getData().getInt("minute"),
                    second = message.getData().getInt("second"),
                    hour;

            String timeFormat = Settings.System.getString(contentResolver, Settings.System.TIME_12_24),
                    dayPeriodText;
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
            if (actionButtonContainer.getAlpha() == 1 && currentTime == hideButtonTimeout) {
                hideActionButtons();
            }
            if (currentTime == ambientTimeout && bgAmbientView.getAlpha() >= bgNormalOpacity) {
                goAmbient();
            }

            //set translation of main text
            if (bgAmbientView.getAlpha() <= bgAmbientOpacity) {
                viewPosition(textMain, nudgeX, nudgeY, 0f, 1f, 0);
            }

            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //switch background and reset current time
                if ((bgAmbientView.getAlpha() <= bgAmbientOpacity
                        && currentTime == switchImageTimeout)) {
                    setImage();
                    switchImageLayer(false);
                    currentTime = 0;
                }
            }
            return true;
        }
    });

    private void setImage() {
        DisplayDimensions dimensions=getDisplayMetrics(getWindowManager());
        if (imagesList != null && imagesList.size() > 0) {
            int newIndex;
            if (imagesList.size() > 1) {
                do {
                    newIndex = (int) (Math.random() * imagesList.size());
                } while (imageListIndex == newIndex);
                imageListIndex = newIndex;
            }
            Bitmap bitmap = decodeSampledBitmap(path + imagesList.get(imageListIndex),
                    dimensions.width, dimensions.height);
            if (upperImgVisible) {//points lower layer
                bgLower.setImageBitmap(bitmap);
            } else {//points upper layer
                bgUpper.setImageBitmap(bitmap);
            }
        }
    }

    private void switchImageLayer(boolean active) {
        View operateView;
        AnimatorListenerAdapter listener = null;
        if (upperImgVisible) {//at upper
            operateView = bgLower;
            viewOpacity(bgUpper, 0f, 0f, 1f, animationDuration_long);
            upperImgVisible = false;
        } else {//at lower
            operateView = bgUpper;
            viewOpacity(bgUpper, 1f, 0f, 1f, animationDuration_long);
            upperImgVisible = true;
        }
        if (active) {
            viewRotation(statusRefresh, 360, 1, 1, animationDuration_short);
            viewOpacity(statusRefresh, 1, 1, 1, animationDuration_short);
            viewPosition(statusRefresh, 0, 0, 1, 1, animationDuration_short);
            listener = new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    statusRefresh.setRotation(0);
                    viewOpacity(statusRefresh, 0, 1, 1, animationDuration_instant);
                    viewPosition(statusRefresh, 0, dp2px(context, -12),
                            1, 1, animationDuration_instant);
                }
            };
        }
        operateView.setScaleX(1.1f);
        operateView.setScaleY(1.1f);
        viewScale(operateView, 1f, 1f, 0.5f, 1f, animationDuration_long, listener);
    }

    private void goAmbient() {
        viewOpacity(bgAmbientView, bgAmbientOpacity, 1f / 3, 1f, animationDuration_normal);
        viewOpacity(textMain, textAmbientOpacity, 1f / 3, 1f, animationDuration_normal);
        viewOpacity(textSub, 0, 1f / 3, 1f, animationDuration_normal);
        viewPosition(textMain, nudgeX, nudgeY, 1f / 3, 1f, animationDuration_normal);
        viewPosition(textSub, -nudgeX, nudgeY, 1f / 3, 1f, animationDuration_normal);
        currentTime = 0;
    }

    private void leaveAmbient() {
        viewOpacity(bgAmbientView, bgNormalOpacity, 1, 1, animationDuration_short);
        viewOpacity(new View[]{textMain, textSub},
                textNormalOpacity, 1, 1, animationDuration_short, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });
        viewPosition(new View[]{textMain, textSub}, 0, 0, 1, 1, animationDuration_short);
    }

    private void showActionButtons() {
        viewOpacity(actionButtonContainer, 1f, 1, 1f, animationDuration_instant);
        viewPosition(actionButtonContainer, 0, 0, 1, 1f, animationDuration_instant);
    }

    private void hideActionButtons() {
        viewOpacity(actionButtonContainer, 0, 1, 1, animationDuration_instant);
        viewPosition(actionButtonContainer, 0, dp2px(context, -12),
                1, 1, animationDuration_instant);
    }

    private void setSleepStatus() {
        statusLight.setVisibility(currentBrightness <= nightBrightness ? View.VISIBLE : View.GONE);
        statusProximity.setVisibility(proximityNear ? View.VISIBLE : View.GONE);
        if (currentBrightness <= nightBrightness || proximityNear) {
            allowSleeping(window);
            viewOpacity(statusTimeout, 1, 1, 1, animationDuration_instant);
            viewPosition(statusTimeout, 0, 0, 1, 1, animationDuration_instant);
        } else {
            preventSleeping(window);
            viewOpacity(statusTimeout, 0, 1, 1, animationDuration_instant);
            viewPosition(statusTimeout, 0, dp2px(context, -12), 1, 1, animationDuration_instant);
        }
    }

    private void initializeHiddenButton(View view) {
        view.setTranslationY(dp2px(context, -12));
        view.setAlpha(0);
    }

    @SuppressLint("SetTextI18n")
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    hint.setText(getString(R.string.empty));
                } else {
                    hint.setText(getString(R.string.hint_storage_permission_explanation_1)
                            + path + getString(R.string.hint_storage_permission_explanation_2));
                }
            });

    private void setSafeArea(int left, int top, int right, int bottom){
        switch (windowManager.getDefaultDisplay().getRotation()){
            case Surface.ROTATION_0:
                setAbsoluteMargin(left, top, right, bottom);
                break;
            case Surface.ROTATION_90:
                setAbsoluteMargin(top, right, bottom, left);
                break;
            case Surface.ROTATION_180:
                setAbsoluteMargin(right, bottom, left, top);
                break;
            case Surface.ROTATION_270:
                setAbsoluteMargin(bottom, left, top, right);
                break;
            default:break;
        }
    }

    private void setAbsoluteMargin(int marginL, int marginT, int marginR, int margonB) {
        ConstraintLayout.LayoutParams topParams = (ConstraintLayout.LayoutParams) topContainer.getLayoutParams(),
                bottomParams = (ConstraintLayout.LayoutParams) bottomContainer.getLayoutParams();
        topParams.leftMargin = marginL;
        topParams.topMargin = marginT;
        topParams.rightMargin = marginR;
        bottomParams.leftMargin = marginL;
        bottomParams.rightMargin = marginR;
        bottomParams.bottomMargin = margonB;
        topContainer.setLayoutParams(topParams);
        bottomContainer.setLayoutParams(bottomParams);
    }
}