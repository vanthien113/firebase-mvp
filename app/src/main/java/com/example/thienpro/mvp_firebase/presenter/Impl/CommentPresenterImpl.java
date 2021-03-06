package com.example.thienpro.mvp_firebase.presenter.Impl;

import com.example.thienpro.mvp_firebase.bases.BasePresentermpl;
import com.example.thienpro.mvp_firebase.manager.UserManager;
import com.example.thienpro.mvp_firebase.model.CommentInteractor;
import com.example.thienpro.mvp_firebase.model.entity.Comment;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.presenter.CommentPresenter;
import com.example.thienpro.mvp_firebase.ultils.SHDateTimeFormat;
import com.example.thienpro.mvp_firebase.view.CommentView;

public class CommentPresenterImpl extends BasePresentermpl<CommentView> implements CommentPresenter {
    private CommentInteractor commentInteractor;
    private UserManager userManager;

    public CommentPresenterImpl(CommentInteractor commentInteractor, UserManager userManager) {
        this.commentInteractor = commentInteractor;
        this.userManager = userManager;
    }

    @Override
    public void writeComment(String content, String postTime) {
        commentInteractor.writeComment(userManager.getUser().getId(), content, SHDateTimeFormat.getPostCurrentTime(), postTime, e -> {
            if (e != null) {
                getView().showExceptionError(e);
            }
        });
    }

    @Override
    public void getComment(String postTime) {
        commentInteractor.getComment(postTime, (e, comments) -> {
            if (e != null) {
                getView().showDatabaseError(e);
            } else {
                for (Comment comment : comments) {
                    User user = userManager.searchUser(comment.getUserId());
                    comment.setUserName(user.getName());
                    comment.setUserAvatar(user.getAvatar());
                }
                getView().showComments(comments);
                getView().clearComment();
            }
        });
    }

    @Override
    public void deleteComment(Comment comment, String postTime) {
        commentInteractor.deleteComment(comment.getCommentTime(), postTime, e -> {
            if (e != null) {
                getView().showExceptionError(e);
            } else {
                getView().showDeleteCommentMessage();
            }
        });
    }
}
