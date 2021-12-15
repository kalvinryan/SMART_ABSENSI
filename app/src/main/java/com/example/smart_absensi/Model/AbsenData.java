package com.example.smart_absensi.Model;

import com.google.gson.annotations.SerializedName;

public class AbsenData {

	@SerializedName("keterangan")
	private String keterangan;

	@SerializedName("ruangan")
	private String ruangan;

	@SerializedName("nip")
	private String nip;

	@SerializedName("nama")
	private String nama;

	@SerializedName("jabatan")
	private String jabatan;

	@SerializedName("jam")
	private String jam;

	@SerializedName("tanggal")
	private String tanggal;

	public void setKeterangan(String keterangan){
		this.keterangan = keterangan;
	}

	public String getKeterangan(){
		return keterangan;
	}

	public void setRuangan(String ruangan){
		this.ruangan = ruangan;
	}

	public String getRuangan(){
		return ruangan;
	}

	public void setNip(String nip){
		this.nip = nip;
	}

	public String getNip(){
		return nip;
	}

	public void setNama(String nama){
		this.nama = nama;
	}

	public String getNama(){
		return nama;
	}

	public void setJabatan(String jabatan){
		this.jabatan = jabatan;
	}

	public String getJabatan(){
		return jabatan;
	}

	public void setJam(String jam){
		this.jam = jam;
	}

	public String getJam(){
		return jam;
	}

	public void setTanggal(String tanggal){
		this.tanggal = tanggal;
	}

	public String getTanggal(){
		return tanggal;
	}
}