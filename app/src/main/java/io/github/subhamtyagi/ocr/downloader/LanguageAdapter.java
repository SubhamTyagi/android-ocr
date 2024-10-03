package io.github.subhamtyagi.ocr.downloader;


public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {

    private List<LanguageItem> languageList;
    private OnLanguageItemClickListener listener;

    public interface OnLanguageItemClickListener {
        void onDownloadClicked(int position);
        void onDeleteClicked(int position);
        void onLanguageSelected(int position, boolean isSelected);
    }

    public LanguageAdapter(List<LanguageItem> languageList, OnLanguageItemClickListener listener) {
        this.languageList = languageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
    LanguageItem currentItem = languageList.get(position);

    holder.languageNameTextView.setText(currentItem.getLanguageName());
    holder.selectLanguageCheckBox.setChecked(currentItem.isSelected());

    // Toggle visibility and color of icons based on download status
    if (currentItem.isDownloaded()) {
        holder.downloadImageView.setImageResource(R.drawable.ic_download_green);  // Change icon if downloaded
        holder.deleteImageView.setVisibility(View.VISIBLE);  // Show delete icon
    } else {
        holder.downloadImageView.setImageResource(R.drawable.ic_download);  // Regular download icon
        holder.deleteImageView.setVisibility(View.GONE);  // Hide delete icon
    }

    // Manage the ProgressBar visibility and progress
    if (currentItem.isDownloading()) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.progressBar.setProgress(currentItem.getDownloadProgress());
    } else {
        holder.progressBar.setVisibility(View.GONE);
    }

    // Handle click events for download and delete
    holder.downloadImageView.setOnClickListener(v -> listener.onDownloadClicked(position));
    holder.deleteImageView.setOnClickListener(v -> listener.onDeleteClicked(position));

    // Handle checkbox selection
    holder.selectLanguageCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->
        listener.onLanguageSelected(position, isChecked)
    );
}

public static class LanguageViewHolder extends RecyclerView.ViewHolder {
    public TextView languageNameTextView;
    public ImageView downloadImageView;
    public ImageView deleteImageView;
    public CheckBox selectLanguageCheckBox;
    public ProgressBar progressBar;  // New ProgressBar view

    public LanguageViewHolder(@NonNull View itemView) {
        super(itemView);
        languageNameTextView = itemView.findViewById(R.id.tv_language_name);
        downloadImageView = itemView.findViewById(R.id.iv_download_language);
        deleteImageView = itemView.findViewById(R.id.iv_delete_language);
        selectLanguageCheckBox = itemView.findViewById(R.id.cb_select_language);
        progressBar = itemView.findViewById(R.id.pb_download_progress);  // Initialize ProgressBar
    }
}


    @Override
    public int getItemCount() {
        return languageList.size();
    }

    
}
