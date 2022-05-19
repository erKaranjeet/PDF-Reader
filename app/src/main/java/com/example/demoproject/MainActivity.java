package com.example.demoproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvAllFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvAllFiles = (RecyclerView) findViewById(R.id.rv_all_files);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AllFilesAdapter adapter = new AllFilesAdapter(MainActivity.this, getPdfList());
                rvAllFiles.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private ArrayList<String> getPdfList() {
        ArrayList<String> pdfList = new ArrayList<>();
        Uri collection;

        final String[] projection = new String[]{
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MIME_TYPE,
        };

        final String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        final String selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?";

        final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf");
        final String[] selectionArgs = new String[]{mimeType};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else{
            collection = MediaStore.Files.getContentUri("external");
        }


        try (Cursor cursor = getContentResolver().query(collection, projection, selection, selectionArgs, sortOrder)) {
            assert cursor != null;

            if (cursor.moveToFirst()) {
                int columnData = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
                int columnName = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                do {
                    pdfList.add((cursor.getString(columnData)));
                    Log.d("MainActivity", "getPdf: " + cursor.getString(columnData));
                    //you can get your pdf files
                } while (cursor.moveToNext());
            }
        }
        return pdfList;
    }
}