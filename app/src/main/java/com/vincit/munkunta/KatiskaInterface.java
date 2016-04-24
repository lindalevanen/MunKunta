package com.vincit.munkunta;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface KatiskaInterface {

    @GET("/municipalities/{id}/news")
    Call<List<NewsItem>> getNewsList(
            @Path("id") String municipalityId,
            @Query("limit") Integer limit,
            @Query("previous") String previous);
    
    @GET("/municipalities/{id}/news/{newsid}")
    Call<NewsItem> getNewsItem(
            @Path("id") String municipalityId,
            @Path("newsid") String newsId);

    @GET("/municipalities/{id}")
    Call<Municipality> getMunicipality(
            @Path("id") String municipalityId);

    @GET("/municipalities")
    Call<List<Municipality>> getMunicipalityList();
}
