/*
 *     Copyright (c) 2015 GuDong
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in all
 *     copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *     SOFTWARE.
 */

package com.gudong.appkit.utils;

import android.os.Environment;

import com.gudong.appkit.dao.AppEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Callable;

import rx.Observable;

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

    /**
     * copy file
     * @param source
     * @param dest
     * @throws IOException
     */
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
     * copy file
     * @param source
     * @param dest
     * @throws IOException
     */
    public static Observable<Boolean> copyFileUsingFileChannelsAsyn(final File source,final  File dest)
            throws IOException {
        return RxUtil.makeObservable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
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
                return true;
            }
        });
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

    public static boolean deleteExportedFile(AppEntity entity){
        if(entity == null)return false;
        File file = new File(entity.getSrcPath());
        return file.delete();
    }
}

