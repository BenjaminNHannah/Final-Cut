package com.example.final_cut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiscoveryFragment extends Fragment {

    private static final String TAG = "FinalCut_DiscoveryFrag";
    private static final String API_KEY = "f1f700f39affba891d1cfe113432cc50";
    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String IMAGE_BASE = "https://image.tmdb.org/t/p/w500";
    private static final String PROVIDERS_BASE = "https://api.themoviedb.org/3/movie/";

    private static final String[] GENRE_NAMES =
            {"Action", "Adventure", "Comedy", "Crime", "Drama", "Fantasy", "Horror", "Romance", "Sci-Fi", "Thriller"};
    private static final String[] GENRE_IDS =
            {"28", "12", "35", "80", "18", "14", "27", "10749", "878", "53"};

    private Spinner genreSpinner;
    private Chip chipNetflix, chipPrime, chipDisney, chipHulu, chipMax, chipApple, chipParamount, chipPeacock;
    private Chip[] serviceChips;
    private TextView btnGenerateMovie, btnShareText, btnShareEmail;
    private ImageView moviePosterImage;
    private TextView movieTitle, movieOverview, movieStreamingInfo;

    private String lastMovieTitle = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        genreSpinner       = view.findViewById(R.id.genreSpinner);
        chipNetflix        = view.findViewById(R.id.chipNetflix);
        chipPrime          = view.findViewById(R.id.chipPrime);
        chipDisney         = view.findViewById(R.id.chipDisney);
        chipHulu           = view.findViewById(R.id.chipHulu);
        chipMax            = view.findViewById(R.id.chipMax);
        chipApple          = view.findViewById(R.id.chipApple);
        chipParamount      = view.findViewById(R.id.chipParamount);
        chipPeacock        = view.findViewById(R.id.chipPeacock);
        btnGenerateMovie   = view.findViewById(R.id.btnGenerateMovie);
        btnShareText       = view.findViewById(R.id.btnShareText);
        btnShareEmail      = view.findViewById(R.id.btnShareEmail);
        moviePosterImage   = view.findViewById(R.id.moviePosterImage);
        movieTitle         = view.findViewById(R.id.movieTitle);
        movieOverview      = view.findViewById(R.id.movieOverview);
        movieStreamingInfo = view.findViewById(R.id.movieStreamingInfo);

        serviceChips = new Chip[]{chipNetflix, chipPrime, chipDisney, chipHulu, chipMax, chipApple, chipParamount, chipPeacock};

        for (Chip chip : serviceChips) {
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    applyChipColor((Chip) buttonView);
                }
            });
        }

        ArrayAdapter<String> genreAdapter =
                new ArrayAdapter<>(requireContext(), R.layout.spinner_item, GENRE_NAMES);
        genreAdapter.setDropDownViewResource(R.layout.spinner_item);
        genreSpinner.setAdapter(genreAdapter);

        btnGenerateMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserPreferences();
                movieTitle.setText("Searching...");
                movieOverview.setText("");
                movieStreamingInfo.setText("");
                String genreId = GENRE_IDS[genreSpinner.getSelectedItemPosition()];
                String providers = getWatchProvidersParam();
                new FetchMovieTask().execute(genreId, providers);
            }
        });

        btnShareText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareViaText();
            }
        });

        btnShareEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareViaEmail();
            }
        });

        loadUserPreferences();

        for (Chip chip : serviceChips) {
            applyChipColor(chip);
        }

        return view;
    }

    private void applyChipColor(Chip chip) {
        if (chip.isChecked()) {
            chip.setChipBackgroundColorResource(R.color.fc_red);
            chip.setTextColor(getResources().getColor(R.color.white));
        } else {
            chip.setChipBackgroundColorResource(R.color.fc_element);
            chip.setTextColor(getResources().getColor(R.color.fc_text_primary));
        }
    }

    private String getSelectedGenre() {
        return GENRE_NAMES[genreSpinner.getSelectedItemPosition()];
    }

    private String getWatchProvidersParam() {
        List<String> ids = new ArrayList<>();
        if (chipNetflix.isChecked())   ids.add("8");
        if (chipPrime.isChecked())     ids.add("9");
        if (chipDisney.isChecked())    ids.add("337");
        if (chipHulu.isChecked())      ids.add("15");
        if (chipMax.isChecked())       ids.add("1899");
        if (chipApple.isChecked())     ids.add("350");
        if (chipParamount.isChecked()) ids.add("531");
        if (chipPeacock.isChecked())   ids.add("386");
        return TextUtils.join("|", ids);
    }

    private void saveUserPreferences() {
        if (getActivity() == null) return;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("FinalCutPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("SAVED_GENRE_POS",    genreSpinner.getSelectedItemPosition());
        editor.putBoolean("HAS_NETFLIX",    chipNetflix.isChecked());
        editor.putBoolean("HAS_PRIME",      chipPrime.isChecked());
        editor.putBoolean("HAS_DISNEY",     chipDisney.isChecked());
        editor.putBoolean("HAS_HULU",       chipHulu.isChecked());
        editor.putBoolean("HAS_MAX",        chipMax.isChecked());
        editor.putBoolean("HAS_APPLE",      chipApple.isChecked());
        editor.putBoolean("HAS_PARAMOUNT",  chipParamount.isChecked());
        editor.putBoolean("HAS_PEACOCK",    chipPeacock.isChecked());
        editor.apply();
        Log.d(TAG, "Preferences saved.");
    }

    private void loadUserPreferences() {
        if (getActivity() == null) return;
        SharedPreferences sharedPref = getActivity().getSharedPreferences("FinalCutPrefs", Context.MODE_PRIVATE);
        genreSpinner.setSelection(sharedPref.getInt("SAVED_GENRE_POS", 0));
        chipNetflix.setChecked(sharedPref.getBoolean("HAS_NETFLIX",   true));
        chipPrime.setChecked(sharedPref.getBoolean("HAS_PRIME",       false));
        chipDisney.setChecked(sharedPref.getBoolean("HAS_DISNEY",     false));
        chipHulu.setChecked(sharedPref.getBoolean("HAS_HULU",         false));
        chipMax.setChecked(sharedPref.getBoolean("HAS_MAX",           false));
        chipApple.setChecked(sharedPref.getBoolean("HAS_APPLE",       false));
        chipParamount.setChecked(sharedPref.getBoolean("HAS_PARAMOUNT", false));
        chipPeacock.setChecked(sharedPref.getBoolean("HAS_PEACOCK",   false));
        Log.d(TAG, "Preferences loaded.");
    }

    private class FetchMovieTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            String genreId   = params[0];
            String providers = params[1];
            try {
                int page = new Random().nextInt(3) + 1;
                StringBuilder urlString = new StringBuilder(BASE_URL);
                urlString.append("?api_key=").append(API_KEY);
                urlString.append("&with_genres=").append(genreId);
                urlString.append("&sort_by=popularity.desc");
                urlString.append("&page=").append(page);
                if (!providers.isEmpty()) {
                    urlString.append("&watch_region=US");
                    urlString.append("&with_watch_providers=").append(providers);
                    urlString.append("&with_watch_monetization_types=flatrate");
                }

                URL url = new URL(urlString.toString());
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
                JSONArray results   = response.getJSONArray("results");
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
            if (getActivity() == null) return;

            if (movie == null) {
                movieTitle.setText("No match found");
                movieOverview.setText("Try a different genre or select more streaming services.");
                movieStreamingInfo.setText("");
                return;
            }

            try {
                String title    = movie.getString("title");
                String overview = movie.getString("overview");
                String poster   = movie.optString("poster_path", "");
                int movieId     = movie.getInt("id");

                lastMovieTitle = title;
                movieTitle.setText(title);
                movieOverview.setText(overview);
                movieStreamingInfo.setText("Loading streaming info...");

                if (!poster.isEmpty()) {
                    new DownloadPosterTask().execute(IMAGE_BASE + poster);
                }

                new FetchStreamingTask().execute(String.valueOf(movieId));

            } catch (Exception e) {
                Log.d(TAG, "onPostExecute error: " + e.getMessage());
            }
        }
    }

    private class FetchStreamingTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String movieId = params[0];
            try {
                String urlString = PROVIDERS_BASE + movieId
                        + "/watch/providers?api_key=" + API_KEY;

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
                JSONObject results  = response.optJSONObject("results");
                if (results == null) return null;

                JSONObject us = results.optJSONObject("US");
                if (us == null) return null;

                JSONArray flatrate = us.optJSONArray("flatrate");
                if (flatrate == null || flatrate.length() == 0) return null;

                List<String> names = new ArrayList<>();
                for (int i = 0; i < flatrate.length(); i++) {
                    JSONObject provider = flatrate.getJSONObject(i);
                    names.add(provider.getString("provider_name"));
                }

                return TextUtils.join(" · ", names);

            } catch (Exception e) {
                Log.d(TAG, "FetchStreamingTask error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String providers) {
            if (getActivity() == null || movieStreamingInfo == null) return;
            if (providers == null || providers.isEmpty()) {
                movieStreamingInfo.setText("Streaming info unavailable");
            } else {
                movieStreamingInfo.setText(providers.toUpperCase());
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
        String moviePart = lastMovieTitle.isEmpty() ? "" : " I'm thinking \"" + lastMovieTitle + "\".";
        String message = "Movie night with Final-Cut! I'm in the mood for a "
                + getSelectedGenre() + " movie." + moviePart + " Want to watch something together?";
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        } catch (Exception e) {
            Log.d(TAG, "No messaging app available.");
        }
    }

    private void shareViaEmail() {
        String moviePart = lastMovieTitle.isEmpty() ? "" : " I found a great one: \"" + lastMovieTitle + "\".";
        String subject   = "Movie Night Invite from Final-Cut";
        String body      = "I'm using Final-Cut to find a " + getSelectedGenre()
                + " movie." + moviePart + " Want to join me for a movie night?";
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

    public void watchTrailer() {
        String trailerUrl = "https://www.youtube.com";
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
        startActivity(trailerIntent);
    }
}