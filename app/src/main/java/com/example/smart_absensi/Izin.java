package com.example.smart_absensi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.Expired;
import com.example.smart_absensi.Model.Tpp;

public class Izin extends AppCompatActivity {

    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private TextView tvDateResult;
    private EditText btDatePicker,txtnip,txtnama,txtjabatan;
    RadioGroup rb_keterangan;
//    RadioButton rb_izin,rb_sakit,rb_alfa;
    int myGeofTimer;
    RadioButton rb_button;
    Button btn_kirim;
    String nip,nama,jabatan,ruangan,latitude,longitude,jamMasuk,tpp;
    ApiInterface apiInterface;
    ImageView btn_back;
    SessionManager sessionManager;

    private void showDateDialog(){
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                btDatePicker.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_izin);
        Expired expired = new Expired();
        sessionManager = new SessionManager(Izin.this);
        if (!sessionManager.isLoggedIn()) {
            moveToLogin();
        }


        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        btDatePicker = findViewById(R.id.txt_dateIzin);
        txtnip=findViewById(R.id.txt_nipIzin);
        txtnama=findViewById(R.id.txt_namaIzin);
        txtjabatan=findViewById(R.id.txt_jabatanIzin);
        rb_keterangan = findViewById(R.id.rb_keterangan);
//        rb_izin = findViewById(R.id.rb_izin);
//        rb_alfa = findViewById(R.id.rb_alfa);
//        rb_sakit = findViewById(R.id.rb_sakit);
        btn_kirim=findViewById(R.id.btn_kirimIzin);
        btn_back=findViewById(R.id.btn_backIzin);
        Intent data = getIntent();

         nip = data.getStringExtra("nip");
         nama = data.getStringExtra("nama");
         jabatan = data.getStringExtra("jabatan");
         ruangan = data.getStringExtra("ruangan");
         jamMasuk = data.getStringExtra("jamMasuk");
         latitude = data.getStringExtra("latitude");
         longitude = data.getStringExtra("longitude");
         tpp = data.getStringExtra("tpp");
         String currentTime2 = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
         jamMasuk = currentTime2;
         myGeofTimer = Integer.parseInt(sessionManager.getExpired());
        if (myGeofTimer>=0){
            Toast.makeText(this, expired.getPesan(), Toast.LENGTH_SHORT).show();
            moveToLogin();
        }

         txtnip.setText(nip);
         txtnama.setText(nama);
         txtjabatan.setText(jabatan);


//        dateFormatter = new SimpleDateFormat("d-MM-yyyy", Locale.US);
//        tvDateResult = (TextView) findViewById(R.id.tv_dateresult);
        btDatePicker = (EditText) findViewById(R.id.txt_dateIzin);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Izin.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int seletecdId = rb_keterangan.getCheckedRadioButtonId();
                rb_button = findViewById(seletecdId);
                try {
                    String tanggal = String.valueOf(btDatePicker.getText());
                    String keterangan = String.valueOf(rb_button.getText());
//                    Toast.makeText(Izin.this, "Radio :" + keterangan + " Tanggal : "+tanggal+ " nama : "+nama+ " Tanggal : "+jabatan
//                            + " ruangan : "+ruangan+ " lat : "+latitude+ " long : "+longitude+ " Jam Masuk : "+jamMasuk, Toast.LENGTH_SHORT).show();
                    if (keterangan.equalsIgnoreCase("Izin")||keterangan.equalsIgnoreCase("Sakit")){
                        izin(nip,nama,jabatan,ruangan,latitude,longitude,jamMasuk,tanggal,keterangan);
                    }else{
                        izin(nip,nama,jabatan,ruangan,latitude,longitude,jamMasuk,tanggal,keterangan);
                        int hasilttp = (int) (0.5 * Integer.parseInt(tpp));
                        tppPegawai(nip,String.valueOf(hasilttp));
                    }
                }catch (Exception e){
                    Log.d("TAG", "onClick: " +e.toString());
                }
            }
        });

    }

    private void izin(String nip, String nama, String jabatan, String ruangan, String latitude, String longitude, String jamMasuk, String tanggal, String keterangan) {
        apiInterface = APIClient.getClient().create(ApiInterface.class);
        Call<Absen> dataSekda = apiInterface.IzinResponse(nip,nama,jabatan,ruangan,latitude,longitude,jamMasuk,tanggal,keterangan);
        dataSekda.enqueue(new Callback<Absen>() {
            @Override
            public void onResponse(Call<Absen> call, Response<Absen> response) {
                if (response.body() != null && response.isSuccessful()) {
                    Intent intent = new Intent(Izin.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(Izin.this, "Anda Sudah Check-in " +
                            "dan jika ingin Izin silahkan untuk menghadap langsung" +
                            "dengan atasan anda..", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Absen> call, Throwable t) {
                Log.d("data", "onFailure: " + t);
            }
        });
    }
    private void tppPegawai(String nip,String tpp){
        apiInterface =APIClient.getClient().create(ApiInterface.class);
        Call<Tpp>UpdateTpp = apiInterface.UpdateTpp(nip,tpp);
        UpdateTpp.enqueue(new Callback<Tpp>() {
            @Override
            public void onResponse(Call<Tpp> call, Response<Tpp> response) {

            }

            @Override
            public void onFailure(Call<Tpp> call, Throwable t) {

            }
        });
    }
    private void moveToLogin() {
        Intent intent = new Intent(Izin.this, LoginAbsen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}