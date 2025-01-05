package com.example.gesturegame;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GestureFragment extends Fragment {

    private static final String[] SHAPES = {"Circle", "Triangle", "Square", "Vertical Line", "Horizontal Line"};
    private String currentShape;
    private String previousShape = "";
    private Random random = new Random();

    private SoundPool soundPool;
    private int correctSoundId, wrongSoundId;
    private MediaPlayer backgroundMusic;

    private TextView gestureText;
    private GestureView gestureView;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the gesture fragment layout
        View view = inflater.inflate(R.layout.fragment_gesture, container, false);

        // Initialize UI components
        gestureText = view.findViewById(R.id.gesture_text);
        gestureView = view.findViewById(R.id.bottom_gesture_view);

        // Set line size to make drawing thicker
        gestureView.setLineSize(22f);  // Set the desired stroke width

        // Initialize sound effects and background music
        setupSoundEffects();
        setupBackgroundMusic();

        // Set the first shape to draw
        currentShape = getRandomShape();
        gestureText.setText("Draw: " + currentShape);

        // Set up gesture detection listener
        setupGestureDetection();

        return view;
    }

    private void setupSoundEffects() {
        soundPool = new SoundPool.Builder().setMaxStreams(5).build();
        correctSoundId = soundPool.load(requireContext(), R.raw.correct_sound, 1);
        wrongSoundId = soundPool.load(requireContext(), R.raw.wrong_sound, 1);
    }

    private void setupBackgroundMusic() {
        backgroundMusic = MediaPlayer.create(requireContext(), R.raw.background_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.start();
    }

    @SuppressLint("SetTextI18n")
    private void setupGestureDetection() {
        gestureView.setOnGestureDetectedListener(gesture -> requireActivity().runOnUiThread(() -> {
            if (gesture.equals(currentShape)) {
                soundPool.play(correctSoundId, 1, 1, 0, 0, 1);
                gestureText.setText("Correct! Prepare for a new shape...");
                new Handler().postDelayed(() -> {
                    currentShape = getRandomShape();
                    gestureText.setText("Draw: " + currentShape);
                }, 2000);
            } else {
                soundPool.play(wrongSoundId, 1, 1, 0, 0, 1);
                gestureText.setText("Try again! Draw: " + currentShape);
            }
        }));
    }

    private String getRandomShape() {
        List<String> shapePool = new ArrayList<>();
        Collections.addAll(shapePool, SHAPES);
        shapePool.remove(previousShape);
        Collections.shuffle(shapePool);
        previousShape = shapePool.get(random.nextInt(shapePool.size()));
        return previousShape;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (soundPool != null) soundPool.release();
        if (backgroundMusic != null) backgroundMusic.release();
    }
}
