package io.github.subhamtyagi.ocr.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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
import java.util.Set;
import java.util.stream.Collectors;

import io.github.subhamtyagi.ocr.Language;
import io.github.subhamtyagi.ocr.R;
import kotlin.Triple;

public class Utils {

    private static final String DEFAULT_LANGUAGE = "eng";

    @SuppressLint("DefaultLocale")
    public static String getSize(int size) {
        String s = "";
        double kb = size / 1024;
        double mb = kb / 1024;
        if (size < 1024) {
            s = "$size Bytes";
        } else if (size < 1024 * 1024) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB";
        }
        return s;
    }

    public static Bitmap preProcessBitmap(Bitmap bitmap) {
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Pix pix = ReadFile.readBitmap(bitmap);
        pix = Convert.convertTo8(pix);

        if (SpUtil.getInstance().getBoolean(Constants.KEY_CONTRAST, true)) {
            // pix=AdaptiveMap.backgroundNormMorph(pix);
            pix = AdaptiveMap.pixContrastNorm(pix);
        }

        if (SpUtil.getInstance().getBoolean(Constants.KEY_UN_SHARP_MASKING, true))
            pix = Enhance.unsharpMasking(pix);

        if (SpUtil.getInstance().getBoolean(Constants.KEY_OTSU_THRESHOLD, true))
            pix = Binarize.otsuAdaptiveThreshold(pix);

        if (SpUtil.getInstance().getBoolean(Constants.KEY_FIND_SKEW_AND_DESKEW, true)) {
            float f = Skew.findSkew(pix);
            pix = Rotate.rotate(pix, f);
        }

        return WriteFile.writeBitmap(pix);
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

    public static @NonNull Set<Language> getTrainingDataLanguages(Context c) {
        return allLangs(c,SpUtil.getInstance().getStringSet(
                c.getString(R.string.key_language_for_tesseract_multi),
                Collections.singleton("eng")));
    }

    public static int getPageSegMode() {
        return Integer.parseInt(SpUtil.getInstance().getString(Constants.KEY_PAGE_SEG_MODE, "1"));
    }

    public static void putLastUsedText(String text) {
        SpUtil.getInstance().putString(Constants.KEY_LAST_USE_IMAGE_TEXT, text);
    }

    public static String getLastUsedText() {
        return SpUtil.getInstance().getString(Constants.KEY_LAST_USE_IMAGE_TEXT, "");
    }

    public static Triple<Set<Language>, Set<Language>, Set<Language>> getLast3UsedLanguage(Context c) {
        return new Triple<>(
                allLangs(c, SpUtil.getInstance().getStringSet(c.getString(R.string.key_language_for_tesseract_multi), Collections.singleton("eng"))),
                allLangs(c, SpUtil.getInstance().getStringSet(Constants.KEY_LAST_USED_LANGUAGE_2, Collections.singleton("hin"))),
                allLangs(c, SpUtil.getInstance().getStringSet(Constants.KEY_LAST_USED_LANGUAGE_3, Collections.singleton("deu")))
        );
    }

    private static Set<Language> allLangs(Context c, Set<String> codes) {
        return codes.stream().map(code -> new Language(c, code)).collect(Collectors.toSet());
    }

    public static void setLastUsedLanguage(Context c, Set<Language> lastUsedLanguage) {
        Set<Language> l1 = getLast3UsedLanguage(c).getFirst();
        Set<Language> l2 = getLast3UsedLanguage(c).getSecond();
        if (lastUsedLanguage.equals(l1)) {
            return;
        }
        Set<String> lastCodes = lastUsedLanguage.stream().map(Language::getCode).collect(Collectors.toSet());
        Set<String> l1Codes = l1.stream().map(Language::getCode).collect(Collectors.toSet());
        Set<String> l2Codes = l2.stream().map(Language::getCode).collect(Collectors.toSet());
        if (l2.equals(lastUsedLanguage)) {
            SpUtil.getInstance().putStringSet(Constants.KEY_LAST_USED_LANGUAGE_2, l1Codes);
            SpUtil.getInstance().putStringSet(c.getString(R.string.key_language_for_tesseract_multi), lastCodes);
        } else {
            SpUtil.getInstance().putStringSet(Constants.KEY_LAST_USED_LANGUAGE_3, l2Codes);
            SpUtil.getInstance().putStringSet(Constants.KEY_LAST_USED_LANGUAGE_2, l1Codes);
            SpUtil.getInstance().putStringSet(c.getString(R.string.key_language_for_tesseract_multi), lastCodes);
        }

    }

    public static void putLastUsedImageLocation(String imageURI) {
        SpUtil.getInstance().putString(Constants.KEY_LAST_USE_IMAGE_LOCATION, imageURI);
    }


}
