package com.gm.androidcodingexercise.util;

import com.googlecode.flickrjandroid.photos.Photo;
import com.gm.androidcodingexercise.Constants;

/**
 * AndroidCodingExercise
 */
public class FlickrPhotoUtil {
    private static final String TAG = Constants.BASE_TAG + FlickrPhotoUtil.class.getSimpleName();

    public enum FlickrPhotoSize {Small, Medium, Large};

    public static String keyForFlickrPhoto(Photo photo, FlickrPhotoSize size) {
        String sizeModifier = "";
        switch (size) {
            case Small:
                sizeModifier = "sml";
                break;
            case Medium:
                sizeModifier = "med";
                break;
            case Large:
                sizeModifier = "lrg";
                break;
            default:
                break;
        }
        String key_base = photo.getOwner().getId() + photo.getId() + sizeModifier;
        return DiskLruMediaCache.keyForFileName(key_base);
    }
}
