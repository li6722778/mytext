package com.mc.parking.client.ui.fragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.navisdk.model.datastruct.SearchPoi;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.mc.parking.client.Constants;
import com.mc.parking.client.PackingApplication;
import com.mc.park.client.R;
import com.mc.parking.client.entity.HistoryEntity;
import com.mc.parking.client.layout.BaseDialogFragment;
import com.mc.parking.client.layout.PullToRefreshListView;
import com.mc.parking.client.layout.PullToRefreshListView.IXListViewListener;
import com.mc.parking.client.layout.PullToRefreshListView.OnRefreshListener;
import com.mc.parking.client.ui.MainActivity;
import com.mc.parking.client.utils.DBHelper;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public class SearchListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<PoiInfo>>, IXListViewListener {

	private String _curFilter; // if non-null, this is the current filter the
								// user has provided.
	private SearchListAdapter _adapter; // the adapter
	private ListView historyView;
	private LinearLayout historyContainer;
	private ImageButton backButton;
	private FrameLayout listContainer;
	private EditText searchEditText;
	private DBHelper dbHelper = null;
	private String edit_context;
	private ImageView clearImageView, searchImageView, voiceImageView;
	public boolean havehistorydata;
	private PullToRefreshListView mListView;
	private SearchListLoader searchListLoader;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 123456;
	private Context context;
	

	// 清除输入框数据 隐藏小键盘
	public void resetStatus() {
		// searchEditText.setText("");
		searchEditText.clearFocus();
		View view = getActivity().getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void setFocus() {
		searchEditText.setFocusable(true);
		searchEditText.setClickable(true);
		searchEditText.setFocusableInTouchMode(true);
		searchEditText.requestFocus();

		InputMethodManager inputmanger = (InputMethodManager) searchEditText
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanger.showSoftInput(searchEditText,
				InputMethodManager.SHOW_FORCED);
		// 得到InputMethodManager的实例
		if (!inputmanger.isActive()) {
			inputmanger.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		setFocus();
	}

	private Handler uiHandler = new Handler();
	private Runnable searchHistoryRunable = new Runnable() {
		@Override
		public void run() {
			try {
				List<HistoryEntity> array = getHelper().getHistoryDao()
						.queryBuilder().orderBy("searchDate", false).query();
				if (array != null && array.size() > 0) {
					havehistorydata = true;
				} else {
					havehistorydata = false;
				}

				historyView.setAdapter(new HistoryAdapter(array));
				displayHistoryViewbackgroud();

			} catch (SQLException e) {
				Log.e("displayHistoryView", e.getMessage(), e);
			}
		}

	};

	public SearchListFragment(EditText searchEditText) {
		this.searchEditText = searchEditText;
	}

	public SearchListFragment() {
		this.searchEditText = null;
	}

	public DBHelper getHelper() {
		if (dbHelper == null) {
			dbHelper = OpenHelperManager.getHelper(getActivity(),
					DBHelper.class);
		}
		return dbHelper;
	}

	// ListFragment methods
	// -----------------------------------------------------------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		ListView lv = (ListView) view.findViewById(android.R.id.list);

		ViewGroup parent = (ViewGroup) lv.getParent();
		context = getActivity().getApplicationContext();

		// Remove ListView and add CustomView in its place
		int lvIndex = parent.indexOfChild(lv);
		parent.removeViewAt(lvIndex);
		RelativeLayout mLinearLayout = (RelativeLayout) inflater.inflate(
				R.layout.fragment_listview, container, false);
		parent.addView(mLinearLayout, lvIndex, lv.getLayoutParams());

		historyView = (ListView) mLinearLayout.findViewById(R.id.historylist);
		historyContainer = (LinearLayout) mLinearLayout
				.findViewById(R.id.historyContainer);
		listContainer = (FrameLayout) mLinearLayout
				.findViewById(R.id.listContainer);

		/* 打开下拉加载更多 */
		mListView = (PullToRefreshListView) listContainer
				.findViewById(android.R.id.list);
		mListView.setXListViewListener(this);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);

		historyView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					HistoryEntity entity = (HistoryEntity) historyView
							.getAdapter().getItem(position);
//					if (searchEditText != null) {
//					searchEditText.setText(entity.getSearchKey());
//					Editable etext = searchEditText.getText();
//					int nowlength = etext.length();
//					Selection.setSelection(etext, nowlength);
//					setFocus();
//				}
				if(searchEditText != null&&entity!=null&&entity.getSearchKey()!=null&&!entity.getSearchKey().trim().equals(""))
				{
					searchEditText.setText(entity.getSearchKey());
					Editable etext = searchEditText.getText();
					int nowlength = etext.length();
					Selection.setSelection(etext, nowlength);
					setFocus();
				searchdestination(entity.getSearchKey().trim().toString());
				}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		backButton = (ImageButton) view.findViewById(R.id.topbarBacks);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FragmentManager manager = getFragmentManager();
				manager.popBackStackImmediate();
				manager.popBackStackImmediate();
				resetStatus();
				return;

			}
		});

		searchEditText = (EditText) view.findViewById(R.id.search_box);
		clearImageView = (ImageView) view.findViewById(R.id.delete_image);
		voiceImageView = (ImageView) view.findViewById(R.id.voice_image);
		searchImageView = (ImageView) view.findViewById(R.id.search_image);
		searchImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchdestination(edit_context);
			}
		});
		voiceImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				View view = getActivity().getWindow().peekDecorView();
				if (view != null) {
					InputMethodManager inputmanger = (InputMethodManager) getActivity()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputmanger.hideSoftInputFromWindow(view.getWindowToken(),
							0);
				}

				try {
					Intent intent = new Intent(
							RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
							RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请开始说话");
					startActivityForResult(intent,
							VOICE_RECOGNITION_REQUEST_CODE);
				} catch (ActivityNotFoundException e) {
					
					BaseDialogFragment noticeDialog = new BaseDialogFragment();
					noticeDialog.setTitle("语音识别");
					noticeDialog.setMessage("您的手机暂不支持语音搜索功能，您可以在各应用商店搜索“语音搜索”进行下载安装");
//					noticeDialog.setPositiveButton("确认",
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog, int id) {
//							//跳转下载页面进行下载
//								}
//							});
					noticeDialog.setNegativeButton("我知道了", null);
					noticeDialog.show(getFragmentManager(), "");
		
				}

			}
		});

		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!searchEditText.getText().toString().trim().equals("")) {
					clearImageView.setVisibility(View.VISIBLE);
					clearImageView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							searchEditText.setText("");
							clearImageView.setVisibility(View.GONE);
							listContainer.setVisibility(View.GONE);
							voiceImageView.setVisibility(View.VISIBLE);
							historyContainer.setVisibility(View.VISIBLE);
						}
					});
				}

				else if (searchEditText.getText().toString().trim().equals("")) {
					clearImageView.setVisibility(View.GONE);
					voiceImageView.setVisibility(View.VISIBLE);
					listContainer.setVisibility(View.GONE);
					historyContainer.setVisibility(View.VISIBLE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				edit_context = searchEditText.getText().toString();
				if (edit_context != null && edit_context.equals("")) {
					voiceImageView.setVisibility(View.VISIBLE);
				} else {
					voiceImageView.setVisibility(View.GONE);
				}
			}

		});

		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// clearImageView.setVisibility(View.VISIBLE);

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchdestination(edit_context);

				}
				return false;
			}

		});

		displayHistoryView();

		return view;
	}

	protected boolean searchdestination(String queryValue) {

		Log.d(getClass().getSimpleName(), "query key::::::::::" + queryValue);
		if (queryValue == null || queryValue.equals("")) {
			Toast.makeText(getActivity(), "目的地不能为空", Toast.LENGTH_SHORT).show();
			return true;

		}

		if (Constants.NETFLAG == false) {

			Toast.makeText(getActivity(), "网络连接失败，请检查网络", Toast.LENGTH_SHORT)
					.show();
			return true;

		}

		else {
			onQueryTextChange(queryValue);
			resetStatus();
			return true;
		}

	}

	public void displayHistoryView() {
		if (historyView == null || historyContainer == null) {
			return;
		}

		uiHandler.post(searchHistoryRunable);

		historyContainer.setVisibility(View.VISIBLE);
		if (isResumed()) {
			listContainer.setVisibility(View.GONE);
		}

		setFocus();

	}

	public void displayHistoryViewbackgroud() {
		if (havehistorydata == true) {
			historyView.setBackgroundResource(R.drawable.contentbg);
			return;
		} else if (havehistorydata == false) {
			historyView.setBackgroundResource(R.color.mainbg);
			return;
		}
		return;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(getClass().getSimpleName(), "===========>onActivityCreated");

		// set an empty filter
		_curFilter = null;

		// has menu
		// setHasOptionsMenu(true);

		// set empty adapter
		_adapter = new SearchListAdapter(getActivity(),
				new ArrayList<PoiInfo>());
		setListAdapter(_adapter);
		// start out with a progress indicator.
		// setListShown(false);

		// prepare the loader. Either re-connect with an existing one, or start
		// a new one.
		// getLoaderManager().initLoader(0, null, this);

	}

	// LoaderManager methods
	// -----------------------------------------------------------------------------------------
	@Override
	public Loader<List<PoiInfo>> onCreateLoader(int id, Bundle args) {

		searchListLoader = new SearchListLoader(getActivity(), _curFilter) {

			@Override
			public void searchDone(int size) {
				mListView.stopLoadMore();
				if (size <= 0) {
					mListView.setPullLoadEnable(false);
				} else {
					mListView.setPullLoadEnable(true);
				}
			}

		};
		return searchListLoader;
	}

	@Override
	public void onLoadFinished(Loader<List<PoiInfo>> loader, List<PoiInfo> data) {
		// Set the new data in the adapter.
		Log.d(getClass().getSimpleName(), "===========>onLoadFinished:" + data);

		_adapter.setData(data);
		listContainer.setVisibility(View.VISIBLE);
		historyContainer.setVisibility(View.GONE);
		// 打开下拉更多的文字
		mListView.setPullLoadEnable(true);
		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
		// mListView.onRefreshComplete();
		// mListView.setOnRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		//
		// Toast.makeText(getActivity(), "test",Toast.LENGTH_SHORT).show();
		// }
		// });
	}

	@Override
	public void onLoaderReset(Loader<List<PoiInfo>> loader) {
		// Android guides set data to null but I need check
		// what happen when I set data as null
		_adapter.setData(new ArrayList<PoiInfo>());
	}

	// // OnQueryTextListener methods
	// //
	// -----------------------------------------------------------------------------------------
	// @Override
	public boolean onQueryTextChange(String newText) {
		// Called when the action bar search text has changed. sUpdate
		// the search filter, and restart the loader to do a new query
		// with this filter.
		setListShown(false);
		_adapter.setData(new ArrayList<PoiInfo>());
		_curFilter = !TextUtils.isEmpty(newText) ? newText : null;
		_adapter.setQuery(_curFilter);
		if (_curFilter != null && !_curFilter.trim().equals("")) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						Dao<HistoryEntity, Integer> dao = getHelper()
								.getHistoryDao();
						List<HistoryEntity> allEntity = dao.queryForAll();
						if (allEntity != null
								&& allEntity.size() >= Constants.maxStoreKey) {
							dao.delete(allEntity.get(0));
						}
						List<HistoryEntity> queryResult = dao.queryBuilder()
								.where().eq("searchKey", _curFilter).query();
						if (queryResult.size() > 0) {
							HistoryEntity entity = queryResult.get(0);
							entity.setSearchDate(new Date());
							dao.update(entity);
						} else {
							HistoryEntity entity = new HistoryEntity();
							entity.setCity(SessionUtils.city);
							entity.setSearchDate(new Date());
							entity.setSearchKey(_curFilter);
							dao.createIfNotExists(entity);
						}
					} catch (SQLException e) {
						Log.e("onQueryTextChange", e.getMessage(), e);
					}

				}

			}).start();

		}

		getLoaderManager().restartLoader(0, null, this);
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (_adapter != null)
			_adapter.setData(new ArrayList<PoiInfo>());

		if (dbHelper != null) {
			OpenHelperManager.releaseHelper();
			dbHelper = null;
		}
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		resetStatus();
		PoiInfo poiInfo = (PoiInfo) _adapter.getItem(position);
		Toast.makeText(getActivity(), poiInfo.name, 1).show();
		FragmentManager manager = getFragmentManager();
//使得返回地图界面不进行resume
		UIUtils.backState=false;
		MainActivity activity = (MainActivity) getActivity();
		manager.popBackStack();
		manager.popBackStack();
		activity.moveToNewLocation(poiInfo.location.latitude,
				poiInfo.location.longitude);
		// super.onDestroy();

	}

	class HistoryAdapter extends BaseAdapter {

		private List<HistoryEntity> listData;

		public HistoryAdapter() {
			super();
			listData = new ArrayList<HistoryEntity>();
		}

		public HistoryAdapter(List<HistoryEntity> listData) {
			super();
			this.listData = listData;
		}

		@Override
		public int getCount() {
			return listData.size();
		}

		@Override
		public HistoryEntity getItem(int position) {
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				LayoutInflater mInflater = (LayoutInflater) getActivity()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = mInflater.inflate(R.layout.item_list_history,
						null);
				holder = new ViewHolder();
				holder.itemString = (TextView) convertView
						.findViewById(R.id.his_item_string);
				holder.deletButton = (ImageButton) convertView
						.findViewById(R.id.his_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final HistoryEntity objStu = listData.get(position);
			holder.itemString.setText(objStu.getSearchKey());
			holder.deletButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						getHelper().getHistoryDao().delete(objStu);
						uiHandler.post(searchHistoryRunable);

					} catch (SQLException e) {
						Log.e("displayHistoryView", e.getMessage(), e);
					}
				}

			});

			return convertView;
		}

	}

	static class ViewHolder {
		TextView itemString;
		TextView itemDate;
		ImageButton deletButton;
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		if (searchListLoader != null) {

			searchListLoader.searchListLoaderMore();

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
				&& resultCode == MainActivity.RESULT_OK) {
			ArrayList<String> matches = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			if (matches.size() > 0) {
				Message msg = new Message();
				msg.obj = matches.get(0);
				jumpHandler.sendMessage(msg);
			}
		}
	}

	private Handler jumpHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String voicecontext = (String) msg.obj;
			voicecontext=voicecontext.substring(0, voicecontext.length()-1);
			searchEditText.setText(voicecontext);
			Editable etext = searchEditText.getText();
			int nowlength = etext.length();
			Selection.setSelection(etext, nowlength);
			searchdestination(edit_context);
			
		};
	};

}
