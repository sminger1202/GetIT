package com.android.getit.netBeanLoader;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.android.getit.netBeanLoader.baseNetBean.INetBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 加载数据的流程类
 * @author gengchenghai
 *
 * @param
 */
public class BeanLoaderImpl implements IBeanLoader{
	private static final int 	MSG_FLAGS_CACHE_COMPLETE = 1 ;
	private static final int 	MSG_FLAGS_HTTP_COMPLETE_SUCCESS = 2 ;
	private static final int 	MSG_FLAGS_HTTP_COMPLETE_FAIL = 3 ;
	
	public static final long 	REPATE_LOAD_TIME = 1000 * 60 * 0 ;


	private static final Object LOCK = new Object() ;
	
	private long				sLastLoadHttpTime = 0  ;
	
	private LoadHandler 		mHandler ;
	private Context mContext ;
	private ILoadCallback		mCallback ;
	private boolean 			bLoadMore = false ;                                   
	
	private Map<Integer, INetBean> mNetBeans = new HashMap<Integer, INetBean>();
	
	public BeanLoaderImpl(Context context) {
		if(context == null){
			return ;
		}
		mContext = context.getApplicationContext() ;
		mHandler = new LoadHandler(mContext.getMainLooper()) ;
	}
	
	@Override
	public void setCallback(ILoadCallback callback) {
		mCallback = callback ;
	}

	@Override
	public void loadCache(INetBean bean , int tag) {
		if(bean == null || TextUtils.isEmpty(bean.getCachePath())){
			return ;
		}
		mNetBeans.put(tag, bean) ;
		LoadCacheThread thread = new LoadCacheThread(tag) ;
		thread.start(); 
	}

	@Override
	public void loadHttp(INetBean bean , int tag) {
		if(bean == null){
			return ;
		}
		mNetBeans.put(tag, bean) ;
		HttpRequestImpl impl = new HttpRequestImpl(mContext) ;
		impl.startRequest(bean.getUrl(), bean.getParams(), new HttpRequestCall(), bean.getHttpMethod().equals(INetBean.HTTPPOST), tag ,bean.addCookie(),bean.getCookie()) ;
	}


	
	@Override
	public void loadData(INetBean bean , int tag) {
		if(bean != null){
			loadCache(bean , tag) ;
			if(System.currentTimeMillis() - sLastLoadHttpTime > REPATE_LOAD_TIME){
				loadHttp(bean , tag) ;
			}
		}
	}
	
	@Override
	public void loadMore(int tag) {
		if (mNetBeans == null) {
			return;
		}
		INetBean netBean = mNetBeans.get(tag) ;
		bLoadMore = true ;
		HttpRequestImpl impl = new HttpRequestImpl(mContext) ;
		impl.startRequest(netBean.getUrl(), netBean.getParamsNeedMore(), new HttpRequestCall(), true , tag,netBean.addCookie(),netBean.getCookie()) ;
	}
	
	class HttpRequestCall implements IHttpRequestCallback{
		@Override
		public void onRequsetComplete(boolean bSuccess, int errorCode, String json , int tag) {
			if(bSuccess){
				INetBean netbean = mNetBeans.get(tag) ;
				if(netbean != null){
					netbean.parseJson(json , bLoadMore) ;
				}
				Message msg = mHandler.obtainMessage() ;
				msg.arg1 = tag ;
				msg.obj = netbean.getResult() ;
				msg.what = MSG_FLAGS_HTTP_COMPLETE_SUCCESS ;
				mHandler.sendMessage(msg) ;
				if(!TextUtils.isEmpty(json) && !bLoadMore){
					writeJsonCache(json, netbean.getCachePath()) ;
				}
			}else{
				Message msg = mHandler.obtainMessage() ;
				msg.arg1 = tag ;
				msg.what = MSG_FLAGS_HTTP_COMPLETE_FAIL ;
				mHandler.sendMessage(msg) ;
			}
		}
	}

	class LoadHandler extends Handler {

		public LoadHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_FLAGS_CACHE_COMPLETE :
				if(mCallback != null){
					mCallback.onCacheComplete(msg.arg1 , msg.obj) ;
				}
				break ;
			case MSG_FLAGS_HTTP_COMPLETE_SUCCESS:
				if(mCallback != null){
					mCallback.onHttpComplete(LOAD_SUCCESS, msg.arg1 , msg.obj) ;
					sLastLoadHttpTime = System.currentTimeMillis() ;
				}
				break ;
			case MSG_FLAGS_HTTP_COMPLETE_FAIL:
				if(mCallback != null){
					mCallback.onHttpComplete(LOAD_FAIL , msg.arg1 , null) ;
				}
				break ;
			}
		}
	}
	
	/**
	 * 加载缓存的线程
	 * @author gengchenghai
	 *
	 */
	class LoadCacheThread extends Thread {
		private int mTag ;
		
		LoadCacheThread(int tag){
			mTag = tag ;
		}
		
		@Override
		public void run() {
			
			if(mNetBeans == null){
				return ;
			}
			INetBean netBean  = mNetBeans.get(mTag) ;
			String mFilePath = netBean.getCachePath();
			if(TextUtils.isEmpty(mFilePath)){
				return ;
			}
			try {
				String json = readJsonCache(mFilePath)  ;
				if(netBean.parseJson(json , false)){
					Message msg = mHandler.obtainMessage() ;
					msg.arg1 = mTag ;
					msg.obj = netBean.getResult() ;
					msg.what = MSG_FLAGS_CACHE_COMPLETE ;
					mHandler.sendMessage(msg) ;
				}
			} catch (IOException e) {
				return  ;
			}
		}
		
		/***
		 * @param filePath
		 * @return
		 * @throws IOException
		 */
		public String readJsonCache(String filePath) throws IOException {
			synchronized (LOCK) {
				File file = new File(filePath);
				if (!file.exists() || file.isDirectory()) {
					return null;
				}
				BufferedReader br = null;
				StringBuffer sb = new StringBuffer();
				try {
					br = new BufferedReader(new FileReader(file));
					String temp = null;
					temp = br.readLine();
					while (temp != null) {
						sb.append(temp);
						temp = br.readLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new IOException(e.getMessage());
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				return sb.toString();
			}
			}
	}

	/**
	 * 本地缓存 写入json文件
	 * 
	 * @throws IOException
	 */
	public void writeJsonCache(String result, String filePath) {
			synchronized(LOCK){
				if(TextUtils.isEmpty(filePath)){
					return ;
				}
				File file = new File(filePath);
				File fileDir = file.getParentFile() ;
				
				if(fileDir == null){
					return ;
				}
				
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				
				if (file.exists()) {
					file.delete();
				}
				
				FileOutputStream out = null;
				try {
					file.createNewFile();
					out = new FileOutputStream(file, true);
					out.write(result.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}	
	}

	
}
