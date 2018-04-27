package com.example.thienpro.mvp_firebase.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.thienpro.mvp_firebase.databinding.ItemActivityHomeBinding;
import com.example.thienpro.mvp_firebase.databinding.ItemProfileHeaderBinding;
import com.example.thienpro.mvp_firebase.model.entity.Post;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.ultils.LayoutUltils;
import com.example.thienpro.mvp_firebase.view.adapters.viewholder.HomeVH;
import com.example.thienpro.mvp_firebase.view.adapters.viewholder.ItemProfileHeaderVH;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HEADER = 0;
    private final int POST = 1;

    private HomeAdapter.ListPostMenuListener postMenuListeneristener;
    private User user;
    private ArrayList<Post> lisPost;
    private ItemProfileClickListener listener;

    public ProfileAdapter(ArrayList<Post> mLisPost, User user, ItemProfileClickListener listener, HomeAdapter.ListPostMenuListener postMenuListeneristener) {
        this.lisPost = mLisPost;
        this.user = user;
        this.listener = listener;
        this.postMenuListeneristener = postMenuListeneristener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            ItemProfileHeaderBinding binding = ItemProfileHeaderBinding.inflate(LayoutInflater.from(parent.getContext()));
            binding.getRoot().setLayoutParams(LayoutUltils.getRecyclerViewLayoutParams());
            return new ItemProfileHeaderVH(binding, listener);
        } else if (viewType == POST) {
            ItemActivityHomeBinding binding = ItemActivityHomeBinding.inflate(LayoutInflater.from(parent.getContext()));
            binding.getRoot().setLayoutParams(LayoutUltils.getRecyclerViewLayoutParams());
            return new HomeVH(binding, postMenuListeneristener, null);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemProfileHeaderVH) {
            ((ItemProfileHeaderVH) holder).bind(user);
        } else if (holder instanceof HomeVH) {
            ((HomeVH) holder).bind(lisPost.get(position - 1));
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case HEADER:
                return HEADER;
            default:
                return POST;
        }
    }

    @Override
    public int getItemCount() {
        return lisPost == null ? 1 : lisPost.size() + 1;
    }

    public interface ItemProfileClickListener {
        void onChangeAvatar();

        void onChangeCover();

        void onShowListPictureClick();

        void onPost();

        void onDeletePost(Post post);

        void onEditPost(Post post);

        void onAvatarClick(String avatar);

        void onCoverClick(String cover);
    }
}
