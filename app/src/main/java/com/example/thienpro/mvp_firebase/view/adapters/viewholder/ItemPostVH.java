package com.example.thienpro.mvp_firebase.view.adapters.viewholder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.example.thienpro.mvp_firebase.R;
import com.example.thienpro.mvp_firebase.databinding.ItemPostBinding;
import com.example.thienpro.mvp_firebase.model.entity.Post;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.ultils.SHStringHelper;
import com.example.thienpro.mvp_firebase.ultils.widget.SHBitmapHelper;
import com.example.thienpro.mvp_firebase.view.ItemListPostView;
import com.example.thienpro.mvp_firebase.view.activity.CommentActivity;
import com.example.thienpro.mvp_firebase.view.adapters.HomeAdapter;

/**
 * Created by ThienPro on 11/11/2017.
 */

public class ItemPostVH extends RecyclerView.ViewHolder implements ItemListPostView {
    protected ItemPostBinding binding;
    private PopupMenu popupMenu;
    private HomeAdapter.ListPostMenuListener listener;
    private User user;
    private Post post;

    public ItemPostVH(ItemPostBinding binding, HomeAdapter.ListPostMenuListener listener, User user) {
        super(binding.getRoot());
        this.user = user;
        this.binding = binding;
        this.listener = listener;

        binding.setEvent(this);

        popupMenu = new PopupMenu(binding.getRoot().getContext(), binding.ibMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_list_post, popupMenu.getMenu());

        downloadImageEvent();
    }

    public void bind(Post post) {
        this.post = post;

        SHStringHelper.hashTag(post.getPost(), ContextCompat.getColor(binding.getRoot().getContext(), R.color.colorGreen), binding.tvPost);
        binding.setData(post);

        if (user != null) {
            if (user.getId().equals(post.getId())) {
                binding.ibMenu.setVisibility(View.VISIBLE);
            } else {
                binding.ibMenu.setVisibility(View.INVISIBLE);
            }
        }

        SHBitmapHelper.bindImage(binding.ivImage, post.getImage());
        SHBitmapHelper.bindImage(binding.ivAvatar, post.getAvatar());
    }

    private void downloadImageEvent() {
        binding.ivImage.setOnLongClickListener(view -> {
            new AlertDialog.Builder(binding.getRoot().getContext())
                    .setTitle(R.string.tai_xuong)
                    .setCancelable(false)
                    .setPositiveButton(R.string.tai, (dialogInterface, i) -> listener.onDownload(post.getImage()))
                    .setCancelable(true)
                    .setNegativeButton(R.string.huy, null)
                    .setMessage(R.string.tai_anh_xuong_may_cua_ban)
                    .create()
                    .show();
            return false;
        });
    }

    @Override
    public void onImageClick() {
        listener.onImageClick(post.getImage());
    }

    @Override
    public void onMenuClick(final Post post) {
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.mn_edit_post:
                    editPost(post);
                    break;
                case R.id.mn_delete_post:
                    deletePost(post);
                    break;
            }
            return false;
        });
    }

    @Override
    public void onFriendProfileClick(Post post) {
        listener.onFriendProfile(post.getId());
    }

    @Override
    public void onCommentClick() {
        listener.onCommentClick(binding.getData());
    }

    private void deletePost(final Post post) {
        new AlertDialog.Builder(binding.getRoot().getContext())
                .setTitle(R.string.xoa_bai_viet)
                .setCancelable(false)
                .setPositiveButton(R.string.xoa, (dialogInterface, i) -> listener.onDeletePost(post))
                .setCancelable(true)
                .setNegativeButton(R.string.huy, null)
                .setMessage(R.string.ban_thuc_su_muon_xoa_bai_viet_nay)
                .create()
                .show();
    }

    private void editPost(Post post) {
        listener.onEditPost(post);
    }
}
