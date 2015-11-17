package com.android.getit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.getit.MainActivity;

/**
 * Created by sminger on 2015/11/1.
 */
public class BaseFragment extends Fragment {
    public callBack mCallBack = null;
    public interface callBack {
        boolean onNavigationFragmentSelected(int id);

    }
    static final String ARG_SITE_NUMBER = "Number";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainActivity) {
            mCallBack = (MainActivity)activity;
        }

    }

//    public static <T> T newInstance(int position, Class<T> clazz) {
//        return null;
//    }
}
