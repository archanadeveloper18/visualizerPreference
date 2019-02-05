package android.example.com.visualizer_preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

//Implement OnPreferenceChangeListener
public class SettingsFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener,Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle ,String s){
        addPreferencesFromResource(R.xml.pref_visualizer);

        //Get the preference screen, get the number of preferences and iterate through
        // all of the preferences if it is not a checkbox preference, call the setSummary method
        // passing in a preference and the value of the preference
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count =  prefScreen.getPreferenceCount();

        for(int i = 0; i < count; i++){
            Preference p = prefScreen.getPreference(i);

            if(!(p instanceof CheckBoxPreference)){
                String value = sharedPreferences.getString(p.getKey(),"");
                setPreferencesSummary(p, value);
            }
        }
        //Add the OnPreferenceChangeListener specifically to the EditTextPreference
        Preference preference = findPreference(getString(R.string.pref_size_key));
        preference.setOnPreferenceChangeListener(this);
    }

    //Override onSharedPreferenceChanged and, if it is not a checkbox preference,
    // call setPreferenceSummary on the changed preference
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key){
        Preference preference = findPreference(key);
        if(null != preference){

            if(!(preference instanceof CheckBoxPreference)){
                String value = sharedPreferences.getString(preference.getKey(),"");
                setPreferencesSummary(preference, value);
            }
        }
    }

    //Create a setPreferenceSummary which takes a Preference and String value as parameters.
    // This method should check if the preference is a ListPreference and, if so, find the label
    // associated with the value. You can do this by using the findIndexOfValue and getEntries methods
    // of Preference.
    private void setPreferencesSummary(Preference preference, String value){
        //Don't forget to add code here to properly set the summary for an EditTextPreference
        if(preference instanceof ListPreference){
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(value);
            if(prefIndex >= 0){
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }else if(preference instanceof EditTextPreference){
            preference.setSummary(value);
        }
    }

    //Override onPreferenceChange. This method should try to convert the new preference value
    // to a float; if it cannot, show a helpful error message and return false. If it can be converted
    // to a float check that that float is between 0 (exclusive) and 3 (inclusive). If it isn't, show
    // an error message and return false. If it is a valid number, return true.

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue){
        // In this context, we're using the onPreferenceChange listener for checking whether the
        // size setting was set to a valid value.
        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);

        // Double check that the preference is the size preference
        String sizeKey = getString(R.string.pref_size_key);
        if(preference.getKey().equals(sizeKey)){
            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                if(size > 3 || size <= 0){
                    error.show();
                    return false;
                }
            }catch (NumberFormatException nfe){
                // If whatever the user entered can't be parsed to a number, show an error
                error.show();
                return false;
            }
        }
        return true;

    }


    //Register and unregister the OnSharedPreferenceChange listener (this class) in
    // onCreate and onDestroy respectively.
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
