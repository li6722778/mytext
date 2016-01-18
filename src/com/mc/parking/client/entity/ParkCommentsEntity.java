package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

public class ParkCommentsEntity implements Serializable {

	private long parkingComId;

	private String createPerson;

	private Date createDate;
	private TParkInfoEntity parkInfo;

	private String comments;

	private float rating;

	public ParkCommentsEntity(TParkInfoEntity parkInfo, String comments,
			float rating) {
		super();
		this.parkInfo = parkInfo;
		this.comments = comments;
		this.rating = rating;
	}

	public String getCreatePerson() {
		return createPerson;
	}

	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}


	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public float getRating() {
		return rating;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	public long getParkingComId() {
		return parkingComId;
	}

	public void setParkingComId(long parkingComId) {
		this.parkingComId = parkingComId;
	}

}
