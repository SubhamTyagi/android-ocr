package io.github.subhamtyagi.ocr.ocr;

import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.subhamtyagi.ocr.utils.Constants;
import io.github.subhamtyagi.ocr.utils.Language;


/**
 * This class convert the image to text and return the text on image
 */
public class ImageTextReader {

    public static final String TAG = "ImageTextReader";
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    /**
     * TessBaseAPI instance
     */
    private static volatile TessBaseAPI api;
    //  private static volatile TesseractImageTextReader INSTANCE;

    /**
     * initialize and train the tesseract engine
     *
     * @param path      a path to training data
     * @param languages language code i.e. selected by user
     * @return the instance of this class for later use
     */
    public static ImageTextReader getInstance(String path, Set<Language> languages, int pageSegMode, Map<String, String> parameters, boolean isParameterSet, TessBaseAPI.ProgressNotifier progressNotifier) {
        try {
            ImageTextReader imageTextReader = new ImageTextReader();
            api = new TessBaseAPI(progressNotifier);
            boolean s = api.init(path, languages
                    .stream()
                    .map(Language::getCode)
                    .collect(Collectors.joining("+")));
            api.setPageSegMode(pageSegMode);
            imageTextReader.setSuccess(s);
            if (isParameterSet)
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (!key.equals(Constants.KEY_OCR_PSM_MODE)) {
                        api.setVariable(key, value);
                    }
                }
            return imageTextReader;
        } catch (Exception e) {
            return null;
        }

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
            //textOnImage = api.getUTF8Text();
            textOnImage = api.getHOCRText(1);
        } catch (Exception e) {
            return "Scan Failed: WTF: Must be reported to developer!";
        }
        if (textOnImage.isEmpty()) {
            return "Scan Failed: Couldn't read the image\nProblem may be related to Tesseract or no Text on Image!";
        } else return textOnImage;

    }

    /**
     * stop the image TEXT reader
     */
    public void stop() {
        api.stop();
    }

    /**
     * find the confidence or
     *
     * @return confidence
     */
    public int getAccuracy() {
        return api.meanConfidence();
    }

    /**
     * Closes down tesseract and free up all memory.
     */
    public void tearDownEverything() {
        api.recycle();
    }

    /**
     * Frees up recognition results and any stored image data,
     */
    public void clearPreviousImage() {
        api.clear();
    }

}
