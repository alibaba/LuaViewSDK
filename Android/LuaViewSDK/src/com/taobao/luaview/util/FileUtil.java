/*
 * Created by LuaView.
 * Copyright (c) 2017, Alibaba Group. All rights reserved.
 *
 * This source code is licensed under the MIT.
 * For the full copyright and license information,please view the LICENSE file in the root directory of this source tree.
 */

package com.taobao.luaview.util;

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 文件操作类
 *
 * @author song
 * @date 15/11/9
 */
public class FileUtil {

    /**
     * is a file path contains folder path
     *
     * @param filePath
     * @param folderPath
     * @return
     */
    public static boolean isContainsFolderPath(final String filePath, final String folderPath) {//TODO ../../目录处理
        if (filePath != null && folderPath != null) {//filePath本身是folder，并且包含folderPath
            if (folderPath.charAt(folderPath.length() - 1) == '/') {//本身是路径
                return filePath.startsWith(folderPath);
            } else {//非路径的话需要判断路径
                return filePath.startsWith(folderPath + "/");
            }
        }
        return false;
    }

    /**
     * 判断文件路径是否是Folder
     *
     * @param filePath
     * @return
     */
    public boolean isFolder(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            return file.exists() && file.isDirectory();
        }
        return false;
    }

    /**
     * 判断文件路径是否是简单的文件
     *
     * @param filePath
     * @return
     */
    public boolean isFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            return file.exists() && file.isFile();
        }
        return false;
    }

    /**
     * build a file path
     *
     * @param basePath
     * @param nameOrPath
     * @return
     */
    public static String buildPath(final String basePath, final String nameOrPath) {
        if (!TextUtils.isEmpty(basePath)) {
            return new StringBuffer().append(basePath).append(basePath.endsWith(File.separator) ? "" : File.separator).append(nameOrPath).toString();
        } else {
            return nameOrPath;
        }
    }

    /**
     * 是否给定的名称是以postfix结尾的名字
     *
     * @param fileName
     * @param posfix
     * @return
     */
    public static boolean isSuffix(final String fileName, final String posfix) {
        return !TextUtils.isEmpty(fileName) && posfix != null && fileName.endsWith(posfix);
    }

    /**
     * 是否有后缀
     *
     * @param fileName
     * @return
     */
    public static boolean hasPostfix(final String fileName) {
        return fileName != null && fileName.lastIndexOf('.') != -1;
    }

    /**
     * 去除文件名称的前缀
     *
     * @param fileName
     * @param prefix
     * @return
     */
    public static String removePrefix(final String fileName, final String prefix) {
        if (prefix != null && fileName != null && fileName.startsWith(prefix)) {
            return fileName.substring(prefix.length());
        }
        return fileName;
    }

    /**
     * 去掉后缀
     *
     * @param fileName
     * @return
     */
    public static String removePostfix(final String fileName) {
        if (fileName != null && fileName.lastIndexOf('.') != -1) {
            return fileName.substring(0, fileName.lastIndexOf('.'));
        }
        return fileName;
    }

    /**
     * 得到文件夹路径
     *
     * @param filePath
     * @return
     */
    public static String getFolderPath(final String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                return file.getParent();
            } else {
                return file.getPath();
            }
        } else if (filePath.lastIndexOf(File.separatorChar) != -1) {
            return filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
        }
        return "";
    }

    /**
     * 得到Asset的目录路径
     *
     * @param assetFilePath
     * @return
     */
    public static String getAssetFolderPath(final String assetFilePath) {
        if (assetFilePath != null && assetFilePath.lastIndexOf(File.separatorChar) != -1) {
            return assetFilePath.substring(0, assetFilePath.lastIndexOf(File.separatorChar));
        }
        return "";
    }

    /**
     * get file name of given path
     *
     * @param nameOrPath
     * @return
     */
    public static String getFileName(final String nameOrPath) {
        if (nameOrPath != null) {
            int index = nameOrPath.lastIndexOf('/');
            if (index != -1) {
                return nameOrPath.substring(index + 1);
            }
        }
        return nameOrPath;
    }


    /**
     * get filepath
     *
     * @param filepath
     * @return
     */
    public static String getCanonicalPath(String filepath) {
        if (filepath != null) {
            if (filepath.contains("../")) {
                try {
                    return new File(filepath).getCanonicalPath();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                return filepath;
            }
        }
        return null;
    }

    /**
     * 不包含父路径，有父路径的话则去掉父路径
     *
     * @param nameOrPath
     * @return
     */
    public static String getSecurityFileName(final String nameOrPath) {
        if (nameOrPath != null) {
            boolean isNotSecurity = nameOrPath.contains("../");
            if (isNotSecurity) {
                int index = nameOrPath.lastIndexOf("../");
                if (index != -1) {
                    return nameOrPath.substring(index + 4);
                }
            }
        }
        return nameOrPath;
    }

    /**
     * crate file with given path and file name
     *
     * @param fullpath
     * @param fullpath
     * @return
     */
    public static File createFile(final String fullpath) {
        File file = new File(fullpath);
        if (file.exists()) {
            return file;
        } else {
            File parent = file.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            String fileName = file.getName();
            return new File(parent, fileName);
        }
    }

    /**
     * crate file with given path and file name
     *
     * @param path
     * @param fileName
     * @return
     */
    public static File createFile(final String path, final String fileName) {
        File directory = new File(path);
        directory.mkdirs();
        return new File(directory, fileName);
    }

    /**
     * read bytes of given f
     *
     * @param f
     * @return
     */
    public static byte[] readBytes(File f) {
        if (f != null && f.exists() && f.isFile()) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(f);
                return IOUtil.toBytes(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * read bytes of given f
     *
     * @param f
     * @return
     */
    public static byte[] fastReadBytes(File f) {
        if (f != null && f.exists() && f.isFile()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(f);

                MappedByteBuffer buffer = inputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, f.length());
                byte[] result = new byte[(int) f.length()];
                buffer.get(result);
                return result;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * save data to a file
     *
     * @param path file path with file name
     * @param data data to saved
     */
    public static boolean fastSave(final String path, final byte[] data) {
        if (!TextUtils.isEmpty(path) && data != null && data.length > 0) {
            FileOutputStream out = null;
            try {
                File destFile = createFile(path);
                out = new FileOutputStream(destFile);
                out.write(data);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * save data to a file
     *
     * @param path file path with file name
     * @param data data to saved
     */
    public static boolean save(final String path, final byte[] data) {
        if (!TextUtils.isEmpty(path) && data != null && data.length > 0) {
            FileOutputStream out = null;
            try {
                File destFile = createFile(path);
                out = new FileOutputStream(destFile);
                out.write(data);
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * open a file
     *
     * @param filePath
     * @return
     */
    public static InputStream open(final String filePath) {
        try {
            if (!TextUtils.isEmpty(filePath)) {
                return new FileInputStream(filePath);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * is file exists
     *
     * @param filePath
     * @return
     */
    public static boolean exists(final String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            return new File(filePath).exists();
        }
        return false;
    }

    /**
     * copy a input stream to given filepath
     *
     * @param input
     * @param filePath
     * @return
     */
    public static boolean copy(final InputStream input, final String filePath) {
        final int bufSize = 8 * 1024;// or other buffer size
        boolean result = false;
        File file = FileUtil.createFile(filePath);
        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(file), bufSize);
            byte[] buffer = new byte[bufSize];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            try {
                if (output != null) {
                    output.flush();
                    output.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * delete all files
     *
     * @param filePath
     */
    public static void delete(final String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            try {
                delete(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * delete a file
     *
     * @param file
     */
    public static void delete(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null) {
                    for (File child : children) {
                        delete(child);
                    }
                }
                file.delete();
            } else {
                file.delete();
            }
        }
    }
    //------------------------------------sdcard operations-----------------------------------------

    /**
     * is sdacard writeable
     *
     * @return
     */
    public static boolean isSdcardWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * is sccard readable
     *
     * @return
     */
    public static boolean isSdcardReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * get sdcard path
     * 使用该类函数，某些手机系统或者杀毒软件会将应用识别为恶意软件，所以该类函数慎用
     *
     * @return
     */
    @Deprecated
    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * create file
     *
     * @param filepath  Absolute file path
     * @param recursion whether create parent directory neccesary or not
     * @return
     * @throws IOException
     */
    public static boolean createFile(String filepath, boolean recursion) throws IOException {
        boolean result = false;
        File f = new File(filepath);
        if (!f.exists()) {
            try {
                result = f.createNewFile();
            } catch (IOException e) {
                if (!recursion) {
                    throw e;
                }
                File parent = f.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                try {
                    result = f.createNewFile();
                } catch (IOException e1) {
                    throw e1;
                }
            }
        }
        return result;
    }
}
