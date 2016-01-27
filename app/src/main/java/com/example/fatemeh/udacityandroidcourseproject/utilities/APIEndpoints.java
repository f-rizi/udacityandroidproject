package com.example.fatemeh.udacityandroidcourseproject.utilities;

import com.example.fatemeh.udacityandroidcourseproject.models.DiscoverMovieResponse;
import com.example.fatemeh.udacityandroidcourseproject.models.ReviewResponse;
import com.example.fatemeh.udacityandroidcourseproject.models.VideoResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by fatemeh on 28/12/15.
 */

public interface APIEndpoints {

    String API = "";
    String BASE_URL = "http://api.themoviedb.org/";
    String POSTER_PATH_URL = "http://image.tmdb.org/t/p/w342";
    String POSTER_H_PATH_URL = "http://image.tmdb.org/t/p/w1280";
    String YOUTUBE_VIDEO_URL = "https://www.youtube.com/watch?v=";
    String YOUTUBE_IMAGE_URL = "http://img.youtube.com/vi/%s/mqdefault.jpg";

    @GET("/3/discover/movie")
    Call<DiscoverMovieResponse> getDiscoverMovieResponse(@Query("sort_by") String sort, @Query("api_key") String api);

    @GET("/3/movie/{movie_id}/videos")
    Call<VideoResponse> getMovieVideoResponse(@Path("movie_id") String movieId, @Query("api_key") String api);

    @GET("/3/movie/{movie_id}/reviews")
    Call<ReviewResponse> getMovieReviewResponse(@Path("movie_id") String movieId, @Query("api_key") String api);
}
