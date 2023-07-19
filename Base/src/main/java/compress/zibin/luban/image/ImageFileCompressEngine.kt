package compress.zibin.luban.image;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

import compress.zibin.luban.Luban;
import compress.zibin.luban.OnCompressListener;
import picture.luck.picture.lib.engine.CompressFileEngine;
import picture.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;

public class ImageFileCompressEngine implements CompressFileEngine {
    @Override
    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
        Luban.with(context)
                .load(source)
                .ignoreBy(100)
                .setCompressListener( new OnCompressListener() {
                    @Override
                    public void onSuccess(String source, File compressFile) {
                        call.onCallback(source, compressFile.getAbsolutePath());
                    }

                    @Override
                    public void onError(String source, Throwable e) {
                        call.onCallback(source, null);
                    }
        }).launch();
    }
}
