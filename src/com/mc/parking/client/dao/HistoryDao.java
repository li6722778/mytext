package com.mc.parking.client.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mc.parking.client.entity.HistoryEntity;
import com.mc.parking.client.utils.DBHelper;

public class HistoryDao {
	DBHelper helper = null;  
	  
    public HistoryDao(Context cxt) {  
        helper = new DBHelper(cxt);  
    }  
    
    
 // ²åÈë²Ù×÷  
    public void insertData(HistoryEntity entity) {  
        String sql = "insert into history_sh (shkey,shdate,city)values(?,?,?)";  
        SQLiteDatabase db = helper.getWritableDatabase();  
        db.execSQL(sql, new Object[] { entity.getSearchKey(), entity.getSearchDate(),entity.getCity() });  
    }  
}
