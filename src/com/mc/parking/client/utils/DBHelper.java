package com.mc.parking.client.utils;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mc.parking.client.dao.NoticeDao;
import com.mc.parking.client.entity.HistoryEntity;
import com.mc.parking.client.entity.NoticeBean;
import com.mc.parking.client.entity.OffinemapEntity;
import com.mc.parking.client.entity.TParkInfoEntity;
import com.mc.parking.client.entity.TParkInfo_ImgEntity;
import com.mc.parking.client.entity.TParkInfo_LocEntity;

public class DBHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "parkingmc.db";  
    private static final int DATABASE_VERSION = 9;  
      
    private Dao<HistoryEntity,Integer> historyDao = null;  
    private Dao<OffinemapEntity,Integer> offineDao = null;  
    private Dao<TParkInfoEntity,Integer> parkinfo = null;
    private Dao<TParkInfo_ImgEntity,Integer> parkinfoimage = null;
    private Dao<TParkInfo_LocEntity,Integer> parkinfoloc = null;
	private Dao<NoticeBean,Integer> noticeDao = null;  
      
    public DBHelper(Context context){  
        super(context, DATABASE_NAME, null, DATABASE_VERSION);  
    }  
  
    /**  
     * 创建SQLite数据库  
     */  
    @Override  
    public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {  
        try {  
            TableUtils.createTable(connectionSource, HistoryEntity.class);  

            TableUtils.createTable(connectionSource, OffinemapEntity.class);
            TableUtils.createTable(connectionSource, TParkInfoEntity.class);
            TableUtils.createTable(connectionSource, TParkInfo_ImgEntity.class);
            TableUtils.createTable(connectionSource, TParkInfo_LocEntity.class);

        
            TableUtils.clearTable(connectionSource, NoticeBean.class);
        } catch (SQLException e) {  
            Log.e(DBHelper.class.getName(), "Unable to create datbases", e);  
        }  
    }  
  
    /**  
     * 更新SQLite数据库  
     */  
    @Override  
    public void onUpgrade(  
            SQLiteDatabase sqliteDatabase,   
            ConnectionSource connectionSource,   
            int oldVer,  
            int newVer) {  
        try {  
            TableUtils.dropTable(connectionSource, HistoryEntity.class, true); 
            TableUtils.dropTable(connectionSource, OffinemapEntity.class,true);

            TableUtils.dropTable(connectionSource, TParkInfoEntity.class,true);
            TableUtils.dropTable(connectionSource, TParkInfo_ImgEntity.class,true);
            TableUtils.dropTable(connectionSource, TParkInfo_LocEntity.class,true);

            TableUtils.dropTable(connectionSource, NoticeBean.class,true);
            onCreate(sqliteDatabase, connectionSource);  
        } catch (SQLException e) {  
            Log.e(DBHelper.class.getName(),   
                    "Unable to upgrade database from version " + oldVer + " to new "  
                    + newVer, e);  
        }  
    }  
      
    public Dao<HistoryEntity,Integer> getHistoryDao() throws SQLException{  
        if(historyDao == null){  
        	historyDao = getDao(HistoryEntity.class);  
        }  
        return historyDao;  
    }  
    
    public Dao<OffinemapEntity,Integer> getOffineDao() throws SQLException{  
        if(offineDao == null){  
        	offineDao = getDao(OffinemapEntity.class);  
        }  
        return offineDao;  
    }  

    
    public Dao<TParkInfoEntity,Integer> getParkdetailDao() throws SQLException
    {
    	 if(parkinfo == null){  
    		 parkinfo = getDao(TParkInfoEntity.class);  
         }  
         return parkinfo;  
    	  
    	
    } 
    
    public Dao<TParkInfo_ImgEntity,Integer> getParkdetail_imagDao() throws SQLException
    {
   	 if(parkinfoimage == null){  
   		parkinfoimage = getDao(TParkInfo_ImgEntity.class);  
        }  
        return parkinfoimage;  
   	  
   	
   }
    public Dao<TParkInfo_LocEntity,Integer> getParkdetail_locDao() throws SQLException
    {
   	 if(parkinfoloc == null){  
   		parkinfoloc = getDao(TParkInfo_LocEntity.class);  
        }  
        return parkinfoloc;  
   	  
   	
   }
    
    

    public Dao<NoticeBean,Integer> getNoticeDao() throws SQLException{  
        if(noticeDao == null){  
        	noticeDao = getDao(NoticeBean.class);  
        }  
        return noticeDao;  
    } 

}
