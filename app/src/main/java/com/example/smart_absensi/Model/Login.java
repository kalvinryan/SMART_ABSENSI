package com.example.smart_absensi.Model;

import com.google.gson.annotations.SerializedName;

public class Login{

	@SerializedName("data")
	private LoginData loginData;

	@SerializedName("message")
	private String message;

	@SerializedName("akses")
	private String akses;

	@SerializedName("status")
	private boolean status;

	public void setLoginData(LoginData loginData){
		this.loginData = loginData;
	}

	public LoginData getLoginData(){
		return loginData;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setAkses(String akses){
		this.akses = akses;
	}

	public String getAkses(){
		return akses;
	}

	public void setStatus(boolean status){
		this.status = status;
	}

	public boolean isStatus(){
		return status;
	}
}