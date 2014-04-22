package com.starnet.hdview.activity;

import java.util.ArrayList;

import com.starnet.hdview.R;
import com.starnet.hdview.component.BaseActivity;
import com.starnet.hdview.component.Toolbar;
import com.starnet.hdview.component.Toolbar.ActionImageButton;
import com.starnet.hdview.component.VideoPager.ACTION;
import com.starnet.hdview.component.VideoPager;
import com.starnet.hdview.util.ActivityUtility;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RealplayActivity extends BaseActivity {

	private static final String TAG = "MainActivity";
	
    
    private Toolbar mToolbar;

    
    private VideoPager mPager;
    private LinearLayout mToolbarQualityMenu;
    private LinearLayout mToolbarPTZMenu;



    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle inState) {
        super.onCreate(inState);

        
        initToolbar();
        
        initToolbarExtendMenu();
    }
    
    private VideoPager.OnActionClickListener mPagerOnActionClickListener 
    	= new VideoPager.OnActionClickListener() {
		@Override
		public void OnActionClick(View v, ACTION action) {
			switch (action) {
			case PREVIOUS:
				
				break;
			case NEXT:
				
				break;
			default:
				break;
			}
			
		}
	};
 
    private boolean bIsPlaying = false;
    private boolean bQualityPressed = false;
    private boolean bPTZPressed = false;
    private boolean bIsMicrophoneOpen = false;
    private boolean bIsSoundOpen = false;
    private boolean bVideoRecordPressed = false;
    private Toolbar.OnItemClickListener 
    	mToolbarOnItemClickListener = new Toolbar.OnItemClickListener() {
		
		@Override
		public void onItemClick(ActionImageButton imgBtn) {
			
			switch (imgBtn.getItemData().getActionID()) {
			case PLAY_PAUSE:
				if (!bIsPlaying) {	// 播放
					bIsPlaying = true;
					mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.PLAY_PAUSE, R.drawable.toolbar_pause_selector);
				} else {			// 暂停
					bIsPlaying = false;
					mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.PLAY_PAUSE, R.drawable.toolbar_play_selector);
				}
					
				break;
			case PICTURE:
				Toast.makeText(getBaseContext(), "Width: " + mPager.getPrevious().getWidth() + ", height: " + mPager.getPrevious().getHeight(), Toast.LENGTH_LONG).show();
				break;
			case QUALITY:
				bQualityPressed = !bQualityPressed;		
				
				if (bQualityPressed) {					
					showToolbarExtendMenu(TOOLBAR_EXTEND_MENU.MENU_QUALITY);				
				} else {
					showToolbarExtendMenu(TOOLBAR_EXTEND_MENU.PAGER);
				}
				
				
				break;
			case PTZ:
				bPTZPressed = !bPTZPressed;		
				
				if (bPTZPressed) {		
					showToolbarExtendMenu(TOOLBAR_EXTEND_MENU.MENU_PTZ);				
				} else {
					showToolbarExtendMenu(TOOLBAR_EXTEND_MENU.PAGER);
				}
				break;
			case MICROPHONE:
				if (!bIsMicrophoneOpen) {	// 开启麦克风
					bIsMicrophoneOpen = true;
					mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.MICROPHONE, R.drawable.toolbar_microphone_selector);
				} else {					// 关闭麦克风
					bIsMicrophoneOpen = false;
					mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.MICROPHONE, R.drawable.toolbar_microphone_stop_selector);
				}
				
				break;
			case SOUND:
				if (!bIsSoundOpen) {	// 开启扬声器
					bIsSoundOpen = true;
					mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.SOUND, R.drawable.toolbar_sound_selector);
				} else {					// 关闭扬声器
					bIsSoundOpen = false;
					mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.SOUND, R.drawable.toolbar_sound_off_selector);
				}
				break;
			case VIDEO_RECORD:
				mToolbar.setActionImageButtonSelected(Toolbar.ACTION_ENUM.VIDEO_RECORD, !bVideoRecordPressed);
				bVideoRecordPressed = !bVideoRecordPressed;
				break;
			case ALARM:
				break;
			default:
				break;
			}
		}
	};
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void initToolbar() {
    	mToolbar = super.getBaseToolbar();
    	
    	ArrayList itemList = new ArrayList();
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.PLAY_PAUSE, R.drawable.toolbar_play_selector));
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.PICTURE, R.drawable.toolbar_take_picture_selector));
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.QUALITY, R.drawable.toolbar_quality_high_selector));
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.PTZ, R.drawable.toolbar_ptz_selector));
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.MICROPHONE, R.drawable.toolbar_microphone_stop_selector));
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.SOUND, R.drawable.toolbar_sound_off_selector));
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.VIDEO_RECORD, R.drawable.toolbar_video_record_selector));
    	itemList.add(new Toolbar.ItemData(Toolbar.ACTION_ENUM.ALARM, R.drawable.toolbar_alarm_selector));
    	
    	mToolbar.createToolbar(itemList, ActivityUtility.getScreenSize(this).x, getResources().getDimensionPixelSize(R.dimen.toolbar_height));
    	
    	this.mToolbar.setOnItemClickListener(mToolbarOnItemClickListener);
    }
    
    private enum TOOLBAR_EXTEND_MENU {
    	PAGER,
    	MENU_QUALITY,
    	MENU_PTZ
    }
    
    
    private ImageButton mQualityMenuFluency;
    private ImageButton mQualityMenuStandard;
    private ImageButton mQualityMenuHigh;
    private ImageButton mQualityMenuCustom;
    
    private OnClickListener mOnQualityMenuClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.toolbar_quality_menu_fluency:
				mQualityMenuFluency.setSelected(true);
				mQualityMenuStandard.setSelected(false);
				mQualityMenuHigh.setSelected(false);
				mQualityMenuCustom.setSelected(false);
				mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.QUALITY, R.drawable.toolbar_quality_fluency_selector);
				break;
			case R.id.toolbar_quality_menu_standard:
				mQualityMenuFluency.setSelected(false);
				mQualityMenuStandard.setSelected(true);
				mQualityMenuHigh.setSelected(false);
				mQualityMenuCustom.setSelected(false);
				mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.QUALITY, R.drawable.toolbar_quality_standard_selector);
				break;
			case R.id.toolbar_quality_menu_high:
				mQualityMenuFluency.setSelected(false);
				mQualityMenuStandard.setSelected(false);
				mQualityMenuHigh.setSelected(true);
				mQualityMenuCustom.setSelected(false);
				mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.QUALITY, R.drawable.toolbar_quality_high_selector);
				break;
			case R.id.toolbar_quality_menu_custom:
				mQualityMenuFluency.setSelected(false);
				mQualityMenuStandard.setSelected(false);
				mQualityMenuHigh.setSelected(false);
				mQualityMenuCustom.setSelected(true);
				mToolbar.setActionImageButtonBg(Toolbar.ACTION_ENUM.QUALITY, R.drawable.toolbar_quality_custom_selector);
				break;
			default:
				break;
			}
			
			mToolbar.setActionItemSelected(Toolbar.ACTION_ENUM.QUALITY, true);
		}
    	
    };
    
    private void initToolbarExtendMenu() {
    	mPager =  (VideoPager) findViewById(R.id.pager);
        mPager.initContent(ActivityUtility.getScreenSize(this).x, getResources().getDimensionPixelSize(R.dimen.toolbar_height));
        mPager.setOnActionClickListener(mPagerOnActionClickListener);
    	
    	mToolbarQualityMenu = (LinearLayout) findViewById(R.id.toolbar_quality_menu);
    	mQualityMenuFluency = (ImageButton)findViewById(R.id.toolbar_quality_menu_fluency);
    	mQualityMenuStandard = (ImageButton)findViewById(R.id.toolbar_quality_menu_standard);
    	mQualityMenuHigh = (ImageButton)findViewById(R.id.toolbar_quality_menu_high);
    	mQualityMenuCustom = (ImageButton)findViewById(R.id.toolbar_quality_menu_custom);
    	
    	mQualityMenuFluency.setOnClickListener(mOnQualityMenuClickListener);
    	mQualityMenuStandard.setOnClickListener(mOnQualityMenuClickListener);
    	mQualityMenuHigh.setOnClickListener(mOnQualityMenuClickListener);
    	mQualityMenuCustom.setOnClickListener(mOnQualityMenuClickListener);
    	
    	
    	mToolbarPTZMenu = (LinearLayout) findViewById(R.id.toolbar_ptz_menu); 
    	
    	showToolbarExtendMenu(TOOLBAR_EXTEND_MENU.PAGER);
    }
    
    private void showToolbarExtendMenu(TOOLBAR_EXTEND_MENU menuId) {
    	switch (menuId) {
    	case PAGER:
    		mPager.setVisibility(View.VISIBLE);
        	mToolbarQualityMenu.setVisibility(View.GONE);
        	mToolbarPTZMenu.setVisibility(View.GONE);
        	
        	mToolbar.setActionItemSelected(Toolbar.ACTION_ENUM.QUALITY, false);
			mToolbar.setActionItemSelected(Toolbar.ACTION_ENUM.PTZ, false);
			
			bQualityPressed = false;
			bPTZPressed = false;
    		break;
    	case MENU_QUALITY:
    		mPager.setVisibility(View.GONE);
        	mToolbarQualityMenu.setVisibility(View.VISIBLE);
        	mToolbarPTZMenu.setVisibility(View.GONE);
        	
        	mToolbar.setActionItemSelected(Toolbar.ACTION_ENUM.QUALITY, true);
			mToolbar.setActionItemSelected(Toolbar.ACTION_ENUM.PTZ, false);
        	
			bQualityPressed = true;
			bPTZPressed = false;			
    		break;
    	case MENU_PTZ:
    		mPager.setVisibility(View.GONE);
        	mToolbarQualityMenu.setVisibility(View.GONE);
        	mToolbarPTZMenu.setVisibility(View.VISIBLE);
        	
        	mToolbar.setActionItemSelected(Toolbar.ACTION_ENUM.QUALITY, false);
			mToolbar.setActionItemSelected(Toolbar.ACTION_ENUM.PTZ, true);
			
			bQualityPressed = false;
			bPTZPressed = true;
    	}
    }
}