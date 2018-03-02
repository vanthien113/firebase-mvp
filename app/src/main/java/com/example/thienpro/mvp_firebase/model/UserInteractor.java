package com.example.thienpro.mvp_firebase.model;

import android.net.Uri;

import com.example.thienpro.mvp_firebase.model.entity.User;
import com.google.firebase.database.DatabaseError;

/**
 * Created by ThienPro on 11/28/2017.
 */

public interface UserInteractor {
    interface LoggedInCheck {
        void checker(int checker);
    }

    interface LoginCheck {
        void checker(boolean checker, Exception e);
    }

    interface RegisterCheck {
        void checker(Exception checker);
    }

    interface VerifiEmailCheck {
        void checker(Exception checker, String email);
    }

    interface LogoutCheck {
        void checker(boolean checker);
    }

    interface GetUserListener {
        void getUser(DatabaseError error, User user);
    }

    interface AddAvatarListener {
        void addAvatar(Exception e, String uri);
    }

    interface AddCoverListener {
        void addCover(Exception e, String uri);
    }

    interface UpdateUserListener {
        void updateUser(Exception e);
    }

    interface LoadCurrentLocalUserListener {
        void currentLocalUser(User user);
    }

    void sigIn(String email, String password, LoginCheck loginCheck);

    void signedInCheck(LoggedInCheck loginCheck);

    void logOut(LogoutCheck logoutCheck);

    void getUser(GetUserListener listener, boolean loadUser);

    void addAvatar(String email, final String name, String address, Boolean sex, Uri uri, String coverUri, AddAvatarListener addAvatarListener);

    void addCover(final String email, final String name, final String address, final Boolean sex, final String avatar, final Uri coverUri, AddCoverListener addCoverListener);

    void updateUser(String email, String name, String address, Boolean sex, String avatar, String cover, UpdateUserListener updateUserListener);

    void verifiEmail(VerifiEmailCheck verifiEmailCheck);

    void register(final String email, String password, final String name, final String address, final boolean sex, RegisterCheck registerCheck);

    void loadCurrentLocalUser(LoadCurrentLocalUserListener loadCurrentLocalUserListener);

    void saveCurrentLocalUser(User user);
}
