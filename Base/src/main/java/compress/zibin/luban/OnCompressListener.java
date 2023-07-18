package compress.zibin.luban;

import java.io.File;

public interface OnCompressListener {

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(String source, File compressFile);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(String source, Throwable e);
}
