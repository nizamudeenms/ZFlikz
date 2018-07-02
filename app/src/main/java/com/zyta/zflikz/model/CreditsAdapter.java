package com.zyta.zflikz.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.R;

import java.util.ArrayList;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.CreditsHolder> {

    private ArrayList<Cast> castArrayList = new ArrayList<Cast>();
    Context mContext;

    public CreditsAdapter(Context context, ArrayList<Cast> castArrayList) {
        mContext = context;
        this.castArrayList = castArrayList;
    }

    @NonNull
    @Override
    public CreditsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.content_crew, parent, false);
        return new CreditsHolder(view,mContext, castArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditsHolder holder, int position) {
        castArrayList.get(position);
        ImageView castImage = holder.castImage;
        TextView castName = holder.castName;
        TextView castChar = holder.castChar;
        GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w185" + castArrayList.get(position).getProfilePath()).placeholder(R.drawable.zlikx_logo).into(castImage);
        castName.setText(castArrayList.get(position).getName());
        castChar.setText(castArrayList.get(position).getCharacter());

    }

    @Override
    public int getItemCount() {
        if (null == castArrayList) return 0;
        return castArrayList.size();
    }

    public class CreditsHolder extends RecyclerView.ViewHolder {
        ImageView castImage;
        TextView castName, castChar;
        ArrayList<Cast> castArrayList;
        Context context;


        public CreditsHolder(View itemView,Context mContext, ArrayList<Cast> castArrayList) {
            super(itemView);
            this.castArrayList = castArrayList;
            this.context = mContext;

            castImage = itemView.findViewById(R.id.cast_image_view);
            castName = itemView.findViewById(R.id.cast_name_text_view);
            castChar = itemView.findViewById(R.id.cast_char_text_view);
        }
    }
}
