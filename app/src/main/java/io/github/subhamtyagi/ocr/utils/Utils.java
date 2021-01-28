package io.github.subhamtyagi.ocr.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;

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

    /**
     * convert the image into the grayscale
     *
     * @param bmpOriginal
     * @return a grayscaled version of original image
     */
    public static Bitmap convertToGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
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

    public static Bitmap binary(Bitmap bitmap) {
        Bitmap binaryBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width = binaryBitmap.getWidth();
        int height = binaryBitmap.getHeight();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int pixel = binaryBitmap.getPixel(i, j);
                int alpha = (pixel & 0xFF000000);
                int red = (pixel & 0x00FF0000) >> 16;
                int green = (pixel & 0x0000FF00) >> 8;
                int blue = (pixel & 0x000000FF);
                int gray = (int) (red * 0.3f + green * 0.59f + blue * 0.11f);
                if (gray <= 127) gray = 0;
                else gray = 255;
                int color = alpha | (gray << 16) | (gray << 8) | gray;
                binaryBitmap.setPixel(i, j, color);
            }
        }
        return binaryBitmap;
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
            } catch (OutOfMemoryError ex) {

            }
        }
        return b;
    }


    public static Bitmap preProcessBitmap(Bitmap bitmap) {
        //
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Pix pix = ReadFile.readBitmap(bitmap);
        pix = Convert.convertTo8(pix);
        //  pix= AdaptiveMap.pixContrastNorm(pix);

        pix = Enhance.unsharpMasking(pix, 5, 2.5f);
        pix = Binarize.otsuAdaptiveThreshold(pix);
        //pix = Enhance.unsharpMasking(pix);
        float f=Skew.findSkew(pix);

       // pix=Skew.deskew(pix,0);
       // float f2=Skew.findSkew(pix);
        //Log.d("Utils", "preProcessBitmap: first skew:"+f+"  second:"+f2);
        pix=Rotate.rotate(pix,f);
        return WriteFile.writeBitmap(pix);



    }

}
