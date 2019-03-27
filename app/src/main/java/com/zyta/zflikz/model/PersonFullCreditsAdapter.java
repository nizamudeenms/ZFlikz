package com.zyta.zflikz.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhukic.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.MovieDetailActivity;
import com.zyta.zflikz.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PersonFullCreditsAdapter  extends SectionedRecyclerViewAdapter<PersonFullCreditsAdapter.PersonCreditHeaderHolder,PersonFullCreditsAdapter.PersonCreditContentHolder> {

    ArrayList<PersonCast> personCastArrayList;
    ArrayList<PersonCrew> personCrewArrayList;
    OnItemClickListener onItemClickListener;
    Context mContext;
    ArrayList<FullCredits> fullCreditsArrayList = new ArrayList<FullCredits>();

    Map<String, List<FullCredits>> personCrewMap = new HashMap<String, List<FullCredits>>();


    public interface OnItemClickListener {
        void onItemClicked(FullCredits fullCredits);
        void onSubheaderClicked(int position);
    }

    static class PersonCreditHeaderHolder extends RecyclerView.ViewHolder {

        TextView mSubheaderText;
        ImageView mArrow;

        PersonCreditHeaderHolder(View itemView) {
            super(itemView);
            this.mSubheaderText = (TextView) itemView.findViewById(R.id.subheaderText);
            this.mArrow = (ImageView) itemView.findViewById(R.id.arrow);
        }

    }

    static class PersonCreditContentHolder extends RecyclerView.ViewHolder {

        TextView castNameTextView;
        TextView castPersonTextView;
        ImageView castImageView;

        PersonCreditContentHolder(View itemView) {
            super(itemView);
            this.castNameTextView = (TextView) itemView.findViewById(R.id.person_movie_name_text_view);
            this.castPersonTextView = (TextView) itemView.findViewById(R.id.person_movie_cast_name_text_view);
            this.castImageView = itemView.findViewById(R.id.person_movie_credits_diagonal_image);
        }
    }

    public PersonFullCreditsAdapter(ArrayList<PersonCast> personCastArrayList, ArrayList<PersonCrew> personCrewArrayList, Context context) {
        super();
        this.personCastArrayList = personCastArrayList;
        this.personCrewArrayList = personCrewArrayList;
        this.mContext = context;

        for (int i = 0; i < personCastArrayList.size(); i++) {
            FullCredits fullCredits = new FullCredits();
            fullCredits.setId(personCastArrayList.get(i).getId());
            fullCredits.setSub(personCastArrayList.get(i).getCharacter());
            fullCredits.setCreditId(personCastArrayList.get(i).getCreditId());
            fullCredits.setName(personCastArrayList.get(i).getTitle());
            if (personCastArrayList.get(i).getPosterPath() != null) {
                fullCredits.setProfilePath(personCastArrayList.get(i).getPosterPath());
            }
            fullCredits.setDepartment("Cast");
            fullCreditsArrayList.add(fullCredits);
        }

        System.out.println("fullCreditsArrayList.size() = " + fullCreditsArrayList.size());

        for (int i = 0; i < personCrewArrayList.size(); i++) {
            FullCredits fullCredits = new FullCredits();
            fullCredits.setSub(personCrewArrayList.get(i).getJob());
            fullCredits.setCreditId(personCrewArrayList.get(i).getCreditId());
            fullCredits.setId(personCrewArrayList.get(i).getId());
            fullCredits.setName(personCrewArrayList.get(i).getTitle());
            if (personCrewArrayList.get(i).getPosterPath() != null) {
                fullCredits.setProfilePath(personCrewArrayList.get(i).getPosterPath().toString());
            }
            fullCredits.setDepartment(personCrewArrayList.get(i).getDepartment());
            fullCreditsArrayList.add(fullCredits);
        }
        System.out.println("fullCreditsArrayList.size() = " + fullCreditsArrayList.size());

        for (FullCredits fullCredits : fullCreditsArrayList) {
            String key = fullCredits.getDepartment();
            if (personCrewMap.get(key) == null) {
                personCrewMap.put(key, new ArrayList<FullCredits>());
            }
            personCrewMap.get(key).add(fullCredits);
        }
    }

    @Override
    public boolean onPlaceSubheaderBetweenItems(int position) {
        System.out.println("position = " + personCrewArrayList.size());
        final FullCredits fullCredits = fullCreditsArrayList.get(position);
        final FullCredits nextFullCredits = fullCreditsArrayList.get(position +1);

        return !fullCredits.getDepartment().equals(nextFullCredits.getDepartment());
    }

    @Override
    public PersonCreditContentHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        return new PersonCreditContentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_movie_credit, parent, false));

    }

    @Override
    public PersonCreditHeaderHolder onCreateSubheaderViewHolder(ViewGroup parent, int viewType) {
        return new PersonCreditHeaderHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.content_header_credits, parent, false));

    }

    @Override
    public void onBindItemViewHolder(PersonCreditContentHolder holder, int itemPosition) {
        final  FullCredits fullCredits = fullCreditsArrayList.get(itemPosition);

        holder.castNameTextView.setText(fullCredits.getSub());
        holder.castPersonTextView.setText(fullCredits.getName());

        if (fullCreditsArrayList.get(itemPosition).getProfilePath() != null) {
            GlideApp.with(mContext).load("https://image.tmdb.org/t/p/w154" + fullCreditsArrayList.get(itemPosition).getProfilePath()).placeholder(R.drawable.zlikx_logo).into(holder.castImageView);
        } else {
            GlideApp.with(mContext)
                    .load(R.drawable.person_placeholder)
                    .into(holder.castImageView);
        }
//        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClicked(fullCredits));
        System.out.println("fullCreditsArrayList.get(itemPosition).getId() = " + fullCreditsArrayList.get(itemPosition).getId());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra("id", fullCreditsArrayList.get(itemPosition).getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindSubheaderViewHolder(PersonCreditHeaderHolder subheaderHolder, int nextItemPosition) {

        boolean isSectionExpanded = isSectionExpanded(getSectionIndex(subheaderHolder.getAdapterPosition()));

        if (isSectionExpanded) {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.getContext(), R.drawable.ic_keyboard_arrow_up_black_24dp));
        } else {
            subheaderHolder.mArrow.setImageDrawable(ContextCompat.getDrawable(subheaderHolder.itemView.getContext(), R.drawable.ic_keyboard_arrow_down_black_24dp));
        }

        subheaderHolder.itemView.setOnClickListener(v -> onItemClickListener.onSubheaderClicked(subheaderHolder.getAdapterPosition()));

        final FullCredits nextFullCredits = fullCreditsArrayList.get(nextItemPosition);
        System.out.println("crewMap.get(nextCrew.getDepartment()).size() = " + personCrewMap.get(nextFullCredits.getDepartment()).size());

        subheaderHolder.mSubheaderText.setText(nextFullCredits.getDepartment() + " (" + personCrewMap.get(nextFullCredits.getDepartment()).size() + ")");
    }

    @Override
    public int getItemSize() {
        return fullCreditsArrayList.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
