package com.x.game2048;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {

	private int num = 0;
	private TextView label;
	
	public Card(Context context) {
		super(context);
		
		label = new TextView(getContext());
		label.setTextSize(30);
		label.setGravity(Gravity.CENTER);

		LayoutParams text_lp = new LayoutParams(-1, -1);
		text_lp.setMargins(10, 10, 0, 0);
		
		addView(label, text_lp);
		//setNum(500);
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
		
		if(num < 1){
			label.setText("");
		}else{
			label.setText(num+"");
		}
		
		if(num < 128){
			label.setBackground(getResources().getDrawable(R.drawable.shape_textview_2));
			label.setTextColor(getResources().getColor(R.color.lable_text_2));
		}else if(128 < num && num<= 512){
			label.setBackground(getResources().getDrawable(R.drawable.shape_textview_512));
			label.setTextColor(getResources().getColor(R.color.lable_text_512));
		}else if(512 < num && num<= 2048){
			label.setBackground(getResources().getDrawable(R.drawable.shape_textview_2048));
			label.setTextColor(getResources().getColor(R.color.lable_text_512));
		}

	}
	
	public boolean equals(Card o) {
		/*if(o instanceof(Card.class)){
			
		}*/
	
		if(o.getNum() == this.num){
			return true;
		}else{
			return false;
		}
	}
	
	public TextView getLabel() {
		return label;
	}
	
}
