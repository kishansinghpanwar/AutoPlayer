package com.example.autoplayer.utils;

import com.example.autoplayer.enums.FeedPostType;
import com.example.autoplayer.model.FeedBean;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<FeedBean> getDummyData() {
        List<FeedBean> dummyFeedList = new ArrayList<>();
        dummyFeedList.add(new FeedBean(FeedPostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4"));
        dummyFeedList.add(new FeedBean(FeedPostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4"));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.TEXT, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.IMAGE, ""));
        dummyFeedList.add(new FeedBean(FeedPostType.VIDEO, "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"));
        return dummyFeedList;
    }


    public static String getDummyPlaceholder() {
        return "https://nwtangsoodo.com/wp-content/uploads/sites/191/2015/12/video-placeholder.png";
    }

}
