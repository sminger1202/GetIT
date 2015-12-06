package com.android.getit.dataRequestResult;

import java.util.ArrayList;

public class WebSiteResult {

    public int ver = -1;
    public boolean ret = false;
    public ArrayList<TicketSource> data = null;
/*
    {
        "id":0,
        "name":"大麦网",
        "wrapperId":"damaiwang",
        "homePage":"http://www.damai.cn",
        "cityPage":"http://www.damai.cn/projectlist.do?cityID=852&mcid=3&ccid=19",
        "listPage":"http://www.damai.cn/projectlist.do?cityID={cityid}&mcid={baseTypeId}&ccid={typeId}",
        "comment":"大麦票务网",
        "lastMod":1434905761689
    }
*/
    public class TicketSource {
        int id;
        String name;
        String wrapperId;
        String homePage;
        String cityPage;
        String listPage;
        String comment;
        int lastMod;

    }

}
