package com.example.gesturegame;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Install the system splash screen (Android 12+)
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        // Set the custom splash screen layout (activity_splash.xml)
        setContentView(R.layout.activity_splash);

        // Optional: Customize the splash screen's exit animation
        splashScreen.setOnExitAnimationListener(splashScreenView -> {
            // Remove splash screen after content is ready
            splashScreenView.remove();
        });

        // After a short delay (e.g., 2 seconds), transition to the HomeFragment
        new Handler().postDelayed(() -> {
            // Ensure activity_main.xml is loaded, then load HomeFragment into fragment_container
            setContentView(R.layout.activity_main);  // Set the main activity layout containing fragment_container

            // Transition to HomeFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()) // Replace with HomeFragment
                    .commit();
        }, 3500); // Delay of 2 seconds (you can adjust this as needed)
    }
}
