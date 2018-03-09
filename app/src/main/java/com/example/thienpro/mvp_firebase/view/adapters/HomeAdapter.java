package com.example.thienpro.mvp_firebase.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.thienpro.mvp_firebase.databinding.ItemActivityHomeBinding;
import com.example.thienpro.mvp_firebase.model.entity.Post;
import com.example.thienpro.mvp_firebase.model.entity.User;
import com.example.thienpro.mvp_firebase.view.adapters.viewholder.HomeVH;

import java.util.ArrayList;

/**
 * Created by ThienPro on 11/11/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeVH> {
    private ArrayList<Post> mLisPost;
    private Context context;
    private ListPostMenuListener listener;
    private User user;
    private DownloadImageListener downloadImageListener;

    public HomeAdapter(ArrayList<Post> mLisPost, Context context, ListPostMenuListener listener, User user, DownloadImageListener downloadImageListener) {
        this.mLisPost = mLisPost;
        this.context = context;
        this.listener = listener;
        this.user = user;
        this.downloadImageListener = downloadImageListener;
    }

    @Override
    public HomeVH onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ItemActivityHomeBinding binding = ItemActivityHomeBinding.inflate(LayoutInflater.from(parent.getContext()));
        binding.getRoot().setLayoutParams(layoutParams);
        return new HomeVH(binding, listener, user, downloadImageListener);
    }

    @Override
    public void onBindViewHolder(HomeVH holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        animation.setDuration(1000);
        holder.itemView.startAnimation(animation);

        holder.bind(mLisPost.get(position));
    }

    @Override
    public int getItemCount() {
        return mLisPost == null ? 0 : mLisPost.size();
    }

    public interface ListPostMenuListener {
        void onEditPost(Post post);

        void onDeletePost(Post post);
    }

    public interface DownloadImageListener {
        void onDownload(String imageUrl);
    }
}
