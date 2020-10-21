package com.ybsystem.tweethub.models.entities;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppData {

    private String version;
    private AccountArray<Account> accounts;

    public AppData() {
        this.version = TweetHubApp.getInstance().getString(R.string.app_version);
        this.accounts = new AccountArray<>();
    }

    public boolean existAccount() {
        return !accounts.isEmpty();
    }

}
