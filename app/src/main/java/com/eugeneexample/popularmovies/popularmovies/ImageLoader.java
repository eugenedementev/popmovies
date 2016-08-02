package com.eugeneexample.popularmovies.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Eugene on 02.08.2016.
 */

public abstract class ImageLoader {
    private final static String TMDB_GET_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private final static String API_KEY = "api_key";

    public static void loadPosterFromTMDB(Context context, String posterPath, ImageView imageView){

        Uri builtUri = Uri.parse(TMDB_GET_IMAGE_BASE_URL).buildUpon().
                appendEncodedPath(posterPath).
                appendQueryParameter(API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY).
                build();

        Picasso.with(context)
                .load(builtUri.toString())
                .into(imageView);
    }
}
