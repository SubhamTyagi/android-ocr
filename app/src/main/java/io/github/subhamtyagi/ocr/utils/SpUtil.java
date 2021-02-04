package io.github.subhamtyagi.ocr.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import java.util.Set;


/**
 * A util class for SharedPreferences
 */
public class SpUtil {
    private volatile static SpUtil mInstance;
    private Context mContext;
    private SharedPreferences mPref;

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

    @Nullable
    public String getString(String key) {
        return mPref.getString(key, "");
    }

    @Nullable
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


}
