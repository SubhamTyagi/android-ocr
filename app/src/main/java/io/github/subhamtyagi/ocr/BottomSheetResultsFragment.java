package io.github.subhamtyagi.ocr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetResultsFragment extends BottomSheetDialogFragment {

    private static final String ARGUMENT_TEXT = "arg_text";

    private Context context;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_dialog_results, container, false);

        context = getContext();
        bundle = getArguments();

        assert bundle != null;
        assert context != null;

        TextView resultantText = v.findViewById(R.id.resultant_text);
        ImageButton btnCopy = v.findViewById(R.id.btn_copy);
        ImageButton btnShare = v.findViewById(R.id.btn_share);

        btnCopy.setOnClickListener(v12 -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("nonsense_data", bundle.getString(ARGUMENT_TEXT));
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            dismiss();
        });

        btnShare.setOnClickListener(v1 -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, bundle.getString(ARGUMENT_TEXT));
            startActivity(Intent.createChooser(intent, null));
            dismiss();
        });

        if (bundle.getString(ARGUMENT_TEXT).trim().isEmpty()) {
            btnCopy.setEnabled(false);
            btnCopy.setAlpha(.3f);
            btnShare.setEnabled(false);
            btnShare.setAlpha(.3f);
            resultantText.setText(R.string.no_results);
        } else {
            resultantText.setText(bundle.getString(ARGUMENT_TEXT));
        }
        return v;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        cancel();
    }

    @Override
    public void dismiss() {
        cancel();
        super.dismiss();
    }

    private void cancel() {

    }

    public static BottomSheetResultsFragment newInstance(String text) {
        BottomSheetResultsFragment fragment = new BottomSheetResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_TEXT, text);
        fragment.setArguments(bundle);
        return fragment;
    }

}