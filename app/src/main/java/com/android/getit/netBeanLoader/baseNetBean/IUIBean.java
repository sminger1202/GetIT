package com.android.getit.netBeanLoader.baseNetBean;


import com.android.getit.netBeanLoader.Item.IItem;

import java.util.List;

/**
 * 界面对应的业务 
 * @author gengchenghai
 * 
 */
public interface IUIBean {
	/**
	 * 获取所有的内容
	 * @return
	 * {@link #}
	 */
	public List<IItem> getListBean()  ;
	
	/**
	 * 
	 * @return
	 */
	public int 				getListSize() ;
	
}
