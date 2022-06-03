package com.ybsystem.tweetmate.models.entities;

import com.ybsystem.tweetmate.utils.ToastUtils;

import static com.ybsystem.tweetmate.resources.ResString.*;

public class ColumnArray<T extends Column> extends EntityArray<T> {

    public int getBootColumnNum() {
        for (int i = 0; i < size(); i++) {
            if (get(i).isBootColumn()) {
                return i;
            }
        }
        return -1;
    }

    public void setBootColumn(int i) {
        // Clear
        for (Column column: this) {
            column.setBootColumn(false);
        }
        // Set
        get(i).setBootColumn(true);
        pcs.firePropertyChange();
    }

    @Override
    public boolean add(T e){
        for (Column column: this) {
            if (column.getId() == e.getId()) {
                ToastUtils.showShortToast(STR_FAIL_ALREADY_ADD);
                return false;
            }
        }
        return super.add(e);
    }

    @Override
    public T remove(int index) {
        if(index == getBootColumnNum()) {
            ToastUtils.showShortToast(STR_FAIL_USING_COLUMN);
            return null;
        }
        return super.remove(index);
    }

}
