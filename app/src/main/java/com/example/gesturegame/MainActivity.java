package com.example.gesturegame;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge UI
        EdgeToEdge.enable(this);

        // Set the content view to your main layout
        setContentView(R.layout.activity_main);

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the GestureView and set the line size
        GestureView gestureView = findViewById(R.id.bottom_gesture_view);
        TextView gestureText = findViewById(R.id.gesture_text);

        if (gestureView != null && gestureText != null) {
            gestureView.setOnGestureDetectedListener(gesture -> runOnUiThread(() -> {
                gestureText.setText("Gesture detected: " + gesture);
            }));
            gestureView.setLineSize(22f); // Set the line size to 20 pixels
        }
    }
}
