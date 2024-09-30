package io.github.subhamtyagi.ocr.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import io.github.subhamtyagi.ocr.R;

public class Language {
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private String code, name;

    public Language(@NonNull Context c, @NonNull String seed) {
        String[] languagesNames = c.getResources().getStringArray(R.array.ocr_engine_language);
        String[] languagesCodes = c.getResources().getStringArray(R.array.key_ocr_engine_language_value);
        for (int i = 0; i < languagesCodes.length; i++) {
            if (languagesCodes[i].equals(seed) || languagesNames[i].equals(seed)) {
                this.code = languagesCodes[i];
                this.name = languagesNames[i];
            }
        }
    }

    @NonNull
    @Override
    public String toString() {
        return getCode();
    }
}
