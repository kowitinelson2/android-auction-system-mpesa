package com.example.myapplication2_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AuctionItemsFragment extends BaseFragment {


    private TextView emptyText;
    private ListView listMyAuctions;
    private List<AuctionItem> auctionItems;
    private AuctionsAdapter auctionsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View rootView = inflater.inflate(R.layout.fragment_auction_items, null);
        emptyText = rootView.findViewById(R.id.empty);
        listMyAuctions = rootView.findViewById(R.id.listMyAuctions);
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateTitle(getString(R.string.title_my_auctions));
        DatabaseHelper dbHelper = getDBHelper();
        try {
            if (dbHelper != null) {
                auctionItems = dbHelper.getMyAuctions(getUser());
            }
        }
        catch (SQLException ex) {
            Log.e(TAG, "## --> " + ex);
        }
        showMyAuctions();
    }


        private void showMyAuctions () {
            if (auctionItems == null || auctionItems.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                listMyAuctions.setVisibility(View.GONE);
            } else {
                emptyText.setVisibility(View.GONE);
                listMyAuctions.setVisibility(View.VISIBLE);
                Context context = requireActivity().getApplicationContext();
                auctionsAdapter = new AuctionsAdapter(context, auctionItems, getDisplayOptions());
                listMyAuctions.setAdapter(auctionsAdapter);
                listMyAuctions.setOnItemClickListener(onItemClick);
            }
        }

        final AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), AuctionBidsActivity.class);
                AuctionItem auctionItem = auctionItems.get(position);
                intent.putExtra(Constants.EXTRA_ITEM_ID, auctionItem.getId());
                intent.putExtra(Constants.EXTRA_TITLE, auctionItem.getTitle());
                startActivity(intent);
            }
        };
    }



