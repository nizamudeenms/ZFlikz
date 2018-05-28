package com.zyta.zflikz.model;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.zyta.zflikz.MovieDetailActivity;
import com.zyta.zflikz.R;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    String TAG = MovieAdapter.class.getSimpleName();
    Cursor movies;
    private Context mContext;
    private static final int LENGTH = 18;

    public MovieAdapter(Context context, Cursor movies) {
        mContext = context;
        this.movies = movies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mContext = parent.getContext();
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.gallery_thumbnail, parent, false);
        return new MyViewHolder(itemView, mContext, movies);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        movies.moveToPosition(position);
        ImageView im = holder.thumbnail;
        if (!movies.moveToPosition(position))
            return;
        RequestOptions glideoptions = new RequestOptions().placeholder(R.mipmap.ic_launcher);
        Glide.with(mContext).load(movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL))).apply(glideoptions).into(im);
    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        return movies.getCount();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView thumbnail;
        Cursor movies;
        Context context;

        public MyViewHolder(View view, Context mContext, Cursor movies) {
            super(view);
            this.movies = movies;
            this.context = mContext;
            view.setOnClickListener(this);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            movies.moveToPosition(position);

            Intent intent = new Intent(this.context, MovieDetailActivity.class);
            intent.putExtra("poster_url", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL)));
            intent.putExtra("backdrop_url", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL)));
            intent.putExtra("id", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
            intent.putExtra("overview", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
            intent.putExtra("release_date", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
            intent.putExtra("title", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            intent.putExtra("vote_average", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
            intent.putExtra("favorite", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));
            this.context.startActivity(intent);

        }
    }


}

