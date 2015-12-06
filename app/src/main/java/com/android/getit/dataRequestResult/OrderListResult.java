package com.android.getit.dataRequestResult;

import android.app.Service;

import java.util.ArrayList;

/**
 * Created by sminger on 2015/12/5.
 */

//{
//        "OrderName":"订单名称",
//        "OrderTime":"2015\/12\/5 13:00:28",
//        "Services":[{"ServiceCount"      :1,
//        "ServiceDescription":"只是安装服务，不提供安装盘",
//        "ServiceID"         :1,
//        "ServiceTypeID"     :1,
//        "ServiceTypeName"   :"自营"}，
//        {"ServiceCount"      :2,
//        "ServiceDescription":"提供全套配置服务",
//        "ServiceID"         :2,
//        "ServiceTypeID"     :1,
//        "ServiceTypeName":"自营"},
//        {"ServiceCount"      :3,
//        "ServiceDescription":"自带安装程序",
//        "ServiceID"         :3,
//        "ServiceTypeID"     :2,
//        "ServiceTypeName":"第三方"}],
//        "TotalPrice":370,
//        "UserID":2,
//        "UserName":"wang"
//        }
public class OrderListResult {

    public String OrderName; //:"订单名称",
    public String OrderTime;//:"2015\/12\/5 13:00:28",
    public ArrayList<Service> Services;
    public float TotalPrice; //":370,
    public long UserID;      //:2,
    public String UserName;  //:"wang"

    public static class Service {
        int ServiceCount;         //     :1,
        String ServiceDescription;//     :"只是安装服务，不提供安装盘",
        int ServiceID;            //     :1,
        int ServiceTypeID;        //     :1,
        String ServiceTypeName;   //     :"自营"}，
    }
}
