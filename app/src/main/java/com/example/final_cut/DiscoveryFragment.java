package com.example.final_cut;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

public class DiscoveryFragment extends Fragment {
    private static final String TAG = "FinalCut_DiscoveryFrag";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_discovery, container, false);
    }

    // Lesson 3 Concept:
    public void simulateSavingPreferences() {
        Bundle userPreferences = new Bundle();
        userPreferences.putString("SELECTED_GENRE", "Sci-Fi");
        userPreferences.putBoolean("PREFER_NETFLIX", true);
        Log.d(TAG, "Preferences bundled safely: " + userPreferences.toString());
    }

    // Lesson 3 Concept:
    public void watchTrailer() {
        String trailerUrl = "https://www.youtube.com";

        // System intent acting to target external handling applications
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl));
        startActivity(trailerIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Fragment lifecycle tracking: onPause execution completed.");
    }
}