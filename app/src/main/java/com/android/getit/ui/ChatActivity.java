package com.android.getit.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;

import com.android.getit.EMChatModule.ChatHelper;
import com.android.getit.R;
import com.android.getit.fragment.FragmentChat;
import com.easemob.easeui.EaseConstant;

/**
 * Created by sminger on 2015/12/16.
 */
public class ChatActivity extends FragmentActivity {
    private FragmentManager mFragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFragmentManager = getSupportFragmentManager();
        setContentView(R.layout.chatlayout);
        FragmentChat fragmentChat= new FragmentChat();
        Bundle bundle = new Bundle();
        bundle.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        bundle.putCharSequence(EaseConstant.EXTRA_USER_ID, ChatHelper.serviceID);
        fragmentChat.setArguments(bundle);
        mFragmentManager.beginTransaction()
                .replace(R.id.container, fragmentChat)
                .commit();
    }
}
