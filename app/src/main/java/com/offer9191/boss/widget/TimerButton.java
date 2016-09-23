package com.offer9191.boss.widget;

import android.content.Context;
import android.widget.Button;
import android.graphics.Canvas;
import android.os.CountDownTimer;
import android.util.AttributeSet;

public class TimerButton extends Button {

	public TimerButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setText("获取验证码");
	}

	public TimerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setText("获取验证码");
	}

	public TimerButton(Context context) {
		super(context);
		setText("获取验证码");
	}

	public void start() {
		new MyTimer(60000, 1000).start();
		setEnabled(false);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	class MyTimer extends CountDownTimer {

		public MyTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			setText("获取验证码");
			setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			setText(millisUntilFinished / 1000+"");
		}

	}
}

