package io.github.subhamtyagi.ocr;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import io.github.subhamtyagi.ocr.ocr.ImageTextReader;
import io.github.subhamtyagi.ocr.utils.Constants;
import io.github.subhamtyagi.ocr.utils.CrashUtils;
import io.github.subhamtyagi.ocr.utils.SpUtil;
import io.github.subhamtyagi.ocr.utils.Utils;
import io.github.subhamtyagi.ocr.views.BoxImageView;


/**
 * Apps MainActivity where all important works is going on
 */
public class MainActivity extends AppCompatActivity implements TessBaseAPI.ProgressNotifier {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SETTINGS = 797;
    private static final int REQUEST_CODE_SELECT_IMAGE = 172;
    /**
     * a progressDialog to show downloading Dialog and also reused for recognition dialog
     */
    ProgressDialog mProgressDialog;
    private CrashUtils crashUtils;
    private ConvertImageToTextTask convertImageToTextTask;
    private DownloadTrainingTask downloadTrainingTask;

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
     * selected language on image or used for detection
     */
    private String mLanguage;
    /**
     * a AlertDialog for showing when language data doesn't exists
     */
    private AlertDialog dialog;
    /**
     * Custom Image View
     */
    private BoxImageView mBoxImageView;
    /**
     * Result
     */
    private TextView mTextResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpUtil.getInstance().init(this);

        crashUtils = new CrashUtils(getApplicationContext(), "");

        mBoxImageView = findViewById(R.id.source_image);
        mTextResult = findViewById(R.id.resultant_text);
        //boxImageView.setScaleType(ImageView.ScaleType.MATRIX);
        initDirectories();
        /**
         *initialize the OCR for faster access at later time
         */
        initializeOCR();
        /*
         * check if this was initiated by shared menu if yes then get the image uri and get the text
         * language will be preselected by user in settings
         */
        initIntent();
        initViews();
    }

    private void initViews() {
        // select button binding
        findViewById(R.id.btn_select_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
                    selectImage();
                } else {
                    setLanguageData();
                }

            }
        });
        // copy button binding
        findViewById(R.id.btn_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("nonsense_data", mTextResult.getText());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity.this, R.string.data_copied, Toast.LENGTH_SHORT).show();
            }
        });


        /*
         * check if there is previous image
         * if yes then show this on home screen
         */
        /*Uri uri = Uri.parse(SpUtil.getInstance().getString(getString(R.string.key_last_use_image_location)));
        if (uri != null) {
            //TODO:
            mBoxImageView.setImageURI(uri);

        }*/

        if (SpUtil.getInstance().getBoolean(getString(R.string.key_persist_data), true)) {
            Bitmap bitmap = loadBitmap();
            if (bitmap != null) {
                mBoxImageView.setImageBitmap(bitmap);
            }

            /*
            * check if there is previous image text
            * if yes then show this on home screen
            */

            String text = SpUtil.getInstance().getString(getString(R.string.key_last_use_image_text));
            if (text != null) {
                mTextResult.setText(Html.fromHtml(text));
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
                    //TODO: nothing to do
                    mBoxImageView.setImageURI(imageUri);
                    CropImage.activity(imageUri).start(this);
                }
            }
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
     * if there is no language training data in directory than it will ask for download
     */
    private void initializeOCR() {
        File cf;
        mTrainingDataType = SpUtil.getInstance().getString(getString(R.string.key_tess_training_data_source), "best");
        mLanguage = SpUtil.getInstance().getString(this.getString(R.string.key_language_for_tesseract), "eng");
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

        if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
            try {
                mImageTextReader = ImageTextReader.geInstance(cf.getAbsolutePath(), mLanguage, this);
                if (!ImageTextReader.success) {
                    File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, mLanguage));
                    destf.delete();
                    setLanguageData();
                }
                if (mImageTextReader == null) {
                    // something is bad
                }
            } catch (Exception e) {
                crashUtils.logException(e);
                File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, mLanguage));
                destf.delete();
                setLanguageData();
            }
        } else {
            setLanguageData();
        }

    }

    /**
     * set the language based on selected in Settings
     * if language training data is not exists then it will download it
     */
    private void setLanguageData() {

        mTrainingDataType = SpUtil.getInstance().getString(getString(R.string.key_tess_training_data_source), "best");
        mLanguage = SpUtil.getInstance().getString(getString(R.string.key_language_for_tesseract), "eng");


        if (!isLanguageDataExists(mTrainingDataType, mLanguage)) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission")
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                //You are not connected to Internet

                Toast.makeText(this, getString(R.string.you_are_not_connected_to_internet), Toast.LENGTH_SHORT).show();
            } else if (ni.isConnected()) {
                // show confirmation dialog.
                String msg = String.format(getString(R.string.download_description), mLanguage);
                dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.training_data_missing)
                        .setCancelable(false)
                        .setMessage(msg)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                //  download2(mTrainingDataType,mLanguage);
                                downloadTrainingTask = new DownloadTrainingTask();
                                downloadTrainingTask.execute(mTrainingDataType, mLanguage);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if (!isLanguageDataExists(mTrainingDataType, mLanguage)) {
                                    //  finish();
                                }

                            }
                        }).create();
                dialog.show();

            } else {

                Toast.makeText(this, getString(R.string.you_are_not_connected_to_internet), Toast.LENGTH_SHORT).show();

                //You are not connected to Internet
            }
        } else {
            initializeOCR();
        }

    }


    /**
     * Check if language data exists
     *
     * @param dataType data type i.e best, fast, standard
     * @param lang     language
     * @return true if language data exists
     */
    private boolean isLanguageDataExists(final String dataType, final String lang) {
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

        File language = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang));
        boolean r = language.exists();
        Log.d(TAG, "isLanguageDataExists: path name is ==" + language.getAbsolutePath());
        Log.v(TAG, "training data for " + lang + " exists? " + r);

        return r;
    }


    /**
     * select the image when button is clicked
     * using edmodo
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
        SpUtil.getInstance().putString(getString(R.string.key_last_use_image_location), imageUri.toString());

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mBoxImageView.setImageURI(imageUri);
        //TODO: do this in Task
        convertImageToTextTask = new ConvertImageToTextTask();
        convertImageToTextTask.execute(bitmap);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SETTINGS) {
            setLanguageData();
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_IMAGE) {
                final boolean isCamera;
                if (data == null || data.getData() == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                }
                Uri selectedImageUri;

                selectedImageUri = data.getData();
                if (selectedImageUri == null) return;
                convertImageToText(selectedImageUri);


            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                convertImageToText(result.getUri());
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (convertImageToTextTask != null && convertImageToTextTask.getStatus() == AsyncTask.Status.RUNNING) {
            convertImageToTextTask.cancel(true);
            Log.d(TAG, "onDestroy: image processing canceled");
        }
        if (downloadTrainingTask != null && downloadTrainingTask.getStatus() == AsyncTask.Status.RUNNING) {
            downloadTrainingTask.cancel(true);
        }
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop: called");
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


    static boolean isRefresh = false;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
        } else if (id == R.id.action_refresh) {
            if (isLanguageDataExists(mTrainingDataType, mLanguage)) {

                Drawable drawable = mBoxImageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    if (bitmap != null) {
                        isRefresh = true;
                        new ConvertImageToTextTask().execute(bitmap);
                    }
                } else {
                    findViewById(R.id.btn_select_image).performClick();
                }

            } else {
                setLanguageData();
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressValues(final TessBaseAPI.ProgressValues progressValues) {
        Log.d(TAG, "onProgressValues: percent " + progressValues.getPercent());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null) {
                    int temp = (int) (progressValues.getPercent() * 1.46);
                    mProgressDialog.setMessage(temp + getString(R.string.percentage_converted_to_text));
                    mProgressDialog.show();
                }
            }
        });

    }

    void download2(String dataType, String lang) {

        // boolean result = true;
        String downloadURL;
        //String location;

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
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadURL));
        request.setTitle("Downloading training data..").setDescription("Downloding the training data for selected language");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        request.setDestinationInExternalFilesDir(this, null, "best/tessdata");
        downloadManager.enqueue(request);
    }

    public void saveBitmap(Bitmap bitmap) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = openFileOutput("last_file.jpeg", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap loadBitmap() {
        Bitmap bitmap = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = openFileInput("last_file.jpeg");
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * A Async Task to convert the image into text the return the text in String
     */
    private class ConvertImageToTextTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            if (!isRefresh &&
                    SpUtil.getInstance().getBoolean(getString(R.string.key_grayscale_image_ocr), true)) {

                bitmap = Utils.preProcessBitmap(bitmap);

                // bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), true);
            }

            isRefresh = false;
            saveBitmap(bitmap);
            return "";
            //  return mImageTextReader.getTextFromBitmap(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle(getString(R.string.processing));
            mProgressDialog.setMessage(getString(R.string.converting_image));
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(String text) {
            if (mProgressDialog != null) {
                mProgressDialog.cancel();
                mProgressDialog = null;

            }
            //mTextResult.setText(text);
            mTextResult.setText(Html.fromHtml(text));
            if (SpUtil.getInstance().getBoolean(getString(R.string.key_persist_data), true)) {
                SpUtil.getInstance().putString(getString(R.string.key_last_use_image_text), text);
            }

            Bitmap bitmap = loadBitmap();
            if (bitmap != null) {
                mBoxImageView.setImageBitmap(bitmap);
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
            initializeOCR();
        }


        @Override
        protected Boolean doInBackground(String... languages) {
            return downloadTraningData(languages[0], languages[1]);
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
                    Log.v(TAG, "downloading " + downloadURL);
                    try {
                        url = new URL(downloadURL);
                    } catch (java.net.MalformedURLException ex) {
                        Log.e(TAG, "url " + downloadURL + " is bad: " + ex);
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
                Log.e(TAG, "failed to download " + downloadURL + " : " + e);
                e.printStackTrace();
            }
            return result;
        }
    }

}
