package com.gudong.appkit.entity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * app信息实体
 * Created by mao on 15/7/8.
 */
public class AppEntity implements Parcelable{
    private String appName="";
    private String packageName="";
    private String versionName="";
    private int versionCode=0;
    private Bitmap appIcon=null;
    private String srcPath;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Bitmap getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Bitmap appIcon) {
        this.appIcon = appIcon;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeParcelable(this.appIcon, flags);
        dest.writeString(this.srcPath);
    }

    public AppEntity() {
    }

    private AppEntity(Parcel in) {
        this.appName = in.readString();
        this.packageName = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.appIcon = in.readParcelable(Drawable.class.getClassLoader());
        this.srcPath = in.readString();
    }

    public static final Creator<AppEntity> CREATOR = new Creator<AppEntity>() {
        public AppEntity createFromParcel(Parcel source) {
            return new AppEntity(source);
        }

        public AppEntity[] newArray(int size) {
            return new AppEntity[size];
        }
    };
}
