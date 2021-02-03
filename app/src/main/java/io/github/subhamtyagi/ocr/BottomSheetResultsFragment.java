package io.github.subhamtyagi.ocr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetResultsFragment extends BottomSheetDialogFragment {

    private static final String ARGUMENT_TEXT = "arg_text";

    public static BottomSheetResultsFragment newInstance(String text) {

        BottomSheetResultsFragment fragment = new BottomSheetResultsFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARGUMENT_TEXT, text);
        fragment.setArguments(bundle);

        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_dialog_results, container, false);

        final Context context = getContext();
        final Bundle bundle = getArguments();

        assert bundle != null;
        assert context != null;

        ImageButton btnCopy = v.findViewById(R.id.btn_copy);
        ImageButton btnShare = v.findViewById(R.id.btn_share);

        TextView resultantText = v.findViewById(R.id.resultant_text);

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("nonsense_data", bundle.getString(ARGUMENT_TEXT));
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                dismiss();

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, bundle.getString(ARGUMENT_TEXT));

                startActivity(Intent.createChooser(intent, null));
                dismiss();

            }
        });

        if(bundle.getString(ARGUMENT_TEXT).trim().isEmpty()) {

            btnCopy.setVisibility(View.GONE);
            btnShare.setVisibility(View.GONE);

            resultantText.setText(R.string.no_results);

        } else {

            resultantText.setText(bundle.getString(ARGUMENT_TEXT));
        }

        return v;

    }

}