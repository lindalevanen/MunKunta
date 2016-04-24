package com.vincit.munkunta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class NewsItem {

    private Integer municipality;
    private String id;
    private String title;
    private String date;
    private String summary;
    private List<String> images = new ArrayList<String>();
    private List<String> content = new ArrayList<String>();

    /**
     *
     * @return
     * The municipality
     */
    public Integer getMunicipality() {
        return municipality;
    }

    /**
     *
     * @return
     * The id
     */

    public String getId() {
        return id;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @return
     * The date
     */
    public String getDate() {
        Date uDate = new Date(Integer.valueOf(date)*1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return sdf.format(uDate);
    }

    public String getUnixDate() {
        String uDate = date;
        return uDate;
    }

    /**
     *
     * @return
     * The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     *
     * @return
     * The images
     */
    public List<String> getImages() {
        return images;
    }

    /**
     *
     * @return
     * The content
     */
    public List<String> getContent() {
        return content;
    }

}