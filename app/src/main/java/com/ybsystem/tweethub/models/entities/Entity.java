package com.ybsystem.tweethub.models.entities;

import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.libs.propertychange.PropertyChangeListener;
import com.ybsystem.tweethub.libs.propertychange.PropertyChangeSupport;
import com.ybsystem.tweethub.storages.PrefEntity;

import java.io.Serializable;

public class Entity implements PropertyChangeListener, Serializable {

    protected transient PropertyChangeSupport pcs = new PropertyChangeSupport();

    public Entity() {
        pcs.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange() {
        AppData appData = TweetHubApp.getData();
        PrefEntity.saveAppData(appData);
    }

}
