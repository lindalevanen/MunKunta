package com.vincit.munkunta;

import android.app.Application;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

public class MunKunta extends Application {
    private static MunKunta application;
    private KatiskaInterface service;
    private Converter<ResponseBody, KatiskaError> converter;
    private final String BASE_URL = "http://vincit-mun-kunta-katiska-node.herokuapp.com";

    public MunKunta getInstance(){
        return application;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        application = this;
        application.initialize();
    }

    private void initialize(){
          Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(KatiskaInterface.class);
        converter = retrofit.responseBodyConverter(KatiskaError.class, new Annotation[0]);
    }

    public KatiskaInterface getKatiska(){
        return service;
    }

    public KatiskaError parseKError(ResponseBody response) {
        KatiskaError error;
        try {
            error = converter.convert(response);
        } catch (IOException e) {
            return new KatiskaError();
        }
        return error;
    }
}
