package com.zyta.zflikz.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.R;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {
    String TAG = MovieAdapter.class.getSimpleName();
    List<Result> movies;
    private Context mContext;
    private static final int LENGTH = 18;

    public MovieAdapter(Context context, List<Result> movies) {
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
        movies.get(position);
        ImageView im = holder.thumbnail;
//        if (!movies.get(position))
//            return;
        RequestOptions glideoptions = new RequestOptions().placeholder(R.mipmap.ic_launcher);
        System.out.println("Poster Path : "+movies.get(position).getPosterPath());
        if (movies.get(position).getPosterPath() == null) {
            GlideApp.with(mContext).load(R.drawable.no_image_available).placeholder(R.mipmap.ic_launcher).apply(glideoptions).into(im);
        }else {
            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w780"+movies.get(position).getPosterPath()).placeholder(R.mipmap.ic_launcher).apply(glideoptions).into(im);
        }
    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        return movies.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  {
        public ImageView thumbnail;
        List<Result> movies;
        Context context;

        public MyViewHolder(View view, Context mContext, List<Result> movies) {
            super(view);
            this.movies = movies;
            this.context = mContext;
//            view.setOnClickListener(this);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        }

//        @Override
//        public void onClick(View v) {
//            int position = getAdapterPosition();
//            movies.moveToPosition(position);
//
//            Intent intent = new Intent(this.context, MovieDetailActivity.class);
//            intent.putExtra("poster_url", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL)));
//            intent.putExtra("backdrop_url", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL)));
//            intent.putExtra("id", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
//            intent.putExtra("overview", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
//            intent.putExtra("release_date", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
//            intent.putExtra("title", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
//            intent.putExtra("vote_average", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
//            intent.putExtra("favorite", movies.getString(movies.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE)));
//            this.context.startActivity(intent);
//
//        }
    }


}

