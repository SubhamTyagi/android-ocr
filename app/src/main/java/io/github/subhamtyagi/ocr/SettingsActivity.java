package io.github.subhamtyagi.ocr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
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

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener{
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SwitchPreference enableMultipleLang = findPreference(getString(R.string.key_enable_multiple_lang));
            ListPreference listPreference = findPreference(getString(R.string.key_language_for_tesseract));
            MultiSelectListPreference multiSelectListPreference = findPreference(getString(R.string.key_language_for_tesseract_multi));
            handleMultiLanguageChange(enableMultipleLang.isChecked(), listPreference, multiSelectListPreference);

            ListPreference ocrOemModePreference = findPreference(getString(R.string.key_engine_mode));
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            String tessTrainingDataSourceValue = sharedPreferences.getString(getString(R.string.key_tess_training_data_source), "");
            handlePreferenceTessTrainingDataSourceChange(tessTrainingDataSourceValue, ocrOemModePreference);
            ListPreference tessTrainingDataSourcePreference = findPreference(getString(R.string.key_tess_training_data_source));
            String keyEngineModeValue = sharedPreferences.getString(getString(R.string.key_engine_mode), "");
            handlePreferenceOcrOemModeChange(keyEngineModeValue, tessTrainingDataSourcePreference);

            findPreference(getString(R.string.key_language)).setOnPreferenceChangeListener(this);
            findPreference(getString(R.string.key_enable_multiple_lang)).setOnPreferenceChangeListener(this);
            findPreference(getString(R.string.key_tess_training_data_source)).setOnPreferenceChangeListener(this);
            findPreference(getString(R.string.key_engine_mode)).setOnPreferenceChangeListener(this);

            findPreference(getString(R.string.key_enable_multiple_lang)).setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {

            if (preference.getKey().equals(getString(R.string.key_language))) {
                Context context = requireContext();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                String previousValue = sharedPreferences.getString(getString(R.string.key_language_for_tesseract),"");
                handlePreferenceLanguageChange(context, previousValue, newValue.toString());
            } else if(preference.getKey().equals(getString(R.string.key_enable_multiple_lang))){
                ListPreference listPreference = findPreference(getString(R.string.key_language_for_tesseract));
                MultiSelectListPreference multiSelectListPreference = findPreference(getString(R.string.key_language_for_tesseract_multi));
                handleMultiLanguageChange((boolean)newValue, listPreference, multiSelectListPreference);
            } else if(preference.getKey().equals(getString(R.string.key_tess_training_data_source))){
                ListPreference ocrOemModePreference = findPreference(getString(R.string.key_engine_mode));
                handlePreferenceTessTrainingDataSourceChange((String) newValue, ocrOemModePreference);
            } else if(preference.getKey().equals(getString(R.string.key_engine_mode))){
                ListPreference tessTrainingDataSourcePreference = findPreference(getString(R.string.key_tess_training_data_source));
                handlePreferenceOcrOemModeChange((String)newValue, tessTrainingDataSourcePreference);
            }

            return true;
        }


        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if(preference.getKey().equals(getString(R.string.key_enable_multiple_lang))){
                SwitchPreference enableMultipleLang = (SwitchPreference) preference;
                ListPreference listPreference = findPreference(getString(R.string.key_language_for_tesseract));
                MultiSelectListPreference multiSelectListPreference = findPreference(getString(R.string.key_language_for_tesseract_multi));
                handleMultiLanguageClick(enableMultipleLang.isChecked(), listPreference, multiSelectListPreference);
            }
            return true;
        }
    }

    public static class AdvanceSettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.advance_preference, rootKey);
        }


    }

    private static void handlePreferenceLanguageChange(Context context,String previousValue, String newValue){
        if(!previousValue.equals(newValue))
            Toast.makeText(context, context.getString(R.string.the_app_needs_to_be_restarted), Toast.LENGTH_SHORT).show();
    }

    /**
     *  When using the traineddata files from the tessdata_best and tessdata_fast repositories,
     *  only the new LSTM-based OCR engine (–oem 1) is supported
     * @see <a href="https://tesseract-ocr.github.io/tessdoc/Data-Files.html">Traineddata Files for Version 4.00 + </a>
     */
    private static void handlePreferenceTessTrainingDataSourceChange(String newValue, ListPreference ocrOemModePreference) {
        if ("fast".equals(newValue) || "best".equals(newValue)) {
            ocrOemModePreference.setEntries(R.array.key_lstm_ocr_oem_mode);
            ocrOemModePreference.setEntryValues(R.array.value_lstm_ocr_oem_mode);
        } else {
            ocrOemModePreference.setEntries(R.array.key_ocr_oem_mode);
            ocrOemModePreference.setEntryValues(R.array.value_ocr_oem_mode);
        }
    }

    /**
     *  Tesseract’s oem modes ‘0’ and ‘2’ won’t work with tessdata_best and tessdata_fast repositories
     * @see <a href="https://tesseract-ocr.github.io/tessdoc/Data-Files.html">Traineddata Files for Version 4.00 + </a>
     */
    private static void handlePreferenceOcrOemModeChange(String newValue, ListPreference tessTrainingDataSourcePreference) {
        if("0".equals(newValue) || "2".equals(newValue)){
            tessTrainingDataSourcePreference.setEntries(R.array.ocr_training_data_legacy_entries);
            tessTrainingDataSourcePreference.setEntryValues(R.array.key_ocr_training_data_legacy_entries_values);
        } else {
            tessTrainingDataSourcePreference.setEntries(R.array.ocr_training_data_entries);
            tessTrainingDataSourcePreference.setEntryValues(R.array.key_ocr_training_data_entries_values);
        }
    }
    private static void handleMultiLanguageChange(boolean newValue, ListPreference listPreference, MultiSelectListPreference multiSelectListPreference){
        multiSelectListPreference.setVisible(newValue);
        listPreference.setVisible(!newValue);
    }
    private static void handleMultiLanguageClick(boolean checked, ListPreference listPreference, MultiSelectListPreference multiSelectListPreference) {
        if (checked) {
            multiSelectListPreference.setVisible(true);
            listPreference.setVisible(false);
        } else {
            multiSelectListPreference.setVisible(false);
            listPreference.setVisible(true);
        }
    }

}