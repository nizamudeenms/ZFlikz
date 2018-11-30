package com.zyta.zflikz;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import com.zyta.zflikz.model.MovieSearchAdapter;
import com.zyta.zflikz.model.SearchResults;
import com.zyta.zflikz.model.Searches;
import com.zyta.zflikz.utils.MovieAPI;
import com.zyta.zflikz.utils.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;


public class MovieSearchActivity extends AppCompatActivity implements MovieSearchAdapter.MovieSearchListener {
    private static final String TAG = MovieSearchActivity.class.getSimpleName();

    private CompositeDisposable disposable = new CompositeDisposable();
    private PublishSubject<String> publishSubject = PublishSubject.create();
    private PostService apiService;
    private MovieSearchAdapter mAdapter;
    private List<SearchResults> searchResultsArrayList = new ArrayList<>();
    private List<Searches> resultArrayList = new ArrayList<>();
    private String API_KEY = BuildConfig.TMDB_KEY;

    @BindView(R.id.movie_input_search)
    EditText movieInputSearch;


    @BindView(R.id.movie_recycler_view)
    RecyclerView movieRecyclerView;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_search);

        unbinder = ButterKnife.bind(this);


        mAdapter = new MovieSearchAdapter(this, resultArrayList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        movieRecyclerView.setLayoutManager(mLayoutManager);
        movieRecyclerView.setItemAnimator(new DefaultItemAnimator());
        movieRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        movieRecyclerView.setAdapter(mAdapter);

        whiteNotificationBar(movieRecyclerView);


        apiService = MovieAPI.getClient().create(PostService.class);

        DisposableObserver<SearchResults> observer = getSearchObserver();

        disposable.add(publishSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .switchMapSingle(new Function<String, Single<SearchResults>>() {
                    @Override
                    public Single<SearchResults> apply(String s) throws Exception {
                        return apiService.getSearchResults(API_KEY, s)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());

                    }
                })
                .subscribeWith(observer));


        // skipInitialValue() - skip for the first time when EditText empty
        disposable.add(
                RxTextView.textChangeEvents(movieInputSearch)
                        .skipInitialValue()
                        .debounce(300, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(searchMovieTextWatcher()));

        disposable.add(observer);

        // passing empty string fetches all the contacts
//        publishSubject.onNext(" ");

    }

    @Override
    public void onSearchSelected(Searches search) {

    }

    private DisposableObserver<SearchResults> getSearchObserver() {
        return new DisposableObserver<SearchResults>() {
            @Override
            public void onNext(SearchResults searchResults) {
                System.out.println(searchResults.getSearches().size());
                resultArrayList.clear();
                resultArrayList.addAll(searchResults.getSearches());
//                searchResultsArrayList.addAll(searchResults.getResults());
                for (Searches result : searchResults.getSearches()) {
                    if (result.getMediaType().equals("person")) {
                        System.out.println("person Name= " + result.getName());
                    } else if (result.getMediaType().equals("movie")) {
                        System.out.println("movie Name= " + result.getTitle());
                    }
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
                mAdapter.notifyDataSetChanged();
                publishSubject.onNext(" ");

            }

            @Override
            public void onComplete() {
                mAdapter.notifyDataSetChanged();
            }
        };
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        disposable.clear();
        unbinder.unbind();
        super.onDestroy();
    }

    private DisposableObserver<TextViewTextChangeEvent> searchMovieTextWatcher() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                Log.d(TAG, "Search query: " + textViewTextChangeEvent.text());
                if (textViewTextChangeEvent.text().length() != 0) {
                    publishSubject.onNext(textViewTextChangeEvent.text().toString());
                    resultArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                } else{
                    resultArrayList.clear();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
                publishSubject.onNext("");

            }

            @Override
            public void onComplete() {

            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

