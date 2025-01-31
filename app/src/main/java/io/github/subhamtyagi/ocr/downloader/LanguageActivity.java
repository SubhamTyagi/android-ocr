
package io.github.subhamtyagi.ocr.downloader;

//include all imports

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.subhamtyagi.ocr.R;

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
        languageList = new ArrayList<>();
        // some random data
        addLanguageData();

        adapter = new LanguageAdapter(languageList, this);
        recyclerView.setAdapter(adapter);
    }

    private void addLanguageData() {
        String[] languagesName = getResources().getStringArray(R.array.ocr_engine_language);
        String[] languagesCode = getResources().getStringArray(R.array.key_ocr_engine_language_value);
        for (int i = 0; i < languagesCode.length; i++) {
            //check for downloaded and checked
            boolean isDownloaded=false;
            boolean isSelected=false;
            languageList.add(new LanguageItem(languagesName[i], languagesCode[i],isDownloaded,isSelected));
        }
    }


    @Override
    public void onDownloadClicked(int position) {
        LanguageItem item = languageList.get(position);
        String languageCode=item.getLanguageCode();
        //if language is already downloaded then return
        //else
        item.setDownloading(true);
        adapter.notifyItemChanged(position);
        //TODO: download codes goes here

        runOnUiThread(() -> {
            item.setDownloadProgress(50);
            adapter.notifyItemChanged(position);
        });
        item.setDownloaded(true);
    }

    @Override
    public void onDeleteClicked(int position) {
        LanguageItem item = languageList.get(position);
        String languageCode=item.getLanguageCode();
        //Delete language data
        item.setDownloaded(false);
        //also remove from selection
    }

    @Override
    public void onLanguageSelected(int position, boolean isSelected) {
        LanguageItem item = languageList.get(position);
        String languageCode=item.getLanguageCode();
        onDownloadClicked(position);
    }
}
