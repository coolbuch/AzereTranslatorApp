package com.example.azuretranslation;

import androidx.annotation.NonNull;

import java.util.ArrayList;

class TranslatedText
{
    // TODO: указать необходимые поля хранения ответа от API при переводе текста
    String text;
    @NonNull
    @Override
    public String toString() {
        return text;
    }
}

class RecievedJson
{
    ArrayList<TranslatedText> translations;

    public RecievedJson(ArrayList<TranslatedText> translations)
    {
        this.translations = translations;
    }

    @NonNull
    @Override
    public String toString() {
        return translations.toString();
    }
}
