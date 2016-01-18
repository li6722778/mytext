package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrderEntity implements Serializable{

	private long orderId;
	private String orderName;
	private String orderCity;	
	public int orderFeeType;
	private TParkInfoEntity parkInfo;
	private List<TParkInfo_Py> pay;
	public String selectedServicesDetail;
	private int orderStatus;
	private String orderDetail;
	private long couponId;
	private Date orderDate;
	private Date startDate;
	private Date endDate;
	private double latitude;
	private double longitude;
	private TuserInfo userInfo;
	
	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	private TParkInfo_LocEntity tParkInfo_LocEntity;
	public long getOrderId() {
		return orderId;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public TParkInfo_LocEntity gettParkInfo_LocEntity() {
		return tParkInfo_LocEntity;
	}
	public void settParkInfo_LocEntity(TParkInfo_LocEntity tParkInfo_LocEntity) {
		this.tParkInfo_LocEntity = tParkInfo_LocEntity;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getOrderName() {
		return orderName;
	}
	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}
	public String getOrderCity() {
		return orderCity;
	}
	public void setOrderCity(String orderCity) {
		this.orderCity = orderCity;
	}

	public int getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(int orderStatus) {
		this.orderStatus = orderStatus;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public TuserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(TuserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public TParkInfoEntity getParkInfo() {
		return parkInfo;
	}

	public void setParkInfo(TParkInfoEntity parkInfo) {
		this.parkInfo = parkInfo;
	}

	public List<TParkInfo_Py> getPay() {
		return pay;
	}

	public void setPay(List<TParkInfo_Py> pay) {
		this.pay = pay;
	}

	public String getOrderDetail() {
		return orderDetail;
	}

	public void setOrderDetail(String orderDetail) {
		this.orderDetail = orderDetail;
	}

	public long getCouponId() {
		return couponId;
	}

	public void setCouponId(long couponId) {
		this.couponId = couponId;
	}

	
	
}
