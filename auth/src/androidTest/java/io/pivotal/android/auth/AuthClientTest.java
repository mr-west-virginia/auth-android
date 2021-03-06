/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.test.AndroidTestCase;

import org.mockito.Mockito;

import java.util.UUID;

public class AuthClientTest extends AndroidTestCase {

    protected abstract class AccountManagerFutureBundle implements AccountManagerFuture<Bundle> {}

    private static final String ACCESS_TOKEN = UUID.randomUUID().toString();
    private static final String ACCOUNT_NAME = UUID.randomUUID().toString();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", mContext.getCacheDir().getPath());
    }

    public void testRequestAccessTokenWithActivityAndUserPromptEnabled() {
        final Activity activity = Mockito.mock(Activity.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        client.setShouldShowUserPrompt(true);

        Mockito.when(proxy.getAuthTokenByFeatures(Mockito.any(Activity.class))).thenReturn(future);
        Mockito.doReturn(response).when(client).validateTokenInFuture(Mockito.any(Activity.class), Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(activity));

        Mockito.verify(proxy).getAuthTokenByFeatures(activity);
        Mockito.verify(client).validateTokenInFuture(activity, future);
    }

    public void testRequestAccessTokenWithActivityAndUserPromptDisabled() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        client.setShouldShowUserPrompt(false);

        Mockito.when(proxy.getAuthToken(Mockito.any(Account.class))).thenReturn(future);
        Mockito.doReturn(account).when(client).getAccount(context);
        Mockito.doReturn(response).when(client).validateTokenInFuture(Mockito.any(Activity.class), Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(context));

        Mockito.verify(client).getAccount(context);
        Mockito.verify(proxy).getAuthToken(account);
        Mockito.verify(client).validateTokenInFuture(context, future);
    }

    public void testRequestAccessTokenWithContext() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthToken(Mockito.any(Account.class))).thenReturn(future);
        Mockito.doReturn(account).when(client).getAccount(context);
        Mockito.doReturn(response).when(client).validateTokenInFuture(Mockito.any(Activity.class), Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(context));

        Mockito.verify(client).getAccount(context);
        Mockito.verify(proxy).getAuthToken(account);
        Mockito.verify(client).validateTokenInFuture(context, future);
    }

    public void testRequestAccessTokenWithContextNoLastUsedAccount() {
        final Context context = Mockito.mock(Context.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.doReturn(null).when(client).getAccount(context);
        Mockito.doReturn(response).when(client).getFailureAuthResponse(Mockito.any(Exception.class));

        assertEquals(response, client.requestAccessToken(context));

        Mockito.verify(client).getAccount(context);
        Mockito.verify(client).getFailureAuthResponse(Mockito.any(Exception.class));
    }

    public void testRequestAccessTokenWithAccountAndActivityWithValidation() {
        final Activity activity = Mockito.mock(Activity.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthToken(Mockito.any(Activity.class), Mockito.any(Account.class))).thenReturn(future);
        Mockito.doReturn(response).when(client).validateTokenInFuture(Mockito.any(Activity.class), Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(activity, account, true));

        Mockito.verify(proxy).getAuthToken(activity, account);
        Mockito.verify(client).validateTokenInFuture(activity, future);
    }

    public void testRequestAccessTokenWithAccountAndActivityWithoutValidation() {
        final Activity activity = Mockito.mock(Activity.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthToken(Mockito.any(Activity.class), Mockito.any(Account.class))).thenReturn(future);
        Mockito.doReturn(response).when(client).retrieveResponseFromFuture(Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(activity, account, false));

        Mockito.verify(proxy).getAuthToken(activity, account);
        Mockito.verify(client).retrieveResponseFromFuture(future);
    }

    public void testRequestAccessTokenWithAccountAndContextWithValidation() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthToken(Mockito.any(Account.class))).thenReturn(future);
        Mockito.doReturn(response).when(client).validateTokenInFuture(Mockito.any(Context.class), Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(context, account, true));

        Mockito.verify(proxy).getAuthToken(account);
        Mockito.verify(client).validateTokenInFuture(context, future);
    }

    public void testRequestAccessTokenWithAccountAndContextWithoutValidation() {
        final Context context = Mockito.mock(Context.class);
        final Account account = Mockito.mock(Account.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final Response response = Mockito.mock(Response.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(proxy.getAuthToken(Mockito.any(Account.class))).thenReturn(future);
        Mockito.doReturn(response).when(client).retrieveResponseFromFuture(Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.requestAccessToken(context, account, false));

        Mockito.verify(proxy).getAuthToken(account);
        Mockito.verify(client).retrieveResponseFromFuture(future);
    }

    public void testValidateFutureWithSuccessAndTokenExpired() {
        final Context context = Mockito.mock(Context.class);
        final Response response = Mockito.spy(new Response(ACCESS_TOKEN, ACCOUNT_NAME));
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));
        final Account account = Mockito.mock(Account.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);

        Mockito.when(response.isSuccess()).thenReturn(true);
        Mockito.when(response.isTokenExpired()).thenReturn(true);
        Mockito.doReturn(response).when(client).retrieveResponseFromFuture(Mockito.any(AccountManagerFutureBundle.class));
        Mockito.doNothing().when(client).setAccountName(Mockito.any(Context.class), Mockito.anyString());
        Mockito.doReturn(account).when(client).getAccount(Mockito.any(Context.class));
        Mockito.doReturn(response).when(client).requestAccessToken(Mockito.any(Context.class), Mockito.any(Account.class), Mockito.anyBoolean());

        assertEquals(response, client.validateTokenInFuture(context, future));

        Mockito.verify(client).retrieveResponseFromFuture(future);
        Mockito.verify(client).setAccountName(context, ACCOUNT_NAME);
        Mockito.verify(proxy).invalidateAccessToken(ACCESS_TOKEN);
        Mockito.verify(client).getAccount(context);
        Mockito.verify(client).requestAccessToken(context, account, false);
    }

    public void testRetrieveResponseFromFutureWithSuccessAndTokenNotExpired() {
        final Context context = Mockito.mock(Context.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final Response response = Mockito.spy(new Response(ACCESS_TOKEN, ACCOUNT_NAME));
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(response.isSuccess()).thenReturn(true);
        Mockito.when(response.isTokenExpired()).thenReturn(false);
        Mockito.doReturn(response).when(client).retrieveResponseFromFuture(Mockito.any(AccountManagerFutureBundle.class));
        Mockito.doNothing().when(client).setAccountName(Mockito.any(Context.class), Mockito.anyString());

        assertEquals(response, client.validateTokenInFuture(context, future));

        Mockito.verify(client).retrieveResponseFromFuture(future);
        Mockito.verify(client).setAccountName(context, ACCOUNT_NAME);
    }

    public void testRetrieveResponseFromFutureWithFailure() {
        final Context context = Mockito.mock(Context.class);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final Response response = Mockito.mock(Response.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));

        Mockito.when(response.isSuccess()).thenReturn(false);
        Mockito.doReturn(response).when(client).retrieveResponseFromFuture(Mockito.any(AccountManagerFutureBundle.class));

        assertEquals(response, client.validateTokenInFuture(context, future));

        Mockito.verify(client).retrieveResponseFromFuture(future);
    }

    public void testValidateTokenInFutureWithAccessToken() throws Exception {
        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_AUTHTOKEN, ACCESS_TOKEN);
        result.putString(AccountManager.KEY_ACCOUNT_NAME, ACCOUNT_NAME);
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = new AuthClient.Default(proxy);

        Mockito.when(future.getResult()).thenReturn(result);

        final Response response = client.retrieveResponseFromFuture(future);

        assertEquals(ACCESS_TOKEN, response.accessToken);
        assertEquals(ACCOUNT_NAME, response.accountName);

        Mockito.verify(future).getResult();
    }

    public void testValidateAccountBundleWithoutAccessToken() throws Exception {
        final Bundle result = new Bundle();
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));
        final Response response = Mockito.mock(Response.class);

        Mockito.when(future.getResult()).thenReturn(result);
        Mockito.doReturn(response).when(client).getFailureAuthResponse(Mockito.any(Exception.class));

        assertEquals(response, client.retrieveResponseFromFuture(future));

        Mockito.verify(future).getResult();
        Mockito.verify(client).getFailureAuthResponse(Mockito.any(Exception.class));
    }

    public void testValidateAccountBundleThrowsException() throws Exception {
        final AccountManagerFuture<Bundle> future = Mockito.mock(AccountManagerFutureBundle.class);
        final AccountsProxy proxy = Mockito.mock(AccountsProxy.class);
        final AuthClient.Default client = Mockito.spy(new AuthClient.Default(proxy));
        final Response response = Mockito.mock(Response.class);
        final RuntimeException exception = Mockito.mock(RuntimeException.class);

        Mockito.doThrow(exception).when(future).getResult();
        Mockito.doReturn(response).when(client).getFailureAuthResponse(Mockito.any(RuntimeException.class));

        assertEquals(response, client.retrieveResponseFromFuture(future));

        Mockito.verify(future).getResult();
        Mockito.verify(client).getFailureAuthResponse(exception);
    }
}
