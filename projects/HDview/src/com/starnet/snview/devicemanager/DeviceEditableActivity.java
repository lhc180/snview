package com.starnet.snview.devicemanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.starnet.snview.R;
import com.starnet.snview.channelmanager.Channel;
import com.starnet.snview.component.BaseActivity;

public class DeviceEditableActivity extends BaseActivity {
	
	private static final String TAG = "DeviceEditableActivity";
	
	private EditText record_et;
	private EditText server_et;
	private EditText port_et;
	private EditText username_et;
	private EditText password_et;
	private EditText defaultChannel_et;
	private EditText channelnumber_et;

	private DeviceItem clickDeviceItem;

	private Button saveButton;
	private int oriChannelNum;//未编辑后的通道数量
	private int newChannelNum;//编辑后的通道数量

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_manager_editable_acitivity);
		superChangeViewFromBase();
		setListeners();
	}

	private void setListeners() {
		super.getLeftButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DeviceEditableActivity.this.finish();
			}
		});

		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取信息
				String dName = record_et.getText().toString();
				String svrIp = server_et.getText().toString();
				String svrPt = port_et.getText().toString();
				String lUser = username_et.getText().toString();

				String lPass = password_et.getText().toString();
				String dfChl = defaultChannel_et.getText().toString();
				String chSum = channelnumber_et.getText().toString();

				if ((!dName.equals("") && !svrIp.equals("")
						&& !svrPt.equals("") && !lUser.equals("")
						&& !lPass.equals("") && !dfChl.equals("") 
						&& !chSum.equals(""))) {// 检查信息是否为空
					int defaultChannl = Integer.valueOf(dfChl);
					int newChannelNum = Integer.valueOf(chSum);
					if (defaultChannl < newChannelNum) {
						clickDeviceItem.setChannelSum(chSum);
						clickDeviceItem.setDefaultChannel(Integer.valueOf(dfChl));
						clickDeviceItem.setDeviceName(dName);
						clickDeviceItem.setSvrIp(svrIp);
						clickDeviceItem.setSvrPort(svrPt);
						clickDeviceItem.setLoginUser(lUser);
						clickDeviceItem.setLoginPass(lPass);
						List<Channel> channelList = new ArrayList<Channel>();
						if(newChannelNum != oriChannelNum){
							for (int i = 0; i < newChannelNum; i++) {
								Channel channel = new Channel();
								channel.setChannelName("通道"+(i+1));
								channel.setChannelNo((i+1));
								channel.setSelected(false);
								channelList.add(channel);
							}
							clickDeviceItem.setChannelList(channelList);
						}
						// 并返回原来的界面
						Intent data = new Intent();
						Bundle bundle = new Bundle();
						bundle.putSerializable("cDeviceItem", clickDeviceItem);
						data.putExtras(bundle);
						setResult(11, data);
						DeviceEditableActivity.this.finish();
					}else {
						String text = getString(R.string.defaultchannel_channelNumber_small);
						Toast toast = Toast.makeText(DeviceEditableActivity.this, text, Toast.LENGTH_SHORT);
						toast.show();
					}
				}else {
					String text = getString(R.string.edit_info_notnull);
					Toast toast = Toast.makeText(DeviceEditableActivity.this, text, Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});

	}

	private void superChangeViewFromBase() {
		super.setRightButtonBg(R.drawable.navigation_bar_add_btn_selector);
		super.setLeftButtonBg(R.drawable.navigation_bar_back_btn_selector);
		String tileName = getString(R.string.common_drawer_device_management);
		super.setTitleViewText(tileName);
		super.hideExtendButton();
		super.setToolbarVisiable(false);

		saveButton = super.getRightButton();

		record_et = (EditText) findViewById(R.id.et_device_add_record);
		server_et = (EditText) findViewById(R.id.et_device_add_server);
		port_et = (EditText) findViewById(R.id.et_device_add_port);
		username_et = (EditText) findViewById(R.id.et_device_add_username);

		password_et = (EditText) findViewById(R.id.et_device_add_password);
		defaultChannel_et = (EditText) findViewById(R.id.et_device_add_defaultChannel);
		channelnumber_et = (EditText) findViewById(R.id.et_device_add_channelnumber);

		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				clickDeviceItem = (DeviceItem) bundle.getSerializable("clickDeviceItem");
			}
		}

		String deviceName = clickDeviceItem.getDeviceName();
		String loginPass = clickDeviceItem.getLoginPass();
		String loginUser = clickDeviceItem.getLoginUser();
		String channelSum = clickDeviceItem.getChannelSum();
		String defaultChannel = String.valueOf(clickDeviceItem.getDefaultChannel());

		String svrIp = clickDeviceItem.getSvrIp();
		String svrPort = clickDeviceItem.getSvrPort();
		
		String word1 = getString(R.string.device_manage_offline_en);
		String word2 = getString(R.string.device_manage_offline_cn);
		String word3 = getString(R.string.device_manage_online_cn);
		String word4 = getString(R.string.device_manage_online_en);
		
		String wordLen = getString(R.string.device_manage_off_on_line_length);
		int len = Integer.valueOf(wordLen);
		String dName = deviceName.substring(0, len);
		
		if ((dName.contains(word1) || dName.contains(word2)|| dName.contains(word3)|| dName.contains(word4))
				&& deviceName.length() > (len-1)) {
			deviceName = deviceName.substring(len);
		}
		
		oriChannelNum = Integer.valueOf(channelSum);//获取通道数量...
		
		record_et.setText(deviceName);
		server_et.setText(svrIp);
		port_et.setText(svrPort);
		username_et.setText(loginUser);
		password_et.setText(loginPass);
		defaultChannel_et.setText(defaultChannel);
		channelnumber_et.setText(channelSum);
	}
}