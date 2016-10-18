package cn.fuyoushuo.fqbb.view.Layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {
	private static String TAG = "MyLinearLayout";
	private int DRAG_X_THROD = 0;
	private int SCROLL_X = 0;

	public MyLinearLayout(Context context) {
		super(context);
		//判断横划的阈值,为了兼容不同尺寸的设备，以dp为单位
		DRAG_X_THROD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
		SCROLL_X = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		//判断横划的阈值,为了兼容不同尺寸的设备，以dp为单位
		DRAG_X_THROD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
		SCROLL_X = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//判断横划的阈值,为了兼容不同尺寸的设备，以dp为单位
		DRAG_X_THROD = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
		SCROLL_X = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics());
	}

	int downX, downY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) event.getX();
				downY = (int) event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				return true;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
		}
		return super.dispatchTouchEvent(event);
	}
    
}
