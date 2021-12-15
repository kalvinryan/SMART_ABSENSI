package com.example.smart_absensi.API;

import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.Login;
import com.example.smart_absensi.Model.Tpp;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<Login>LoginResponse(
            @Field("nip") String nip,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("absen")
    Call<Absen>AbsenResponse(
            @Field("nip") String nip,
            @Field("nama") String nama,
            @Field("jabatan") String jabatan,
            @Field("ruangan") String ruangan,
            @Field("jamMasuk") String jamMasuk,
            @Field("tanggal") String tanggal,
            @Field("keterangan") String keterangan
    );

    @FormUrlEncoded
    @PUT("absen")
    Call<Absen>absenUpdate(
            @Field("nip") String nip,
            @Field("jamKeluar") String jamKeluar,
            @Field("tanggal") String tanggal,
            @Field("keterangan") String keterangan
    );

    @GET("absen")
    Call<Absen>getabsenHadir(
            @Query("nip" )String nip,
            @Query("ruangan")String ruangan
            );

    @FormUrlEncoded
    @PUT("tpp")
    Call<Tpp>UpdateTpp(
            @Field("nip" ) String nip,
            @Field("tpp")String tpp
            );

}
