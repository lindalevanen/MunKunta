package com.vincit.munkunta;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vincit.munkunta.HelperMethods.*;

public class NewsListActivity extends AppCompatActivity {

    Municipality activeMuni;
    public String municipality = MainViewActivity.activeMunicipality.getId().toString();
    private int screenWidth;
    private int screenHeigth;
    private ProgressBar progressBar;
    private List<NewsItem> allNewsItems = new LinkedList<>();

    private int newsThreshold = 8; //how many newsItems to fetch at once
    private String lastNewsDate; //date of the oldest news this far

    private RecyclerView recyclerView;


    /**
     * Creates the newslist view and fetches the news items
     * @param savedInstanceState The saved state of the application.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        activeMuni = MainViewActivity.activeMunicipality;

        //get screen width and heigth
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeigth = size.y;

        changeStatusBarColor(this, activeMuni);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(activeMuni.getColor())));

        //configure the RecyclerView
        recyclerView = (RecyclerView) findViewById(R.id.newsListLayout);
        final NewsListAdapter adapter = new NewsListAdapter(this, screenWidth);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                fetchMoreItems(adapter);
            }
        });

        //fetching the first set of news
        KatiskaInterface katiska = ((MunKunta) getApplication()).getKatiska();
        Call<List<NewsItem>> call = katiska.getNewsList(municipality, newsThreshold, null);
        call.enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Response<List<NewsItem>> response) {
                if (response.isSuccess()) {
                    List<NewsItem> newsItems = response.body();
                    allNewsItems.addAll(newsItems);
                    adapter.addNewsItems(newsItems);
                } else {
                    //error
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }

    /**
     * Display an indeterminate progress bar (spinning wheel) as next child of given layout.
     * Second parameter centered can be used to center the wheel on blank view/layout.
     *
     * @param layout parent layout
     * @param centered Boolean for centering the ProgressBar
     */

    /*
    private void addProgressBar(RecyclerView layout, Boolean centered) {
        progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(centered) {
            layoutParams.setMargins(0, screenHeigth/3, 0, 0);
        }
        progressBar.setLayoutParams(layoutParams);
        layout.addView(progressBar);
    }
    */
    /**
     * Remove the progressBar from the newsListLayout.
     * There should never exist more than one progressBar in the layout.
     * If there's no ProgressBar to be removed, does nothing.
     */
    /*
    private void removeProgressBar() {
        recyclerView.removeView(progressBar);
    }
    */

    /**
     * Fetch more items to the newsListLayout.
     * isLoading is set to true while the call starts and set to false after the items have been drawn.
     */

    public void fetchMoreItems(final NewsListAdapter adapter) {
        lastNewsDate = allNewsItems.get(allNewsItems.size()-1).getUnixDate();
        KatiskaInterface katiska = ((MunKunta) getApplication()).getKatiska();
        Call<List<NewsItem>> call = katiska.getNewsList(municipality, newsThreshold, lastNewsDate);
        call.enqueue(new Callback<List<NewsItem>>() {
            @Override
            public void onResponse(Response<List<NewsItem>> response) {
                if (response.isSuccess()) {
                    List<NewsItem> items = response.body();
                    allNewsItems.addAll(items);
                    adapter.addNewsItems(items);
                    adapter.notifyDataSetChanged();

                } else {
                    //error TODO: error handling
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }


}