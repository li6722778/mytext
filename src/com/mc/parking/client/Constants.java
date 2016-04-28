package com.mc.parking.client;



public class Constants {

	public final static  String CACHE_PROP= "cache_pref" ;
	public final static long VERSION = 2015121801; 
	public final static String APPNAME = "锟斤拷锟斤拷锟斤拷"; 
	//锟斤拷页锟斤拷
	public final static int PAGINATION_PAGESIZE = 10;
	
	public static final int maxStoreKey = 20;
	public static final String HTTP_WEB_PORTAL="file:///android_asset/html/portal.html";
	public static final String HTTP_WEB_RENT="file:///android_asset/html/rent.html";
	public static final String HTTP_WEB_CSRENT="file:///android_asset/html/csrentlist.html";
	public static final String HTTP_WEB_CASH="file:///android_asset/html/cash.html";
	
//	public static final String CITY_DEFAULT="锟缴讹拷";
//	public static final int CITY_DEFAULT_CODE=75;
	
	public static final String CITY_DEFAULT="锟斤拷锟斤拷";
	public static final int CITY_DEFAULT_CODE=58;
	
	public static final int BARCODE_WIDTH=300;
	public static final int BARCODE_HEIGHT=300;
	
	
	public static final int PARKING_TYPE_IN=1;
	public static final int PARKING_TYPE_OUT=2;
	public static final int PARKING_STATUS_OPEN=1;
	public static final int PARKING_STATUS_CLOSE=0;
	
	public static final int ORDER_TYPE_START=1;
	public static final int ORDER_TYPE_FINISH=2;
	public static final int ORDER_TYPE_PENDING=5;
	public static final int ORDER_TYPE_OVERDUE=3;
	public static final int ORDER_TYPE_EXCPTION=4;
	public static final int ORDER_TYPE_DOING=6;
	
	public static final int PAYMENT_STATUS_START=1;
	public static final int PAYMENT_STATUS_FINISH=2;
	public static final int PAYMENT_STATUS_PENDING=5;
	public static final int PAYMENT_STATUS_EXCPTION=3;
	
	
	public static final int PAYMENT_TYPE_ZFB=1;
	public static final int PAYMENT_TYPE_WEIXIN=2;
	public static final int PAYMENT_TYPE_YL=3;	
	public static final int PAYMENT_COUPON=4;
	public static final int PAYMENT_TYPE_CASH=9;	

	public static final int PAYMENT_COUPONZFB=PAYMENT_TYPE_ZFB+PAYMENT_COUPON;
	public static final int PAYMENT_COUPONCASH=PAYMENT_TYPE_CASH+PAYMENT_COUPON;
	public static final int PAYMENT_LIJIAN=21;
	public static final int PAYMENT_SERVICE=999;
	
	public static final int USER_TYPE_NORMAL=10;
	public static final int USER_TYPE_PADMIN=20;
	public static final int USER_TYPE_PSADMIN=21;
	public static final int USER_TYPE_MADMIN=30;
	public static final int USER_TYPE_MSADMIN=31;
	public static final int USER_TYPE_SADMIN=999;
	
	//锟斤拷锟斤拷状态1.锟斤拷锟斤拷时锟戒。2锟斤拷锟斤拷时锟戒。3锟斤拷锟绞憋拷锟�
	public static final int TAKE_MONEY_ASK=1;
	public static final int TAKE_MONEY_HANDLE=2;
	public static final int TAKE_MONEY_COMPLETE=3;
	
	//锟街斤拷支锟斤拷锟斤拷锟节第讹拷位锟斤拷千锟斤拷要锟斤拷锟斤拷锟斤拷位锟斤拷
	public static final String[] PAY_WAY=new String[]{"支锟斤拷锟斤拷","微锟斤拷支锟斤拷"};
	
	
	
	/*remote server configuration*/
	public static final String SERVER_TOPPAGE="http://114.215.155.185/index.html";
	//public static final String HTTP="http://114.215.155.185";

	//############only for test#################
	//public static final String HTTP="http://localhost:9000";
	//public static final String HTTP="http://192.168.0.118:9000";
	public static final String HTTP="http://192.168.1.105:9000";
	public static final String MENUSHAREPAGE="http://www.chebolechina.com/app/scxz5/";

	
	public static final String KEY_MAP = "RT4hP7oESncz1uKWuFz8T8A";
	public static class Extra {
		public static final String IMAGES = "com.mc.parking.client.IMAGES";
		public static final String IMAGE_POSITION = "com.mc.parking.client.IMAGE_POSITION";
		
	}
	public static class ResultCode{
		public static final int HOME = 999;
		public static final int NAVATIGOR_START = 998;
		public static final int NO_ORDER = 1000;
		public static final int NON_ORDER = 1001;
		public static final int ORDER_AUTH = 997;
		public static final int NAVATIGOR_END = 996;
		public static final int ADDPARK_END = 995;
		public static final int ORDER_LIST_RELOAD = 994;

	}
	
	
	public static final int BAIDUPOIDISTANCE=1000;
	public static boolean  NETFLAG=true;
	
	public static final int INPARK=0;
	public static final int OUTPARK=1;
	
	
	
	public static  int NEWMESSAGENOTICE=1;
	public static  int NEWMESSAGENOTICEVOICE=1;
	public static  int NEWMESSAGENOTICEVIBRATE=1;
	
    public static final Double MOVE_UNIT=0.000020;
	
	public static final Double EX_UNIT=0.000050;
	
	
	public static final int OrderbyTime =1;
	public static final int Orderbysecond =0;
	//锟斤拷页layout 透锟斤拷锟斤拷 锟斤拷锟斤拷钮透锟斤拷锟斤拷
	public static final int  Alpha=230;
	
	public static  int  loginperiod =30;
	
	
	
	public static final int USER_SCAN_BACK=10;
	
	
	//锟角凤拷锟揭伙拷蔚锟铰糰pp
	public static boolean isfirst;
	
	//锟斤拷证锟诫倒锟斤拷时
	public static Long Verifycodetime=120000L;
	
	public static String verifycodeNumber="10657563288924";
	
	
	//时锟斤拷锟角匡拷
	public static final String TIME_NULL="--:--";
	
	public static final int PARK_STATE_OPEN=1;
	
	public static final int PARK_STATE_CLOSE=0;
	
	//锟斤拷锟斤拷啥锟斤拷锟阶刺�
	public static final int PARKORDER_STATE_COMPLETE=2;
	public static final int PARKORDER_STATE_ERROR=4;
	
	
	
	//微锟斤拷锟斤拷锟�
    //appid
    //锟斤拷同时锟睫革拷  androidmanifest.xml锟斤拷锟芥，.PayActivityd锟斤拷锟斤拷锟斤拷锟�<data android:scheme="wxb4ba3c02aa476ea1"/>为锟斤拷锟斤拷锟矫碉拷appid
  public static final String APP_ID = "wx4eb75affbe3a59d5";




  //锟教伙拷锟斤拷
   public static final String MCH_ID = "1233848001";



	
    
    //微锟脚凤拷锟斤拷状态
    
    public static int backstate=0;
    
    //微锟斤拷errocode锟斤拷微锟斤拷支锟斤拷锟斤拷锟截的ｏ拷
    public static final int  WXPAY_SUCCESS=0;
  
    public static final int WXPAY_EXCPTION=-2;  //支锟斤拷锟斤拷途锟剿筹拷
   

	//微锟脚凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
    public static String WXSHARE_TITLE=null;
    public static String WXSHARE_CONTEXT=null;
    
    //锟斤拷锟斤拷状态
    public static final int SERVICE_FINISH=1;
    public static final int SERVICE_REJECT=3;
    
    
}

