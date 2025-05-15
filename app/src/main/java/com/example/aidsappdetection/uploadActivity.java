package com.example.aidsappdetection;


import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class uploadActivity extends AppCompatActivity {
    private static final String TAG = "uploadActivity"; // Consistent TAG for logging
    Button uploadbtn;
    EditText searchEditText;
    TextView updatestatus, lastupload;
    DatabaseHelper dbHelper;
    ListView reportList;
    List<ReportItem> reports;
    ReportAdapter adapter;
    private ActivityResultLauncher<String> filePickerLauncher;
    final String placeholder = "No reports uploaded yet."; // Consistent placeholder string
    String userEmail; // Store user email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //ocrProcessor = new OCRProcessor();
        updatestatus = findViewById(R.id.text_upload_status);
        lastupload = findViewById(R.id.last_upload_time);
        reportList = findViewById(R.id.reportListView);
        reports = new ArrayList<>();
        //-------------------------------------------------------------------------------------------------------------------------
        // Database introduction and email fetch
        dbHelper = new DatabaseHelper(this);
        SharedPreferences sharedPref = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        userEmail = sharedPref.getString("email", null); // Get email and store
        loadReportsFromDB(userEmail); // Load reports on activity creation
        updateReportInfo(userEmail); // Update report info on activity creation

        // Initialize ActivityResultLauncher for file picking
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        String fileUriString = uri.toString(); // filepath
                        String uploadTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()); // datetime
                        String fileName = getFileNameFromUri(uri);
                        Log.d(TAG, "File URI: " + fileUriString);
                        Log.d(TAG, "File Name: " + fileName);

                        if (!dbHelper.isFileExist(fileUriString)) {
                            // Insert report and cache image
                            boolean success = dbHelper.insertReport(userEmail, fileUriString, uploadTime, fileName);
                            if (success) {
                                File cacheDir = getCacheDir();
                                File cachedImageFile = new File(cacheDir, fileName);
                                Log.d(TAG, "Cache File Path: " + cachedImageFile.getAbsolutePath());
                                try (InputStream in = getContentResolver().openInputStream(uri);  // Use content resolver
                                     OutputStream out = new FileOutputStream(cachedImageFile)) {
                                    byte[] buffer = new byte[1024];
                                    int length;
                                    while ((length = in.read(buffer)) > 0) {
                                        out.write(buffer, 0, length);
                                    }
                                    Toast.makeText(uploadActivity.this, "Report Saved and Image Cached!", Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    Log.e(TAG, "Error caching image: " + e.getMessage());
                                    Toast.makeText(uploadActivity.this, "Report Saved, but Error Caching Image!", Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = new Intent(uploadActivity.this, Preview.class);
                                intent.putExtra("imageUri", uri); // imageUri is your selected URI
                                startActivity(intent);

                            } else {
                                Toast.makeText(uploadActivity.this, "Failed to save report.", Toast.LENGTH_SHORT).show();
                            }
                            updateReportInfo(userEmail); // info update
                            loadReportsFromDB(userEmail);
                        } else {
                            Toast.makeText(uploadActivity.this, "Image already exist.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(uploadActivity.this, "Image selection cancelled.", Toast.LENGTH_SHORT).show();
                    }

                });
        //-------------------------------------------------------------------------------------------------------------------------
        // listview view
        reportList.setOnItemClickListener((parent, view, position, id) -> {
            if (reports.get(position).getFileName().equals(placeholder)) {
                Toast.makeText(this, "Please upload a file first.", Toast.LENGTH_SHORT).show();
                return;
            }
            ReportItem selectedReport = reports.get(position);
            String imageName = selectedReport.getFileName();

            File cacheDir = getCacheDir();
            File cachedImageFile = new File(cacheDir, imageName);

            if (cachedImageFile.exists()) {
                Log.d(TAG, "Image found in cache: " + cachedImageFile.getAbsolutePath());
                openSecondActivity(cachedImageFile.getAbsolutePath());
            } else {
                String actualImagePath = dbHelper.getImagePath(selectedReport.getId()); // Retrieve the actual path using report ID
                if (actualImagePath != null) {
                    File sourceFile = new File(actualImagePath);
                    try {
                        copyFileToCache(sourceFile, cachedImageFile);
                        openSecondActivity(cachedImageFile.getAbsolutePath());
                    } catch (IOException e) {
                        Log.e(TAG, "Error copying image to cache: " + e.getMessage());
                        Toast.makeText(this, "Error opening image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Image path not found in our records.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Add long click listener for deleting reports
        reportList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!reports.get(position).getFileName().equals(placeholder)) { // Prevent deleting the placeholder
                    showDeleteConfirmationDialog(position);
                    return true; // Consume the event to prevent further actions
                }
                return false;
            }
        });

        //------------------------------------------------------------------------------------------------------------
        // upload btn click
        uploadbtn = findViewById(R.id.btn_upload);
        uploadbtn.setOnClickListener(v -> {
            // Check for READ_MEDIA_IMAGES permission on Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 101); // Request code 101
                    return;
                }
            } else {
                // For older versions, check READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102); // Request code 102
                    return;
                }
            }
            filePickerLauncher.launch("image/*");
        });

        searchEditText = findViewById(R.id.search_bar);  // Replace with your actual ID from activity_upload.xml
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                filterReports(searchText);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    private void filterReports(String searchText) {
        List<ReportItem> filteredList = new ArrayList<>();
        if (searchText.isEmpty()) {
            loadReportsFromDB(userEmail);
            return;        }

        String searchTextLower = searchText.toLowerCase();

        for (ReportItem report : reports) {
            boolean nameMatch = report.getFileName().toLowerCase().contains(searchTextLower);
            boolean dateMatch = report.getUploadTime().startsWith(searchText);

            if (nameMatch || dateMatch) {
                filteredList.add(report);
            }
        }
        updateReportListView(filteredList);
    }

    private void updateReportListView(List<ReportItem> filteredList) {
        if (filteredList.isEmpty()) {
            reports.clear();
            reports.add(new ReportItem(0, placeholder, ""));
        }
        else{
            reports.clear();
            reports.addAll(filteredList);
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void openSecondActivity(String imagePath) {
        Intent intent = new Intent(this, SecondActivity.class);
        intent.putExtra("imagePath", imagePath);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ((requestCode == 101 || requestCode == 102) && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            filePickerLauncher.launch("image/*");
        } else {
            Toast.makeText(this, "Permission denied to access storage.", Toast.LENGTH_SHORT).show();
        }
    }

    //----------------------------------------------------------------------------------------------------------
    // listview work
    private void loadReportsFromDB(String userEmail) {
        reports.clear();
        Cursor cursor = dbHelper.getAllReportsByUser(userEmail);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                do {
                    int idIndex = cursor.getColumnIndex(DatabaseHelper.ReportEntry._ID);
                    int fileNameIndex = cursor.getColumnIndex(DatabaseHelper.ReportEntry.COLUMN_NAME_FILENAME);
                    int dateTimeIndex = cursor.getColumnIndex(DatabaseHelper.ReportEntry.COLUMN_NAME_DATETIME);

                    // Check for null values before accessing data.
                    if (idIndex != -1 && fileNameIndex != -1 && dateTimeIndex != -1) {
                        long reportId = cursor.getLong(idIndex);
                        String fileName = cursor.getString(fileNameIndex);
                        String dateTime = cursor.getString(dateTimeIndex);
                        reports.add(new ReportItem(reportId, fileName, dateTime));
                    }
                    else{
                        Log.e(TAG, "loadReportsFromDB:  One of the columns is missing from cursor");
                    }
                } while (cursor.moveToNext());
            } finally {
                cursor.close(); // Ensure cursor is closed in a finally block
            }
        }

        if (reports.isEmpty()) {
            reports.add(new ReportItem(0, placeholder, ""));
        }

        adapter = new ReportAdapter(this, reports);
        reportList.setAdapter(adapter);
    }

    //---------------------------------------------------------------------------------------------------------------
    // for info updation
    private void updateReportInfo(String userEmail) {
        Cursor cursor = dbHelper.getAllReportsByUser(userEmail);
        if (cursor != null) {
            try {
                int count = cursor.getCount();
                updatestatus.setText("Reports uploaded: " + count);
                if (count > 0) {
                    cursor.moveToLast(); // Move to last row (most recent report)
                    int dateTimeIndex = cursor.getColumnIndex(DatabaseHelper.ReportEntry.COLUMN_NAME_DATETIME);
                    if(dateTimeIndex != -1) {
                        String lastDate = cursor.getString(dateTimeIndex);
                        lastupload.setText("Last upload: " + lastDate);
                    }
                    else{
                        Log.e(TAG, "updateReportInfo:  Column is missing from cursor");
                    }

                } else {
                    lastupload.setText("Last upload: None"); // No report uploaded yet
                }
            } finally {
                cursor.close(); // Ensure cursor is closed
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void copyFileToCache(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        try (InputStream in = getContentResolver().openInputStream(Uri.fromFile(sourceFile));
             OutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw e; // Re-throw the exception to be caught by the caller
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    try{
                        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        if (nameIndex != -1) {
                            result = cursor.getString(nameIndex);
                        }
                        else{
                            Log.e(TAG, "getFileNameFromUri:  Column is missing from cursor");
                        }
                    }
                    finally{
                        cursor.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (result == null) {
            String path = uri.getPath();
            if (path != null) {
                int cut = path.lastIndexOf('/');
                if (cut != -1) {
                    result = path.substring(cut + 1);
                } else {
                    result = path;
                }
            } else {
                result = "Unknown";
            }
        }
        return result;
    }

    private void showDeleteConfirmationDialog(int position) {
        final int posToDelete = position; // Create a final copy for the lambda
        new android.app.AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this report?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Get the report item to delete
                    ReportItem reportToDelete = reports.get(posToDelete);
                    String fileNameToDelete = reportToDelete.getFileName();

                    // Delete from the database
                    boolean deleted = dbHelper.deleteReport(userEmail, fileNameToDelete);
                    if (deleted) {
                        Log.d(TAG, "Report deleted from database");
                        // Delete the file from cache if it exists
                        File cacheDir = getCacheDir();
                        File fileToDelete = new File(cacheDir, fileNameToDelete);
                        if (fileToDelete.exists()) {
                            if (fileToDelete.delete()) {
                                Log.d(TAG, "Cache file deleted: " + fileToDelete.getAbsolutePath());
                            } else {
                                Log.e(TAG, "Failed to delete cache file: " + fileToDelete.getAbsolutePath());
                            }
                        }
                        // Remove from the list and update the adapter.  The key is to do this *after* the database and cache operations.
                        reports.remove(posToDelete);
                        adapter.notifyDataSetChanged();
                        loadReportsFromDB(userEmail);
                        updateReportInfo(userEmail);

                        if (reports.isEmpty()) {
                            reports.add(new ReportItem(0, placeholder, ""));
                            adapter.notifyDataSetChanged();
                        }
                        Toast.makeText(uploadActivity.this, "Report deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Failed to delete report from database");
                        Toast.makeText(uploadActivity.this, "Failed to delete report", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}

// Separate class for ReportItem
class ReportItem {
    private long id;
    private String fileName;
    private String uploadTime;

    public ReportItem(long id, String fileName, String uploadTime) {
        this.id = id;
        this.fileName = fileName;
        this.uploadTime = uploadTime;
    }

    public long getId() {
        return id;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    @Override
    public String toString() {
        return "Uploaded at: " + uploadTime + "\nFile: " + fileName;
    }
}

// Separate class for ReportAdapter
class ReportAdapter extends ArrayAdapter<ReportItem> {
    public ReportAdapter(Context context, List<ReportItem> reports) {
        super(context, android.R.layout.simple_list_item_1, reports);
    }

}

