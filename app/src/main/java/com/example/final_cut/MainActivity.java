package com.example.final_cut;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FinalCut_MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            DiscoveryFragment discoveryFragment = new DiscoveryFragment();
            fragmentTransaction.add(R.id.fragment_container, discoveryFragment);
            fragmentTransaction.commit();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called: Activity interface is initializing visibility.");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called: Interactive foreground state achieved.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called: App backgrounding initiated. Context paused.");
    }
}