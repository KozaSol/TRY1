package com.jobsearchtry.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import com.jobsearchtry.Homepage;
import com.jobsearchtry.R;

public class CustomAutoCompleteTextView extends AutoCompleteTextView {

	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}
	@Override
	public boolean enoughToFilter() {
		return true;
	}
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new Handler(Homepage.callback2).sendEmptyMessage(0);
		}
		return false;
	}
	private void init(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.MyTextView);
			String fontName = a.getString(R.styleable.MyTextView_fontName);
			if (fontName != null) {
				Typeface myTypeface = Typeface.createFromAsset(getContext()
						.getAssets(), fontName);
				setTypeface(myTypeface);
			}
			a.recycle();
		}
	}
}
