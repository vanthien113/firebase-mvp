package com.example.thienpro.mvp_firebase.presenter.Impl;

import com.example.thienpro.mvp_firebase.model.Impl.UserInteractorImpl;
import com.example.thienpro.mvp_firebase.model.UserInteractor;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.presenter.VerifiEmailPresenter;
import com.example.thienpro.mvp_firebase.view.VerifiEmailView;

/**
 * Created by ThienPro on 11/28/2017.
 */

public class VerifiEmailPresenterImpl implements VerifiEmailPresenter, UserInteractor.userListener {
    private UserInteractor userInteractor;
    private VerifiEmailView verifiEmailView;

    public VerifiEmailPresenterImpl(VerifiEmailView verifiEmailView) {
        this.userInteractor = new UserInteractorImpl(this);
        this.verifiEmailView = verifiEmailView;
    }

    @Override
    public void verifiEmail() {
        userInteractor.verifiEmail();
    }

    @Override
    public void logOut() {
        userInteractor.logOut();
    }

    @Override
    public void sendVerifiEmailComplete(String email) {
        verifiEmailView.sendverifiEmailComplete(email);
    }

    @Override
    public void sendVerifiEmailFail(String email) {
        verifiEmailView.sendverifiEmailFail(email);
    }

    @Override
    public void getUser(User user) {

    }

    @Override
    public void navigationToHome() {
        verifiEmailView.navigationToHome();
    }

    @Override
    public void navigationToLogin() {

    }

    @Override
    public void onRegisterFail() {

    }

    @Override
    public void onLoginFail() {

    }

    @Override
    public void navigationToVerifiEmail() {

    }
}
