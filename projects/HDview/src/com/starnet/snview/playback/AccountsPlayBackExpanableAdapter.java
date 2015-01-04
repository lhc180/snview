package com.starnet.snview.playback;

import java.util.List;

import com.starnet.snview.R;
import com.starnet.snview.devicemanager.DeviceItem;
import com.starnet.snview.syssetting.CloudAccount;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountsPlayBackExpanableAdapter extends BaseExpandableListAdapter {

	private Context ctx;
	private List<CloudAccount> users;// 星云账户

	public AccountsPlayBackExpanableAdapter(Context ctx,
			List<CloudAccount> users) {
		this.ctx = ctx;
		this.users = users;
	}

	@Override
	public int getGroupCount() {
		int size = 0;
		if (users != null) {
			size = users.size();
		}
		return size;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int size = 0;
		List<DeviceItem> items = users.get(groupPosition).getDeviceList();
		if (items != null) {
			size = items.size();
		}
		return size;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return users.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return users.get(groupPosition).getDeviceList().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.playback_cloudaccount_preview_item, null);
		}
		TextView txt = (TextView) convertView.findViewById(R.id.account_name);
		txt.setText(users.get(groupPosition).getUsername());
		ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);
		if (RealPlaybackStateUtils.exapandFlag) {
			arrow.setBackgroundResource(R.drawable.channel_listview_down_arrow_sel);
		}else {
			arrow.setBackgroundResource(R.drawable.channel_listview_right_arrow_sel);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(ctx).inflate(
					R.layout.playback_deviceitems_act, null);
		}
		TextView txt = (TextView) convertView.findViewById(R.id.channel_name);
		txt.setText(users.get(groupPosition).getDeviceList().get(childPosition).getDeviceName());
		
		final Button stateBtn = (Button) convertView.findViewById(R.id.stateBtn);
		if (RealPlaybackStateUtils.stateFlag) {
			stateBtn.setBackgroundResource(R.drawable.channellist_select_empty);
		}else {
			stateBtn.setBackgroundResource(R.drawable.channellist_select_alled);
		}
		stateBtn.setOnClickListener(new OnClickListener() {
			private boolean clickFlag = false;
			@Override
			public void onClick(View v) {
				if (clickFlag) {
					clickFlag = false;
					stateBtn.setBackgroundResource(R.drawable.channellist_select_empty);
					notifyDataSetChanged();
				}else{
					clickFlag = true;
					stateBtn.setBackgroundResource(R.drawable.channellist_select_alled);
					notifyDataSetChanged();
				}	
			}
		});
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}