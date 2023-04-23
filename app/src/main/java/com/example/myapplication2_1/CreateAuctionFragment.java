package com.example.myapplication2_1;

import static com.example.myapplication2_1.Constants.RQ_TAKE_PHOTO;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.view.View.OnClickListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.app.DatePickerDialog.OnDateSetListener;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContract;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CreateAuctionFragment extends PhotoFragment implements OnClickListener {
    private AuctionItem auctionItem;
    private List<String> pics;
    private int mYear, mMonth, mDay, mHour, mMinute;

    private EditText etTitle, etDescription, etBasePrice, etAuctionDate, etAuctionTime;
    private Spinner spAuctionClosingIn;
    private ImageView imageView;
    private CreateAuctionCallbacks createAuctionCallbacks;



    public static interface CreateAuctionCallbacks {
        void createAuction(AuctionItem auctionItem, List<String> photos);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CreateAuctionCallbacks) {
            createAuctionCallbacks = (CreateAuctionCallbacks) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        createAuctionCallbacks = null;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auctionItem = new AuctionItem();
        pics = new ArrayList<>();

//        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//            @Override
//            public void onActivityResult(ActivityResult activityResult) {
//                int result = activityResult.getResultCode();
//                Intent data = activityResult.getData();
//
//                if (resultCode == Activity.RESULT_OK) {
//                    if (requestCode == Constants.RQ_TAKE_PHOTO) {
//                        // for now only one image
//                        pics = new ArrayList<>();
//                        pics.add(mCurrentPhotoPath);
//                        showImage(imageView, mCurrentPhotoPath);
//                    }
//                }
//
//
//                }
//
//
//        });

//        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireActivity().getPackageName()));
//        activityResultLauncher.launch(intent);

        }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View rootView = inflater.inflate(R.layout.fragment_create_auction, null);
        etTitle = rootView.findViewById(R.id.etTitle);
        etDescription = rootView.findViewById(R.id.etDescription);
        etBasePrice = rootView.findViewById(R.id.etBasePrice);
        etAuctionDate = rootView.findViewById(R.id.etBidStartsAtDate);
        etAuctionDate.setOnClickListener(this);
        etAuctionTime = rootView.findViewById(R.id.etBidStartsAtTime);
        etAuctionTime.setOnClickListener(this);
        spAuctionClosingIn = rootView.findViewById(R.id.spBidClosing);
        imageView = rootView.findViewById(R.id.ivItem);
        rootView.findViewById(R.id.ibTakePhoto).setOnClickListener(this);
        rootView.findViewById(R.id.btnSubmitAuction).setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateTitle(getString(R.string.title_create_auction));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RQ_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            pics = new ArrayList<>();
                pics.add(mCurrentPhotoPath);
            showImage(imageView, mCurrentPhotoPath);
        }
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etBidStartsAtDate:
                showDatePicker();
                break;
            case R.id.etBidStartsAtTime:
                showTimePicker();
                break;
            case R.id.ibTakePhoto:
                takePicture(Constants.RQ_TAKE_PHOTO);
                break;
            case R.id.btnSubmitAuction:
                saveAuction();
                break;
        }
    }

    private void saveAuction() {
        auctionItem.setOwner(getUser());
        auctionItem.setTitle(etTitle.getText().toString().trim());
        auctionItem.setDescription(etDescription.getText().toString().trim());
        String basePrice = etBasePrice.getText().toString().trim();
        if (basePrice.length() == 0) {
           // basePrice = "0.0";
            return;
        }
        auctionItem.setBasePrice(Double.parseDouble(basePrice));
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        if (mYear == 0 || mMonth == 0 || mDay == 0) {
            showToast(getString(R.string.select_auction_date));
            return;
        }
        calendar.set(mYear, mMonth, mDay, mHour, mMinute);
        Date auctionStart = calendar.getTime();
        auctionItem.setBiddingStartOn(auctionStart);
        int hours = Integer.parseInt(spAuctionClosingIn.getSelectedItem().toString());
        calendar.add(Calendar.MINUTE, hours);
        Date auctionClose = calendar.getTime();
        auctionItem.setBiddingClosesOn(auctionClose);
        if (auctionItem.getTitle().length() == 0) {
            showToast(getString(R.string.enter_auction_title));
        } else if (auctionItem.getDescription().length() == 0) {
            showToast(getString(R.string.enter_auction_description));
        } else if (auctionItem.getBasePrice() <= 0.0) {
            showToast(getString(R.string.enter_base_price));
        } else if (createAuctionCallbacks != null) {
            createAuctionCallbacks.createAuction(auctionItem, pics);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        dateDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timeDialog = new TimePickerDialog(getActivity(), timeSetListener, hour, minute, false);
        timeDialog.show();
    }

    private final TimePickerDialog.OnTimeSetListener timeSetListener = new OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
            etAuctionTime.setText(String.format("%s:%s", mHour < 10 ? "0" + mHour : "" + mHour, mMinute < 10 ? "0" + mMinute : "" + mMinute));
        }
    };

    private final DatePickerDialog.OnDateSetListener dateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear + 1;
            mDay = dayOfMonth;
            etAuctionDate.setText(String.format("%s-%s-%s", mYear, mMonth < 10 ? "0" + mMonth : "" + mMonth, mDay < 10 ? "0" + mDay : "" + mDay));
            mMonth = monthOfYear;
        }
    };

}
