package io.github.subhamtyagi.ocr;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
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

import io.github.subhamtyagi.ocr.ocr.ImageTextReader;
import io.github.subhamtyagi.ocr.utils.Constants;
import io.github.subhamtyagi.ocr.utils.SpUtil;
import io.github.subhamtyagi.ocr.utils.Utils;


public class MainActivity extends AppCompatActivity {

    //static final String EXTRA_MESSAGE = BuildConfig.APPLICATION_ID + ".msg";
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 677;
    private static final int REQUEST_CODE_SETTINGS = 797;
    private static final int REQUEST_CODE_SELECT_IMAGE = 172;
    static ConnectivityManager cm;
    ProgressDialog progressDialog;
    private ImageTextReader imageTextReader;
    private String dataType;
    private String lang;
    private AlertDialog dialog;

    private Uri outputFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SpUtil.getInstance().init(this);
        requestStoragePermission();
        if (requestStoragePermission()) {
            initializeOCR();
        }
        findViewById(R.id.select_img).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void initializeOCR() {
        dataType = SpUtil.getInstance().getString(getString(R.string.key_tess_training_data_source), "best");
        lang = SpUtil.getInstance().getString(getString(R.string.key_language_for_tesseract), "eng");
        String path;
        switch (dataType) {
            case "best":
                path = Constants.TESSERACT_PATH_BEST;
                break;
            case "standard":
                path = Constants.TESSERACT_PATH_STANDARD;
                break;
            default:
                path = Constants.TESSERACT_PATH_FAST;
        }
        if (isLanguageDataExists(dataType, lang)) {
            imageTextReader = ImageTextReader.geInstance(path, lang);
        } else {
            setLanguageData();
        }

    }

    private void selectImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void selectImage2() {
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
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
                    selectedImageUri = outputFileUri;
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

    private void convertImageToText(Uri imageUri) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            new ConvertImageToTextTask().execute(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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
        }
        return super.onOptionsItemSelected(item);
    }


    private void setLanguageData() {
        dataType = SpUtil.getInstance().getString(getString(R.string.key_tess_training_data_source), "best");
        lang = SpUtil.getInstance().getString(getString(R.string.key_language_for_tesseract), "eng");

        if (requestStoragePermission()) {
            if (!isLanguageDataExists(dataType, lang)) {
                cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni == null) {
                    //You are not connected to Internet
                    Log.d(TAG, "onResume: you are not connected to internet");
                } else if (ni.isConnected()) {
                    // show confirmation dialog.
                    String msg = String.format("Do you want to download '%s' data for '%s' source?", lang, dataType);
                    dialog = new AlertDialog.Builder(this)
                            .setTitle("Current language data missing!")
                            .setMessage(msg)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    new DownloadTrainingTask().execute(dataType, lang);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new File(Constants.PATH_OF_TESSERACT_DATA_STANDARD).mkdirs();
                new File(Constants.PATH_OF_TESSERACT_DATA_FAST).mkdirs();
                new File(Constants.PATH_OF_TESSERACT_DATA_BEST).mkdirs();
            } else {
                finish();
            }
        }
    }

    private boolean isLanguageDataExists(final String dataFile, final String lang) {
        String fileName;
        switch (dataFile) {
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

    private class DownloadTrainingTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPostExecute(Boolean bool) {
            progressDialog.cancel();
            progressDialog = null;
            initializeOCR();
        }

        @Override
        protected Boolean doInBackground(String... languages) {
            return downloadTraningData(languages[0], languages[1]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Downloading...");
            progressDialog.setMessage("Downloading language data...");
            progressDialog.show();
        }

        private boolean downloadTraningData(String dataSource, String lang) {
            boolean result = true;
            String downloadURL;
            String location;
            String destFileName;
            switch (dataSource) {
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

    private class ConvertImageToTextTask extends AsyncTask<Bitmap, Void, String> {

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            Bitmap bitmap = Utils.convertToGrayscale(bitmaps[0]);
            bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), true);
            return imageTextReader.getTextFromBitmap(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Processing...");
            progressDialog.setMessage("Converting Image to text...");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String text) {
            progressDialog.cancel();
            progressDialog = null;
            Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
        }

    }
}
