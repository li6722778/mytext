package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

public class TIncome implements Serializable{

	
	public Long incomeId;

	
	public TParkInfoEntity parkInfo;

	public double incometotal;

	public double cashtotal;
	public double incometoday;
	
	public double takeCashTotal;

	public double counpontotal;
	
	public Date createDate;

	
	public Date updateDate;


	public int finishedOrder;


	public double onlineIncomeTotal;
	

	public double allowance;
	
	
}
