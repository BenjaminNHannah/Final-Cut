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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discovery, container, false);

        genreRadioGroup = view.findViewById(R.id.genreRadioGroup);
        checkNetflix = view.findViewById(R.id.checkNetflix);
        checkHulu = view.findViewById(R.id.checkHulu);
        checkPrime = view.findViewById(R.id.checkPrime);
        Button btnGenerateMovie = view.findViewById(R.id.btnGenerateMovie);
        Button btnShareText = view.findViewById(R.id.btnShareText);
        Button btnShareEmail = view.findViewById(R.id.btnShareEmail);

        btnGenerateMovie.setOnClickListener(v -> saveUserPreferences());

        btnShareText.setOnClickListener(v -> shareViaText());

        btnShareEmail.setOnClickListener(v -> shareViaEmail());

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

    private String getSelectedGenre() {
        int checkedId = genreRadioGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.radioAction) {
            return getString(R.string.genre_action);
        } else if (checkedId == R.id.radioComedy) {
            return getString(R.string.genre_comedy);
        } else {
            return getString(R.string.genre_scifi);
        }
    }

    private void shareViaText() {
        String message = getString(R.string.share_text_message, getSelectedGenre());

        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
            Log.d(TAG, "SMS share intent launched.");
        } catch (Exception e) {
            Log.d(TAG, "No messaging app available to handle SMS intent.");
        }
    }

    private void shareViaEmail() {
        String subject = getString(R.string.share_email_subject);
        String body = getString(R.string.share_email_body, getSelectedGenre());

        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("message/rfc822");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(emailIntent);
            Log.d(TAG, "Email share intent launched.");
        } catch (Exception e) {
            Log.d(TAG, "No email app available to handle email intent.");
        }
    }

}