package com.x.game2048;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class AnimatorView extends FrameLayout{

	private ArrayList<Card> cardPool = new ArrayList<Card>();
	private Context context ;
	
	
	public AnimatorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		cardPool = new ArrayList<Card>();
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public AnimatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		cardPool = new ArrayList<Card>();
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	public AnimatorView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		cardPool = new ArrayList<Card>();
		this.context = context;
	}

	private Card getCard(int num){
		Card c = null;
		if(cardPool.size() > 0){
			//c = cardPool.get(0);
			//cardPool.remove(0);
			c = cardPool.remove(0);
		}else{
			c = new Card(getContext());
			addView(c);
		}
		c.setNum(num);
		c.setVisibility(View.VISIBLE);
		return c;
	}
	
	private void recycleCard(Card c){
		c.setAnimation(null);
		c.setVisibility(View.INVISIBLE);
		cardPool.add(c);
	}
	
	/**
	 * @param from
	 * @param to
	 * @param fromX 指card[x][y]的下标，下同。
	 * @param toX
	 * @param fromY
	 * @param toY
	 */
	public void createTranslateAnimator(final Card from,final Card to,int fromX,int toX,int fromY,int toY){
		
		final Card c = getCard(from.getNum());
		
		LayoutParams lp = new LayoutParams(Consts.cardWidth, Consts.cardWidth);
		lp.leftMargin = fromX * Consts.cardWidth;
		lp.topMargin = fromY * Consts.cardWidth;
		c.setLayoutParams(lp);

		if(to.getNum() > 0){
			//to.getLable().setVisibility(View.INVISIBLE);
		}
		
		Log.i("xxx", "  " + fromX + "  " + toX);
		TranslateAnimation a = new TranslateAnimation(0, Consts.cardWidth *(toX - fromX),0,Consts.cardWidth *(toY -fromY));
		a.setDuration(100);
		a.setAnimationListener(new Animation.AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				recycleCard(c);
				//to.getLable().setVisibility(View.VISIBLE);
			}
		});
		c.startAnimation(a);
	}
	
	public void createScaleAnimator(Card c){
		ScaleAnimation a = new ScaleAnimation(0.1f, 1f, 0.1f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
		a.setDuration(300);
		c.setAnimation(null);
		c.getLabel().startAnimation(a);
	}
	
	
}
