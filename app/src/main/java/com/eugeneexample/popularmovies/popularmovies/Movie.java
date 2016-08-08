package com.eugeneexample.popularmovies.popularmovies;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by Eugene on 21.06.2016.
 */
public class Movie implements Serializable{

    private int idFromAPI;
    private String posterPath;
    private String title;
    private String release_date;
    private String overview;
    private double voteAverage;

    public Movie() {
    }

    public Movie(int idFromAPI, String title, String release_date, String overview, String posterPath, double voteAverage) {
        this.idFromAPI = idFromAPI;
        this.posterPath = posterPath;
        this.title = title;
        this.release_date = release_date;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    public int getIdFromAPI() {
        return idFromAPI;
    }

    public void setIdFromAPI(int idFromAPI) {
        this.idFromAPI = idFromAPI;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }
}
