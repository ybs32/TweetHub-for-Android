package com.ybsystem.tweetmate.models.entities.twitter;

import com.ybsystem.tweetmate.models.entities.Entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.UserList;

@Data
@EqualsAndHashCode(callSuper = false)
public class TwitterUserList extends Entity {

    private TwitterUser user;
    private Date createdAt;
    private long id;
    private String name;
    private String fullName;
    private String description;

    // Count
    private int memberCount;
    private int subscriberCount;

    // State
    private boolean isProtected;
    private boolean isFollowing;

    // Not use
    // private String uri;
    // private String slug;

    // Custom
    private boolean isOwner;

    public TwitterUserList(UserList userList) {
        this.user = new TwitterUser(userList.getUser());
        this.createdAt = userList.getCreatedAt();
        this.id = userList.getId();
        this.name = userList.getName();
        this.fullName = userList.getFullName();
        this.description = userList.getDescription();

        // Count
        this.memberCount = userList.getMemberCount();
        this.subscriberCount = userList.getSubscriberCount();

        // State
        this.isProtected = !userList.isPublic();
        this.isFollowing = userList.isFollowing();

        // Custom
        this.isOwner = false;
    }

}
