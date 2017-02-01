package com.jobsearchtry.swipe;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;


public class SwipeMenu {

	private final Context mContext;
	private final List<SwipeMenuItem> mItems;
	private int mViewType;

	 SwipeMenu(Context context) {
		mContext = context;
		mItems = new ArrayList<>();
	}

	public Context getContext() {
		return mContext;
	}

	public void addMenuItem(SwipeMenuItem item) {
		mItems.add(item);
	}



	 List<SwipeMenuItem> getMenuItems() {
		return mItems;
	}



	 void setViewType(int viewType) {
		this.mViewType = viewType;
	}

}
