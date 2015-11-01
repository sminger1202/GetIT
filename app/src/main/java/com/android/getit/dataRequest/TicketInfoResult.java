package com.android.getit.dataRequest;

import java.util.ArrayList;

public class TicketInfoResult {

    public int ver = -1;
    public boolean ret = false;
    public ArrayList<TicketInfo> data = null;
    /*

    {
        "id":1431,
        "title":"天空之城-久石让•宫崎峻动漫作品视听音乐会",
        "url":"http://www.chinaticket.com/beijing/view/5821.html",
        "note":"",
        "time":"2015.08.15 - 2015.12.12",
        "address":"国图艺术中心，北京音乐厅",
        "headPic":"http://img.piaochong.com/admin/2015/01/06/d5f57cf193d67671_60_85.jpg",
        "price":"100.00 - 380.00",
        "wrapperId":"chinaticket",
        "typeId":"yinlehui",
        "lastMod":null
    }
*/
    public class TicketInfo {
        public int id;
        public String title;
        public String url;
        public String note;
        public String time;
        public String address;
        public String headPic;
        public String price;
        public String wrapperId;
        public String typeId;
        public int lastMod;
    }
}
