package com.serenegiant.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

public class MyChronometer extends Chronometer {

    public int msElapsed;
    public boolean isRunning = false;

    public MyChronometer(Context context) {
        super(context);
    }

    public MyChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyChronometer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getMsElapsed() {
        return msElapsed;
    }

    public void setMsElapsed(int ms) {
        setBase(getBase() - ms);
        msElapsed  = ms;
    }

    @Override
    public void start() {
        super.start();
        setBase(SystemClock.elapsedRealtime() - msElapsed);
        isRunning = true;
    }

    @Override
    public void stop() {
        super.stop();
        if(isRunning) {
            msElapsed = (int)(SystemClock.elapsedRealtime() - this.getBase());
        }
        isRunning = false;
    }
}
