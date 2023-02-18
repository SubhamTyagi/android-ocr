package io.github.subhamtyagi.ocr;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.subhamtyagi.ocr.ocr.ImageTextReader;
import io.github.subhamtyagi.ocr.spinner.SpinnerDialog;
import io.github.subhamtyagi.ocr.utils.Constants;
import io.github.subhamtyagi.ocr.utils.CrashUtils;
import io.github.subhamtyagi.ocr.utils.SpUtil;
import io.github.subhamtyagi.ocr.utils.Utils;
import kotlin.Triple;

/**
 * Apps MainActivity where all important works is going on
 */
public class MainActivity extends AppCompatActivity implements TessBaseAPI.ProgressNotifier {

    public static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_SETTINGS = 797;
    private static boolean isRefresh = false;
    /**
     * a progressDialog to show downloading Dialog
     */
    private ProgressDialog mProgressDialog;
    /**
     * A spinner dialog shown on share menu
     */
    private SpinnerDialog spinnerDialog;
    private ArrayList<String> languagesNames;
    private CrashUtils crashUtils;
    private ConvertImageToTextTask convertImageToTextTask;
    private File dirBest;
    private File dirStandard;
    private File dirFast;
    private File currentDirectory;
    /**
     * Our ImageTextReader Instance
     */
    private ImageTextReader mImageTextReader;
    /**
     * TrainingDataType: i.e Best, Standard, Fast
     */
    private String mTrainingDataType;
    /**
     * Page segmentation mode
     */
    private int mPageSegMode;
    /**
     * AlertDialog for showing when language data doesn't exists
     */
    private AlertDialog dialog;
    /**
     * Image View
     */
    private ImageView mImageView;
    /**
     * ProgressIndicator
     */
    private LinearProgressIndicator mProgressIndicator;
    /**
     * SwipeRefreshLayout
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;
    /**
     * FloatingActionButton
     */
    private FloatingActionButton mFloatingActionButton;
    /**
     * Language name to be displayed
     */
    private TextView mLanguageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpUtil.getInstance().init(this);
        crashUtils = new CrashUtils(getApplicationContext(), "");

        languagesNames = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.ocr_engine_language)));

        mImageView = findViewById(R.id.source_image);
        mProgressIndicator = findViewById(R.id.progress_indicator);
        mSwipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        mFloatingActionButton = findViewById(R.id.btn_scan);
        mLanguageName = findViewById(R.id.language_name1);

        initDirectories();
        /*
         * check if this was initiated by shared menu if yes then get the image uri and get the text
         * language will be preselected by user in settings
         */
        initIntent();
        initializeOCR();
        initViews();
    }

    private void initViews() {

        mFloatingActionButton.setOnClickListener(v -> {
            if (noLanguageIsMissing(mTrainingDataType, Utils.getLast3UsedLanguage(this).getFirst())) {
                if (mImageTextReader != null) {
                    selectImage();
                } else {
                    initializeOCR();
                }
            } else {
                downloadLanguageData(mTrainingDataType);
            }

        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (noLanguageIsMissing(mTrainingDataType, Utils.getTrainingDataLanguages(this))) {
                if (mImageTextReader != null) {
                    Drawable drawable = mImageView.getDrawable();
                    if (drawable != null) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (bitmap != null) {
                            isRefresh = true;
                            new ConvertImageToTextTask().execute(bitmap);
                        }
                    }
                } else {
                    initializeOCR();
                }
            } else {
                downloadLanguageData(mTrainingDataType);
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

    private void initIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    mImageView.setImageURI(imageUri);
                    showLanguageSelectionDialog(imageUri);
                }
            }
        } else if (action.equals("screenshot")) {
            // uri
        }
    }


    private void showLanguageSelectionDialog(Uri imageUri) {
        spinnerDialog = new SpinnerDialog(MainActivity.this, languagesNames, getString(R.string.select_search_language), R.style.DialogAnimations_SmileWindow);
        spinnerDialog.setShowKeyboard(false);

        spinnerDialog.bindOnSpinnerListener((item, position) -> {
            startOCRFromShareMenu(imageUri, Collections.singleton(new Language(this, item)));
        });

        spinnerDialog.showSpinnerDialog();
        View view = spinnerDialog.getView();
        RadioButton radioButton1 = view.findViewById(R.id.rb_language1);
        RadioButton radioButton2 = view.findViewById(R.id.rb_language2);
        RadioButton radioButton3 = view.findViewById(R.id.rb_language3);
        Triple<Set<Language>, Set<Language>, Set<Language>> languages = Utils.getLast3UsedLanguage(this);

        radioButton1.setText(languages.getFirst().stream().map(Language::getName).collect(Collectors.joining(", ")));
        radioButton2.setText(languages.getSecond().stream().map(Language::getName).collect(Collectors.joining(", ")));
        radioButton3.setText(languages.getThird().stream().map(Language::getName).collect(Collectors.joining(", ")));

        radioButton1.setOnClickListener(view1 -> startOCRFromShareMenu(imageUri, languages.getFirst()));
        radioButton2.setOnClickListener(view1 -> startOCRFromShareMenu(imageUri, languages.getSecond()));
        radioButton3.setOnClickListener(view1 -> startOCRFromShareMenu(imageUri, languages.getThird()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLanguageName.setText(Utils.getLast3UsedLanguage(this).getFirst()
                .stream()
                .map(Language::getName)
                .collect(Collectors.joining(", ")));
    }

    public void startOCRFromShareMenu(Uri imageUri, Set<Language> languages) {
        initializeOCR(languages);
        Utils.setLastUsedLanguage(this, languages);
        // Log.d("radio", "showLanguageSelectionDialog: " + mLanguage);
        spinnerDialog.closeSpinnerDialog();
        mImageView.setImageURI(imageUri);
        if (noLanguageIsMissing(mTrainingDataType, languages)) {
            CropImage.activity(imageUri).start(MainActivity.this);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initDirectories() {
        dirBest = new File(getExternalFilesDir("best").getAbsolutePath());
        dirFast = new File(getExternalFilesDir("fast").getAbsolutePath());
        dirStandard = new File(getExternalFilesDir("standard").getAbsolutePath());
        dirBest.mkdirs();
        dirStandard.mkdirs();
        dirFast.mkdirs();
        currentDirectory = new File(dirBest, "tessdata");
        currentDirectory.mkdirs();
        currentDirectory = new File(dirStandard, "tessdata");
        currentDirectory.mkdirs();
        currentDirectory = new File(dirFast, "tessdata");
        currentDirectory.mkdirs();
    }

    /**
     * initialize the OCR i.e tesseract api
     * if there is no training data in directory than it will ask for download
     */
    private void initializeOCR(Set<Language> languages) {
        File cf;
        mTrainingDataType = Utils.getTrainingDataType();
        Log.d(TAG, "initializeOCR: " + Utils.getLast3UsedLanguage(this).getFirst());
        mPageSegMode = Utils.getPageSegMode();

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

        if (noLanguageIsMissing(mTrainingDataType, Utils.getLast3UsedLanguage(this).getFirst())) {
            //region Initialize image text reader
            new Thread() {
                @Override
                public void run() {
                    try {
                        if (mImageTextReader != null) {
                            mImageTextReader.tearDownEverything();
                        }
                        mImageTextReader = ImageTextReader.getInstance(
                                cf.getAbsolutePath(),
                                languages,
                                mPageSegMode,
                                MainActivity.this);
                        //check if current language data is valid
                        //if it is invalid(i.e. corrupted, half downloaded, tempered) then delete it
                        if (!mImageTextReader.success) {
                            File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, languages));
                            destf.delete();
                            mImageTextReader = null;
                        }

                    } catch (Exception e) {
                        crashUtils.logException(e);
                        File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, languages));
                        destf.delete();
                        mImageTextReader = null;
                    }
                }
            }.start();
            //endregion
        } else {
            Log.d(TAG, "initializeOCR: language data doesn't exist " + languages);
            downloadLanguageData(mTrainingDataType, languages);
        }
    }
    private void initializeOCR() {
        initializeOCR(Utils.getTrainingDataLanguages(this));
    }

    @SuppressLint("StringFormatInvalid")
    private void downloadLanguageData(final String dataType, Set<Language> languages) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        for (Language language: languages) {
            if (languageDataMissing(dataType, language)) {
                if (ni == null || !ni.isConnected()) {
                    Toast.makeText(this, getString(R.string.you_are_not_connected_to_internet), Toast.LENGTH_SHORT).show();
                    break;
                }
                //region show confirmation dialog, On 'yes' download the training data.
                String msg = String.format(getString(R.string.download_description), language.getName());
                dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.training_data_missing)
                        .setCancelable(false)
                        .setMessage(msg)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            dialog.cancel();
                            new DownloadTrainingTask().execute(dataType, language.getCode());
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> {
                            dialog.cancel();
                        }).create();
                dialog.show();
                //endregion
            }

        }
    }
    private void downloadLanguageData(final String dataType) {
        downloadLanguageData(dataType, Utils.getTrainingDataLanguages(this));
    }

    private boolean noLanguageIsMissing(final String dataType, final Set<Language> languages) {
        for (Language language : languages) {
            if (languageDataMissing(dataType, language))
                return false;
        }
        return true;
    }

    /**
     * Check if language data exists
     *
     * @param dataType data type i.e best, fast, standard
     * @param language     language
     * @return true if language data exists
     */
    private boolean languageDataMissing(final @NonNull String dataType, final @NonNull Language language) {
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

    /**
     * select the image when button is clicked
     * using
     */
    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    /**
     * Executed after onActivityResult or when used from shared menu
     * <p>
     * convert the image uri to bitmap
     * call the appropriate AsyncTask based on Settings
     *
     * @param imageUri uri of selected image
     */
    private void convertImageToText(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
            crashUtils.logException(e);

        }
        mImageView.setImageURI(imageUri);
        convertImageToTextTask = new ConvertImageToTextTask();
        convertImageToTextTask.execute(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            initializeOCR();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (noLanguageIsMissing(mTrainingDataType, Utils.getLast3UsedLanguage(this).getFirst())) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    convertImageToText(result.getUri());
                } else initializeOCR();

            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (convertImageToTextTask != null && convertImageToTextTask.getStatus() == AsyncTask.Status.RUNNING) {
            convertImageToTextTask.cancel(true);
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
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

        /*if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }

        if (convertImageToTextTask !=null && convertImageToTextTask.getStatus()== AsyncTask.Status.RUNNING){
            convertImageToTextTask.cancel(true);
            Log.d(TAG, "onDestroy: image processing canceled");
        }
        if (downloadTrainingTask !=null && downloadTrainingTask.getStatus()== AsyncTask.Status.RUNNING){
            downloadTrainingTask.cancel(true);
        }*/
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            crashUtils.logException(e);
        } catch (IOException e) {
            e.printStackTrace();
            crashUtils.logException(e);
        }
    }

    public Bitmap loadBitmapFromStorage() {
        Bitmap bitmap = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = openFileInput("last_file.jpeg");
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            crashUtils.logException(e);
        } catch (IOException e) {
            e.printStackTrace();
            crashUtils.logException(e);
        }
        return bitmap;
    }

    public void showOCRResult(String text) {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            BottomSheetResultsFragment bottomSheetResultsFragment = BottomSheetResultsFragment.newInstance(text);
            bottomSheetResultsFragment.show(getSupportFragmentManager(), "bottomSheetResultsFragment");
        }

    }

    /**
     * A Async Task to convert the image into text the return the text in String
     */
    private class ConvertImageToTextTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            if (!isRefresh && Utils.isPreProcessImage()) {
                bitmap = Utils.preProcessBitmap(bitmap);
                // bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), true);
            }
            isRefresh = false;
            saveBitmapToStorage(bitmap);
            return mImageTextReader.getTextFromBitmap(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressIndicator.setProgress(0);
            mProgressIndicator.setVisibility(View.VISIBLE);
            mImageView.animate()
                    .alpha(.2f)
                    .setDuration(450)
                    .start();
        }

        @Override
        protected void onPostExecute(String text) {
            mProgressIndicator.setVisibility(View.GONE);
            mImageView.animate()
                    .alpha(1f)
                    .setDuration(450)
                    .start();
            String clean_text = Html.fromHtml(text).toString().trim();
            showOCRResult(clean_text);
            Toast.makeText(MainActivity.this, "With Confidence:" + mImageTextReader.getAccuracy() + "%", Toast.LENGTH_SHORT).show();

            Utils.putLastUsedText(clean_text);
            Bitmap bitmap = loadBitmapFromStorage();
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
        }

    }

    /**
     * Download the training Data and save this to external storage
     */
    private class DownloadTrainingTask extends AsyncTask<String, Integer, Boolean> {
        String size;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle(getString(R.string.downloading));
            mProgressDialog.setMessage(getString(R.string.downloading_language));
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int percentage = values[0];
            if (mProgressDialog != null) {
                mProgressDialog.setMessage(percentage + getString(R.string.percentage_downloaded) + size);
                mProgressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if (mProgressDialog != null) {
                mProgressDialog.cancel();
                mProgressDialog = null;
            }
            initializeOCR(Utils.getTrainingDataLanguages(MainActivity.this));
        }

        @Override
        protected Boolean doInBackground(String... languages) {
            String dataType = languages[0];
            String lang = languages[1];
            return downloadTraningData(dataType, lang);
        }

        /**
         * done the actual work of download
         *
         * @param dataType data type i.e best, fast, standard
         * @param lang     language
         * @return true if success else false
         */
        private boolean downloadTraningData(String dataType, String lang) {
            boolean result = true;
            String downloadURL;
            String location;

            switch (dataType) {
                case "best":
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang);
                    break;
                case "standard":
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang);
                    break;
                default:
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang);
            }

            URL url, base, next;
            HttpURLConnection conn;
            try {
                while (true) {
                    try {
                        url = new URL(downloadURL);
                    } catch (java.net.MalformedURLException ex) {
                        crashUtils.logException(ex);
                        return false;
                    }
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(false);
                    switch (conn.getResponseCode()) {
                        case HttpURLConnection.HTTP_MOVED_PERM:
                        case HttpURLConnection.HTTP_MOVED_TEMP:
                            location = conn.getHeaderField("Location");
                            base = new URL(downloadURL);
                            next = new URL(base, location);  // Deal with relative URLs
                            downloadURL = next.toExternalForm();
                            continue;
                    }
                    break;
                }
                conn.connect();

                int totalContentSize = conn.getContentLength();
                size = Utils.getSize(totalContentSize);

                InputStream input = new BufferedInputStream(url.openStream());

                File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang));
                destf.createNewFile();
                OutputStream output = new FileOutputStream(destf);

                byte[] data = new byte[1024 * 6];
                int count, downloaded = 0;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                    downloaded += count;
                    int percentage = (downloaded * 100) / totalContentSize;
                    publishProgress(percentage);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
                crashUtils.logException(e);
            }
            return result;
        }
    }
}
