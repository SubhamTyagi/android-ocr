package io.github.subhamtyagi.ocr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * A util class for SharedPreferences
 */
public class SpUtil {
    private volatile static SpUtil mInstance;
    private Context mContext;
    private SharedPreferences mPref;

    public static final String KEY_OCR_PSM_MODE = "key_ocr_psm_mode";
    public static final String KEY_TESSERACT_OEM_MODE = "key_tesseract_oem_mode";
    public static final String KEY_PRESERVE_INTERWORD_SPACES = "key_preserve_interword_spaces";
    public static final String KEY_CHOP_ENABLE = "chop_enable";
    public static final String KEY_USE_NEW_STATE_COST = "use_new_state_cost";
    public static final String KEY_SEGMENT_SEGCOST_RATING = "segment_segcost_rating";
    public static final String KEY_ENABLE_NEW_SEGSEARCH = "enable_new_segsearch";
    public static final String KEY_LANGUAGE_MODEL_NGRAM_ON = "language_model_ngram_on";
    public static final String KEY_TEXTORD_FORCE_MAKE_PROP_WORDS = "textord_force_make_prop_words";
    public static final String KEY_EDGES_MAX_CHILDREN_PER_OUTLINE = "edges_max_children_per_outline";
    public static final String KEY_EXTRA_PREFS = "extra_parameters";


    private SpUtil() {
    }

    /**
     * A factory method for
     *
     * @return a instance of this class
     */
    public static SpUtil getInstance() {
        if (null == mInstance) {
            synchronized (SpUtil.class) {
                if (null == mInstance) {
                    mInstance = new SpUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * initialization of context, use only first time later it will use this again and again
     *
     * @param context app context: first time
     */
    public void init(Context context) {
        if (mContext == null) {
            mContext = context;
        }
        if (mPref == null) {
            mPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        }
    }

    public void putString(String key, String value) {
        Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void putLong(String key, long value) {
        Editor editor = mPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public void putInt(String key, int value) {
        Editor editor = mPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void putBoolean(String key, boolean value) {
        Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }


    public boolean getBoolean(String key) {
        return mPref.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        return mPref.getBoolean(key, def);
    }

    @NonNull
    public String getString(String key) {
        return mPref.getString(key, "");
    }

    @NonNull
    public String getString(String key, String def) {
        return mPref.getString(key, def);
    }

    public Set<String> getStringSet(String key, Set<String> def) {
        return mPref.getStringSet(key, def);
    }

    public long getLong(String key) {
        return mPref.getLong(key, 0);
    }

    public long getLong(String key, int defInt) {
        return mPref.getLong(key, defInt);
    }

    public int getInt(String key) {
        return mPref.getInt(key, 0);
    }

    public long getInt(String key, int defInt) {
        return mPref.getInt(key, defInt);
    }

    public boolean contains(String key) {
        return mPref.contains(key);
    }


    public void remove(String key) {
        Editor editor = mPref.edit();
        editor.remove(key);
        editor.apply();
    }

    public void clear() {
        Editor editor = mPref.edit();
        editor.clear();
        editor.apply();
    }


    public void putStringSet(String key, Set<String> value) {
        Editor editor = mPref.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }


    // Retrieve all preferences as a Map
    public Map<String, String> getAllParameters() {
        Map<String, String> preferencesMap = new HashMap<>();
        preferencesMap.put(KEY_OCR_PSM_MODE, getString(KEY_OCR_PSM_MODE, "1"));
        preferencesMap.put(KEY_TESSERACT_OEM_MODE, getString(KEY_TESSERACT_OEM_MODE, "3"));
        preferencesMap.put(KEY_PRESERVE_INTERWORD_SPACES, getString(KEY_PRESERVE_INTERWORD_SPACES, "0"));
        preferencesMap.put(KEY_CHOP_ENABLE, getString(KEY_CHOP_ENABLE, "T"));
        preferencesMap.put(KEY_USE_NEW_STATE_COST, getString(KEY_USE_NEW_STATE_COST, "F"));
        preferencesMap.put(KEY_SEGMENT_SEGCOST_RATING, getString(KEY_SEGMENT_SEGCOST_RATING, "F"));
        preferencesMap.put(KEY_ENABLE_NEW_SEGSEARCH, getString(KEY_ENABLE_NEW_SEGSEARCH, "0"));
        preferencesMap.put(KEY_LANGUAGE_MODEL_NGRAM_ON, getString(KEY_LANGUAGE_MODEL_NGRAM_ON, "0"));
        preferencesMap.put(KEY_TEXTORD_FORCE_MAKE_PROP_WORDS, getString(KEY_TEXTORD_FORCE_MAKE_PROP_WORDS, "F"));
        preferencesMap.put(KEY_EDGES_MAX_CHILDREN_PER_OUTLINE, getString(KEY_EDGES_MAX_CHILDREN_PER_OUTLINE, "40"));

        if (!getString(KEY_EXTRA_PREFS, "").isBlank()){
            String values=getString(KEY_EXTRA_PREFS, "");
            for (String paras : values.split(",")) {
                String key =paras.split(" ")[0];
                String val =paras.split(" ")[1];
                preferencesMap.put(key,val);
            }
        }
        return preferencesMap;
    }
}
