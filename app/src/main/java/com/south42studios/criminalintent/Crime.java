package com.south42studios.criminalintent;

import android.text.format.Time;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Jsin on 8/24/2015.
 */
public class Crime {
    private UUID mID;
    private String mTitle;
    private Date mDate;
    private Time mTime;
    private boolean mSolved;

    public Crime(){
        mID = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
