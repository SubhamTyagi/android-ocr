package io.github.subhamtyagi.ocr;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.subhamtyagi.ocr.ocr.ImageTextReader;
import io.github.subhamtyagi.ocr.utils.Constants;
import io.github.subhamtyagi.ocr.utils.SpUtil;
import io.github.subhamtyagi.ocr.utils.Utils;
import io.github.subhamtyagi.ocr.views.BoxImageView;


/**
 * Apps MainActivity where all important works is going on
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SETTINGS = 797;
    private static final int REQUEST_CODE_SELECT_IMAGE = 172;
    /**
     * a progressDialog to show downloading Dialog and also reused for recognition dialog
     */
    ProgressDialog mProgressDialog;
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
     *
     */
    // private Uri mOutputFileUri;

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
                selectImage();
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
    }

    private void initIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    mBoxImageView.setImageURI(imageUri);
                    CropImage.activity(imageUri).start(this);
                }
            }
        } else {
            /*
             * check if there is previous image
             * if yes then show this on home screen
             */
            Uri uri = Uri.parse(SpUtil.getInstance().getString(getString(R.string.key_last_use_image_location)));
            if (uri != null) {
                mBoxImageView.setImageURI(uri);
            }
            /*
             * check if there is previous image text
             * if yes then show this on home screen
             */

            String text = SpUtil.getInstance().getString(getString(R.string.key_last_use_image_text));
            if (text != null) {
                mTextResult.setText(text);
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
        mLanguage = SpUtil.getInstance().getString(getString(R.string.key_language_for_tesseract), "eng");
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

        Log.d(TAG, "initializeOCR: current Directory is =" + cf.getAbsolutePath());
        if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
            mImageTextReader = ImageTextReader.geInstance(cf.getAbsolutePath(), mLanguage);
        } else {
            setLanguageData();
        }

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


    /**
     * Executed after onActivityResult or when used from shared menu
     * <p>
     * convert the image uri to bitmap
     * call the appropriate AsyncTask based on Settings
     *
     * @param imageUri uri of selected image
     */
    private void convertImageToText(Uri imageUri) {

        try {
            SpUtil.getInstance().putString(getString(R.string.key_last_use_image_location), imageUri.toString());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            mBoxImageView.setImageBitmap(bitmap);
            if (SpUtil.getInstance().getBoolean(getString(R.string.key_draw_box), false)) {
                Log.d(TAG, "convertImageToText: draw box");
                //  new ConvertImageToTextTask2().execute(bitmap);

                new ConvertImageToTextTask().execute(bitmap);

            } else {
                Log.d(TAG, "convertImageToText: not  draw box");
                new ConvertImageToTextTask().execute(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * destroy the dialog instance: memory leak  surety
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), REQUEST_CODE_SETTINGS);
        } else if (id == R.id.action_refresh) {
            Drawable drawable = mBoxImageView.getDrawable();
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null)
                    new ConvertImageToTextTask().execute(bitmap);
            } else {
                findViewById(R.id.btn_select_image).performClick();
            }

        }
        return super.onOptionsItemSelected(item);
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
                Log.d(TAG, "onResume: you are not connected to internet");
            } else if (ni.isConnected()) {
                // show confirmation dialog.
                String msg = String.format("Do you want to download '%s' data for '%s' source?", mLanguage, mTrainingDataType);
                dialog = new AlertDialog.Builder(this)
                        .setTitle("Current language data missing!")
                        .setMessage(msg)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //downloadTrainingData(mTrainingDataType,mLanguage);
                                new DownloadTrainingTask().execute(mTrainingDataType, mLanguage);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();
                dialog.show();

            } else {
                Log.d(TAG, "onResume: you are not connected to internet");
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

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    /**
     * A Async Task to convert the image into text the return the text in String
     */
    private class ConvertImageToTextTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = bitmaps[0];
            if (SpUtil.getInstance().getBoolean(getString(R.string.key_grayscale_image_ocr), true)) {
                bitmap = Utils.convertToGrayscale(bitmaps[0]);
                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), true);
            }
            return mImageTextReader.getTextFromBitmap(bitmap);
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
            mProgressDialog.cancel();
            mProgressDialog = null;
            mTextResult.setText(text);
            SpUtil.getInstance().putString(getString(R.string.key_last_use_image_text), text);
        }

    }

    /**
     * Download the training Data and save this to external storage
     */
    private class DownloadTrainingTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPostExecute(Boolean bool) {
            mProgressDialog.cancel();
            mProgressDialog = null;
            initializeOCR();
        }

        @Override
        protected Boolean doInBackground(String... languages) {
            return downloadTraningData(languages[0], languages[1]);
        }

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
                InputStream input = new BufferedInputStream(url.openStream());

                File destf = new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang));
                destf.createNewFile();
                OutputStream output = new FileOutputStream(destf);

                byte[] data = new byte[1024 * 6];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
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
