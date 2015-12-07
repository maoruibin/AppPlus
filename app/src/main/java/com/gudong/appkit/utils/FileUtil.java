package com.gudong.appkit.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by mao on 7/19/15.
 */
public class FileUtil {
    public static final String KEY_EXPORT_DIR_OLDER = "App+导出目录";
    public static final String KEY_EXPORT_DIR = "AppPlus";
    /**
     * 判断sd卡是不是处于挂载状态
     * @return
     */
    public static boolean isSdCardOnMounted(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得sd卡根路径
     * @return
     */
    public static String getSDPath(){
        return Environment.getExternalStorageDirectory().toString();
    }

    public static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    /**
     * create a directory
     * @param parent parent directory
     * @param directoryName child directory name,it must a directory not file name
     */
    public static File createDir(String parent,String directoryName){
        File createdFile = new File(parent, directoryName);
        if (!createdFile.exists()) {
            createdFile.mkdir();
        }
        return createdFile;
    }
}

