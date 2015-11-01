package com.android.getit.netBeanLoader.baseNetBean;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.android.getit.GetITApp;
import com.android.getit.Utils.DeviceInfo;
import com.android.getit.Utils.Profile;
import com.android.getit.Utils.Utils;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseNetBean implements INetBean{
	public static String HOST = OFFICIAL_USER_HOST_TEST ;

	public List<BasicNameValuePair> sParams = new ArrayList<>() ;



	protected String mJsonStr ;
	protected Object result ;


	@Override
	public List<BasicNameValuePair> getParams() {
		if(sParams.size() == 0)
			initParams();
		List<BasicNameValuePair> params = new ArrayList<>(sParams) ;
		return params;
	}

	@Override
	public List<BasicNameValuePair> getParamsNeedMore() {
		if(sParams.size() == 0)
			initParams();
		List<BasicNameValuePair> params = new ArrayList<>(sParams) ;
		return params;
	}


	@Override
	public Object getResult() {
		return result;
	}

	public String getBaseUrl (){
		Context context = GetITApp.context;
		final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		if (TextUtils.isEmpty(deviceId)) {
			deviceId = "null";
		}

		long tmp = System.currentTimeMillis() / 1000;
		String timeStamp = String.valueOf(tmp);// 时间戳

		String numRaw = "GET" + ":" + getSuffix() + ":" + timeStamp + ":" + NEWSECRET;
		String md5NumRaw = Utils.md5(numRaw);

		final StringBuilder sb = new StringBuilder();
		sb.append("?pid=").append(Profile.getPid());
		sb.append("&_t_=").append(timeStamp);
		sb.append("&_e_=").append(SECRET_TYPE);
		sb.append("&_s_=").append(md5NumRaw);
		sb.append("&guid=").append(Utils.getGUID());
		sb.append("&ver=").append(Profile.VER);
		sb.append("&network=").append(Utils.getNetworkType());
		if (!TextUtils.isEmpty(DeviceInfo.OPERATOR)) {
			sb.append("&operator=" + DeviceInfo.OPERATOR);
		}
		return sb.toString() ;
	}

	public abstract String getSuffix() ;


	public  void initParams(){
		long tmp = System.currentTimeMillis() / 1000;
		String timeStamp = String.valueOf(tmp);// 时间戳

		String numRaw = "POST" + ":" + getSuffix() + ":" + timeStamp + ":" + NEWSECRET;

		String md5NumRaw = Utils.md5(numRaw);

		sParams.add(new BasicNameValuePair("pid",Profile.getPid()));
		sParams.add(new BasicNameValuePair("_t_", timeStamp));
		sParams.add(new BasicNameValuePair("_e_", SECRET_TYPE));
		sParams.add(new BasicNameValuePair("_s_", md5NumRaw));
		sParams.add(new BasicNameValuePair("guid",Utils.getGUID()));
		sParams.add(new BasicNameValuePair("ver",Profile.VER));
		sParams.add(new BasicNameValuePair("network",Utils.getNetworkType()));
		if (!TextUtils.isEmpty(DeviceInfo.OPERATOR)) {
			sParams.add(new BasicNameValuePair("&operator=",DeviceInfo.OPERATOR));
		}
	}

	@Override
	public boolean addCookie() {
		return false;
	}

	@Override
	public boolean getCookie() {
		return false;
	}
}
