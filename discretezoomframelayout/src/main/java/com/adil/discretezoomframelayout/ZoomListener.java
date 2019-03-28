package com.adil.discretezoomframelayout;

public interface ZoomListener {
	void onValueChanged(int value);

	void onZoomBegin();

	void onZoomEnd(int value);
}
