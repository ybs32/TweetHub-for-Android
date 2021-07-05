package com.ybsystem.tweethub.models.entities;

import com.ybsystem.tweethub.utils.ToastUtils;

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
                ToastUtils.showShortToast("既に追加されています。");
                return false;
            }
        }
        return super.add(e);
    }

    @Override
    public T remove(int index) {
        if(index == getCurrentViaNum()) {
            ToastUtils.showShortToast("使用中のキーです。");
            return null;
        }
        return super.remove(index);
    }

}

