package com.eugeneexample.popularmovies.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Eugene on 21.06.2016.
 */
public class MoviesAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Movie> mMovies;
    private LayoutInflater inflater;
    private final String LOG_TAG = MainDiscoveryFragment.class.getSimpleName();

    public MoviesAdapter(Context context){
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (mMovies == null){
            mMovies = new ArrayList<Movie>();
        }
    }

    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Movie getItem(int position) {
        try {
            return mMovies.get(position);
        }catch (RuntimeException exception){
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView = convertView;
        if (resultView == null){
            resultView = inflater.inflate(R.layout.grid_item_film,parent,false);
        }else{
            resultView = convertView;
        }
        ImageView imageView = (ImageView)resultView.findViewById(R.id.grid_item_film_image);

        Movie movie = mMovies.get(position);

        final String TMDB_GET_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
        final String API_KEY = "api_key";

        Uri builtUri = Uri.parse(TMDB_GET_IMAGE_BASE_URL).buildUpon().
                appendEncodedPath(movie.getPosterPath()).
                appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY).
                build();

        Picasso.with(mContext)
                .load(builtUri.toString())
                .into(imageView);
        return resultView;
    }

    @Override
    public boolean isEmpty() {
        return mMovies.isEmpty();
    }

    public void clear(){
        mMovies.clear();
        notifyDataSetChanged();
    };

    public void addAll(ArrayList<Movie> movieArrayList){
        mMovies.addAll(movieArrayList);
        notifyDataSetChanged();
    }

}
