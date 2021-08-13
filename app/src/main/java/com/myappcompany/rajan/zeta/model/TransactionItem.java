package com.myappcompany.rajan.zeta.model;

import com.google.firebase.Timestamp;

public class TransactionItem {

    private String mId;
    private Timestamp mTimestamp;
    private boolean mStatus;
    private double mAmount;

    public TransactionItem(String id, Timestamp timestamp, boolean status, double amount) {
        mId = id;
        mTimestamp = timestamp;
        mStatus = status;
        mAmount = amount;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public Timestamp getTimestamp() {
        return mTimestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        mTimestamp = timestamp;
    }

    public String getStatus() {
        if(mStatus) {
            return "Success";
        }
        return "Failed";
    }

    public void setStatus(boolean status) {
        mStatus = status;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }
}
