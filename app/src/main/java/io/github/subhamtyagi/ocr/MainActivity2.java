import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements TessBaseAPI.ProgressNotifier {

    private ImageView mImageView;
    private LinearProgressIndicator mProgressIndicator;
    private ImageTextReader mImageTextReader;
    private ExecutorService executorService;
    private AlertDialog dialog;
    private File currentDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.source_image);
        mProgressIndicator = findViewById(R.id.progress_indicator);

        executorService = Executors.newFixedThreadPool(2);  // Initialize the executor
        initializeOCR();
    }

    private void initializeOCR() {
        executorService.submit(() -> {
            // Initialize OCR components (assuming this method initializes the OCR engine)
            Set<Language> languages = Utils.getTrainingDataLanguages(this);
            mImageTextReader = ImageTextReader.getInstance(
                    currentDirectory.getAbsolutePath(), 
                    languages, 
                    Utils.getPageSegMode(), 
                    Utils.getAllParameters(), 
                    Utils.isExtraParameterSet(), 
                    MainActivity.this
            );
        });
    }

    private void convertImageToText(Uri imageUri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mImageView.setImageURI(imageUri);

        // Use ExecutorService for background tasks
        executorService.submit(() -> {
            if (bitmap != null) {
                saveBitmapToStorage(bitmap);
                String resultText = mImageTextReader.getTextFromBitmap(bitmap);
                runOnUiThread(() -> onOCRCompleted(resultText));
            }
        });
    }

    private void onOCRCompleted(String text) {
        mProgressIndicator.setVisibility(View.GONE);
        String cleanText = text.trim();  // Removed HTML processing for simplicity
        showOCRResult(cleanText);
        Toast.makeText(MainActivity.this, "OCR Completed", Toast.LENGTH_SHORT).show();
    }

    private void showOCRResult(String text) {
        if (this.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            // BottomSheet to show results
            BottomSheetResultsFragment bottomSheetResultsFragment = BottomSheetResultsFragment.newInstance(text);
            bottomSheetResultsFragment.show(getSupportFragmentManager(), "bottomSheetResultsFragment");
        }
    }

    public void saveBitmapToStorage(Bitmap bitmap) {
        try (FileOutputStream fileOutputStream = openFileOutput("last_file.jpeg", MODE_PRIVATE)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
        runOnUiThread(() -> mProgressIndicator.setProgress((int) (progressValues.getPercent() * 1.46)));
    }

    // Method to handle language data download
    private void downloadLanguageData(final String dataType, Set<Language> languages) {
        for (Language language : languages) {
            if (languageDataMissing(dataType, language)) {
                dialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.training_data_missing)
                        .setMessage("Downloading " + language.getName() + " data...")
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            // Submit to Executor for background download
                            executorService.submit(() -> downloadTrainingData(dataType, language.getCode()));
                        })
                        .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel())
                        .create();
                dialog.show();
            }
        }
    }

    private boolean downloadTrainingData(String dataType, String lang) {
        String downloadURL = getDownloadUrl(dataType, lang);
        try {
            URL url = new URL(downloadURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int totalContentSize = conn.getContentLength();
            if (totalContentSize <= 0) return false;

            try (InputStream input = new BufferedInputStream(conn.getInputStream());
                 OutputStream output = new FileOutputStream(new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang)))) {
                byte[] data = new byte[6 * 1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getDownloadUrl(String dataType, String lang) {
        switch (dataType) {
            case "best":
                return String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang);
            case "standard":
                return String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang);
            default:
                return String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang);
        }
    }

    private boolean languageDataMissing(@NonNull String dataType, @NonNull Language language) {
        switch (dataType) {
            case "best":
                currentDirectory = new File(getExternalFilesDir("best"), "tessdata");
                break;
            case "standard":
                currentDirectory = new File(getExternalFilesDir("standard"), "tessdata");
                break;
            default:
                currentDirectory = new File(getExternalFilesDir("fast"), "tessdata");
        }
        return !new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, language.getCode())).exists();
    }

    // Result handling after image crop
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri resultUri = CropImage.getActivityResult(data).getUri();
            convertImageToText(resultUri);
        }
    }
}

