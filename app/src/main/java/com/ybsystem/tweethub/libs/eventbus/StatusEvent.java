package com.ybsystem.tweethub.libs.eventbus;

import com.ybsystem.tweethub.models.entities.twitter.TwitterStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatusEvent {

    private TwitterStatus status;
}
