package com.example.myapplication2_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.annotation.NonNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AuctionsWonFragment extends BaseFragment {
    private TextView emptyText;
    private ListView listAuctionsWon;
    private List<AuctionItem> auctionItems;
    private AuctionsAdapter auctionsAdapter;
    Button buyNow;
    int totalQuantity = 1;
    int totalPrice = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View rootView = inflater.inflate(R.layout.fragment_auctions_won, null);
        emptyText = rootView.findViewById(R.id.empty);
        listAuctionsWon = rootView.findViewById(R.id.listAuctionsWon);
        buyNow = (Button) rootView.findViewById(R.id.buyNow);

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(view.getContext(), PaymentActivity.class);
               view.getContext().startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateTitle(getString(R.string.title_auctions_won));

        DatabaseHelper dbHelper=getDBHelper();
        try {
            if (dbHelper != null) {
                auctionItems = dbHelper.getAuctionsWon(getUser());
            }
        }
        catch (SQLException ex) {
            Log.e(TAG, "## --> " + ex);
        }
        showAuctionsWon();
    }

    private void showAuctionsWon() {
        if (auctionItems == null || auctionItems.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            listAuctionsWon.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            listAuctionsWon.setVisibility(View.VISIBLE);
            Context context = requireActivity().getApplicationContext();
            auctionsAdapter = new AuctionsAdapter(context, auctionItems, getDisplayOptions());
            listAuctionsWon.setAdapter(auctionsAdapter);
            listAuctionsWon.setOnItemClickListener(onItemClick);
        }
    }

    private final OnItemClickListener onItemClick = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AuctionItem auctionItem = auctionItems.get(position);
            UserDetailsFragment userFragment = new UserDetailsFragment();
            Bundle arguments = new Bundle();
            arguments.putParcelable(Constants.EXTRA_USER, (Parcelable) getOwner(auctionItem.getOwner().getId()));
            userFragment.setArguments(arguments);
            userFragment.show(getChildFragmentManager(), "FRG_USER_DETAILS");
        }
    };

    private User getOwner(long ownerId) {
        User user = null;
        DatabaseHelper dbHelper = getDBHelper();
        if (dbHelper != null) {
            user = dbHelper.getUserRuntimeDao().queryForId(ownerId);
        }
        return user;
    }


}
