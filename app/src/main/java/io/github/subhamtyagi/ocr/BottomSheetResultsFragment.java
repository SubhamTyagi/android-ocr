package io.github.subhamtyagi.ocr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetResultsFragment extends BottomSheetDialogFragment {

    private static final String ARGUMENT_TEXT = "arg_text";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_results, container, false);
        Bundle arguments = getArguments();
        
        if (arguments == null) {
            dismiss();
            return view; 
        }

        String resultantTextString = arguments.getString(ARGUMENT_TEXT, "");
        TextView resultantText = view.findViewById(R.id.resultant_text);
        ImageButton btnCopy = view.findViewById(R.id.btn_copy);
        ImageButton btnShare = view.findViewById(R.id.btn_share);

        setupButtons(resultantTextString, btnCopy, btnShare, resultantText);
        
        return view;
    }

    private void setupButtons(String text, ImageButton btnCopy, ImageButton btnShare, TextView resultantText) {
        if (TextUtils.isEmpty(text.trim())) {
            setButtonState(btnCopy, false);
            setButtonState(btnShare, false);
            resultantText.setText(R.string.no_results);
        } else {
            resultantText.setText(text);
            btnCopy.setOnClickListener(v -> copyToClipboard(text));
            btnShare.setOnClickListener(v -> shareText(text));
        }
    }

    private void setButtonState(ImageButton button, boolean enabled) {
        button.setEnabled(enabled);
        button.setAlpha(enabled ? 1f : 0.3f);
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboardManager = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("nonsense_data", text);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    private void shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, null));
        dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public static BottomSheetResultsFragment newInstance(String text) {
        BottomSheetResultsFragment fragment = new BottomSheetResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }
}
