package com.example.thienpro.mvp_firebase.view;

/**
 * Created by ThienPro on 11/10/2017.
 */

public interface RegisterView {
    void navigationToVerifiEmail();

    void onNextClick();

    void onRegisterFail(Exception e);

    void onBackClick();
}
