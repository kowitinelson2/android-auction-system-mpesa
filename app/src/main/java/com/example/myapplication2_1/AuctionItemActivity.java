package com.example.myapplication2_1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import com.j256.ormlite.dao.CloseableIterable;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AuctionItemActivity extends OrmActivity implements OnClickListener, SubmitBidFragment.SubmitBidCallbacks {
    //specify an action when the button is pressed(OnClickListener)
    private AuctionItem auctionItem;
    private TextView timerText;
    private Button btnBidNow;
    private BidCloseTimer bidCloseTimer;
    private Bid higestBid;
    private Bid userBid;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();

        setContentView(R.layout.activity_auction_item);
        setupToolbar();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        long itemId = getIntent().getLongExtra(Constants.EXTRA_ITEM_ID, -1);
        auctionItem = getDBHelper().getItemRuntimeDao().queryForId(itemId);
        if (auctionItem == null) {
            finish();
        } else {

            try {
                User user = getUser();
                higestBid = getDBHelper().getHighestBid(itemId);
                if (higestBid != null && higestBid.getBidder().getId() == user.getId()) {
                    userBid = higestBid;
                } else {
                     userBid = getDBHelper().getUserBid(itemId, user.getId());
                }
            } catch (SQLException ex) {
                higestBid = null;
                Log.e(TAG, "## -- > " + ex);
            }
            btnBidNow = findViewById(R.id.btnBidNow);
            btnBidNow.setOnClickListener(this);
            TextView textView = findViewById(R.id.tvTitle);
            textView.setText(auctionItem.getTitle());
            textView = findViewById(R.id.tvDescription);
            textView.setText(auctionItem.getDescription());
            textView = findViewById(R.id.tvBasePrice);
            textView.setText(String.format("%.2f", auctionItem.getBasePrice()));
            ImageView imageView = findViewById(R.id.ivItem);
            Collection<ItemPhoto> itemPhotos = auctionItem.getItemPhotos();
            if (itemPhotos != null) {
                for (ItemPhoto photo : itemPhotos) {
                    imageLoader.displayImage(Uri.fromFile(new File(photo.getPath())).toString(), imageView, getDisplayOptions());
                    break;
                }
            }
            timerText = findViewById(R.id.tvEndTimer);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userBid == null) {
            btnBidNow.setEnabled(true);
            long millisInFuture = auctionItem.getBiddingClosesOn().getTime() - Calendar.getInstance().getTimeInMillis();
            bidCloseTimer = new BidCloseTimer(millisInFuture, 1000);
            bidCloseTimer.start();
        } else {
            btnBidNow.setEnabled(false);
            timerText.setText(String.format(getString(R.string.your_bid_amount), userBid.getQuotePrice()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bidCloseTimer != null) {
            bidCloseTimer.cancel();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnBidNow:
                SubmitBidFragment fragment = new SubmitBidFragment();
                Bundle arguments = new Bundle();
                arguments.putString(Constants.EXTRA_TITLE, auctionItem.getTitle());
                arguments.putDouble(Constants.EXTRA_BASE_AMOUNT, auctionItem.getBasePrice());
                if (higestBid != null) {
                    arguments.putDouble(Constants.EXTRA_HIGHEST_AMOUNT, higestBid.getQuotePrice());
                } else {
                    arguments.putDouble(Constants.EXTRA_HIGHEST_AMOUNT, 0.0);
                }
                fragment.setArguments(arguments);
                fragment.show(getSupportFragmentManager(), "FRG_SUBMIT_BID");
                break;
        }
    }

    @Override
    public void submitBid(double amount, String notes) {
        // create new bid from current user
        btnBidNow.setEnabled(false);
        Bid newBid = new Bid();
        newBid.setBidFor(auctionItem);
        newBid.setBidder(getUser());
        newBid.setQuotePrice(amount);
        newBid.setBidNotes(notes);
        if (getDBHelper() .getBidRuntimeDao().create(newBid) == 1) {
            userBid = higestBid = newBid;
            bidCloseTimer.cancel();
            timerText.setText(String.format(getString(R.string.your_bid_amount), userBid.getQuotePrice()));
            showToast(getString(R.string.bid_submitted));
        }else {
            btnBidNow.setEnabled(true);
            showToast(getString(R.string.bid_submit_error));
        }
    }

         class BidCloseTimer extends CountDownTimer {

            private final String endingFormat;

            public BidCloseTimer(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
                endingFormat = getString(R.string.auction_ends_in);
            }

            @Override
            public void onFinish() {
                btnBidNow.setEnabled(false);
                timerText.setText(R.string.auction_ended);
            }
//countdowntimer
            @Override
            //amount of time until finished
            public void onTick(long millisUntilFinished) {
                long h = millisUntilFinished / (60 * 60 * 1000);
                millisUntilFinished = millisUntilFinished % (60 * 60 * 1000);
                long m = millisUntilFinished / (60 * 1000);
                millisUntilFinished = millisUntilFinished % (60 * 1000);
                long s = millisUntilFinished / 1000;
                @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", h, m, s);
                timerText.setText(String.format(endingFormat, time));
            }
        }
    }
