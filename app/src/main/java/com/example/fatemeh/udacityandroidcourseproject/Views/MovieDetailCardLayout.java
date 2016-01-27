package com.example.fatemeh.udacityandroidcourseproject.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fatemeh.udacityandroidcourseproject.R;

/**
 * Created by fatemeh on 03/01/16.
 */
public class MovieDetailCardLayout extends LinearLayout {

    private TextView mTitleTextView;
    private TextView mInfoTextView;

    public MovieDetailCardLayout(Context context) {
        this(context, null);
    }

    public MovieDetailCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieDetailCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.movie_detail_card, this, true);

        mTitleTextView = (TextView) findViewById(R.id.title);
        mInfoTextView = (TextView) findViewById(R.id.info);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MovieDetailItemInfoLayout);
        final String title = array.getString(R.styleable.MovieDetailItemInfoLayout_item_title);
        final boolean seeMoreVisibility = array.getBoolean(R.styleable.MovieDetailItemInfoLayout_info_visibility, false);

        if (!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }

        if (seeMoreVisibility) {
            mInfoTextView.setVisibility(VISIBLE);
        }


        array.recycle();
    }

    public void setInfoVisibility(boolean visible) {
        mInfoTextView.setVisibility(visible ? VISIBLE : GONE);
    }

    public void setInfoText(String info) {
        mInfoTextView.setText(info);
    }

    public void setInfoOnClickListener(OnClickListener listener) {
        mInfoTextView.setOnClickListener(listener);
    }

    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }

    public void setTitle(int titleResId) {
        setTitle(getResources().getString(titleResId));
    }

}
