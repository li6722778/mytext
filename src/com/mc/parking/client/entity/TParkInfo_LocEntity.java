package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="TParkInfo_Loc")
public class TParkInfo_LocEntity implements Serializable,Cloneable  {
	
	
	
	@DatabaseField(generatedId=true) 
	public Long ID;
	
	@DatabaseField
	public Long parkLocId;

		public TParkInfoEntity parkInfo;

	//1:开放中; 2:关闭
		@DatabaseField 
	public int isOpen;
	
		@DatabaseField 
	public int parkFreeCount;
	
		@DatabaseField 
	/*类型,1:出口,2:入口*/
	public int type;
	
		@DatabaseField 
	public double latitude;

		@DatabaseField 
	public double longitude;

		
		@DatabaseField 
	public Date createDate;

		
		@DatabaseField 
	public Date updateDate;
	
		
		@DatabaseField 
	public String createPerson;

		
		public TParkInfo_LocEntity(Long iD, Long parkLocId,
				TParkInfoEntity parkInfo, int isOpen, int parkFreeCount,
				int type, double latitude, double longitude, Date createDate,
				Date updateDate, String createPerson, String updatePerson,
				double distance) {
			super();
			ID = iD;
			this.parkLocId = parkLocId;
			this.parkInfo = parkInfo;
			this.isOpen = isOpen;
			this.parkFreeCount = parkFreeCount;
			this.type = type;
			this.latitude = latitude;
			this.longitude = longitude;
			this.createDate = createDate;
			this.updateDate = updateDate;
			this.createPerson = createPerson;
			this.updatePerson = updatePerson;
			this.distance = distance;
		}

		public TParkInfo_LocEntity() {
			// TODO Auto-generated constructor stub
		}

		@DatabaseField 
	public String updatePerson;
		
	public double distance;

		public TParkInfo_LocEntity clone() {  
			TParkInfo_LocEntity o = null;  
	        try {  
	            o = (TParkInfo_LocEntity) super.clone();   
	            o.parkInfo = parkInfo;
	        } catch (CloneNotSupportedException e) {  
	            e.printStackTrace();  
	        }  
	        return o;  
	    } 

}
