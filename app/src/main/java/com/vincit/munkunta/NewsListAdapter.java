package com.vincit.munkunta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by harzza on 3/31/16.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.MyViewHolder>
{
    public String host = "http://vincit-mun-kunta-katiska-node.herokuapp.com";
    List<NewsItem> mNewsItems = new LinkedList<>();
    Context context;
    private int screenWidth;


    public NewsListAdapter(Context context, int width) {
        this.context = context;
        screenWidth = width;

    }

    public void addNewsItems(List<NewsItem> newsItems) {
        for(NewsItem item : newsItems) {
            mNewsItems.add(item);
        }
        this.notifyDataSetChanged();
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView header;
        public TextView date;
        public TextView description;
        public ImageView image;
        public View view;

        // We create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public MyViewHolder(View itemView, int viewType) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            view = itemView;

            //cases: 0) first with img 1) first without img 2) normal with img 3) normal without img
            switch(viewType) {
                case 0: {
                    header = (TextView) itemView.findViewById(R.id.newsListFirstItemWithImageHeader);
                    date = (TextView) itemView.findViewById(R.id.newsListFirstItemWithImageDate);
                    description = (TextView) itemView.findViewById(R.id.newsListFirstItemWithImageDescription);
                    image = (ImageView) itemView.findViewById(R.id.newsListFirstItemImage);
                    break;
                }
                case 1: {
                    header = (TextView) itemView.findViewById(R.id.newsListFirstItemWithoutImageHeader);
                    date = (TextView) itemView.findViewById(R.id.newsListFirstItemWithoutImageDate);
                    description = (TextView) itemView.findViewById(R.id.newsListFirstItemWithoutImageDescription);
                    break;
                }
                case 2: {
                    header = (TextView) itemView.findViewById(R.id.newsListItemWithImageHeader);
                    date = (TextView) itemView.findViewById(R.id.newsListItemWithImageDate);
                    description = (TextView) itemView.findViewById(R.id.newsListItemWithImageDescription);
                    image = (ImageView) itemView.findViewById(R.id.newsListItemImage);
                    break;
                }
                case 3: {
                    header = (TextView) itemView.findViewById(R.id.newsListItemWithoutImageHeader);
                    date = (TextView) itemView.findViewById(R.id.newsListItemWithoutImageDate);
                    break;
                }
                //in case something goes wrong with viewType number
                default: {
                    header = (TextView) itemView.findViewById(R.id.newsListItemWithoutImageHeader);
                    date = (TextView) itemView.findViewById(R.id.newsListItemWithoutImageDate);
                    break;
                }
            }
        }
    }

    /**
     * Get viewtype of a newsitem in given position
     * @param position
     * @return 0-3 depending on wether the newsItem is first/not first + with image / without image
     */
    @Override
    public int getItemViewType(int position) {

        NewsItem item = mNewsItems.get(position);
        //position 0 means first news item
        if(position == 0) {
            if (!item.getSummary().equals("") && item.getImages().size() != 0) {
                return 0;
            }
            else {
                return 1;
            }
        }
        else {
            if (!item.getSummary().equals("") && item.getImages().size() != 0) {
                return 2;
            }
            else {
                return 3;
            }
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        System.out.println(viewType);
        View newsView;
        switch(viewType){
            case 0: {
                newsView = inflater.inflate(R.layout.list_item_news_list_first_with_image, parent, false);
                break;
            }
            case 1: {
                newsView = inflater.inflate(R.layout.list_item_news_list_first_without_image, parent, false);
                break;
            }
            case 2: {
                newsView = inflater.inflate(R.layout.list_item_news_list_with_image, parent, false);
                break;
            }
            case 3: {
                newsView = inflater.inflate(R.layout.list_item_news_list_without_image, parent, false);
                break;
            }
            default: {
                newsView = inflater.inflate(R.layout.list_item_news_list_without_image, parent, false);
                break;
            }
        }

        //newsView = inflater.inflate(R.layout.list_item_news_list_first_with_image, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(newsView, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NewsItem item = mNewsItems.get(position);
        int viewType = getItemViewType(position);
        //set ID as tag, will be used when moving to single news view
        holder.view.setTag(item.getId());

        switch(viewType) {
            case 0: {
                holder.header.setText(item.getTitle());
                holder.date.setText(item.getDate());
                holder.description.setText(cutDescription(item.getSummary()));
                String image = host + item.getImages().get(0).replaceAll("size", "ORIGINAL");
                Picasso.with(context)
                        .load(image)
                        .into(holder.image);
                holder.image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
            case 1: {
                holder.header.setText(item.getTitle());
                holder.date.setText(item.getDate());
                holder.description.setText(cutDescription(item.getSummary()));
            }
            case 2: {
                holder.header.setText(item.getTitle());
                holder.date.setText(item.getDate());
                holder.description.setText(cutDescription(item.getSummary()));
                String image = host + item.getImages().get(0).replaceAll("size", "ORIGINAL");
                Picasso.with(context)
                        .load(image)
                        .resize(screenWidth / 3, (int) context.getResources().getDimension(R.dimen.max_small_img_height))
                        .centerCrop()
                        .into(holder.image);
            }
            case 3: {
                holder.header.setText(item.getTitle());
                holder.date.setText(item.getDate());
            }
        }
        setListener(holder.view);
    }

    /**
     * If the description is more than 100 characters, cuts it to be <100 and adds "..." to end
     *
     * @param description the description to be cut
     * @return cut version of the description
     */
    private String cutDescription(String description){
        if (description.length() > 100) {
            boolean spaceFound = false;
            Integer index = 101;
            while (!spaceFound) {
                index -= 1;
                if (description.charAt(index) == ' ') {
                    spaceFound = true;
                }
            }
            return (description.substring(0, index + 1) + "...");
        } else {
            return description;
        }
    }

    View tempLO;

    /**
     * Sets a click-listener to the layout given as a parameter
     * and starts newsItemActivity with the layout's tag
     * @param lo The layout the listener is set to
     */

    public void setListener(View lo) {
        lo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Darken the news item background color when pressed
                v.setBackgroundColor(Color.parseColor("#e6e6e6"));

                //Start the newsItemActivity with a key that describes the the item
                Intent intent = new Intent(context.getApplicationContext(), NewsItemActivity.class);
                Bundle b = new Bundle();
                b.putString("key", v.getTag().toString());
                intent.putExtras(b);
                context.startActivity(intent);

                //Set the news item background color back to transparent after 0,5s
                tempLO = v;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tempLO.setBackgroundColor(Color.TRANSPARENT);
                    }
                }, 500);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNewsItems.size();
    }

}
