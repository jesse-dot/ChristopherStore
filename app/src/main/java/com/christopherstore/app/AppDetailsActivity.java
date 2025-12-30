package com.christopherstore.app;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;

public class AppDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_INSTALL_PACKAGES = 1001;
    private static final int REQUEST_WRITE_STORAGE = 1002;

    private TextView appNameTextView;
    private TextView appDescriptionTextView;
    private TextView appVersionTextView;
    private TextView appPackageTextView;
    private TextView appSizeTextView;
    private Button downloadButton;
    private ProgressBar progressBar;

    private String appName;
    private String appDescription;
    private String appVersion;
    private String appPackage;
    private String downloadUrl;
    private long appSize;
    private long downloadId = -1;

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == downloadId) {
                progressBar.setVisibility(View.GONE);
                downloadButton.setEnabled(true);
                Toast.makeText(AppDetailsActivity.this, "Download completed!", Toast.LENGTH_SHORT).show();
                installApk();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_details);

        // Initialize views
        appNameTextView = findViewById(R.id.detail_app_name);
        appDescriptionTextView = findViewById(R.id.detail_app_description);
        appVersionTextView = findViewById(R.id.detail_app_version);
        appPackageTextView = findViewById(R.id.detail_app_package);
        appSizeTextView = findViewById(R.id.detail_app_size);
        downloadButton = findViewById(R.id.download_button);
        progressBar = findViewById(R.id.progress_bar);

        // Get data from intent
        Intent intent = getIntent();
        appName = intent.getStringExtra("app_name");
        appDescription = intent.getStringExtra("app_description");
        appVersion = intent.getStringExtra("app_version");
        appPackage = intent.getStringExtra("app_package");
        downloadUrl = intent.getStringExtra("app_download_url");
        appSize = intent.getLongExtra("app_size", 0);

        // Set data to views
        appNameTextView.setText(appName);
        appDescriptionTextView.setText(appDescription);
        appVersionTextView.setText("Version: " + appVersion);
        appPackageTextView.setText("Package: " + appPackage);
        appSizeTextView.setText("Size: " + formatSize(appSize));

        // Set up download button
        downloadButton.setOnClickListener(v -> startDownload());

        // Register download receiver
        registerReceiver(downloadReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private String formatSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        }
    }

    private void startDownload() {
        // Check for storage permission on Android 6.0-9.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
                return;
            }
        }

        downloadApk();
    }

    private void downloadApk() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            downloadButton.setEnabled(false);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setTitle(appName);
            request.setDescription("Downloading " + appName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appPackage + ".apk");
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            downloadId = downloadManager.enqueue(request);

            Toast.makeText(this, "Download started...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            downloadButton.setEnabled(true);
            Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void installApk() {
        // Check if the app can install packages from unknown sources
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!getPackageManager().canRequestPackageInstalls()) {
                showInstallPermissionDialog();
                return;
            }
        }

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    appPackage + ".apk");

            if (!file.exists()) {
                Toast.makeText(this, "APK file not found", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            Uri apkUri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                apkUri = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        file);
                installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                apkUri = Uri.fromFile(file);
            }

            installIntent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(installIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Installation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void showInstallPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("This app needs permission to install packages from unknown sources. Would you like to enable this in Settings?")
                .setPositiveButton("Settings", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, REQUEST_INSTALL_PACKAGES);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INSTALL_PACKAGES) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    installApk();
                } else {
                    Toast.makeText(this, "Install permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadApk();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(downloadReceiver);
        } catch (Exception e) {
            // Receiver not registered
        }
    }
}
