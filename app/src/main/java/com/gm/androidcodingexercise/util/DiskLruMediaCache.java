package com.gm.androidcodingexercise.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.gm.androidcodingexercise.BuildConfig;
import com.gm.androidcodingexercise.Constants;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * AndroidCodingExercise
 */
public class DiskLruMediaCache {
    private static final String TAG = Constants.BASE_TAG + DiskLruMediaCache.class.getSimpleName();
    private DiskLruCache diskCache;
    private static final int DISK_CACHE_SIZE = 1024 *1024 *10; //10MB
    private static final int VALUE_COUNT = 1;
    private static final int CACHE_INDEX = 0;
    private static final int IO_BUFFER_SIZE = 8 * 1024;
    private static final String CACHE_NAME = "Flickr-Cache";

    public DiskLruMediaCache(Context context) {
        try {
            final File diskCacheDir = getDiskCacheDir(context, CACHE_NAME);
            if (diskCacheDir != null) {
                diskCache = DiskLruCache.open(diskCacheDir, BuildConfig.VERSION_CODE, VALUE_COUNT, DISK_CACHE_SIZE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating/opening DiskLruCache: " + e.getMessage());
        }
    }

    private File getDiskCacheDir(Context context, String cacheName) {
        try {
            String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();
            return new File(cachePath + File.separator + cacheName);
        } catch (Exception e) {
            Log.e(TAG, "error getting disk cache directory, e=" + e.getMessage());
        }
        return null;
    }

    private File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    private boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    private boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    private static final int MAX_KEY_LENGTH = 52;
    public static String keyForFileName(String fileName) {

        String fileKey = fileName.toLowerCase();
        fileKey = fileKey.replaceAll("[^a-z0-9]+", "");

        if (fileKey.length() > MAX_KEY_LENGTH)
            fileKey = fileKey.substring(fileKey.length() - MAX_KEY_LENGTH, fileKey.length());
        return fileKey;
    }

    public void clearCache() {
        try {
            if (diskCache != null) diskCache.delete();
            Log.i(TAG, "disk cache CLEARED");
        } catch (IOException e) {
            Log.e(TAG, "error clearing cache, e=" + e.getMessage());
        }
    }

    public boolean containsKey(String key) {
        boolean contained = false;
        DiskLruCache.Snapshot snapshot = null;
        try {
            if (!diskCache.isClosed()) {
                snapshot = diskCache.get(key);
                contained = snapshot != null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return contained;
    }

    public void putStream(InputStream inStream, String key) {
        DiskLruCache.Editor editor = null;
        try {
            editor = diskCache.edit(key);
            if (editor == null) {
                return;
            }
            if (writeStreamToFile(inStream, editor)) {
                diskCache.flush();
                editor.commit();
                Log.i(TAG, "stream put on disk cache " + key);
            } else {
                editor.abort();
                Log.e(TAG, "ERROR on: stream put on disk cache " + key);
            }
        } catch (Exception e) {
            if (editor != null) {
                try {
                    editor.abort();
                } catch (IOException e1) {
                    Log.e(TAG, "Error closing cache editor..." + e.getMessage());
                }
            }
        }
    }

    private boolean writeStreamToFile(InputStream inStream, DiskLruCache.Editor editor) throws IOException {
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(editor.newOutputStream(CACHE_INDEX), IO_BUFFER_SIZE);
            byte data[] = new byte[IO_BUFFER_SIZE];
            int count;
            while ((count = inStream.read(data)) != -1) {
                out.write(data, 0, count);
            }
            return true;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public Bitmap getBitmap(String key) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {
            snapshot = diskCache.get(key);
            if (snapshot == null) {
                return null;
            }
            InputStream in = snapshot.getInputStream(CACHE_INDEX);
            if (in != null) {
                final BufferedInputStream buffIn = new BufferedInputStream(in, IO_BUFFER_SIZE);
                bitmap = BitmapFactory.decodeStream(buffIn);
                in.close();
                buffIn.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, bitmap == null ? "Image null on disk: " + key : "image read from disk: " + key);
        }
        return bitmap;

    }
}
