package com.example.smart_absensi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Model.Absen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.smart_absensi.Model.Tpp;

public class CheckInNow extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1888;
    private static final int CAMERA_REQUEST = 1;
    private static final int REQUEST_PICK_FOTO = 2;
    private static final int REQUEST_WRITE_PERMISION = 786;
    ImageView imgGMB;
    Button btn_ambilGambar,btn_chckInNow;
    ApiInterface apiInterface;
    Bitmap bitmap;

    private String mediaPath;
    private String postPath;
    String nip,nama,jabatan,ruangan,latitude,longitude,jamMasuk,tanggal,keterangan,tpp;
    final int TAKE_PHOTO_REQ = 100;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    final String formattedDate = df.format(c.getTime());
    String file_path = Environment.getExternalStorageDirectory()
            + "/"+formattedDate+".png";



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Check_in();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_now);
        imgGMB = findViewById(R.id.img_gambar);

        Intent data = getIntent();

        nip = data.getStringExtra("nip");
        nama = data.getStringExtra("nama");
        jabatan = data.getStringExtra("jabatan");
        ruangan = data.getStringExtra("ruangan");
        jamMasuk = data.getStringExtra("jamMasuk");
        latitude = data.getStringExtra("latitude");
        longitude = data.getStringExtra("longitude");
        tanggal = data.getStringExtra("tanggal");
        keterangan = data.getStringExtra("keterangan");
        tpp = data.getStringExtra("tpp");

        imgGMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keterangan =="Terlambat"){
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                    TppPegawai(nip,tpp);
                }
                else{
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQUEST_CAMERA){
            try {
                if(data.getExtras() != null) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);

                File f = new File(file_path);
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // write the bytes in file
                FileOutputStream fo = null;
                try {
                    fo = new FileOutputStream(f);
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // remember close de FileOutput
                try {
                    fo.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.e("take-img", "Image Saved to sd card...");
                // Toast.makeText(getApplicationContext(),
                // "Image Saved to sd card...", Toast.LENGTH_SHORT).show();

                if (f == null) {
                    Toast.makeText(this, "Gagal,,", Toast.LENGTH_SHORT).show();
                } else {
                    //                File Imagefle = new File(postPath);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"), f);
                    MultipartBody.Part partImage = MultipartBody.Part.createFormData("image", f.getAbsolutePath(), requestBody);
                    apiInterface = APIClient.getClient().create(ApiInterface.class);
                    Call<Absen> absen = apiInterface.AbsenResponse(
                            RequestBody.create(MediaType.parse("text/plain"), nip)
                            , RequestBody.create(MediaType.parse("text/plain"), nama)
                            , RequestBody.create(MediaType.parse("text/plain"), jabatan)
                            , RequestBody.create(MediaType.parse("text/plain"), ruangan)
                            , RequestBody.create(MediaType.parse("text/plain"), latitude)
                            , RequestBody.create(MediaType.parse("text/plain"), longitude)
                            , RequestBody.create(MediaType.parse("text/plain"), jamMasuk)
                            , RequestBody.create(MediaType.parse("text/plain"), tanggal)
                            , RequestBody.create(MediaType.parse("text/plain"), keterangan)
                            , partImage);
                    absen.enqueue(new Callback<Absen>() {
                        @Override
                        public void onResponse(Call<Absen> call, Response<Absen> response) {
                            if (response.body() != null && response.isSuccessful()) {
                                Intent intent = new Intent(CheckInNow.this, SplashAbsen.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(CheckInNow.this, "Anda Sudah Check-in", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(Call<Absen> call, Throwable t) {
                            Toast.makeText(CheckInNow.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT);
                        }
                    });
                }
              }else {
                Intent intent = new Intent(CheckInNow.this,CheckInNow.class);
                startActivity(intent);
                }
            }catch (Exception e){
                Log.d("TAG", "onActivityResult: " + e.toString());
            }



        }
        else{
            Intent intent = new Intent(CheckInNow.this,CheckInNow.class);
            startActivity(intent);
        }
    }


        private void TppPegawai(String nip, String tpp) {
            apiInterface = APIClient.getClient().create(ApiInterface.class);
            Call<Tpp> UpdateTpp = apiInterface.UpdateTpp(nip, tpp);
            UpdateTpp.enqueue(new Callback<Tpp>() {
                @Override
                public void onResponse(Call<Tpp> call, Response<Tpp> response) {

                }

                @Override
                public void onFailure(Call<Tpp> call, Throwable t) {

                }
            });
        }

//        @Override
//        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            if (requestCode == permsRequestCode) {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(intent, REQUEST_CAMERA);
//                }
//            }
//        }

//    private void Check_in() {
//            if (mediaPath == null){
//                Toast.makeText(this, "Gagal,,", Toast.LENGTH_SHORT).show();
//            }else{
//                File Imagefle = new File(postPath);
//                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"),Imagefle);
//                MultipartBody.Part partImage = MultipartBody.Part.createFormData("image",Imagefle.getName(),requestBody);
//                apiInterface = APIClient.getClient().create(ApiInterface.class);
//                Call<Absen> absen = apiInterface.AbsenResponse(
//                         RequestBody.create(MediaType.parse("text/plain"),nip)
//                        ,RequestBody.create(MediaType.parse("text/plain"),nama)
//                        ,RequestBody.create(MediaType.parse("text/plain"),jabatan)
//                        ,RequestBody.create(MediaType.parse("text/plain"),ruangan)
//                        ,RequestBody.create(MediaType.parse("text/plain"),latitude)
//                        ,RequestBody.create(MediaType.parse("text/plain"),longitude)
//                        ,RequestBody.create(MediaType.parse("text/plain"),jamMasuk)
//                        ,RequestBody.create(MediaType.parse("text/plain"),tanggal)
//                        ,RequestBody.create(MediaType.parse("text/plain"),keterangan)
//                        ,partImage);
//                absen.enqueue(new Callback<Absen>() {
//                    @Override
//                    public void onResponse(Call<Absen> call, Response<Absen> response) {
//                        if (response.body() != null && response.isSuccessful()) {
//                            Intent intent = new Intent(CheckInNow.this, SplashAbsen.class);
//                            startActivity(intent);
//                        } else {
//                            Toast.makeText(CheckInNow.this, "Anda Sudah Check-in", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<Absen> call, Throwable t) {
//                        Toast.makeText(CheckInNow.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT);
//                    }
//                });
//            }
//
//    }
//
//    public Uri getOutputMediaFileUri() {
//        return Uri.fromFile(getOutputMediaFile());
//    }
//
//    private static File getOutputMediaFile() {
//
//        // External sdcard location
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DeKa");
//
//        // Create the storage directory if it does not exist
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
//                return null;
//            }
//        }
//
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        File mediaFile;
//        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_DeKa_" + timeStamp + ".jpg");
//
//        return mediaFile;
//    }
//
//    private void requestPermission(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_PERMISION);
//        }else{
//            Check_in();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                // API 5+ solution
//                onBackPressed();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


}