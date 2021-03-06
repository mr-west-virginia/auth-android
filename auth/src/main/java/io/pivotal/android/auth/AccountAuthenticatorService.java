/*
 * Copyright (C) 2014 Pivotal Software, Inc. All rights reserved.
 */
package io.pivotal.android.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccountAuthenticatorService extends Service {
	
	@Override
	public IBinder onBind(final Intent intent) {
        final AccountAuthenticator accountAuthenticator = new AccountAuthenticator(this);
		return accountAuthenticator.getIBinder();
	}
}