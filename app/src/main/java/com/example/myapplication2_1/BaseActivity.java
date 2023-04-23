package com.example.myapplication2_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.os.Handler;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class BaseActivity extends AppCompatActivity {
    protected static final String TAG = BaseActivity.class.getSimpleName();
    protected ProgressDialog mProgressDialog;
    protected PrefManager prefManager;
    //used to help create preference hierarchies from activities or xml.
    protected Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
    }


    protected void setupToolbar() {
        toolbar = findViewById(R.id.custom_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    protected User getUser() {
        User user = new User();
        PrefManager prefManager = new PrefManager(this);
        user.setId(Long.parseLong(prefManager.readPreference(Constants.PREF_LOGGED_USER_ID, "-1")));
        return user;
    }

    protected void logout() {
        prefManager.clearAll();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.EXTRA_NO_SPLASH, true);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showProgressDialog(String title, String message) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = ProgressDialog.show(this, title, message);
        }
        catch (Exception ex) {
            Log.e(TAG, "## --> " + ex);
        }
    }

    protected void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = null;
        }
        catch (Exception ex) {
            Log.e(TAG, "## --> " + ex);
        }
    }

    protected DisplayImageOptions getDisplayOptions() {
        return new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.no_photo).showImageOnFail(R.drawable.no_photo).showImageOnLoading(R.drawable.no_photo).displayer(new FadeInBitmapDisplayer(500)).cacheOnDisk(true).handler(new Handler()).build();
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
