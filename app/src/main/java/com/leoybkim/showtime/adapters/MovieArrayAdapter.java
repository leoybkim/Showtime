package com.leoybkim.showtime.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leoybkim.showtime.R;
import com.leoybkim.showtime.activities.MovieDetailsActivity;
import com.leoybkim.showtime.models.Movie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by leo on 07/03/17.
 */

public class MovieArrayAdapter extends RecyclerView.Adapter<MovieArrayAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.poster) ImageView imageView;
        @BindView(R.id.main_title) TextView title;
        @BindView(R.id.main_overview) TextView overview;
        @BindView(R.id.progress_bar) ProgressBar progressBar;

        // Constructor accepts the entire item row and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int poistion = getAdapterPosition();
            Movie movie = mMovies.get(poistion);
            Intent intent = new Intent(view.getContext(), MovieDetailsActivity.class);
            intent.putExtra("movie", movie);
            view.getContext().startActivity(intent);
        }
    }

    static private List<Movie> mMovies;
    private Context mContext;

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        mMovies = movies;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    // Inflate layout from XML and return the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View movieView = layoutInflater.inflate(R.layout.item_movie, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(movieView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Get the data model based on position
        Movie movie = mMovies.get(position);

        // Set item views based on your views and data model
        ImageView imageView = holder.imageView;
        TextView title = holder.title;
        TextView overview = holder.overview;
        final ProgressBar progressBar = holder.progressBar;

        title.setText(movie.getOriginalTitle());
        overview.setText(movie.getOverview());
        progressBar.setVisibility(View.VISIBLE);

        int orientation = getContext().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Picasso.with(getContext()).load(movie.getPosterPath())
                    .transform(new RoundedCornersTransformation(30, 10)).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Picasso.with(getContext()).load(movie.getWideBackdropPath())
                    .transform(new RoundedCornersTransformation(20, 10)).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }
}
