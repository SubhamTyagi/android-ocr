
package io.github.subhamtyagi.ocr.downloader;

//include all imports

public class LanguageActivity extends AppCompatActivity implements LanguageAdapter.OnLanguageItemClickListener {

    private RecyclerView recyclerView;
    private LanguageAdapter adapter;
    private List<LanguageItem> languageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        // Initialize the RecyclerView and data
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Example data
        languageList = new ArrayList<>();
        languageList.add(new LanguageItem("English", false, false));
        languageList.add(new LanguageItem("Spanish", true, false));
        // Add more items...

        adapter = new LanguageAdapter(languageList, this);
        recyclerView.setAdapter(adapter);
    }
     @Override
public void onDownloadClicked(int position) {
    // Start download and show progress
    LanguageItem item = languageList.get(position);
    item.setDownloading(true);
    adapter.notifyItemChanged(position);

    // download progress 
    //TODO: download codes goes here
    
       runOnUiThread(() -> {
                item.setDownloadProgress(finalProgress);
                adapter.notifyItemChanged(position);
            });
        }

        // After download is complete
        runOnUiThread(() -> {
            item.setDownloading(false);
            item.setDownloaded(true);
            adapter.notifyItemChanged(position);
        });
    
}

@Override
public void onDeleteClicked(int position) {
    // Handle delete logic
    LanguageItem item = languageList.get(position);
    item.setDownloaded(false);
    adapter.notifyItemChanged(position);
}

    

    @Override
    public void onLanguageSelected(int position, boolean isSelected) {
        // Handle selection logic here
        LanguageItem item = languageList.get(position);
        item.setSelected(isSelected);
    }
    
    private class DownloadTraining implements Runnable {
        private final String dataType;
        private final String lang;
        private String size;

        public DownloadTraining(String dataType, String lang) {
            this.dataType = dataType;
            this.lang = lang;
        }

        @Override
        public void run() {
            handler.post(() -> {
                mProgressTitle.setText(getString(R.string.downloading));
                mProgressMessage.setText(getString(R.string.downloading_language));
                mDownloadLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            });

            boolean success = downloadTrainingData(dataType, lang);

            handler.post(() -> {
                mDownloadLayout.setVisibility(View.GONE);
                if (success) {
                    initializeOCR(Utils.getTrainingDataLanguages(MainActivity.this));
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
                    mProgressMessage.setText("0"+ getString(R.string.percentage_downloaded)+ size);
                    mProgressBar.setProgress(0);               // Reset progress bar to 0
                });

                try (InputStream input = new BufferedInputStream(conn.getInputStream());
                     OutputStream output = new FileOutputStream(new File(currentDirectory, String.format(Constants.LANGUAGE_CODE, lang)))) {

                    byte[] data = new byte[6 * 1024];
                    int downloaded = 0;
                    int count;

                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                        downloaded += count;
                        int percentage = (downloaded * 100) / totalContentSize;
                        handler.post(() -> {
                            mProgressBar.setProgress(percentage);
                            mProgressMessage.setText(percentage+getString(R.string.percentage_downloaded)+size+".");
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
                    return lang.equals("akk") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST :
                            lang.equals("eqo") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU :
                                    String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang);
                case "standard":
                    return lang.equals("akk") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_STANDARD :
                            lang.equals("eqo") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU :
                                    String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang);
                default: // Assuming "fast" is the default
                    return lang.equals("akk") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_FAST :
                            lang.equals("eqo") ? Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU :
                                    String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang);
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
