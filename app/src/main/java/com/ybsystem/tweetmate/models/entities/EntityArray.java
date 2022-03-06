package com.ybsystem.tweetmate.models.entities;

import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.libs.propertychange.PropertyChangeListener;
import com.ybsystem.tweetmate.libs.propertychange.PropertyChangeSupport;
import com.ybsystem.tweetmate.storages.PrefEntity;
import com.ybsystem.tweetmate.utils.ToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class EntityArray<T> extends ArrayList<T>
        implements PropertyChangeListener, Serializable {

    protected transient PropertyChangeSupport pcs = new PropertyChangeSupport();

    public EntityArray() {
        pcs.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange() {
        AppData appData = TweetMateApp.getData();
        PrefEntity.saveAppData(appData);
    }

    @Override
    public boolean add(T e){
        boolean result = super.add(e);
        pcs.firePropertyChange();
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        pcs.firePropertyChange();
    }

    @Override
    public T remove(int index) {
        T e = super.remove(index);
        pcs.firePropertyChange();
        return e;
    }

    public void movePrev(int index) {
        if (index == 0) {
            ToastUtils.showShortToast("これ以上移動できません。");
            return;
        }
        Collections.swap(this, index, index - 1);
        pcs.firePropertyChange();
    }

    public void moveNext(int index) {
        if (index == size() - 1) {
            ToastUtils.showShortToast("これ以上移動できません。");
            return;
        }
        Collections.swap(this, index, index + 1);
        pcs.firePropertyChange();
    }

}

