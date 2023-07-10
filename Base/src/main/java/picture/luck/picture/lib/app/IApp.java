package picture.luck.picture.lib.app;

import android.content.Context;

import picture.luck.picture.lib.engine.PictureSelectorEngine;

/**
 * @author：luck
 * @date：2019-12-03 15:14
 * @describe：IApp
 */
public interface IApp {
    /**
     * Application
     *
     * @return
     */
    Context getAppContext();

    /**
     * PictureSelectorEngine
     *
     * @return
     */
    PictureSelectorEngine getPictureSelectorEngine();
}
