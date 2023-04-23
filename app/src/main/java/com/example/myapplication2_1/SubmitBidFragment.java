package com.example.myapplication2_1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Objects;

public class SubmitBidFragment extends DialogFragment implements View.OnClickListener {
    private SubmitBidCallbacks bidCallbacks;
    private TextView tvBaseAmount, tvHighestAmount;
    private double baseAmount, highestAmount;
    private EditText etYourAmount, etBidNotes;

    public interface SubmitBidCallbacks {
        void submitBid(double amount, String notes);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity){
            a=(Activity) context;
        }
        if (context instanceof SubmitBidCallbacks) {
            bidCallbacks = (SubmitBidCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        bidCallbacks = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View rootView = inflater.inflate(R.layout.dialog_submit_bid, null);
        tvBaseAmount = rootView.findViewById(R.id.tvBasePrice);
        tvHighestAmount = rootView.findViewById(R.id.tvHighestBid);
        etYourAmount = rootView.findViewById(R.id.etUserBid);
        etBidNotes = rootView.findViewById(R.id.etBidNotes);
        rootView.findViewById(R.id.btnSubmit).setOnClickListener(this);
        return rootView;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        assert arguments != null;
        String title = arguments.getString(Constants.EXTRA_TITLE, "N/A");
        baseAmount = arguments.getDouble(Constants.EXTRA_BASE_AMOUNT, 0.0);
        highestAmount = arguments.getDouble(Constants.EXTRA_HIGHEST_AMOUNT, 0.0);
        tvBaseAmount.setText(String.format("%.2f", baseAmount));
        if (highestAmount > 0.0) {
            tvHighestAmount.setText(String.format("%.2f", highestAmount));
        } else {
            tvHighestAmount.setText(R.string.no_bids);
        }
        Objects.requireNonNull(getDialog()).setTitle(String.format(getString(R.string.bidding_for), title));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                if (bidCallbacks != null) {
                    String strAmount = etYourAmount.getText().toString().trim();
                    if (strAmount.equals("")) {
                        strAmount = "0.0";
                    }
                    double amount = Double.parseDouble(strAmount);
                    if (amount > baseAmount) {
                        if (amount > highestAmount) {
                            bidCallbacks.submitBid(amount, etBidNotes.getText().toString().trim());
                            Objects.requireNonNull(getDialog()).dismiss();
                        } else {
                            Toast.makeText(getActivity(), R.string.cant_bid_less, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.more_than_base_amount, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

}
