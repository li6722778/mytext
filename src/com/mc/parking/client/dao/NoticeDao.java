package com.mc.parking.client.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mc.parking.client.entity.NoticeBean;
import com.mc.parking.client.utils.DBHelper;

public class NoticeDao {
	DBHelper helper = null;  
	  
    public NoticeDao(Context cxt) {  
        helper = new DBHelper(cxt);  
    }  
    
    
 // ¸üÐÂ²Ù×÷  
    public void UpdateData(NoticeBean noticeBean) {  
        String sql = "update notice_tb set (notice,noticevoice,noticevibrate)values(?,?,?) ";  
        SQLiteDatabase db = helper.getWritableDatabase();  
        db.execSQL(sql, new Object[] { noticeBean.getNotice(), noticeBean.getNoticevoice(),noticeBean.getNoticevibrate() });  
    }  
}
