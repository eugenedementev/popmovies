package com.eugeneexample.popularmovies.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainDiscoveryFragment extends Fragment {

    private ConnectivityManager mConnectivityManager;
    private MoviesAdapter mMoviesAdapter;

    public static final String ACTION_MOVIE = "MOVIE_CLASS";

    public MainDiscoveryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_discovery, container, false);
        setHasOptionsMenu(true);
        mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        mMoviesAdapter = new MoviesAdapter(getActivity());

        GridView gridView = (GridView)rootView.findViewById(R.id.movies_grid);
        gridView.setAdapter(mMoviesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startMovieDetailActivity = new Intent(getActivity(),MovieDetailActivity.class);
                startMovieDetailActivity.putExtra(ACTION_MOVIE,mMoviesAdapter.getItem(position));
                startActivity(startMovieDetailActivity);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_discovery_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                updateMoviesGrid();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(getContext(),SettingsActivity.class);
                startActivity(intent);
                break;
        }

        if (item.getItemId() == R.id.action_refresh){
            updateMoviesGrid();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMoviesGrid();
    }

    protected void updateMoviesGrid(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrderValue = sharedPreferences.getString(getString(R.string.pref_sort_order_key),getString(R.string.pref_sort_order_enty_values_most_popular));
        FetchMoviesTask getMovies = new FetchMoviesTask();
        getMovies.execute(sortOrderValue);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()){
                return null;
            }
            String moviesJsonString = getMoviesJSONStringFromAPI(params[0]);
            if (moviesJsonString == null){
                return null;
            }else{
                return getMovieArrayFromJSON(moviesJsonString);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> moviesArrayList) {
            if (moviesArrayList != null){
                mMoviesAdapter.clear();
                mMoviesAdapter.addAll(moviesArrayList);
            }else{
                Toast.makeText(getActivity(),getString(R.string.network_no_received_data),Toast.LENGTH_SHORT).show();
            }
        }

        protected String getMoviesJSONStringFromAPI(String sortOrderPreference){
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            InputStream inputStream = null;
            try {
                final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";

                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon().
                        appendEncodedPath(sortOrderPreference).
                        appendQueryParameter(API_KEY,BuildConfig.THE_MOVIE_DB_API_KEY).
                        build();

                URL url = new URL(builtUri.toString());

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                inputStream = connection.getInputStream();
                if (inputStream == null)
                    return null;
                StringBuffer stringBuffer = new StringBuffer();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) !=null){
                    stringBuffer.append(line + "\n");
                }
                if (stringBuffer.length() == 0)
                    return null;
                return stringBuffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG,"IOException in getMoviesJSONStringFromAPI()",e);
                return null;
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try{
                    if (inputStream != null)
                        inputStream.close();
                    if (bufferedReader != null)
                        bufferedReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,"Error closing stream",e);
                }
            }
        }

        protected ArrayList<Movie> getMovieArrayFromJSON(String jsonStrin){
            if (jsonStrin == null){
                return null;
            }

            try {
                final String MOVIE_JSON_RESULTS = "results";
                final String MOVIE_PARAM_ID = "id";
                final String MOVIE_PARAM_TITLE = "title";
                final String MOVIE_PARAM_RELEASE_DATE = "release_date";
                final String MOVIE_PARAM_OVERVIEW = "overview";
                final String MOVIE_PARAM_POSTER_PATH = "poster_path";

                ArrayList<Movie> resultArrayList = new ArrayList<Movie>();
                JSONObject jsonObjectFromSting = new JSONObject(jsonStrin);
                JSONArray jsonMoviesArray = jsonObjectFromSting.getJSONArray(MOVIE_JSON_RESULTS);
                for (int position = 0;position < jsonMoviesArray.length();position++){
                    JSONObject jsonMovie = jsonMoviesArray.getJSONObject(position);
                    resultArrayList.add(new Movie(
                            jsonMovie.getInt(MOVIE_PARAM_ID),
                            jsonMovie.getString(MOVIE_PARAM_TITLE),
                            jsonMovie.getString(MOVIE_PARAM_RELEASE_DATE),
                            jsonMovie.getString(MOVIE_PARAM_OVERVIEW),
                            jsonMovie.getString(MOVIE_PARAM_POSTER_PATH)
                    ));
                }
                return resultArrayList;
            } catch (JSONException e) {
                Log.e(getTag(),"getMovieArrayFromJSON(): ",e);
                return null;
            }
        }
    }



}
