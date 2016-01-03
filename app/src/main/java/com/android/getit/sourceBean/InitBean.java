package com.android.getit.sourceBean;

import com.android.getit.netBeanLoader.baseNetBean.BaseNetBean;

/**
 * Created by sminger on 2015/12/17.
 */
public class InitBean extends BaseNetBean {

    @Override
    public String getSuffix() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Override
    public boolean parseJson(String json, boolean bLoadMore) {
        return false;
    }

    @Override
    public String getCachePath() {
        return null;
    }

    @Override
    public String getHttpMethod() {
        return null;
    }
}
