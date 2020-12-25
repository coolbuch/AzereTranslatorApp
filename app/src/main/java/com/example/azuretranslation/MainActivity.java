package com.example.azuretranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainActivity extends AppCompatActivity
{


    static TextView translatedTextTV, typedTextTV;
    Button translateBtn;
    Spinner spinnerTo;
    ListView lv;
    String lang;
    ArrayList<Language> languages = new ArrayList<>();
    ArrayAdapter<DB.Phrases> lvAdapter;
    ArrayAdapter<Language> adapter;
    final String TAG = "mytag";
    public DB db;
    // Экземпляр библиотеки и интерфейса можно использовать для всех обращений к сервису
    // формируем экземпляр библиотеки
    Retrofit retrofit = new Retrofit.Builder().addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()) // ответ от сервера в виде строки
            .baseUrl(AzureTranslationAPI.API_URL) // адрес API сервера
            .build();

    AzureTranslationAPI api = retrofit.create(AzureTranslationAPI.class); // описываем, какие функции реализованы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db =  new DB(this);
        spinnerTo = findViewById(R.id.spinner_to);
        translateBtn = findViewById(R.id.button_translate);
        translatedTextTV = findViewById(R.id.translatedText);
        typedTextTV = findViewById(R.id.typedText);
        adapter = new ArrayAdapter<Language>(this, R.layout.item, languages);
        spinnerTo.setAdapter(adapter);
        lv = findViewById(R.id.lv);
        lvAdapter = new ArrayAdapter<DB.Phrases>(this, R.layout.item, db.getPhrases());
        lv.setAdapter(lvAdapter);
        Date cur = new Date();
        long lastUpdate = 0;
        if (!loadFromSettings("Date").equals(""))
            lastUpdate = Long.parseLong(loadFromSettings("Date"));

        Log.d(TAG, "onCreate: "  + loadFromSettings("Lang"));
        if (cur.getTime() - lastUpdate > 86400000l)
        {
            Call<LanguagesResponse> call = api.getLanguages(); // создаём объект-вызов
            call.enqueue(new LanguagesCallback());
            saveToSettings("Date", Long.toString(cur.getTime()));
        }
        else
        {
            Gson gson = new Gson();
            languages.clear();
            languages.addAll(gson.fromJson(loadFromSettings("Lang"), new TypeToken<ArrayList<Language>>(){}.getType()));
            adapter.notifyDataSetChanged();
        }

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = typedTextTV.getText().toString();
                JsonObject json = new JsonObject();
                json.addProperty("Text", s);
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(json);
                Log.d(TAG, "onClick: " + jsonArray.toString());
                Call<RecievedJson[]> translatedTextCall = api.translate(jsonArray.toString(), ((Language) spinnerTo.getSelectedItem()).getAbr());

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

    class TranslatedTextCallback implements Callback<RecievedJson[]>
    {

        @Override
        public void onResponse(Call<RecievedJson[]> call, Response<RecievedJson[]> response)
        {
            if (response.isSuccessful())
            {
                Log.d("mytag", "TranslatedTextCallback: " + response.headers() + '\n' + response.body()[0].toString());
                translatedTextTV.setText(response.body()[0].toString());
                db.addPhrase(typedTextTV.getText().toString(), translatedTextTV.getText().toString());
                lvAdapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onFailure(Call<RecievedJson[]> call, Throwable t)
        {
            Log.d("mytag", "onFailure: " + t.getMessage());
        }
    }

    void saveLangs(LanguagesResponse l)
    {
        GsonBuilder gb = new GsonBuilder();
        Gson gson = gb.create();

        Log.d(TAG, "saveLangs: " + l.toString());
        for (String st: l.translation.keySet())
        {
            languages.add(l.translation.get(st));
            adapter.notifyDataSetChanged();
        }
        saveToSettings("Lang", gson.toJson(languages));
    }


    private void saveToSettings(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String loadFromSettings(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        return  sharedPreferences.getString(key, "");
    }

}
