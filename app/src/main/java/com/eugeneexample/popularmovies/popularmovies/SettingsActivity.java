package com.eugeneexample.popularmovies.popularmovies;


import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getString(R.string.title_activity_settings));
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.settings_container,new SettingsFragment())
                    .commit();
        }
    }
}
