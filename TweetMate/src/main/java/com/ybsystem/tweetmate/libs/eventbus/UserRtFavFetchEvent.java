package com.ybsystem.tweetmate.libs.eventbus;

import java.util.ArrayList;

public class UserRtFavFetchEvent {

    private ArrayList<Long> mUserIds;

    public UserRtFavFetchEvent(ArrayList<Long> userIds) {
        this.mUserIds = userIds;
    }

    public ArrayList<Long> getUserIds() {
        return this.mUserIds;
    }
}
