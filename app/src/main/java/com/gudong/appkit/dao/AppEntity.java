package com.gudong.appkit.dao;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;

/**
 * app信息实体
 * Created by mao on 15/7/8.
 */
@Table("app_entity") public class AppEntity  implements Parcelable{
    @PrimaryKey(PrimaryKey.AssignType.AUTO_INCREMENT)
    @Column("_id")
    protected long id;

    @Column("appName") private String appName="";
    @Column("packageName") private String packageName="";
    @Column("versionName") private String versionName="";
    @Column("versionCode") private int versionCode=0;
    @Column("appIconData") private byte[] appIconData=null;
    @Column("srcPath") private String srcPath;


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
    }

    public AppEntity() {
    }

    private AppEntity(Parcel in) {
        this.id = in.readLong();
        this.appName = in.readString();
        this.packageName = in.readString();
        this.versionName = in.readString();
        this.versionCode = in.readInt();
        this.appIconData = in.createByteArray();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEntity appEntity = (AppEntity) o;

        return !(packageName != null ? !packageName.equals(appEntity.packageName) : appEntity.packageName != null);

    }

    @Override
    public int hashCode() {
        return packageName != null ? packageName.hashCode() : 0;
    }
}
