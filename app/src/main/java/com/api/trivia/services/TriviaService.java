package com.api.trivia.services;

import com.api.trivia.models.Result;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TriviaService {

    @GET("/api.php")
    Call<Result> getSaveGames(@Query("amount") int amount, @Query("category") String category, @Query("difficulty") String difficulty, @Query("type") String type );

}
