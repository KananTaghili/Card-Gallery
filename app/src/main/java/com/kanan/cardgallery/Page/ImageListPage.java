package com.kanan.cardgallery.Page;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.BuildConfig;
import com.google.android.material.snackbar.Snackbar;
import com.kanan.cardgallery.Adapter.ImageListAdapter;
import com.kanan.cardgallery.R;

/**
 * Created by Kanan on 01.08.2023.
 */

public class ImageListPage extends AppCompatActivity {

    private ImageListAdapter imageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_list);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cards");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        requestPermission();

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (!Environment.isExternalStorageManager()) {

                Snackbar.make(findViewById(android.R.id.content), "Permission required!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Go to setteings", v -> {
                            Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                            startActivity(intent);
                            finish();
                        }).show();
            } else {
                RecyclerView recyclerview_image = findViewById(R.id.recyclerview_image);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerview_image.setLayoutManager(layoutManager);
                imageListAdapter = new ImageListAdapter(this);
                recyclerview_image.setAdapter(imageListAdapter);
                imageListAdapter.refresh();
            }

        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                }
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                } else {
                    RecyclerView recyclerview_image = findViewById(R.id.recyclerview_image);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                    recyclerview_image.setLayoutManager(layoutManager);
                    imageListAdapter = new ImageListAdapter(this);
                    recyclerview_image.setAdapter(imageListAdapter);
                    imageListAdapter.refresh();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_list_and_take_photo, menu);
        menu.getItem(3).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_take_photo_from_camera) {
            gotoPhotoPage(1);
        } else if (itemId == R.id.action_take_photo_from_gallery) {
            gotoPhotoPage(2);
        } else if (itemId == R.id.action_refresh) {
            imageListAdapter.refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    private void gotoPhotoPage(int requestCode) {
        Intent intent = new Intent(ImageListPage.this, TakePhotoPage.class);
        intent.putExtra("request_code", requestCode);
        startActivity(intent);
    }

}