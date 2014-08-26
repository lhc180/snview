package com.starnet.snview.component.liveview;

import com.starnet.snview.R;
import com.starnet.snview.protocol.Connection;
import com.starnet.snview.realplay.PreviewDeviceItem;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LiveViewItemContainer extends RelativeLayout {
	
	
	private String deviceRecordName;
	private PreviewDeviceItem previewItem;
	
	private WindowLinearLayout mWindowLayout;
	private FrameLayout mPlaywindowFrame;
	private LiveView mSurfaceView;
	private ProgressBar mProgressBar;
	private ImageView mRefresh;
	private TextView mWindowInfoText;


//	private OnLiveViewContainerClickListener mLvContainerClickListener;
	private OnRefreshButtonClickListener mRefreshButtonClickListener;
	
	private Connection mCurrentConnection;
	
	
	
	public LiveViewItemContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public LiveViewItemContainer(Context context) {
		super(context);
	}
	
	

	public void findSubViews() {
		mWindowLayout = (WindowLinearLayout) findViewById(R.id.liveview_surface_infotext_layout);
		mPlaywindowFrame = (FrameLayout) findViewById(R.id.liveview_playwindow_frame);
		mSurfaceView = (LiveView) findViewById(R.id.liveview_surfaceview);
		mProgressBar = (ProgressBar) findViewById(R.id.liveview_progressbar);
		mRefresh = (ImageView) findViewById(R.id.liveview_refresh_imageview);
		mWindowInfoText = (TextView) findViewById(R.id.liveview_liveinfo_textview);
	}
	
	public void init() {
//		if (mLvContainerClickListener != null) {
//			this.setOnClickListener(mLvContainerClickListener);
//		}
		
		if (mRefreshButtonClickListener != null) {
			mRefresh.setOnClickListener(mRefreshButtonClickListener);
		}
		
		mWindowInfoText.setText(null);
		
		
//		detector = new GestureDetector(new GestureListener());
//		
//		animations[0] = AnimationUtils.loadAnimation(getContext(), R.anim.left_in);
//        animations[1] = AnimationUtils.loadAnimation(getContext(), R.anim.left_out);
//        animations[2] = AnimationUtils.loadAnimation(getContext(), R.anim.right_in);
//        animations[3] = AnimationUtils.loadAnimation(getContext(), R.anim.right_in);
        
        
		
	}	

	public Connection getCurrentConnection() {
		return mCurrentConnection;
	}
	
	public void setCurrentConnection(Connection conn) {
		this.mCurrentConnection = conn;
	}
	
	public String getDeviceRecordName() {
		return deviceRecordName;
	}
	
	public void setDeviceRecordName(String deviceRecordName) {
		this.deviceRecordName = deviceRecordName;
	}
	
	
	
//	public void setLiveViewContainerClickListener(
//			OnLiveViewContainerClickListener lvContainerClickListener) {
//		this.mLvContainerClickListener = lvContainerClickListener;
//	}
	
	
	public PreviewDeviceItem getPreviewItem() {
		return previewItem;
	}
	public void setPreviewItem(PreviewDeviceItem previewItem) {
		this.previewItem = previewItem;
	}
	public void setRefreshButtonClickListener(
			OnRefreshButtonClickListener refreshButtonClickListener) {
		this.mRefreshButtonClickListener = refreshButtonClickListener;
	}
	
	public WindowLinearLayout getWindowLayout() {
		return mWindowLayout;
	}
	
	public FrameLayout getPlaywindowFrame() {
		return mPlaywindowFrame;
	}
	
	public LiveView getSurfaceView() {
		return mSurfaceView;
	}
	
	public ProgressBar getProgressBar() {
		return mProgressBar;
	}
	
	public ImageView getRefreshImageView() {
		return mRefresh;
	}
	
	public TextView getWindowInfoText() {
		return mWindowInfoText;
	}
	

	public void setWindowInfoContent(String info) {
		final StringBuffer s;
		
		if (deviceRecordName != null && info != null) {
			s = new StringBuffer(deviceRecordName);
			s.append("[");
			s.append(info);
			s.append("]");
		} else {
			s = new StringBuffer("");
		}
		
		mWindowInfoText.post(new Runnable() {
			@Override
			public void run() {
				mWindowInfoText.setText(s.toString());
			}
		});
	}
	
	public void resetView() {
		mSurfaceView.setValid(true);
		mProgressBar.setVisibility(View.INVISIBLE);
		mRefresh.setVisibility(View.GONE);
	}
	
	
//	public static interface OnLiveViewContainerClickListener extends View.OnClickListener {}
	public static interface OnRefreshButtonClickListener extends View.OnClickListener {}
}
