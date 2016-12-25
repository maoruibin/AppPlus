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

package com.gudong.appkit.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

/**
 * app信息实体
 * Created by mao on 15/7/8.
 */
@Table("app_entity") public class AppEntity  implements Parcelable{
    public static final String COLUMN_FAVORITE = "favorite";
    public static final String COLUMN_PACKAGE_NAME = "packageName";
    public static final String COLUMN_LAST_MODIFY_TIME = "lastModifyTime";
    public static final String COLUMN_TOTAL_SPACE = "totalSpace";
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Column("_id") protected long id;

    @Column("appName") private String appName="";
    @Column(COLUMN_PACKAGE_NAME) private String packageName="";
    @Column("versionName") private String versionName="";
    @Column("versionCode") private int versionCode=0;
    @Column("appIconData") private byte[] appIconData=null;
    @Column("srcPath") private String srcPath;
    @Column("uid") private int uid;

    //add in 2016.6.21
    @Default("false")
    @Column(COLUMN_FAVORITE) private boolean isFavorite;
    //add in 2016.8.1
    @Column(COLUMN_LAST_MODIFY_TIME) private long lastModifyTime;
    @Column(COLUMN_TOTAL_SPACE) private long totalSpace;

    private int status;

    public AppEntity() {
    }

    public AppEntity(String packageName) {
        this.packageName = packageName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public byte[] getAppIconData() {
        return appIconData;
    }

    public void setAppIconData(byte[] appIconData) {
        this.appIconData = appIconData;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEntity entity = (AppEntity) o;

        if (versionCode != entity.versionCode) return false;
        if (appName != null ? !appName.equals(entity.appName) : entity.appName != null)
            return false;
        if (packageName != null ? !packageName.equals(entity.packageName) : entity.packageName != null)
            return false;
        if (versionName != null ? !versionName.equals(entity.versionName) : entity.versionName != null)
            return false;
        if (appIconData != null ? !(appIconData.length == entity.appIconData.length) : entity.appIconData != null)
            return false;
        return srcPath != null ? srcPath.equals(entity.srcPath) : entity.srcPath == null;

    }

    @Override
    public int hashCode() {
        return packageName != null ? packageName.hashCode() : 0;
    }



    @Override
    public String toString() {
        return "AppEntity{" +
                "appIconData size =" + appIconData.length +
                ", id=" + id +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", srcPath='" + srcPath + '\'' +
                ", uid=" + uid +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.appName);
        dest.writeString(this.packageName);
        dest.writeString(this.versionName);
        dest.writeInt(this.versionCode);
        dest.writeByteArray(this.appIconData);
        dest.writeString(this.srcPath);
        dest.writeInt(this.uid);
        dest.writeByte(this.isFavorite ? (byte) 1 : (byte) 0);
        dest.writeLong(this.lastModifyTime);
        dest.writeLong(this.totalSpace);
        dest.writeInt(this.status);
    }

    protected AppEntity(Parcel in) {
        this.id = in.readLong();
        this.appName = in.readString();
        this.packageName = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.appIconData = in.createByteArray();
        this.srcPath = in.readString();
        this.uid = in.readInt();
        this.isFavorite = in.readByte() != 0;
        this.lastModifyTime = in.readLong();
        this.totalSpace = in.readLong();
        this.status = in.readInt();
    }

    public static final Creator<AppEntity> CREATOR = new Creator<AppEntity>() {
        @Override
        public AppEntity createFromParcel(Parcel source) {
            return new AppEntity(source);
        }

        @Override
        public AppEntity[] newArray(int size) {
            return new AppEntity[size];
        }
    };
}
