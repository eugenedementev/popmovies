package com.eugeneexample.popularmovies.popularmovies;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainDiscoveryActivity extends AppCompatActivity {
    //TODO fill array of films from the API
    //TODO create custom adapter for the film object
    //TODO set data in the grid
    //TODO set onClickListener
    //TODO implement detail activity
    //TODO implement settings
    //TODO upgrade datafetching using settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_discovery);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container,new MainDiscoveryFragment())
                    .commit();
        }
    }

}