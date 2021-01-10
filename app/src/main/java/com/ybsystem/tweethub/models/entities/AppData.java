package com.ybsystem.tweethub.models.entities;

import com.ybsystem.tweethub.R;
import com.ybsystem.tweethub.application.TweetHubApp;
import com.ybsystem.tweethub.models.entities.twitter.TwitterUser;
import com.ybsystem.tweethub.utils.ResourceUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import static com.ybsystem.tweethub.models.enums.ColumnType.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppData extends Entity {

    private String version;
    private AccountArray<Account> accounts;

    public AppData() {
        this.version = TweetHubApp.getInstance().getString(R.string.app_version);
        this.accounts = new AccountArray<>();
    }

    public boolean existAccount() {
        return !accounts.isEmpty();
    }

    public void addAccount(final AccessToken token, final User user) {
        // Create account
        Account account = new Account();

        // Set user
        account.setUser(new TwitterUser(user));

        // Set vias
        ViaArray<Via> vias = new ViaArray<>();
        Via via = new Via();
        via.setName("TweetHub");
        via.setConsumerKey(ResourceUtils.getS());
        via.setConsumerSecret(ResourceUtils.getK());
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
        account.setColumns(columns);

        // Set lists, drafts, hashtags
        account.setLists(new EntityArray<>());
        account.setDrafts(new EntityArray<>());
        account.setHashtags(new EntityArray<>());

        // Add account
        this.accounts.add(account);
        this.accounts.setCurrentAccount(accounts.size() - 1);
    }

}
