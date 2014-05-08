package com.starnet.hdview.images;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.starnet.hdview.R;
import com.starnet.hdview.component.BaseActivity;
import com.starnet.hdview.global.GlobalApplication;
import com.starnet.hdview.util.ActivityUtility;

public class ImagesManagerActivity extends BaseActivity {

	private FrameLayout mBaseContentView;
	private ExpandableListView mExpandableListView;
	private ImagesExpandableListAdapter mExpandableListAdapter;
	
	private final ArrayList<ImagesGroup> mImagesThumbnailGroupList = new ArrayList();
	
	private ImagesManager mImagesManager;
	
	
	private boolean mIsEdit = false;	// 是否处于编辑状态
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.images_manager_activity);
		
		initViews();
		
		setListeners();
		
		GlobalApplication.getInstance().setScreenWidth(ActivityUtility.getScreenSize(this).x);
		
		mImagesManager = ImagesManager.getInstance();
	}

	private void initViews() {
		TextView title = super.getTitleView();
		title.setText(R.string.navigation_title_picture_management);
		
		
		super.hideExtendButton();
		super.setRightButtonBg(R.drawable.navigation_bar_edit_btn_selector);
		super.setToolbarVisiable(false);
		//super.setExtendBarVisible(false);
		
		mBaseContentView = ((FrameLayout)findViewById(R.id.base_content));
	    mBaseContentView.setBackgroundResource(R.color.list_view_bg);
	    mBaseContentView.setPadding(0, 0, 0, 0);
	    
	    mExpandableListView = (ExpandableListView) findViewById(R.id.images_listview);
		
	}
	
	private void setListeners() {
		super.getLeftButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mIsEdit) {	// 菜单按钮
					
				} else {	// 退出图像管理编辑状态
					switch2EditStatus(false);;
					mIsEdit = false;
				}
				
			}
		});
		
		super.getRightButton().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!mIsEdit) {	// 进入图像管理编辑状态
					switch2EditStatus(true);					
					mIsEdit = true;
				} else {	// 删除所选图片
					
				}
			}
			
		});
		
	}
	
	private void updateImageGroupList()
	  {
	    this.mImagesThumbnailGroupList.clear();
	    List dateList = this.mImagesManager.getDateList();
	    if ((dateList == null) || (dateList.size() == 0)) {
	    	return;
	    } else {
	    	for (int i = 0; i < dateList.size(); i++) {
	    		String str = (String)dateList.get(i);
		        List imageList = this.mImagesManager.getImageListForDate(str);

		        ImagesGroup imagesGroup = new ImagesGroup(str, imageList);
		        this.mImagesThumbnailGroupList.add(imagesGroup);
	    	}
	    }
	   
	  }


	private boolean loadThumbnailsInBackground() {
		mImagesManager.loadLocalImages();
		updateImageGroupList();
		return true;
	}
	
	public boolean getEditStatus() {
		return mIsEdit;
	}
	
	private void switch2EditStatus(boolean isOrnot) {
		if (isOrnot) {
			ImagesManagerActivity.this
					.setNavbarBgFromColor(ImagesManagerActivity.this
							.getResources().getColor(
									R.color.navigation_bar_red_bg));
			ImagesManagerActivity.this
					.setLeftButtonBg(R.drawable.navigation_bar_back_btn_selector);
			ImagesManagerActivity.this
					.setRightButtonBg(R.drawable.navigation_bar_del_btn_selector);
		} else {
			ImagesManagerActivity.this
					.setNavbarBgFromColor(ImagesManagerActivity.this
							.getResources().getColor(
									R.color.navigation_bar_blue_bg));
			ImagesManagerActivity.this
					.setLeftButtonBg(R.drawable.navigation_bar_menu_btn_selector);
			ImagesManagerActivity.this
					.setRightButtonBg(R.drawable.navigation_bar_edit_btn_selector);
			
			setTitleText(0);
			mExpandableListAdapter.setThumbnailSelectedCount(0);
			
			for (ImagesGroup ig : mExpandableListAdapter.getImageGroupList()) {
				for (Image img : ig.getThumbnailList()) {
					img.setSelected(false);
				}
			}
			
			mExpandableListView.invalidateViews();						
		}
	}
	
	public void setTitleText(int count) {
		String imageTitle = this.getResources().getString(R.string.navigation_title_picture_management);
		if (count > 0) {
			String oldTitle = imageTitle;
			StringBuilder newTitle = new StringBuilder(oldTitle);
			newTitle.append("(");
			newTitle.append(count);
			newTitle.append(")");
			super.setTitleViewText(newTitle.toString());
		} else {
			super.setTitleViewText(imageTitle);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		ImageLoader.getInstance().release();
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
	    LoadThumbnailTask localLoadThumbnailTask = new LoadThumbnailTask();
	    Object[] arrayOfObject = new Object[3];
	    arrayOfObject[0] = null;
	    arrayOfObject[1] = null;
	    arrayOfObject[2] = null;
	    localLoadThumbnailTask.execute(arrayOfObject);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private class LoadThumbnailTask extends AsyncTask<Object, Object, Object> {

		public LoadThumbnailTask() {}
		
		@Override
		protected Object doInBackground(Object... params) {
			return Boolean.valueOf(loadThumbnailsInBackground());
		}

		@Override
		protected void onPostExecute(Object result) {
			if (((Boolean)result).booleanValue()) {
				
				mExpandableListAdapter = new ImagesExpandableListAdapter(ImagesManagerActivity.this, ImagesManagerActivity.this.mImagesThumbnailGroupList);
		        mExpandableListView.setAdapter(ImagesManagerActivity.this.mExpandableListAdapter);
		        
		        
		        if (ImagesManagerActivity.this.mExpandableListAdapter != null) {
					ImagesManagerActivity.this.mExpandableListAdapter.notifyDataSetChanged();
			        for (int i = 0; i < ImagesManagerActivity.this.mExpandableListAdapter.getGroupCount(); i++) {
			          ImagesManagerActivity.this.mExpandableListView.expandGroup(i);
			        }
			     }
		        
		        if (ImagesManagerActivity.this.mImagesThumbnailGroupList.size() > 0) {
		        	ImagesManagerActivity.this.mExpandableListView.expandGroup(0);
		        }
			}
	

		}
		
		
	}
	
}