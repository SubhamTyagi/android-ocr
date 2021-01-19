package io.github.subhamtyagi.ocr.ocr;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.ResultIterator;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.ArrayList;

import io.github.subhamtyagi.ocr.models.Blocks;
import io.github.subhamtyagi.ocr.models.RecognizedResults;

/**
 * This class convert the image to text and return the text on image
 */
public class ImageTextReader {


    /**
     * TessBaseAPI instance
     */
    private static volatile TessBaseAPI api;
    //  private static volatile TesseractImageTextReader INSTANCE;

    /**
     * initialize and train the tesseract engine
     *
     * @param path     a path to training data
     * @param language language code i.e. selected by user
     * @return the instance of this class for later use
     */
    public static ImageTextReader geInstance(String path, String language, TessBaseAPI.ProgressNotifier progressNotifier) {
        api = new TessBaseAPI(progressNotifier);
        api.init(path, language);
        api.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);
        return new ImageTextReader();
    }

    /**
     * get the text from bitmap
     *
     * @param bitmap a image
     * @return text on image
     */
    public String getTextFromBitmap(Bitmap bitmap) {
        api.setImage(bitmap);
        String textOnImage;
        try {
            textOnImage = api.getUTF8Text();
            //textOnImage = api.getHOCRText(1);

        } catch (Exception e) {
            return "Scan Failed: WTF: Must be reported to developer!";
        }
        if (textOnImage.isEmpty()) {
            return "Scan Failed: Couldn't read the image\nProblem may be related to Tesseract or no Text on Image!";
        } else return textOnImage;

    }


    /**
     * Get the RecognizedText from Bitmap
     *
     * @param bitmap a image
     * @return RecognizedResult object that contains the text,rect,fullText
     */
    public RecognizedResults getRecognizedResultsFromBitmap(Bitmap bitmap) {
        api.setImage(bitmap);
        RecognizedResults results = new RecognizedResults();
        String fullText = api.getUTF8Text();

        ResultIterator it = api.getResultIterator();
        it.begin();

        Pixa pixa = api.getWords();
        ArrayList<Rect> rects = pixa.getBoxRects();
        for (int i = 0; i < rects.size(); i++) {
            Blocks item = new Blocks();
            item.setText(it.getUTF8Text(TessBaseAPI.PageIteratorLevel.RIL_WORD));
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
