package io.github.subhamtyagi.ocr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import io.github.subhamtyagi.ocr.ocr.ImageTextReader;
import io.github.subhamtyagi.ocr.utils.Constants;
import io.github.subhamtyagi.ocr.utils.Language;
import io.github.subhamtyagi.ocr.utils.SpUtil;
import io.github.subhamtyagi.ocr.utils.Utils;

public class MainActivity extends AppCompatActivity implements TessBaseAPI.ProgressNotifier {

    public static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SETTINGS = 797;
    private static boolean isRefresh = false;

    private File dirBest;
    private File dirStandard;
    private File dirFast;
    private File currentDirectory;
    private ImageTextReader mImageTextReader;
    /**
     * TrainingDataType: i.e Best, Standard, Fast
     */
    private String mTrainingDataType;
    private int mPageSegMode;
    private Map<String, String> parameters;
    /**
     * AlertDialog for showing when language data doesn't exists
     */
    private AlertDialog dialog;
    private ImageView mImageView;
    private LinearProgressIndicator mProgressIndicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton mFloatingActionButton;
    private LinearLayout mDownloadLayout;
    /**
     * Language name to be displayed
     */
    private TextView mLanguageName;
    private ExecutorService executorService;
    private Handler handler;
    private LinearProgressIndicator mProgressBar;
    private TextView mProgressMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpUtil.getInstance().init(this);

        mImageView = findViewById(R.id.source_image);
        mProgressIndicator = findViewById(R.id.progress_indicator);
        mSwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        mFloatingActionButton = findViewById(R.id.btn_scan);
        mLanguageName = findViewById(R.id.language_name1);

        mProgressBar = findViewById(R.id.progress_bar);
        mProgressMessage = findViewById(R.id.progress_message);
        mDownloadLayout = findViewById(R.id.download_layout);

        executorService = Executors.newFixedThreadPool(1);
        handler = new Handler(Looper.getMainLooper());

        initDirectories();
        initializeOCR();
        initViews();
    }

    private void initViews() {

        mFloatingActionButton.setOnClickListener(v -> {
            if (isNoLanguagesDataMissingFromSet()) {
                if (mImageTextReader != null) {
                    selectImage();
                } else {
                    initializeOCR();
                }
            } else {
                downloadLanguageData();
            }

        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNoLanguagesDataMissingFromSet()) {
                if (mImageTextReader != null) {
                    Drawable drawable = mImageView.getDrawable();
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (bitmap != null) {
                            isRefresh = true;
                            executorService.submit(new ConvertImageToText(bitmap));
                        }
                    }
                } else {
                    initializeOCR();
                }
            } else {
                downloadLanguageData();
            }
            mSwipeRefreshLayout.setRefreshing(false);

        });
        if (Utils.isPersistData()) {
            Bitmap bitmap = loadBitmapFromStorage();
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLanguageName.setText(Utils.getTrainingDataLanguages(this).stream().map(Language::getName).collect(Collectors.joining(", ")));
    }

    private void initDirectories() {
        String[] dirNames = {"best", "fast", "standard"};
        for (String dirName : dirNames) {
            File dir = new File(getExternalFilesDir(dirName), "tessdata");
            if (dir.mkdirs() || dir.isDirectory()) {
                switch (dirName) {
                    case "best":
                        dirBest = dir.getParentFile();
                        break;
                    case "fast":
                        dirFast = dir.getParentFile();
                        break;
                    case "standard":
                        dirStandard = dir.getParentFile();
                        break;
                }
            }
        }
        // Set currentDirectory to the last initialized directory (standard)
        currentDirectory = new File(dirStandard, "tessdata");
    }


    /**
     * initialize the OCR i.e tesseract api
     * if there is no training data in directory than it will ask for download
     */
    private void initializeOCR() {
        Set<Language> languages=Utils.getTrainingDataLanguages(this);
        File cf;
        mTrainingDataType = Utils.getTrainingDataType();
        mPageSegMode = Utils.getPageSegMode();
        parameters = Utils.getAllParameters();

        switch (mTrainingDataType) {
            case "best":
                currentDirectory = new File(dirBest, "tessdata");
                cf = dirBest;
                break;
            case "standard":
                cf = dirStandard;
                currentDirectory = new File(dirStandard, "tessdata");
                break;
            default:
                cf = dirFast;
                currentDirectory = new File(dirFast, "tessdata");

        }

        if (isNoLanguagesDataMissingFromSet()) {
            startImageTextReaderThread(cf, languages);
        } else {
            downloadLanguageData();
        }
    }

    private void startImageTextReaderThread(File cf, Set<Language> languages) {
        new Thread(() -> {
            try {
                if (mImageTextReader != null) {
                    mImageTextReader.tearDownEverything();
                }
                mImageTextReader = ImageTextReader.getInstance(cf.getAbsolutePath(), languages, mPageSegMode, parameters, Utils.isExtraParameterSet(), MainActivity.this);
                if (mImageTextReader != null && !mImageTextReader.isSuccess()) {
                    handleReaderException(languages);
                }
            } catch (Exception e) {
                handleReaderException(languages);
            }
        }).start();
    }

    private void handleReaderException(Set<Language> languages) {
        File destFile = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, languages));
        destFile.delete();
        mImageTextReader = null;
    }

    private void downloadLanguageData() {
        Set<Language> missingLanguage = new HashSet<>();
        Set<Language> languages =Utils.getTrainingDataLanguages(this);
        if (!Utils.isNetworkAvailable(getApplication())) {
            Toast.makeText(this, getString(R.string.you_are_not_connected_to_internet), Toast.LENGTH_SHORT).show();
            return;
            }
        for (Language l : languages) {
            if (isLanguageDataMissing(mTrainingDataType, l)) {
                missingLanguage.add(l);
            }
        }
        String missingLangName = missingLanguage.stream().map(Language::getName).collect(Collectors.joining(", "));
        String msg = String.format(getString(R.string.download_description), missingLangName);
        dialog = new AlertDialog.Builder(this).setTitle(R.string.training_data_missing).setCancelable(false).setMessage(msg).setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.cancel();
            executorService.submit(new DownloadTraining(mTrainingDataType, missingLanguage));    
        }).setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).create();
        dialog.show();
        
    }

    private boolean isNoLanguagesDataMissingFromSet() {
        final String dataType=mTrainingDataType;
        Set<Language> languages=Utils.getTrainingDataLanguages(this);
        for (Language language : languages) {
            if (isLanguageDataMissing(dataType, language)) return false;
        }
        return true;
    }

    private boolean isLanguageDataMissing(final @NonNull String dataType, final @NonNull Language language) {
        switch (dataType) {
            case "best":
                currentDirectory = new File(dirBest, "tessdata");
                break;
            case "standard":
                currentDirectory = new File(dirStandard, "tessdata");
                break;
            default:
                currentDirectory = new File(dirFast, "tessdata");

        }
        return !new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, language.getCode())).exists();
    }

    private void selectImage() {
        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(this);
    }

    private void convertImageToText(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            Log.e(TAG, "convertImageToText: " + e.getLocalizedMessage());
        }
        mImageView.setImageURI(imageUri);
        executorService.submit(new ConvertImageToText(bitmap));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            initializeOCR();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (isNoLanguagesDataMissingFromSet()) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (result != null) {
                        convertImageToText(result.getUri());
                    }
                } else {
                    initializeOCR();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdownNow();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (mImageTextReader != null) mImageTextReader.tearDownEverything();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem showHistoryItem = menu.findItem(R.id.action_history);
        showHistoryItem.setVisible(Utils.isPersistData());
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
        } else if (id == R.id.action_history) {
            showOCRResult(Utils.getLastUsedText());
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressValues(final TessBaseAPI.ProgressValues progressValues) {
        runOnUiThread(() -> mProgressIndicator.setProgress((int) (progressValues.getPercent() * 1.46)));
    }

    public void saveBitmapToStorage(Bitmap bitmap) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("last_file.jpeg", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "loadBitmapFromStorage: " + e.getLocalizedMessage());
        }
    }

    public Bitmap loadBitmapFromStorage() {
        Bitmap bitmap = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = openFileInput("last_file.jpeg");
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();

        } catch (IOException e) {
            Log.e(TAG, "loadBitmapFromStorage: " + e.getLocalizedMessage());
        }
        return bitmap;
    }

    public void showOCRResult(String text) {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            BottomSheetResultsFragment bottomSheetResultsFragment = BottomSheetResultsFragment.newInstance(text);
            bottomSheetResultsFragment.show(getSupportFragmentManager(), "bottomSheetResultsFragment");
        }

    }

    private class ConvertImageToText implements Runnable {
        private Bitmap bitmap;

        public ConvertImageToText(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            // Pre-execute on UI thread
            handler.post(() -> {
                mProgressIndicator.setProgress(0);
                mProgressIndicator.setVisibility(View.VISIBLE);
                animateImageViewAlpha(0.2f);
            });

            // Background execution
            if (!isRefresh && Utils.isPreProcessImage()) {
                bitmap = Utils.preProcessBitmap(bitmap);
            }
            isRefresh = false;
            saveBitmapToStorage(bitmap);
            String text = mImageTextReader.getTextFromBitmap(bitmap);

            // Post-execution on UI thread
            handler.post(() -> {
                mProgressIndicator.setVisibility(View.GONE);
                animateImageViewAlpha(1f);
                String cleanText = Html.fromHtml(text).toString().trim();
                showOCRResult(cleanText);
                Toast.makeText(MainActivity.this, "With Confidence: " + mImageTextReader.getAccuracy() + "%", Toast.LENGTH_SHORT).show();
                Utils.putLastUsedText(cleanText);
                updateImageView();
            });
        }

        private void animateImageViewAlpha(float alpha) {
            mImageView.animate().alpha(alpha).setDuration(450).start();
        }

        private void updateImageView() {
            Bitmap bitmap = loadBitmapFromStorage();
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }
    }

    private class DownloadTraining implements Runnable {
        private final String dataType;
        private final  Set<Language> languages;
        private String size;

        public DownloadTraining(String dataType, Set<Language> langs) {
            this.dataType = dataType;
            this.languages = langs;
        }

        @Override
        public void run() {
            handler.post(() -> {
                mProgressMessage.setText(getString(R.string.downloading_language));
                mDownloadLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            });

            final boolean[] success={true};
            for (Language lang : languages) {
               success[0] = success[0] && downloadTrainingData(dataType, lang.getCode());
            }
            handler.post(() -> {
                mDownloadLayout.setVisibility(View.GONE);
                if (success[0]) {
                    initializeOCR();
                } else {
                    Toast.makeText(MainActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @SuppressLint("DefaultLocale")
        private boolean downloadTrainingData(String dataType, String lang) {
            String downloadURL = getDownloadUrl(dataType, lang);
            if (downloadURL == null) {
                return false;
            }
            try {
                URL url = new URL(downloadURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(false);
                downloadURL = followRedirects(conn, downloadURL);
                conn = (HttpURLConnection) new URL(downloadURL).openConnection();
                conn.connect();
                int totalContentSize = conn.getContentLength();
                if (totalContentSize <= 0) {
                    return false;
                }
                size = Utils.getSize(totalContentSize);

                // Switch from indeterminate to determinate progress bar
                handler.post(() -> {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressMessage.setText(String.format("0%s%s", getString(R.string.percentage_downloaded), size));
                    mProgressBar.setProgress(0);               // Reset progress bar to 0
                });

                try (InputStream input = new BufferedInputStream(conn.getInputStream()); OutputStream output = new FileOutputStream(new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang)))) {

                    byte[] data = new byte[6 * 1024];
                    int downloaded = 0;
                    int count;

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                        downloaded += count;
                        int percentage = (downloaded * 100) / totalContentSize;
                        handler.post(() -> {
                            mProgressBar.setProgress(percentage);
                            mProgressMessage.setText(String.format("%d%s%s.", percentage, getString(R.string.percentage_downloaded), size));
                        });
                    }
                    output.flush();
                }

                return true;
            } catch (IOException e) {
                Log.e(TAG, "Download failed: " + e.getLocalizedMessage());
                return false;
            }
        }

        private String getDownloadUrl(String dataType, String lang) {
            switch (dataType) {
                case "best":
                    return lang.equals("akk") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST : lang.equals("eqo") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU : String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang);
                case "standard":
                    return lang.equals("akk") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_STANDARD : lang.equals("eqo") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU : String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang);
                default: // Assuming "fast" is the default
                    return lang.equals("akk") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_FAST : lang.equals("eqo") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU : String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang);
            }
        }

        private String followRedirects(HttpURLConnection conn, String downloadURL) throws IOException {
            while (true) {
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                    String location = conn.getHeaderField("Location");
                    URL base = new URL(downloadURL);
                    downloadURL = new URL(base, location).toExternalForm(); // Handle relative URLs
                    conn = (HttpURLConnection) new URL(downloadURL).openConnection(); // Re-open connection
                } else {
                    break; // No more redirects
                }
            }
            return downloadURL;
        }
    }

}
