package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;





import com.google.gson.annotations.Expose;

public class TServiceOrder implements Serializable{
	private static final long serialVersionUID = 1L;

	@Expose
	public long orderId;

	//这里对应的就是service里面的serviceName
	@Expose
	public String orderName;
	
	//界面的服务地址
	@Expose
	public String contactAddress;
	
	//界面的联系电话
	@Expose
	public String contactNumber;
	
	//界面的车牌号码
	@Expose
	public String carLisence;

	
	//这里就是服务标准，对应service里面的serviceDetailForApp
	@Expose
	public String serviceDetailForApp;
	
	//packservie id
	@Expose
	public long serviceId;
	
	//供应商id
	@Expose
	public long venderId;
	
	//供应商名字
	@Expose
	public String venderName;
	
	//订单状态，1:开始，2:完成，3:超时，4，取消 6进行中
	@Expose
	public int orderStatus;
	
	//订单城市
	@Expose
	public String orderCity;
	
	//订单创建人
	@Expose
	public long createUserPhone;
	
	//订单提交的时间
	@Expose
	public Date orderStartDate;
	
	//订单完成的时间
	@Expose
	public Date orderEndDate;
	
	//付款总额
	@Expose
	public double payTotal;
	
	//实际付款
	@Expose
	public double payActu;
	
	//付款方式 // 1：支付宝 2：微信支付
	@Expose
	public int payMethod;
	
	//支付状态 0:初始状态 1：支付成功 2：等待结果确认 3:支付失败
	@Expose
	public int ackStatus; 
	
	//付款日期
	@Expose
	public Date payDate;

	//支付响应日期
	@Expose
	public Date ackDate;
	
	@Expose
	public String orderDetail;
	//服务预计结束时间
		@Expose
		public int estimate;
		
		//供应商官方电话
		@Expose
		public String supplyPhone;
		
		//上门服务联系电话
		@Expose
		public String venderServicePhone;
		
		//上门服务联系人
		@Expose
		public String venderServicePerson;
		//供应商说明
		public String orderSupplyComments;
}
