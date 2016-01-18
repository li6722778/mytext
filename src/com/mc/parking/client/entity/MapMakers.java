package com.mc.parking.client.entity;

import java.io.Serializable;


/**
 * 地图坐标
 * @author woderchen
 *
 */
public class MapMakers implements Serializable{

	public Long parkLocId;


	public int parkFreeCount;

	public int isOpen;
	
	public double latitude;


	public double longitude;


	public double parkId;

	public double distance;
	public int serviceTotal;
	
}
