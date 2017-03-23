package com.gm.androidcodingexercise.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.gm.androidcodingexercise.Constants;
import com.gm.androidcodingexercise.R;
import com.gm.androidcodingexercise.controller.fragment.PictureGridFragment;


public class MainActivity extends ActionBarActivity {
    private static final String TAG = Constants.BASE_TAG + MainActivity.class.getSimpleName();

    private Fragment gridFragment = new PictureGridFragment();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, gridFragment).commit();
        }
    }

    @Override public void onBackPressed() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() == 0) super.onBackPressed();
        else manager.popBackStack();
    }
}
