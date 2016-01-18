package com.mc.parking.client.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.baidu.platform.comapi.map.B;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "TParkInfo")
public class TParkInfoEntity implements Serializable, Cloneable {

	@DatabaseField(generatedId = true)
	public Long parkId;

	@DatabaseField
	public String parkname;

	public String detail;

	@DatabaseField
	public String address = "";

	@DatabaseField
	public String vender;

	@DatabaseField
	public String owner;
	
	@DatabaseField
	public String cityname;

	@DatabaseField
	public long parkPhone;

	@DatabaseField
	public int teleable;
	
	@DatabaseField
	public long ownerPhone;
	public String venderBankName;

	public String venderBankNumber;

	public float averagerat;

	@DatabaseField
	public int feeType = 1;
	@DatabaseField
	public int feeTypeSecInScopeHours;
	@DatabaseField
	public double feeTypeSecInScopeHourMoney;
	@DatabaseField
	public double feeTypeSecOutScopeHourMoney;
	@DatabaseField
	public double feeTypefixedHourMoney;
	@DatabaseField
	public int isDiscountAllday;
	@DatabaseField
	public int isDiscountSec;
	@DatabaseField
	public double discountHourAlldayMoney;
	@DatabaseField
	public double discountSecHourMoney;
	@DatabaseField
	public Date discountSecStartHour;
	@DatabaseField
	public Date discountSecEndHour;
	@DatabaseField
	public int feeTypeSecMinuteOfActivite;
	@DatabaseField
	public int feeTypeFixedMinuteOfInActivite;

	public List<TParkService> services;
	
	public List<TParkInfo_ImgEntity> imgUrlArray;

	public List<TParkInfo_LocEntity> latLngArray;
	public List<TParkInfo_Product> produArray;
	@DatabaseField
	public Date createDate;
	@DatabaseField
	public Date updateDate;
	@DatabaseField
	public String createPerson;
	@DatabaseField
	public String updatePerson;

	public TParkInfoEntity(Long parkId, String parkname, String detail,
			String address, String vender, String owner, long ownerPhone,
			String venderBankName, String venderBankNumber, int feeType,
			int feeTypeSecInScopeHours, double feeTypeSecInScopeHourMoney,
			double feeTypeSecOutScopeHourMoney, double feeTypefixedHourMoney,
			int isDiscountAllday, int isDiscountSec,
			double discountHourAlldayMoney, double discountSecHourMoney,
			Date discountSecStartHour, Date discountSecEndHour,
			List<TParkInfo_ImgEntity> imgUrlArray,
			List<TParkInfo_LocEntity> latLngArray, Date createDate,
			Date updateDate, String createPerson, String updatePerson) {
		super();
		this.parkId = parkId;
		this.parkname = parkname;
		this.detail = detail;
		this.address = address;
		this.vender = vender;
		this.owner = owner;
		this.ownerPhone = ownerPhone;
		this.venderBankName = venderBankName;
		this.venderBankNumber = venderBankNumber;
		this.feeType = feeType;
		this.feeTypeSecInScopeHours = feeTypeSecInScopeHours;
		this.feeTypeSecInScopeHourMoney = feeTypeSecInScopeHourMoney;
		this.feeTypeSecOutScopeHourMoney = feeTypeSecOutScopeHourMoney;
		this.feeTypefixedHourMoney = feeTypefixedHourMoney;
		this.isDiscountAllday = isDiscountAllday;
		this.isDiscountSec = isDiscountSec;
		this.discountHourAlldayMoney = discountHourAlldayMoney;
		this.discountSecHourMoney = discountSecHourMoney;
		this.discountSecStartHour = discountSecStartHour;
		this.discountSecEndHour = discountSecEndHour;
		this.imgUrlArray = imgUrlArray;
		this.latLngArray = latLngArray;
		this.createDate = createDate;
		this.updateDate = updateDate;
		this.createPerson = createPerson;
		this.updatePerson = updatePerson;
	}

	public TParkInfoEntity() {
		// TODO Auto-generated constructor stub
	}

	public TParkInfoEntity clone() {
		TParkInfoEntity o = null;
		try {
			o = (TParkInfoEntity) super.clone();
			o.imgUrlArray = new ArrayList<TParkInfo_ImgEntity>();
			for (int i = 0; i < imgUrlArray.size(); i++) {
				TParkInfo_ImgEntity temp = (TParkInfo_ImgEntity) imgUrlArray
						.get(i).clone();
				o.imgUrlArray.add(temp);
			}
			o.latLngArray = new ArrayList<TParkInfo_LocEntity>();
			for (int i = 0; i < latLngArray.size(); i++) {
				TParkInfo_LocEntity temp = (TParkInfo_LocEntity) latLngArray
						.get(i).clone();
				o.latLngArray.add(temp);
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return o;
	}
}
