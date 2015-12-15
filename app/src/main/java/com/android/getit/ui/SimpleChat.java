package com.android.getit.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.getit.R;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.google.android.gms.plus.model.people.Person;

import java.util.zip.Inflater;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sminger on 2015/11/18.
 */
public class SimpleChat extends Activity {

    @InjectView (R.id.chatlist) ListView mListView;
    @InjectView(R.id.inputText) EditText mInputText;
    @InjectView(R.id.btn_send)  Button mSendButton;
    @InjectView(R.id.name)    EditText mUsernameEditText;

    private EMConversation mConversation;
    private String username = "123";
    private MyAdapter mMyAdapter;
    private NewMessageBroadcastReceiver msgReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = this.getLayoutInflater().inflate(R.layout.simple_chart, null);
        ButterKnife.inject(this, rootView);
        setContentView(rootView);
//        mConversation = EMChatManager.getInstance().getConversation(username);
//        mMyAdapter = new MyAdapter();
//        mListView.setAdapter(mMyAdapter);
//        registerBroadcast();
    }

    public void registerBroadcast() {
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
        EMChat.getInstance().setAppInited();
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 注销广播
            abortBroadcast();

            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation	conversation = EMChatManager.getInstance().getConversation(username);
            // 如果是群聊消息，获取到group id
            if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(username)) {
                // 消息不是发给当前会话，return
                return;
            }
            conversation.addMessage(message);
            mMyAdapter.notifyDataSetChanged();
            mListView.setSelection(mMyAdapter.getCount() - 1);

        }
    }

    @OnClick(R.id.btn_confirm)
    public void confirm() {
        username = mUsernameEditText.getText().toString();
        mConversation = EMChatManager.getInstance().getConversation(username);
        registerBroadcast();
        mMyAdapter = new MyAdapter();
        mListView.setAdapter(mMyAdapter);
        Toast.makeText(getApplicationContext(),"confirm the contact name is : " + username, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.btn_send)
    public void sendTxt(View view) {
        Toast.makeText(getApplicationContext(),"send now !", Toast.LENGTH_LONG).show();
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(username);
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //如果是群聊，设置chattype,默认是单聊
       // message.setChatType(EMMessage.ChatType.GroupChat);
        //设置消息body
        TextMessageBody txtBody = new TextMessageBody(mInputText.getText().toString());
        message.addBody(txtBody);
        //设置接收人
        message.setReceipt(username);
        //把消息加入到此会话对象中
        conversation.addMessage(message);
        //发送消息
        EMChatManager.getInstance().sendMessage(message, new EMCallBack(){
            @Override
            public void onError(int i, String s) {
                final String t = s;
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       Toast.makeText(getApplicationContext(), "发送失败！" + t, Toast.LENGTH_SHORT);
                   }
               });

            }

            @Override
            public void onProgress(int i, String s) {

            }

            @Override
            public void onSuccess() {
                Log.d("sss", "发送成功！");

            }
        });
        mMyAdapter.notifyDataSetChanged();
        mListView.setSelection(mMyAdapter.getCount() - 1);
        mInputText.setText("");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (msgReceiver != null) {
            unregisterReceiver(msgReceiver);
        }
    }

    public class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return mConversation.getAllMsgCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            EMMessage smg = mConversation.getMessage(position);
            TextMessageBody tbody = (TextMessageBody)smg.getBody();
            if(smg.direct == EMMessage.Direct.RECEIVE) {
                if(smg.getType() == EMMessage.Type.TXT) {
                    convertView =getLayoutInflater().inflate(R.layout.him, null);
                    TextView textView = (TextView)convertView.findViewById(R.id.him);
                    textView.setText(tbody.getMessage());
                }

            } else {
                if (smg.getType() == EMMessage.Type.TXT){
                    convertView =getLayoutInflater().inflate(R.layout.myself, null);
                    TextView textView = (TextView)convertView.findViewById(R.id.myself);
                    textView.setText(tbody.getMessage());

                }
            }
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return mConversation.getAllMessages().get(position);
        }
    }

}
