package io.github.subhamtyagi.ocr.utils;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.Locale;


public class LanguageUtil {

    public static final void changeLanguage(Context context, String language, String country) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(language, country);
        resources.updateConfiguration(config, null);
    }

    public static final void followSystemLanguage(Context context) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        Locale locale = Locale.getDefault();
        config.locale = locale;
        resources.updateConfiguration(config, null);
    }

}