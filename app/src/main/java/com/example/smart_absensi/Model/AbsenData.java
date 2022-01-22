package com.example.smart_absensi.Model;

import com.google.gson.annotations.SerializedName;

public class AbsenData {

	@SerializedName("id")
	private int idAbsen;

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

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("longitude")
	private String longitude;

	@SerializedName("jamMasuk")
	private String jamMasuk;

	@SerializedName("jamKeluar")
	private String jamKeluar;

	@SerializedName("tanggal")
	private String tanggal;

	@SerializedName("image")
	private String image;

	@SerializedName("image2")
	private String image2;

	@SerializedName("tpp")
	private String myTpp;

	@SerializedName("sts_tanggal")
	private String sts_tanggal;

	public int getIdAbsen() {
		return idAbsen;
	}

	public void setIdAbsen(int idAbsen) {
		this.idAbsen = idAbsen;
	}

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

	public String getJamMasuk() {
		return jamMasuk;
	}

	public void setJamMasuk(String jamMasuk) {
		this.jamMasuk = jamMasuk;
	}

	public String getJamKeluar() {
		return jamKeluar;
	}

	public void setJamKeluar(String jamKeluar) {
		this.jamKeluar = jamKeluar;
	}

	public void setTanggal(String tanggal){
		this.tanggal = tanggal;
	}

	public String getTanggal(){
		return tanggal;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getImage2() {
		return image2;
	}

	public void setImage2(String image2) {
		this.image2 = image2;
	}

	public String getMyTpp() {
		return myTpp;
	}

	public void setMyTpp(String myTpp) {
		this.myTpp = myTpp;
	}

	public String getSts_tanggal() {
		return sts_tanggal;
	}

	public void setSts_tanggal(String sts_tanggal) {
		this.sts_tanggal = sts_tanggal;
	}
}