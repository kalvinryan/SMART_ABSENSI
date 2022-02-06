package com.example.smart_absensi.Model;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginData {

	@SerializedName("password")
	private String password;

	@SerializedName("ruangan")
	private String ruangan;

	@SerializedName("nip")
	private String nip;

	@SerializedName("nama")
	private String nama;

	@SerializedName("role_id")
	private String roleId;

	@SerializedName("jabatan")
	private String jabatan;

	@SerializedName("jkl")
	private String jkl;

	@SerializedName("id")
	private String id;

	@SerializedName("ttl")
	private String ttl;

	@SerializedName("alamat")
	private String alamat;

	@SerializedName("tpp")
	private String tpp;

	@SerializedName("time")
	private String time;

	private String expired;

	long diffDays,diff;



	public void setPassword(String password){
		this.password = password;
	}

	public String getPassword(){
		return password;
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

	public void setRoleId(String roleId){
		this.roleId = roleId;
	}

	public String getRoleId(){
		return roleId;
	}

	public void setJabatan(String jabatan){
		this.jabatan = jabatan;
	}

	public String getJabatan(){
		return jabatan;
	}

	public void setJkl(String jkl){
		this.jkl = jkl;
	}

	public String getJkl(){
		return jkl;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setTtl(String ttl){
		this.ttl = ttl;
	}

	public String getTtl(){
		return ttl;
	}

	public void setAlamat(String alamat){
		this.alamat = alamat;
	}

	public String getAlamat(){
		return alamat;
	}

	public String getTpp() {
		return tpp;
	}

	public void setTpp(String tpp) {
		this.tpp = tpp;
	}

	public void setExpired(String expired) {
		this.expired = expired;
	}


	public String getExpired(){
		Date currentDate = Calendar.getInstance().getTime();
		String birthDateString="2022/02/06";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date birthDate;
            birthDate = format.parse(birthDateString);
            diff =  birthDate.getTime()-currentDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
        }catch (ParseException e){
            e.printStackTrace();
        }
		return String.valueOf(diffDays);
	}
}