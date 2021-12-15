package com.example.smart_absensi.Model;

import com.google.gson.annotations.SerializedName;

public class TppData {

	@SerializedName("tpp")
	private String tpp;

	public void setTpp(String tpp){
		this.tpp = tpp;
	}

	public String getTpp(){
		return tpp;
	}
}