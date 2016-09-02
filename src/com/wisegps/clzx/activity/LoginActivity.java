package com.wisegps.clzx.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Config;
import com.wisegps.clzx.app.Data;
import com.wisegps.clzx.app.IntentExtra;
import com.wisegps.clzx.biz.UpdateManager;
import com.wisegps.clzx.net.NetThread;
import com.wisegps.clzx.util.GetSystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Wu
 *
 * 登录界面
 */
public class LoginActivity extends Activity{	
	private final String TAG = "LoginActivity";
	private final int Login = 1; 	
	private final int Update = 2;
	
	private EditText et_name,et_pwd;
	private TextView tv_update;
	private TextView tv_version;
	private CheckBox cb_isSavePwd;
	
	/*全局变量*/
	private ProgressDialog Dialog = null;    //progress
	private String LoginName ;               //用户名
	private String LoginPws ;                //密码
	private boolean LoginNote;               //是否保存密码
	private double Version;   //当前版本
	private String VersonUrl; //下载路径
	private String logs;   //更新信息
	private int Text_size; //字体大小
	
	private Button bt_login ;
	private boolean autoLogin = false;
	private boolean showLetter = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
		getDM();
		getSp();
		isUpdate();
		showLetterActivity();
	}	
	
	public void showLetterActivity(){
		Intent intent = getIntent();
		
		autoLogin = intent.getBooleanExtra(IntentExtra.AutoLogin, false);
		showLetter = intent.getBooleanExtra(IntentExtra.ShowLetter, false);
		
		if(autoLogin){
			bt_login.performClick();
		}
		
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Login:
				LoginData(msg);
				break;
			case Update:
				UpdateData(msg);
				break;
			}
		}
	};
	
	OnClickListener OCL = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.bt_login:
				//登陆事件
				LoginName = et_name.getText().toString();
				LoginPws = et_pwd.getText().toString();
				if (LoginName.equals("")|| LoginPws.equals("")) {
					Toast.makeText(LoginActivity.this, R.string.Login_null, Toast.LENGTH_LONG).show();
				}else{
					String url = Config.url + "login?username=" + LoginName + "&password=" + GetSystem.getM5DEndo(LoginPws) +"&mac=" + GetSystem.getMacAddress(LoginActivity.this);
					
					
					Dialog = ProgressDialog.show(LoginActivity.this,getString(R.string.login),getString(R.string.login_pd_context),true);
					new Thread(new NetThread.GetDataThread(handler, url, Login)).start();
					
					Log.i("LoginActivity", "登录"+url);
				}
				break;
			case R.id.tv_update:
				UpdateManager mUpdateManager = new UpdateManager(LoginActivity.this,VersonUrl,logs,Version);
			    mUpdateManager.checkUpdateInfo();
				break;
			}
		}
	};
	
	private void LoginData(Message msg){
    	if(Dialog != null){
			Dialog.dismiss();
		}
		try {
			Log.i("LoginActivity", msg.obj.toString());
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			if(jsonObject.opt("auth_code") == null){
				String status_code = jsonObject.getString("status_code");
				if(status_code.equals("5")){
					Toast.makeText(getApplicationContext(), R.string.accout_bind_phone, Toast.LENGTH_LONG).show();
				}else if(status_code.equals("2") || status_code.equals("1")){
					Toast.makeText(getApplicationContext(), R.string.login_id_wrong, Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(), R.string.login_id_wrong, Toast.LENGTH_LONG).show();
				}
			}else{
				
			
				String auth_code = jsonObject.getString("auth_code");
				int cust_id = jsonObject.getInt("cust_id");
				String cust_name = jsonObject.getString("cust_name");
				int parent_cust_id = jsonObject.getInt("parent_cust_id");
				

				String tree_path = jsonObject.getString("tree_path");
				String number_type = jsonObject.getString("number_type");
				String Url = "http://"+jsonObject.getString("host")+":"+jsonObject.getString("port")+"/";
				//更新配置信息
				SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putString("LoginName", LoginName);
				editor.putString("LoginPws", LoginPws);
				editor.putBoolean("LoginNote", LoginNote);					
				editor.commit();
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				
				intent.putExtra(IntentExtra.ShowLetter, showLetter);
//				App app = (App) getApplication();
//				app.setAuth_code(auth_code);
//				app.setCust_id(cust_id);
//				app.setNumber_type(number_type);
//				app.setUrl(Url);
//				app.setText_size(Text_size);
//				app.setLoginName(LoginName);
//				app.setTree_path(tree_path);
				
				Data.auth_code = auth_code;
				Data.cust_id = cust_id;
				Data.parent_id = parent_cust_id;
				Data.number_type = number_type;
				Data.Url = Url;
				Data.Text_size = Text_size;
				Data.LoginName = LoginName;
				Data.cust_name = cust_name;
				Data.tree_path = tree_path;
				
				startActivity(intent);
				finish();
			}				
		}catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	/**
	 * 初始化数据
	 */
	private void getSp(){ 
		Version = Double.valueOf(GetSystem.GetVersion(getApplicationContext(), Config.PackString));	
		//读取sharedPreferences配置信息
		SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
		String LoginName = preferences.getString("LoginName", "");
		String LoginPws = preferences.getString("LoginPws", "");
		LoginNote = preferences.getBoolean("LoginNote", true);
		VersonUrl = preferences.getString("VersonUrl", "");
		logs = preferences.getString("logs", "");
		et_name.setText(LoginName);
		et_pwd.setText(LoginPws);
		cb_isSavePwd.setChecked(LoginNote);
	}
	
	
	/**
	 * 获取版本号
	 * @return 当前应用的版本号
	 */
	public String getVersion() {
	    try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        String version = info.versionName;
	        return "版本：CLZX_V"+ version;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	
	/**
	 * 初始化控件
	 */
	private void init() {
		et_name = (EditText) findViewById(R.id.et_account);
		et_pwd = (EditText) findViewById(R.id.et_password);
		tv_version = (TextView) findViewById(R.id.tv_version);
		String version = getVersion();
		tv_version.setText(version);
		cb_isSavePwd = (CheckBox) findViewById(R.id.NotePsw);
		cb_isSavePwd.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox)v;
				LoginNote = cb.isChecked();
			}
		});
		bt_login = (Button) findViewById(R.id.bt_login);
		bt_login.setOnClickListener(OCL);
		tv_update = (TextView)findViewById(R.id.tv_update);
		tv_update.setOnClickListener(OCL);
	}	
		
	/**
	 * 根据分辨率，设置字体大小
	 */
	private void getDM(){
		 DisplayMetrics dm = new DisplayMetrics();  
	     getWindowManager().getDefaultDisplay().getMetrics(dm);
	     int dpi = dm.densityDpi;
		 if(dpi <= 120){
			 Text_size = 8;
			 return;
	     }else if(dpi <= 160){
	    	 Text_size = 12;
	    	 return;
	     }else if(dpi <= 240){
	    	 Text_size = 20;
	    	 return;
	     }else{
	    	 Text_size = 24;
	    	 return;
	     }
	}
	/**
	 * 是否读取更新信息,如果有sd卡则更新
	 */
	private void isUpdate(){
		if(isSdCardExist()){
			try {
				//得到系统的版本
				JSONArray jsonArray = new JSONArray(logs);
				for (int i = 0; i < jsonArray.length(); i++) {
					 double logVersion = Double.valueOf(jsonArray.getJSONObject(i).getString("version"));
					 if(logVersion > Version){
						 tv_update.setVisibility(View.VISIBLE);
						 break;
					 }
				}
			} catch (Exception e) {
				Log.d(TAG, "解析更新信息出错");
			}
			String url = Config.url + Config.UpdateUrl;
			new Thread(new NetThread.GetDataThread(handler, url, Update)).start();
		}else{
			Toast.makeText(LoginActivity.this, R.string.SD_NOTFIND, Toast.LENGTH_LONG).show();
		}
	}
	/**
	 * 判断sd卡是否存在
	 * @return
	 */
	private boolean isSdCardExist(){
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}
	/**
     * 读取更新接口信息
     * @param msg
     */
    private void UpdateData(Message msg){
    	try {
			JSONObject jsonObject = new JSONObject(msg.obj.toString());
			SharedPreferences preferences = getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putString("Verson", jsonObject.getString("version"));
			editor.putString("VersonUrl", jsonObject.getString("app_path"));
			editor.putString("logs", jsonObject.getString("logs"));
			editor.commit();
			
			VersonUrl = jsonObject.getString("app_path");
			logs = jsonObject.getString("logs");			
			if(isSdCardExist()){
				try {
					//得到系统的版本
					JSONArray jsonArray = new JSONArray(logs);
					for (int i = 0; i < jsonArray.length(); i++) {
						 double logVersion = Double.valueOf(jsonArray.getJSONObject(i).getString("version"));
						 if(logVersion > Version){
							 tv_update.setVisibility(View.VISIBLE);
							 break;
						 }
					}
				} catch (Exception e) {
					Log.d(TAG, "解析更新信息出错");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}