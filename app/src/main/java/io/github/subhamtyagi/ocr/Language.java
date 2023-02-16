package io.github.subhamtyagi.ocr;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

public class Language {
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private String code, name;
    public Language(@NonNull Context c, @NonNull String code) {
        String[] languagesNames = c.getResources().getStringArray(R.array.ocr_engine_language);
        String[] languagesCodes = c.getResources().getStringArray(R.array.key_ocr_engine_language_value);
        for (int i = 0; i < languagesCodes.length; i++) {
            if (languagesCodes[i].equals(code)) {
                this.code = languagesCodes[i];
                this.name = languagesNames[i];
            }
        }
    }

    @Override public String toString() {
        return getCode();
    }
}
