/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.gudong.appkit.process.models;

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
