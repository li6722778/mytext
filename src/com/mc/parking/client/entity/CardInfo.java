package com.mc.parking.client.entity;

import java.io.Serializable;

import android.R.integer;
import android.R.string;

public class CardInfo implements Serializable{
	
	
	private String  bankid;
	private String  bankname;
	private String cardid;
	
	
	public CardInfo(String bankid, String bankname ,String carid){
		
		super();
		this.bankid=bankid;
		this.bankname=bankname;
		this.cardid=carid;
		
		
		
	}
	public String getBankid() {
		return bankid;
	}
	public void setBankid(String bankid) {
		this.bankid = bankid;
	}
	public String getBankname() {
		return bankname;
	}
	public void setBankname(String bankname) {
		this.bankname = bankname;
	}
	public String getCardid() {
		return cardid;
	}
	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

}
