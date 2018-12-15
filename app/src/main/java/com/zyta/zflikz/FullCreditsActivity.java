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
import com.zyta.zflikz.model.PersonCast;
import com.zyta.zflikz.model.PersonCrew;
import com.zyta.zflikz.model.PersonFullCreditsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jp.wasabeef.recyclerview.animators.FlipInTopXAnimator;

public class FullCreditsActivity extends AppCompatActivity implements FullCreditsAdapter.OnItemClickListener, PersonFullCreditsAdapter.OnItemClickListener {
    RecyclerView creditsRecyclerview;
    private ArrayList<Crew> crewList = new ArrayList<>();
    private ArrayList<Cast> castList = new ArrayList<>();

    private ArrayList<PersonCrew> personCrewArrayList = new ArrayList<>();
    private ArrayList<PersonCast> personCastArrayList = new ArrayList<>();

    private Comparator<Crew> crewComparator;
    private Comparator<PersonCrew> personCrewComparator;
    private FullCreditsAdapter mSectionedRecyclerAdapter;
    private PersonFullCreditsAdapter personFullCreditsAdapter;
    FloatingActionButton myFab;
    Boolean isExpanded = false;
    String type = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        myFab = (FloatingActionButton) findViewById(R.id.more_fab);

        creditsRecyclerview = findViewById(R.id.credits_recycler_view);
        creditsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        creditsRecyclerview.setItemAnimator(new FlipInTopXAnimator());
        type = getIntent().getSerializableExtra("type").toString();

        if (type.equals("person")) {
            personCrewArrayList = (ArrayList<PersonCrew>) getIntent().getSerializableExtra("crew_list");
            personCastArrayList = (ArrayList<PersonCast>) getIntent().getSerializableExtra("cast_list");
            this.personCrewComparator = (o1, o2) -> o1.getDepartment().compareTo(o2.getDepartment());
            if (!personCrewArrayList.isEmpty()) {
                Collections.sort(personCrewArrayList, personCrewComparator);
            }
            personFullCreditsAdapter = new PersonFullCreditsAdapter(personCastArrayList, personCrewArrayList, this);
            personFullCreditsAdapter.setOnItemClickListener(this);
            personFullCreditsAdapter.notifyDataChanged();
            creditsRecyclerview.setAdapter(personFullCreditsAdapter);
            if (!personCrewArrayList.isEmpty()) {
                personFullCreditsAdapter.collapseAllSections();
            } else{
                personFullCreditsAdapter.expandAllSections();
                isExpanded = true;
                myFab.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
            }

        } else if (type.equals("movie")) {
            crewList = (ArrayList<Crew>) getIntent().getSerializableExtra("crew_list");
            castList = (ArrayList<Cast>) getIntent().getSerializableExtra("cast_list");
            this.crewComparator = (o1, o2) -> o1.getDepartment().compareTo(o2.getDepartment());
            if (!crewList.isEmpty()) {
                Collections.sort(crewList, crewComparator);
            }
            mSectionedRecyclerAdapter = new FullCreditsAdapter(castList, crewList, this);
            mSectionedRecyclerAdapter.setOnItemClickListener(this);
            creditsRecyclerview.setAdapter(mSectionedRecyclerAdapter);
            mSectionedRecyclerAdapter.notifyDataChanged();
            mSectionedRecyclerAdapter.collapseAllSections();
        }

        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("isExpanded = " + isExpanded);
                if (!isExpanded) {
                    if (type.equals("person")) {
                        personFullCreditsAdapter.expandAllSections();
                    } else if (type.equals("movie")) {
                        mSectionedRecyclerAdapter.expandAllSections();
                    }
                    myFab.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    isExpanded = true;
                    Toast.makeText(FullCreditsActivity.this, "Expanded all Sections", Toast.LENGTH_SHORT).show();
                } else {
                    if (type.equals("person")) {
                        personFullCreditsAdapter.collapseAllSections();
                    } else if (type.equals("movie")) {
                        mSectionedRecyclerAdapter.collapseAllSections();
                    }
                    myFab.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    isExpanded = false;
                    Toast.makeText(FullCreditsActivity.this, "Collapsed all Sections", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
//
//    @Override
//    public void onItemClicked(FullCredits fullCredits) {
////        final int index = castList.indexOf(cast);
////        castList.remove(cast);
////        mSectionedRecyclerAdapter.notifyItemRemovedAtPosition(index);
//    }

    @Override
    public void onItemClicked(FullCredits fullCredits) {
//        final int index = castList.indexOf(fullCredits);
//        castList.remove(cast);
//        mSectionedRecyclerAdapter.notifyItemRemovedAtPosition(index);
    }

    @Override
    public void onSubheaderClicked(int position) {
        if (type.equals("person")) {
            if (personFullCreditsAdapter.isSectionExpanded(personFullCreditsAdapter.getSectionIndex(position))) {
                personFullCreditsAdapter.collapseSection(personFullCreditsAdapter.getSectionIndex(position));
            } else {
                personFullCreditsAdapter.expandSection(personFullCreditsAdapter.getSectionIndex(position));
            }
        } else if (type.equals("movie")) {
            if (mSectionedRecyclerAdapter.isSectionExpanded(mSectionedRecyclerAdapter.getSectionIndex(position))) {
                mSectionedRecyclerAdapter.collapseSection(mSectionedRecyclerAdapter.getSectionIndex(position));
            } else {
                mSectionedRecyclerAdapter.expandSection(mSectionedRecyclerAdapter.getSectionIndex(position));
            }
        }
    }
}
