package com.example.thienpro.mvp_firebase.presenter;

import com.example.thienpro.mvp_firebase.view.PictureView;
import com.example.thienpro.mvp_firebase.view.bases.BasePresenter;

public interface PicturePresenter extends BasePresenter<PictureView> {
    void getPicture();
}
