package com.example.smart_absensi.API;

import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.Login;
import com.example.smart_absensi.Model.Tpp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<Login>LoginResponse(
            @Field("nip") String nip,
            @Field("password") String password
    );

//    @FormUrlEncoded
    @Multipart
    @POST("absen")
    Call<Absen>AbsenResponse(
            @Part("nip") RequestBody nip,
            @Part("nama") RequestBody nama,
            @Part("jabatan") RequestBody jabatan,
            @Part("ruangan") RequestBody ruangan,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("jamMasuk") RequestBody jamMasuk,
            @Part("tanggal") RequestBody tanggal,
            @Part("keterangan") RequestBody keterangan,
            @Part("tpp") RequestBody tpp,
            @Part MultipartBody.Part image
            );

    @FormUrlEncoded
    @POST("sekda")
    Call<Absen>IzinResponse(
            @Field("nip") String nip,
            @Field("nama") String nama,
            @Field("jabatan") String jabatan,
            @Field("ruangan") String ruangan,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("jamMasuk") String jamMasuk,
            @Field("tanggal") String tanggal,
            @Field("keterangan") String keterangan
            );

    @Multipart
    @POST("chckout")
    Call<Absen>absenUpdate(
            @Part("nip") RequestBody nip,
            @Part("jamKeluar") RequestBody jamKeluar,
            @Part("tanggal") RequestBody tanggal,
            @Part("keterangan") RequestBody keterangan,
            @Part MultipartBody.Part image2
    );

    @GET("libur")
    Call<Absen>getabsenlibur(
            );

    @GET("absen")
    Call<Absen>getabsenHadir(
            @Query("ruangan")String ruangan,
            @Query("jabatan")String jabatan
            );
    @GET("riwayat")
    Call<Absen>getabsenRiwayat(
            @Query("ruangan")String ruangan,
            @Query("nip")String nip,
            @Query("jabatan")String jabatan
            );
    @GET("sekda")
    Call<Absen>getabsenSekda(
            @Query("jabatan")String jabatan
            );

    @FormUrlEncoded
    @POST("riwayat")
    Call<Absen>getRiwayat(
            @Field("id") int id
            );

    @FormUrlEncoded
    @PUT("riwayat")
    Call<Absen>getRiwayatAbsen(
            @Field("id") String id,
            @Field("keterangan") String keterangan,
            @Field("flag") String flag
            );

    @FormUrlEncoded
    @PUT("tpp")
    Call<Tpp>UpdateTpp(
            @Field("nip" ) String nip,
            @Field("tpp")String tpp
            );
    @FormUrlEncoded
    @PUT("login")
    Call<Login>UpdatePassword(
            @Field("nip" ) String nip,
            @Field("password")String tpp
            );

}
