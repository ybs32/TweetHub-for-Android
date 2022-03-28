package com.ybsystem.tweetmate.models.entities.twitter;

import com.ybsystem.tweetmate.models.entities.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.URLEntity;

@Data
@EqualsAndHashCode(callSuper = false)
public class TwitterURLEntity extends Entity {

    private String url;
    private String displayURL;
    private String expandedURL;
    private String text;

    private int start;
    private int end;

    public TwitterURLEntity(URLEntity urlEntity) {
        this.url = urlEntity.getURL();
        this.displayURL = urlEntity.getDisplayURL();
        this.expandedURL = urlEntity.getExpandedURL();
        this.text = urlEntity.getText();

        this.start = urlEntity.getStart();
        this.end = urlEntity.getEnd();
    }

}
