package com.github.selectphoto.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import com.github.selectphoto.R;

/**
 * 自定义的imageView,作用是在确定按钮左上角显示勾选图片数量。
 * @author chengsy
 *
 */
public class CustomImageView extends View {
	private Paint 			mPaint;
	private	float 			radus;
	private Point 			centerP;
	private int 			width = 0;
	private int 			height = 0;
	private int 			count = 0;
	private float 			textSize = 0.0f;
	
	public CustomImageView(Context context) {
		super(context);
		float size = context.getResources().getDimension(R.dimen.custom_imageview_size);
		textSize = context.getResources().getDimension(R.dimen.custom_text_size);
		width = height = (int) size;
		init();
	}
	
	public CustomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		float size = context.getResources().getDimension(R.dimen.custom_imageview_size);
		textSize = context.getResources().getDimension(R.dimen.custom_text_size);
		width = height = (int) size;
		init();
	}
	
	public void init() {
		radus = width / 2;
		centerP = new Point(width / 2, height / 2);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setTextSize(textSize);
		mPaint.setStrokeWidth(12);
		mPaint.setTextAlign(Align.CENTER);
	}
	
	public void setCount(int count) {
		this.count = count;
		if (count == 0) {
			setVisibility(View.GONE);
		} else {
			setVisibility(View.VISIBLE);
		}
		invalidate();
	}
	    
	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.setColor(Color.GREEN);
		canvas.drawCircle(centerP.x, centerP.y, radus, mPaint);
		FontMetrics fontMetrics = mPaint.getFontMetrics(); 
		float fontHeight = fontMetrics.descent - fontMetrics.ascent; 
		float textBaseY = height - (height - fontHeight) / 2 - fontMetrics.descent;
		mPaint.setColor(Color.WHITE);
		canvas.drawText(String.valueOf(count), width/2, textBaseY, mPaint);
		super.onDraw(canvas);
	}
}
