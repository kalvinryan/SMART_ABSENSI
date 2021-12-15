package com.example.smart_absensi.Model;

import com.google.gson.annotations.SerializedName;

public class Tpp{

	@SerializedName("data")
	private TppData tppData;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private boolean status;

	public void setData(TppData tppData){
		this.tppData = tppData;
	}

	public TppData getData(){
		return tppData;
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
}