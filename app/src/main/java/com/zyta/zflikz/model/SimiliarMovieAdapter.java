package com.zyta.zflikz.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.MovieDetailActivity;
import com.zyta.zflikz.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimiliarMovieAdapter extends RecyclerView.Adapter<SimiliarMovieAdapter.SimiliarMovieHolder> {
    Context mContext;
    private ArrayList<SimiliarMovie> similiarMovieArrayList;

    public SimiliarMovieAdapter(Context context, ArrayList<SimiliarMovie> similiarMoviesList) {
        this.mContext = context;
        this.similiarMovieArrayList = similiarMoviesList;
    }

    @NonNull
    @Override
    public SimiliarMovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.content_similar_movie, parent, false);
        return new SimiliarMovieHolder(view, mContext, similiarMovieArrayList);
    }



    @Override
    public void onBindViewHolder(@NonNull SimiliarMovieHolder holder, int position) {
        similiarMovieArrayList.get(position );

        ImageView similiarMoviePosterImageView;
        TextView similiarMovieTitleTextView;
        similiarMoviePosterImageView = holder.similiarMoviePosterImageView;
        similiarMovieTitleTextView = holder.similiarMovieTitleTextView;

        similiarMovieTitleTextView.setText(similiarMovieArrayList.get(position).getTitle());

        if (similiarMovieArrayList.get(position).getPosterPath() != null) {
            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w185" + similiarMovieArrayList.get(position).getPosterPath()).centerCrop().placeholder(R.drawable.zlikx_logo).into(similiarMoviePosterImageView);
        }else {
            GlideApp.with(mContext).load(R.drawable.no_image_available).placeholder(R.drawable.zlikx_logo).into(similiarMoviePosterImageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra("id", similiarMovieArrayList.get(position).getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (null == similiarMovieArrayList) return 0;
        return similiarMovieArrayList.size();
    }

    public class SimiliarMovieHolder extends RecyclerView.ViewHolder {
        ImageView similiarMoviePosterImageView;
        TextView similiarMovieTitleTextView;
        ArrayList<SimiliarMovie> similiarMovieArrayList;
        Context mContext;

        public SimiliarMovieHolder(View itemView, Context mContext, ArrayList<SimiliarMovie> similiarMovieArrayList) {
            super(itemView);
            this.similiarMovieArrayList = similiarMovieArrayList;

            similiarMovieTitleTextView = itemView.findViewById(R.id.similiar_movie_title);
            similiarMoviePosterImageView = itemView.findViewById(R.id.similar_movie_image_view);

        }
    }
}
