package com.wisegps.clzx.view;
import com.wisegps.clzx.R;
import com.wisegps.clzx.biz.AccountBiz;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
public class ChangepsdView implements Callback {
	
	private Context context;
	private String LoginPws;
	private String newpwd;
	private ProgressDialog Dialog;
	private Handler handler;
	public ChangepsdView(Context context) {
		super();
		this.context = context;
		handler = new Handler(this);
	}

	/**
	 * 修改密码对话框
	 */
	public void changePwd() {
		View view = LayoutInflater.from(context).inflate(
				R.layout.changepwd, null);
		final EditText et_oldpwd = (EditText) view
				.findViewById(R.id.oldPassWord_ET);
		final EditText et_newpwd = (EditText) view
				.findViewById(R.id.newPassWord_ET);
		final EditText et_newpwdtwo = (EditText) view
				.findViewById(R.id.newPassWordTwo_ET);
		AlertDialog.Builder bulder = new AlertDialog.Builder(context);
		bulder.setView(view);
		bulder.setTitle(R.string.changePwd_title);// 设置标题
		bulder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						LoginPws = et_oldpwd.getText().toString().trim();
						newpwd = et_newpwd.getText().toString().trim();
						String newpwdtoo = et_newpwdtwo.getText().toString()
								.trim();
						if (LoginPws.equals("") || newpwd.equals("")
								|| newpwdtoo.equals("")) {
							Toast.makeText(context,
									R.string.change_pwd_null,
									Toast.LENGTH_SHORT).show();
							return;
						} else {
							if (newpwd.equals(newpwdtoo)) {
								Dialog = ProgressDialog.show(context,
										context.getString(R.string.login),
										context.getString(R.string.change_pwd_now),
										true);
								AccountBiz biz = new AccountBiz(handler);
								biz.changePsd(LoginPws, newpwdtoo);
							} else {
								Toast.makeText(context,
										R.string.change_pwd_TwoNewPwd_false,
										Toast.LENGTH_SHORT).show();
								return;
							}
						}
					}
				});
		bulder.setNegativeButton(android.R.string.cancel, null);
		bulder.show();
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (Dialog != null) {
			Dialog.dismiss();
		}
		String resultPwd = msg.obj.toString();
		if (resultPwd.indexOf("0") > 0) {
			Toast.makeText(context, R.string.change_pwd_true,
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, R.string.change_pwd_false,
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}
