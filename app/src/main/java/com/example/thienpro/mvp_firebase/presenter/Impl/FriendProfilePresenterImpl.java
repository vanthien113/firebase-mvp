package com.example.thienpro.mvp_firebase.presenter.Impl;

import android.content.Context;

import com.example.thienpro.mvp_firebase.model.Impl.PostInteractorImpl;
import com.example.thienpro.mvp_firebase.model.Impl.UserInteractorImpl;
import com.example.thienpro.mvp_firebase.model.PostInteractor;
import com.example.thienpro.mvp_firebase.model.UserInteractor;
import com.example.thienpro.mvp_firebase.model.entity.Post;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.presenter.FriendProfilePresenter;
import com.example.thienpro.mvp_firebase.view.FriendProfileView;
import com.example.thienpro.mvp_firebase.view.bases.BasePresentermpl;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class FriendProfilePresenterImpl extends BasePresentermpl<FriendProfileView> implements FriendProfilePresenter {
    private UserInteractor userInteractor;
    private PostInteractor postInteractor;

    public FriendProfilePresenterImpl(Context context) {
        userInteractor = new UserInteractorImpl(context);
        postInteractor = new PostInteractorImpl();
    }

    @Override
    public void getFriendInfomation(String userId) {
        getView().showLoadingDialog();

        userInteractor.getFriendInfomation(userId, new UserInteractor.FriendInfomationCallback() {
            @Override
            public void friendInfomation(DatabaseError e, User user) {
                getView().hideLoadingDialog();
                if (e != null) {
                    getView().showDatabaseError(e);
                } else {
                    getView().showUserInfomation(user);
                }
            }
        });
    }

    @Override
    public void getFriendPost(String userId) {
        getView().showLoadingDialog();
        postInteractor.getFriendPost(userId, new PostInteractor.FriendPostCallback() {
            @Override
            public void friendPost(DatabaseError e, ArrayList<Post> post) {
                getView().hideLoadingDialog();
                if (e != null) {
                    getView().showDatabaseError(e);
                } else {
                    getView().showListPost(post);
                }
            }
        });
    }
}
