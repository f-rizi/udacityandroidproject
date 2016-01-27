package com.example.fatemeh.udacityandroidcourseproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fatemeh.udacityandroidcourseproject.R;
import com.example.fatemeh.udacityandroidcourseproject.models.VideoResponseResult;
import com.example.fatemeh.udacityandroidcourseproject.utilities.APIEndpoints;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fatemeh on 04/01/16.
 */

public class MovieVideosAdapter extends RecyclerView.Adapter<MovieVideosAdapter.ViewHolder>{

    private final Context mContext;
    private List<VideoResponseResult> mVideos;

    public MovieVideosAdapter(Context context, List<VideoResponseResult> videos) {
        this.mContext = context;
        this.mVideos = videos;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_movie_video, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final VideoResponseResult result = mVideos.get(position);

        final String videoKey = result.getKey();

        String url = APIEndpoints.YOUTUBE_IMAGE_URL;
        url = String.format(url, videoKey);

        Picasso.with(mContext).load(url).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoURL = APIEndpoints.YOUTUBE_VIDEO_URL + result.getKey();

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoURL));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVideos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.video_image);
        }
    }

    public void updateData(List<VideoResponseResult> videos) {
        this.mVideos = videos;
        notifyDataSetChanged();
    }
}
