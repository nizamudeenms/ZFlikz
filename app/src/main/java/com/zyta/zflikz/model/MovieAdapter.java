package com.zyta.zflikz.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.MovieDetailActivity;
import com.zyta.zflikz.R;

import java.util.List;
import java.util.Random;

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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        movies.get(position);
        ImageView im = holder.thumbnail;
        TextView titleOnPoster = holder.titleOnPoster;
//        if (!movies.get(position))
//            return;
//        System.out.println("Poster Path : "+movies.get(position).getPosterPath());
        holder.setIsRecyclable(false);
        if (movies.get(position).getPosterPath() != null) {
            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w500" + movies.get(position).getPosterPath()).placeholder(R.drawable.zlikx_logo).into(im);
        } else {
            GlideApp.with(mContext).load(R.drawable.no_image_available).placeholder(R.drawable.zlikx_logo).into(im);
            titleOnPoster.setText(movies.get(position).getTitle());
            titleOnPoster.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
//                intent.putExtra("poster_url", "http://image.tmdb.org/t/p/w780" + movies.get(position).getPosterPath());
                intent.putExtra("poster_url", (movies.get(position).getPosterPath() == null) ? null : "http://image.tmdb.org/t/p/w500" + movies.get(position).getPosterPath());
                intent.putExtra("backdrop_url", (movies.get(position).getBackdropPath() == null) ? null : "http://image.tmdb.org/t/p/w500" + movies.get(position).getBackdropPath());
//                if (movies.get(position).getBackdropPath() == null) {
//                    intent.putExtra("backdrop_url",movies.get(position).getBackdropPath());
//                } else {
//                    intent.putExtra("backdrop_url", "http://image.tmdb.org/t/p/w780" + movies.get(position).getBackdropPath());
//                }
                intent.putExtra("id", movies.get(position).getId());
                intent.putExtra("overview", movies.get(position).getOverview());
                intent.putExtra("release_date", movies.get(position).getReleaseDate());
                intent.putExtra("title", movies.get(position).getTitle());
                intent.putExtra("vote_average", movies.get(position).getVoteAverage());
                intent.putExtra("favorite", movies.get(position).getVoteCount());
                mContext.startActivity(intent);
            }
        });
//        notifyItemChanged(position);
        setAnimation(holder.itemView, position);

    }

    int mLastPosition = -1;

    private void setAnimation(View itemView, int position) {
        if (position > mLastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            itemView.startAnimation(anim);
            mLastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        return movies.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView titleOnPoster;
        List<Result> movies;
        Context context;

        public MyViewHolder(View view, Context mContext, List<Result> movies) {
            super(view);
            this.movies = movies;
            this.context = mContext;
//            view.setOnClickListener(this);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            titleOnPoster = view.findViewById(R.id.movie_title_on_poster);
        }
    }
}

