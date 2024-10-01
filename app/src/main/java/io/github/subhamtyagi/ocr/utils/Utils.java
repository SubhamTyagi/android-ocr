package io.github.subhamtyagi.ocr.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import androidx.annotation.NonNull;

import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Enhance;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.leptonica.android.WriteFile;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.subhamtyagi.ocr.R;
import kotlin.Triple;

public class Utils {

    private static final String DEFAULT_LANGUAGE = "eng";

    @SuppressLint("DefaultLocale")
    public static String getSize(int size) {
        if (size < 0) {
            return "Invalid size";
        }
        if (size < 1024) {
            return size + " Bytes";
        }

        double kb = size / 1024.0;
        if (kb < 1024) {
            return String.format("%.2f KB", kb);
        }

        double mb = kb / 1024.0;
        return String.format("%.2f MB", mb);
    }

    public static Bitmap preProcessBitmap(Bitmap bitmap) {
        Pix pix = preparePix(bitmap);

        if (shouldApplyContrast()) {
            pix = AdaptiveMap.pixContrastNorm(pix);
        }

        if (shouldApplyUnsharpMasking()) {
            pix = Enhance.unsharpMasking(pix);
        }

        if (shouldApplyOtsuThreshold()) {
            pix = Binarize.otsuAdaptiveThreshold(pix);
        }

        if (shouldFindAndDeskw()) {
            pix = rotateToCorrectSkew(pix);
        }

        return WriteFile.writeBitmap(pix);
    }

    private static Pix preparePix(Bitmap bitmap) {
        return Convert.convertTo8(ReadFile.readBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    private static boolean shouldApplyContrast() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_CONTRAST, true);
    }

    private static boolean shouldApplyUnsharpMasking() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_UN_SHARP_MASKING, true);
    }

    private static boolean shouldApplyOtsuThreshold() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_OTSU_THRESHOLD, true);
    }

    private static boolean shouldFindAndDeskw() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_FIND_SKEW_AND_DESKEW, true);
    }

    private static Pix rotateToCorrectSkew(Pix pix) {
        float skewAngle = Skew.findSkew(pix);
        return Rotate.rotate(pix, skewAngle);
    }

    public static boolean isPreProcessImage() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_GRAYSCALE_IMAGE_OCR, true);
    }

    public static boolean isPersistData() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_PERSIST_DATA, true);
    }

    public static String getTrainingDataType() {
        return SpUtil.getInstance().getString(Constants.KEY_TESS_TRAINING_DATA_SOURCE, "best");
    }

    public static @NonNull Set<Language> getTrainingDataLanguages(Context context) {
        return allLangs(context, SpUtil.getInstance().getStringSet(
                context.getString(R.string.key_language_for_tesseract_multi),
                Collections.singleton(DEFAULT_LANGUAGE)));
    }

    public static int getPageSegMode() {
        return Integer.parseInt(SpUtil.getInstance().getString(Constants.KEY_PAGE_SEG_MODE, "1"));
    }

    public static void putLastUsedText(String text) {
        SpUtil.getInstance().putString(Constants.KEY_LAST_USE_IMAGE_TEXT, text);
    }

    public static Map<String, String> getAllParameters() {
        return SpUtil.getInstance().getAllParameters();
    }

    public static boolean isExtraParameterSet() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_ADVANCE_TESS_OPTION);
    }

    public static String getLastUsedText() {
        return SpUtil.getInstance().getString(Constants.KEY_LAST_USE_IMAGE_TEXT, "");
    }

    public static Triple<Set<Language>, Set<Language>, Set<Language>> getLast3UsedLanguage(Context context) {
        return new Triple<>(
                allLangs(context, SpUtil.getInstance().getStringSet(context.getString(R.string.key_language_for_tesseract_multi), Collections.singleton(DEFAULT_LANGUAGE))),
                allLangs(context, SpUtil.getInstance().getStringSet(Constants.KEY_LAST_USED_LANGUAGE_2, Collections.singleton("hin"))),
                allLangs(context, SpUtil.getInstance().getStringSet(Constants.KEY_LAST_USED_LANGUAGE_3, Collections.singleton("deu")))
        );
    }

    private static Set<Language> allLangs(Context context, Set<String> codes) {
        return codes.stream().map(code -> new Language(context, code)).collect(Collectors.toSet());
    }

    public static void setLastUsedLanguage(Context context, Set<Language> lastUsedLanguage) {
        Set<Language> lastLanguage1 = getLast3UsedLanguage(context).getFirst();
        Set<Language> lastLanguage2 = getLast3UsedLanguage(context).getSecond();

        if (lastUsedLanguage.equals(lastLanguage1)) {
            return;
        }

        Set<String> lastCodes = lastUsedLanguage.stream().map(Language::getCode).collect(Collectors.toSet());
        Set<String> lastCodes1 = lastLanguage1.stream().map(Language::getCode).collect(Collectors.toSet());
        Set<String> lastCodes2 = lastLanguage2.stream().map(Language::getCode).collect(Collectors.toSet());

        if (lastLanguage2.equals(lastUsedLanguage)) {
            SpUtil.getInstance().putStringSet(Constants.KEY_LAST_USED_LANGUAGE_2, lastCodes1);
            SpUtil.getInstance().putStringSet(context.getString(R.string.key_language_for_tesseract_multi), lastCodes);
        } else {
            SpUtil.getInstance().putStringSet(Constants.KEY_LAST_USED_LANGUAGE_3, lastCodes2);
            SpUtil.getInstance().putStringSet(Constants.KEY_LAST_USED_LANGUAGE_2, lastCodes1);
            SpUtil.getInstance().putStringSet(context.getString(R.string.key_language_for_tesseract_multi), lastCodes);
        }
    }

    public static void putLastUsedImageLocation(String imageURI) {
        SpUtil.getInstance().putString(Constants.KEY_LAST_USE_IMAGE_LOCATION, imageURI);
    }

    public static Boolean isNetworkAvailable(Application application) {
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }
}
