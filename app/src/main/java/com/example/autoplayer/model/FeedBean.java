package com.example.autoplayer.model;

import com.example.autoplayer.enums.FeedPostType;

public class FeedBean {
    FeedPostType type;
    String url;
    boolean mute = false;


    public FeedBean(FeedPostType type, String url) {
        this.type = type;
        this.url = url;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public FeedPostType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

}
