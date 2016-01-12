package com.x.game2048;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

public class GameView extends GridLayout {

	private boolean merge = false;
	private Card[][] cards = new Card[4][4];
	private Context context;
	private SharedPreferences sp;
	private int score = 0;
	/**
	 * 用于回退一步
	 */
	private int[][] dataForUndo = new int[4][4];
	/**
	 * 用于shareprefrence存储当前游戏状态
	 */
	private int[][] dataForSp = new int[4][4];
	private List<Point> emptyPoint = new ArrayList<Point>();
	private MainActivity mMainActivity;
	
	public GameView(Context context) {
		super(context);
		this.context = context;
		initView();
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}
	
	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		initView();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		int cardWith = (Math.min(w, h) -10)/4;
		Consts.cardWidth = cardWith;
		
		addCards(cardWith);
	}
	
	public void resetGame() {
		
		Editor e = sp.edit();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				cards[i][j].setNum(0);
				dataForSp[i][j] = 0;
				dataForUndo[i][j] = 0;
				e.putInt(Consts.Sp_Key_Data_Index + i + j, 0);
			}
		}
		
		if( (mMainActivity ) != null) {
			mMainActivity.setScore(0);
		}
		
		int high = sp.getInt(Consts.Sp_Key_High_Score, 0);
		if(score > high) {
			e.putInt(Consts.Sp_Key_High_Score, score);
			if(mMainActivity != null){
				mMainActivity.setHighScore(score);
			}
		}
		score = 0;
		e.commit();
		
		addRandom();
		addRandom();
	}

	private void addCards(int cardWith) {
		
		if(sp.getBoolean(Consts.Sp_Key_Have_Status, false)){
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					dataForSp[i][j] = sp.getInt(Consts.Sp_Key_Data_Index+i+j, 0);
					Card c = new Card(getContext());
					c.setNum(dataForSp[i][j]);
					addView(c, cardWith, cardWith);
					cards[i][j] = c;
					dataForUndo[i][j] = dataForSp[i][j];
				}
			}
		}else{
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					Card c = new Card(getContext());
					addView(c, cardWith, cardWith);
					cards[i][j] = c;
				}
			}
			resetGame();
		}
		
	}

	private void addRandom(){
		emptyPoint.clear();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if(cards[i][j].getNum() < 1){
					emptyPoint.add(new Point(i, j));
				}
			}
		}
		
		if(emptyPoint.size() > 0){
			int remove = (int) (Math.random() * emptyPoint.size());
			Point p = emptyPoint.get(remove);
			emptyPoint.remove(remove);
			
			int num = Math.random()>0.1?2:4;
			cards[p.x][p.y].setNum(num);
			dataForSp[p.x][p.y] = num;
			
			if(mMainActivity != null)   mMainActivity.getAnimatorView().createScaleAnimator(cards[p.x][p.y]);
		}
		
	}
	
	private void initView(){
		
		sp = context.getSharedPreferences(Consts.Sp_Key_name, Context.MODE_PRIVATE);
		setBackgroundColor(getResources().getColor(R.color.GameViewBg2)); 
		//setAlpha((float) 0.5);
		setColumnCount(4);
		
		setOnTouchListener(new View.OnTouchListener() {
			
			float startX,startY,offsetX,offsetY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case  MotionEvent.ACTION_DOWN:
						startX = event.getX();
						startY = event.getY();
						break;
					case MotionEvent.ACTION_UP:
						offsetX = event.getX() - startX;
						offsetY = event.getY() - startY;
						
						if(Math.abs(offsetX) > Math.abs(offsetY)){
							if(offsetX > 5){
								swipeRight();
							}else if(offsetX < -5){
								swipeLeft();
							}
						}else{
							if(offsetY > 5){
								swipeDown();
							}else if(offsetY < -5){
								swipeUp();
							}
						}
						
						break;
					default:
				}
				return true;
			}
		});
		
	}

	private void swipeLeft(){
		saveDataForUndo();
		for (int x = 0; x < 4; x++) {
			for (int y = 1; y < 4; y++) {
				for (int y1 = y; y1 > 0; y1--) {
							
					if(cards[x][y1].getNum() > 0){
						if(cards[x][y1-1].getNum() == 0){
							if(mMainActivity != null)  mMainActivity.getAnimatorView().createTranslateAnimator(cards[x][y1], cards[x][y1-1], y1, y1-1, x, x);
							cards[x][y1-1].setNum(cards[x][y1].getNum());
							cards[x][y1].setNum(0);
							merge = true;
						}else if(cards[x][y1-1].getNum() == cards[x][y1].getNum()){
							if(mMainActivity != null)  mMainActivity.getAnimatorView().createTranslateAnimator(cards[x][y1], cards[x][y1-1], y1, y1-1, x, x);
							cards[x][y1-1].setNum(cards[x][y1].getNum()*2);
							cards[x][y1].setNum(0);
							merge = true;
							
							if(mMainActivity != null) {
								mMainActivity.addScore(cards[x][y1-1].getNum());
								score += cards[x][y1-1].getNum();
							}
							
						}else{
							break;
						}
					}
				}
			}
		}
		
		saveAndCheck();
	}

	private void swipeRight(){
		saveDataForUndo();
		for (int x = 0; x < 4; x++) {
			for (int y = 2; y >= 0; y--) {
				for (int y1 = y; y1 <3; y1++) {
					
					if(cards[x][y1].getNum() > 0){
						if(cards[x][y1+1].getNum() == 0){
							if(mMainActivity != null)  mMainActivity.getAnimatorView().createTranslateAnimator(cards[x][y1], cards[x][y1+1], y1, (y1+1),x, x);
							cards[x][y1+1].setNum(cards[x][y1].getNum());
							cards[x][y1].setNum(0);
							merge = true;
						}else if(cards[x][y1+1].getNum() == cards[x][y1].getNum()){
							if(mMainActivity != null)  mMainActivity.getAnimatorView().createTranslateAnimator(cards[x][y1], cards[x][y1+1],  y1, (y1+1),x, x);
							cards[x][y1+1].setNum(cards[x][y1].getNum()*2);
							cards[x][y1].setNum(0);
							merge = true;
							
							if(mMainActivity != null) {
								mMainActivity.addScore(cards[x][y1+1].getNum());
								score += cards[x][y1+1].getNum();
							}
							
						}else{
							break;
						}
					}
				}
			}
		}
		
		saveAndCheck();
	}
	
	private void swipeUp(){
		saveDataForUndo();
		for (int y = 0; y < 4; y++) {
			for (int x = 1; x < 4; x++) {
				for (int x1 = x; x1 > 0; x1--) {
					
					if(cards[x1][y].getNum() > 0){
						if(cards[x1-1][y].getNum() == 0){
							if(mMainActivity != null) mMainActivity.getAnimatorView().createTranslateAnimator(cards[x1][y], cards[x1-1][y],  y, y,x1 , x1-1);
							cards[x1-1][y].setNum(cards[x1][y].getNum());
							cards[x1][y].setNum(0);
							merge = true;
						}else if(cards[x1-1][y].getNum() == cards[x1][y].getNum()){
							if(mMainActivity != null) mMainActivity.getAnimatorView().createTranslateAnimator(cards[x1][y], cards[x1-1][y], y, y ,x1 , x1-1);
							cards[x1-1][y].setNum(cards[x1][y].getNum()*2);
							cards[x1][y].setNum(0);
							merge = true;
							
							if( mMainActivity != null) {
								mMainActivity.addScore(cards[x1-1][y].getNum());
								score += cards[x1-1][y].getNum();
							}
							
						}else{
							break;
						}
					}
				}
			}
		}
		
		saveAndCheck();
	}
	
	private void swipeDown(){

		saveDataForUndo();
		for (int y = 0; y < 4; y++) {
			for (int x = 2; x >= 0; x--) {
				for (int x1 = x; x1 < 3; x1++) {
					
					if(cards[x1][y].getNum() > 0){
						if(cards[x1+1][y].getNum() == 0){
							if(mMainActivity != null)  mMainActivity.getAnimatorView().createTranslateAnimator(cards[x1][y], cards[x1+1][y],y, y, x1 , x1+1 );
							cards[x1+1][y].setNum(cards[x1][y].getNum());
							cards[x1][y].setNum(0);
							merge = true;
						}else if(cards[x1+1][y].getNum() == cards[x1][y].getNum()){
							if(mMainActivity != null)  mMainActivity.getAnimatorView().createTranslateAnimator(cards[x1][y], cards[x1+1][y], y, y, x1 , x1+1);
							cards[x1+1][y].setNum(cards[x1][y].getNum()*2);
							cards[x1][y].setNum(0);
							
							if(mMainActivity != null) {
								mMainActivity.addScore(cards[x1+1][y].getNum());
								score += cards[x1+1][y].getNum();
							}
							
							merge = true;
						}else{
							break;
						}
					}
				}
			}
		}
		
		saveAndCheck();
	}
	
	private void saveDataForUndo(){
		int i,j;
		for(i = 0;i < 4;i++){
			for(j = 0;j < 4;j++){
				dataForUndo[i][j] = cards[i][j].getNum();
			}
		}
	}
	
	private void saveDataForSp(){
		int i,j;
		for(i = 0;i < 4;i++){
			for(j = 0;j < 4;j++){
				dataForSp[i][j] = cards[i][j].getNum();
			}
		}
	}
	
	public void resetData(){
		int i,j;
		for(i = 0;i < 4;i++){
			for(j = 0;j < 4;j++){
				
				cards[i][j].setNum(dataForUndo[i][j]);
			}
		}
	}
	
	
	private void saveAndCheck() {
		// TODO Auto-generated method stub
		saveDataForSp();
		if(merge == true){
			addRandom();
			checkOver();
			merge = false; 
		}
	}
	
	void checkOver(){
		boolean isOver = true;
		boolean breakFlag = true;
		for (int y = 0; breakFlag && y < 4; y++) {
			for (int x = 0; breakFlag && x < 4; x++) {
				
				if((cards[x][y].getNum() == 0) ||
						((x > 0) && (cards[x][y].equals(cards[x-1][y]))) ||
						((x < 3) && (cards[x][y].equals(cards[x+1][y]))) ||
						((y > 0) && (cards[x][y].equals(cards[x][y-1]))) ||
						((y < 3) && (cards[x][y].equals(cards[x][y+1]))) ){
					
					isOver = false;
					breakFlag = false;
				}
			}
		}
		
		if(isOver == true){
			new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.gameview_game_is_over)).
				setMessage(getResources().getString(R.string.gameview_game_is_over)).
				setPositiveButton(getResources().getString(R.string.gameview_again), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					resetGame();
				}
			} ).show();
		}
		
	}
	
	public int[][] getDataForSp() {
		return dataForSp;
	}

	public void setmMainActivity(MainActivity mMainActivity) {
		this.mMainActivity = mMainActivity;
	}
	
}
