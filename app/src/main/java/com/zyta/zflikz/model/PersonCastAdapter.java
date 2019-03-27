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

public class PersonCastAdapter extends RecyclerView.Adapter<PersonCastAdapter.PersonCreditsHolder> {

    private ArrayList<PersonCast> castArrayList = new ArrayList<PersonCast>();
    Context mContext;


    public PersonCastAdapter(Context context, ArrayList<PersonCast> castArrayList) {
        mContext = context;
        this.castArrayList = castArrayList;
    }

    @NonNull
    @Override
    public PersonCreditsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.content_crew, parent, false);
        return new PersonCreditsHolder(view, mContext, castArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonCreditsHolder holder, int position) {
        castArrayList.get(position);
        ImageView castImage = holder.castImage;
        TextView castName = holder.castName;
        TextView castChar = holder.castChar;
        if (castArrayList.get(position).getPosterPath() != null) {
            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w185" + castArrayList.get(position).getPosterPath()).centerCrop().placeholder(R.drawable.zlikx_logo).into(castImage);
        } else {
            GlideApp.with(mContext).load(R.drawable.person_placeholder).placeholder(R.drawable.zlikx_logo).into(castImage);
        }
        castName.setText(castArrayList.get(position).getTitle());
        castChar.setText(castArrayList.get(position).getCharacter());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra("id", castArrayList.get(position).getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (null == castArrayList) return 0;
        return castArrayList.size();
    }

    public class PersonCreditsHolder extends RecyclerView.ViewHolder {
        ImageView castImage;
        TextView castName, castChar;
        ArrayList<PersonCast> castArrayList;
        Context context;

        public PersonCreditsHolder(View itemView, Context mContext, ArrayList<PersonCast> castArrayList) {
            super(itemView);
            this.castArrayList = castArrayList;
            this.context = mContext;

            castImage = itemView.findViewById(R.id.cast_image_view);
            castName = itemView.findViewById(R.id.cast_name_text_view);
            castChar = itemView.findViewById(R.id.cast_char_text_view);
        }


    }
}