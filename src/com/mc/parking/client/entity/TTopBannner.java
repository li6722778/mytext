package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


public class TTopBannner implements Serializable{

	private static final long serialVersionUID = 1L;
	
	
	public Long id;
	
	//图片HTTP服务器,如http://127.0.0.1:9000/cblimg
	public String imgUrlHeader;

	//图片的后面路径,如/banner/23.jpg; 正确的显示图片的方式是 imgUrlHeader+imgUrlPath
	public String imgUrlPath;
	
	//描述信息，这个描述信息，显示在banner上的滚动文字中
	public String detail;
	
	// 服务状态，1:native UI，点击后打开原生ui,获取用户信息。2:web UI,点击后，跳转到web view。3:宣传,点击后没有反应
	public int bannerType;
	
	//如果bannerType＝＝2，这里才有效，点击后跳转到那个url
	public String clickurl;
	
	// 服务状态，1:自动出现，2:手动点击出现
	public int autoappear;
	
	// 服务状态，0:未开放，1:开放
	public int bannnerStatus;
	
	//广告对应的服务id
	public long serviceId;
	
	//广告对应的服务entity
	public List<TParkService> parkServiceBean;
	

	public Date updateDate;
	
	//广告说明
	public String formComments;
	public String updateName;
	//返回给用户前一次车位订单的位置，用于用户地址的默认值。如果地址为null或空串，就用客户端sessionutils里面的地址
		public String cacheAddress;
}
