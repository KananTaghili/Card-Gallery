package com.kanan.cardgallery.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.github.chrisbanes.photoview.PhotoView;
import com.kanan.cardgallery.Model.ImageModel;
import com.kanan.cardgallery.Page.TakePhotoPage;
import com.kanan.cardgallery.R;

/**
 * Created by Kanan on 01.08.2023.
 */

public class ViewPagerAdapterUploadImage extends PagerAdapter {

    private final Context context;
    private final ViewPager viewPager;

    public ViewPagerAdapterUploadImage(Context context, ViewPager viewPager) {
        this.context = context;
        this.viewPager = viewPager;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.upload_image_item, null);

        ImageModel imageInfo = TakePhotoPage.images.get(position);

        PhotoView photoView = view.findViewById(R.id.img_image);
        EditText txt_note = view.findViewById(R.id.txt_note);
        ImageView img_delete = view.findViewById(R.id.img_delete_photo);

        img_delete.setColorFilter(Color.RED);
        img_delete.setOnClickListener(view12 -> new AlertDialog.Builder(context)
                .setMessage("Are you sure to delete?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    TakePhotoPage.images.remove(position);
                    notifyDataSetChanged();
                    viewPager.setCurrentItem(0);
                })
                .setNegativeButton(android.R.string.no, null)
                .show());

        photoView.setImageURI(imageInfo.imageUri);

        txt_note.setText(imageInfo.note);
        txt_note.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                imageInfo.note = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    @Override
    public int getCount() {
        return TakePhotoPage.images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}