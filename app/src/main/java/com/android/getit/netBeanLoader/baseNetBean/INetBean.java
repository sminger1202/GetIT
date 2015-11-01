package com.android.getit.netBeanLoader.baseNetBean;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 * 每个页面打开时请求服务器的业务接口
 * @author gengchenghai
 *
 */
public interface INetBean{
	public static final String HTTPGET = "GET" ;
	public static final String HTTPPOST = "POST" ;

	public static final String OFFICIAL_USER_HOST_TEST = "http://10.155.6.17:8000"; // "http://test1.api.3g.tudou.com"  ;
	public static final String OFFICIAL_USER_HOST = "http://api.3g.tudou.com"  ;
	public static String OFFICIAL_TUDOU_USER_HOST = "http://user.api.3g.tudou.com";
	public static final String OFFICIAL_REC_API = "http://rec.api.3g.tudou.com";

	public static final String Url  = "&" ;


	/**
	 * 新版本 防止盗链
	 */
	public static final String NEWSECRET = "6b72db72a6639e1d5a2488ed485192f6";
	/**
	 * 本地时间与服务器时间戳 单位 秒
	 */
	public static String OFFICIAL_PLAY_HOST = "http://play.api.3g.tudou.com";
	public static final String SECRET_TYPE = "md5";

	public static String PL_VIDEOLIST_V4 = "/v4/album/videos";
	public static String PL_VIDEOLIST_UGC_V4 = "/v4/playlist/videos";

	public static final String USER_VIDEO_COMMENTS = "/v5/item_comments";




	/**
	 * 获取服务器接口URL
	 * @return String 返回加载json的Url
	 */
	String getUrl() ;
	
	/**
	 * 业务转换成Json
	 * @return 返回原始json 数据
	 */
	String toJson() ;

	
	/**
	 * @param json 从服务器或者缓存中取到的 json
	 * @return true 解析成功  false 解析失败
	 * 此方法肯能被多个线程同时调用记得左同步
	 */
	boolean parseJson(String json, boolean bLoadMore) ;
	
	/**
	 * 获取缓存数据存在的路径
	 * @return 返回存在的路径 
	 * 返回null则不需要缓存
	 */
	String getCachePath() ;
	
	/**
	 * 获取上传参数
	 * @return
	 */
	List<BasicNameValuePair> getParams() ;
	
	/**
	 * 需要加载更得的业务集成   不需要的不用管
	 * 获取上传参数
	 * @return
	 */
	List<BasicNameValuePair> getParamsNeedMore() ;

	/**
	 * 获取http请求方法  GET OR POST
	 * @return
	 */
	String getHttpMethod() ;


	/**
	 * return request result
	 * @return
	 */
	Object getResult() ;

	/**
	 * return whether to add cookie
	 * @return
	 */
	boolean addCookie();

	boolean getCookie();

}
