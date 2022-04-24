package com.ybsystem.tweetmate.utils;

public class MediaUtils {

    private MediaUtils() {
    }

    // ----- Ref -----
    // https://developer.twitter.com/en/docs/accounts-and-users/user-profile-images-and-banners
    // https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/entities-object#media

    /**
     * Get a profile image URL of specified size
     *
     * @param url Profile image url
     * @param size Target size
     * @return Converted URL
     */
    public static String getProfileBySize(String url, int size) {
        String extension = url.substring(url.lastIndexOf('.'));
        url = url.substring(0, url.lastIndexOf('_'));
        switch (size) {
            case 0:
                return url + "_mini" + extension;
            case 1:
                return url + "_normal" + extension;
            case 2:
                return url + "_bigger" + extension;
            case 3:
                return url + extension;
            default:
                return null;
        }
    }

    /**
     * Get a banner image URL of specified size
     *
     * @param url Banner image url
     * @param size Target size
     * @return Converted URL
     */
    public static String getBannerBySize(String url, int size) {
        url = url.substring(0, url.lastIndexOf('/'));
        switch (size) {
            case 0:
                return url + "/mobile";
            case 1:
                return url + "/mobile_retina";
            case 2:
                return url + "/web_retina";
            case 3:
                return url + "/1500x500";
            default:
                return null;
        }
    }

    /**
     * Get a media image URL of specified size
     *
     * @param url Media image URL
     * @param size Target size
     * @return Converted URL
     */
    public static String getMediaBySize(String url, int size) {
        url = url.substring(0, url.lastIndexOf(':'));
        switch (size) {
            case 0:
                return url + ":small";
            case 1:
                return url + ":medium";
            case 2:
                return url + ":large";
            case 3:
                return url + ":orig";
            default:
                return null;
        }
    }

}
