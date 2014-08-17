package com.starnet.snview.syssetting;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.starnet.snview.R;
import com.starnet.snview.channelmanager.xml.CloudAccountXML;
import com.starnet.snview.component.BaseActivity;
import com.starnet.snview.devicemanager.CloudService;
import com.starnet.snview.devicemanager.CloudServiceImpl;
import com.starnet.snview.util.SynObject;

@SuppressLint("SdCardPath")
public class CloudAccountSettingActivity extends BaseActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "CloudAccountSettingActivity";
	private final String filePath = "/data/data/com.starnet.snview/star_cloudAccount.xml";

	private EditText serverEditText;
	private EditText portEditText;
	private EditText usernameEditText;
	private EditText passwordEditText;
	private RadioButton isenablYseRadioBtn;
	private RadioButton isenablNoRadioBtn;
	private RadioGroup isenableRadioGroup;

	private CloudAccount cloudAccount;
	private CloudAccountXML caXML;
	private String showStatus;

	private final int DDNS_RESP_SUCC = 0x1100; // 获取设备信息成功
	private final int DDNS_RESP_FAILURE = 0x1101; // 获取设备信息失败
	private final int DDNS_REQ_TIMEOUT = 0x1102; // 设备列表请求超时
	private final int DDNS_SYS_FAILURE = 0x1103; // 非DDNS返回错误
	private CloudService cloudService = new CloudServiceImpl("conn1");
//	private List<DVRDevice> deviceInfoList;
	String server;
	String port;
	String username;
	String password;

	// private CloudAccount clickCloudAccount;
	private Button identify_btn;//验证客户是否网络可达按钮

	private SynObject synObj = new SynObject();

	private Handler responseHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (synObj.getStatus() == SynObject.STATUS_RUN) {
				return;
			}

			dismissDialog(1);

			// 解除挂起， 程序往下执行
			synObj.resume();

			String printSentence = "";
			String errMsg = "";

			switch (msg.what) {
			case 0:
				Toast toast0 = Toast.makeText(CloudAccountSettingActivity.this,showStatus, Toast.LENGTH_LONG);
				toast0.show();
				dismissDialog(1);
				break;
			case DDNS_RESP_SUCC:
				printSentence = getString(R.string.add_sucess);
				Toast toast1 = Toast.makeText(CloudAccountSettingActivity.this,printSentence, Toast.LENGTH_LONG);
				toast1.show();
				dismissDialog(1);
				caXML.addNewCloudAccoutNodeToRootXML(filePath,cloudAccount);// 保存到XML文档中。。
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("cloudAccount",cloudAccount);
				intent.putExtras(bundle);
				setResult(3, intent);
				CloudAccountSettingActivity.this.finish();
				break;
			case DDNS_RESP_FAILURE:
				errMsg = msg.getData().getString("ERR_MSG");
				Toast.makeText(CloudAccountSettingActivity.this, errMsg, Toast.LENGTH_LONG).show();
				break;
			case DDNS_SYS_FAILURE:
				errMsg = getString(R.string.DEVICE_LIST_ErrorReason);
				Toast.makeText(CloudAccountSettingActivity.this, errMsg, Toast.LENGTH_LONG).show();
				break;
			case DDNS_REQ_TIMEOUT:
				errMsg = getString(R.string.DEVICE_LIST_REQ_TIMEOUT);
				Toast.makeText(CloudAccountSettingActivity.this, errMsg, Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_setting_cloudaccount_setting_activity_another);

		initView();
		setListeners();
	}

	private void initView() {
		super.setTitleViewText(getString(R.string.navigation_title_system_setting_cloudaccount_setting));
		super.hideExtendButton();
		super.setRightButtonBg(R.drawable.navigation_bar_add_btn_selector);
		super.setToolbarVisiable(false);
		super.setLeftButtonBg(R.drawable.navigation_bar_back_btn_selector);

		serverEditText = (EditText) findViewById(R.id.cloudaccount_setting_server_edittext);
		portEditText = (EditText) findViewById(R.id.cloudaccount_setting_port_edittext);
		usernameEditText = (EditText) findViewById(R.id.cloudaccount_setting_username_edittext);
		passwordEditText = (EditText) findViewById(R.id.cloudaccount_setting_password_edittext);
		isenableRadioGroup = (RadioGroup) findViewById(R.id.cloudaccount_setting_isenable_radioGroup);
		isenablYseRadioBtn = (RadioButton) findViewById(R.id.cloudaccount_setting_isenable_yes_radioBtn);
		isenablNoRadioBtn = (RadioButton) findViewById(R.id.cloudaccount_setting_isenable_no_radioBtn);
		
		identify_btn = (Button) findViewById(R.id.identify_cloudaccount_right);
	}

	private void setListeners() {
		super.getLeftButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CloudAccountSettingActivity.this.finish();
			}
		});
		
		identify_btn.setOnClickListener(new OnClickListener() {//验证用户的有效性；
			@Override
			public void onClick(View v) {
				//requset4DeviceList();
				//synObj.suspend();// 挂起等待请求结果
			}
		});
		

		super.getRightButton().setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cloudAccount = new CloudAccount();
				if (isenablYseRadioBtn.isChecked()) {
					cloudAccount.setEnabled(true);
				} else if (isenablNoRadioBtn.isChecked()) {
					cloudAccount.setEnabled(false);
				}
				
				// 验证是否有为空的现象
				server = serverEditText.getText().toString();
				port = portEditText.getText().toString();
				username = usernameEditText.getText().toString();
				password = passwordEditText.getText().toString();

				if (!server.equals("") && !port.equals("")&& !username.equals("") && !password.equals("")) {
					cloudAccount.setDomain(server);
					cloudAccount.setPassword(password);
					cloudAccount.setUsername(username);
					cloudAccount.setPort(port);
					caXML = new CloudAccountXML();
					try {
						List<CloudAccount> cloudAcountList = caXML.getCloudAccountList(filePath);
						boolean result = judgeListContainCloudAccount(cloudAccount, cloudAcountList);
						if (result) {
							String printSentence = getString(R.string.already_contain_no_need);
							Toast toast = Toast.makeText(CloudAccountSettingActivity.this,printSentence, Toast.LENGTH_SHORT);
							toast.show();
						} else {
							requset4DeviceList();
							synObj.suspend();// 挂起等待请求结果
						}
					} catch (Exception e1) {
						System.out.println(e1.toString());
					}
				} else {
					String printSentence = getString(R.string.add_null_content);
					Toast toast3 = Toast.makeText(CloudAccountSettingActivity.this,printSentence, Toast.LENGTH_LONG);
					toast3.show();
				}
			}
		});
	}

	private void requset4DeviceList() {
		showDialog(1);
		(new RequestDeviceInfoThread(responseHandler)).start();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			ProgressDialog progress = ProgressDialog.show(this, "",getString(R.string.identify_user_right), true, true);
			progress.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					dismissDialog(1);
					synObj.resume();
//					CloudAccountSettingActivity.this.finish();
				}
			});
			return progress;
		default:
			return null;
		}
	}

	class RequestDeviceInfoThread extends Thread {
		private Handler handler;
		public RequestDeviceInfoThread(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			Message msg = new Message();
			try {
				Document doc = cloudService.SendURLPost(server, port, username,password);
				String requestResult = cloudService.readXmlStatus(doc);
				if (requestResult == null) // 请求成功，返回null
				{
//					deviceInfoList = cloudService.readXmlDVRDevices(doc);
					msg.what = DDNS_RESP_SUCC;
				} else { // 请求失败，返回错误原因
					Bundle errMsg = new Bundle();
					msg.what = DDNS_RESP_FAILURE;
					errMsg.putString("ERR_MSG", requestResult);
					msg.setData(errMsg);
				}
			} catch (DocumentException e) {
				msg.what = DDNS_SYS_FAILURE;
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				msg.what = DDNS_REQ_TIMEOUT;
				e.printStackTrace();
			} catch (IOException e) {
				msg.what = DDNS_SYS_FAILURE;
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}

	private boolean judgeListContainCloudAccount(CloudAccount cloudAccount,List<CloudAccount> cloudAccountList2) {
		boolean result = false;
		int size = cloudAccountList2.size();
		for (int i = 0; i < size; i++) {
			CloudAccount cA = cloudAccountList2.get(i);
			String cADomain = cA.getDomain();
			String cAPort = cA.getPort();
			String cAUsername = cA.getUsername();
			String cAPassword = cA.getPassword();
			/* boolean isEnabled = cA.isEnabled(); */
			if (cloudAccount.getUsername().equals(cAUsername)&& cloudAccount.getDomain().equals(cADomain)
				&& cloudAccount.getPassword().equals(cAPassword)&& cloudAccount.getPort().equals(cAPort)) {/*&&(isEnabled ==cloudAccount.isEnabled())*/
				result = true;
				break;
			}
		}
		return result;
	}
}