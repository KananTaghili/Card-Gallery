package com.kanan.cardgallery.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.kanan.cardgallery.R;

import java.io.File;

/**
 * Created by Kanan on 01.08.2023.
 */

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ImageListViewHolder> {

    private final Context context;
    private File[] files = new File[0];

    public ImageListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ImageListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_list_item, viewGroup, false);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (getScreenHeight(context) * 0.4));
        view.setLayoutParams(params);

        return new ImageListViewHolder(view);
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageListViewHolder holder, int position) {

        try {
            TextView txt_description = holder.itemView.findViewById(R.id.txt_description);
            PhotoView photoView = holder.itemView.findViewById(R.id.photo_view);

            txt_description.setText("Bazarstore");

            Glide.with(context)
                    .load(files[position])
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(photoView);
            photoView.setOnLongClickListener(v -> {
                zoomImage(files[position]);
                return false;
            });

        } catch (Exception e) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return files.length;
    }

    static class ImageListViewHolder extends RecyclerView.ViewHolder {
        ImageListViewHolder(View itemView) {
            super(itemView);
        }
    }

    @SuppressLint("InflateParams")
    private void zoomImage(File file) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_list_zoom, null);
        PhotoView photoView = dialogView.findViewById(R.id.photo_view);
        ImageView rotate_image = dialogView.findViewById(R.id.rotate_image);

        rotate_image.setOnClickListener(view -> {
            float scale = photoView.getRotation() % 180 == 0 ? (float) photoView.getWidth() / photoView.getHeight() : 1.0f;
            photoView.animate().rotationBy(90).scaleX(scale).scaleY(scale).setDuration(100).start();
        });

        Glide.with(context)
                .load(file)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(photoView);
        Dialog dialog = new Dialog(context);
        dialog.setContentView(dialogView);
        assert dialog.getWindow() != null;
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refresh() {
        String path = Environment.getExternalStorageDirectory().toString() + "/CardGallery";
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdir();
        }
        files = dir.listFiles();
        notifyDataSetChanged();
    }
}