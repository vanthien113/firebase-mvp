package com.example.thienpro.mvp_firebase.model;

import android.net.Uri;

import com.example.thienpro.mvp_firebase.model.entity.Post;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

/**
 * Created by ThienPro on 11/28/2017.
 */

public interface PostInteractor {
    interface PostCallback {
        void postListener(Exception e);
    }

    interface ListPostCallback {
        void listPost(DatabaseError e, ArrayList<Post> listPost);
    }

    interface LoadPersonalPostCallback {
        void post(DatabaseError e, ArrayList<Post> listPost);
    }

    interface DeletePostCallback {
        void listPost(Exception e);
    }

    interface EditPostCallback {
        void editPost(Exception e);
    }

    interface GetPictureCallback {
        void getPicture(DatabaseError e, ArrayList<String> listPicture);
    }

    interface FriendPostCallback {
        void friendPost(DatabaseError e, ArrayList<Post> post);
    }

    void writeNewPost(String content, Uri filePath, PostCallback callback);

    void loadPersonalPost(LoadPersonalPostCallback callback);

    void loadAllPost(ListPostCallback callback);

    void deletePost(Post post, DeletePostCallback callback);

    void editPost(Post post, EditPostCallback callback);

    void getPicture(String userId, GetPictureCallback callback);

    void getFriendPost(String userId, FriendPostCallback callback);
}
