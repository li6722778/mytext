package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;





import com.google.gson.annotations.Expose;

public class TSupply implements Serializable{

	@Expose
	public long id;
	
	@Expose
	public String supplyName;
	
	//供应商描述
	@Expose
	public String supplyDesc;
	
	//供应商电话
	@Expose
	public String supplyPhone;
	
	//供应商银行
	@Expose
	public String venderBankOwner;
	
	//供应商银行名
	@Expose
	public String venderBankName;

	//供应商银行号码
	@Expose
	public String venderBankNumber;
	
	// 创建和更新时间
	@Expose
	public Date updateDate;

	// 更新人
	@Expose
	public String updatePerson;
	
	@Expose
	public String orderSupplyComments;
}
