package com.jobsearchtry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;

import com.jobsearchtry.utils.FileUtils;

 class FileLoader extends AsyncTaskLoader<List<File>> {
	private static final int FILE_OBSERVER_MASK = FileObserver.CREATE
			| FileObserver.DELETE | FileObserver.DELETE_SELF
			| FileObserver.MOVED_FROM | FileObserver.MOVED_TO
			| FileObserver.MODIFY | FileObserver.MOVE_SELF;
	private FileObserver mFileObserver;
	private List<File> mData;
	private final String mPath;

	 FileLoader(Context context, String path) {
		super(context);
		this.mPath = path;
	}

	@Override
	public List<File> loadInBackground() {
		ArrayList<File> list = new ArrayList<>();
		final File pathDir = new File(mPath);
		final File[] dirs = pathDir.listFiles(FileUtils.sDirFilter);
		if (dirs != null) {
			Arrays.sort(dirs, FileUtils.sComparator);
			Collections.addAll(list, dirs);
		}
		final File[] files = pathDir.listFiles(FileUtils.sFileFilter);
		if (files != null) {
			Arrays.sort(files, FileUtils.sComparator);
			Collections.addAll(list, files);
		}
		return list;
	}

	@Override
	public void deliverResult(List<File> data) {
		if (isReset()) {
			onReleaseResources(data);
			return;
		}
		List<File> oldData = mData;
		mData = data;
		if (isStarted())
			super.deliverResult(data);
		if (oldData != null && oldData != data)
			onReleaseResources(oldData);
	}

	@Override
	protected void onStartLoading() {
		if (mData != null)
			deliverResult(mData);
		if (mFileObserver == null) {
			mFileObserver = new FileObserver(mPath, FILE_OBSERVER_MASK) {
				@Override
				public void onEvent(int event, String path) {
					onContentChanged();
				}
			};
		}
		mFileObserver.startWatching();
		if (takeContentChanged() || mData == null)
			forceLoad();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		onStopLoading();
		if (mData != null) {
			onReleaseResources(mData);
			mData = null;
		}
	}

	@Override
	public void onCanceled(List<File> data) {
		super.onCanceled(data);
		onReleaseResources(data);
	}

	private void onReleaseResources(List<File> data) {
		if (mFileObserver != null) {
			mFileObserver.stopWatching();
			mFileObserver = null;
		}
	}
}