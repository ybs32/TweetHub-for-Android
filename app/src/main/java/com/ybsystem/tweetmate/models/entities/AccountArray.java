package com.ybsystem.tweetmate.models.entities;

import com.ybsystem.tweetmate.utils.ToastUtils;

public class AccountArray<T extends Account> extends EntityArray<T> {

    public Account getCurrentAccount() {
        int index = getCurrentAccountNum();
        if (index == -1) {
            return null;
        } else {
            return this.get(index);
        }
    }

    public int getCurrentAccountNum() {
        for (int i = 0; i < size(); i++) {
            if (get(i).isCurrentAccount()) {
                return i;
            }
        }
        return -1;
    }

    public void setCurrentAccount(int i) {
        // Clear
        for (Account account: this) {
            account.setCurrentAccount(false);
        }
        // Set
        get(i).setCurrentAccount(true);
        pcs.firePropertyChange();
    }

    @Override
    public T remove(int index) {
        if(index == getCurrentAccountNum()) {
            ToastUtils.showShortToast("使用中のアカウントです。");
            return null;
        }
        return super.remove(index);
    }

}
