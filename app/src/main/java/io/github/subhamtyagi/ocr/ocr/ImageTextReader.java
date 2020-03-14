package io.github.subhamtyagi.ocr.ocr;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;

import io.github.subhamtyagi.ocr.models.RecognizedResults;
import io.github.subhamtyagi.ocr.models.RecognizedText;

public class ImageTextReader {


    private static volatile TessBaseAPI api;
    //  private static volatile TesseractImageTextReader INSTANCE;

    public static ImageTextReader geInstance(String path, String language) {
        api = new TessBaseAPI();
        api.init(path, language);
        api.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        return new ImageTextReader();
    }

    public String getTextFromBitmap(Bitmap bitmap) {
        api.setImage(bitmap);
        String textOnImage;
        try {
            textOnImage = api.getUTF8Text();
        } catch (Exception e) {
            return "Scan Failed: WTF: Must be reported to developer!";
        }
        if (textOnImage == null) {
            return "Scan Failed: No Text on screen!";
        } else return textOnImage;

    }


    public RecognizedResults getTextFromBitmap2(Bitmap bitmap){
        api.setImage(bitmap);
        RecognizedResults results=new RecognizedResults();
        String fullText = api.getUTF8Text();

        ResultIterator it = api.getResultIterator();
        it.begin();

        Pixa pixa = api.getWords();
        ArrayList<Rect> rects = pixa.getBoxRects();
        for (int i = 0; i < rects.size(); i++) {
            RecognizedText item = new RecognizedText();
            item.setText( it.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD));
            item.setRect(rects.get(i));
            results.add(item);
            it.next(TessBaseAPI.PageIteratorLevel.RIL_WORD);
        }
        pixa.recycle();
        api.end();
        results.setFullText(fullText);
        return results;
    }

}
