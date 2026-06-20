package com.example.final_cut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class DiscoveryFragment extends Fragment {

    private static final String TAG = "FinalCut_DiscoveryFrag";
    private static final String API_KEY = "f1f700f39affba891d1cfe113432cc50";
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String IMAGE_BASE = "https://image.tmdb.org/t/p/w500";

    private RadioGroup genreRadioGroup;
    private CheckBox checkNetflix, checkHulu, checkPrime;
    private ImageView moviePosterImage;
    private TextView movieTitle, movieOverview;

    private String lastMovieTitle = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        genreRadioGroup   = view.findViewById(R.id.genreRadioGroup);
        checkNetflix      = view.findViewById(R.id.checkNetflix);
        checkHulu         = view.findViewById(R.id.checkHulu);
        checkPrime        = view.findViewById(R.id.checkPrime);
        moviePosterImage  = view.findViewById(R.id.moviePosterImage);
        movieTitle        = view.findViewById(R.id.movieTitle);
        movieOverview     = view.findViewById(R.id.movieOverview);

        Button btnGenerateMovie = view.findViewById(R.id.btnGenerateMovie);
        Button btnShareText     = view.findViewById(R.id.btnShareText);
        Button btnShareEmail    = view.findViewById(R.id.btnShareEmail);

        btnGenerateMovie.setOnClickListener(v -> {
            saveUserPreferences();
            int genreId = getTmdbGenreId();
            new FetchMovieTask().execute(String.valueOf(genreId));
        });

        btnShareText.setOnClickListener(v -> shareViaText());
        btnShareEmail.setOnClickListener(v -> shareViaEmail());

        loadUserPreferences();
        return view;
    }

    private int getTmdbGenreId() {
        int checkedId = genreRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioAction)  return 28;
        if (checkedId == R.id.radioComedy)  return 35;
        return 878;
    }

    private String getSelectedGenre() {
        int checkedId = genreRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioAction)  return getString(R.string.genre_action);
        if (checkedId == R.id.radioComedy)  return getString(R.string.genre_comedy);
        return getString(R.string.genre_scifi);
    }

    private void saveUserPreferences() {
        if (getActivity() == null) return;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("FinalCutPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SAVED_GENRE_ID", genreRadioGroup.getCheckedRadioButtonId());
        editor.putBoolean("HAS_NETFLIX", checkNetflix.isChecked());
        editor.putBoolean("HAS_HULU",    checkHulu.isChecked());
        editor.putBoolean("HAS_PRIME",   checkPrime.isChecked());
        editor.apply();
        Log.d(TAG, "Preferences saved.");
    }

    private void loadUserPreferences() {
        if (getActivity() == null) return;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("FinalCutPrefs", Context.MODE_PRIVATE);
        genreRadioGroup.check(sharedPref.getInt("SAVED_GENRE_ID", R.id.radioSciFi));
        checkNetflix.setChecked(sharedPref.getBoolean("HAS_NETFLIX", true));
        checkHulu.setChecked(sharedPref.getBoolean("HAS_HULU",    false));
        checkPrime.setChecked(sharedPref.getBoolean("HAS_PRIME",  false));
        Log.d(TAG, "Preferences loaded.");
    }

    private class FetchMovieTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String genreId = params[0];
            try {
                int page = new Random().nextInt(5) + 1;
                String urlString = BASE_URL
                        + "?api_key=" + API_KEY
                        + "&with_genres=" + genreId
                        + "&sort_by=popularity.desc"
                        + "&page=" + page;

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();
                StringBuilder builder = new StringBuilder();
                int ch;
                while ((ch = stream.read()) != -1) {
                    builder.append((char) ch);
                }
                connection.disconnect();

                JSONObject response = new JSONObject(builder.toString());
                JSONArray results = response.getJSONArray("results");

                if (results.length() == 0) return null;

                int pick = new Random().nextInt(results.length());
                return results.getJSONObject(pick);

            } catch (Exception e) {
                Log.d(TAG, "FetchMovieTask error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject movie) {
            if (movie == null || getActivity() == null) return;

            try {
                String title    = movie.getString("title");
                String overview = movie.getString("overview");
                String poster   = movie.optString("poster_path", "");

                lastMovieTitle = title;
                movieTitle.setText(title);
                movieOverview.setText(overview);

                if (!poster.isEmpty()) {
                    new DownloadPosterTask().execute(IMAGE_BASE + poster);
                }

            } catch (Exception e) {
                Log.d(TAG, "onPostExecute error: " + e.getMessage());
            }
        }
    }

    private class DownloadPosterTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                connection.disconnect();
                return bitmap;
            } catch (Exception e) {
                Log.d(TAG, "DownloadPosterTask error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && moviePosterImage != null) {
                moviePosterImage.setImageBitmap(bitmap);
            }
        }
    }

    private void shareViaText() {
        String moviePartFormatted = lastMovieTitle.isEmpty() ? "" : " I'm thinking \"" + lastMovieTitle + "\".";
        String message = getString(R.string.share_text_message, getSelectedGenre(), moviePartFormatted);
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        } catch (Exception e) {
            Log.d(TAG, "No messaging app available.");
        }
    }

    private void shareViaEmail() {
        String moviePartFormatted = lastMovieTitle.isEmpty() ? "" : " I found a great one: \"" + lastMovieTitle + "\".";
        String subject = getString(R.string.share_email_subject);
        String body = getString(R.string.share_email_body, getSelectedGenre(), moviePartFormatted);
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(emailIntent);
        } catch (Exception e) {
            Log.d(TAG, "No email app available.");
        }
    }
}