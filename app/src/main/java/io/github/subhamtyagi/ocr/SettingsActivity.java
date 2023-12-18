package io.github.subhamtyagi.ocr;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;


/**
 * Settings for App: I think all codes are self explanatory
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }*/
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreference enableMultipleLang = findPreference(getString(R.string.key_enable_multiple_lang));
            ListPreference listPreference = findPreference(getString(R.string.key_language_for_tesseract));
            ListPreference listPreferenceLanguage = findPreference(getString(R.string.key_language));
            String oldValue = listPreferenceLanguage.getValue();
            listPreferenceLanguage.setOnPreferenceChangeListener((preference, newValue) -> {
                if(!oldValue.equals(newValue)) showRestartAppDialog(requireContext());
                return true;
            });
            MultiSelectListPreference multiSelectListPreference = findPreference(getString(R.string.key_language_for_tesseract_multi));
            if (enableMultipleLang.isChecked()) {
                multiSelectListPreference.setVisible(true);
                listPreference.setVisible(false);
            } else {
                multiSelectListPreference.setVisible(false);
                listPreference.setVisible(true);
            }

            enableMultipleLang.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean b = (boolean) newValue;
                multiSelectListPreference.setVisible(b);
                listPreference.setVisible(!b);

                return true;
            });

            ListPreference tessTrainingDataPreference = findPreference(getString(R.string.key_tess_training_data_source));
            tessTrainingDataPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String selectedTrainingData = (String) newValue;
                updateOcrOemModeOptions(selectedTrainingData);
                return true;
            });

        }

        /**
         *  tessdata-best and tessdata-fast DO NOT support legacy OCR engine mode
         * @see <a href="https://tesseract-ocr.github.io/tessdoc/Data-Files.html">Traineddata Files for Version 4.00 + </a>
         * @param selectedTrainingData
         */
        private void updateOcrOemModeOptions(String selectedTrainingData) {
            ListPreference ocrOemModePreference = findPreference(getString(R.string.key_engine_mode));
            if ("fast".equals(selectedTrainingData) || "best".equals(selectedTrainingData)) {
                ocrOemModePreference.setEntries(R.array.key_lstm_ocr_oem_mode);
                ocrOemModePreference.setEntryValues(R.array.value_lstm_ocr_oem_mode);
            } else {
                ocrOemModePreference.setEntries(R.array.key_ocr_oem_mode);
                ocrOemModePreference.setEntryValues(R.array.value_ocr_oem_mode);
            }
        }

    }

    private static void showRestartAppDialog(Context context){
        Toast.makeText(context, context.getString(R.string.the_app_needs_to_be_restarted), Toast.LENGTH_SHORT).show();
    }

    public static class AdvanceSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.advance_preference, rootKey);
        }


    }


}