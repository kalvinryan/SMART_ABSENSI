package com.example.smart_absensi.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.smart_absensi.API.APIClient;
import com.example.smart_absensi.API.ApiInterface;
import com.example.smart_absensi.Config;
import com.example.smart_absensi.Model.Absen;
import com.example.smart_absensi.Model.AbsenData;
import com.example.smart_absensi.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRiwayat extends RecyclerView.Adapter<AdapterRiwayat.HolderData> {
    private Context ctx;
    private List<AbsenData>listAbsen;
    private List<AbsenData>listRiwayat;
    int idRiwayat;
    String JbtPGW;

    public AdapterRiwayat(Context ctx,List<AbsenData>listAbsen){
        this.ctx =ctx;
        this.listAbsen=listAbsen;
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.riwayat_item,parent,false);
        HolderData holder= new HolderData(layout);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        AbsenData absenData = listAbsen.get(position);

        holder.txtid.setText(String.valueOf(absenData.getIdAbsen()));
        holder.txtnama.setText(absenData.getNama());
        holder.txtjbt.setText(absenData.getJabatan());
        holder.txtJamMasuk.setText(absenData.getJamMasuk());
        holder.txtjamKeluar.setText(absenData.getJamKeluar());
        holder.txtketerangan.setText(absenData.getKeterangan());
        holder.txtTanggal.setText(absenData.getTanggal());
        holder.txt_tpp.setText(absenData.getMyTpp());
        Glide.with(holder.itemView.getContext())
                .load(Config.IMAGE_URL+absenData.getImage())
                .apply(new RequestOptions().override(150,650))
                .into(holder.imgb);
    }

    @Override
    public int getItemCount() {
        return listAbsen.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        TextView txtid,txtnama,txtjbt,txtJamMasuk,txtjamKeluar,txtketerangan,txtTanggal,txt_tpp;
        ImageView imgb;

        public HolderData(@NonNull View itemView) {
            super(itemView);

            txtid = itemView.findViewById(R.id.txt_idRwt);
            txtnama = itemView.findViewById(R.id.txt_namaRwt);
            txtjbt = itemView.findViewById(R.id.txt_jabatanRwt);
            txtJamMasuk = itemView.findViewById(R.id.txt_masukRwt);
            txtjamKeluar = itemView.findViewById(R.id.txt_keluarRwt);
            txtTanggal = itemView.findViewById(R.id.txt_tanggalRwt);
            txtketerangan = itemView.findViewById(R.id.txt_keteranganRwt);
            txt_tpp = itemView.findViewById(R.id.txt_tppRwt);
            imgb = itemView.findViewById(R.id.imgbItm);


            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder dialogPesan = new AlertDialog.Builder(ctx);
                    dialogPesan.setMessage("Options :");
                    dialogPesan.setIcon(R.mipmap.ic_launcher);
                    dialogPesan.setCancelable(true);
                    idRiwayat = Integer.parseInt(txtid.getText().toString());

                    dialogPesan.setNegativeButton("Show", new DialogInterface.OnClickListener() {
                        @Override
                    public void onClick(DialogInterface dialog, int which) {
                            getDataRiwayat();
                            }
                        });
                        dialogPesan.show();
                        return false;
                    }
            });
        }
        public void getDataRiwayat(){
            ApiInterface apiInterface = APIClient.getClient().create(ApiInterface.class);
            Call<Absen> getData = apiInterface.getRiwayat(idRiwayat);
            getData.enqueue(new Callback<Absen>() {
                @Override
                public void onResponse(Call<Absen> call, Response<Absen> response) {
                    String pesan = response.body().getMessage();
                    listAbsen = response.body().getAbsenDataku();
                    String id = String.valueOf(listAbsen.get(0).getIdAbsen());
                    String nip = listAbsen.get(0).getNip();
                    String nama = listAbsen.get(0).getNama();
                    String latitude = listAbsen.get(0).getLatitude();
                    String longitude = listAbsen.get(0).getLongitude();
                    String keterangan = listAbsen.get(0).getKeterangan();
                    String tpp = listAbsen.get(0).getMyTpp();

//                    Toast.makeText(ctx, "betul : "+id+"|Nama : ", Toast.LENGTH_SHORT).show();

//                    Intent intent = new Intent(ctx, TampilRiwayat.class);
//                    intent.putExtra("xId",id);
//                    intent.putExtra("xNIP",nip);
//                    intent.putExtra("xNama",nama);
//                    intent.putExtra("xLatitude",latitude);
//                    intent.putExtra("xLongitude",longitude);
//                    intent.putExtra("xketerangan",keterangan);
//                    intent.putExtra("xtpp",tpp);
//                    ctx.startActivities(new Intent[]{intent});

                }

                @Override
                public void onFailure(Call<Absen> call, Throwable t) {
                    Log.e(String.valueOf(ctx), "onFailure: ",t );
//                    Toast.makeText(ctx, "Gagal : " + t.toString(), Toast.LENGTH_LONG).show();

                }
            });
        }
    }
}
