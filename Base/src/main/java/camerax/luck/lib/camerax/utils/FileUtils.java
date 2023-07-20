package camerax.luck.lib.camerax.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import camerax.luck.lib.camerax.type.CameraImageFormat;
import camerax.luck.lib.camerax.type.CameraVideoFormat;

/**
 * @author：luck
 * @date：2021/11/29 8:17 下午
 * @describe：FileUtils
 */
public class FileUtils {

    public static final String POSTFIX = ".jpeg";

    public static final String POST_VIDEO = ".mp4";

    /**
     * @param context
     * @param chooseMode
     * @param format
     * @return
     */
    public static File createCameraFile(Context context, int chooseMode, String format) {
        return createOutFile(context, chooseMode, format);
    }
    /**
     * 创建文件
     *
     * @param ctx                上下文
     * @param chooseMode         选择模式
     * @param format             文件格式
     * @return
     */
    private static File createOutFile(Context ctx, int chooseMode, String format) {
        Context context = ctx.getApplicationContext();
        File folderDir;
        File rootDir;
        if (TextUtils.equals(Environment.MEDIA_MOUNTED, Environment.getExternalStorageState())) {
            rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            folderDir = new File(rootDir.getAbsolutePath() + File.separator + "Camera" + File.separator);
        } else {
            rootDir = getRootDirFile(context, chooseMode);
            folderDir = new File(rootDir.getAbsolutePath() + File.separator);
        }
        if (!rootDir.exists()) {
            rootDir.mkdirs();
        }
        if (!folderDir.exists()) {
            folderDir.mkdirs();
        }

        if (chooseMode == CameraUtils.TYPE_VIDEO) {
            String newFileVideoName = DateUtils.INSTANCE.getCreateFileName("VID_") + POST_VIDEO;
            return new File(folderDir, newFileVideoName);
        }
        String suffix = TextUtils.isEmpty(format) ? POSTFIX : format;
        String newFileImageName = DateUtils.INSTANCE.getCreateFileName("IMG_") + suffix;
        return new File(folderDir, newFileImageName);
    }

    /**
     * 文件根目录
     *
     * @param context
     * @param type
     * @return
     */
    private static File getRootDirFile(Context context, int type) {
        if (type == CameraUtils.TYPE_VIDEO) {
            return context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        }
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }


    /**
     * 创建一个临时路径，主要是解决华为手机放弃拍照后会弹出相册图片被删除的提示
     *
     * @param isVideo
     * @return
     */
    public static File createTempFile(Context context, boolean isVideo) {
        File externalFilesDir = context.getExternalFilesDir("");
        File tempCameraFile = new File(externalFilesDir.getAbsolutePath(), ".TemporaryCamera");
        if (!tempCameraFile.exists()) {
            tempCameraFile.mkdirs();
        }
        String fileName = System.currentTimeMillis() + (isVideo ? CameraVideoFormat.VIDEO.getType() : CameraImageFormat.JPEG.getType());
        return new File(tempCameraFile.getAbsolutePath(), fileName);
    }

    /**
     * is content://
     *
     * @param url
     * @return
     */
    public static boolean isContent(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.startsWith("content://");
    }


    /**
     * 文件复制
     *
     * @param context
     * @param originalPath
     * @param newPath
     * @return
     */
    public static boolean copyPath(Context context, String originalPath, String newPath) {
        FileOutputStream fos = null;
        ByteArrayOutputStream stream = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(originalPath, options);
            options.inSampleSize = CameraUtils.INSTANCE.computeSize(options.outWidth, options.outHeight);
            options.inJustDecodeBounds = false;

            Bitmap newBitmap = CameraUtils.INSTANCE.toHorizontalMirror(BitmapFactory.decodeFile(originalPath, options));
            stream = new ByteArrayOutputStream();
            newBitmap.compress(newBitmap.hasAlpha() ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 90, stream);
            newBitmap.recycle();
            fos = new FileOutputStream(newPath);
            fos.write(stream.toByteArray());
            fos.flush();
            FileUtils.deleteFile(context, originalPath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.close(fos);
            FileUtils.close(stream);
        }
        return false;
    }

    /**
     * 复制文件
     *
     * @param is 文件输入流
     * @param os 文件输出流
     * @return
     */
    public static boolean writeFileFromIS(final InputStream is, final OutputStream os) {
        OutputStream osBuffer = null;
        BufferedInputStream isBuffer = null;
        try {
            isBuffer = new BufferedInputStream(is);
            osBuffer = new BufferedOutputStream(os);
            byte[] data = new byte[1024];
            for (int len; (len = isBuffer.read(data)) != -1; ) {
                os.write(data, 0, len);
            }
            os.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close(isBuffer);
            close(osBuffer);
        }
    }

    /**
     * delete camera PATH
     *
     * @param context Context
     * @param path    path
     */
    public static void deleteFile(Context context, String path) {
        try {
            if (isContent(path)) {
                context.getContentResolver().delete(Uri.parse(path), null, null);
            } else {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void close(@Nullable Closeable c) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c instanceof Closeable) {
            try {
                c.close();
            } catch (Exception e) {
                // silence
            }
        }
    }
}
