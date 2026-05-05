/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.server.notification;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.UserHandle;
import android.permission.IPermissionManager;

import com.android.server.pm.permission.PermissionManagerServiceInternal;
import com.android.server.uri.UriGrantsManagerInternal;

/**
 * NotificationManagerService helper for querying/setting the app-level notification permission
 */
public final class PermissionHelper {
    private static final String TAG = "PermissionHelper";
    private final PermissionManagerServiceInternal mPmi;
    private final IPackageManager mPackageManager;
    private final IPermissionManager mPermManager;
    public PermissionHelper(PermissionManagerServiceInternal pmi, IPackageManager packageManager,
            IPermissionManager permManager) {
        mPmi = pmi;
        mPackageManager = packageManager;
        mPermManager = permManager;
    }

    static void grantUriPermission(final UriGrantsManagerInternal ugmInternal, Uri uri,
            int sourceUid) {
        if (uri == null || !ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) return;
        Binder.withCleanCallingIdentity(() -> {
            // This will throw a SecurityException if the caller can't grant.
            ugmInternal.checkGrantUriPermission(sourceUid, null,
                    ContentProvider.getUriWithoutUserId(uri),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                    ContentProvider.getUserIdFromUri(uri, UserHandle.getUserId(sourceUid)));
        });
    }
}
