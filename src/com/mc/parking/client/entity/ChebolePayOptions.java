package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;

public class ChebolePayOptions implements Serializable{
	

	public String payInfo;

	public OrderEntity order;

	public long paymentId;

    public double payOrginalPriceFoTotal;
	

    public double payOrginalPrice;

    public double payActualPriceForTotal;
	

    public double payActualPrice;
	

	public double counponUsedMoneyForTotal;

	public double counponUsedMoneyForIn;
	
	public double counponUsedMoneyForOut;
    public double userAllowance;

	public double parkSpentHour;
	

    public boolean isDiscount;

	public boolean useCounpon;

	public long counponId;

	public String keepToDate;
	
	public List<TParkService> selectedServices;
	
	//已经选择服务的总费用
	public double serviceTotalFee;
	
	public String userAllowanceDescription;

}
