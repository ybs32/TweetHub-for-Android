package com.ybsystem.tweetmate.models.entities.twitter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.Entity;
import com.ybsystem.tweetmate.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.URLEntity;
import twitter4j.User;

@Data
@EqualsAndHashCode(callSuper = false)
public class TwitterUser extends Entity {

    private TwitterURLEntity urlEntity;
    private TwitterURLEntity[] descriptionURLEntities;

    private Date createdAt;
    private long id;
    private String name;
    private String screenName;

    private String url;
    private String email;
    private String location;
    private String description;
    private String lang;
    private String timeZone;
    private int utcOffset;

    // Count
    private int statusesCount;
    private int friendsCount;
    private int followersCount;
    private int favouritesCount;
    private int listedCount;

    // State
    private boolean translator;
    private boolean isVerified;
    private boolean isProtected;
    private boolean isGeoEnabled;
    private boolean isFollowRequestSent;
    private boolean isContributorsEnabled;
    private boolean showAllInlineMedia;

    // Profile
    private String profileTextColor;
    private String profileLinkColor;
    private String profileBackgroundColor;
    private String profileSidebarFillColor;
    private String profileSidebarBorderColor;

    private boolean isDefaultProfile;
    private boolean isDefaultProfileImage;
    private boolean profileBackgroundTiled;
    private boolean profileUseBackgroundImage;

    private String profileImageUrl;
    private String profileImageUrlHttps;
    private String profileBackgroundImageUrl;
    private String profileBackgroundImageUrlHttps;

    private String biggerProfileImageURL;
    private String biggerProfileImageURLHttps;
    private String miniProfileImageURL;
    private String miniProfileImageURLHttps;
    private String originalProfileImageURL;
    private String originalProfileImageURLHttps;
    private String profileImageURL400x400;
    private String profileImageURL400x400Https;

    private String profileBannerURL;
    private String profileBannerRetinaURL;
    private String profileBannerIPadURL;
    private String profileBannerIPadRetinaURL;
    private String profileBannerMobileURL;
    private String profileBannerMobileRetinaURL;
    private String profileBanner300x100URL;
    private String profileBanner600x200URL;
    private String profileBanner1500x500URL;

    // Custom
    private boolean isFriend;
    private boolean isFollower;
    private boolean isMuting;
    private boolean isBlocking;
    private boolean isOutgoing;
    private boolean isIncoming;
    private ArrayList<Long> friendIds;
    private ArrayList<Long> followerIds;
    private ArrayList<Long> muteIds;
    private ArrayList<Long> blockIds;
    private ArrayList<Long> outgoingIds;
    private ArrayList<Long> incomingIds;

    public TwitterUser(User user) {
        URLEntity[] entities = user.getDescriptionURLEntities();
        this.urlEntity = new TwitterURLEntity(user.getURLEntity());
        this.descriptionURLEntities = new TwitterURLEntity[entities.length];
        for (int i = 0; i < entities.length; i++) {
            this.descriptionURLEntities[i] = new TwitterURLEntity(entities[i]);
        }

        this.createdAt = user.getCreatedAt();
        this.id = user.getId();
        this.name = user.getName();
        this.screenName = user.getScreenName();

        this.url = user.getURL();
        this.email = user.getEmail();
        this.location = user.getLocation();
        this.description = user.getDescription();
        this.lang = user.getLang();
        this.timeZone = user.getTimeZone();
        this.utcOffset = user.getUtcOffset();

        // Count
        this.statusesCount = user.getStatusesCount();
        this.friendsCount = user.getFriendsCount();
        this.followersCount = user.getFollowersCount();
        this.favouritesCount = user.getFavouritesCount();
        this.listedCount = user.getListedCount();

        // State
        this.translator = user.isTranslator();
        this.isVerified = user.isVerified();
        this.isProtected = user.isProtected();
        this.isGeoEnabled = user.isGeoEnabled();
        this.isFollowRequestSent = user.isFollowRequestSent();
        this.isContributorsEnabled = user.isContributorsEnabled();
        this.showAllInlineMedia = user.isShowAllInlineMedia();

        // Profile
        this.profileTextColor = user.getProfileTextColor();
        this.profileLinkColor = user.getProfileLinkColor();
        this.profileBackgroundColor = user.getProfileBackgroundColor();
        this.profileSidebarFillColor = user.getProfileSidebarFillColor();
        this.profileSidebarBorderColor = user.getProfileSidebarBorderColor();

        this.isDefaultProfile = user.isDefaultProfile();
        this.isDefaultProfileImage = user.isDefaultProfileImage();
        this.profileBackgroundTiled = user.isProfileBackgroundTiled();
        this.profileUseBackgroundImage = user.isProfileUseBackgroundImage();

        this.profileImageUrl = user.getProfileImageURL();
        this.profileImageUrlHttps = user.getProfileImageURLHttps();
        this.profileBackgroundImageUrl = user.getProfileBackgroundImageURL();
        this.profileBackgroundImageUrlHttps = user.getProfileBackgroundImageUrlHttps();

        this.biggerProfileImageURL = user.getBiggerProfileImageURL();
        this.biggerProfileImageURLHttps = user.getBiggerProfileImageURLHttps();
        this.miniProfileImageURL = user.getMiniProfileImageURL();
        this.miniProfileImageURLHttps = user.getMiniProfileImageURLHttps();
        this.originalProfileImageURL = user.getOriginalProfileImageURL();
        this.originalProfileImageURLHttps = user.getOriginalProfileImageURLHttps();
        this.profileImageURL400x400 = user.get400x400ProfileImageURL();
        this.profileImageURL400x400Https = user.get400x400ProfileImageURLHttps();

        this.profileBannerURL = user.getProfileBannerURL();
        this.profileBannerRetinaURL = user.getProfileBannerRetinaURL();
        this.profileBannerIPadURL = user.getProfileBannerIPadURL();
        this.profileBannerIPadRetinaURL = user.getProfileBannerIPadRetinaURL();
        this.profileBannerMobileURL = user.getProfileBannerMobileURL();
        this.profileBannerMobileRetinaURL = user.getProfileBannerMobileRetinaURL();
        this.profileBanner300x100URL = user.getProfileBanner300x100URL();
        this.profileBanner600x200URL = user.getProfileBanner600x200URL();
        this.profileBanner1500x500URL = user.getProfileBanner1500x500URL();

        // Custom
        this.isFriend = false;
        this.isFollower = false;
        this.isMuting = false;
        this.isBlocking = false;
        this.isOutgoing = false;
        this.isIncoming = false;
        this.friendIds = new ArrayList<>();
        this.followerIds = new ArrayList<>();
        this.muteIds = new ArrayList<>();
        this.blockIds = new ArrayList<>();
        this.outgoingIds = new ArrayList<>();
        this.incomingIds = new ArrayList<>();
    }

    public boolean isMyself() {
        return getId() == TweetMateApp.getMyUser().getId();
    }

    public boolean isPrivate() {
        return isProtected()
                && !isFriend()
                && !isMyself();
    }

    public String getRelationText() {
        if (isMyself())
            return "あなた";
        else if (isBlocking())
            return "ブロック中";
        else if (isFriend() && isFollower())
            return "相互フォロー";
        else if (isFriend())
            return "フォロー中";
        else if (isFollower())
            return "フォロワー";
        else
            return "フォロー";
    }

    public int getRelationTextColor() {
        if (isMyself() || isFriend())
            return Color.parseColor("#FFFFFF");
        else
            return ResourceUtils.getAccentColor();
    }

    public Drawable getRelationBackground() {
        if (isMyself() || isFriend())
            return ResourceUtils.getDrawable(R.drawable.bg_rounded_purple);
        else
            return ResourceUtils.getDrawable(R.drawable.bg_rounded_reverse);
    }

}
