package com.android.getit.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.getit.EMChatModule.ChaterCallback;
import com.android.getit.MainActivity;
import com.android.getit.R;
import com.android.getit.UserModule.UserHelper;
import com.android.getit.Utils.Utils;


import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by sminger on 2015/11/16.
 */
public class FragmentLogin extends BaseFragment {

    private static final String TAG = "LoginFragment";
    private EditText usernameEditText;
    private EditText passwordEditText;

    private boolean autoLogin = false;

    private String currentUsername;
    private String currentPassword;
    @InjectView(R.id.login) Button loginButton;
    @InjectView(R.id.register) Button registerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        // 如果登录成功过，直接进入主页面
        if (UserHelper.getInstance().isLogin()) {
            autoLogin = true;
            //TODO 进入正式的app页面。
            return;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login, container, false);
        ButterKnife.inject(this, rootView);
        usernameEditText = (EditText) rootView.findViewById(R.id.username);
        passwordEditText = (EditText) rootView.findViewById(R.id.password);

        // 如果用户名改变，清空密码
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEditText.setText(null);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (UserHelper.getInstance().getCurrentUsernName() != null) {
            usernameEditText.setText(UserHelper.getInstance().getCurrentUsernName());
        }
        return rootView;
    }

    @OnClick(R.id.login)
    public void login() {
        if (!Utils.hasInternet()) {
            Toast.makeText(getActivity(), R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
            return;
        }
        currentUsername = usernameEditText.getText().toString().trim();
        currentPassword = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(currentUsername)) {
            Toast.makeText(getActivity(), R.string.User_name_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            Toast.makeText(getActivity(), R.string.Password_cannot_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        UserHelper.getInstance().userLogin(getActivity(), currentUsername, currentPassword, new ChaterCallback(){
            @Override
            public void onSuccess(){
                //如果登陆成功，那么进入正式页
                UserHelper.getInstance().setState(true);
                if ( mCallBack != null) {
                    mCallBack.onNavigationFragmentSelected(R.id.nav_home);
                }
            }
        });

    }
    @OnClick(R.id.register)
    public void register(){
        if(mCallBack != null) {
            mCallBack.onNavigationFragmentSelected(Utils.REGISTER);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        UserHelper.getInstance().cancelLogin();
    }

}
