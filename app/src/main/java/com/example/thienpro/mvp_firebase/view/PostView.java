package com.example.thienpro.mvp_firebase.view;

/**
 * Created by ThienPro on 11/28/2017.
 */

public interface PostView {
    void onBackClick();

    void onPostClick();

    void onPostFail(Exception e);

    void navigationToHome();

    void showLoading();

    void hideLoading();
}
