package com.jobsearchtry.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.jobsearchtry.R;

public class MyButton extends Button {

	public MyButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public MyButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public MyButton(Context context) {
		super(context);
		init(null);
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