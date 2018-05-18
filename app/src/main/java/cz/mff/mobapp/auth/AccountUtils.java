package cz.mff.mobapp.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountUtils {

    public static final String ACCOUNT_TYPE = "mobapp.mff.cz";
    public static final String AUTH_TOKEN_TYPE = "cz.mff.mobapp.RegularToken";

    public static DummyAuthenticator mServerAuthenticator = new DummyAuthenticator();

    public static Account getAccount(Context context, String accountName) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        for (Account account : accounts) {
            if (account.name.equalsIgnoreCase(accountName)) {
                return account;
            }
        }
        return null;
    }

}
