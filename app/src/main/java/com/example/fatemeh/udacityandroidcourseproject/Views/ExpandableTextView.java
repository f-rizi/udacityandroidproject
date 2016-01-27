package com.example.fatemeh.udacityandroidcourseproject.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.example.fatemeh.udacityandroidcourseproject.R;

/**
 * Created by fatemeh on 03/01/16.
 */

public class ExpandableTextView extends TextView implements View.OnClickListener{

    private int mCollapsedMaxLine;

    private boolean mExpanded;

    public ExpandableTextView(Context context) {
        this(context, null);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.expanding_text_view, 0, defStyleAttr);

        mCollapsedMaxLine = array.getInt(R.styleable.expanding_text_view_collapsed_max_lines, 8);
        mExpanded = array.getBoolean(R.styleable.expanding_text_view_expanded_view, false);

        array.recycle();

        setOnClickListener(this);

        if (mExpanded) {
            expand();
        } else {
            collapse();
        }
    }

    public void expand() {
        setMaxLines(Integer.MAX_VALUE);
        mExpanded = true;
    }

    public void collapse() {
        setMaxLines(mCollapsedMaxLine);
        mExpanded = false;
    }

    @Override
    public void onClick(View v) {
        if (mExpanded) {
            collapse();

        } else {
            expand();
        }
    }

    @Override
    public void setMaxWidth(int maxpixels) {
        super.setMaxWidth(maxpixels);
    }
}
