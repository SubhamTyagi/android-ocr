package io.github.subhamtyagi.ocr;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.ArrayList;
import java.util.List;

import alirezat775.lib.downloader.Downloader;
import alirezat775.lib.downloader.core.OnDownloadListener;
import io.github.subhamtyagi.ocr.models.RecognizedResults;
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
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 677;
    private static final int REQUEST_CODE_SETTINGS = 797;
    private static final int REQUEST_CODE_SELECT_IMAGE = 172;

    /**
     * a progressDialog to show downloading Dialog and also reused for recognition dialog
     */
    ProgressDialog mProgressDialog;
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
    private Uri mOutputFileUri;

    /**
     * Custom Image View
     */
    private BoxImageView mBoxImageView;

    /**
     * Result
     */
    private TextView mTextResult;

    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SpUtil.getInstance().init(this);
        requestStoragePermission();
        mBoxImageView = findViewById(R.id.source_image);
        mTextResult = findViewById(R.id.resultant_text);
        //boxImageView.setScaleType(ImageView.ScaleType.MATRIX);

        /*
          request for storage permission
          and initialize the OCR for faster access at later time
         */
        if (requestStoragePermission()) {
            initializeOCR();
        }


        /*
         * check if this was initiated by shared menu if yes then get the image uri and get the text
         * language will be preselected by user in settings
         */
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

    /**
     * initialize the OCR i.e tesseract api
     * if there is no language training data in directory than it will ask for download
     */
    private void initializeOCR() {
        mTrainingDataType = SpUtil.getInstance().getString(getString(R.string.key_tess_training_data_source), "best");
        mLanguage = SpUtil.getInstance().getString(getString(R.string.key_language_for_tesseract), "eng");
        final String path;
        switch (mTrainingDataType) {
            case "best":
                path = Constants.TESSERACT_PATH_BEST;
                break;
            case "standard":
                path = Constants.TESSERACT_PATH_STANDARD;
                break;
            default:
                path = Constants.TESSERACT_PATH_FAST;
        }
        if (isLanguageDataExists(mTrainingDataType, mLanguage)) {
            mImageTextReader = ImageTextReader.geInstance(path, mLanguage);
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

    /**
     * Currently not in use
     * select the image when button is clicked
     */
    private void selectImage2() {
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fname);
        mOutputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri);
            cameraIntents.add(intent);
        }
        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, REQUEST_CODE_SELECT_IMAGE);
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
                if (isCamera) {
                    selectedImageUri = mOutputFileUri;
                    if (selectedImageUri == null) return;
                    convertImageToText(selectedImageUri);

                } else {
                    selectedImageUri = data.getData();
                    if (selectedImageUri == null) return;
                    convertImageToText(selectedImageUri);

                }
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
                new ConvertImageToTextTask2().execute(bitmap);

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
            Drawable drawable= mBoxImageView.getDrawable();
            if (drawable!=null) {
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                if (bitmap !=  null)
                    new ConvertImageToTextTask().execute(bitmap);
            }else{
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

        if (requestStoragePermission()) {
            if (!isLanguageDataExists(mTrainingDataType, mLanguage)) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
                                    downloadTrainingData(mTrainingDataType,mLanguage);
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

    }

    private void downloadTrainingData(String mTrainingDataType, String lang) {

        String downloadURL;
        //String location;
        String destFolderName;
        switch (mTrainingDataType) {
            case "best":
                destFolderName = Constants.TESSERACT_DATA_FILE_NAME_BEST;
                downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang);
                break;
            case "standard":
                destFolderName = Constants.TESSERACT_DATA_FILE_NAME_STANDARD;
                downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang);
                break;
            default:
                destFolderName = Constants.TESSERACT_DATA_FILE_NAME_FAST;
                downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang);
        }

        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setTitle(getString(R.string.downloading));
        mProgressDialog.setMessage(getString(R.string.downloading_language));
        mProgressDialog.show();

        Downloader.Builder downloader =new Downloader.Builder(this,downloadURL)
                .downloadDirectory(destFolderName)
                .downloadListener(new OnDownloadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onProgressUpdate(int percent, int downloadSize, int totalSize) {
                String total=getSize(totalSize);
                String downloaded=getSize(downloadSize);
                //publish progress
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }

            @Override
            public void onCompleted(File file) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.cancel();
                        mProgressDialog = null;
                    }
                });

                initializeOCR();
            }

            @Override
            public void onFailure(String s) {

            }

            @Override
            public void onCancel() {

            }
        });
        downloader.build().download();

    }

    private String  getSize(int size) {
        String s = "";
        double kb = (size / 1024);
        double mb = kb / 1024;
        double gb = kb / 1024;
        double tb = kb / 1024;
        if (size < 1024) {
            s = "$size Bytes";
        } else if (size < 1024 * 1024) {
            s = String.format("%.2f", kb) + " KB";
        } else if ( size < 1024 * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB";
        } else if ( size < 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", gb) + " GB";
        } else {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    /**
     * request for storage permission
     * if permission required then it will also request the permission
     *
     * @return false: if no permission required, True: if permission required and
     */
    private boolean requestStoragePermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                &&
                (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                    },
                    REQUEST_CODE_STORAGE_PERMISSION);
        } else {
            return true;
        }
        return false;

    }


    // create the directory
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new File(Constants.PATH_OF_TESSERACT_DATA_STANDARD).mkdirs();
                new File(Constants.PATH_OF_TESSERACT_DATA_FAST).mkdirs();
                new File(Constants.PATH_OF_TESSERACT_DATA_BEST).mkdirs();
                initializeOCR();
            } else {
                finish();
            }
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
        String fileName;
        switch (dataType) {
            case "best":
                fileName = Constants.TESSERACT_DATA_FILE_NAME_BEST;
                break;
            case "standard":
                fileName = Constants.TESSERACT_DATA_FILE_NAME_STANDARD;
                break;
            default:
                fileName = Constants.TESSERACT_DATA_FILE_NAME_FAST;
        }
        File t = new File(String.format(fileName, lang));
        boolean r = t.exists();
        Log.v(TAG, "training data for " + lang + " exists? " + r);
        return r;
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
     * A Async Task to convert the image into @RecognizedResult
     */
    private class ConvertImageToTextTask2 extends AsyncTask<Bitmap, Void, RecognizedResults> {

        @Override
        protected RecognizedResults doInBackground(Bitmap... bitmaps) {
            //Bitmap bitmap = Utils.convertToGrayscale(bitmaps[0]);
            //bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), true);
            return mImageTextReader.getRecognizedResultsFromBitmap(bitmaps[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setTitle("Processing...");
            mProgressDialog.setMessage("Converting Image to text...");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(RecognizedResults recognizedResults) {
            mProgressDialog.cancel();
            mProgressDialog = null;
            mTextResult.setText(recognizedResults.getFullText());
            mBoxImageView.setRecognitionResults(recognizedResults);
            SpUtil.getInstance().putString(getString(R.string.key_last_use_image_text), recognizedResults.getFullText());
            // Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
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
            String destFileName;
            switch (dataType) {
                case "best":
                    destFileName = String.format(Constants.TESSERACT_DATA_FILE_NAME_BEST, lang);
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang);
                    break;
                case "standard":
                    destFileName = String.format(Constants.TESSERACT_DATA_FILE_NAME_STANDARD, lang);
                    downloadURL = String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang);
                    break;
                default:
                    destFileName = String.format(Constants.TESSERACT_DATA_FILE_NAME_FAST, lang);
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
                OutputStream output = new FileOutputStream(destFileName);
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
            }
            return result;
        }
    }

}
