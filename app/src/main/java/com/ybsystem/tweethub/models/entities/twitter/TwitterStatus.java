package com.ybsystem.tweethub.models.entities.twitter;

import com.ybsystem.tweethub.models.entities.Entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

@Data
@EqualsAndHashCode(callSuper = false)
public class TwitterStatus extends Entity {

    private TwitterUser user;
    private TwitterStatus rtStatus;
    private TwitterStatus qtStatus;
    private TwitterURLEntity[] urlEntities;
    private TwitterMediaEntity[] mediaEntities;
    private TwitterHashtagEntity[] hashtagEntities;
    private TwitterUserMentionEntity[] userMentionEntities;

    private Date createdAt;
    private long id;
    private long quotedStatusId;
    private long currentUserRetweetId;

    private String lang;
    private String source;
    private String text;
    private int displayTextRangeStart;
    private int displayTextRangeEnd;

    // In reply to
    private long inReplyToStatusId;
    private long inReplyToUserId;
    private String inReplyToScreenName;

    // Count
    private int favoriteCount;
    private int retweetCount;

    // State
    private boolean isFavorited;
    private boolean isRetweeted;
    private boolean isRetweet;
    private boolean isRetweetedByMe;

    // Not use
    // private Place place;
    // private Scopes scopes;
    // private GeoLocation geoLocation;
    // private TwitterURLEntity quotedStatusPermalink;
    // private TwitterSymbolEntity[] symbolEntities;
    // private long[] contributorsIDs;
    // private boolean isTruncated;
    // private boolean isPossiblySensitive;

    public TwitterStatus(Status status) {
        User user = status.getUser();
        Status rtStatus = status.getRetweetedStatus();
        Status qtStatus = status.getQuotedStatus();
        this.user = user != null ? new TwitterUser(user) : null;
        this.rtStatus = rtStatus != null ? new TwitterStatus(rtStatus) : null;
        this.qtStatus = qtStatus != null ? new TwitterStatus(qtStatus) : null;

        URLEntity[] urls = status.getURLEntities();
        MediaEntity[] medias = status.getMediaEntities();
        HashtagEntity[] hashtags = status.getHashtagEntities();
        UserMentionEntity[] userMentions = status.getUserMentionEntities();
        this.urlEntities = new TwitterURLEntity[urls.length];
        this.mediaEntities = new TwitterMediaEntity[medias.length];
        this.hashtagEntities = new TwitterHashtagEntity[hashtags.length];
        this.userMentionEntities = new TwitterUserMentionEntity[userMentions.length];
        for (int i = 0; i < urls.length; i++) {
            this.urlEntities[i] = new TwitterURLEntity(urls[i]);
        }
        for (int i = 0; i < medias.length; i++) {
            this.mediaEntities[i] = new TwitterMediaEntity(medias[i]);
        }
        for (int i = 0; i < hashtags.length; i++) {
            this.hashtagEntities[i] = new TwitterHashtagEntity(hashtags[i]);
        }
        for (int i = 0; i < userMentions.length; i++) {
            this.userMentionEntities[i] = new TwitterUserMentionEntity(userMentions[i]);
        }

        this.createdAt = status.getCreatedAt();
        this.id = status.getId();
        this.quotedStatusId = status.getQuotedStatusId();
        this.currentUserRetweetId = status.getCurrentUserRetweetId();

        this.lang = status.getLang();
        this.source = status.getSource();
        this.text = status.getText();
        this.displayTextRangeStart = status.getDisplayTextRangeStart();
        this.displayTextRangeEnd = status.getDisplayTextRangeEnd();

        // In reply to
        this.inReplyToStatusId = status.getInReplyToStatusId();
        this.inReplyToUserId = status.getInReplyToUserId();
        this.inReplyToScreenName = status.getInReplyToScreenName();

        // Count
        this.favoriteCount = status.getFavoriteCount();
        this.retweetCount = status.getRetweetCount();

        // State
        this.isFavorited = status.isFavorited();
        this.isRetweeted = status.isRetweeted();
        this.isRetweet = rtStatus != null;
        this.isRetweetedByMe = currentUserRetweetId != -1L;
    }

}
