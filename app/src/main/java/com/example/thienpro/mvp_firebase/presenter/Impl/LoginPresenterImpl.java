package com.example.thienpro.mvp_firebase.presenter.Impl;

import com.example.thienpro.mvp_firebase.bases.BasePresentermpl;
import com.example.thienpro.mvp_firebase.manager.UserManager;
import com.example.thienpro.mvp_firebase.model.UserInteractor;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.presenter.LoginPresenter;
import com.example.thienpro.mvp_firebase.view.LoginView;
import com.google.firebase.database.DatabaseError;

/**
 * Created by ThienPro on 11/21/2017.
 */

public class LoginPresenterImpl extends BasePresentermpl<LoginView> implements LoginPresenter {
    private UserInteractor userInteractor;
    private UserManager userManager;

    public LoginPresenterImpl(UserInteractor userInteractor, UserManager userManager) {
        this.userInteractor = userInteractor;
        this.userManager = userManager;
    }

    public void signedInCheck() {
        userInteractor.signedInCheck(new UserInteractor.BooleanCheckCallback() {
            @Override
            public void onFinish(boolean checker) {
                if (getView() == null)
                    return;
                getView().hideLoadingDialog();
                if (checker) {
                    getView().navigationToHome();
                } else {
                    getView().navigationToVerifiEmail();
                }
            }
        });
    }

    public void onSignIn(String email, String password) {
        if (getView() == null)
            return;
        getView().showLoadingDialog();

        userInteractor.sigIn(email, password, new UserInteractor.ExceptionCheckCallback() {
            @Override
            public void onFinish(Exception e) {
                if (getView() == null)
                    return;
                getView().hideLoadingDialog();
                if (e == null) {
                    getUserInfo();
                } else {
                    getView().showExceptionError(e);
                }
            }
        });
    }

    @Override
    public void forgotPassword(final String email) {
        if (getView() == null)
            return;
        getView().showLoadingDialog();

        userInteractor.forgotPassword(email, new UserInteractor.ExceptionCheckCallback() {
            @Override
            public void onFinish(Exception e) {
                if (getView() == null)
                    return;
                getView().hideLoadingDialog();
                if (e != null) {
                    getView().showExceptionError(e);
                } else {
                    getView().showLoginMessage(email);
                }
            }
        });
    }

    @Override
    public void getUserInfo() {
        if (getView() == null)
            return;
        getView().showLoadingDialog();

        userInteractor.getUser(new UserInteractor.UserCallback() {
            @Override
            public void onFinish(DatabaseError error, User user) {
                if (getView() == null)
                    return;
                getView().hideLoadingDialog();
                if (error != null) {
                    getView().showDatabaseError(error);
                } else {
                    userManager.updateCurrentUser(user);
                    getView().navigationToHome();
                }
            }
        }, false);
    }

    @Override
    public void attachView(LoginView view) {
        super.attachView(view);
    }

    @Override
    public void detach() {
        super.detach();
    }
}
