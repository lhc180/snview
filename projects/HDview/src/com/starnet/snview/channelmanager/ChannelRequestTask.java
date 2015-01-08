package com.starnet.snview.channelmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.starnet.snview.channelmanager.xml.DVRDevice;
import com.starnet.snview.devicemanager.DeviceItem;
import com.starnet.snview.syssetting.CloudAccount;
import com.starnet.snview.util.CollectDeviceParams;
import com.starnet.snview.util.ReadWriteXmlUtils;

@SuppressLint("SdCardPath")
public class ChannelRequestTask {
	private int pos;
	private Context ctx;
	private Handler mHandler;
	private Thread timeThread;
	private Thread workThread;
	private CloudAccount reqCA;
	private final int TIME = 7;
	private boolean isCanceled;
	private SharedPreferences sp;
	private boolean isDocumentOpt;
	private boolean isRequestTimeOut;
	private boolean isTimeThreadOver;
	private boolean isStartWorkRequest;
	private final int TIMEOUT = 0x0002;
	private final int LOADSUC = 0x0003;
	private final int LOADFAI = 0x0004;
	private final String CLOUD_ACCOUNT_PATH = "/data/data/com.starnet.snview/cloudAccount_list.xml";

	public ChannelRequestTask(Context ctx,CloudAccount reqCA, Handler mHandler, int pos) {
		this.pos = pos;
		this.ctx = ctx;
		this.reqCA = reqCA;
		this.mHandler = mHandler;
		workThread = new Thread() {

			@Override
			public void run() {
				super.run();
				try {
					onStartWorkRequest();
				} catch (IOException e) {
					onRequestTimeOut();
				} catch (DocumentException e) {
					onDocumentOpt();
				}
			}
		};
		timeThread = new Thread() {
			@Override
			public void run() {
				super.run();
				boolean isRun = false;
				int timeCount = 0;
				while (!isRun && !isTimeThreadOver && !isCanceled) {
					timeCount++;
					try {
						Thread.sleep(1000);
						if (timeCount == TIME) {
							isRun = true;
							onTimeOut();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void onDocumentOpt() {

		isTimeThreadOver = true;
		isRequestTimeOut = true;
		isTimeThreadOver = true;
		isStartWorkRequest = true;

		if (!isCanceled && !isDocumentOpt) {
			isDocumentOpt = true;
			Message msg = new Message();
			msg.what = LOADFAI;
			Bundle data = new Bundle();
			data.putInt("position", pos);
			data.putSerializable("netCA", reqCA);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}
	}

	private void onRequestTimeOut() {
		isDocumentOpt = true;
		isTimeThreadOver = true;
		isStartWorkRequest = true;
		if (!isCanceled && !isRequestTimeOut) {
			isDocumentOpt = true;
			Message msg = new Message();
			msg.what = LOADFAI;
			Bundle data = new Bundle();
			data.putInt("position", pos);
			data.putSerializable("netCA", reqCA);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}
	}

	private void onStartWorkRequest() throws IOException, DocumentException {
		String domain = reqCA.getDomain();
		String port = reqCA.getPort();
		String username = reqCA.getUsername();
		String password = reqCA.getPassword();
		Document doc = ReadWriteXmlUtils.SendURLPost(domain, port, username,
				password, "");
		String result = ReadWriteXmlUtils.readXmlStatus(doc);
		if (result == null) {
			ArrayList<DVRDevice> dList = (ArrayList<DVRDevice>) ReadWriteXmlUtils
					.readXmlDVRDevices(doc);
			if (!isCanceled && !isStartWorkRequest) {
				Message msg = new Message();
				Bundle data = new Bundle();
				CloudAccount netAct = getCloudAccountFromDVRDevice(dList);
				sp = ctx.getSharedPreferences("isFirstWrite", Context.MODE_PRIVATE);
				boolean isFirst = sp.getBoolean(netAct.getUsername(), true);
				if (isFirst) {
					ReadWriteXmlUtils.writeNewCloudAccountToXML(netAct,CLOUD_ACCOUNT_PATH);// 第一次是写入，之后都是替代
					Editor editor = sp.edit();
					editor.putBoolean(netAct.getUsername(), false);
					editor.commit();
				}else {
					data.putBoolean("replace", true);
//					ReadWriteXmlUtils.specifyNewAccountInXML(netAct,CLOUD_ACCOUNT_PATH, pos-1);
				}
				isDocumentOpt = true;
				isTimeThreadOver = true;
				isRequestTimeOut = true;
				isTimeThreadOver = true;
				isStartWorkRequest = true;
				msg.what = LOADSUC;
				data.putInt("position", pos);
				data.putString("success", "Yes");
				data.putSerializable("netCA", netAct);
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		} else {
			if (!isCanceled && !isStartWorkRequest) {
				isDocumentOpt = true;
				isTimeThreadOver = true;
				isRequestTimeOut = true;
				isTimeThreadOver = true;
				isStartWorkRequest = true;
				Message msg = new Message();
				msg.what = LOADFAI;
				Bundle data = new Bundle();
				data.putInt("position", pos);
				data.putSerializable("netCA", reqCA);
				msg.setData(data);
				mHandler.sendMessage(msg);
			}
		}
	}

	private void onTimeOut() {
		isDocumentOpt = true;
		isTimeThreadOver = true;
		isRequestTimeOut = true;
		isTimeThreadOver = true;
		isStartWorkRequest = true;
		if (!isCanceled) {
			Message msg = new Message();
			msg.what = TIMEOUT;
			Bundle data = new Bundle();
			data.putInt("position", pos);
			data.putSerializable("netCA", reqCA);
			msg.setData(data);
			mHandler.sendMessage(msg);
		}
	}

	public void start() {
		isCanceled = false;
		isDocumentOpt = false;
		isRequestTimeOut = false;
		isTimeThreadOver = false;
		isStartWorkRequest = false;
		timeThread.start();
		workThread.start();
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public CloudAccount getCloudAccountFromDVRDevice(List<DVRDevice> dvrDevices) {
		CloudAccount cloudAccount = new CloudAccount();
		String domain = reqCA.getDomain();
		String port = reqCA.getPort();
		String username = reqCA.getUsername();
		String password = reqCA.getPassword();
		cloudAccount.setDomain(domain);
		cloudAccount.setPort(port);
		cloudAccount.setUsername(username);
		cloudAccount.setPassword(password);
		cloudAccount.setExpanded(false);// 暂时设置
		int dvrDeviceSize = dvrDevices.size();
		List<DeviceItem> deviceList = new ArrayList<DeviceItem>();
		for (int i = 0; i < dvrDeviceSize; i++) {
			DeviceItem deviceItem = new DeviceItem();

			DVRDevice dvrDevice = dvrDevices.get(i);
			int deviceType = 5;

			String deviceName = dvrDevice.getDeviceName();
			String svrIp = dvrDevice.getLoginIP();
			String svrPort = dvrDevice.getMobliePhonePort();
			String loginUser = dvrDevice.getLoginUsername();
			String loginPass = dvrDevice.getLoginPassword();
			String defaultChannel = dvrDevice.getStarChannel();
			// 设置设备信息
			deviceItem.setDefaultChannel(Integer.valueOf(defaultChannel));
			deviceItem.setDeviceName(deviceName);
			deviceItem.setSvrIp(svrIp);
			deviceItem.setSvrPort(svrPort);
			deviceItem.setLoginPass(loginPass);
			deviceItem.setLoginUser(loginUser);
			deviceItem.setSecurityProtectionOpen(true);
			deviceItem.setExpanded(false);
			deviceItem.setDeviceType(deviceType);
			String channelSum = dvrDevice.getChannelNumber();// 用于为设备添加通道列表而准备
			deviceItem.setChannelSum(channelSum);
			deviceItem.setPlatformUsername(username);
			deviceItem.setIdentify(true);
			List<Channel> channelList = new ArrayList<Channel>();
			int channeNumber = Integer.valueOf(channelSum);
			if (channeNumber != 0) {
				for (int j = 0; j < channeNumber; j++) {
					Channel channel = new Channel();
					channel.setChannelName(CollectDeviceParams.DEFAULT_CHANNELNAMEFOR_COLLECTDEVICE
							+ (j + 1));
					channel.setSelected(false);
					channel.setChannelNo((j + 1));
					channelList.add(channel);
				}
			}
			deviceItem.setChannelList(channelList);
			deviceList.add(deviceItem);
		}
		cloudAccount.setDeviceList(deviceList);
		return cloudAccount;
	}
}
