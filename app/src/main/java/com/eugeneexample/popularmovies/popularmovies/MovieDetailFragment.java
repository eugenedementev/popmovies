package com.eugeneexample.popularmovies.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
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

public class MovieDetailFragment extends Fragment {

    private ArrayAdapter<String> mTrailersAdapter;

    private class FetchTrailersTask extends AsyncTask<Movie, Void, JSONArray> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        @Override
        protected JSONArray doInBackground(Movie... params) {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;
            InputStream inputStream = null;
            Movie movie = params[0];
            try {
                final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String API_KEY = "api_key";
                final String VIDEOS_ENCODED_PATH = "videos";

                Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon().
                        appendEncodedPath(String.valueOf(movie.getIdFromAPI())).
                        appendEncodedPath(VIDEOS_ENCODED_PATH).
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
                String jsonString = stringBuffer.toString();
                return getTrailersJSONArray(jsonString);
            } catch (IOException e) {
                Log.e(LOG_TAG,"IOException in fitching movie details",e);
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
//            return null;
        }

        @Override
        protected void onPostExecute(JSONArray trailersArray) {
            if (trailersArray!= null){
                //https://www.youtube.com/watch
                final String JSON_NAME = "name";
                final String JSON_KEY = "key";
                final String YOUTUBE_URL = "https://www.youtube.com/";
                final String WATCH_PATH = "watch";
                final String VIDEO_URL_PARAM = "v";

                for (int i=0;i<trailersArray.length();i++){
                    try {
                        final JSONObject trailer = trailersArray.getJSONObject(i);
                        View trailerView = getActivity().getLayoutInflater().inflate(R.layout.list_item_trailer,null,false);
                        TextView textView = (TextView) trailerView.findViewById(R.id.list_item_trailer_textview);
                        textView.setText(trailer.getString(JSON_NAME));
                        //TODO implement listener for redirecting
                    trailerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Uri builtUri = Uri.parse(YOUTUBE_URL).buildUpon().
                                        appendEncodedPath(WATCH_PATH).
                                        appendQueryParameter(VIDEO_URL_PARAM,trailer.getString(JSON_KEY)).
                                        build();
                                Intent openTrailerIntent = new Intent(Intent.ACTION_VIEW,builtUri);
                                startActivity(openTrailerIntent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                        LinearLayout linearLayoutInScrollView = (LinearLayout) getView().findViewById(R.id.detail_linear_layout);
                        linearLayoutInScrollView.addView(trailerView);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                Toast.makeText(getActivity(),"Trouble in onPostExecute. Inputparameter is null", Toast.LENGTH_SHORT);
            }
        }

        protected JSONArray getTrailersJSONArray(String jsonString){
            if (jsonString == null)
                return null;
            final String JSON_RESULT = "results";
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                return jsonObject.getJSONArray(JSON_RESULT);
            } catch (JSONException e) {
                return null;
            }
        }

//        protected ArrayList<String> getTrailerNamesFromJSON(String jsonString){
//            if (jsonString == null)
//                return null;
//            try {
//                final String JSON_RESULT = "results";
//                final String JSON_NAME = "name";
//
//                ArrayList<String> resultArray = new ArrayList<String>();
//
//                JSONObject jsonObject = new JSONObject(jsonString);
//                JSONArray trailersJsonArray = jsonObject.getJSONArray(JSON_RESULT);
//                for (int i = 0; i < trailersJsonArray.length();i++){
//                    resultArray.add(trailersJsonArray.getJSONObject(i).getString(JSON_NAME));
//                }
//                return  resultArray;
//            } catch (JSONException e) {
//                Log.e(LOG_TAG,e.getMessage());
//                return null;
//            }
//        }
    }

    public MovieDetailFragment() {
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
        View resultView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent incomingIntent = getActivity().getIntent();
        Movie movie = (Movie) incomingIntent.getSerializableExtra(MainDiscoveryFragment.ACTION_MOVIE);

        ((TextView)resultView.findViewById(R.id.detail_movie_title)).setText(movie.getTitle());
        ImageLoader.loadPosterFromTMDB(getContext(),movie.getPosterPath(),ImageLoader.QUALITY_W500,(ImageView) resultView.findViewById(R.id.detail_movie_poster));
        ((TextView)resultView.findViewById(R.id.detail_overview)).setText(movie.getOverview());
        ((TextView)resultView.findViewById(R.id.detail_release_date)).setText(movie.getRelease_date().substring(0,4));
        ((RatingBar)resultView.findViewById(R.id.detail_ratingBar)).setRating((float) movie.getVoteAverage());

//        mTrailersAdapter = new ArrayAdapter<String>(getActivity(),R.layout.list_item_trailer,R.id.list_item_trailers_textview);
//        ListView listViewTrailers = (ListView)resultView.findViewById(R.id.detail_trailer_list);
//        listViewTrailers.setAdapter(mTrailersAdapter);

        FetchTrailersTask getTrailers = new FetchTrailersTask();
        getTrailers.execute(movie);

        return resultView;
    }



}
