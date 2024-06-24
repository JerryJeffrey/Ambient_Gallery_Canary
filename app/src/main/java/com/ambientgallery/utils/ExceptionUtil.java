package com.ambientgallery.utils;

import static com.ambientgallery.utils.FileUtil.path;

import android.content.Context;

import com.ambientgallery.R;

public class ExceptionUtil {
    /**
     * @noinspection SpellCheckingInspection
     */
    public static String convertExceptionMessage(Context c,Exception e) {
        if (e.getMessage() != null) {
            switch (e.getMessage()) {
                case "Directory name occupied":
                    return c.getString(R.string.hint_directory_name_occupied_1) + path
                            + c.getString(R.string.hint_directory_name_occupied_2);
                case "Failed to create directory":
                    return c.getString(R.string.hint_failed_to_create_directory_1) + path
                            + c.getString(R.string.hint_failed_to_create_directory_2);
                case "Cannot clear nomedia":
                    return c.getString(R.string.hint_cannot_clear_nomedia_1) + path
                            + c.getString(R.string.hint_cannot_clear_nomedia_2);
                case "No available files":
                    return c.getString(R.string.hint_no_available_files_1) + path
                            + c.getString(R.string.hint_no_available_files_2);
                default:
                    break;
            }
        }
        return "";
    }
}
