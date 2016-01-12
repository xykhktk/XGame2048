package com.x.game2048;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	private TextView scoreTextview;
	private TextView highScoreTextview;
	private ImageView undoImageView;
	private ImageView refreshImageView;
	
	private static int score = 0;
	private GameView gameView;
	private AnimatorView animatorView;

	public int getScore() {
		return score;
	}
	
	public AnimatorView getAnimatorView() {
		return animatorView;
	}

	public void setScore(int mScore) {
		score = mScore;
		if(scoreTextview != null){
			scoreTextview.setText(score+"");
		}
	}

	public  void addScore(int num){
		score +=num;
		setScore(score);
	}
	
	public void setHighScore(int mScore) {
		if(highScoreTextview != null){
			highScoreTextview.setText(mScore+"");
		}
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        scoreTextview = (TextView) findViewById(R.id.main_textview_showscore);
        highScoreTextview = (TextView) findViewById(R.id.main_textview_show_high_score);
        gameView = (GameView) findViewById(R.id.main_gameview);
        animatorView = (AnimatorView) findViewById(R.id.main_animatorview);
        undoImageView = (ImageView) findViewById(R.id.ib_undo_mainactivity);
        refreshImageView = (ImageView) findViewById(R.id.ib_refresh_mainactivity);
        
        undoImageView.setOnClickListener(this);
        refreshImageView.setOnClickListener(this);
        gameView.setmMainActivity(MainActivity.this);
        
        setScore(0);
        int h =getSharedPreferences(Consts.Sp_Key_name, Context.MODE_PRIVATE).getInt(Consts.Sp_Key_High_Score, 0);
        highScoreTextview.setText(h + "");
        
    }
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Editor editor = getSharedPreferences(Consts.Sp_Key_name, Context.MODE_PRIVATE).edit();
		
		int[][] data = gameView.getDataForSp(); 
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				editor.putInt(Consts.Sp_Key_Data_Index + i + j, data[i][j]);
			}
		}
		editor.putBoolean(Consts.Sp_Key_Have_Status, true);
		editor.commit();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_refresh_mainactivity:
			gameView.resetGame();
			break;
		case R.id.ib_undo_mainactivity:
			gameView.resetData();
			break;
		default:
			break;
		}
		
	}
	
}
