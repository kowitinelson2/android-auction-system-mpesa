package com.example.myapplication2_1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;

public class LoginFragment extends BaseFragment implements OnClickListener {
    private LoginCallbacks loginCallbacks;
    private EditText etUsername, etPassword;
    private CheckBox rememberMe;

    public interface LoginCallbacks {
        void login(String username, String password, boolean remember);

        void showSignup();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof LoginCallbacks) {
            loginCallbacks = (LoginCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View rootView = inflater.inflate(R.layout.fragment_login, null);
        etUsername = rootView.findViewById(R.id.et_username);
        etPassword = rootView.findViewById(R.id.et_password);
        rememberMe = rootView.findViewById(R.id.remember_me);
        rootView.findViewById(R.id.btnLogin).setOnClickListener(this);
        rootView.findViewById(R.id.btnSignup).setOnClickListener(this);
        return rootView;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (loginCallbacks != null) {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    if (username.length() == 0) {
                        showToast(getString(R.string.empty_username));
                        etUsername.requestFocus();
                    } else if (password.length() == 0) {
                        showToast(getString(R.string.empty_password));
                        etPassword.requestFocus();
                    } else {
                        loginCallbacks.login(username, password, rememberMe.isChecked());
                    }
                } else {
                    showToast(getString(R.string.app_error));
                }
                break;
            case R.id.btnSignup:
                if (loginCallbacks != null) {
                    loginCallbacks.showSignup();
                }
                break;
        }
    }

}
