package io.github.subhamtyagi.ocr;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

public class ImageTextReader {


    private static volatile TessBaseAPI api;
    //  private static volatile TesseractImageTextReader INSTANCE;

    public static ImageTextReader geInstance(String path, String language) {
        api = new TessBaseAPI();
        api.init(path, language);
        api.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        return new ImageTextReader();
    }

    public String getTextFromBitmap(Bitmap src) {
        api.setImage(src);
        String textOnImage;

        try {
            textOnImage = api.getUTF8Text();

        } catch (Exception e) {
            return "Scan Failed: WTF: Must be submitted to developer!";
        }

        if (textOnImage == null) {
            return "Scan Failed: No Text on screen!";
        } else return textOnImage;

    }

}
