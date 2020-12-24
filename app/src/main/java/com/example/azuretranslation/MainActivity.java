package com.example.azuretranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    TextView translatedTextTV, typedTextTV;
    Button translateBtn;
    Spinner spinnerFrom, spinnerTo;
    String lang;
    ArrayList<Language> languages = new ArrayList<>();
    ArrayAdapter<Language> adapter;
    // Экземпляр библиотеки и интерфейса можно использовать для всех обращений к сервису
    // формируем экземпляр библиотеки
    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) // ответ от сервера в виде строки
            .baseUrl(AzureTranslationAPI.API_URL) // адрес API сервера
            .build();

    AzureTranslationAPI api = retrofit.create(AzureTranslationAPI.class); // описываем, какие функции реализованы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinnerFrom = findViewById(R.id.spinner_from);
        spinnerTo = findViewById(R.id.spinner_to);
        translateBtn = findViewById(R.id.button_translate);
        translatedTextTV = findViewById(R.id.translatedText);
        typedTextTV = findViewById(R.id.typedText);
        adapter = new ArrayAdapter<Language>(this, R.layout.item, languages);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);



        Call<LanguagesResponse> call = api.getLanguages(); // создаём объект-вызов
        call.enqueue(new LanguagesCallback());
        JSONObject json = new JSONObject();
        try {
            json.accumulate("Text", "Я люблю программирование");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Translate translateApi = retrofit.create(Translate.class);
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<String> translatedTextCall = translateApi.translate(json);
                Log.d("mytag", "onCreate: " + json.toString());

                translatedTextCall.enqueue(new TranslatedTextCallback());
            }
        });
    }

    // TODO: создать аналогичным образом класс для ответа сервера при переводе текста
    class LanguagesCallback implements Callback<LanguagesResponse> {

        @Override
        public void onResponse(Call<LanguagesResponse> call, Response<LanguagesResponse> response) {
            if (response.isSuccessful()) {
                // TODO: response.body() содержит массив языков, доступных для перевода
                Log.d("mytag", "response: " + response.body());
                saveLangs(response.body());
            } else {
                // TODO: выводить Toast и сообщение в журнал в случае ошибки
            }
        }

        @Override
        public void onFailure(Call<LanguagesResponse> call, Throwable t) {
            // TODO: выводить Toast и сообщение в журнал в случае ошибки
            Toast.makeText(MainActivity.this, "Произошла Ошибка", Toast.LENGTH_SHORT).show();
        }
    }

    static class TranslatedTextCallback implements Callback<String>
    {

        @Override
        public void onResponse(Call<String> call, Response<String> response)
        {
            if (response.isSuccessful())
                Log.d("mytag", "TranslatedTextCallback: " + response.headers());
            else
                Log.d("mytag", "TranslatedTextCallback Error: " + response.toString() + '\n' + response.headers());

        }

        @Override
        public void onFailure(Call<String> call, Throwable t)
        {
            Log.d("mytag", "onFailure: " + t.getMessage());
        }
    }

    void saveLangs(LanguagesResponse l)
    {
        for (String st: l.translation.keySet())
        {
            languages.add(l.translation.get(st));
            adapter.notifyDataSetChanged();

        }
    }
}
