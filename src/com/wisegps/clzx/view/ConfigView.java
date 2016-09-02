package com.wisegps.clzx.view;
import com.wisegps.clzx.R;
import com.wisegps.clzx.app.Config;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
public class ConfigView{
	private Context context;
	private EditText ET_time;
	private RadioGroup radioGroup02;
	private RadioButton radioButton03,radioButton04;
	private boolean isRef = true;
	public ConfigView(Context context) {
		super();
		this.context = context;
	}
	/**
	 * 初始化界面
	 */
	public void setting() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.config, null);
		radioGroup02 = (RadioGroup) view.findViewById(R.id.RadioGroup02);
		radioButton03 = (RadioButton) view.findViewById(R.id.RadioButton03);
		radioButton04 = (RadioButton) view.findViewById(R.id.RadioButton04);
		radioGroup02.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radioButton03.getId()) {
					isRef = true;
				}else if (checkedId == radioButton04.getId()) {
					isRef = false;
				}
			}
		});
		
		ET_time = (EditText)view.findViewById(R.id.ET_config_update_time);
		getSharedPrefernece();
		
		AlertDialog.Builder bulder = new AlertDialog.Builder(context);
		bulder.setView(view);
		bulder.setTitle(R.string.setting);// 设置标题
		bulder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SharedPreferences preferences = context.getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
						Editor editor = preferences.edit();
						editor.putInt("ShortTime", Integer.valueOf(ET_time.getText().toString()));
						editor.putBoolean("isRef", isRef);
						editor.commit();
						Toast.makeText(context, R.string.config_update_in_xml_ok, Toast.LENGTH_LONG).show();
					}
				});
		bulder.setNegativeButton(android.R.string.cancel, null);
		bulder.show();
	}

	/**
	 * 获取当前参数
	 */
	private void getSharedPrefernece(){
		SharedPreferences preferences = context.getSharedPreferences(Config.Shared_Preferences, Context.MODE_PRIVATE);
		int ShortTime = preferences.getInt("ShortTime", 30);
		boolean isRef = preferences.getBoolean("isRef", true);
		ET_time.setText(String.valueOf(ShortTime));
		if(isRef){
			radioButton03.setChecked(true);
		}else{
			radioButton04.setChecked(true);
		}
	}
}
