package com.ybsystem.tweethub.models.entities.twitter;

import com.ybsystem.tweethub.models.entities.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.HashtagEntity;

@Data
@EqualsAndHashCode(callSuper = false)
public class TwitterHashtagEntity extends Entity {

    private String text;
    private int start;
    private int end;

    public TwitterHashtagEntity(HashtagEntity hashtagEntity) {
        this.text = hashtagEntity.getText();
        this.start = hashtagEntity.getStart();
        this.end = hashtagEntity.getEnd();
    }

}
