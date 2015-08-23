package com.gudong.appkit.utils.logger;

/**
 * Created by mao on 8/16/15.
 */
public class Setting {

    private static Setting setting = new Setting();

    private Setting(){
    }

    public synchronized static Setting getInstance(){
        return setting;
    }

    private static final String DEF_TAG = "logger";
    private static String mTag = DEF_TAG;
    private static LogLevel mLevel = LogLevel.FULL;

    public Setting setLogLevel(LogLevel level){
        mLevel = level;
        return setting;
    }

    public Setting setTag(String tag){
        mTag = tag;
        return setting;
    }

    public LogLevel getLevel() {
        return mLevel;
    }

    public String getTag(){
        return mTag;
    }

    public String getDefTag(){
        return DEF_TAG;
    }
}
