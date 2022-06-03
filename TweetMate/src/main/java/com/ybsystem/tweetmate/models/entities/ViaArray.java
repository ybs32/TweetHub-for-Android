package com.ybsystem.tweetmate.models.entities;

import com.ybsystem.tweetmate.utils.ToastUtils;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class ViaArray<T extends Via> extends EntityArray<T> {

    public Via getCurrentVia() {
        int index = getCurrentViaNum();
        if (index == -1) {
            return null;
        } else {
            return this.get(index);
        }
    }

    public int getCurrentViaNum() {
        for (int i = 0; i < size(); i++) {
            if (get(i).isCurrentVia()) {
                return i;
            }
        }
        return -1;
    }

    public void setCurrentVia(int i) {
        // Clear
        for (Via via: this) {
            via.setCurrentVia(false);
        }
        // Set
        get(i).setCurrentVia(true);
        pcs.firePropertyChange();
    }

    @Override
    public boolean add(T e){
        for (Via via: this) {
            if (via.getConsumerKey().equals(e.getConsumerKey())) {
                ToastUtils.showShortToast(STR_FAIL_ALREADY_ADD);
                return false;
            }
        }
        return super.add(e);
    }

    @Override
    public T remove(int index) {
        if(index == getCurrentViaNum()) {
            ToastUtils.showShortToast(STR_FAIL_USING_VIA);
            return null;
        }
        return super.remove(index);
    }

}

