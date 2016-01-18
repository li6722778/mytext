package com.mc.parking.client.utils;

/**
 * Log output for global app.
 * @author woderchen
 *
 */
public class Log {
	private static final boolean LOG_MODE_DEBUG = true; 
    
	public static final int DEBUG = android.util.Log.DEBUG;
	public static final int INFO = android.util.Log.INFO;
	public static final int ERROR = android.util.Log.ERROR;
	
	public static boolean isLoggable(String tag, int level){
         return android.util.Log.isLoggable(tag,level);
	}
	
    public static void v(String tag, String msg) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.v(tag, msg); 
        } 
    } 
    public static void v(String tag, String msg, Throwable tr) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.v(tag, msg, tr); 
        } 
    } 
    public static void d(String tag, String msg) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.d(tag, msg); 
        } 
    } 
    public static void d(String tag, String msg, Throwable tr) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.d(tag, msg, tr); 
        } 
    } 
    public static void i(String tag, String msg) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.i(tag, msg); 
        } 
    } 
    public static void i(String tag, String msg, Throwable tr) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.i(tag, msg, tr); 
        } 
    } 
    public static void w(String tag, String msg) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.w(tag, msg); 
        } 
    } 
    public static void w(String tag, String msg, Throwable tr) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.w(tag, msg, tr); 
        } 
    } 
    public static void w(String tag, Throwable tr) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.w(tag, tr); 
        } 
    } 
    public static void e(String tag, String msg) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.e(tag, msg); 
        } 
    } 
    public static void e(String tag, String msg, Throwable tr) { 
        if(LOG_MODE_DEBUG) { 
            android.util.Log.e(tag, msg, tr); 
        } 
    } 
}
