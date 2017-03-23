package com.gm.androidcodingexercise.util;

/**
 * AndroidCodingExercise
 */
public interface AsyncTaskListener {

    public void onCompletion(Object object);
    public void onError(Exception e);
}
