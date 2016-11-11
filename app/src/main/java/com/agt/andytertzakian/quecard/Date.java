package com.agt.andytertzakian.quecard;

import java.util.Locale;

/**
 * Created by Andy Tertzakian on 2016-10-16.
 */

public class Date {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public Date(int minute, int hour, int day, int month, int year ){
        setMinute(minute);
        setHour(hour);
        setDay(day);
        setMonth(month);
        setYear(year);
    }

    public String getFormatted() {
        boolean isPM = 12 <= hour && hour < 24;

        return String.format(Locale.US, "%02d:%02d %s",
                (hour == 12 || hour == 0) ? 12 : hour % 12, minute,
                isPM ? "PM" : "AM");
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }


    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}