package com.example.fatemeh.udacityandroidcourseproject.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.fatemeh.udacityandroidcourseproject.R;
import com.example.fatemeh.udacityandroidcourseproject.models.ReviewResponseResult;

import java.util.List;

/**
 * Created by fatemeh on 04/01/16.
 */
public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ViewHolder>{

    private final Context mContext;
    private List<ReviewResponseResult> mReviews;

    private int mWidth;

    public MovieReviewsAdapter(Context context, List<ReviewResponseResult> reviews) {
        mContext = context;
        mReviews = reviews;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        mWidth = metrics.widthPixels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_movie_review, parent, false);

        return new ViewHolder(itemView, mWidth);
    }

    @Override
    public void onBindViewHolder(MovieReviewsAdapter.ViewHolder holder, int position) {
        final ReviewResponseResult review = mReviews.get(position);

        holder.mAuthorTextView.setText(review.getAuthor());
        holder.mContentTextView.setText(review.getContent());
        holder.mUrlTextView.setText(review.getUrl());

        holder.mContentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle(review.getAuthor());
                dialog.setMessage(review.getContent());
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mUrlTextView;
        public TextView mAuthorTextView;
        public TextView mContentTextView;

        public ViewHolder(View itemView, int maxWidth) {
            super(itemView);

            mUrlTextView = (TextView) itemView.findViewById(R.id.url_text_view);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.author_text_view);

            mContentTextView = (TextView) itemView.findViewById(R.id.content_expandable_text_view);

            mContentTextView.setMaxWidth(maxWidth - 90);
        }
    }

    public void updateData(List<ReviewResponseResult> reviews) {
        mReviews = reviews;
        notifyDataSetChanged();
    }
}
