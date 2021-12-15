package com.example.smart_absensi.Model;

import com.google.gson.annotations.SerializedName;

public class Absen{

	@SerializedName("data")
	private AbsenData absenData;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	@SerializedName("hadir")
	private String hadir;

	@SerializedName("sakit")
	private String sakit;

	@SerializedName("izin")
	private String izin;

	@SerializedName("alfa")
	private String alfa;

	public void setData(AbsenData absenData){
		this.absenData = absenData;
	}

	public AbsenData getData(){
		return absenData;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}

	public String getHadir() {
		return hadir;
	}

	public void setHadir(String hadir) {
		this.hadir = hadir;
	}

	public String getSakit() {
		return sakit;
	}

	public void setSakit(String sakit) {
		this.sakit = sakit;
	}

	public String getIzin() {
		return izin;
	}

	public void setIzin(String izin) {
		this.izin = izin;
	}

	public String getAlfa() {
		return alfa;
	}

	public void setAlfa(String alfa) {
		this.alfa = alfa;
	}
}