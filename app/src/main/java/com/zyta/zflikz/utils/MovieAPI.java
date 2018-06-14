package com.zyta.zflikz.utils;

import com.zyta.zflikz.BuildConfig;
import com.zyta.zflikz.model.PostList;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MovieAPI {

    public static final String key = BuildConfig.TMDB_KEY;
    public static final String url = "https://api.themoviedb.org/3/";

    public static PostService postService = null;

    public static PostService getService()
    {
        if(postService == null)
        {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            postService = retrofit.create(PostService.class);
        }
        return postService;
    }

    public interface PostService {
        @GET("movie/popular")
        Call<PostList> getPopularMovies(@Query("api_key") String apiKey, @Query("page") int pageIndex);

        @GET("discover/movie")
        Call<PostList> getTrendingMovies(@Query("api_key") String apiKey,
                                         @Query("sort_by") String sortBy,
                                         @Query("primary_release_year") int primRelYear,
                                         @Query("with_original_language") String orgLang,
                                         @Query("page") int pageIndex);

    }


}
