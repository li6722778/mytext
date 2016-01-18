package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

import android.R.string;

public class TuserInfo implements Serializable{

	public Long userid;
	
	public String userName;

	public String passwd;
	
	public long userPhone;

	public String email;

	///*用户类型,10:普通用户,20:车位管理员,30:市场用户,99超级用户*/
	public int userType;

	public Date createDate;
	
	public String extensionstring;
	
	public String  userimageurl;
	
	
	public Long getUserid() {
		return userid;
	}


	public void setUserid(Long userid) {
		this.userid = userid;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPasswd() {
		return passwd;
	}


	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}


	public long getUserPhone() {
		return userPhone;
	}


	public void setUserPhone(long userPhone) {
		this.userPhone = userPhone;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public int getUserType() {
		return userType;
	}


	public void setUserType(int userType) {
		this.userType = userType;
	}


	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}


	public Date getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}


	public String getCreatePerson() {
		return createPerson;
	}


	public void setCreatePerson(String createPerson) {
		this.createPerson = createPerson;
	}


	public String getUpdatePerson() {
		return updatePerson;
	}


	public void setUpdatePerson(String updatePerson) {
		this.updatePerson = updatePerson;
	}


	public TParkInfoEntity getParkInfoAdm() {
		return parkInfoAdm;
	}


	public void setParkInfoAdm(TParkInfoEntity parkInfoAdm) {
		this.parkInfoAdm = parkInfoAdm;
	}


	public Date updateDate;
	

	public String createPerson;

	public String updatePerson;


	public TParkInfoEntity parkInfoAdm;
}
