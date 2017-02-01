package com.jobsearchtry.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jobsearchtry.R;

public class TransparentProgressDialog extends Dialog {
	ImageView iv;

	public TransparentProgressDialog(Context context) {
		super(context, R.style.TransparentProgressDialog);

		WindowManager.LayoutParams wlmp = getWindow().getAttributes();
		wlmp.gravity = Gravity.CENTER_HORIZONTAL;
		getWindow().setAttributes(wlmp);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);
		iv = new ImageView(context);
		iv.setImageResource(R.drawable.loadfinal);
//		Glide.with(context).load(R.drawable.loadfinal).placeholder(R.drawable.loadfinal).diskCacheStrategy(DiskCacheStrategy.ALL)
//				.error(R.drawable.loadfinal).into(iv);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.addView(iv, params);
		addContentView(layout, params);
	}

	@Override
	public void show() {
		super.show();
		RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(3000);
		iv.setAnimation(anim);
		iv.startAnimation(anim);
	}
}