package com.android.getit.netBeanLoader;

import com.android.getit.netBeanLoader.baseNetBean.INetBean;

/**
 * 资源加载的类
 * @author gengchenghai
 *
 */
public interface IBeanLoader{
	public static final int 	DOWNLOAD_TYPE_APP = 1;
	
	public static final int 	LOAD_SUCCESS = 1 ;
	public static final int 	LOAD_FAIL = 2 ;
	
	/**
	 * 从缓存中加载
	 * @param bean 	要加载的netbean 
	 * @param tag	不同的bean对应的不同的tag 在加载成功的回调中会传回去
	 */
	public void loadCache(INetBean bean, int tag) ;
	
	/**
	 * 从服务器加载
	 * @param bean 	要加载的netbean 
	 * @param tag	不同的bean对应的不同的tag 在加载成功的回调中会传回去
	 */
	public void loadHttp(INetBean bean, int tag) ;


	/**
	 * 普通加载 会分别调用 {@link IBeanLoader#loadCache(INetBean)} {@link IBeanLoader#loadHttp(INetBean)}
	 * @param bean
	 */
	public void loadData(INetBean bean, int tag) ;
	
	/**
	 * 设置加载完成的回调
	 * @param callback 加载完成的回调
	 */
	public void setCallback(ILoadCallback callback) ;
	
	/**
	 * 加载更多资源
	 * @param fileId 资源对应的ID
	 */
	public void loadMore(int tags) ;
	
	public interface ILoadCallback<T>{
		
		/**
		 * 缓存加载成功
		 * @param tag  在调用加载的时候穿入的tag
		 * {@link IBeanLoader#loadData(INetBean, int)}
		 * {@link IBeanLoader#loadHttp(INetBean, int)}
		 * {@link IBeanLoader#loadCache(INetBean, int)}
		 */
		void onCacheComplete(int tag, T result) ;
		
		/**
		 * @param result 加载结果  成功或者失败
		 * @see #LOAD_SUCCESS
		 * @see #LOAD_FAIL
		 * @param tag
		 * {@link IBeanLoader#loadData(INetBean, int)}
		 * {@link IBeanLoader#loadHttp(INetBean, int)}
		 * {@link IBeanLoader#loadCache(INetBean, int)}
		 */
		void onHttpComplete(int resultCode, int tag, T result) ;
		
		/**
		 * 当内容发生改变的时候
		 * 通知界面刷新 
		 * @param tag 
		 * {@link IBeanLoader#loadData(INetBean, int)}
		 * {@link IBeanLoader#loadHttp(INetBean, int)}
		 * {@link IBeanLoader#loadCache(INetBean, int)}
		 */
		void onContentChange(int tag) ;
	}
	
}


