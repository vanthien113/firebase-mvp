package com.example.thienpro.mvp_firebase.presenter.Impl;

import com.example.thienpro.mvp_firebase.bases.BasePresentermpl;
import com.example.thienpro.mvp_firebase.model.UserInteractor;
import com.example.thienpro.mvp_firebase.presenter.VerifiEmailPresenter;
import com.example.thienpro.mvp_firebase.view.VerifiEmailView;

/**
 * Created by ThienPro on 11/28/2017.
 */

public class VerifiEmailPresenterImpl extends BasePresentermpl<VerifiEmailView> implements VerifiEmailPresenter {
    private UserInteractor userInteractor;

    public VerifiEmailPresenterImpl(UserInteractor userInteractor) {
        this.userInteractor = userInteractor;
    }

    @Override
    public void verifiEmail() {
        userInteractor.verifiEmail(new UserInteractor.StringCallback() {
            @Override
            public void onFinish(Exception checker, String email) {
                if (getView() == null)
                    return;
                if (checker == null && email == null) {
                    getView().navigationToHome();
                } else if (checker == null && email != null) {
                    getView().sendverifiEmailComplete(email);
                } else {
                    getView().sendverifiEmailFail(email);
                }
            }
        });
    }

    @Override
    public void logOut() {
        userInteractor.logOut();
        getView().navigationToLogin();

    }
}
