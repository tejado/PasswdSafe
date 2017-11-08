/*
 * Copyright (©) 2016 Jeff Harris <jefftharris@gmail.com>
 * All rights reserved. Use of the code is allowed under the
 * Artistic License 2.0 terms, as specified in the LICENSE file
 * distributed with this code, or available from
 * http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.jefftharris.passwdsafe.sync.owncloud;

import android.content.Context;
import android.util.Log;

import com.jefftharris.passwdsafe.lib.PasswdSafeUtil;
import com.jefftharris.passwdsafe.sync.lib.AbstractLocalToRemoteSyncOper;
import com.jefftharris.passwdsafe.sync.lib.DbFile;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.ReadRemoteFileOperation;
import com.owncloud.android.lib.resources.files.RemoteFile;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import java.io.File;

/**
 * An ownCloud sync operation to sync a local file to a remote one
 */
public class OwncloudLocalToRemoteOper extends
        AbstractLocalToRemoteSyncOper<OwnCloudClient>
{
    private static final String TAG = "OwncloudLocalToRemoteOp";

    /** Constructor */
    public OwncloudLocalToRemoteOper(DbFile file)
    {
        super(file, TAG);
    }

    /* (non-Javadoc)
     * @see com.jefftharris.passwdsafe.sync.lib.AbstractSyncOper#doOper(java.lang.Object, android.content.Context)
     */
    @Override
    public void doOper(OwnCloudClient providerClient, Context ctx)
            throws Exception
    {
        PasswdSafeUtil.dbginfo(TAG, "syncLocalToRemote %s", itsFile);

        File tmpFile = null;
        try {
            File uploadFile;
            String remotePath;
            if (itsFile.itsLocalFile != null) {
                uploadFile = ctx.getFileStreamPath(itsFile.itsLocalFile);
                setLocalFile(uploadFile);
                if (isInsert()) {
                    remotePath =
                            OwncloudSyncer.createRemoteIdFromLocal(itsFile);
                } else {
                    remotePath = itsFile.itsRemoteId;
                }
            } else {
                tmpFile = File.createTempFile("passwd", ".psafe3");
                tmpFile.deleteOnExit();
                uploadFile = tmpFile;
                remotePath = OwncloudSyncer.createRemoteIdFromLocal(itsFile);
            }

            UploadRemoteFileOperation oper = new UploadRemoteFileOperation(
                    uploadFile.getAbsolutePath(),
                    remotePath, PasswdSafeUtil.MIME_TYPE_PSAFE3);
            RemoteOperationResult res = oper.execute(providerClient);
            OwncloudSyncer.checkOperationResult(res, ctx);

            ReadRemoteFileOperation fileOper =
                    new ReadRemoteFileOperation(remotePath);
            res = fileOper.execute(providerClient);
            OwncloudSyncer.checkOperationResult(res, ctx);
            setUpdatedFile(
                    new OwncloudProviderFile((RemoteFile)res.getData().get(0)));
        } finally {
            if (tmpFile != null) {
                if (!tmpFile.delete()) {
                    Log.e(TAG, "Can't delete temp file " + tmpFile);
                }
            }
        }
    }
}
