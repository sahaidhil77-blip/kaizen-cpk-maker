package com.kaizen.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;

@CapacitorPlugin(name = "Opener")
public class OpenerPlugin extends Plugin {

    // path relatif dari folder Documents, mis. "Kaizen" atau "Kaizen/hasil"
    @PluginMethod
    public void openFolder(PluginCall call) {
        String path = call.getString("path", "Kaizen");
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), path);
        if (!dir.exists()) dir.mkdirs();

        String docId = "primary:Documents/" + path;
        Uri uri = Uri.parse("content://com.android.externalstorage.documents/document/" + Uri.encode(docId));

        Intent view = new Intent(Intent.ACTION_VIEW);
        view.setDataAndType(uri, "vnd.android.document/directory");
        view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            Intent chooser = Intent.createChooser(view, "Buka folder dengan");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(chooser);
            call.resolve();
        } catch (ActivityNotFoundException e) {
            call.reject("Tidak ada aplikasi yang bisa membuka folder");
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }

    // path relatif dari folder Documents, mis. "Kaizen/hasil.zip"
    @PluginMethod
    public void openFile(PluginCall call) {
        String path = call.getString("path");
        if (path == null) { call.reject("path kosong"); return; }
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), path);
        if (!f.exists()) { call.reject("File tidak ditemukan"); return; }

        try {
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);
            String ext = MimeTypeMap.getFileExtensionFromUrl(f.getName());
            String mime = ext == null ? null : MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());
            if (mime == null) mime = "*/*";

            Intent view = new Intent(Intent.ACTION_VIEW);
            view.setDataAndType(uri, mime);
            view.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent chooser = Intent.createChooser(view, "Buka dengan");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(chooser);
            call.resolve();
        } catch (ActivityNotFoundException e) {
            call.reject("Tidak ada aplikasi yang bisa membuka file ini");
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }
}
