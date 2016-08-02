package com.eugeneexample.popularmovies.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MovieDetailFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailFragment extends Fragment {

    public MovieDetailFragment() {
        // Required empty public constructor
    }

//    public static MovieDetailFragment newInstance(String param1, String param2) {
//        MovieDetailFragment fragment = new MovieDetailFragment();
//        Bundle args = new Bundle();
////        args.putString(ARG_PARAM1, param1);
////        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

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
        ImageLoader.loadPosterFromTMDB(getContext(),movie.getPosterPath(),(ImageView) resultView.findViewById(R.id.detail_movie_poster));
        ((TextView)resultView.findViewById(R.id.detail_overview)).setText(movie.getOverview());
        ((TextView)resultView.findViewById(R.id.detail_release_date)).setText(movie.getRelease_date().substring(0,4));

        return resultView;
    }

}
