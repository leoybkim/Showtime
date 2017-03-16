package com.leoybkim.showtime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.leoybkim.showtime.BuildConfig;
import com.leoybkim.showtime.R;
import com.leoybkim.showtime.adapters.MovieArrayAdapter;
import com.leoybkim.showtime.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.leoybkim.showtime.R.id.swipeContainer;

public class MovieActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieActivity.class.getSimpleName();
    private ArrayList<Movie> mMovies;
    private MovieArrayAdapter mMovieArrayAdapter;
    private ListView mListViewItems;
    private String nowPlayingUrl;
    private SwipeRefreshLayout mSwipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Find view resources
        mSwipeContainer = (SwipeRefreshLayout) findViewById(swipeContainer);
        mListViewItems = (ListView) findViewById(R.id.lvmovies);

        // Setup custom adapter
        mMovies = new ArrayList<>();
        mMovieArrayAdapter = new MovieArrayAdapter(this, mMovies);
        mListViewItems.setAdapter(mMovieArrayAdapter);

        // Setup API and make http request
        String movieDbApiKey = BuildConfig.MOVIE_DB_API_KEY;
        nowPlayingUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + movieDbApiKey;
        getNowPlaying();

        // Launch MovieDetailsActivity on click
        mListViewItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Movie movie = mMovieArrayAdapter.getItem(i);
                Intent intent = new Intent(MovieActivity.this, MovieDetailsActivity.class);
                intent.putExtra("movie", movie);
                startActivity(intent);
            }
        });

        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNowPlaying();
                mSwipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        mSwipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Make API call to TheMovieDatabase and retrieves "now playing" movies in json object
    public void getNowPlaying() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(nowPlayingUrl, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJsonResults = null;
                try {
                    // Clear pre-existing array list to avoid duplication
                    if (mMovies != null) { mMovies.clear(); }

                    // Parse json results
                    movieJsonResults = response.getJSONArray("results");
                    mMovies.addAll(Movie.fromJSONArray(movieJsonResults));

                    // Update adapter
                    mMovieArrayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }
}
