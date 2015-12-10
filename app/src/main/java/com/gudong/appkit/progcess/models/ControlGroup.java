package com.gudong.appkit.progcess.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GuDong on 12/10/15 15:58.
 * Contact with 1252768410@qq.com.
 */
public class ControlGroup implements Parcelable {
    public final int id;
    public final String subsystems;
    public final String group;

    public ControlGroup(String line) {
        String[]fields = line.split(":");
        this.id = Integer.parseInt(fields[0]);
        this.subsystems = fields[1];
        this.group = fields[2];
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.subsystems);
        dest.writeString(this.group);
    }

    private ControlGroup(Parcel in) {
        this.id = in.readInt();
        this.subsystems = in.readString();
        this.group = in.readString();
    }

    public static final Creator<ControlGroup> CREATOR = new Creator<ControlGroup>() {
        public ControlGroup createFromParcel(Parcel source) {
            return new ControlGroup(source);
        }

        public ControlGroup[] newArray(int size) {
            return new ControlGroup[size];
        }
    };
}
