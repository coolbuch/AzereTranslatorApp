package com.example.azuretranslation;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.json.JSONObject;
import org.w3c.dom.Text;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AzureTranslationAPI {
    String API_URL = "https://api.cognitive.microsofttranslator.com";  //https://eastasia.api.cognitive.microsoft.com/

    // TODO: рекомендуется использовать свой ключ, чтобы получить доп. балл
    String key = "43a4d51635ab42ef99ac9741131b146a"; //
    String REGION = "eastasia";
    String lang = "es";

    // TODO: регион указать отдельной переменной

    // запрос языков работает без ключа
    @GET("/languages?api-version=3.0&scope=translation")
    Call<LanguagesResponse> getLanguages();

    @POST("/translate?api-version=3.0") // путь к API
    @Headers({
            "Content-Type: application/json",
            "Ocp-Apim-Subscription-Key: 3e985f30c7e8447296a145b9d7e97f9c"/* + key*/,
            "Ocp-Apim-Subscription-Region: eastasia" /*+ REGION*/
            // TODO: указать ключ и регион
    })
    Call<RecievedJson[]> translate(@Body String text, @Query("to") String lang);

    // TranslatedText - формат ответа от сервера
    // Тип ответа - TranslatedText, действие - translate, содержание запроса - пустое (нет полей формы)
    // TODO: с помощью аннотации @Body передать поля запроса к API (текст для перевода)
    // см. примеры https://square.github.io/retrofit/

}
