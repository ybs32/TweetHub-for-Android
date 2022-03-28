package com.ybsystem.tweetmate.models.entities.twitter;

import com.ybsystem.tweetmate.models.entities.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.UserMentionEntity;

@Data
@EqualsAndHashCode(callSuper = false)
public class TwitterUserMentionEntity extends Entity {

    private long id;
    private String text;
    private String name;
    private String screenName;

    private int start;
    private int end;

    public TwitterUserMentionEntity(UserMentionEntity userMentionEntity) {
        this.id = userMentionEntity.getId();
        this.text = userMentionEntity.getText();
        this.name = userMentionEntity.getName();
        this.screenName = userMentionEntity.getScreenName();

        this.start = userMentionEntity.getStart();
        this.end = userMentionEntity.getEnd();
    }

}
