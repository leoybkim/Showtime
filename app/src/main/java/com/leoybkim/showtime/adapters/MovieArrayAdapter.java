package com.leoybkim.showtime.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leoybkim.showtime.R;
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

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    // View lookup cache
    static class ViewHolder {
        @BindView(R.id.poster) ImageView imageView;
        @BindView(R.id.main_title) TextView title;
        @BindView(R.id.main_overview) TextView overview;
        @BindView(R.id.progress_bar) ProgressBar progressBar;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MovieArrayAdapter(Context context, List<Movie> movies) {
        super(context, android.R.layout.simple_list_item_1, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        final ViewHolder viewHolder;

        if (convertView == null) {

            // if there is no view to re-use, inflate a new view
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_movie, parent, false);
            viewHolder = new ViewHolder(convertView);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(movie.getOriginalTitle());
        viewHolder.overview.setText(movie.getOverview());
        viewHolder.progressBar.setVisibility(View.VISIBLE);

        int orientation = getContext().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            Picasso.with(getContext()).load(movie.getPosterPath())
                    .transform(new RoundedCornersTransformation(30, 10)).into(viewHolder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub
                }
            });
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Picasso.with(getContext()).load(movie.getBackdropPath())
                    .transform(new RoundedCornersTransformation(20, 10)).into(viewHolder.imageView, new Callback() {
                @Override
                public void onSuccess() {
                    viewHolder.progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub

                }
            });
        }
        return convertView;
    }
}
