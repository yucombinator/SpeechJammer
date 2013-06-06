package com.icechen1.speechjammer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class VisualizerView extends View {

	public static final int WAVE = 1;
	public static final int FFT = 2;

	// Namespaces to read attributes
	private static final String VISUALIZER_NS = "http://schemas.android.com/apk/res/com.icechen1.speechjammer";

	// Attribute names
	private static final String ATTR_ANTIALIAS = "antialias";
	private static final String ATTR_COLOR = "color";
	private static final String ATTR_TYPE = "type";

	// Default values for defaults
	private static final boolean DEFAULT_ANTIALIAS = true;
	private static final int DEFAULT_COLOR = Color.RED;
	private static final int DEFAULT_TYPE = WAVE;

	// Real defaults
	private final boolean mAntiAlias;
	private final int mColor;
	private int mType;
	private int mFftSamples = 48;

	private byte[] mBytes = null;
	private float[] mPoints;
	private Rect mRect = new Rect();

	private Paint mForePaint = new Paint();

	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Read parameters from attributes
		mAntiAlias = attrs.getAttributeBooleanValue(VISUALIZER_NS,
				ATTR_ANTIALIAS, DEFAULT_ANTIALIAS);
		mColor = attrs.getAttributeIntValue(VISUALIZER_NS, ATTR_COLOR,
				DEFAULT_COLOR);
		mType = attrs.getAttributeIntValue(VISUALIZER_NS, ATTR_TYPE,
				DEFAULT_TYPE);

		switch (mType) {
		case WAVE:
			mForePaint.setStrokeWidth(2.0f);
			break;
		case FFT:
			mForePaint.setStrokeWidth((float) getWidth() / (float) mFftSamples / 2);
			break;
		}
		mForePaint.setAntiAlias(mAntiAlias);
		mForePaint.setColor(mColor);
	}

	public void updateVisualizer(byte[] bytes) {
		mBytes = bytes;
		invalidate();
	}

	public void setWaveStrokeWidth(float width) {
		mForePaint.setStrokeWidth(width);
	}

	public void setAntiAlias(boolean antialias) {
		mForePaint.setAntiAlias(antialias);
	}

	public void setColor(int color) {
		mForePaint.setColor(color);
	}

	public void setType(int type) {
		mType = type;
		switch (type) {
		case WAVE:
			mForePaint.setStrokeWidth(2.0f);
			break;
		case FFT:
			mForePaint.setStrokeWidth(getWidth() / mFftSamples/2);
			break;
		}
	}

	public void setFftSamples(int samples) {
		mFftSamples = samples;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mBytes == null) {
			return;
		}

		if (mPoints == null || mPoints.length < mBytes.length * 4) {
			mPoints = new float[mBytes.length * 4];
		}

		switch (mType) {
		case WAVE:
			mRect.set(0, 0, getWidth(), getHeight());

			for (int i = 0; i < mBytes.length - 1; i++) {
				mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
				mPoints[i * 4 + 1] = mRect.height() / 2
						+ ((byte) (mBytes[i] + 128)) * (mRect.height() / 2)
						/ 128;
				mPoints[i * 4 + 2] = mRect.width() * (i + 1)
						/ (mBytes.length - 1);
				mPoints[i * 4 + 3] = mRect.height() / 2
						+ ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2)
						/ 128;
			}
			canvas.drawLines(mPoints, mForePaint);
			break;
		case FFT:
			mRect.set(0, 0, getWidth(), getHeight() * 2);

			for (int i = 0; i < mFftSamples; i++) {
				if (mBytes[i] < 0) {
					mBytes[i] = 127;
				}
				mPoints[i * 4] = mRect.width() * i / mFftSamples;
				mPoints[i * 4 + 1] = mRect.height() / 2;
				mPoints[i * 4 + 2] = mRect.width() * i / mFftSamples;
				mPoints[i * 4 + 3] = mRect.height() / 2 - 2 - mBytes[i] * 2;
			}
			canvas.drawLines(mPoints, mForePaint);
			break;

		}

	}
}
