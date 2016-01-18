package com.mc.parking.client.entity;


import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;



public class TUseCouponEntity implements Serializable{

	
	public Long Id;
	
	
	public TuserInfo userInfo;
	
	
	public TCouponEntity counponentity;

	public int isable;
	

	public Date scanDate;


	public Date useDate;
	
	public int type;
	
	
	

}
