package com.mc.parking.client.layout;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mc.parking.client.R;

public class CustomProgressDialog extends Dialog {
	 private Context context = null;
	 private static ImageView imageView;
	    private static CustomProgressDialog customProgressDialog = null;
	     
	    public CustomProgressDialog(Context context){
	        super(context);
	        this.context = context;
	    }
	     
	    public CustomProgressDialog(Context context, int theme) {
	        super(context, theme);
	    }
	     
	    public static CustomProgressDialog createDialog(Context context){
	        customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
	        customProgressDialog.setContentView(R.layout.dialog_progress);
	        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
	        imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
	        
	        customProgressDialog.setCanceledOnTouchOutside(false);
        
	        return customProgressDialog;
	    }
	  
	    public void onWindowFocusChanged(boolean hasFocus){
	    	super.onWindowFocusChanged(hasFocus);
	        if (customProgressDialog == null){
	            return;
	        }
	        
	        Animation anim = AnimationUtils.loadAnimation(getContext(),
					R.anim.loading);
	        imageView.clearAnimation();
	        imageView.startAnimation(anim);
	        imageView.setVisibility(View.VISIBLE);
	    }
	  
	    public void clear(){
	    	imageView.clearAnimation();
	    	imageView.setVisibility(View.GONE);
	    }
	    
	    /**
	     *
	     * [Summary]
	     *       setTitile 标题
	     * @param strTitle
	     * @return
	     *
	     */
	    public CustomProgressDialog setTitile(String strTitle){
	        return customProgressDialog;
	    }
	     
	    /**
	     *
	     * [Summary]
	     *       setMessage 提示内容
	     * @param strMessage
	     * @return
	     *
	     */
	    public CustomProgressDialog setMessage(String strMessage){
	        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);
	         
	        if (tvMsg != null){
	            tvMsg.setText(strMessage);
	        }
	         
	        return customProgressDialog;
	    }
}
