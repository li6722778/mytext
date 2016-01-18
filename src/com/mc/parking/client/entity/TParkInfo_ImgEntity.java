package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName="TParkInfo_Img")
public class TParkInfo_ImgEntity implements Serializable,Cloneable {
	
	
	@DatabaseField(generatedId=true) 
	public Long ID;
	
	@DatabaseField
	public Long parkImgId;

	public TParkInfo_LocEntity parkInfo;
	@DatabaseField  
	public String imgUrlHeader;
	@DatabaseField  
	public String imgUrlPath;
	@DatabaseField  
	public String detail;
	@DatabaseField  
	public Date createDate;
	@DatabaseField  
	public Date updateDate;
	@DatabaseField  
	public String createPerson;
	@DatabaseField  
	public String updatePerson;
	
	public TParkInfo_ImgEntity clone() {  
		TParkInfo_ImgEntity o = null;  
        try {  
            o = (TParkInfo_ImgEntity) super.clone();  
            o.parkInfo = parkInfo;
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return o;  
    }



	public TParkInfo_ImgEntity(Long parkImgId,
			TParkInfo_LocEntity parkInfo, String imgUrlHeader,
			String imgUrlPath, String detail, Date createDate, Date updateDate,
			String createPerson, String updatePerson) {
		super();
		this.parkImgId = parkImgId;
		this.parkInfo = parkInfo;
		this.imgUrlHeader = imgUrlHeader;
		this.imgUrlPath = imgUrlPath;
		this.detail = detail;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.createPerson = createPerson;
		this.updatePerson = updatePerson;
	}



	public TParkInfo_ImgEntity() {
		// TODO Auto-generated constructor stub
	} 
}
