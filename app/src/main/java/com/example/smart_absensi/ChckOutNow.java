package com.example.smart_absensi;

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


public class ChckOutNow extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 1888;
    private static final int CAMERA_REQUEST = 1;
    private static final int REQUEST_PICK_FOTO = 2;
    private static final int REQUEST_WRITE_PERMISION = 786;
    ImageView imgGMB;
//    Button btn_ambilGambar,btn_chckInNow;
    ApiInterface apiInterface;
    Bitmap bitmap;

    private String mediaPath;
    private String postPath;
    String nip,nama,jabatan,ruangan,latitude,longitude,jamMasuk,tanggal,keterangan,tpp,chck,jamKeluar;
    final int TAKE_PHOTO_REQ = 100;
    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    final String formattedDate = df.format(c.getTime());
//    String file_path = Environment.getExternalStorageDirectory()
//            + "/"+formattedDate+".png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chck_out_now);
        Intent data = getIntent();

        imgGMB = findViewById(R.id.img_gambar2);

        nip = data.getStringExtra("nip");
        nama = data.getStringExtra("nama");
        jabatan = data.getStringExtra("jabatan");
        ruangan = data.getStringExtra("ruangan");
        jamMasuk = data.getStringExtra("jamMasuk");
        jamKeluar = data.getStringExtra("jamKeluar");
        latitude = data.getStringExtra("latitude");
        longitude = data.getStringExtra("longitude");
        tanggal = data.getStringExtra("tanggal");
        keterangan = data.getStringExtra("keterangan");
        tpp = data.getStringExtra("tpp");
        chck = data.getStringExtra("chekOut");
        Toast.makeText(this, "Jam keluar "+jamKeluar, Toast.LENGTH_SHORT).show();


//        Toast.makeText(ChckOutNow.this, "Data : "+jamKeluar+keterangan+tanggal, Toast.LENGTH_SHORT).show();

        imgGMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
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
                String file_path = Environment.getExternalStorageDirectory()
                    + "/"+nip+formattedDate+".png";
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

                    if (f ==null){
                        Toast.makeText(this, "Foto Kosong", Toast.LENGTH_SHORT).show();
                    }
                    else{
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-file"), f);
                            MultipartBody.Part partImage = MultipartBody.Part.createFormData("image", f.getAbsolutePath(), requestBody);
                            apiInterface = APIClient.getClient().create(ApiInterface.class);
                            Call<Absen> absen = apiInterface.absenUpdate(
                                    RequestBody.create(MediaType.parse("text/plain"), nip)
                                    , RequestBody.create(MediaType.parse("text/plain"), jamKeluar)
                                    , RequestBody.create(MediaType.parse("text/plain"), tanggal)
                                    , RequestBody.create(MediaType.parse("text/plain"), keterangan)
                                    , partImage);
                            absen.enqueue(new Callback<Absen>() {
                                @Override
                                public void onResponse(Call<Absen> call, Response<Absen> response) {
                                    if (response.body() != null && response.isSuccessful()) {
                                        if (response.body().isStatus()==true) {
//                                            Toast.makeText(ChckOutNow.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChckOutNow.this, SplashCheckOut.class);
                                            startActivity(intent);
                                        }else{
                                            Toast.makeText(ChckOutNow.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
//                                        Toast.makeText(ChckOutNow.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onFailure(Call<Absen> call, Throwable t) {
                                    Toast.makeText(ChckOutNow.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT);
                                }
                            });


                    }


              }else {
                Intent intent = new Intent(ChckOutNow.this,ChckOutNow.class);
                startActivity(intent);
                }
            }catch (Exception e){
                Log.d("TAG", "onActivityResult: " + e.toString());
            }



        }
        else{
            Intent intent = new Intent(ChckOutNow.this,CheckInNow.class);
            startActivity(intent);
        }
    }


     public Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DeKa");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e("Monitoring", "Oops! Failed create Monitoring directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_DeKa_" + timeStamp + ".jpg");

        return mediaFile;
    }

}
