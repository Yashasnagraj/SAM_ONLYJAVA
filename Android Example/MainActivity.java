package com.example.videotransitions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example Android Activity demonstrating how to use the TransitionEngine
 * with JavaCV for video transitions.
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Button btnApplyTransition;
    private ProgressBar progressBar;
    private TextView statusText;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnApplyTransition = findViewById(R.id.btn_apply_transition);
        progressBar = findViewById(R.id.progress_bar);
        statusText = findViewById(R.id.status_text);
        
        executorService = Executors.newSingleThreadExecutor();

        btnApplyTransition.setOnClickListener(v -> checkPermissionsAndApplyTransition());
    }

    private void checkPermissionsAndApplyTransition() {
        if (hasRequiredPermissions()) {
            applyTransition();
        } else {
            requestPermissions();
        }
    }

    private boolean hasRequiredPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && allPermissionsGranted(grantResults)) {
                applyTransition();
            } else {
                Toast.makeText(this, "Permissions required to access videos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void applyTransition() {
        btnApplyTransition.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        statusText.setText("Processing transition...");

        executorService.execute(() -> {
            try {
                // Define input and output paths
                File moviesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                String input1 = new File(moviesDir, "clip_a.mp4").getAbsolutePath();
                String input2 = new File(moviesDir, "clip_b.mp4").getAbsolutePath();
                String output = new File(moviesDir, "transition_output.mp4").getAbsolutePath();

                // Apply the transition
                boolean success = TransitionEngine.applyFadeTransition(input1, input2, output, 1.0);

                // Update UI on the main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnApplyTransition.setEnabled(true);
                    
                    if (success) {
                        statusText.setText("Transition completed successfully!");
                        Toast.makeText(this, "Output saved to: " + output, Toast.LENGTH_LONG).show();
                    } else {
                        statusText.setText("Failed to apply transition");
                        Toast.makeText(this, "Error processing videos", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnApplyTransition.setEnabled(true);
                    statusText.setText("Error: " + e.getMessage());
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
