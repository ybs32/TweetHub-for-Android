package com.ybsystem.tweethub.models.entities;

import com.ybsystem.tweethub.utils.ToastUtils;

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
                ToastUtils.showShortToast("既に追加されています。");
                return false;
            }
        }
        return super.add(e);
    }

    @Override
    public T remove(int index) {
        if(index == getBootColumnNum()) {
            ToastUtils.showShortToast("起動カラムに設定されています。");
            return null;
        }
        return super.remove(index);
    }

}
