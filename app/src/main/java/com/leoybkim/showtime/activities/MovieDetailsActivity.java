package com.leoybkim.showtime.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.leoybkim.showtime.BuildConfig;
import com.leoybkim.showtime.R;
import com.leoybkim.showtime.models.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.leoybkim.showtime.R.id.progress_bar;


/**
 * Created by leo on 12/03/17.
 */

public class MovieDetailsActivity extends YouTubeBaseActivity {

    public static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private String mVideoId;
    private YouTubePlayer mYouTubePlayer;
    private String mMovieUrl;

    @BindView(R.id.title) TextView title;
    @BindView(R.id.overview) TextView overview;
    @BindView(R.id.release_date) TextView releaseDate;
    @BindView(R.id.rating_star) RatingBar ratings;
    @BindView(R.id.youtube_player) YouTubePlayerView youtubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // ButterKnife :)
        ButterKnife.bind(this);

        // Prepare API call
        String movieDbApiKey = BuildConfig.MOVIE_DB_API_KEY;
        Movie movie = getIntent().getParcelableExtra("movie");
        mMovieUrl = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=" + movieDbApiKey;

        // Get orientation
        int orientation = getResources().getConfiguration().orientation;

        // Load movie details
        title.setText(movie.getOriginalTitle());
        overview.setText(movie.getOverview());
        releaseDate.setText(getString(R.string.release_date, movie.getReleaseDate()));
        ratings.setRating((float) movie.getRating());

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Horizontal orientation
            ImageView backdrop = (ImageView) findViewById(R.id.backdrop);
            ImageView playButton = (ImageView) findViewById(R.id.play_button);
            final ProgressBar progressBar = (ProgressBar) findViewById(progress_bar);

            // Load backdrop image
            Picasso.with(this).load(movie.getWideBackdropPath())
                    .transform(new RoundedCornersTransformation(30, 10)).into(backdrop, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub
                }
            });

            // Start youtube fragment on image touch
            playButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    youtubePlayerView.setVisibility(View.VISIBLE);
                    initializeYoutubePlayer();
                    return false;
                }
            });
        } else {
            // Vertical orientation
            initializeYoutubePlayer();
        }
    }

    // Cue trailers
    public void initializeYoutubePlayer() {
        youtubePlayerView.initialize(BuildConfig.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer = youTubePlayer;
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(mMovieUrl)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            // Retrieve video url from the movie specified
                            JSONObject responseJson = new JSONObject(response.body().string());
                            mVideoId = responseJson.getJSONArray("results").getJSONObject(0).getString("key");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mYouTubePlayer.cueVideo(mVideoId);
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(LOG_TAG, youTubeInitializationResult.toString());
            }
        });
    }
}