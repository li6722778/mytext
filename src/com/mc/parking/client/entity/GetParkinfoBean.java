package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.List;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;

public class  GetParkinfoBean  implements Serializable {

	private long parkingId;
	

//	/**
//	 * 精度
//	 */
//	private double latitude;
//	/**
//	 * 纬度
//	 */
//	private double longitude;
	/**
	 * 地图上的地址，可能是街道名，大厦
	 */
	private String mapAddress;
	
	/**
	 * 商家名称
	 */
	private String parkname;

	
	private String telenum;
	
	
	//0表示分段1表示固定
	private int paymode;
	private int couponmode;
	
	private	String mode1basetime;
	private	String mode1basepay;
	private	String mode1outpay;
	private	String mode2pay;
	private	String daypay;
	private	String couponpay;
	private String starttime;
	private String endtime;
	

	private List<Marker> InmarkList;
	
	private List<Marker> OutmarkList;

	/**
	 * 图片
	 */
	private String[] imageUrls;
	
	private String CompanyName;
	
	private String contactman;
	

	private List<LatLng> exitlist;
	
	

	public String getMode1basepay() {
		return mode1basepay;
	}

	public void setMode1basepay(String mode1basepay) {
		this.mode1basepay = mode1basepay;
	}

	public String getMode1outpay() {
		return mode1outpay;
	}

	public void setMode1outpay(String mode1outpay) {
		this.mode1outpay = mode1outpay;
	}

	public String getMode2pay() {
		return mode2pay;
	}

	public void setMode2pay(String mode2pay) {
		this.mode2pay = mode2pay;
	}

	public String getDaypay() {
		return daypay;
	}

	public void setDaypay(String daypay) {
		this.daypay = daypay;
	}

	public String getCouponpay() {
		return couponpay;
	}

	public void setCouponpay(String couponpay) {
		this.couponpay = couponpay;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String[] getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(String[] imageUrls) {
		this.imageUrls = imageUrls;
	}

	public String getCompanyName() {
		return CompanyName;
	}

	public void setCompanyName(String companyName) {
		CompanyName = companyName;
	}

	public String getContactman() {
		return contactman;
	}

	public void setContactman(String contactman) {
		this.contactman = contactman;
	}

	public List<LatLng> getExitlist() {
		return exitlist;
	}

	public void setExitlist(List<LatLng> exitlist) {
		this.exitlist = exitlist;
	}

	public List<LatLng> getInlist() {
		return Inlist;
	}

	public void setInlist(List<LatLng> inlist) {
		Inlist = inlist;
	}

	private List<LatLng> Inlist;

	public long getParkingId() {
		return parkingId;
	}

	public void setParkingId(long parkingId) {
		this.parkingId = parkingId;
	}

	public String getMapAddress() {
		return mapAddress;
	}

	public void setMapAddress(String mapAddress) {
		this.mapAddress = mapAddress;
	}

	public String getParkname() {
		return parkname;
	}

	public void setParkname(String parkname) {
		this.parkname = parkname;
	}

	public String getTelenum() {
		return telenum;
	}

	public void setTelenum(String telenum) {
		this.telenum = telenum;
	}

	public int getPaymode() {
		return paymode;
	}

	public void setPaymode(int paymode) {
		this.paymode = paymode;
	}

	public int getCouponmode() {
		return couponmode;
	}

	public void setCouponmode(int couponmode) {
		this.couponmode = couponmode;
	}

	public String getMode1basetime() {
		return mode1basetime;
	}

	public void setMode1basetime(String mode1basetime) {
		this.mode1basetime = mode1basetime;
	}


	public GetParkinfoBean(long parkingId,String companyName, String mapAddress,String parkname,String telenum,int paymode,int couponmode,
			String mode1basetime,String mode1basepay,String mode1outpay,String mode2pay,
			String daypay,String couponpay, String starttime, String endtime)
	{
		this.parkingId=parkingId;
		this.mapAddress=mapAddress;
		this.parkname=parkname;
		this.telenum=telenum;
		this.paymode=paymode;
		this.couponmode=couponmode;
		this.mode1basetime=mode1basetime;
		this.mode1basepay=mode1basepay;
		this.mode1outpay=mode1outpay;
		this.mode2pay=mode2pay;
		this.daypay=daypay;
		this.starttime=starttime;
		this.couponpay=couponpay;
		this.endtime=endtime;
		this.CompanyName=companyName;
	}

	public GetParkinfoBean(String[] imageUrls  , List<LatLng> Inlist,  List<LatLng> exitlist)
	{
		this.imageUrls=imageUrls;
		this.exitlist=exitlist;
		this.Inlist=Inlist;
		
		
	}

	public GetParkinfoBean()
	{
		
		
	}
	
	
}
