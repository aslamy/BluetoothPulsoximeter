package com.benjamin.bluetoothpulse;

import android.content.Context;
import android.os.Environment;


import java.io.File;
import java.io.IOException;

/**
 * Created by Benjamin on 2015-12-07.
 */
public class FileResource {

    private Context context;

    public FileResource(Context context){
        this.context = context ;
    }

    public File createFileResource(String fileName)
    {
        String path;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            path = context.getFilesDir().getPath();
        }

        File file = new File(path, fileName);

        if (file.exists())
            file.delete();

        try {
            file.createNewFile();
        } catch (IOException e) {

        }

        return file;
    }

    public File getFileResource(String fileName)
    {
        String path;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            path = Environment.getExternalStorageDirectory().getPath();
        } else {
            path = context.getFilesDir().getPath();
        }

        File file = new File(path, fileName);

        return file;
    }
}
