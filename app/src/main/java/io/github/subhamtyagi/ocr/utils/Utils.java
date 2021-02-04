package io.github.subhamtyagi.ocr.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Enhance;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.leptonica.android.WriteFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Utils {

    public static Map<Character, Character> DigitCorrectDictionary;

    static {
        DigitCorrectDictionary = new HashMap<>();
        DigitCorrectDictionary.put('D', '0');
        DigitCorrectDictionary.put('l', '1');
        DigitCorrectDictionary.put('I', '1');
        DigitCorrectDictionary.put('S', '5');
        DigitCorrectDictionary.put('s', '5');
        DigitCorrectDictionary.put('g', '9');
    }


    @SuppressLint("DefaultLocale")
    public static String getSize(int size) {
        String s;
        double kb = (size / 1024);
        double mb = kb / 1024;
        double gb = kb / 1024;
        double tb = kb / 1024;
        if (size < 1024) {
            s = "$size Bytes";
        } else if (size < 1024 * 1024) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB";
        } else if (size < 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", gb) + " GB";
        } else {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }


    private static String correctDigit(String text) {
        StringBuilder correctedText = new StringBuilder();
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (DigitCorrectDictionary.containsKey(c)) {
                correctedText.append(DigitCorrectDictionary.get(c));
            } else {
                correctedText.append(c);
            }
        }
        return correctedText.toString();
    }


    /**
     * bitmap
     *
     * @param b
     * @param degrees
     * @return
     */
    public static Bitmap rotate(Bitmap b, int degrees) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(
                        b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                    b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ignore) {

            }
        }
        return b;
    }


    public static Bitmap preProcessBitmap(Bitmap bitmap) {
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Pix pix = ReadFile.readBitmap(bitmap);
        pix = Convert.convertTo8(pix);
        //  pix= AdaptiveMap.pixContrastNorm(pix);
        pix = Enhance.unsharpMasking(pix, 5, 2.5f);
        pix = Binarize.otsuAdaptiveThreshold(pix);
        //pix = Enhance.unsharpMasking(pix);
        float f = Skew.findSkew(pix);
        // pix=Skew.deskew(pix,0);
        // float f2=Skew.findSkew(pix);
        //Log.d("Utils", "preProcessBitmap: first skew:"+f+"  second:"+f2);
        pix = Rotate.rotate(pix, f);
        return WriteFile.writeBitmap(pix);
    }


    private static String getAllLanguage(Set<String> langs) {
        if (langs == null) return "eng";
        StringBuilder rLanguage = new StringBuilder();
        for (String lang : langs) {
            rLanguage.append(lang);
            rLanguage.append("+");
        }
        return rLanguage.subSequence(0, rLanguage.toString().lastIndexOf('+')).toString();
    }

    public static String getTrainingDataType() {
        return SpUtil.getInstance().getString(Constants.KEY_TESS_TRAINING_DATA_SOURCE, "best");
    }

    public static String getTrainingDataLanguage() {
        return SpUtil.getInstance().getString(Constants.KEY_LANGUAGE_FOR_TESSERACT, "eng");
    }

    public static String getTrainingDataMultipleLanguage() {
        return getAllLanguage(SpUtil.getInstance().getStringSet(Constants.KEY_LANGUAGE_FOR_TESSERACT_MULTI, null));
    }

}
