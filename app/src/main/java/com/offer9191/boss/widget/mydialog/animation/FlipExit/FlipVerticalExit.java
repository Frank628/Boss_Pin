package com.offer9191.boss.widget.mydialog.animation.FlipExit;

import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;
import com.offer9191.boss.widget.mydialog.animation.BaseAnimatorSet;

public class FlipVerticalExit extends BaseAnimatorSet {
	@Override
	public void setAnimation(View view) {
		animatorSet.playTogether(ObjectAnimator.ofFloat(view, "rotationX", 0, 90),//
				ObjectAnimator.ofFloat(view, "alpha", 1, 0));
	}
}
