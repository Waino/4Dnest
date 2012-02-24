package org.fourdnest.androidclient.test.comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

public class MemoryCardInitializer{
    
    /**
     * Static method to initialize the emulator memory card with a picture file kuva.jpg
     * @param context current context
     * @throws IOException
     */
    public static void initialize(Context context) throws IOException {
        addFile(context, "kuva.jpg");
        addFile(context, "sound1.wav");
        addFile(context, "sound2.wav");
        addFile(context, "music.mp3");
    }
    /**
     * 
     * @param context
     * @param name name of the file in project assets
     * @throws IOException
     */
    private static void addFile(Context context, String name) throws IOException {
        InputStream is = context.getAssets().open(name);
        BufferedInputStream bufin = new BufferedInputStream(is);
        File root = Environment.getExternalStorageDirectory();
        FileOutputStream os = new FileOutputStream(new File(root, name));
        BufferedOutputStream bufout = new BufferedOutputStream(os);
        int c;
        while ((c = bufin.read()) != -1) {
            bufout.write(c);
        }
        bufout.close();
        bufin.close();
    }
    
}
