package com.digutsoft.note;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;

public class DMSettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}