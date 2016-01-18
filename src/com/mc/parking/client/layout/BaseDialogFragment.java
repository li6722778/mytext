package com.mc.parking.client.layout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.mc.parking.client.R;

public class BaseDialogFragment extends DialogFragment {
	private String message;
	private String title;
	private Activity currentActivity;

	private HashMap<String, DialogInterface.OnClickListener> positiveButtonEvent;
	private HashMap<String, DialogInterface.OnClickListener> negativeButtonEvent;
	private HashMap<String, DialogInterface.OnClickListener> neutralButtonEvent;

	public BaseDialogFragment() {
		this(null);
	}
	
	public BaseDialogFragment(Activity currentActivity) {
		super();
		this.currentActivity = currentActivity;
		setStyle(R.style.BaseCustomDialog, R.style.BaseCustomDialog);
		message = "";
		title = "";
		positiveButtonEvent = new HashMap<String, DialogInterface.OnClickListener>();
		negativeButtonEvent = new HashMap<String, DialogInterface.OnClickListener>();
		neutralButtonEvent = new HashMap<String, DialogInterface.OnClickListener>();
	}

	public void setPositiveButton(String buttonName,
			DialogInterface.OnClickListener listener) {
		positiveButtonEvent.put(buttonName, listener);
	}

	public void setNegativeButton(String buttonName,
			DialogInterface.OnClickListener listener) {
		negativeButtonEvent.put(buttonName, listener);
	}

	public void setNeutralButton(String buttonName,
			DialogInterface.OnClickListener listener) {
		neutralButtonEvent.put(buttonName, listener);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void show(FragmentManager manager, String tag) {
		if (message == null || message.trim().equals(""))
			return;
		try {
			super.show(manager, tag);
		} catch (Exception e) {
			Log.e("BaseDialogFragment", e.getMessage(), e);
		}
	}

	public int show(FragmentTransaction fragmentTransaction, String tag) {
		if (message == null || message.trim().equals(""))
			return -1;
		try {
			return super.show(fragmentTransaction, tag);
		} catch (Exception e) {
			Log.e("BaseDialogFragment", e.getMessage(), e);
		}
		return -1;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		if(currentActivity==null){
		   currentActivity = getActivity();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
		if (title != null && !title.trim().equals("")) {
			builder.setTitle(title);
		}
		builder.setMessage(message);

		if (positiveButtonEvent.size() > 0) {
			Set<String> keyArray = positiveButtonEvent.keySet();
			Iterator<String> it = keyArray.iterator();
			if (it.hasNext()) {
				String key = it.next();
				builder.setPositiveButton(key, positiveButtonEvent.get(key));
			}

		}

		if (negativeButtonEvent.size() > 0) {
			Set<String> keyArray = negativeButtonEvent.keySet();
			Iterator<String> it = keyArray.iterator();
			if (it.hasNext()) {
				String key = it.next();
				builder.setNegativeButton(key, negativeButtonEvent.get(key));
			}

		}

		if (neutralButtonEvent.size() > 0) {
			Set<String> keyArray = neutralButtonEvent.keySet();
			Iterator<String> it = keyArray.iterator();
			if (it.hasNext()) {
				String key = it.next();
				builder.setNeutralButton(key, neutralButtonEvent.get(key));
			}

		}

		// Create the AlertDialog object and return it
		return builder.create();
	}
	
	@Override  
    public void onSaveInstanceState(Bundle outState) {  
		super.onSaveInstanceState(outState);
	}
}