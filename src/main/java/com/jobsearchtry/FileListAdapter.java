package com.jobsearchtry;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

 class FileListAdapter extends BaseAdapter {
	private final static int ICON_FOLDER = R.drawable.ic_folder;
	private final static int ICON_FILE = R.drawable.ic_file;
	private final LayoutInflater mInflater;
	private List<File> mData = new ArrayList<>();

	 FileListAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}

	public void add(File file) {
		mData.add(file);
		notifyDataSetChanged();
	}

	public void remove(File file) {
		mData.remove(file);
		notifyDataSetChanged();
	}

	 public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}

	@Override
	public File getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	 void setListItems(List<File> data) {
		mData = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null)
			row = mInflater.inflate(R.layout.file, parent, false);
		TextView view = (TextView) row;
		final File file = getItem(position);
		view.setText(file.getName());
		int icon = file.isDirectory() ? ICON_FOLDER : ICON_FILE;
		view.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
		return row;
	}
}