package io.github.subhamtyagi.ocr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

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

        TextView resultantText = v.findViewById(R.id.resultant_text);
        Toolbar toolbar = v.findViewById(R.id.toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                if(id == R.id.action_copy) {

                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("nonsense_data", bundle.getString(ARGUMENT_TEXT));
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                    dismiss();

                } else if(id == R.id.action_share) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, bundle.getString(ARGUMENT_TEXT));

                    startActivity(Intent.createChooser(intent, null));
                    dismiss();

                }

                return true;

            }
        });

        if(bundle.getString(ARGUMENT_TEXT).trim().isEmpty()) {

            toolbar.getMenu()
                    .setGroupVisible(0, false);

            resultantText.setText(R.string.no_results);

        } else {

            resultantText.setText(bundle.getString(ARGUMENT_TEXT));
        }

        return v;

    }

}