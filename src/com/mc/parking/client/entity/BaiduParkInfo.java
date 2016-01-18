package com.mc.parking.client.entity;

import java.io.Serializable;

public class BaiduParkInfo implements Serializable{

	private static final long serialVersionUID = -758459602806858414L;
	
	
	
	private String parkingId;
	/**
	 * 精度
	 */
	private double latitude;
	/**
	 * 纬度
	 */
	private double longitude;
	/**
	 * 商家名称
	 */
	private String name;
	
	private String address;
	
	private String addname;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public BaiduParkInfo(String parkingId, double latitude, double longitude, String name,String address)
	{
		super();
		this.setParkingId(parkingId);
		this.latitude = latitude;
		this.longitude = longitude;
		this.name = name;
		this.address = address;
		
		
		
	}
	public String getParkingId() {
		return parkingId;
	}
	public void setParkingId(String parkingId) {
		this.parkingId = parkingId;
	}
	public String getAddname() {
		return addname;
	}
	public void setAddname(String addname) {
		this.addname = addname;
	}
}
