package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.Tpp;

import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextClock jamdigital;
    ImageButton btn_checkIn,btn_checkOut;
    ConstraintLayout btn_LogOut;
    TextView txt_Nama,txt_Nip,txt_tanggal,txtHadir,txtSakit,txtIzin,txtAlfa,txtTpp;
    SessionManager sessionManager;
    String nip,nama,jabatan,ruangan,Tpp,jamMasuk,jamKeluar,tanggal,keterangan1,keterangan2;

    ApiInterface apiInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(MainActivity.this);
        if(!sessionManager.isLoggedIn()){
            moveToLogin();
        }
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => "+c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        String currentTime = new SimpleDateFormat("HH.mm", Locale.getDefault()).format(new Date());

        jamdigital=findViewById(R.id.txt_clock);
        txt_tanggal = findViewById(R.id.txtTanggal);
        txt_Nip = findViewById(R.id.txtNip);
        txt_Nama = findViewById(R.id.txtNama);
        txtHadir = findViewById(R.id.txtHadir);
        txtSakit = findViewById(R.id.txtSakit);
        txtIzin = findViewById(R.id.txtIzin);
        txtAlfa = findViewById(R.id.txtAlfa);
        txtTpp = findViewById(R.id.txtTpp);


        txt_tanggal.setText(formattedDate+", ");

        btn_LogOut=findViewById(R.id.btn_LogOut);
        btn_checkIn=findViewById(R.id.btn_Check_in);
        btn_checkOut=findViewById(R.id.btn_CheckOut);
        btn_checkOut.setOnClickListener(this);
        btn_LogOut.setOnClickListener(this);
        btn_checkIn.setOnClickListener(this);

        nip=sessionManager.getUserDetail().get(SessionManager.NIP);
        nama= sessionManager.getUserDetail().get(SessionManager.Nama);
        ruangan=sessionManager.getUserDetail().get(SessionManager.Ruangan);
        Tpp=sessionManager.getUserDetail().get(SessionManager.Tpp);

        Locale LocaleID = new Locale("in","ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(LocaleID);
        numberAbsen(nip,ruangan);
//        DataTpp(nip);
        int myTPP = Integer.parseInt(Tpp);

        txt_Nip.setText(nip);
        txt_Nama.setText("Selamat Beraktivitas, "+nama);
        txtTpp.setText(formatRupiah.format(myTPP));


    }


    private void numberAbsen(String nip,String ruangan) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen>numberHadir = apiInterface.getabsenHadir(nip,ruangan);
        numberHadir.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                txtHadir.setText(response.body().getHadir());
                txtSakit.setText(response.body().getSakit());
                txtIzin.setText(response.body().getIzin());
                txtAlfa.setText(response.body().getAlfa());
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT);
            }
        });
    }

    private void moveToLogin() {
        Intent intent = new Intent(MainActivity.this,LoginAbsen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_LogOut:
                 sessionManager.logoutSession();
                 moveToLogin();
                break;
            case R.id.btn_Check_in:
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());
                String currentTime = new SimpleDateFormat("kkmm", Locale.getDefault()).format(new Date());
                String currentTime2 = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                nip=sessionManager.getUserDetail().get(SessionManager.NIP);
                nama= sessionManager.getUserDetail().get(SessionManager.Nama);
                jabatan= sessionManager.getUserDetail().get(SessionManager.Jabatan);
                ruangan= sessionManager.getUserDetail().get(SessionManager.Ruangan);
                Tpp= sessionManager.getUserDetail().get(SessionManager.Tpp);
                int MyTpp = Integer.parseInt(Tpp);
                jamMasuk = currentTime2;
                tanggal= formattedDate;
                keterangan1 ="On Working";
                keterangan2 ="Terlambat";

                String time1 = currentTime;
                String time2 = "1600";
                String time3 = currentTime;
                String time4 = "0730";
                SimpleDateFormat format = new SimpleDateFormat("HHmm");

                try{
                    Date date1 = format.parse(time1);
                    Date date2 = format.parse(time2);
                    Date date3 = format.parse(time3);
                    Date date4 = format.parse(time4);
                    long difference = date2.getTime() - date1.getTime();
//                    long diffMinutes = difference / (60 * 1000) % 60;
                    long diffMinutes = difference / (60 * 60 * 1000) % 24;
                    long diff = date4.getTime() - date3.getTime();
                    long dif = diff / (60 * 60 * 1000) % 24;
                    int hours = (int) Math.abs(dif);
                    int minuts = (int) Math.abs(diffMinutes);
//                    String coba=String.valueOf(minuts);
//                    Toast.makeText(MainActivity.this,coba+" jam " , Toast.LENGTH_SHORT).show();
                    if(minuts >= 0){
                        if (minuts >= 8){
                            Check_in(nip,nama,jabatan,ruangan,jamMasuk,tanggal,keterangan1);
                        }
                        else{
                            double updtateTPP;
                            updtateTPP=  MyTpp - 0.5;
                            int TppPns = (int) updtateTPP;
                            String tppPns=String.valueOf(TppPns);
                            TppPegawai(nip,tppPns);
                            Check_in(nip,nama,jabatan,ruangan,jamMasuk,tanggal,keterangan2);
                            Toast.makeText(MainActivity.this, "Anda Terlambat Melakukan Absen", Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Silahkan Melakukan Absen Di jam 7.00 pagi", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }


                break;
            case R.id.btn_CheckOut:
                Calendar T = Calendar.getInstance();
                SimpleDateFormat dT = new SimpleDateFormat("dd-MM-yyyy");
                String formattedT = dT.format(T.getTime());
                String mytime = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
                nip=sessionManager.getUserDetail().get(SessionManager.NIP);
                jamKeluar = mytime;
                tanggal= formattedT;
                keterangan1 ="Hadir";

                String time12 = mytime;
                String time13 = "1600";
                String time14 = mytime;
                String time15 = "0730";
                SimpleDateFormat formatout = new SimpleDateFormat("HHmm");

                try {
                    Date date1 = formatout.parse(time12);
                    Date date2 = formatout.parse(time13);
                    Date date3 = formatout.parse(time14);
                    Date date4 = formatout.parse(time15);
                    long difference = date2.getTime() - date1.getTime();
//                    long diffMinutes = difference / (60 * 1000) % 60;
                    long diffMinutes = difference / (60 * 60 * 1000) % 24;
                    long diff = date4.getTime() - date3.getTime();
                    long dif = diff / (60 * 60 * 1000) % 24;
                    int hours = (int) Math.abs(dif);
                    int minuts = (int) Math.abs(diffMinutes);
//                    String coba=String.valueOf(minuts);
//                    Toast.makeText(MainActivity.this,"jam : "+coba , Toast.LENGTH_SHORT).show();
                    if (minuts < 0) {
                        ChecK_Out(nip,jamKeluar,tanggal,keterangan1);
                    }else{
                        Toast.makeText(MainActivity.this, "Silahkan Check-Out di jam 16.00", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void TppPegawai(String nip,String tpp) {
        apiInterface= APIClient.getClient().create(ApiInterface.class);
        Call<Tpp>UpdateTpp=apiInterface.UpdateTpp(nip,tpp);
        UpdateTpp.enqueue(new Callback<Tpp>() {
            @Override
            public void onResponse(Call<Tpp> call, Response<Tpp> response) {

            }

            @Override
            public void onFailure(Call<Tpp> call, Throwable t) {

            }
        });
    }

    private void ChecK_Out(String nip,String jamKeluar,String tanggal ,String keterangan) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen>CheckOut = apiInterface.absenUpdate(nip,jamKeluar,tanggal,keterangan);
        CheckOut.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                if (response.body() !=null && response.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, SplashCheckOut.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Anda Sudah Check-Out", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT);
            }
        });
    }

    private void Check_in(String nip, String nama, String jabatan, String ruangan, String jamMasuk, String tanggal, String keterangan) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen> absen = apiInterface.AbsenResponse(nip, nama, jabatan, ruangan, jamMasuk, tanggal, keterangan);
        absen.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                if (response.body() !=null && response.isSuccessful()){
                   Intent intent = new Intent(MainActivity.this,SplashAbsen.class);
                   startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Anda Sudah Check-in", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Toast.makeText(MainActivity.this,t.getLocalizedMessage(),Toast.LENGTH_SHORT);
            }
        });
    }
}