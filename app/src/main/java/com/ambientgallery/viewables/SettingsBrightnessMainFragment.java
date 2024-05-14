package com.ambientgallery.viewables;

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
        if (getActivity() != null)
            prefs = getActivity().getSharedPreferences("MainPrefs", Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_settings_brightness_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            tabBar = getActivity().findViewById(R.id.settings_brightness_main_tab_container);
            textSeek = getActivity().findViewById(R.id.settings_brightness_main_text_opacity_seekbar);
            bgSeek = getActivity().findViewById(R.id.settings_brightness_main_bg_opacity_seekbar);
            textValue = getActivity().findViewById(R.id.settings_brightness_main_text_opacity_value);
            bgValue = getActivity().findViewById(R.id.settings_brightness_main_bg_opacity_value);
            textMinDesc = getActivity().findViewById(R.id.settings_brightness_main_text_opacity_desc_min);
            textMaxDesc = getActivity().findViewById(R.id.settings_brightness_main_text_opacity_desc_max);
            bgMinDesc = getActivity().findViewById(R.id.settings_brightness_main_bg_opacity_desc_min);
            bgMaxDesc = getActivity().findViewById(R.id.settings_brightness_main_bg_opacity_desc_max);
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
                        if (seekBar.getId() == R.id.settings_brightness_main_bg_opacity_seekbar) {//refers to 0
                            switch (currentTab) {
                                case 0://normal bg
                                    actualProgress = calculateDisplayProgress(prefsFloat(prefs, "bgAmbientOpacity"), 1f, progress);
                                    break;
                                case 1://ambient bg
                                    actualProgress = calculateDisplayProgress(0f, prefsFloat(prefs, "bgNormalOpacity"), progress);
                                    break;
                                default:
                                    break;
                            }
                            currentView = 0;
                            setPercentage(bgValue, actualProgress);
                        } else if (seekBar.getId() == R.id.settings_brightness_main_text_opacity_seekbar) {//refers to 2
                            switch (currentTab) {
                                case 0://normal text
                                    actualProgress = calculateDisplayProgress(prefsFloat(prefs, "textAmbientOpacity"), 1f, progress);
                                    break;
                                case 1://ambient text
                                    actualProgress = calculateDisplayProgress(0f, prefsFloat(prefs, "textNormalOpacity"), progress);
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
                    if (seekBar.getId() == R.id.settings_brightness_main_bg_opacity_seekbar) {
                        switch (currentTab) {
                            case 0://normal bg
                                setPrefs(prefs, "bgNormalOpacity", calculateDisplayProgress(prefsFloat(prefs, "bgAmbientOpacity"), 1f, progress) / 100f);
                                break;
                            case 1://ambient bg
                                setPrefs(prefs, "bgAmbientOpacity", calculateDisplayProgress(0f, prefsFloat(prefs, "bgNormalOpacity"), progress) / 100f);
                                break;
                            default:
                                break;
                        }
                    } else if (seekBar.getId() == R.id.settings_brightness_main_text_opacity_seekbar) {
                        switch (currentTab) {
                            case 0://normal text
                                setPrefs(prefs, "textNormalOpacity", calculateDisplayProgress(prefsFloat(prefs, "textAmbientOpacity"), 1f, progress) / 100f);
                                break;
                            case 1://ambient text
                                setPrefs(prefs, "textAmbientOpacity", calculateDisplayProgress(0f, prefsFloat(prefs, "textNormalOpacity"), progress) / 100f);
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

    //when minimum is 50% and maximum is 100%, this turns 50% on seekbar to 75% data
    private int calculateDisplayProgress(float min, float max, int actualProgress) {
        return (int) (min * 100 + (actualProgress * (max - min)));
    }

    //when minimum is 50% and maximum is 100%, this turns 75% data to 50% on seekbar
    private int calculateSeekProgress(float min, float max, int displayedProgress) {
        return (int) ((displayedProgress - min * 100) / (max - min));
    }

    @SuppressLint("SetTextI18n")
    private void setPercentage(TextView textView, float value) {
        textView.setText((int) (value * 100) + " %");
    }

    @SuppressLint("SetTextI18n")
    private void setPercentage(TextView textView, int percent) {
        textView.setText(percent + " %");
    }

    private void refreshSeekAndText(boolean animated) {
        switch (currentTab) {
            case 0://normal
                setPercentage(textValue, prefsFloat(prefs, "textNormalOpacity"));
                setPercentage(bgValue, prefsFloat(prefs, "bgNormalOpacity"));
                bgSeek.setProgress(calculateSeekProgress(prefsFloat(prefs, "bgAmbientOpacity"), 1, (int) (prefsFloat(prefs, "bgNormalOpacity") * 100)));
                textSeek.setProgress(calculateSeekProgress(prefsFloat(prefs, "textAmbientOpacity"), 1, (int) (prefsFloat(prefs, "textNormalOpacity") * 100)));
                textMinDesc.setText((int) (prefsFloat(prefs, "textAmbientOpacity") * 100) + " / Opacity of ambient mode");
                textMaxDesc.setText("100");
                bgMinDesc.setText((int) (prefsFloat(prefs, "bgAmbientOpacity") * 100) + " / Opacity of ambient mode");
                bgMaxDesc.setText("100");
                break;
            case 1://ambient
                setPercentage(textValue, prefsFloat(prefs, "textAmbientOpacity"));
                setPercentage(bgValue, prefsFloat(prefs, "bgAmbientOpacity"));
                bgSeek.setProgress(calculateSeekProgress(0, prefsFloat(prefs, "bgNormalOpacity"), (int) (prefsFloat(prefs, "bgAmbientOpacity") * 100)));
                textSeek.setProgress(calculateSeekProgress(0, prefsFloat(prefs, "textNormalOpacity"), (int) (prefsFloat(prefs, "textAmbientOpacity") * 100)));
                textMinDesc.setText("0");
                textMaxDesc.setText("Opacity of normal mode / " + (int) (prefsFloat(prefs, "textNormalOpacity") * 100));
                bgMinDesc.setText("0");
                bgMaxDesc.setText("Opacity of normal mode / " + (int) (prefsFloat(prefs, "bgNormalOpacity") * 100));
                break;
            default:
                break;
        }
    }
}
