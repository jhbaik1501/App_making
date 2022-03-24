package com.example.app_making.Time;

public class Time {
    String name;
    String time;
    int resId;

    public Time(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public Time(String name, String time, int resId) {
        this.name = name;
        this.time = time;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
