package com.adil.discretezoomframelayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements
		ZoomListener,
		ReferenceValueProvider {
	private static final String TAG = "MainActivity";
	private static final String PREF_KEY = "value";
	private static final int DEFAULT = 37;

	private TextView textView, textViewSize;
	private int currentSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textViewSize = findViewById(R.id.textViewSize);
		textView = findViewById(R.id.textView);

		DiscreteZoomFrameLayout zoomLayoutTextViewSize = findViewById(R.id.zoomLayout);
		zoomLayoutTextViewSize.setZoomListener(this);
		zoomLayoutTextViewSize.setReferenceValueProvider(this);

		currentSize = getSharedPreferences(TAG, MODE_PRIVATE).getInt(PREF_KEY, DEFAULT);
		textView.setTextSize(currentSize);
		textViewSize.setText(String.valueOf(currentSize));
	}

	@Override
	public void onValueChanged(int value) {
		textView.setTextSize(value);
		textViewSize.setText(String.valueOf(value));
	}

	@Override
	public void onZoomEnd(int value) {
		currentSize = value;
		getSharedPreferences(TAG, MODE_PRIVATE).edit().putInt(PREF_KEY, value).apply();
	}

	@Override
	public void onZoomBegin() {
	}

	@Override
	public int provideValue() {
		return currentSize;
	}
}
