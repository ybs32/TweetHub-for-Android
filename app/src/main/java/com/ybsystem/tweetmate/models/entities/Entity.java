package com.ybsystem.tweetmate.models.entities;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.propertychange.PropertyChangeListener;
import com.ybsystem.tweetmate.libs.propertychange.PropertyChangeSupport;
import com.ybsystem.tweetmate.databases.PrefEntity;

import java.io.Serializable;

public class Entity implements PropertyChangeListener, Serializable {

    protected transient PropertyChangeSupport pcs = new PropertyChangeSupport();

    public Entity() {
        pcs.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange() {
        AppData appData = TweetMateApp.getData();
        PrefEntity.saveAppData(appData);
    }

}
