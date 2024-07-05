package com.kanan.cardgallery.Page;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.github.chrisbanes.photoview.BuildConfig;
import com.kanan.cardgallery.Model.ImageModel;
import com.kanan.cardgallery.Adapter.ViewPagerAdapterUploadImage;
import com.kanan.cardgallery.Util.CardImageHelper;
import com.kanan.cardgallery.Util.ViewPagerFixed;
import com.kanan.cardgallery.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kanan on 01.08.2023.
 */

public class TakePhotoPage extends AppCompatActivity {
    private Uri imageUri;
    private String normalPath = "";
    private ViewPagerFixed viewPager;
    private WormDotsIndicator dot;
    public static List<ImageModel> images;
    private CardImageHelper helper;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_image);

        getSupportActionBar().setTitle("Set image(s)");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = this;

        images = new ArrayList<>();

        viewPager = findViewById(R.id.view_pager);
        dot = findViewById(R.id.dot);

        helper = new CardImageHelper();

        int requestCode = getIntent().getIntExtra("request_code", 1);

        if (requestCode == 1) {
            openCamera();
        } else {
            openGallery();
        }
    }

    private void openCamera() {
        File photoFile = null;
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File root = new File(Environment.getExternalStorageDirectory().getPath() + "/CardGallery/Camera/");
                if (!root.exists()) {
                    root.mkdirs();
                }
                String formatter = new SimpleDateFormat("dd_MM_yyyy_HHmmss", Locale.getDefault()).format(new Date());
                String imageName = formatter + ".jpg";
                photoFile = new File(root, imageName);
                normalPath = photoFile.getPath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, 1);
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            //Camera
            if (requestCode == 1) {
                images.add(helper.makeImageModel(false, normalPath, imageUri));

                //Gallery
            } else if (requestCode == 2) {

                //Multiple from gallery
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        imageUri = data.getClipData().getItemAt(i).getUri();
                        normalPath = helper.getPath(context, imageUri);
                        images.add(helper.makeImageModel(true, normalPath, imageUri));
                    }

                    //One from gallery
                } else if (data.getData() != null) {
                    imageUri = data.getData();
                    normalPath = helper.getPath(context, imageUri);
                    images.add(helper.makeImageModel(true, normalPath, imageUri));
                }
            }

            ViewPagerAdapterUploadImage adapter = new ViewPagerAdapterUploadImage(context, viewPager);
            viewPager.setAdapter(adapter);
            if (requestCode == 1) {
                viewPager.setCurrentItem(images.size() - 1);
            }

            dot.setViewPager(viewPager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_list_and_take_photo, menu);
        menu.getItem(0).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save_photo) {
            for (int i = 0; i < images.size(); i++) {

                try {
                    String path = Environment.getExternalStorageDirectory().toString() + "/CardGallery";
                    File gpxfile = new File(path, images.get(i).imgName);

                    File f = new File(images.get(i).normalPath);

                    try (InputStream in = new FileInputStream(f)) {
                        try (OutputStream out = new FileOutputStream(gpxfile)) {
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            finish();

            } else if (itemId == R.id.action_take_photo_from_camera) {
                openCamera();
            } else if (itemId == R.id.action_take_photo_from_gallery) {
                openGallery();
            }
        return super.onOptionsItemSelected(item);
    }
}