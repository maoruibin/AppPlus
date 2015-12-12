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

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by GuDong on 12/10/15 15:54.
 * Contact with 1252768410@qq.com.
 */
public class Cgroup extends ProcFile implements android.os.Parcelable {

    public final ArrayList<ControlGroup>groups;

    public static Cgroup get(int pid)throws IOException {
        return new Cgroup(String.format("/proc/%d/cgroup",pid));
    }

    private Cgroup(String path)throws IOException{
        super(path);
        String[]lines = content.split("\n");
        groups = new ArrayList<>();
        for (String line:lines){
            try{
                groups.add(new ControlGroup(line));
            }catch (Exception e){

            }
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeSerializable(this.groups);
    }

    private Cgroup(Parcel in) {
        super(in);
        this.groups = in.createTypedArrayList(ControlGroup.CREATOR);
    }

    public ControlGroup getGroup(String subsystem){
        for (ControlGroup group: groups) {
            String[]systems = group.subsystems.split(",");
            for (String name:systems){
                if(name.equals(subsystem)){
                    return group;
                }
            }
        }
        return null;
    }

    public static final Creator<Cgroup> CREATOR = new Creator<Cgroup>() {
        public Cgroup createFromParcel(Parcel source) {
            return new Cgroup(source);
        }

        public Cgroup[] newArray(int size) {
            return new Cgroup[size];
        }
    };
}
