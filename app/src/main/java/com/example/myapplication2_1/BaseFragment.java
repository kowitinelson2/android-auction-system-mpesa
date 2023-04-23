package com.example.myapplication2_1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.Objects;

public class BaseFragment extends Fragment {
    protected static final String TAG = BaseFragment.class.getSimpleName();
    protected ProgressDialog mProgressDialog;

    protected void showProgressDialog(String title, String message) {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            mProgressDialog = ProgressDialog.show(getActivity(), title, message);
        }
        catch (Exception ex) {
            Log.e(TAG, "##### --> " + ex);
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
            Log.e(TAG, "##### --> " + ex);
        }
    }

    protected DatabaseHelper getDBHelper() {
        DatabaseHelper dbHelper = null;
        Activity mActivity = getActivity();
        if (mActivity != null && mActivity instanceof OrmActivity) {
            dbHelper = ((OrmActivity) mActivity).getDBHelper();
        }
        return dbHelper;
    }

    protected DisplayImageOptions getDisplayOptions() {
        return new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.no_photo).showImageOnFail(R.drawable.no_photo).showImageOnLoading(R.drawable.no_photo).displayer(new FadeInBitmapDisplayer(500)).cacheOnDisk(true).handler(new Handler()).build();
    }

    protected User getUser() {
        User user = new User();
        PrefManager prefManager = new PrefManager(getActivity());
        user.setId(Long.parseLong(prefManager.readPreference(Constants.PREF_LOGGED_USER_ID, "-1")));
        return user;
    }

    protected void updateTitle(String title) {
        Activity mActivity = getActivity();
        if (mActivity != null && mActivity instanceof AppCompatActivity) {
            mActivity.setTitle(title);
            Objects.requireNonNull(((AppCompatActivity) mActivity).getSupportActionBar()).setTitle(title);
        }
    }

    protected void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}
