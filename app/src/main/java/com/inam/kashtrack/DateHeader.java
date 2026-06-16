package com.inam.kashtrack;

public class DateHeader {
    public String dateLabel;
    public int count;
    public double out;
    public double in;

    public DateHeader(String dateLabel, int count, double out, double in) {
        this.dateLabel = dateLabel;
        this.count = count;
        this.out = out;
        this.in = in;
    }
}
