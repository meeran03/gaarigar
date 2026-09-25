package com.gianteyes.gaarigarapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class GetFile {
    public static File getFile(final Context context, final Uri uri) {
        final File destinationFilename = new File(context.getFilesDir().getPath() + File.separatorChar + GetFile.queryName(context, uri));

        try (final InputStream ins = context.getContentResolver().openInputStream(uri)) {
            GetFile.createFileFromStream(ins, destinationFilename);
        } catch (final Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
        return destinationFilename;
    }

    public static void createFileFromStream(final InputStream ins, final File destination) {
        try (final OutputStream os = new FileOutputStream(destination)) {
            final byte[] buffer = new byte[4096];
            int length;
            while ((length = ins.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
        } catch (final Exception ex) {
            Log.e("Save File", ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static String queryName(final Context context, final Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (final Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    final int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
