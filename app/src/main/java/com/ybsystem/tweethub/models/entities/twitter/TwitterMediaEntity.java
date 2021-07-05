package com.ybsystem.tweethub.models.entities.twitter;

import com.ybsystem.tweethub.models.entities.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.MediaEntity;

@Data
@EqualsAndHashCode(callSuper = false)
public class TwitterMediaEntity extends Entity {

    private long id;
    private String type;

    private String url;
    private String displayURL;
    private String expandedURL;
    private String mediaURL;
    private String mediaURLHttps;
    private MediaEntity.Variant[] videoVariants;

    private int start;
    private int end;

    public TwitterMediaEntity(MediaEntity mediaEntity) {
        this.id = mediaEntity.getId();
        this.type = mediaEntity.getType();

        this.url = mediaEntity.getURL();
        this.displayURL = mediaEntity.getDisplayURL();
        this.expandedURL = mediaEntity.getExpandedURL();
        this.mediaURL = mediaEntity.getMediaURL();
        this.mediaURLHttps = mediaEntity.getMediaURLHttps();
        this.videoVariants = mediaEntity.getVideoVariants();

        this.start = mediaEntity.getStart();
        this.end = mediaEntity.getEnd();
    }

}
