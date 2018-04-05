package com.example.thienpro.mvp_firebase.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.thienpro.mvp_firebase.R;
import com.example.thienpro.mvp_firebase.databinding.FragmentSettingBinding;
import com.example.thienpro.mvp_firebase.presenter.Impl.SettingPresenterImpl;
import com.example.thienpro.mvp_firebase.presenter.SettingPresenter;
import com.example.thienpro.mvp_firebase.view.SettingView;
import com.example.thienpro.mvp_firebase.view.bases.BaseFragment;
import com.example.thienpro.mvp_firebase.view.listener.HomeNavigationListener;

/**
 * Created by ThienPro on 11/22/2017.
 */

public class SettingFragment extends BaseFragment<FragmentSettingBinding> implements SettingView {
    private SettingPresenter presenter;
    private HomeNavigationListener navigationListener;

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void init(@Nullable View view) {
        getBinding().setEvent(this);

        presenter = new SettingPresenterImpl(getContext());
        presenter.attachView(this);
    }

    @Override
    public void onEditInfoClick() {
        navigationListener.navigationToEditInfoActivity();
    }

    @Override
    public void onLogout() {
        presenter.logOut();
    }

    @Override
    public void navigationToLogin() {
        navigationListener.navigationToLoginActivity();
    }

    @Override
    public void onAppSettingClick() {
        navigationListener.navigationToAppSettingActivity();
    }

    @Override
    public void onChangePasswordClick() {
        navigationListener.navigationToChangePasswordActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void attach(Context context) {
        if (context instanceof HomeNavigationListener) {
            navigationListener = (HomeNavigationListener) context;
        }
    }

    @Override
    protected void screenResume() {

    }

    @Override
    protected void screenPause() {

    }

    @Override
    protected void screenStart(@Nullable Bundle saveInstanceState) {

    }
}
