package com.hammersmith.thetinhluok;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public class ApiClient {
    public static final String BASE_URL = "http://hammersmithconsultancy.com/online/";
//    public static final String BASE_URL = "http://192.168.1.114/online/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
