package com.android.getit.UserModule;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.getit.EMChatModule.ChatHelper;
import com.android.getit.EMChatModule.ChaterCallback;
import com.android.getit.R;
import com.android.getit.netBeanLoader.IHttpRequest;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

/**
 * Created by sminger on 2015/11/16.
 *   这个类保存了用户的登录信息，也封装了环信账户。
 *   对用户而言，环信的注册和登陆是完全透明的。
 */


public class UserHelper {
    private static UserHelper mInstance = null;
    private static boolean isLogInState = false;
    private static String mUserName = null;
    private static IHttpRequest mLoginRequest;
    public UserHelper(){
    }

    public static UserHelper getInstance() {
        if(mInstance == null) {
            mInstance = new UserHelper();
        }
        return mInstance;
    }

    /*
       app 登录且环信登录，才算真正登录。
     */
    public boolean isLogin(){
        return isLogInState && ChatHelper.getInstance().isLogin();
    }

    public String getCurrentUsernName(){
        return mUserName;
    }


    public void setCurrentUserName(String name) {
        mUserName = name;
    }

    public void setState( boolean state) {
        isLogInState = state;
    }

    /*
       环信注册群组联系人监听
     */
    public void registerGroupAndContactListener() {

    }

    /**
     * app 登录
     * @param context
     * @param currentUsername
     * @param currentPassword
     * @return
     */
    private boolean appLogin(Context context, final String currentUsername, final String currentPassword) {
        // TODO: 2015/11/17 添加 app登陆
        return true;
    }

    /**
     * app 注册
     * @param context
     * @param currentUsername
     * @param currentPassword
     * @return
     */
    private boolean appRegister(Context context, final String currentUsername, final String currentPassword) {
        //// TODO: 2015/11/17 添加app注册

        return true;
    }

    /**
     * 登录
     * @param userName
     * @param pwd
     *
     * 登录包括App登录和环信登录
     */
    public boolean userLogin(Context context, final String currentUsername, final String currentPassword, final ChaterCallback callback) {
        //TODO 还需都要登陆App账户
        isLogInState = appLogin(context, currentUsername, currentPassword) &&
                ChatHelper.getInstance().Login(context, currentUsername, currentPassword, callback);
        return isLogInState;
    }

    /**
     * 注册
     * @param context
     * @param currentUsername
     * @param currentPassword
     * @param callback
     * @return
     */
    public boolean userRegister(Context context, final String currentUsername, final String currentPassword, final ChaterCallback callback) {
        //TODO注册环信，还需要注册App账户。
        isLogInState = appRegister(context, currentUsername, currentPassword) &&
                ChatHelper.getInstance().register(context, currentUsername, currentPassword, callback);
        return isLogInState;
    }
    public void cancelLogin() {
//        if(mLoginRequest != null) {
//            Log.d("GetIT","终止登陆！");
//        }
//        mLoginRequest.cancel();
//        mLoginRequest = null;
    }

    public void logout() {
        // TODO: 2015/11/18 app 添加app退出
        ChatHelper.getInstance().logout();
    }

}
