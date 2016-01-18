package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

public class TOrder_Py implements Serializable{



	
	public Long parkPyId;
	



	public OrderEntity order;
		

	public double payTotal;
	

	public double payActu;
	

	public double couponUsed;
	

	public int payMethod; // 1：支付宝 9：现金
	

	public int ackStatus;  //0:初始状态 1：支付成功 2：等待结果确认 3:支付失败
	
	

	public Date payDate;

	
	public Date ackDate;

	public String createPerson;
	
	//付款单名，如：停车费，洗车费，除雪费
	public String paymentName;

	//费用类型:对应 停车费用:1； 增值服务：2
	public int paymentType;

	//付款单状态,1:正常，2：pending（也许是还需要后续服务，如停车场增值服务）
	public int paymentStatus;

	
}
