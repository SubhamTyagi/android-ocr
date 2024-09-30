package io.github.subhamtyagi.ocr;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.subhamtyagi.ocr.spinner.ArrayAdapterWithContainsFilter;
import io.github.subhamtyagi.ocr.utils.Language;
import io.github.subhamtyagi.ocr.utils.Utils;
import kotlin.Triple;

public class ShareFragment extends BottomSheetDialogFragment {

    private static final String LANGUAGE = "language";


    private Bundle bundle;
    static ArrayList<String> items;
    OnSpinnerItemClick onSpinnerItemClick;

    boolean showKeyboard = false;
    boolean useContainsFilter = false;

    int pos;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View vw = inflater.inflate(R.layout.dialog_layout, container, false);


        bundle = getArguments();

        assert bundle != null;

        // BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(vw.findViewById(R.id.mainView));
        // bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        //  bottomSheetBehavior.setHideable(false);

        TextView title = vw.findViewById(R.id.spinerTitle);
        ImageView searchIcon = vw.findViewById(R.id.searchIcon);
        title.setText(R.string.select_search_language);

        final ListView listView = vw.findViewById(R.id.list);

        //        ColorDrawable sage = new ColorDrawable(itemDividerColor);
        //        listView.setDivider(sage);
        //        listView.setDividerHeight(1);

        final EditText searchBox = vw.findViewById(R.id.searchBox);
        if (isShowKeyboard()) {
            showKeyboard(searchBox);
        }

        final ArrayAdapterWithContainsFilter<String> adapter =
                new ArrayAdapterWithContainsFilter<String>(
                        getActivity(), R.layout.items_view, items) {
                    @NonNull
                    @Override
                    public View getView(
                            int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(R.id.text1);
                        // text1.setTextColor(itemColor);
                        return view;
                    }
                };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(
                (adapterView, view, i, l) -> {
                    TextView t = view.findViewById(R.id.text1);
                    for (int j = 0; j < items.size(); j++) {
                        if (t.getText().toString().equalsIgnoreCase(items.get(j))) {
                            pos = j;
                        }
                    }
                    //onSpinnerItemClick.onClick(t.getText().toString(), pos);
                });

        searchBox.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (isUseContainsFilter()) {
                            adapter.getContainsFilter(searchBox.getText().toString());
                        } else {
                            adapter.getFilter().filter(searchBox.getText().toString());
                        }
                    }

                });

        // startOCRFromShareMenu(imageUri, Collections.singleton(new Language(this, item)));
        RadioButton radioButton1 = vw.findViewById(R.id.rb_language1);
        RadioButton radioButton2 = vw.findViewById(R.id.rb_language2);
        RadioButton radioButton3 = vw.findViewById(R.id.rb_language3);
        Triple<Set<Language>, Set<Language>, Set<Language>> languages =
                Utils.getLast3UsedLanguage(getContext());

        radioButton1.setText(
                languages.getFirst().stream()
                        .map(Language::getName)
                        .collect(Collectors.joining(", ")));
        radioButton2.setText(
                languages.getSecond().stream()
                        .map(Language::getName)
                        .collect(Collectors.joining(", ")));
        radioButton3.setText(
                languages.getThird().stream()
                        .map(Language::getName)
                        .collect(Collectors.joining(", ")));

        // radioButton1.setOnClickListener(view1 -> startOCRFromShareMenu(imageUri,
        // languages.getFirst()));
        //  radioButton2.setOnClickListener(view1 -> startOCRFromShareMenu(imageUri,
        // languages.getSecond()));
        //  radioButton3.setOnClickListener(view1 -> startOCRFromShareMenu(imageUri,
        // languages.getThird()));

        return vw;
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

    public static ShareFragment newInstance(Uri imageUri, ArrayList<String> languagesName) {
        ShareFragment fragment = new ShareFragment();
        items = languagesName;
        Bundle bundle = new Bundle();
        bundle.putString(LANGUAGE, "string");
        fragment.setArguments(bundle);
        return fragment;
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputManager =
                    (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            //  inputManager.hideSoftInputFromWindow(
            //getContext().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignore) {
        }
    }

    private void showKeyboard(final EditText ettext) {
        ettext.requestFocus();
        ettext.postDelayed(
                () -> {
                    InputMethodManager keyboard =
                            (InputMethodManager)
                                    getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(ettext, 0);
                },
                200);
    }

    private boolean isShowKeyboard() {
        return showKeyboard;
    }

    public void setShowKeyboard(boolean showKeyboard) {
        this.showKeyboard = showKeyboard;
    }

    private boolean isUseContainsFilter() {
        return useContainsFilter;
    }

    public void setUseContainsFilter(boolean useContainsFilter) {
        this.useContainsFilter = useContainsFilter;
    }

    public interface OnSpinnerItemClick {
        public void onClick(String item, int position);
    }
}
