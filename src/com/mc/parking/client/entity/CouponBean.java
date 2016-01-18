package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;


public class CouponBean implements Serializable{

	
private long CouponID;
	private String CouponInfo;
	private Date EndDate;
	private Double Couponmoney;
	
	
	
	public CouponBean(long CouponID,String CouponInfo,Date EndDate,Double Couponmoney)
	{
		super();
		this.CouponID=CouponID;
		this.CouponInfo=CouponInfo;
		this.EndDate=EndDate;
		this.Couponmoney=Couponmoney;
		
		
	}
	
	
	public long getCouponID() {
		return CouponID;
	}
	public void setCouponID(long couponID) {
		CouponID = couponID;
	}
	public String getCouponInfo() {
		return CouponInfo;
	}
	public void setCouponInfo(String couponInfo) {
		CouponInfo = couponInfo;
	}
	public Date getEndDate() {
		return EndDate;
	}
	public void setEndDate(Date endDate) {
		EndDate = endDate;
	}
	public Double getCouponmoney() {
		return Couponmoney;
	}
	public void setCouponmoney(Double couponmoney) {
		Couponmoney = couponmoney;
	}
	
}
