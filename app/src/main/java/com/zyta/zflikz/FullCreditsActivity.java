package com.zyta.zflikz;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.zyta.zflikz.model.Cast;
import com.zyta.zflikz.model.Crew;
import com.zyta.zflikz.model.FullCredits;
import com.zyta.zflikz.model.FullCreditsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;

public class FullCreditsActivity extends AppCompatActivity implements FullCreditsAdapter.OnItemClickListener {
    RecyclerView creditsRecyclerview;
    private ArrayList<Crew> crewList = new ArrayList<>();
    private ArrayList<Cast> castList = new ArrayList<>();
    private Comparator<Crew> crewComparator;
    private FullCreditsAdapter mSectionedRecyclerAdapter;
    FloatingActionButton myFab;
    Boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        creditsRecyclerview = findViewById(R.id.credits_recycler_view);
        creditsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        crewList = (ArrayList<Crew>) getIntent().getSerializableExtra("crew_list");

        castList = (ArrayList<Cast>) getIntent().getSerializableExtra("cast_list");


        this.crewComparator = (o1, o2) -> o1.getDepartment().compareTo(o2.getDepartment());

        if (!crewList.isEmpty()) {
            Collections.sort(crewList, crewComparator);
        }

        mSectionedRecyclerAdapter = new FullCreditsAdapter(castList, crewList, this);

        mSectionedRecyclerAdapter.setOnItemClickListener(this);
        creditsRecyclerview.setItemAnimator(new FlipInTopXAnimator());
        creditsRecyclerview.setAdapter(mSectionedRecyclerAdapter);


        mSectionedRecyclerAdapter.notifyDataChanged();
        mSectionedRecyclerAdapter.collapseAllSections();

        myFab = (FloatingActionButton) findViewById(R.id.more_fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("isExpanded = " + isExpanded);
                if (!isExpanded) {
                    mSectionedRecyclerAdapter.expandAllSections();
                    myFab.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    isExpanded = true;
                    Toast.makeText(FullCreditsActivity.this, "Expanded all Sections", Toast.LENGTH_SHORT).show();
                } else {
                    mSectionedRecyclerAdapter.collapseAllSections();
                    myFab.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    isExpanded = false;
                    Toast.makeText(FullCreditsActivity.this, "Collapsed all Sections", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    @Override
    public void onItemClicked(FullCredits fullCredits) {
//        final int index = castList.indexOf(cast);
//        castList.remove(cast);
//        mSectionedRecyclerAdapter.notifyItemRemovedAtPosition(index);
    }

    @Override
    public void onSubheaderClicked(int position) {
        if (mSectionedRecyclerAdapter.isSectionExpanded(mSectionedRecyclerAdapter.getSectionIndex(position))) {
            mSectionedRecyclerAdapter.collapseSection(mSectionedRecyclerAdapter.getSectionIndex(position));
            Toast.makeText(this, "Collapsed all sections", Toast.LENGTH_SHORT).show();
        } else {
            mSectionedRecyclerAdapter.expandSection(mSectionedRecyclerAdapter.getSectionIndex(position));
            Toast.makeText(this, "Expanded all sections", Toast.LENGTH_SHORT).show();
        }
    }
}
