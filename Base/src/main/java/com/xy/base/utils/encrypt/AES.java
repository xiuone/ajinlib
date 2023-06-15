package com.xy.base.utils.encrypt;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * author       : wangyalei
 * time         : 19-11-12
 * description  :
 * history      :
 */
public class AES {
    private static final String ALGORITHM = "AES";
    private static final String ALGORITHM_TRANSFORMATION = "AES/CBC/PKCS7Padding";
    private static final int KEY_SIZE = 128;
    private static final int CACHE_SIZE = 1024;

    /**
     * 生成随机的aes密钥
     * @return
     */
    public static String getRandomKey() {
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(KEY_SIZE);
            //要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            return BASE64.encode(b);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密 跟decrypt有一致关系，是互逆的过程
     * @param sourceStr 数据需要getbyte获取字节数组
     * @param key		key是base64编码后得到的，需要再用base64解码
     * @return			返回的数据是base64编码后的
     * @throws Exception
     */
    public static String encrypt(String sourceStr, String key) throws Exception {
        byte[] data = sourceStr.getBytes(StandardCharsets.UTF_8);

        Key k = toKey(BASE64.decode(key));
        byte[] raw = k.getEncoded();

        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        IvParameterSpec ivspec = new IvParameterSpec(raw);

        Cipher cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
        return BASE64.encode(cipher.doFinal(data));
    }

    /**
     * cbc加密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptCbc(byte[] data, String key) throws Exception {
        Key k = toKey(BASE64.decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        IvParameterSpec ivspec = new IvParameterSpec(raw);
         Cipher cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * @param sourceStr 是base64编码得到的，所以该数据要先解码
     * @param key		key是base64后得到的，需要再用base64解码
     * @return			因为加密时候的数据源是getbyte得到的，所以解密后的数据直接生成
     * @throws Exception
     */
    public static String decrypt(String sourceStr, String key) throws Exception {
        return decrypt(sourceStr, key, BASE64.decode(key));
    }

    public static String decrypt(String sourceStr, String key, byte[] iv) throws Exception {
        try {
            byte[] encryptedArr = BASE64.decode(sourceStr);

            Cipher cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);
            byte[] keyByte = BASE64.decode(key);
            SecretKeySpec keyspec = new SecretKeySpec(keyByte, ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            byte[] original = cipher.doFinal(encryptedArr);
            String originalString = new String(original);
            return originalString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <p>
     * 文件加密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static boolean encryptFile(String key, String sourceFilePath, String destFilePath) throws Exception{
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        if (sourceFile.exists() && sourceFile.isFile()) {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            destFile.createNewFile();
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destFile);
            Key k = toKey(BASE64.decode(key));
            byte[] raw = k.getEncoded();
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
            IvParameterSpec ivspec = new IvParameterSpec(raw);

            Cipher cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            CipherInputStream cin = new CipherInputStream(in, cipher);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = cin.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            cin.close();
            in.close();
            return true;
        }
        return false;
    }

    /**
     * <p>
     * 解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(BASE64.decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        return cipher.doFinal(data);
    }

    /**
     * <p>
     * CBC解密
     * </p>
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptCbc(byte[] data, String key) throws Exception {
        Key k = toKey(BASE64.decode(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        IvParameterSpec ivspec = new IvParameterSpec(raw);
        Cipher cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
        return cipher.doFinal(data);
    }


    /**
     * <p>
     * 文件解密
     * </p>
     *
     * @param key
     * @param sourceFilePath
     * @param destFilePath
     * @throws Exception
     */
    public static boolean decryptFile(String key, String sourceFilePath, String destFilePath){
        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destFilePath);
        FileInputStream in = null;
        FileOutputStream out = null;
        try{
            if (sourceFile.exists() && sourceFile.isFile()) {
                if (!destFile.getParentFile().exists()) {
                    destFile.getParentFile().mkdirs();
                }
                destFile.createNewFile();
                in = new FileInputStream(sourceFile);
                out = new FileOutputStream(destFile);
                Key k = toKey(BASE64.decode(key));
                byte[] raw = k.getEncoded();
                SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
                IvParameterSpec ivspec = new IvParameterSpec(raw);

                Cipher cipher = Cipher.getInstance(ALGORITHM_TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
                CipherOutputStream cout = new CipherOutputStream(out, cipher);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    cout.write(cache, 0, nRead);
                    cout.flush();
                }
                cout.close();
                out.close();
                in.close();
                return true;
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * <p>
     * 转换密钥
     * </p>
     *
     * @param key
     * @return
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }
}
