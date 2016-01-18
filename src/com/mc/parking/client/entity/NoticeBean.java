package com.mc.parking.client.entity;


import android.R.integer;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="notice_tb")
public class NoticeBean {

	@DatabaseField(generatedId=true) 
	private int id;
	@DatabaseField  
	private int notice;
	@DatabaseField  
	private int  noticevoice;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@DatabaseField 
	private int noticevibrate;
	public int getNotice() {
		return notice;
	}
	public void setNotice(int notice) {
		this.notice = notice;
	}
	public int getNoticevoice() {
		return noticevoice;
	}
	public void setNoticevoice(int noticevoice) {
		this.noticevoice = noticevoice;
	}
	public int getNoticevibrate() {
		return noticevibrate;
	}
	public void setNoticevibrate(int noticevibrate) {
		this.noticevibrate = noticevibrate;
	}
	
	
	
	
}
