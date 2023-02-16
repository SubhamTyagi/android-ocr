package io.github.subhamtyagi.ocr.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

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

    public static Set<Language> getTrainingDataLanguage(Context c) {
        if (SpUtil.getInstance().getBoolean(Constants.KEY_ENABLE_MULTI_LANG)) {
            return SpUtil.getInstance()
                    .getStringSet(Constants.KEY_LANGUAGE_FOR_TESSERACT_MULTI, Collections.singleton(DEFAULT_LANGUAGE))
                    .stream().map(code -> new Language(c, code)).collect(Collectors.toSet());
        } else {
            return Collections.singleton(new Language(c, SpUtil.getInstance().getString(Constants.KEY_LANGUAGE_FOR_TESSERACT)));
        }
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

    public static Language[] getLast3UsedLanguage(Context c) {
        return new Language[]{
                new Language(c, SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_1, "eng")),
                new Language(c, SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_2, "hin")),
                new Language(c, SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_3, "deu"))
        };
    }

    public static void setLastUsedLanguage(Context c, Language lastUsedLanguage) {
        Language l1 = getLast3UsedLanguage(c)[0];
        if (lastUsedLanguage.equals(l1)) {
            return;
        }
        Language l2 = getLast3UsedLanguage(c)[0];
        if (l2.equals(lastUsedLanguage)) {
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_2, l1.getCode());
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_1, lastUsedLanguage.getCode());
        } else {
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_3, l2.getCode());
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_2, l1.getCode());
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_1, lastUsedLanguage.getCode());
        }

    }

    public static void putLastUsedImageLocation(String imageURI) {
        SpUtil.getInstance().putString(Constants.KEY_LAST_USE_IMAGE_LOCATION, imageURI);
    }


}
