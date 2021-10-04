package com.example.autoplayer.enums;

public enum FeedPostType {
    TEXT(1),
    IMAGE(2),
    VIDEO(3);

    private final int value;

    FeedPostType(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
