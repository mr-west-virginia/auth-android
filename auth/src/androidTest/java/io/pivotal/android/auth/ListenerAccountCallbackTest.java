/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Base64;

public class ListenerAccountCallbackTest extends AndroidTestCase {

    public void testCallbackWithNullListenerThrowsException() {
        try {
            new ListenerAccountCallback(null, null, null).run(new MockAccountManagerFuture());
            fail();
        } catch (final NullPointerException e) {
            assertNotNull(e);
        }
    }

    public void testAuthorizationFailsWhenExceptionIsThrown() {
        final AssertionLatch latch = new AssertionLatch(1);
        final ListenerAccountCallback callback = new ListenerAccountCallback(null, null, new Auth.Listener() {
            @Override
            public void onFailure(final Error error) {
                latch.countDown();
                assertEquals("error", error.getMessage());
            }

            @Override
            public void onComplete(final String token, final String name) {
                fail();
            }
        });
        callback.run(new MockAccountManagerFuture() {
            @Override
            public Bundle getResult() {
                throw new RuntimeException("error");
            }
        });
        latch.assertComplete();
    }

    public void testAuthorizationWithExpiredTokenInvalidatesTokenAndRequestsANewOne() {
        final AssertionLatch latch1 = new AssertionLatch(1);
        final AssertionLatch latch2 = new AssertionLatch(1);
        TokenProviderFactory.init(new ExpiredTokenProvider() {
            @Override
            public void invalidateAccessToken(final String accessToken) {
                latch1.countDown();
            }

            @Override
            public void getAccessToken(final Account account, final boolean prompt, final Auth.Listener listener) {
                latch2.countDown();
            }
        });
        new ListenerAccountCallback(null, null, null).run(new MockAccountManagerFuture() {
            @Override
            public Bundle getResult() {
                final long timeInPast = System.currentTimeMillis() / 1000 - 60;
                final String expiration = "{ \"exp\": \"" + timeInPast + "\" }";
                final String token = "." + Base64.encodeToString(expiration.getBytes(), Base64.DEFAULT);

                final Bundle bundle = new Bundle();
                bundle.putString(AccountManager.KEY_AUTHTOKEN, token);
                return bundle;
            }
        });

        latch1.assertComplete();
        latch2.assertComplete();
    }

    public void testAuthorizationWithValidTokenSucceeds() {
        final AssertionLatch latch = new AssertionLatch(1);

        TokenProviderFactory.init(new ValidTokenProvider());

        final ListenerAccountCallback callback = new ListenerAccountCallback(null, null, new Auth.Listener() {
            @Override
            public void onFailure(final Error error) {
                fail();
            }

            @Override
            public void onComplete(final String token, final String name) {
                latch.countDown();
            }
        });
        callback.run(new MockAccountManagerFuture() {
            @Override
            public Bundle getResult() {
                return Bundle.EMPTY;
            }
        });

        latch.assertComplete();
    }

    private static class ExpiredTokenProvider extends MockTokenProvider {

        @Override
        public Account[] getAccounts() {
            return new Account[0];
        }

        @Override
        public String getAccessToken(final Account account) {
            final long timeInPast = System.currentTimeMillis() / 1000 - 60;
            final String expiration = "{ \"exp\": \"" + timeInPast + "\" }";
            return "." + Base64.encodeToString(expiration.getBytes(), Base64.DEFAULT);
        }

        @Override
        public String getRefreshToken(final Account account) {
            return "refresh";
        }
    }

    private static class ValidTokenProvider extends MockTokenProvider {

        @Override
        public Account[] getAccounts() {
            return new Account[0];
        }

        @Override
        public String getAccessToken(final Account account) {
            final long timeInFuture = System.currentTimeMillis() / 1000 + 60;
            final String expiration = "{ \"exp\": \"" + timeInFuture + "\" }";
            return "." + Base64.encodeToString(expiration.getBytes(), Base64.DEFAULT);
        }

        @Override
        public String getRefreshToken(final Account account) {
            return "refresh";
        }
    }
}