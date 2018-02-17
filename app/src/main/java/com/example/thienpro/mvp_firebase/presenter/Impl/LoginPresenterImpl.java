package com.example.thienpro.mvp_firebase.presenter.Impl;

import com.example.thienpro.mvp_firebase.model.UserInteractor;
import com.example.thienpro.mvp_firebase.model.Impl.UserInteractorImpl;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.presenter.LoginPresenter;
import com.example.thienpro.mvp_firebase.view.LoginView;

/**
 * Created by ThienPro on 11/21/2017.
 */

public class LoginPresenterImpl implements UserInteractor.userListener, LoginPresenter {
    private UserInteractor userInteractor;
    private LoginView loginView;

    public LoginPresenterImpl(LoginView loginView) {
        this.userInteractor = new UserInteractorImpl(this);
        this.loginView = loginView;
    }

    public void signedInCheck(){
        if(userInteractor.signedInCheck()==1)
            loginView.navigationToHome();
        else if(userInteractor.signedInCheck()==2)
            loginView.navigationToVerifiEmail();
    }

    public void onSignIn(String email, String password){
        userInteractor.sigIn(email, password);
    }

    @Override
    public void sendVerifiEmailComplete(String email) {

    }

    @Override
    public void sendVerifiEmailFail(String email) {

    }

    @Override
    public void getUser(User user) {

    }

    @Override
    public void navigationToHome() {
        loginView.navigationToHome();
    }

    @Override
    public void navigationToLogin() {
        loginView.navigationToLogin();
    }

    @Override
    public void onRegisterFail() {
    }

    @Override
    public void onLoginFail() {
        loginView.onLoginFail();
    }

    @Override
    public void navigationToVerifiEmail() {
        loginView.navigationToVerifiEmail();
    }
}
