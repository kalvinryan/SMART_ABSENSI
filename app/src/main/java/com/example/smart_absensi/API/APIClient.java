package com.example.smart_absensi.API;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
//    private static final String BASE_URL="https://smartabsensisetda.000webhostapp.com/api/";
    private static final String BASE_URL="http://192.168.43.100/ci-absensi/api/";

    private static Retrofit retrofit;

    public static Retrofit getClient(){
        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
