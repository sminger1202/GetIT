package com.android.getit.netBeanLoader.Item;

import org.json.JSONObject;

/**
 * INetBean 中的item @see INetBean
 * @author gengchenghai
 *
 */
public interface IItem {


	/**
	 * 转换为json
	 * @return
	 * 显示类型对应的Json
	 */
	public JSONObject toJson() ;

	/**
	 * 解析单个 Item 
	 * @param 	obj
	 * @return	解析成功返回true  否则返回false
	 */
	public boolean parseJson(JSONObject obj) ;

}
