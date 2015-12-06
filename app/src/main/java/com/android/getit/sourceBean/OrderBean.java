package com.android.getit.sourceBean;

import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.android.getit.dataRequestResult.OrderListResult;
import com.android.getit.netBeanLoader.baseNetBean.BaseNetBean;

/**
 * Created by sminger on 2015/12/5.
 */
public class OrderBean extends BaseNetBean {
    @Override
    public String getUrl() {

        String url = HOST + getSuffix() + getBaseUrl() ;

        return url;
    }

    @Override
    public String getHttpMethod() {
        return HTTPGET;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public boolean parseJson(String json, boolean bLoadMore) {
        if(TextUtils.isEmpty(json)){
            return  false;
        }
        result = JSON.parseObject(json, OrderListResult.class) ;
        return true;
    }

    @Override
    public String getCachePath() {
        return null;
    }

    @Override
    public String getSuffix() {
        return "/allInfo.json";
    }

}
