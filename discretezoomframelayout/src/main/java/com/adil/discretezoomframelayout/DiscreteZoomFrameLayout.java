package com.adil.discretezoomframelayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

/**
 * This FrameLayout can be used to give discrete value values when user does a
 * ScaleGestureDetector (pinch-zoom).
 */
public class DiscreteZoomFrameLayout extends FrameLayout {
	private static final int defaultValue = 37;
	private float scaleFactor = 1.0f;
	private ScaleGestureDetector scaleDetector;
	private ReferenceValueProvider referenceValueProvider;
	private ZoomListener zoomListener;
	private int minValue, maxValue, value;

	public DiscreteZoomFrameLayout(Context context) {
		this(context, null);
	}

	public DiscreteZoomFrameLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DiscreteZoomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(attrs);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public DiscreteZoomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initialize(attrs);
	}

	private void initialize(AttributeSet set) {
		scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
		if (set != null) {
			TypedArray typedArray = getContext().obtainStyledAttributes(set, R.styleable.DiscreteZoomFrameLayout);
			minValue = typedArray.getInt(R.styleable.DiscreteZoomFrameLayout_minValue, 20);
			maxValue = typedArray.getInt(R.styleable.DiscreteZoomFrameLayout_maxValue, 80);
			typedArray.recycle();
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(@NonNull MotionEvent ev) {
		super.onTouchEvent(ev);
		// To make sure to receive touch events, tell parent we are handling them
		return true;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		scaleDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			// publish value when zoom begin.
			if (zoomListener != null) {
				zoomListener.onZoomBegin();
			}
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float tempScaleFactor = scaleFactor;
			tempScaleFactor *= detector.getScaleFactor();
			value = (int) (getCurrentValue() * tempScaleFactor);

			// updated scaleFactor only if value is in bounds.
			scaleFactor = (value < maxValue && value > minValue) ? tempScaleFactor : scaleFactor;

			// restrict value in bounds
			value = (value > maxValue) ? maxValue : (value < minValue ? minValue : value);

			// updated value on listener
			if (zoomListener != null) {
				zoomListener.onValueChanged(value);
			}
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			// publish value when zoom end.
			if (zoomListener != null && value > 0) {
				zoomListener.onZoomEnd(value);
			}
			// reset scaleFactor to 1
			scaleFactor = 1.0f;
		}
	}

	private int getCurrentValue() {
		return referenceValueProvider == null ? defaultValue : referenceValueProvider.provideValue();
	}

	/**
	 * Minimum bound of value
	 *
	 * @param minValue minimum value
	 */
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	/**
	 * Minimum bound of value
	 *
	 * @param maxValue maximum value
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * ReferenceValueProvider can be used to provide current value to scale up or down.
	 * For example, if we want to scale a TextView, whose font value is saved in preferences,
	 * then this interface can be used to provide the saved value.
	 * If this is not set, then default value of 37 is used.
	 *
	 * @param referenceValueProvider referenceValueProvider
	 */
	public void setReferenceValueProvider(ReferenceValueProvider referenceValueProvider) {
		this.referenceValueProvider = referenceValueProvider;
	}

	/**
	 * Publishes the current value when zoom operation is finished.
	 *
	 * @param zoomListener zoomListener
	 */
	public void setZoomListener(ZoomListener zoomListener) {
		this.zoomListener = zoomListener;
	}
}
