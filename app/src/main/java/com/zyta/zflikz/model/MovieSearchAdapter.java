package com.zyta.zflikz.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zyta.zflikz.MovieDetailActivity;
import com.zyta.zflikz.PersonActivity;
import com.zyta.zflikz.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.MovieSearchViewHolder> {

    Context context;
    private List<Searches> resultsList;
    private MovieSearchListener listener;


    public MovieSearchAdapter(Context context, List<Searches> searchSearchessList, MovieSearchListener listener) {
        this.context = context;
        this.resultsList = searchSearchessList;
        this.listener = listener;
    }

    @Override
    public MovieSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View iteView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_search_item, parent, false);

        return new MovieSearchViewHolder(iteView);
    }

    @Override
    public void onBindViewHolder(MovieSearchViewHolder holder, int position) {
        final Searches result = resultsList.get(position);
        if (result.getMediaType().equals("person")) {
            System.out.println("person Name= " + result.getName());
            holder.searchName.setText(result.getName());
            if (result.getProfilePath() != null) {
                System.out.println("Poster result.getProfilePath() = " + "http://image.tmdb.org/t/p/w45" + result.getProfilePath());
                Glide.with(context)
                        .load("http://image.tmdb.org/t/p/w45" + result.getProfilePath())
                        .into(holder.searchImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_launcher_foreground)
                        .into(holder.searchImage);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, PersonActivity.class);
                    intent.putExtra("personId", result.getId());
                    context.startActivity(intent);
                }
            });
        } else if (result.getMediaType().equals("movie")) {
            System.out.println("movie Name= " + result.getTitle());
            holder.searchName.setText(result.getTitle());
            if (result.getPosterPath() != null) {
                System.out.println("Poster result.getProfilePath() = " + "http://image.tmdb.org/t/p/w92" + result.getPosterPath());
                Glide.with(context)
                        .load("http://image.tmdb.org/t/p/w92" + result.getPosterPath())
                        .into(holder.searchImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_launcher_foreground)
                        .into(holder.searchImage);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra("id", result.getId());
                    context.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }


    public interface MovieSearchListener {
        void onSearchSelected(Searches searches);
    }

    public class MovieSearchViewHolder extends RecyclerView.ViewHolder {
        TextView searchName;
        ImageView searchImage;

        public MovieSearchViewHolder(View itemView) {
            super(itemView);
            searchName = itemView.findViewById(R.id.search_name);
            searchImage = itemView.findViewById(R.id.search_thumbnail);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onSearchSelected(resultsList.get(getAdapterPosition()));
                }
            });
        }
    }
}
