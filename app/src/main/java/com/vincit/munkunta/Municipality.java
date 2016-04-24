package com.vincit.munkunta;

import android.graphics.Bitmap;

public class Municipality {

    private Integer id;
    private String name;
    private String img;
    private String highlightColor;
    private String highlightInvertedColor;
    private String baseUrl;
    private String secondaryColor;

    /**
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     * The img
     */
    public String getImg() { return img; }

    /**
     *
     * @return
     * The colors
     */
    public String getColor() {
        return highlightColor;
    }

    public String getIColor() {
        return highlightInvertedColor;
    }

    /**
     *
     * @return
     * The baseUrl
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getSColor() { return secondaryColor; }
}
