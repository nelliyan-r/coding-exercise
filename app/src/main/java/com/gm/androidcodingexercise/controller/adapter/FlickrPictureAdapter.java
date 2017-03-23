package com.gm.androidcodingexercise.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.googlecode.flickrjandroid.photos.PhotoList;
import com.gm.androidcodingexercise.Constants;

/**
 * AndroidCodingExercise
 */
public class FlickrPictureAdapter extends BaseAdapter {
    private static final String TAG = Constants.BASE_TAG + FlickrPictureAdapter.class.getSimpleName();

    private Context context;
    private PhotoList photos;

    public FlickrPictureAdapter(Context context, PhotoList photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override public int getCount() {
        return photos.size();
    }

    @Override public Object getItem(int position) {
        return photos.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View cellView;
        return cellView;
    }
}
