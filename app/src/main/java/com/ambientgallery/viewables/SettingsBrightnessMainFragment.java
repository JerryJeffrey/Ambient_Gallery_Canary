package com.ambientgallery.viewables;

import static com.ambientgallery.utils.SharedPrefsUtil.BG_AMBIENT_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.BG_NORMAL_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.MAIN_PREFS;
import static com.ambientgallery.utils.SharedPrefsUtil.TEXT_MAIN_AMBIENT_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.TEXT_MAIN_NORMAL_OPACITY;
import static com.ambientgallery.utils.SharedPrefsUtil.prefsFloat;
import static com.ambientgallery.utils.SharedPrefsUtil.setPrefs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ambientgallery.R;
import com.google.android.material.tabs.TabLayout;

public class SettingsBrightnessMainFragment extends Fragment {
    TextView bgValue, textValue, textMinDesc, textMaxDesc, bgMinDesc, bgMaxDesc;
    SeekBar textSeek, bgSeek;
    TabLayout tabBar;
    SharedPreferences prefs;
    int currentTab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        prefs = requireActivity().getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_brightness_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabBar = requireActivity().findViewById(R.id.settings_brightness_main_tab_container);
        textSeek = requireActivity().findViewById(R.id.settings_brightness_main_text_brightness_seekbar);
        bgSeek = requireActivity().findViewById(R.id.settings_brightness_main_bg_brightness_seekbar);
        textValue = requireActivity().findViewById(R.id.settings_brightness_main_text_brightness_value);
        bgValue = requireActivity().findViewById(R.id.settings_brightness_main_bg_brightness_value);
        textMinDesc = requireActivity().findViewById(R.id.settings_brightness_main_text_brightness_desc_min);
        textMaxDesc = requireActivity().findViewById(R.id.settings_brightness_main_text_brightness_desc_max);
        bgMinDesc = requireActivity().findViewById(R.id.settings_brightness_main_bg_brightness_desc_min);
        bgMaxDesc = requireActivity().findViewById(R.id.settings_brightness_main_bg_brightness_desc_max);
        currentTab = tabBar.getSelectedTabPosition();

        tabBar.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                refreshSeekAndText(true);
                setCardDisplayMode(currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int actualProgress = -1, currentView = -1;
                    Bundle resultBundle = new Bundle();
                    if (seekBar.getId() == R.id.settings_brightness_main_bg_brightness_seekbar) {//refers to 0
                        switch (currentTab) {
                            case 0://normal bg
                                actualProgress = calculateDisplayProgress(prefsFloat(prefs, BG_AMBIENT_OPACITY), 1f, progress);
                                break;
                            case 1://ambient bg
                                actualProgress = calculateDisplayProgress(0f, prefsFloat(prefs, BG_NORMAL_OPACITY), progress);
                                break;
                            default:
                                break;
                        }
                        currentView = 0;
                        setPercentage(bgValue, actualProgress);
                    } else if (seekBar.getId() == R.id.settings_brightness_main_text_brightness_seekbar) {//refers to 2
                        switch (currentTab) {
                            case 0://normal text
                                actualProgress = calculateDisplayProgress(prefsFloat(prefs, TEXT_MAIN_AMBIENT_OPACITY), 1f, progress);
                                break;
                            case 1://ambient text
                                actualProgress = calculateDisplayProgress(0f, prefsFloat(prefs, TEXT_MAIN_NORMAL_OPACITY), progress);
                                break;
                            default:
                                break;
                        }
                        currentView = 1;
                        setPercentage(textValue, actualProgress);
                    }
                    if (currentView < 0) throw new RuntimeException();
                    if (actualProgress < 0) throw new RuntimeException();
                    //give info to card
                    resultBundle.putInt("currentView", currentView);
                    resultBundle.putFloat("percentage", actualProgress / 100f);
                    getParentFragmentManager().setFragmentResult("viewOpacity", resultBundle);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int actualProgress = -1;
                int progress = seekBar.getProgress();
                if (seekBar.getId() == R.id.settings_brightness_main_bg_brightness_seekbar) {
                    switch (currentTab) {
                        case 0://normal bg
                            setPrefs(prefs, BG_NORMAL_OPACITY, calculateDisplayProgress(prefsFloat(prefs, BG_AMBIENT_OPACITY), 1f, progress) / 100f);
                            break;
                        case 1://ambient bg
                            setPrefs(prefs, BG_AMBIENT_OPACITY, calculateDisplayProgress(0f, prefsFloat(prefs, BG_NORMAL_OPACITY), progress) / 100f);
                            break;
                        default:
                            break;
                    }
                } else if (seekBar.getId() == R.id.settings_brightness_main_text_brightness_seekbar) {
                    switch (currentTab) {
                        case 0://normal text
                            setPrefs(prefs, TEXT_MAIN_NORMAL_OPACITY, calculateDisplayProgress(prefsFloat(prefs, TEXT_MAIN_AMBIENT_OPACITY), 1f, progress) / 100f);
                            break;
                        case 1://ambient text
                            setPrefs(prefs, TEXT_MAIN_AMBIENT_OPACITY, calculateDisplayProgress(0f, prefsFloat(prefs, TEXT_MAIN_NORMAL_OPACITY), progress) / 100f);
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        textSeek.setOnSeekBarChangeListener(seekBarChangeListener);
        bgSeek.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSeekAndText(false);
        setCardDisplayMode(currentTab);
    }

    private void setCardDisplayMode(int tabId) {
        Bundle resultBundle = new Bundle();
        resultBundle.putInt("tabNumber", tabId);
        getParentFragmentManager().setFragmentResult("displayMode", resultBundle);
    }

    //note: setMin() & setMax() requires API 26, while this project has to support API 14
    //when minimum is 50% and maximum is 100%, this turns 50% on seekbar to 75% data
    private int calculateDisplayProgress(float min, float max, int actualProgress) {
        return (int) (min * 100 + (actualProgress * (max - min)));
    }

    //when minimum is 50% and maximum is 100%, this turns 75% data to 50% on seekbar
    private int calculateSeekProgress(float min, float max, int displayedProgress) {
        return (int) ((displayedProgress - min * 100) / (max - min));
    }

    @SuppressLint("SetTextI18n")
    private void setPercentage(TextView textView, float percentage) {
        textView.setText((int) (percentage * 100) + " %");
    }

    @SuppressLint("SetTextI18n")
    private void setPercentage(TextView textView, int progress) {
        textView.setText(progress + " %");
    }

    @SuppressLint("SetTextI18n")
    private void refreshSeekAndText(boolean animated) {
        switch (currentTab) {
            case 0://normal
                setPercentage(textValue, prefsFloat(prefs, TEXT_MAIN_NORMAL_OPACITY));
                setPercentage(bgValue, prefsFloat(prefs, BG_NORMAL_OPACITY));
                bgSeek.setProgress(calculateSeekProgress(prefsFloat(prefs, BG_AMBIENT_OPACITY), 1, (int) (prefsFloat(prefs, BG_NORMAL_OPACITY) * 100)));
                textSeek.setProgress(calculateSeekProgress(prefsFloat(prefs, TEXT_MAIN_AMBIENT_OPACITY), 1, (int) (prefsFloat(prefs, TEXT_MAIN_NORMAL_OPACITY) * 100)));
                textMinDesc.setText((int) (prefsFloat(prefs, TEXT_MAIN_AMBIENT_OPACITY) * 100) + " / " + getString(R.string.settings_brightness_min_desc));
                textMaxDesc.setText("100");
                bgMinDesc.setText((int) (prefsFloat(prefs, BG_AMBIENT_OPACITY) * 100) + " / " + getString(R.string.settings_brightness_min_desc));
                bgMaxDesc.setText("100");
                break;
            case 1://ambient
                setPercentage(textValue, prefsFloat(prefs, TEXT_MAIN_AMBIENT_OPACITY));
                setPercentage(bgValue, prefsFloat(prefs, BG_AMBIENT_OPACITY));
                bgSeek.setProgress(calculateSeekProgress(0, prefsFloat(prefs, BG_NORMAL_OPACITY), (int) (prefsFloat(prefs, BG_AMBIENT_OPACITY) * 100)));
                textSeek.setProgress(calculateSeekProgress(0, prefsFloat(prefs, TEXT_MAIN_NORMAL_OPACITY), (int) (prefsFloat(prefs, TEXT_MAIN_AMBIENT_OPACITY) * 100)));
                textMinDesc.setText("0");
                textMaxDesc.setText(getString(R.string.settings_brightness_max_desc) + " / " + (int) (prefsFloat(prefs, TEXT_MAIN_NORMAL_OPACITY) * 100));
                bgMinDesc.setText("0");
                bgMaxDesc.setText(getString(R.string.settings_brightness_max_desc) + " / " + (int) (prefsFloat(prefs, BG_NORMAL_OPACITY) * 100));
                break;
            default:
                break;
        }
    }
}
