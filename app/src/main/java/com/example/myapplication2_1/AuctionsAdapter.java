package com.example.myapplication2_1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class AuctionsAdapter extends BaseAdapter {
    private final List<AuctionItem> auctions;
    private final LayoutInflater inflater;
    private final String auctionEndsFormat;
    private final DisplayImageOptions displayImageOptions;
    private final ImageLoader imageLoader;
    Context context;
    public AuctionsAdapter(Context context,  List<AuctionItem> items, DisplayImageOptions displayImageOptions) {
        this.auctions = items;
        this.displayImageOptions = displayImageOptions;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        auctionEndsFormat = context.getString(R.string.bidding_ends_on);



        //imageLoader.displayImage(models.getImg(), holder.imageView);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }



    @Override
    public int getCount() {
        return auctions.size();
    }

    @Override
    public Object getItem(int position) {
        return auctions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return auctions.get(position).getId();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AuctionItem item = (AuctionItem) getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.auction_list_item, null);
            holder = new ViewHolder();
            holder.itemImage = convertView.findViewById(R.id.ivItem);
            holder.tvTitle = convertView.findViewById(R.id.itemTitle);
            holder.tvDesc = convertView.findViewById(R.id.itemDesc);
            holder.tvEndsOn = convertView.findViewById(R.id.itemAuctionEnd);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Collection<ItemPhoto> itemPhotos = item.getItemPhotos();
        if(itemPhotos != null) {
            Iterator<ItemPhoto> iterator = itemPhotos.iterator();
            while(iterator.hasNext()) {
                ItemPhoto photo = iterator.next();
               // ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
                imageLoader.displayImage(Uri.fromFile(new File(photo.getPath())).toString(), holder.itemImage, displayImageOptions);
                break;
            }
        }
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());
        holder.tvEndsOn.setText(String.format(auctionEndsFormat, Utils.getFormattedDate(item.getBiddingClosesOn())));
        convertView.setTag(holder);
        return convertView;
    }

     static class ViewHolder {
        ImageView itemImage;
        TextView tvTitle, tvDesc, tvEndsOn;
    }
}


