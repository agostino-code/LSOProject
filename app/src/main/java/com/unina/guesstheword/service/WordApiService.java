package com.unina.guesstheword.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WordApiService {
    @GET("word")
    Call<List<String>> getWords(@Query("number") Integer number, @Query("lang") String language);
}
