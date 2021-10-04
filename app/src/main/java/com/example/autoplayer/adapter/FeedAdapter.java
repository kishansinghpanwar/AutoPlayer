package com.example.autoplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autoplayer.R;
import com.example.autoplayer.enums.FeedPostType;
import com.example.autoplayer.interfaces.MuteListener;
import com.example.autoplayer.model.FeedBean;
import com.example.autoplayer.utils.Utils;
import com.player.autoplayer.AutoPlayer;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private final List<FeedBean> feedBeanList;
    private final MuteListener muteListener;

    public FeedAdapter(Context context, List<FeedBean> feedBeanList, MuteListener muteListener) {
        this.context = context;
        this.feedBeanList = feedBeanList;
        this.muteListener = muteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == FeedPostType.TEXT.getValue()) {
            //Text
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_text, parent, false);
            viewHolder = new FeedHolderText(view);
        } else if (viewType == FeedPostType.VIDEO.getValue()) {
            //Video
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_video, parent, false);
            viewHolder = new FeedHolderVideo(view);
        } else {
            //Image
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_image, parent, false);
            viewHolder = new FeedHolderImage(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FeedHolderVideo) {
            Glide.with(context).load(Utils.getDummyPlaceholder())
                    .centerCrop()
                    .into(((FeedHolderVideo) holder).image);

            ((FeedHolderVideo) holder).autoPlayer.setUrl(feedBeanList.get(position).getUrl());
            ((FeedHolderVideo) holder).autoPlayer.setAnimationTime(500);
            ((FeedHolderVideo) holder).autoPlayer.setPlaceholderView(((FeedHolderVideo) holder).image);
            ((FeedHolderVideo) holder).ivVolume.setOnClickListener(view -> {
                ((FeedHolderVideo) holder).autoPlayer.setMute(!((FeedHolderVideo) holder).autoPlayer.isMute());
                if (((FeedHolderVideo) holder).autoPlayer.isMute()) {
                    ((FeedHolderVideo) holder).ivVolume.setImageResource(R.drawable.ic_volume_off);
                } else {
                    ((FeedHolderVideo) holder).ivVolume.setImageResource(R.drawable.ic_volume_on);
                }
                muteListener.onMute(position);
            });
            if (feedBeanList.get(position).isMute()) {
                ((FeedHolderVideo) holder).ivVolume.setImageResource(R.drawable.ic_volume_off);
            } else {
                ((FeedHolderVideo) holder).ivVolume.setImageResource(R.drawable.ic_volume_on);
            }
        }
        // handle your other views for Image and Text type.
    }


    @Override
    public int getItemViewType(int position) {
        return feedBeanList.get(position).getType().getValue();
    }

    @Override
    public int getItemCount() {
        return feedBeanList.size();
    }


    static class FeedHolderText extends RecyclerView.ViewHolder {
        public FeedHolderText(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class FeedHolderVideo extends RecyclerView.ViewHolder {
        AutoPlayer autoPlayer;
        ImageView image;
        ImageView ivVolume;

        public FeedHolderVideo(@NonNull View itemView) {
            super(itemView);
            autoPlayer = itemView.findViewById(R.id.autoPlayer);
            image = itemView.findViewById(R.id.image);
            ivVolume = itemView.findViewById(R.id.ivVolume);
        }
    }

    static class FeedHolderImage extends RecyclerView.ViewHolder {
        public FeedHolderImage(@NonNull View itemView) {
            super(itemView);
        }
    }
}
