package android.example.com.visualizerpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

public class PrefFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener,Preference.OnPreferenceChangeListener{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref);

        SharedPreferences sharedPreferences=getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen=getPreferenceScreen();

        Preference pp=findPreference(getString(R.string.scale_key));
        pp.setOnPreferenceChangeListener(this);

        int count=preferenceScreen.getPreferenceCount();
        for(int i=0;i<count;i++){
            android.support.v7.preference.Preference p=preferenceScreen.getPreference(i);

            if(!(p instanceof CheckBoxPreference)){
                String key=sharedPreferences.getString(p.getKey(),"");
                getSummary(p,key);
            }


        }



    }

    private void getSummary(Preference p, String key) {

        if(p instanceof ListPreference) {
            ListPreference list = (ListPreference) p;
            int index = list.findIndexOfValue(key);
            CharSequence x = list.getEntries()[index];
            list.setSummary(x);
        }else if(p instanceof android.support.v7.preference.EditTextPreference){
            p.setSummary(key);

        }
        }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference=findPreference(s);
        if(!(preference instanceof  CheckBoxPreference)){
            String key=sharedPreferences.getString(preference.getKey(),"");
            getSummary(preference,key);
        }


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key=preference.getKey();
        Toast error= Toast .makeText(getContext(),"error",Toast.LENGTH_LONG);
        if(key.equals(getString(R.string.scale_key))){
            String val=(String)newValue;
            Float f=Float.parseFloat(val);

            if(f>3||f<0){
                error.show();
            }
            else  return true;
        }

        return true;

    }
}

