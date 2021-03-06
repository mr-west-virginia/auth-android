/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.AuthorizationCodeTokenRequest;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.PasswordTokenRequest;
import com.google.api.client.auth.oauth2.RefreshTokenRequest;
import com.google.api.client.http.BasicAuthentication;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Arrays;
import java.util.UUID;

public interface RemoteAuthenticator {

    public PasswordTokenRequest newPasswordTokenRequest(String username, String password);

    public RefreshTokenRequest newRefreshTokenRequest(String refreshToken);

    public AuthorizationCodeTokenRequest newAuthorizationCodeTokenRequest(String authorizationCode);

    public AuthorizationCodeRequestUrl newAuthorizationCodeUrl();

    public static class Default implements RemoteAuthenticator {

        private static final HttpTransport TRANSPORT = new NetHttpTransport();
        private static final JsonFactory JSON_FACTORY = new JacksonFactory();
        private static final GenericUrl TOKEN_URL = new GenericUrl(Pivotal.getTokenUrl());

        private static final HttpExecuteInterceptor INTERCEPTOR = new BasicAuthentication(Pivotal.getClientId(), Pivotal.getClientSecret());

        @Override
        public PasswordTokenRequest newPasswordTokenRequest(final String username, final String password) {
            final PasswordTokenRequest request = new PasswordTokenRequest(TRANSPORT, JSON_FACTORY, TOKEN_URL, username, password);
            request.set("client_id", Pivotal.getClientId());
            request.set("client_secret", Pivotal.getClientSecret());
            request.setClientAuthentication(INTERCEPTOR);
            request.setScopes(Arrays.asList(Pivotal.getScopes().split(" ")));
            return request;
        }

        @Override
        public RefreshTokenRequest newRefreshTokenRequest(final String refreshToken) {
            final RefreshTokenRequest request = new RefreshTokenRequest(TRANSPORT, JSON_FACTORY, TOKEN_URL, refreshToken);
            request.set("client_id", Pivotal.getClientId());
            request.set("client_secret", Pivotal.getClientSecret());
            request.setClientAuthentication(INTERCEPTOR);
            return request;
        }

        @Override
        public AuthorizationCodeTokenRequest newAuthorizationCodeTokenRequest(final String authorizationCode) {
            final DefaultAuthorizationCodeFlow flow = new DefaultAuthorizationCodeFlow();
            final AuthorizationCodeTokenRequest request = flow.newTokenRequest(authorizationCode);
            request.set("client_id", Pivotal.getClientId());
            request.set("client_secret", Pivotal.getClientSecret());
            return request;
        }

        @Override
        public AuthorizationCodeRequestUrl newAuthorizationCodeUrl() {
            final DefaultAuthorizationCodeFlow flow = new DefaultAuthorizationCodeFlow();
            return flow.newAuthorizationUrl();
        }

        private static final class DefaultAuthorizationCodeFlow extends AuthorizationCodeFlow {

            private static final Credential.AccessMethod METHOD = BearerToken.authorizationHeaderAccessMethod();

            public DefaultAuthorizationCodeFlow() {
                super(new Builder(METHOD, TRANSPORT, JSON_FACTORY, TOKEN_URL, INTERCEPTOR, Pivotal.getClientId(), Pivotal.getAuthorizeUrl()).setScopes(Arrays.asList(Pivotal.getScopes().split(" "))));
            }

            @Override
            public AuthorizationCodeTokenRequest newTokenRequest(final String authorizationCode) {
                final AuthorizationCodeTokenRequest request = super.newTokenRequest(authorizationCode);
                request.setRedirectUri(Pivotal.getRedirectUrl());
                return request;
            }

            @Override
            public AuthorizationCodeRequestUrl newAuthorizationUrl() {
                final AuthorizationCodeRequestUrl requestUrl = super.newAuthorizationUrl();
                requestUrl.setRedirectUri(Pivotal.getRedirectUrl());
                requestUrl.setState(UUID.randomUUID().toString());
                requestUrl.set("access_type", "offline");
                return requestUrl;
            }
        }
    }
}
