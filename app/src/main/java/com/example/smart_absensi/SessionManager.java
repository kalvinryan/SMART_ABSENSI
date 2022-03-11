package com.example.smart_absensi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.smart_absensi.Model.LoginData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class SessionManager {
    private Context _context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final String IS_LOGGED_IN = "ISLoggedIn";
    public static final String ID = "id";
    public static final String NIP = "nip";
    public static final String Nama = "nama";
    public static final String Jabatan = "jabatan";
    public static final String Ruangan = "ruangan";
    public static final String Password = "password";
    public static final String Tpp = "tpp";
    public static final String Expired = "expired";
    public long diffDays,diff;



    public SessionManager (Context context){
        this._context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(LoginData user){
        editor.putBoolean(IS_LOGGED_IN,true);
        editor.putString(ID,user.getId());
        editor.putString(NIP,user.getNip());
        editor.putString(Nama, user.getNama());
        editor.putString(Jabatan, user.getJabatan());
        editor.putString(Ruangan, user.getRuangan());
        editor.putString(Password, user.getPassword());
        editor.putString(Tpp, user.getTpp());
        editor.putString(Expired, user.getExpired());

        editor.commit();
    }

    public HashMap<String,String>getUserDetail(){
        HashMap<String,String>user = new HashMap<>();
        user.put(ID,sharedPreferences.getString(ID,null));
        user.put(NIP,sharedPreferences.getString(NIP,null));
        user.put(Nama,sharedPreferences.getString(Nama,null));
        user.put(Jabatan,sharedPreferences.getString(Jabatan,null));
        user.put(Ruangan,sharedPreferences.getString(Ruangan,null));
        user.put(Password,sharedPreferences.getString(Password,null));
        user.put(Tpp,sharedPreferences.getString(Tpp,null));
        user.put(Expired,sharedPreferences.getString(Expired,null));
        return user;
    }

    public void logoutSession(){
        editor.clear();
        editor.apply();
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGGED_IN,false);
    }

    public String getExpired(){
		Date currentDate = Calendar.getInstance().getTime();
		String birthDateString="2022/03/30";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date birthDate;
            birthDate = format.parse(birthDateString);
            diff =  currentDate.getTime()-birthDate.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
        }catch (ParseException e){
            e.printStackTrace();
        }
		return String.valueOf(diffDays);
	}

}
