package com.ambientgallery.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class FileUtil {
    public static final String path =
            Environment.getExternalStorageDirectory().getPath() + "/Pictures/Ambient/";
    private static final File targetDirectory = new File(path);

    private static void checkAndCreateDirectory() throws IOException {
        if (targetDirectory.exists()) {
            if (!targetDirectory.isDirectory()) {
                throw new IOException("Directory name occupied");
            }
        } else if (!targetDirectory.mkdirs()) {
            throw new IOException("Failed to create directory");
        }
    }

    /** @noinspection SpellCheckingInspection*/
    private static void checkAndDeleteNomedia() throws IOException {
        File nomedia = new File(targetDirectory, ".nomedia");
        if (nomedia.exists()) {
            if (!nomedia.delete()) {
                throw new IOException("Cannot clear nomedia");
            }
        }
    }

    public static ArrayList<String> getFileNameList() throws IOException {
        checkAndCreateDirectory();
        checkAndDeleteNomedia();
        File[] relativeFileNameList = targetDirectory.listFiles(file -> file.isFile() &&
                !(file.isHidden()) &&
                !(file.getName().startsWith(".")) &&
                (file.getName().toLowerCase().endsWith(".jpg") ||
                file.getName().toLowerCase().endsWith(".jpeg") ||
                file.getName().toLowerCase().endsWith(".png") ||
                file.getName().toLowerCase().endsWith(".bmp"))
        );
        if (relativeFileNameList == null || relativeFileNameList.length == 0) {
            throw new IOException("No available files");
        }
        ArrayList<String> fileNameList = new ArrayList<>();
        for (File file : relativeFileNameList) {
            fileNameList.add(file.getName());
        }
        return fileNameList;
    }
}
