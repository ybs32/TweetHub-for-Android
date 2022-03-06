package com.ybsystem.tweetmate.models.entities;

import com.ybsystem.tweetmate.BuildConfig;
import com.ybsystem.tweetmate.R;
import com.ybsystem.tweetmate.application.TweetMateApp;
import com.ybsystem.tweetmate.models.entities.twitter.TwitterUser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import static com.ybsystem.tweetmate.models.enums.ColumnType.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppData extends Entity {

    private String version;
    private AccountArray<Account> accounts;

    public AppData() {
        this.version = TweetMateApp.getInstance().getString(R.string.app_version);
        this.accounts = new AccountArray<>();
    }

    public boolean isAccountEmpty() {
        return accounts.isEmpty();
    }

    public void addAccount(final AccessToken token, final User user) {
        // Create account
        Account account = new Account();

        // Set user
        account.setUser(new TwitterUser(user));

        // Set vias
        ViaArray<Via> vias = new ViaArray<>();
        Via via = new Via();
        via.setName("TweetMate");
        via.setConsumerKey(BuildConfig.CK);
        via.setConsumerSecret(BuildConfig.CS);
        via.setToken(token.getToken());
        via.setTokenSecret(token.getTokenSecret());
        via.setCurrentVia(true);
        vias.add(via);
        account.setVias(vias);

        // Set columns
        ColumnArray<Column> columns = new ColumnArray<>();
        columns.add(new Column(-1, "@Mentions", MENTIONS, false));
        columns.add(new Column(-2, "ホーム", HOME, true));
        columns.add(new Column(-3, "リスト", LIST, false));
        columns.add(new Column(-4, "いいね", FAVORITE, false));
        columns.add(new Column(-5, "検索", SEARCH, false));
        account.setColumns(columns);

        // Set lists, drafts, hashtags
        account.setLists(new EntityArray<>());
        account.setDrafts(new EntityArray<>());
        account.setHashtags(new EntityArray<>());

        // Save account
        this.accounts.add(account);
        this.accounts.setCurrentAccount(accounts.size() - 1);
    }

}
