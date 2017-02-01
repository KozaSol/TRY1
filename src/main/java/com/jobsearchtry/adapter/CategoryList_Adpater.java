package com.jobsearchtry.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jobsearchtry.R;
import com.jobsearchtry.wrapper.JS_CATEGORY;

import java.util.ArrayList;

public class CategoryList_Adpater extends ArrayAdapter<JS_CATEGORY> {
    private ArrayList<JS_CATEGORY> category = new ArrayList<>();
    private final Activity activity;
    ProgressDialog pg;

    public CategoryList_Adpater(Activity a, int layoutResourceId,
                                ArrayList<JS_CATEGORY> category) {
        super(a, layoutResourceId, category);
        activity = a;
        this.category = category;
    }

    public void setGridData(ArrayList<JS_CATEGORY> category) {
        this.category = category;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        LayoutInflater mInflater = (LayoutInflater) activity
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jscategory_row, parent,
                    false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView
                    .findViewById(R.id.grid_item_image);
            holder.rolename = (TextView) convertView.findViewById(R.id.gridcategoryname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /*Glide.with(activity).load(category.get(position).getImg())
				.placeholder(R.drawable.ic_launchernew).diskCacheStrategy(DiskCacheStrategy.ALL)
				.error(R.drawable.ic_launchernew).into(holder.imageView);*/
//		pg = new ProgressDialog(activity, R.style.MyTheme);
//		pg.setCancelable(false);
//		pg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		pg.setIndeterminate(true);
//		pg.setIndeterminateDrawable(activity.getResources().getDrawable(R.drawable.custom_progress_dialog_animation));
//		pg.show();
        Glide.with(activity).load(category.get(position).getImg()).placeholder(R.drawable.ic_launchernew).diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.ic_launchernew).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {

                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//				if (pg != null && pg.isShowing())
//					pg.dismiss();
                return false;
            }
        }).into(holder.imageView);
        String languages = activity.getResources().getConfiguration().locale.getDisplayLanguage();
        holder.rolename.setText(category.get(position).getRole_name());
        if (!languages.equalsIgnoreCase("English")) {
            holder.rolename.setText(category.get(position).getRole_name_local());
        }
        return convertView;
    }

    public void refill(ArrayList<JS_CATEGORY> category) {
        this.category = category;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imageView;
        TextView rolename;
    }
}
