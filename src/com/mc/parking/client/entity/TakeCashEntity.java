package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

import android.provider.ContactsContract.Contacts.Data;


public class TakeCashEntity implements Serializable {

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public Long takecashid;
	public Double takemoney;
	public String cardnumber;
	public String cardownername;
	public String cardname;
	public Date askdata;
	public Date bankHandleData;
	public Date okData;
	//1.申请 2.处理中 3.提现成功
	public	int status;
	public long parkid;
	public String handleName;
	
    
	
	

	

	

}
