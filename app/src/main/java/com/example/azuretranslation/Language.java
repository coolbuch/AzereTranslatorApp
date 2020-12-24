package com.example.azuretranslation;

import androidx.annotation.NonNull;

class Language {
    String name, nativeName, abr;

    public Language(String name, String nativeName, String abr)
    {
        this.name = name;
        this.nativeName = nativeName;
        this.abr = abr;
    }

    public void setAbr(String abr) {
        this.abr = abr;
    }

    @NonNull
    @Override
    public String toString()
    {
        return name;
    }
}
