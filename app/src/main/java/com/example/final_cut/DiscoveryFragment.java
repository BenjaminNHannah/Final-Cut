package com.example.final_cut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import androidx.fragment.app.Fragment;

public class DiscoveryFragment extends Fragment {
    private static final String TAG = "FinalCut_DiscoveryFrag";

    private RadioGroup genreRadioGroup;
    private CheckBox checkNetflix, checkHulu, checkPrime;
    private Button btnGenerateMovie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        genreRadioGroup = view.findViewById(R.id.genreRadioGroup);
        checkNetflix = view.findViewById(R.id.checkNetflix);
        checkHulu = view.findViewById(R.id.checkHulu);
        checkPrime = view.findViewById(R.id.checkPrime);
        btnGenerateMovie = view.findViewById(R.id.btnGenerateMovie);

        btnGenerateMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserPreferences();
            }
        });

        loadUserPreferences();

        return view;
    }

    private void saveUserPreferences() {
        if (getActivity() == null) return;

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FinalCutPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int selectedGenreId = genreRadioGroup.getCheckedRadioButtonId();
        editor.putInt("SAVED_GENRE_ID", selectedGenreId);

        editor.putBoolean("HAS_NETFLIX", checkNetflix.isChecked());
        editor.putBoolean("HAS_HULU", checkHulu.isChecked());
        editor.putBoolean("HAS_PRIME", checkPrime.isChecked());

        editor.apply();
        Log.d(TAG, "User filters successfully committed to SharedPreferences storage.");
    }

    private void loadUserPreferences() {
        if (getActivity() == null) return;

        SharedPreferences sharedPref = getActivity().getSharedPreferences("FinalCutPrefs", Context.MODE_PRIVATE);

        int savedGenreId = sharedPref.getInt("SAVED_GENRE_ID", R.id.radioSciFi);
        genreRadioGroup.check(savedGenreId);

        checkNetflix.setChecked(sharedPref.getBoolean("HAS_NETFLIX", true));
        checkHulu.setChecked(sharedPref.getBoolean("HAS_HULU", false));
        checkPrime.setChecked(sharedPref.getBoolean("HAS_PRIME", false));

        Log.d(TAG, "Persistent user checkboxes verified and synchronized successfully.");
    }

    public void watchTrailer() {
        String trailerUrl = "https://www.youtube.com";
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
        startActivity(trailerIntent);
    }
}