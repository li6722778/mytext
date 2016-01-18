package com.mc.parking.client.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.comapi.mapcontrol.BNMapController;
import com.baidu.navisdk.comapi.routeplan.BNRoutePlaner;
import com.baidu.navisdk.comapi.tts.BNTTSPlayer;
import com.baidu.navisdk.comapi.tts.BNavigatorTTSPlayer;
import com.baidu.navisdk.comapi.tts.IBNTTSPlayerListener;
import com.baidu.navisdk.model.datastruct.LocData;
import com.baidu.navisdk.model.datastruct.SensorData;
import com.baidu.navisdk.ui.routeguide.BNavigator;
import com.baidu.navisdk.ui.routeguide.IBNavigatorListener;
import com.baidu.navisdk.ui.widget.RoutePlanObserver;
import com.baidu.navisdk.ui.widget.RoutePlanObserver.IJumpToDownloadListener;
import com.baidu.nplatform.comapi.map.MapGLSurfaceView;
import com.mc.parking.client.Constants;
import com.mc.parking.client.layout.BaseActivity;

public class NavigationActivity extends BaseActivity {

	private int FLAG = 0;
	private IBNavigatorListener mBNavigatorListener = new IBNavigatorListener() {

		@Override
		public void onYawingRequestSuccess() {
			// TODO ƫ������ɹ�

		}

		@Override
		public void onYawingRequestStart() {
			// TODO ��ʼƫ������

		}

		@Override
		public void onPageJump(int jumpTiming, Object arg) {
			// TODO ҳ����ת�ص�

			if (IBNavigatorListener.PAGE_JUMP_WHEN_GUIDE_END == jumpTiming) {

				if (FLAG == 1) {
					setResult(Constants.ResultCode.NO_ORDER);
					finish();

				} else {
					setResult(Constants.ResultCode.ORDER_AUTH, getIntent());
					finish();

				}

			} else if (IBNavigatorListener.PAGE_JUMP_WHEN_ROUTE_PLAN_FAIL == jumpTiming) {

				if (FLAG == 1) {
					setResult(Constants.ResultCode.NO_ORDER);
					finish();

				} else {
					setResult(3);
					finish();
				}

			}
		}

		@Override
		public void notifyGPSStatusData(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyLoacteData(LocData arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyNmeaData(String arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifySensorData(SensorData arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifyStartNav() {
			// TODO Auto-generated method stub
			BaiduNaviManager.getInstance().dismissWaitProgressDialog();
		}

		@Override
		public void notifyViewModeChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ����NmapView
		MapGLSurfaceView nMapView = BaiduNaviManager.getInstance()
				.createNMapView(this);

		// ����������ͼ
		View navigatorView = BNavigator.getInstance().init(
				NavigationActivity.this, getIntent().getExtras(), nMapView);

		FLAG = getIntent().getFlags();

		// �����ͼ
		setContentView(navigatorView);

		BNavigator.getInstance().setListener(mBNavigatorListener);
		BNavigator.getInstance().startNav();

		// ��ʼ��TTS. ������Ҳ����ʹ�ö���TTSģ�飬����ʹ�õ���SDK�ṩ��TTS
		BNTTSPlayer.initPlayer();

		// ����TTS���Żص�
		BNavigatorTTSPlayer.setTTSPlayerListener(new IBNTTSPlayerListener() {

			@Override
			public int playTTSText(String arg0, int arg1) {
				// �����߿���ʹ������TTS��API
				return BNTTSPlayer.playTTSText(arg0, arg1);
			}

			@Override
			public void phoneHangUp() {
				// �ֻ��Ҷ�
			}

			@Override
			public void phoneCalling() {
				// ͨ����
			}

			@Override
			public int getTTSState() {
				// �����߿���ʹ������TTS��API,
				return BNTTSPlayer.getTTSState();
			}
		});

		BNRoutePlaner.getInstance().setObserver(
				new RoutePlanObserver(this, new IJumpToDownloadListener() {

					@Override
					public void onJumpToDownloadOfflineData() {
						// TODO Auto-generated method stub

					}
				}));

	}

	@Override
	public void onResume() {
		BNavigator.getInstance().resume();
		super.onResume();
		BNMapController.getInstance().onResume();
	};

	@Override
	public void onPause() {
		BNavigator.getInstance().pause();
		super.onPause();
		BNMapController.getInstance().onPause();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		BNavigator.getInstance().onConfigurationChanged(newConfig);
		super.onConfigurationChanged(newConfig);
	}

	public void onBackPressed() {
		BNavigator.getInstance().onBackPressed();
	}

	@Override
	public void onDestroy() {
		BNavigator.destory();
		BNRoutePlaner.getInstance().setObserver(null);
		super.onDestroy();
	}
}