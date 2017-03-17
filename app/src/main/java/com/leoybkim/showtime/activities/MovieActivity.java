package com.leoybkim.showtime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.leoybkim.showtime.BuildConfig;
import com.leoybkim.showtime.R;
import com.leoybkim.showtime.adapters.MovieArrayAdapter;
import com.leoybkim.showtime.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieActivity extends AppCompatActivity {

    private static final String LOG_TAG = MovieActivity.class.getSimpleName();
    private ArrayList<Movie> mMovies;
    private MovieArrayAdapter mMovieArrayAdapter;
    private String nowPlayingUrl;

    @BindView(R.id.lvmovies) ListView listViewItems;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // ButterKnife :)
        ButterKnife.bind(this);

        // Setup custom adapter
        mMovies = new ArrayList<>();
        mMovieArrayAdapter = new MovieArrayAdapter(this, mMovies);
        listViewItems.setAdapter(mMovieArrayAdapter);

        // Setup API and make http request
        String movieDbApiKey = BuildConfig.MOVIE_DB_API_KEY;
        nowPlayingUrl = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + movieDbApiKey;
        getNowPlaying();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNowPlaying();
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // Launch MovieDetailsActivity on click
    @OnItemClick(R.id.lvmovies)
    void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Movie movie = mMovieArrayAdapter.getItem(i);
        Intent intent = new Intent(MovieActivity.this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
    }

    // Make API call to TheMovieDatabase and retrieves "now playing" movies in json object
    public void getNowPlaying() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(nowPlayingUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MovieActivity.this, "OkHttpClient error: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONArray moviesJsonResult = null;
                try {
                    // Clear pre-existing array list to avoid duplication
                    if (mMovies != null) { mMovies.clear(); }

                    // Parse json results
                    JSONObject responseJson = new JSONObject(response.body().string());
                    moviesJsonResult = responseJson.getJSONArray("results");
                    mMovies.addAll(Movie.fromJSONArray(moviesJsonResult));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Adapter on UI Tread to update the data
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Update adapter
                        mMovieArrayAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
