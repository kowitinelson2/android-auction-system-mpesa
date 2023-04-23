package com.example.myapplication2_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication2_1.Constants;
import com.example.myapplication2_1.ItemPhoto;
import com.example.myapplication2_1.AuctionItem;
import com.google.firebase.auth.FirebaseAuth;


import java.util.List;
import java.util.Objects;


public class HomeActivity extends OrmActivity implements CreateAuctionFragment.CreateAuctionCallbacks {
    private static final String FRG_CREATE_AUCTION = "FRG_CREATE_AUCTION";
    private static final String FRG_OPEN_AUCTIONS = "FRG_OPEN_AUCTIONS";
    private static final String FRG_AUCTION_ITEMS = "FRG_AUCTION_ITEMS";
    private static final String FRG_AUCTIONS_WON = "FRG_AUCTIONS_WON";

    protected DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected FrameLayout fragmentContainer;
    protected ListView mLeftDrawerView;
    protected String[] navItems;
    protected DrawerAdapter drawerAdapter;
    protected FragmentManager fragmentManager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupToolbar();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        fragmentContainer = mDrawerLayout.findViewById(R.id.fragment_container);
        fragmentManager = getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        navItems = getResources().getStringArray(R.array.nav_items);
        // drawer layout initialization
        mLeftDrawerView = findViewById(R.id.left_drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View drawerView) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getTitle());
                supportInvalidateOptionsMenu();
                mDrawerToggle.syncState();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
                supportInvalidateOptionsMenu();
                mDrawerToggle.syncState();
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        if (fragmentManager.findFragmentByTag(FRG_OPEN_AUCTIONS) == null) {
            showOpenAuctions();
        }
        populateDrawer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerToggle.onOptionsItemSelected(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void populateDrawer() {
        drawerAdapter = new DrawerAdapter();
        mLeftDrawerView.setAdapter(drawerAdapter);
        drawerAdapter.notifyDataSetChanged();
        mLeftDrawerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                mDrawerLayout.closeDrawer(mLeftDrawerView);
                switch (position) {
                    case 0:
                        fragmentManager.popBackStackImmediate();
                        break;
                    case 1:
                        showCreateAuction();
                        break;
                    case 2:
                        showMyAuctions();
                        break;
                    case 3:
                        showAuctionsWon();
                        break;
                    case 4:
                        logout();
                        break;
                }
            }
        });
    }

    private void showOpenAuctions() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        OpenAuctionsFragment fragment = new OpenAuctionsFragment();
        ft.replace(R.id.fragment_container, fragment, FRG_OPEN_AUCTIONS);
        ft.commit();
    }

    private void showCreateAuction() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        CreateAuctionFragment fragment = new CreateAuctionFragment();
        ft.replace(R.id.fragment_container, fragment, FRG_CREATE_AUCTION);
        ft.addToBackStack(FRG_CREATE_AUCTION);
        ft.commit();
    }

    private void showMyAuctions() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        com.example.myapplication2_1.AuctionItemsFragment myItemsFragment = new com.example.myapplication2_1.AuctionItemsFragment();
        ft.replace(R.id.fragment_container, myItemsFragment, FRG_AUCTION_ITEMS);
        ft.addToBackStack(FRG_AUCTION_ITEMS);
        ft.commit();
    }

    private void showAuctionsWon() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStackImmediate();
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        AuctionsWonFragment fragment = new AuctionsWonFragment();
        ft.replace(R.id.fragment_container, fragment, FRG_AUCTIONS_WON);
        ft.addToBackStack(FRG_AUCTIONS_WON);
        ft.commit();
    }

    private class DrawerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return navItems.length;
        }

        @Override
        public Object getItem(int i) {
            return navItems[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            String item = (String) getItem(i);
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.layout_drawer_item, null);
                holder = new ViewHolder();
                holder.text1 = convertView.findViewById(R.id.text1);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text1.setText(item);
            convertView.setTag(holder);
            return convertView;
        }

        class ViewHolder {
            TextView text1;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mLeftDrawerView)) {
            mDrawerLayout.closeDrawer(mLeftDrawerView);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void createAuction(AuctionItem auctionItem, List<String> photos) {
        if (getDBHelper().getItemRuntimeDao().create(auctionItem) == 1) {
            for (String path : photos) {
                ItemPhoto itemPhoto = new ItemPhoto();
                itemPhoto.setPath(path);
                itemPhoto.setAuctionItem(auctionItem);
                getDBHelper().getPhotoRuntimeDao().create(itemPhoto);
            }
            fragmentManager.popBackStackImmediate();
            Intent serviceIntent = new Intent(this, BidderService.class);
            serviceIntent.putExtra(Constants.EXTRA_ITEM_ID, auctionItem.getId());
            serviceIntent.putExtra(Constants.EXTRA_BASE_AMOUNT, auctionItem.getBasePrice());
            startService(serviceIntent);
            showToast(getString(R.string.auction_item_created));
        }
    }
}