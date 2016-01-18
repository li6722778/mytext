package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;





import com.google.gson.annotations.Expose;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="TParkInfo_Product")
public class TParkInfo_Product implements Serializable {

	/**
	 * 
	 */

	@DatabaseField(generatedId=true) 
	public Long ProductId;

	@DatabaseField
	public TParkInfoEntity parkInfo;

	// 1:�?放中; 2:关闭

	@DatabaseField
	public int isOpen;
	
	public String ProductName;

	// 总数�?
	@DatabaseField
	public int totalCount;
	
	// 已销售数�?
	@DatabaseField
	public int hasCount;
	

	@DatabaseField
	public String imgUrlHeader;


	@DatabaseField
	public String imgUrlPath;

	/* 类型,1:入口,2:出口 */
	@DatabaseField
	public int type;


	@DatabaseField
	public double currentMoney;
	


	@DatabaseField
	public double oldMoney;

	@DatabaseField
	public Date createDate;

	@DatabaseField
	public Date updateDate;

	@DatabaseField
	public String createPerson;


	@DatabaseField
	public String updatePerson;



}
