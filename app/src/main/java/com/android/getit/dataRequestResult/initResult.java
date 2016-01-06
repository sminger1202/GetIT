package com.android.getit.dataRequestResult;

/**
 * Created by sminger on 2016/1/6.
 */

//        "giSwitch":{
//        "NewVersion":"1.0",
//        "AutoLogin":"true",
//        "ApkAddress":"http://dl.m.tudou.com/android/phone/Tudou_Phone_tudouweb.apk"
//        },
//        "status":0,
//        "errMsg":null
public class InitResult {
    public Switch giSwitch;
    public String status;
    public String errMsg;

    public class Switch{
        public String NewVersion;
        public String AutoLogin;
        public String ApkAddress;
    }

    @Override
    public String toString() {
        return "status:" + status + ".  errMsg :" + errMsg + ".  giSwitch:" +
                giSwitch.NewVersion + giSwitch.AutoLogin + giSwitch.ApkAddress;
    }
}
