package io.github.subhamtyagi.ocr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 677;
    static ConnectivityManager cm;
    private ImageTextReader imageTextReader;
    private String dataType;
    private String lang;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        SpUtil.getInstance().init(this);
        storagePermission();

    }

    private void storagePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                },
                REQUEST_CODE_STORAGE_PERMISSION);
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

    private void convertImageToText(Bitmap bitmap) {
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
        imageTextReader = ImageTextReader.geInstance(path, lang);

        String text = imageTextReader.getTextFromBitmap(bitmap);
        Log.d(TAG, "convertImageToText: text==" + text);

    }

    @Override
    protected void onResume() {
        super.onResume();
        dataType = SpUtil.getInstance().getString(getString(R.string.key_tess_training_data_source), "best");
        lang = SpUtil.getInstance().getString(getString(R.string.key_language_for_tesseract), "eng");

        if (!isLanguageDataExists(dataType, lang)) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                //You are not connected to Internet
                Log.d(TAG, "onResume: you are not connected to internet");
            } else if (ni.isConnected()) {
                new DownloadTrainingTask().execute(dataType, lang);
            } else {
                Log.d(TAG, "onResume: you are not connected to internet");
                //You are not connected to Internet
            }
        }

    }

    private static class DownloadTrainingTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPostExecute(Boolean bool) {
            //say success...
        }

        @Override
        protected Boolean doInBackground(String... languages) {
            return downloadTraningData(languages[0], languages[1]);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // publish progress...
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
}
