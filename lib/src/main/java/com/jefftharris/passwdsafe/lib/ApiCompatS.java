/*
 * Copyright (©) 2022 Jeff Harris <jefftharris@gmail.com>
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.jefftharris.passwdsafe.lib;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.os.Build;

/**
 * The ApiCompatS class contains helper methods that are usable on S and higher
 */
@TargetApi(Build.VERSION_CODES.S)
class ApiCompatS
{
    /// The mutable flag for a pending intent
    public static final int PENDING_INTENT_FLAG_MUTABLE =
            PendingIntent.FLAG_MUTABLE;
}
