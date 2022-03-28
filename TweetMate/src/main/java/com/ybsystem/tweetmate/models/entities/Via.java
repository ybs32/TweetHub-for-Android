package com.ybsystem.tweetmate.models.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Via extends Entity {

    private String name;
    private String consumerKey;
    private String consumerSecret;
    private String token;
    private String tokenSecret;
    private boolean isCurrentVia;
}
