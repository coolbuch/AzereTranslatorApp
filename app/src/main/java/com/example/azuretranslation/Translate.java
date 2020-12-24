package com.example.azuretranslation;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Translate
{
    String API_URL = "https://api.cognitive.microsofttranslator.com";  //https://eastasia.api.cognitive.microsoft.com/

    // TODO: рекомендуется использовать свой ключ, чтобы получить доп. балл
    String key = "43a4d51635ab42ef99ac9741131b146a"; //
    String REGION = "eastasia";
    String lang = "es";

    @POST("/translate?api-version=3.0&to=es"/* + lang*/) // путь к API
    @Headers({
            "Content-Type: application/json",
            "Ocp-Apim-Subscription-Key: " + key,
            "Ocp-Apim-Subscription-Region: " + REGION
            // TODO: указать ключ и регион
    })
    Call<String> translate(@Body JSONObject obj);
}
