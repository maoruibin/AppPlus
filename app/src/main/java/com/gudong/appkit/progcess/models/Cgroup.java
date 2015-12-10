package com.gudong.appkit.progcess.models;

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
