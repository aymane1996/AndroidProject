package com.aymane.android.Models;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MapCardView extends android.support.v7.widget.CardView {
    public MapCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
