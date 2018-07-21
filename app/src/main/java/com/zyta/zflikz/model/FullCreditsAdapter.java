package com.zyta.zflikz.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullCreditsAdapter extends SectionedRecyclerViewAdapter<FullCreditsAdapter.CreditHeaderHolder, FullCreditsAdapter.CreditContentHolder> {

    ArrayList<Cast> castList;
    ArrayList<Crew> crewList;
    OnItemClickListener onItemClickListener;
    Context mContext;
    int totalDept = 0;

    Map<String, List<Crew>> crewMap = new HashMap<String, List<Crew>>();


    public interface OnItemClickListener {
        void onItemClicked(Crew crew);

        void onSubheaderClicked(int position);
    }


    static class CreditHeaderHolder extends RecyclerView.ViewHolder {

//        private static Typeface meduiumTypeface = null;

        TextView mSubheaderText;
        ImageView mArrow;

        CreditHeaderHolder(View itemView) {
            super(itemView);
            this.mSubheaderText = (TextView) itemView.findViewById(R.id.subheaderText);
            this.mArrow = (ImageView) itemView.findViewById(R.id.arrow);
//
//            if(meduiumTypeface == null) {
//                meduiumTypeface = Typeface.createFromAsset(itemView.getContext().getAssets(), "Roboto-Medium.ttf");
//            }
//            this.mSubheaderText.setTypeface(meduiumTypeface);


        }

    }

    static class CreditContentHolder extends RecyclerView.ViewHolder {

        TextView castNameTextView;
        TextView castPersonTextView;
        ImageView castImageView;

        CreditContentHolder(View itemView) {
            super(itemView);
            this.castNameTextView = (TextView) itemView.findViewById(R.id.cast_name);
            this.castPersonTextView = (TextView) itemView.findViewById(R.id.cast_person);
            this.castImageView = itemView.findViewById(R.id.credits_diagonal_image);


        }
    }

    public FullCreditsAdapter(ArrayList<Cast> castList, ArrayList<Crew> crewList, Context context) {
        super();
        this.castList = castList;
        this.crewList = crewList;
        this.mContext = context;

        for (Crew crew : crewList) {
            String key = crew.getDepartment();
            if (crewMap.get(key) == null) {
                crewMap.put(key, new ArrayList<Crew>());
            }
            crewMap.get(key).add(crew);
        }

    }


    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {
        System.out.println("position = " + position);
        System.out.println("position = " + crewList.size());
        final Crew crew = crewList.get(position);
        final Crew nextCrew = crewList.get(position+1);


        return !crew.getDepartment().equals(nextCrew.getDepartment());
    }

    @Override
    public CreditContentHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new CreditContentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_credits, parent, false));

    }

    @Override
    public CreditHeaderHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        return new CreditHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_header_credits, parent, false));
    }

    @Override
    public void onBindItemViewHolder(CreditContentHolder holder, int itemPosition) {
        final Crew crew = crewList.get(itemPosition);


        holder.castNameTextView.setText(crew.getJob());
        holder.castPersonTextView.setText(crew.getName());
//        totalDept++;

        if (crewList.get(itemPosition).getProfilePath() != null) {
            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w185" + crewList.get(itemPosition).getProfilePath()).placeholder(R.drawable.zlikx_logo).into(holder.castImageView);
        } else {
            GlideApp.with(mContext)
                    .load(R.drawable.no_image_available)
                    .into(holder.castImageView);
        }
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClicked(crew));


    }

    @Override
    public void onBindSubheaderViewHolder(CreditHeaderHolder subheaderHolder, int nextItemPosition) {

        boolean isSectionExpanded = isSectionExpanded(getSectionIndex(subheaderHolder.getAdapterPosition()));

        if (isSectionExpanded) {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.getContext(), R.drawable.ic_keyboard_arrow_up_black_24dp));
        } else {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.getContext(), R.drawable.ic_keyboard_arrow_down_black_24dp));
        }

        subheaderHolder.itemView.setOnClickListener(v -> onItemClickListener.onSubheaderClicked(subheaderHolder.getAdapterPosition()));

        final Crew nextCrew = crewList.get(nextItemPosition);
        System.out.println("crewMap.get(nextCrew.getDepartment()).size() = " + crewMap.get(nextCrew.getDepartment()).size());

        subheaderHolder.mSubheaderText.setText(nextCrew.getDepartment() + " (" + crewMap.get(nextCrew.getDepartment()).size() + ")");
    }

    @Override
    public int getItemSize() {
        return crewList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
