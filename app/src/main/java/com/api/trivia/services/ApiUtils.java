package com.api.trivia.services;

import com.api.trivia.utils.Constants;

public class ApiUtils {

    public static TriviaService getUserAPI() {

        return RetrofitClient.getClient(Constants.URL_BASE).create(TriviaService.class);

    }

}
