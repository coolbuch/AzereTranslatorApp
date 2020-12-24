package com.example.azuretranslation;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AzureTranslationAPI {
    String API_URL = "https://api.cognitive.microsofttranslator.com";  //https://eastasia.api.cognitive.microsoft.com/

    // TODO: рекомендуется использовать свой ключ, чтобы получить доп. балл
    String key = "3e985f30c7e8447296a145b9d7e97f9c"; //
    String REGION = "eastasia";
    String lang = "es";

    // TODO: регион указать отдельной переменной

    // запрос языков работает без ключа
    @GET("/languages?api-version=3.0&scope=translation")
    Call<LanguagesResponse> getLanguages();

    @POST("/translate?api-version=3.0&to=es"/* + lang*/) // путь к API
    @Headers({
            "Content-Type: application/json",
            "Ocp-Apim-Subscription-Key:" + key,
            "Ocp-Apim-Subscription-Region:" + REGION
            // TODO: указать ключ и регион
    })
    Call<String> translate(@Body String obj);

    // TranslatedText - формат ответа от сервера
    // Тип ответа - TranslatedText, действие - translate, содержание запроса - пустое (нет полей формы)
    // TODO: с помощью аннотации @Body передать поля запроса к API (текст для перевода)
    // см. примеры https://square.github.io/retrofit/

}
