package com.zyta.zflikz.utils;

import com.zyta.zflikz.model.Credits;
import com.zyta.zflikz.model.ImageDetails;
import com.zyta.zflikz.model.MovieDetails;
import com.zyta.zflikz.model.PersonCreditDetails;
import com.zyta.zflikz.model.PersonDetails;
import com.zyta.zflikz.model.PostList;
import com.zyta.zflikz.model.ReviewDetails;
import com.zyta.zflikz.model.SearchResults;
import com.zyta.zflikz.model.SimiliarMovieDetails;
import com.zyta.zflikz.model.VideoDetails;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostService {
    @GET("movie/popular")
    Call<PostList> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int pageIndex);

    @GET("discover/movie")
    Call<PostList> getTrendingMovies(@Query("api_key") String apiKey,
                                     @Query("sort_by") String sortBy,
                                     @Query("primary_release_year") int primRelYear,
                                     @Query("with_original_language") String orgLang,
                                     @Query("page") int pageIndex);


    @GET("movie/{movie_id}/credits")
    Call<Credits> getCredits(@Path("movie_id") Integer movieId,
                             @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewDetails> getReviews(@Path("movie_id") Integer movieId,
                                   @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/similar")
    Call<SimiliarMovieDetails> getSimiliarMovies(@Path("movie_id") Integer movieId,
                                                 @Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<MovieDetails> getMovieDetails(@Path("movie_id") Integer movieId,
                                       @Query("api_key") String apiKey);


    @GET("movie/{movie_id}/images")
    Call<ImageDetails> getImageDetails(@Path("movie_id") Integer movieId,
                                       @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<VideoDetails> getVideoDetails(@Path("movie_id") Integer movieId,
                                       @Query("api_key") String apiKey);

    @GET("person/{person_id}")
    Call<PersonDetails> getPersonDetails(@Path("person_id") Integer personId,
                                         @Query("api_key") String apiKey);

    @GET("person/{person_id}/movie_credits")
    Call<PersonCreditDetails> getPersonCredits(@Path("person_id") Integer personId,
                                               @Query("api_key") String apiKey);

    @GET("search/multi")
    Single<SearchResults> getSearchResults(@Query("api_key") String apiKey,
                                           @Query("query") String query);




}