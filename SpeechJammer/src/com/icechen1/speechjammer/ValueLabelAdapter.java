package com.icechen1.speechjammer;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ValueLabelAdapter extends com.fima.chartview.LabelAdapter {
	public enum LabelOrientation {
		HORIZONTAL, VERTICAL
	}

	private Context mContext;
	private LabelOrientation mOrientation;

	public ValueLabelAdapter(Context context, LabelOrientation orientation) {
		mContext = context;
		mOrientation = orientation;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		TextView labelTextView;
//		if (convertView == null) {
//			convertView = new TextView(mContext);
//		}
//
//		labelTextView = (TextView) convertView;
//
//		int gravity = Gravity.CENTER;
//		if (mOrientation == LabelOrientation.VERTICAL) {
//			if (position == 0) {
//				gravity = Gravity.BOTTOM | Gravity.RIGHT;
//			} else if (position == getCount() - 1) {
//				gravity = Gravity.TOP | Gravity.RIGHT;
//			} else {
//				gravity = Gravity.CENTER | Gravity.RIGHT;
//			}
//		} else if (mOrientation == LabelOrientation.HORIZONTAL) {
//			if (position == 0) {
//				gravity = Gravity.CENTER | Gravity.LEFT;
//			} else if (position == getCount() - 1) {
//				gravity = Gravity.CENTER | Gravity.RIGHT;
//			}
//		}
//
//		labelTextView.setGravity(gravity);
//		labelTextView.setPadding(8, 0, 8, 0);
//		labelTextView.setText(String.format("%.1f", getItem(position)));

		return convertView;
	}
}