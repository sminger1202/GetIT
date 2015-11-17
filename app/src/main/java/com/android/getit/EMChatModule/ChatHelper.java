package com.android.getit.EMChatModule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.getit.R;
import com.android.getit.UserModule.UserHelper;
import com.android.getit.fragment.FragmentLogin;
import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.easeui.controller.EaseUI;
import com.easemob.chat.EMChat;
import com.easemob.exceptions.EaseMobException;

/**
 * Created by sminger on 2015/11/16.
 * 这个类是用来封装环信的，在工程中作为即时通讯的模块
 */
public class ChatHelper {

    private EaseUI mEaseUI = null;

    private Context mAppContext = null;

    private static ChatHelper mChatHelper = null;

    private boolean isGroupAndContactListenerRegisted;
    private boolean isSyncingGroupsWithServer = false;
    private boolean isSyncingContactsWithServer = false;
    private boolean isSyncingBlackListWithServer = false;
    private boolean isGroupsSyncedWithServer = false;
    private boolean isContactsSyncedWithServer = false;
    private boolean isBlackListSyncedWithServer = false;

    private ChatHelper(){

    }

    public synchronized static ChatHelper getInstance() {
        if (mChatHelper == null) {
            mChatHelper = new ChatHelper();
        }
        return mChatHelper;
    }

    /**
     * init helper
     *
     * @param context application context
     *
     */
    public void init(Context context) {
        if (EaseUI.getInstance().init(context)) {
            mAppContext = context;

//            EMChat.getInstance().init(mAppContext);// 在EaseUI中已经对EMChat初始化了。

            //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMChat.getInstance().setDebugMode(true);
            //get easeui instance
            mEaseUI = EaseUI.getInstance();
//            //调用easeui的api设置providers
//            setEaseUIProviders();
//            demoModel = new DemoModel(context);
//            //设置chat options
//            setChatoptions();
//            //初始化PreferenceManager
//            PreferenceManager.init(context);
//            //初始化用户管理类
//            getUserProfileManager().init(context);
//
//            //设置全局监听
//            setGlobalListeners();
//            broadcastManager = LocalBroadcastManager.getInstance(appContext);
//            initDbDao();
        }
    }

    public boolean isLogin() {
        return EMChat.getInstance().isLoggedIn();
    }

    /**
     * 环信登录
     * @param context
     * @param currentUsername
     * @param currentPassword
     * @return
     */
    public boolean Login(final Context context, final String currentUsername, final String currentPassword, final ChaterCallback callback) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.Login_cancel),
                        Toast.LENGTH_SHORT).show();
            }
        });
        pd.setMessage(context.getString(R.string.Is_landing));
        pd.show();

        EMChatManager.getInstance().login(currentUsername, currentPassword, new EMCallBack() {

            @Override
            public void onSuccess() {
                // ** 第一次登录或者之前logout后再登录，加载所有本地群和回话
                // ** manually load all local groups and
                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();
                //TODO 登陆成功后，应异步获取用户昵称和头像从自己的服务器上。

                if (pd.isShowing()) {
                    pd.dismiss();
                }
                callback.onSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return EMChat.getInstance().isLoggedIn();
    }

    public boolean register(final Context context, final String username, final String pwd, final ChaterCallback callBack) {
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            final ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage(context.getResources().getString(R.string.Is_the_registered));
            pd.show();

            new Thread(new Runnable() {
                public void run() {
                    try {
                        // 调用sdk注册方法
                        EMChatManager.getInstance().createAccountOnServer(username, pwd);
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                if (!((Activity) context).isFinishing())
                                    pd.dismiss();
                                // 保存用户名
                                UserHelper.getInstance().setCurrentUserName(username);
                                Toast.makeText(((Activity) context).getApplicationContext(),
                                        ((Activity) context).getResources().getString(R.string.Registered_successfully), 0).show();
                                callBack.onSuccess();
                            }
                        });
                    } catch (final EaseMobException e) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            public void run() {
                                if (!((Activity) context).isFinishing())
                                    pd.dismiss();
                                int errorCode = e.getErrorCode();
                                if (errorCode == EMError.NONETWORK_ERROR) {
                                    Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.USER_ALREADY_EXISTS) {
                                    Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.User_already_exists), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.UNAUTHORIZED) {
                                    Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                } else if (errorCode == EMError.ILLEGAL_USER_NAME) {
                                    Toast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context.getApplicationContext(), ((Activity) context).getResources().getString(R.string.Registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }).start();

        }
        return  EMChat.getInstance().isLoggedIn();
    }

    synchronized void reset(){
        //TODO 应该释放相应的资源
    }

}
