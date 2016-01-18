package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

public class TParkInfo_Py implements Serializable{


	private Long parkPyId;
	  

	private double payTotal;
	 

	private double payActu;
	 
	public double couponUsed;
	
	public double getCouponUsed() {
		return couponUsed;
	}

	public void setCouponUsed(double couponUsed) {
		this.couponUsed = couponUsed;
	}

	private int payMethod;
	 

	public Long getParkPyId() {
		return parkPyId;
	}

	public void setParkPyId(Long parkPyId) {
		this.parkPyId = parkPyId;
	}

	public double getPayTotal() {
		return payTotal;
	}

	public void setPayTotal(double payTotal) {
		this.payTotal = payTotal;
	}

	public double getPayActu() {
		return payActu;
	}

	public void setPayActu(double payActu) {
		this.payActu = payActu;
	}

	public int getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(int payMethod) {
		this.payMethod = payMethod;
	}

	public int getAckStatus() {
		return ackStatus;
	}

	public void setAckStatus(int ackStatus) {
		this.ackStatus = ackStatus;
	}

	public Date getPayDate() {
		return payDate;
	}

	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}

	public Date getAckDate() {
		return ackDate;
	}

	public void setAckDate(Date ackDate) {
		this.ackDate = ackDate;
	}

	public String getCreatePerson() {
		return createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}

	private int ackStatus;

	private Date payDate;

	private Date ackDate;
	 
	private String createPerson;
}
