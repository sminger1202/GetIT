package com.android.getit.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.getit.EMChatModule.ChatHelper;
import com.android.getit.EMChatModule.ChaterCallback;
import com.android.getit.R;
import com.android.getit.UserModule.UserHelper;
import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EaseMobException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by sminger on 2015/11/16.
 */
public class FragmentRegister extends BaseFragment {

    @InjectView(R.id.username)        EditText userNameEditText;
    @InjectView(R.id.password)        EditText passwordEditText;
    @InjectView(R.id.confirm_password)EditText confirmPwdEditText;
    @InjectView(R.id.register)        Button register;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @OnClick(R.id.register)
    public void register() {
        final String username = userNameEditText.getText().toString().trim();
        final String pwd = passwordEditText.getText().toString().trim();
        String confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return;
        } else if (!pwd.equals(confirm_pwd)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return;
        }

        UserHelper.getInstance().userRegister(getActivity(), username, pwd, new ChaterCallback() {
            @Override
            public void onSuccess() {
                //如果注册成功，那么进入正式页
                if (mCallBack != null) {
                    mCallBack.onNavigationFragmentSelected(R.id.nav_home);
                }
            }
        });

    }
}
