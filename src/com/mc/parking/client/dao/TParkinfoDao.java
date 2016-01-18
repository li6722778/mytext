package com.mc.parking.client.dao;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mc.parking.client.entity.HistoryEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;
import com.mc.parking.client.utils.DBHelper;

public class TParkinfoDao {
	DBHelper helper = null;

	public TParkinfoDao(Context cxt) {
		helper = new DBHelper(cxt);
	}
	// ²åÈë²Ù×÷  
    public void insertData(TParkInfoEntity entity) {  
        String sql = "insert into TParkInfo (parkId,parkname,detail,address,vender,owner,ownerPhone,venderBankName,venderBankNumber,"
        		+ "feeType,feeTypeSecInScopeHours,feeTypeSecInScopeHourMoney,feeTypeSecOutScopeHourMoney,feeTypefixedHourMoney,isDiscountAllday,isDiscountSec,discountHourAlldayMoney"
        		+ "discountSecHourMoney,discountSecStartHour,discountSecEndHour,createDate,updateDate,createPerson,updatePerson)values"
        		+ "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";  
        SQLiteDatabase db = helper.getWritableDatabase();  
        db.execSQL(sql, new Object[] {
        		entity.parkId,entity.parkname,entity.detail,entity.address,entity.vender,entity.owner,entity.ownerPhone,entity.venderBankName,entity.venderBankNumber,
        		entity.feeType,entity.feeTypeSecInScopeHours,entity.feeTypeSecInScopeHourMoney,entity.feeTypeSecOutScopeHourMoney,entity.feeTypefixedHourMoney,entity.isDiscountAllday,entity.isDiscountSec,entity.discountHourAlldayMoney,
        		entity.discountSecHourMoney,entity.discountSecStartHour,entity.discountSecEndHour,entity.createDate,entity.updateDate,entity.createPerson,entity.updatePerson
        
        });  
    }  
    public void insertImageData(TParkInfo_ImgEntity entity)
    {
    	
    	 String sql = "insert into TParkInfo_Img (parkImgId,imgUrlHeader,imgUrlPath,createDate,updateDate,createPerson,updatePerson)values"
         		+ "(?,?,?,?,?,?,?)";  
         SQLiteDatabase db = helper.getWritableDatabase();  
         db.execSQL(sql, new Object[] {entity.parkImgId,entity.imgUrlHeader,entity.imgUrlPath,entity.createDate,entity.updateDate,entity.createPerson,entity.updatePerson });  
    }
    
    public void insertImageLoc(TParkInfo_LocEntity entity)
    {
    	
    	 String sql = "insert into TParkInfo_Loc (parkLocId,isOpen,parkFreeCount,type,latitude,longitude,createDate,updateDate,createPerson,updatePerson)values"
         		+ "(?,?,?,?,?,?,?,?,?,?)";  
         SQLiteDatabase db = helper.getWritableDatabase();  
         db.execSQL(sql, new Object[] {entity.parkLocId,entity.isOpen,entity.parkFreeCount,entity.type,entity.latitude,entity.longitude,entity.createDate,entity.updateDate,entity.createPerson,entity.updatePerson,});  
    }
    
    
}






