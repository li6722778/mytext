package com.mc.parking.client.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.mc.parking.client.Constants;
import com.mc.parking.client.utils.SessionUtils;
import com.mc.parking.client.utils.UIUtils;

public abstract class SearchListLoader extends AsyncTaskLoader<List<PoiInfo>> {
	private List<PoiInfo> _data;
	private PoiSearch mSearch;
	public boolean searching;
	private List<PoiInfo> resultData;
	private List<PoiInfo> tempData;
	private int index, totalpage, totalcount;
	private String queryFiler;
	private Context context;

	public abstract void searchDone(int size);
	
	/**
	 * 
	 * @param context
	 *            Context The context
	 * @param queryFiler
	 *            String Set to null for not use it
	 */
	public SearchListLoader(Context context, String queryFiler) {
		super(context);
		this.context = context;
		this.queryFiler = queryFiler;
		searching = true;
		resultData = new ArrayList<PoiInfo>();
		tempData = new ArrayList<PoiInfo>();
		index = 0;
		totalpage = 0;
		totalcount = 0;
		
		if (mSearch == null) {
			mSearch = PoiSearch.newInstance();
			OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
				public void onGetPoiResult(PoiResult result) {
					tempData = result.getAllPoi();
					totalpage = result.getTotalPageNum();
					totalcount = result.getTotalPoiNum();

					// 获取poi检索结果
					if (tempData != null)
						resultData.addAll(tempData);

					Log.d(SearchListLoader.class.getSimpleName(),
							"OnGetPoiSearchResultListener got result："
									+ resultData);
					if (tempData == null || tempData.size() == 0) {
						Toast.makeText(getContext(), "没有检索到更多的数据.",
								Toast.LENGTH_SHORT).show();
						searchDone(0);
					} else {
						
						
						//显示分页信息
						UIUtils.displayPaginationInfo(getContext(), index, Constants.PAGINATION_PAGESIZE+10, totalcount);
						
						searchDone(resultData.size());

					}
					// 获取POI检索结果
					searching = false;
					
					
				}

				public void onGetPoiDetailResult(PoiDetailResult result) {
					// 获取Place详情页检索结果

				}
			};
			mSearch.setOnGetPoiSearchResultListener(poiListener);
		}

		String city = SessionUtils.city;
		Log.d("SearchListLoader", "QUERY CITY:" + city + "KEY:" + queryFiler);

		if (city != null && !city.trim().equals("")) {

			mSearch.searchInCity((new PoiCitySearchOption())
					.city(SessionUtils.city).keyword(queryFiler)
					.pageCapacity(20).pageNum(index));
		} else {
			// mSearch.searchInCity((new PoiCitySearchOption())
			// .keyword(queryFiler).pageCapacity(20));
		}
	}

	public void searchListLoaderMore() {

		index++;
		if (index <= totalpage) {
			searching = true;
			String city = SessionUtils.city;
			Log.d("SearchListLoader", "QUERY CITY:" + city + "KEY:"
					+ queryFiler);

			if (city != null && !city.trim().equals("")) {

				mSearch.searchInCity((new PoiCitySearchOption())
						.city(SessionUtils.city).keyword(queryFiler)
						.pageCapacity(20).pageNum(index));
			}
		} else {
			return;
		}

	}

	/**
	 * This is where the bulk of our work is done. This function is called in a
	 * background thread and should generate a new set of data to be published
	 * by the loader.
	 */
	@Override
	public List<PoiInfo> loadInBackground() {

		while (searching) {
			try {
				Thread.sleep(200l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return resultData;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(List<PoiInfo> data) {
		if (isReset()) {
			// The Loader has been reset; ignore the result and invalidate the
			// data.
			onReleaseResources(data);
		}
		List<PoiInfo> oldData = _data;
		_data = data;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately deliver
			// its results.
			super.deliverResult(data);
		}

		// At this point we can release the resources associated with 'oldApps'
		// if needed; now
		// that the new result is delivered we know that it is no longer in use.
		if (oldData != null && oldData != data) {
			onReleaseResources(oldData);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (_data != null) {
			// Deliver any previously loaded data immediately.
			deliverResult(_data);
		}

		if (takeContentChanged() || _data == null) {
			// When the observer detects a change, it should call
			// onContentChanged()
			// on the Loader, which will cause the next call to
			// takeContentChanged()
			// to return true. If this is ever the case (or if the current data
			// is
			// null), we force a new load.
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(List<PoiInfo> data) {
		super.onCanceled(data);
		// At this point we can release the resources associated with 'apps' if
		// needed.
		onReleaseResources(data);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'apps'
		// if needed.
		if (_data != null) {
			onReleaseResources(_data);
			_data = null;
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	protected void onReleaseResources(List<PoiInfo> apps) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}

}
