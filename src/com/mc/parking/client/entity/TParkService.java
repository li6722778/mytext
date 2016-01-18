package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;

public class TParkService implements Serializable,Cloneable {

	
	public long id;


	public long serviceId;


	public long ownerId;

	//服务类型.1：车泊乐团队,2：停车场自建
	public int serviceType;

	//服务名
	public String serviceName;

	//服务描述
	public String serviceDetail;

	//服务状态，0:停止中，1:运行中
	public int serviceStatus;

	//可以提供多少次服务，-1不限制

	public int serviceLimit;

	//单次服务费用
	public double serviceFee;

	//多少小时收一次费，0:不安小时收费，1～N:具体小时数目
	public int feePreHour;

	//强制首选，0:默认不选中，1:强制选中
	public int forceSelection;

	//这个字段我会把serviceDetail和serviceFee合起来，app端就用这个字段显示详情
	public String serviceDetailForApp;

	//创建和更新时间
	public Date updateDate;

	//更新人
	public String updatePerson;
	
	public TSupply supplyInfo;
	public TParkService clone() {  
		TParkService o = null;  
        try {  
            o = (TParkService) super.clone();  
        } catch (CloneNotSupportedException e) {  
            e.printStackTrace();  
        }  
        return o;  
    }

}
