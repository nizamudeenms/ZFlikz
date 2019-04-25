package com.zyta.zflikz.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.PersonActivity;
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
    ArrayList<FullCredits> fullCreditsArrayList = new ArrayList<FullCredits>();

    Map<String, List<FullCredits>> crewMap = new HashMap<String, List<FullCredits>>();


    public interface OnItemClickListener {
        void onItemClicked(FullCredits fullCredits);
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




        for (int i = 0; i < castList.size(); i++) {
            FullCredits fullCredits = new FullCredits();
            fullCredits.setCastId(castList.get(i).getCastId());
            fullCredits.setSub(castList.get(i).getCharacter());
            fullCredits.setCreditId(castList.get(i).getCreditId());
            fullCredits.setGender(castList.get(i).getGender());
            fullCredits.setId(castList.get(i).getId());
            fullCredits.setName(castList.get(i).getName());
            fullCredits.setOrder(castList.get(i).getOrder());
            if (castList.get(i).getProfilePath() != null) {
                fullCredits.setProfilePath(castList.get(i).getProfilePath());
            }
            fullCredits.setDepartment("Cast");
            fullCreditsArrayList.add(fullCredits);
        }

        System.out.println("fullCreditsArrayList.size() = " + fullCreditsArrayList.size());

        for (int i = 0; i < crewList.size(); i++) {
            FullCredits fullCredits = new FullCredits();
            fullCredits.setCastId(null);
            fullCredits.setSub(crewList.get(i).getJob());
            fullCredits.setCreditId(crewList.get(i).getCreditId());
            fullCredits.setGender(crewList.get(i).getGender());
            fullCredits.setId(crewList.get(i).getId());
            fullCredits.setName(crewList.get(i).getName());
            fullCredits.setOrder(null);
            if (crewList.get(i).getProfilePath() != null) {
                fullCredits.setProfilePath(crewList.get(i).getProfilePath().toString());
            }
            fullCredits.setDepartment(crewList.get(i).getDepartment());
            fullCreditsArrayList.add(fullCredits);
        }
        System.out.println("fullCreditsArrayList.size() = " + fullCreditsArrayList.size());

        for (FullCredits fullCredits : fullCreditsArrayList) {
            String key = fullCredits.getDepartment();
            if (crewMap.get(key) == null) {
                crewMap.put(key, new ArrayList<FullCredits>());
            }
            crewMap.get(key).add(fullCredits);
        }

    }


    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {
        System.out.println("position = " + position);
//        System.out.println("position = " + crewList.size());
//        final Crew crew = crewList.get(position);
//        final Crew nextCrew = crewList.get(position + 1);
        final FullCredits fullCredits = fullCreditsArrayList.get(position);
        final FullCredits nextFullCredits = fullCreditsArrayList.get(position +1);

        return !fullCredits.getDepartment().equals(nextFullCredits.getDepartment());
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
//        final Crew crew = crewList.get(itemPosition);
        final  FullCredits fullCredits = fullCreditsArrayList.get(itemPosition);

        holder.castNameTextView.setText(fullCredits.getSub());
        holder.castPersonTextView.setText(fullCredits.getName());
//        totalDept++;

        if (fullCreditsArrayList.get(itemPosition).getProfilePath() != null) {
            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w185" + fullCreditsArrayList.get(itemPosition).getProfilePath()).placeholder(R.drawable.zlikx_logo).into(holder.castImageView);
        } else {
            GlideApp.with(mContext)
                    .load(R.drawable.person_placeholder)
                    .into(holder.castImageView);
        }
//        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClicked(fullCredits));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonActivity.class);
                intent.putExtra("personId", fullCreditsArrayList.get(itemPosition).getId());
                mContext.startActivity(intent);
            }
        });

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

//        final Crew nextCrew = crewList.get(nextItemPosition);
        final FullCredits nextFullCredits = fullCreditsArrayList.get(nextItemPosition);
        System.out.println("crewMap.get(nextCrew.getDepartment()).size() = " + crewMap.get(nextFullCredits.getDepartment()).size());

        subheaderHolder.mSubheaderText.setText(nextFullCredits.getDepartment() + " (" + crewMap.get(nextFullCredits.getDepartment()).size() + ")");
    }

    @Override
    public int getItemSize() {
        return fullCreditsArrayList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
