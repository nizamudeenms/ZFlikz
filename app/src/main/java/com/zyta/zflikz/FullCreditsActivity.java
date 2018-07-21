package com.zyta.zflikz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zyta.zflikz.model.Cast;
import com.zyta.zflikz.model.Crew;
import com.zyta.zflikz.model.FullCreditsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FullCreditsActivity extends AppCompatActivity implements FullCreditsAdapter.OnItemClickListener {
    RecyclerView creditsRecyclerview;
    private ArrayList<Crew> crewList = new ArrayList<>();
    private ArrayList<Cast> castList = new ArrayList<>();
    private Comparator<Crew> crewComparator;
    private FullCreditsAdapter mSectionedRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        creditsRecyclerview = findViewById(R.id.credits_recycler_view);
        creditsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        crewList = (ArrayList<Crew>) getIntent().getSerializableExtra("crew_list");

        castList = (ArrayList<Cast>) getIntent().getSerializableExtra("cast_list");


        System.out.println("crewList = " + crewList.size());
        System.out.println("castList = " + castList.size());


        this.crewComparator  = (o1, o2) -> o1.getDepartment().compareTo(o2.getDepartment());
        Collections.sort(crewList, crewComparator);

        mSectionedRecyclerAdapter = new FullCreditsAdapter(castList, crewList, this);


        mSectionedRecyclerAdapter.setOnItemClickListener(this);

        creditsRecyclerview.setAdapter(mSectionedRecyclerAdapter);

        mSectionedRecyclerAdapter.notifyDataChanged();
        mSectionedRecyclerAdapter.collapseAllSections();

    }

    @Override
    public void onItemClicked(Crew crew) {
//        final int index = castList.indexOf(cast);
//        castList.remove(cast);
//        mSectionedRecyclerAdapter.notifyItemRemovedAtPosition(index);
    }

    @Override
    public void onSubheaderClicked(int position) {
        if (mSectionedRecyclerAdapter.isSectionExpanded(mSectionedRecyclerAdapter.getSectionIndex(position))) {
            mSectionedRecyclerAdapter.collapseSection(mSectionedRecyclerAdapter.getSectionIndex(position));
        } else {
            mSectionedRecyclerAdapter.expandSection(mSectionedRecyclerAdapter.getSectionIndex(position));
        }
    }
}
