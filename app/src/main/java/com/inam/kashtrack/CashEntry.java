package com.inam.kashtrack;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cash_entries")
public class CashEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private double amount;
    private String type; // "IN" or "OUT"
    private String description;
    private long timestamp;

    public CashEntry(double amount, String type, String description, long timestamp) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
