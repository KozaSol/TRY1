package com.jobsearchtry.utils;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
//location - right drawable onclick listener
public abstract class DrawableClickListener implements View.OnTouchListener {

    public static final int DRAWABLE_INDEX_RIGHT = 2;
    public static final int DEFAULT_FUZZ = 10;
    private final int fuzz;
    private Drawable drawable = null;

    public DrawableClickListener(final TextView view, final int drawableIndex) {
        this(view, drawableIndex, DrawableClickListener.DEFAULT_FUZZ);
    }

    public DrawableClickListener(final TextView view, final int drawableIndex, final int fuzz) {
        super();
        this.fuzz = fuzz;
        final Drawable[] drawables = view.getCompoundDrawables();
        if (drawables.length == 4) {
            this.drawable = drawables[drawableIndex];
        }
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final Rect bounds = drawable.getBounds();
            if (this.isClickOnDrawable(x, y, v, bounds, this.fuzz)) {
                return this.onDrawableClick();
            }
        }
        return false;
    }

    public abstract boolean isClickOnDrawable(final int x, final int y, final View view, final Rect drawableBounds, final int fuzz);

    public abstract boolean onDrawableClick();

    public static abstract class RightDrawableClickListener extends DrawableClickListener {
        public RightDrawableClickListener(final TextView view) {
            super(view, DrawableClickListener.DRAWABLE_INDEX_RIGHT);
        }


        public RightDrawableClickListener(final TextView view, final int fuzz) {
            super(view, DrawableClickListener.DRAWABLE_INDEX_RIGHT, fuzz);
        }

        public boolean isClickOnDrawable(final int x, final int y, final View view, final Rect drawableBounds, final int fuzz) {
            if (x >= (view.getWidth() - view.getPaddingRight() - drawableBounds.width() - fuzz)) {
                if (x <= (view.getWidth() - view.getPaddingRight() + fuzz)) {
                    if (y >= (view.getPaddingTop() - fuzz)) {
                        if (y <= (view.getHeight() - view.getPaddingBottom() + fuzz)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

    }
}
