package com.mc.parking.client.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mc.parking.client.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * @author mxs E-mail:308348194@qq.com
 * @version 创建时间：2015年8月18日 下午4:03:33
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{

	public static final String APP_ID="wx081f466690a80fb0";
  private IWXAPI api;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    api = WXAPIFactory.createWXAPI(this, APP_ID, false);
    api.registerApp(APP_ID);
    api.handleIntent(getIntent(), this);
  }

  @Override
  public void onReq(BaseReq req)
  {
  }

  @Override
  public void onResp(BaseResp resp)
  {
    int result = 0;

    switch (resp.errCode)
    {
    case BaseResp.ErrCode.ERR_OK:
      result = R.string.errcode_success;
      //返回成功，发送广播
      
      Intent intent = new Intent();  
      intent.setAction("success"); 
      intent.putExtra("success", "ok");
      WXEntryActivity.this.sendBroadcast(intent);  
      break;
    case BaseResp.ErrCode.ERR_USER_CANCEL:
      result = R.string.errcode_cancel;
      break;
    case BaseResp.ErrCode.ERR_AUTH_DENIED:
      result = R.string.errcode_deny;
      break;
    default:
      result = R.string.errcode_unknown;
      break;
    }

    finish();
    overridePendingTransition(R.anim.change_in, R.anim.change_out);
  }

}