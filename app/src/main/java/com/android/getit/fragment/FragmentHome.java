package com.android.getit.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.getit.R;
import com.android.getit.Utils.FrescoLoaderImage;

/**
 * Created by sminger on 2015/11/1.
 */
public class FragmentHome extends BaseFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_page, container, false);
//        FrescoLoaderImage.loaderProgressively();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public static FragmentHome newInstance(int position) {
        FragmentHome mFragmentHome = new FragmentHome();
        Bundle args = new Bundle();
        args.putInt(ARG_SITE_NUMBER, position);
        mFragmentHome.setArguments(args);
        return mFragmentHome;
    }
}
