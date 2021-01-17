package com.ybsystem.tweethub.models.entities;

import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Account extends Entity {

    private TwitterUser user;
    private ViaArray<Via> vias;
    private ColumnArray<Column> columns;

    private EntityArray<UserList> lists;
    private EntityArray<String> drafts;
    private EntityArray<String> hashtags;

    private boolean isCurrentAccount;

    public void setUser(TwitterUser user) {
        this.user = user;
        pcs.firePropertyChange();
    }

    public void setVias(ViaArray<Via> vias) {
        this.vias = vias;
        pcs.firePropertyChange();
    }

    public void setColumns(ColumnArray<Column> columns) {
        this.columns = columns;
        pcs.firePropertyChange();
    }

    public void setLists(EntityArray<UserList> lists) {
        this.lists = lists;
        pcs.firePropertyChange();
    }

    public void setDrafts(EntityArray<String> drafts) {
        this.drafts = drafts;
        pcs.firePropertyChange();
    }

    public void setHashtags(EntityArray<String> hashtags) {
        this.hashtags = hashtags;
        pcs.firePropertyChange();
    }

}

