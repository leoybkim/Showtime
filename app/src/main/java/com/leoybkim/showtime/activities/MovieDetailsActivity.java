package com.leoybkim.showtime.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.leoybkim.showtime.BuildConfig;
import com.leoybkim.showtime.R;
import com.leoybkim.showtime.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static com.leoybkim.showtime.R.id.progress_bar;


/**
 * Created by leo on 12/03/17.
 */

public class MovieDetailsActivity extends AppCompatActivity {

    public static final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();
    private String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // Prepare api call
        String movieDbApiKey = BuildConfig.MOVIE_DB_API_KEY;
        Movie movie = getIntent().getParcelableExtra("movie");
        String movieUrl = "https://api.themoviedb.org/3/movie/" + movie.getId() + "/videos?api_key=" + movieDbApiKey;

        ImageView backdrop = (ImageView) findViewById(R.id.backdrop);
        ImageView playButton = (ImageView) findViewById(R.id.play_button);
        TextView title = (TextView) findViewById(R.id.title);
        TextView overview = (TextView) findViewById(R.id.overview);
        TextView releaseDate = (TextView) findViewById(R.id.release_date);
        RatingBar ratings = (RatingBar) findViewById(R.id.rating_star);
        final ProgressBar progressBar = (ProgressBar) findViewById(progress_bar);
        final YouTubePlayerFragment youtubeFragment = (YouTubePlayerFragment)
                getFragmentManager().findFragmentById(R.id.youtube_fragment);

        // Load backdrop image
        Picasso.with(this).load(movie.getWideBackdropPath())
                .transform(new RoundedCornersTransformation(30, 10)).into(backdrop, new Callback() {
            @Override
            public void onSuccess() {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                // TODO Auto-generated method stub
            }
        });

        // Load movie details
        title.setText(movie.getOriginalTitle());
        overview.setText(movie.getOverview());
        releaseDate.setText(getString(R.string.release_date, movie.getReleaseDate()));
        ratings.setRating((float) movie.getRating());

        // Retrieve trailer url
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(movieUrl, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJsonResults = null;
                try {
                    movieJsonResults = response.getJSONArray("results");
                    videoId = movieJsonResults.getJSONObject(0).getString("key").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

        // Start youtube fragment on image touch
        playButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                youtubeFragment.initialize(com.leoybkim.showtime.BuildConfig.YOUTUBE_API_KEY,
                        new YouTubePlayer.OnInitializedListener() {
                            @Override
                            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                                YouTubePlayer youTubePlayer, boolean b) {
                                youTubePlayer.loadVideo(videoId);
                            }
                            @Override
                            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                                YouTubeInitializationResult youTubeInitializationResult) {

                            }
                        });
                return false;
            }
        });

    }
}