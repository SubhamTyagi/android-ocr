package io.github.subhamtyagi.ocr.spinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import io.github.subhamtyagi.ocr.R;


/**
 * Created by Md Farhan Raja on 2/23/2017.
 */

public class SpinnerDialog {
    ArrayList<String> items;
    Activity context;
    String dTitle;
    OnSpinnerItemClick onSpinnerItemClick;
    AlertDialog alertDialog;
    int pos;
    int style;
    boolean showKeyboard = false;
    boolean useContainsFilter = false;
    int titleColor, searchIconColor, searchTextColor, itemColor, itemDividerColor;

    private View view;

    public SpinnerDialog(Activity activity, ArrayList<String> items, String dialogTitle) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
        initColor(context);
    }

    public SpinnerDialog(Activity activity, ArrayList<String> items, String dialogTitle, int style) {
        this.items = items;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.style = style;
        initColor(context);
    }

    private void initColor(Context context) {
        this.titleColor = context.getResources().getColor(R.color.colorBlack);
        this.searchIconColor = context.getResources().getColor(R.color.colorBlack);
        this.searchTextColor = context.getResources().getColor(R.color.colorBlack);
        this.itemColor = context.getResources().getColor(R.color.colorBlack);
                this.itemDividerColor = context.getResources().getColor(R.color.colorLightGray);
    }

    public void bindOnSpinnerListener(OnSpinnerItemClick onSpinnerItemClick1) {
        this.onSpinnerItemClick = onSpinnerItemClick1;
    }

    public void showSpinnerDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(context);
        view = context.getLayoutInflater().inflate(R.layout.dialog_layout, null);
        TextView title = view.findViewById(R.id.spinerTitle);
        ImageView searchIcon = view.findViewById(R.id.searchIcon);
       // rippleViewClose.setText(closeTitle);
        title.setText(dTitle);
        final ListView listView = view.findViewById(R.id.list);

        ColorDrawable sage = new ColorDrawable(itemDividerColor);
        listView.setDivider(sage);
        listView.setDividerHeight(1);

        final EditText searchBox = view.findViewById(R.id.searchBox);
        if (isShowKeyboard()) {
            showKeyboard(searchBox);
        }

        title.setTextColor(titleColor);
        searchBox.setTextColor(searchTextColor);

        searchIcon.setColorFilter(searchIconColor);

        final ArrayAdapterWithContainsFilter<String> adapter = new ArrayAdapterWithContainsFilter<String>(context, R.layout.items_view, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(R.id.text1);
                text1.setTextColor(itemColor);
                return view;
            }
        };
        listView.setAdapter(adapter);
        adb.setView(view);
        alertDialog = adb.create();
        alertDialog.getWindow().getAttributes().windowAnimations = style;//R.style.DialogAnimations_SmileWindow;

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            TextView t = view.findViewById(R.id.text1);
            for (int j = 0; j < items.size(); j++) {
                if (t.getText().toString().equalsIgnoreCase(items.get(j))) {
                    pos = j;
                }
            }
            onSpinnerItemClick.onClick(t.getText().toString(), pos);
            closeSpinnerDialog();
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    public void closeSpinnerDialog() {
        hideKeyboard();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception ignore) {
        }
    }

    private void showKeyboard(final EditText ettext) {
        ettext.requestFocus();
        ettext.postDelayed(() -> {
                    InputMethodManager keyboard = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    keyboard.showSoftInput(ettext, 0);
                }
                , 200);
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

    public View getView() {
        return view;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public void setSearchIconColor(int searchIconColor) {
        this.searchIconColor = searchIconColor;
    }

    public void setSearchTextColor(int searchTextColor) {
        this.searchTextColor = searchTextColor;
    }

    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
    }

    public void setItemDividerColor(int itemDividerColor) {
        this.itemDividerColor = itemDividerColor;
    }
}
