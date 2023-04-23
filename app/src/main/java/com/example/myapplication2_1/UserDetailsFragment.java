package com.example.myapplication2_1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class UserDetailsFragment extends DialogFragment {
    private TextView tvName, tvUsername, tvEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View rootView = inflater.inflate(R.layout.dialog_user_details, null);
        tvUsername = rootView.findViewById(R.id.tvUsername);
        tvName = rootView.findViewById(R.id.tvName);
        tvEmail = rootView.findViewById(R.id.tvEmail);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        assert arguments != null;
        User user = arguments.getParcelable(Constants.EXTRA_USER);
        tvUsername.setText(user.getUsername());
        tvName.setText(user.getFullName());
        tvEmail.setText(user.getEmail());
        Objects.requireNonNull(getDialog()).setTitle(R.string.title_user_details);
    }

}
