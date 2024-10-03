package io.github.subhamtyagi.ocr.downloader;

public class LanguageItem {
    private String languageName;
    private boolean isDownloaded;
    private boolean isSelected;
    private boolean isDownloading;  // track downloading status
    private int downloadProgress;   // track download progress

    public LanguageItem(String languageName, boolean isDownloaded, boolean isSelected) {
        this.languageName = languageName;
        this.isDownloaded = isDownloaded;
        this.isSelected = isSelected;
        this.isDownloading = false;
        this.downloadProgress = 0;
    }

    public String getLanguageName() {
        return languageName;
    }

    public boolean isDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(boolean downloaded) {
        isDownloaded = downloaded;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }
}

