package com.ybsystem.tweetmate.models.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UserList extends Entity {

    private long id;
    private String name;
    private boolean isRegistered;
}
