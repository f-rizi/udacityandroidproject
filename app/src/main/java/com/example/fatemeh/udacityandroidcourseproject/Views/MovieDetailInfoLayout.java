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
public class MovieDetailInfoLayout extends LinearLayout {

    private final TextView mTitleTextView;
    private final TextView mContentTextView;

    public MovieDetailInfoLayout(Context context) {
        this(context, null);
    }

    public MovieDetailInfoLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MovieDetailInfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.include_movie_detail_info, this, true);

        mTitleTextView = (TextView) findViewById(R.id.title);
        mContentTextView = (TextView) findViewById(R.id.content);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MovieDetailInfoLayout);

        final String title = array.getString(R.styleable.MovieDetailInfoLayout_detail_title);

        if(!TextUtils.isEmpty(title)) {
            mTitleTextView.setText(title);
        }

        array.recycle();
    }

    public TextView getTitleTextView() {
        return mTitleTextView;
    }

    public TextView getContentTextView() {
        return mContentTextView;
    }

    public void setContentText(CharSequence text) {
        mContentTextView.setText(text);
    }


}
