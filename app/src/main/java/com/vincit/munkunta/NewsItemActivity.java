package com.vincit.munkunta;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import static com.vincit.munkunta.HelperMethods.*;

public class NewsItemActivity extends AppCompatActivity {

    Municipality activeMuni;
    public List<String> newsContent = new ArrayList<>();
    public String host2 = "http://vincit-mun-kunta-katiska-node.herokuapp.com";
    public ProgressBar progressBar;
    private int screenWidth;
    private int screenHeigth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_item);

        activeMuni = MainViewActivity.activeMunicipality;

        changeStatusBarColor(this, activeMuni);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(activeMuni.getColor())));

        //get screen width and heigth
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeigth = size.y;

        addProgressBar((LinearLayout) findViewById(R.id.allLayout), true);

        Bundle b = getIntent().getExtras();

        //This is the newsItems id, with this the content is to be fetched
        String newsItemId = b.getString("key");

        KatiskaInterface katiska = ((MunKunta) getApplication()).getKatiska();
        Call<NewsItem> callItem = katiska.getNewsItem(activeMuni.getId().toString(), newsItemId);
        callItem.enqueue(new Callback<NewsItem>() {
            @Override
            public void onResponse(Response<NewsItem> response) {
                if (response.isSuccess()) {
                    NewsItem item = response.body();
                    removeProgressBar();
                    draw(item);

                } else {
                    KatiskaError error = ((MunKunta) getApplication())
                            .parseKError(response.errorBody());
                    Log.e("KatiskaError", error.getMessage());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    /**
     * Draws the whole view for a single news item (title, date, images/text)
     * @param item NewsItem containing all the data to draw the news view
     */

    private void draw(NewsItem item) {

        //Initializing the in content created layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.allLayout);

        drawTitle(layout, item);
        drawDate(layout, item);

        //Adding the images to content
        for (int i = 0; i < item.getImages().size(); i++) {
            newsContent.add(item.getImages().get(i));
        }

        //Adding the content to content
        for (int i = 0; i < item.getContent().size(); i++) {
            newsContent.add(item.getContent().get(i));
        }

        // TODO: do something about this, shoudlnt be necessary to add everything in newsContent
        //Drawing the content
        for (int i = 0; i < newsContent.size(); i++) {
            String contentURL = host2 + newsContent.get(i).replaceAll("\\{size\\}", "%7BORIGINAL%7D");
            if (validURL(contentURL)) {
                drawImage(layout, contentURL);
            } else {
                drawHtml(layout, i);
            }
        }
    }

    /**
     * Display an indeterminate progress bar (spinning wheel) as next child of given layout.
     * Second parameter centered can be used to center the wheel on blank view/layout.
     *
     * @param layout parent layout
     * @param centered Boolean for centering the ProgressBar
     */

    private void addProgressBar(LinearLayout layout, Boolean centered) {
        progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if(centered) {
            layoutParams.setMargins(0, screenHeigth/3, 0, 0);
        }
        progressBar.setLayoutParams(layoutParams);
        layout.addView(progressBar);
    }

    /**
     * Remove the progressBar from the layout.
     * There should never exist more than one progressBar in the layout.
     * If there's no ProgressBar to be removed, does nothing.
     */

    private void removeProgressBar() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.allLayout);
        layout.removeView(progressBar);
    }

    private void drawTitle(LinearLayout layout, NewsItem item) {
        TextView title = new TextView(this);
        title.setText(item.getTitle());

        //Styling the title
        //(text styling can't be in the styles.xml since typefaces can only be set in java code)
        //(style attributes can be fetched though from xml like done below)
        //(hopefully this won't be a problem in the future)
        title.setTextColor(Color.parseColor(activeMuni.getColor()));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, getResources().getInteger(R.integer.big_title_size));

        layout.addView(title);
    }

    private void drawDate(LinearLayout layout, NewsItem item) {
        TextView date = new TextView(this);
        //Initializing date's parameters
        LinearLayout.LayoutParams paramsD =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //Setting date's margins
        paramsD.setMargins(0, (int) getResources().getDimension(R.dimen.item_small_margin), 0, (int) getResources().getDimension(R.dimen.item_basic_margin));

        date.setText(item.getDate());
        date.setTextColor(ContextCompat.getColor(this, R.color.dateColor));

        layout.addView(date, paramsD);
    }

    /**
     * Adds the image(view) into the current layout
     * @param layout the layout the image is to be added
     * @param url image URL
     */
    private void drawImage(LinearLayout layout, String url) {
        ImageView image = new ImageView(this);
        LinearLayout.LayoutParams paramsC = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsC.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.item_basic_margin));
        image.setLayoutParams(paramsC);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Picasso.with(this)
                .load(url)
                .into(image);
        image.setMaxHeight((int) getResources().getDimension(R.dimen.max_big_img_height));
        layout.addView(image, paramsC);
    }

    /**
     * Adds the html view to current layout
     * @param layout the layout the view is to be added
     * @param i index number for newsContent
     */
    private void drawHtml(LinearLayout layout, int i) {
        WebView web = new WebView(this);
        web.loadDataWithBaseURL("file:///android_asset/",
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />" + newsContent.get(i),
                "text/html", "utf-8",
                null);
        web.setBackgroundColor(Color.TRANSPARENT);
        layout.addView(web);
    }




}
