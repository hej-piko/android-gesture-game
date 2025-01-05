package com.example.gesturegame;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private MediaPlayer backgroundMusic;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the home fragment layout
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the background music player
        backgroundMusic = MediaPlayer.create(getContext(), R.raw.home_music);

        // Play the music
        backgroundMusic.setLooping(true); // Set the music to loop
        backgroundMusic.start(); // Start playing the music
        backgroundMusic.setVolume(0.5f, 0.5f); // Set the volume to 50% (range is from 0.0 to 1.0)

        // Play button to navigate to GestureFragment
        Button playButton = view.findViewById(R.id.play_button);
        playButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GestureFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause the music when the fragment is paused
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume the music when the fragment is resumed
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Release the music resources when the fragment is destroyed
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
    }
}
