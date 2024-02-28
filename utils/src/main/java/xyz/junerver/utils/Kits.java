package xyz.junerver.utils;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Kits {

    public static class Package {
        /**
         * 获取版本号
         *
         * @param context
         * @return
         */
        public static int getVersionCode(Context context) {
            PackageManager pManager = context.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                packageInfo = pManager.getPackageInfo(context.getPackageName(), 0);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return packageInfo.versionCode;
        }

        /**
         * 获取当前版本
         *
         * @param context
         * @return
         */
        public static String getVersionName(Context context) {
            PackageManager pManager = context.getPackageManager();
            PackageInfo packageInfo = null;
            try {
                packageInfo = pManager.getPackageInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return packageInfo.versionName;
        }

        /**
         * 安装App
         *
         * @param context
         * @param filePath
         * @return
         */
        public static boolean installNormal(Context context, String filePath) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            java.io.File file = new java.io.File(filePath);
            if (file == null || !file.exists() || !file.isFile() || file.length() <= 0) {
                return false;
            }

            i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }

        /**
         * 卸载App
         *
         * @param context
         * @param packageName
         * @return
         */
        public static boolean uninstallNormal(Context context, String packageName) {
            if (packageName == null || packageName.length() == 0) {
                return false;
            }

            Intent i = new Intent(Intent.ACTION_DELETE, Uri.parse(new StringBuilder().append("package:")
                    .append(packageName).toString()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }

        /**
         * 判断是否是系统App
         *
         * @param context
         * @param packageName 包名
         * @return
         */
        public static boolean isSystemApplication(Context context, String packageName) {
            if (context == null) {
                return false;
            }
            PackageManager packageManager = context.getPackageManager();
            if (packageManager == null || packageName == null || packageName.length() == 0) {
                return false;
            }

            try {
                ApplicationInfo app = packageManager.getApplicationInfo(packageName, 0);
                return (app != null && (app.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return false;
        }

        /**
         * 判断某个包名是否运行在顶层
         *
         * @param context
         * @param packageName
         * @return
         */
        public static Boolean isTopActivity(Context context, String packageName) {
            if (context == null || TextUtils.isEmpty(packageName)) {
                return null;
            }

            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo == null || tasksInfo.isEmpty()) {
                return null;
            }
            try {
                return packageName.equals(tasksInfo.get(0).topActivity.getPackageName());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * 获取Meta-Data
         *
         * @param context
         * @param key
         * @return
         */
        public static String getAppMetaData(Context context, String key) {
            if (context == null || TextUtils.isEmpty(key)) {
                return null;
            }
            String resultData = null;
            try {
                PackageManager packageManager = context.getPackageManager();
                if (packageManager != null) {
                    ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                    if (applicationInfo != null) {
                        if (applicationInfo.metaData != null) {
                            resultData = applicationInfo.metaData.getString(key);
                        }
                    }

                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return resultData;
        }

        /**
         * 判断当前应用是否运行在后台
         *
         * @param context
         * @return
         */
        public static boolean isApplicationInBackground(Context context) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
            if (taskList != null && !taskList.isEmpty()) {
                ComponentName topActivity = taskList.get(0).topActivity;
                if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 获取应用程序名称
         */
        public static synchronized String getAppName(Context context) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(
                        context.getPackageName(), 0);
                int labelRes = packageInfo.applicationInfo.labelRes;
                return context.getResources().getString(labelRes);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }


    public static class Dimens {
        public static float dpToPx(Context context, float dp) {
            return dp * context.getResources().getDisplayMetrics().density;
        }

        public static float pxToDp(Context context, float px) {
            return px / context.getResources().getDisplayMetrics().density;
        }

        public static int dpToPxInt(Context context, float dp) {
            return (int) (dpToPx(context, dp) + 0.5f);
        }

        public static int pxToDpCeilInt(Context context, float px) {
            return (int) (pxToDp(context, px) + 0.5f);
        }
    }


    public static class Random {
        public static final String NUMBERS_AND_LETTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String NUMBERS = "0123456789";
        public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";

        public static String getRandomNumbersAndLetters(int length) {
            return getRandom(NUMBERS_AND_LETTERS, length);
        }

        public static String getRandomNumbers(int length) {
            return getRandom(NUMBERS, length);
        }

        public static String getRandomLetters(int length) {
            return getRandom(LETTERS, length);
        }

        public static String getRandomCapitalLetters(int length) {
            return getRandom(CAPITAL_LETTERS, length);
        }

        public static String getRandomLowerCaseLetters(int length) {
            return getRandom(LOWER_CASE_LETTERS, length);
        }

        public static String getRandom(String source, int length) {
            return TextUtils.isEmpty(source) ? null : getRandom(source.toCharArray(), length);
        }

        public static String getRandom(char[] sourceChar, int length) {
            if (sourceChar == null || sourceChar.length == 0 || length < 0) {
                return null;
            }

            StringBuilder str = new StringBuilder(length);
            java.util.Random random = new java.util.Random();
            for (int i = 0; i < length; i++) {
                str.append(sourceChar[random.nextInt(sourceChar.length)]);
            }
            return str.toString();
        }

        public static int getRandom(int max) {
            return getRandom(0, max);
        }

        public static int getRandom(int min, int max) {
            if (min > max) {
                return 0;
            }
            if (min == max) {
                return min;
            }
            return min + new java.util.Random().nextInt(max - min);
        }
    }

    public static class File {
        public final static String FILE_EXTENSION_SEPARATOR = ".";

        /**
         * read file
         *
         * @param filePath
         * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
         * @return if file not exist, return null, else return content of file
         * @throws RuntimeException if an error occurs while operator BufferedReader
         */
        public static StringBuilder readFile(String filePath, String charsetName) {
            java.io.File file = new java.io.File(filePath);
            StringBuilder fileContent = new StringBuilder("");
            if (file == null || !file.isFile()) {
                return null;
            }

            BufferedReader reader = null;
            try {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
                reader = new BufferedReader(is);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (!fileContent.toString().equals("")) {
                        fileContent.append("\r\n");
                    }
                    fileContent.append(line);
                }
                return fileContent;
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            } finally {
                IO.close(reader);
            }
        }

        /**
         * write file
         *
         * @param filePath
         * @param content
         * @param append   is append, if true, write to the end of file, else clear content of file and write into it
         * @return return false if content is empty, true otherwise
         * @throws RuntimeException if an error occurs while operator FileWriter
         */
        public static boolean writeFile(String filePath, String content, boolean append) {
            if (TextUtils.isEmpty(content)) {
                return false;
            }

            FileWriter fileWriter = null;
            try {
                makeDirs(filePath);
                fileWriter = new FileWriter(filePath, append);
                fileWriter.write(content);
                return true;
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            } finally {
                IO.close(fileWriter);
            }
        }

        /**
         * write file
         *
         * @param filePath
         * @param contentList
         * @param append      is append, if true, write to the end of file, else clear content of file and write into it
         * @return return false if contentList is empty, true otherwise
         * @throws RuntimeException if an error occurs while operator FileWriter
         */
        public static boolean writeFile(String filePath, List<String> contentList, boolean append) {
            if (contentList == null || contentList.isEmpty()) {
                return false;
            }

            FileWriter fileWriter = null;
            try {
                makeDirs(filePath);
                fileWriter = new FileWriter(filePath, append);
                int i = 0;
                for (String line : contentList) {
                    if (i++ > 0) {
                        fileWriter.write("\r\n");
                    }
                    fileWriter.write(line);
                }
                return true;
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            } finally {
                IO.close(fileWriter);
            }
        }

        /**
         * write file, the string will be written to the begin of the file
         *
         * @param filePath
         * @param content
         * @return
         */
        public static boolean writeFile(String filePath, String content) {
            return writeFile(filePath, content, false);
        }

        /**
         * write file, the string list will be written to the begin of the file
         *
         * @param filePath
         * @param contentList
         * @return
         */
        public static boolean writeFile(String filePath, List<String> contentList) {
            return writeFile(filePath, contentList, false);
        }

        /**
         * write file, the bytes will be written to the begin of the file
         *
         * @param filePath
         * @param stream
         * @return
         * @see {@link #writeFile(String, InputStream, boolean)}
         */
        public static boolean writeFile(String filePath, InputStream stream) {
            return writeFile(filePath, stream, false);
        }

        /**
         * write file
         *
         * @param stream the input stream
         * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
         * @return return true
         * @throws RuntimeException if an error occurs while operator FileOutputStream
         */
        public static boolean writeFile(String filePath, InputStream stream, boolean append) {
            return writeFile(filePath != null ? new java.io.File(filePath) : null, stream, append);
        }

        /**
         * write file, the bytes will be written to the begin of the file
         *
         * @param file
         * @param stream
         * @return
         * @see {@link #writeFile(java.io.File, InputStream, boolean)}
         */
        public static boolean writeFile(java.io.File file, InputStream stream) {
            return writeFile(file, stream, false);
        }

        /**
         * write file
         *
         * @param file   the file to be opened for writing.
         * @param stream the input stream
         * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
         * @return return true
         * @throws RuntimeException if an error occurs while operator FileOutputStream
         */
        public static boolean writeFile(java.io.File file, InputStream stream, boolean append) {
            OutputStream o = null;
            try {
                makeDirs(file.getAbsolutePath());
                o = new FileOutputStream(file, append);
                byte data[] = new byte[1024];
                int length = -1;
                while ((length = stream.read(data)) != -1) {
                    o.write(data, 0, length);
                }
                o.flush();
                return true;
            } catch (FileNotFoundException e) {
                throw new RuntimeException("FileNotFoundException occurred. ", e);
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            } finally {
                IO.close(o);
                IO.close(stream);
            }
        }

        /**
         * move file
         *
         * @param sourceFilePath
         * @param destFilePath
         */
        public static void moveFile(String sourceFilePath, String destFilePath) {
            if (TextUtils.isEmpty(sourceFilePath) || TextUtils.isEmpty(destFilePath)) {
                throw new RuntimeException("Both sourceFilePath and destFilePath cannot be null.");
            }
            moveFile(new java.io.File(sourceFilePath), new java.io.File(destFilePath));
        }

        /**
         * move file
         *
         * @param srcFile
         * @param destFile
         */
        public static void moveFile(java.io.File srcFile, java.io.File destFile) {
            boolean rename = srcFile.renameTo(destFile);
            if (!rename) {
                copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
                deleteFile(srcFile.getAbsolutePath());
            }
        }

        /**
         * copy file
         *
         * @param sourceFilePath
         * @param destFilePath
         * @return
         * @throws RuntimeException if an error occurs while operator FileOutputStream
         */
        public static boolean copyFile(String sourceFilePath, String destFilePath) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(sourceFilePath);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("FileNotFoundException occurred. ", e);
            }
            return writeFile(destFilePath, inputStream);
        }

        /**
         * read file to string list, a element of list is a line
         *
         * @param filePath
         * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
         * @return if file not exist, return null, else return content of file
         * @throws RuntimeException if an error occurs while operator BufferedReader
         */
        public static List<String> readFileToList(String filePath, String charsetName) {
            java.io.File file = new java.io.File(filePath);
            List<String> fileContent = new ArrayList<String>();
            if (file == null || !file.isFile()) {
                return null;
            }

            BufferedReader reader = null;
            try {
                InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
                reader = new BufferedReader(is);
                String line = null;
                while ((line = reader.readLine()) != null) {
                    fileContent.add(line);
                }
                return fileContent;
            } catch (IOException e) {
                throw new RuntimeException("IOException occurred. ", e);
            } finally {
                IO.close(reader);
            }
        }

        /**
         * get file name from path, not include suffix
         * <p/>
         * <pre>
         *      getFileNameWithoutExtension(null)               =   null
         *      getFileNameWithoutExtension("")                 =   ""
         *      getFileNameWithoutExtension("   ")              =   "   "
         *      getFileNameWithoutExtension("abc")              =   "abc"
         *      getFileNameWithoutExtension("a.mp3")            =   "a"
         *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
         *      getFileNameWithoutExtension("c:\\")              =   ""
         *      getFileNameWithoutExtension("c:\\a")             =   "a"
         *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
         *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
         *      getFileNameWithoutExtension("/home/admin")      =   "admin"
         *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
         * </pre>
         *
         * @param filePath
         * @return file name from path, not include suffix
         * @see
         */
        public static String getFileNameWithoutExtension(String filePath) {
            if (TextUtils.isEmpty(filePath)) {
                return filePath;
            }

            int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            int filePosi = filePath.lastIndexOf(java.io.File.separator);
            if (filePosi == -1) {
                return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
            }
            if (extenPosi == -1) {
                return filePath.substring(filePosi + 1);
            }
            return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
        }

        /**
         * get file name from path, include suffix
         * <p/>
         * <pre>
         *      getFileName(null)               =   null
         *      getFileName("")                 =   ""
         *      getFileName("   ")              =   "   "
         *      getFileName("a.mp3")            =   "a.mp3"
         *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
         *      getFileName("abc")              =   "abc"
         *      getFileName("c:\\")              =   ""
         *      getFileName("c:\\a")             =   "a"
         *      getFileName("c:\\a.b")           =   "a.b"
         *      getFileName("c:a.txt\\a")        =   "a"
         *      getFileName("/home/admin")      =   "admin"
         *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
         * </pre>
         *
         * @param filePath
         * @return file name from path, include suffix
         */
        public static String getFileName(String filePath) {
            if (TextUtils.isEmpty(filePath)) {
                return filePath;
            }

            int filePosi = filePath.lastIndexOf(java.io.File.separator);
            return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
        }

        /**
         * get folder name from path
         * <p/>
         * <pre>
         *      getFolderName(null)               =   null
         *      getFolderName("")                 =   ""
         *      getFolderName("   ")              =   ""
         *      getFolderName("a.mp3")            =   ""
         *      getFolderName("a.b.rmvb")         =   ""
         *      getFolderName("abc")              =   ""
         *      getFolderName("c:\\")              =   "c:"
         *      getFolderName("c:\\a")             =   "c:"
         *      getFolderName("c:\\a.b")           =   "c:"
         *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
         *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
         *      getFolderName("/home/admin")      =   "/home"
         *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
         * </pre>
         *
         * @param filePath
         * @return
         */
        public static String getFolderName(String filePath) {

            if (TextUtils.isEmpty(filePath)) {
                return filePath;
            }

            int filePosi = filePath.lastIndexOf(java.io.File.separator);
            return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
        }

        /**
         * get suffix of file from path
         * <p/>
         * <pre>
         *      getFileExtension(null)               =   ""
         *      getFileExtension("")                 =   ""
         *      getFileExtension("   ")              =   "   "
         *      getFileExtension("a.mp3")            =   "mp3"
         *      getFileExtension("a.b.rmvb")         =   "rmvb"
         *      getFileExtension("abc")              =   ""
         *      getFileExtension("c:\\")              =   ""
         *      getFileExtension("c:\\a")             =   ""
         *      getFileExtension("c:\\a.b")           =   "b"
         *      getFileExtension("c:a.txt\\a")        =   ""
         *      getFileExtension("/home/admin")      =   ""
         *      getFileExtension("/home/admin/a.txt/b")  =   ""
         *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
         * </pre>
         *
         * @param filePath
         * @return
         */
        public static String getFileExtension(String filePath) {
            if (TextUtils.isEmpty(filePath)) {
                return filePath;
            }

            int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
            int filePosi = filePath.lastIndexOf(java.io.File.separator);
            if (extenPosi == -1) {
                return "";
            }
            return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
        }

        /**
         * Creates the directory named by the trailing filename of this file, including the complete directory path required
         * to create this directory. <br/>
         * <br/>
         * <ul>
         * <strong>Attentions:</strong>
         * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
         * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
         * </ul>
         *
         * @param filePath
         * @return true if the necessary directories have been created or the target directory already exists, false one of
         * the directories can not be created.
         * <ul>
         * <li>if {@link File#getFolderName(String)} return null, return false</li>
         * <li>if target directory already exists, return true</li>
         * </ul>
         */
        public static boolean makeDirs(String filePath) {
            String folderName = getFolderName(filePath);
            if (TextUtils.isEmpty(folderName)) {
                return false;
            }

            java.io.File folder = new java.io.File(folderName);
            return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
        }

        /**
         * @param filePath
         * @return
         * @see #makeDirs(String)
         */
        public static boolean makeFolders(String filePath) {
            return makeDirs(filePath);
        }

        /**
         * Indicates if this file represents a file on the underlying file system.
         *
         * @param filePath
         * @return
         */
        public static boolean isFileExist(String filePath) {
            if (TextUtils.isEmpty(filePath)) {
                return false;
            }

            java.io.File file = new java.io.File(filePath);
            return (file.exists() && file.isFile());
        }

        /**
         * Indicates if this file represents a directory on the underlying file system.
         *
         * @param directoryPath
         * @return
         */
        public static boolean isFolderExist(String directoryPath) {
            if (TextUtils.isEmpty(directoryPath)) {
                return false;
            }

            java.io.File dire = new java.io.File(directoryPath);
            return (dire.exists() && dire.isDirectory());
        }

        /**
         * delete file or directory
         * <ul>
         * <li>if path is null or empty, return true</li>
         * <li>if path not exist, return true</li>
         * <li>if path exist, delete recursion. return true</li>
         * <ul>
         *
         * @param path
         * @return
         */
        public static boolean deleteFile(String path) {
            if (TextUtils.isEmpty(path)) {
                return true;
            }

            java.io.File file = new java.io.File(path);
            if (!file.exists()) {
                return true;
            }
            if (file.isFile()) {
                return file.delete();
            }
            if (!file.isDirectory()) {
                return false;
            }
            for (java.io.File f : file.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                } else if (f.isDirectory()) {
                    deleteFile(f.getAbsolutePath());
                }
            }
            return file.delete();
        }

        /**
         * get file size
         * <ul>
         * <li>if path is null or empty, return -1</li>
         * <li>if path exist and it is a file, return file size, else return -1</li>
         * <ul>
         *
         * @param path
         * @return returns the length of this file in bytes. returns -1 if the file does not exist.
         */
        public static long getFileSize(String path) {
            if (TextUtils.isEmpty(path)) {
                return -1;
            }

            java.io.File file = new java.io.File(path);
            return (file.exists() && file.isFile() ? file.length() : -1);
        }

    }

    public static class IO {
        /**
         * 关闭流
         *
         * @param closeable
         */
        public static void close(Closeable closeable) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    public static class Date {
        private static SimpleDateFormat yyyy = new SimpleDateFormat("yyyy", Locale.getDefault());
        private static SimpleDateFormat yy = new SimpleDateFormat("yy", Locale.getDefault());
        private static SimpleDateFormat m = new SimpleDateFormat("MM", Locale.getDefault());
        private static SimpleDateFormat d = new SimpleDateFormat("dd", Locale.getDefault());
        private static SimpleDateFormat md = new SimpleDateFormat("MM-dd", Locale.getDefault());
        private static SimpleDateFormat md1 = new SimpleDateFormat("MM/dd", Locale.getDefault());
        private static SimpleDateFormat ym = new SimpleDateFormat("yyyy/MM", Locale.getDefault());
        private static SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        private static SimpleDateFormat ymdDot = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        private static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        private static SimpleDateFormat ymdhmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        private static SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        private static SimpleDateFormat hm = new SimpleDateFormat("HH:mm", Locale.getDefault());
        private static SimpleDateFormat mdhm = new SimpleDateFormat("MM月dd日 HH:mm", Locale.getDefault());
        private static SimpleDateFormat mdhmLink = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        private static SimpleDateFormat ymdhmChinese = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
        private static SimpleDateFormat englishddmmyyyy = new SimpleDateFormat("dd MM,yyyy", Locale.getDefault());

        /**
         * 获取当前时间戳
         * @return
         */
        public static long getCurrentTime() {
            return Calendar.getInstance().getTime().getTime();
        }

        /**
         * 获取前n天日期、后n天日期
         *
         * @return
         */
        public static long getDay(long time,boolean isBack) {
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(time);
            if(isBack){
                date.set(Calendar.DATE, date.get(Calendar.DATE) - 1);
            }else{
                date.set(Calendar.DATE, date.get(Calendar.DATE) + 1);
            }
            return date.getTimeInMillis();
        }

        /**
         * 获取前n天日期、后n天日期
         *
         * @return
         */
        public static long getWeek(long time,boolean isBack) {
            java.util.Date date1 = new java.util.Date(time);
            Calendar date = Calendar.getInstance();
            date.setTime(date1);
            if(isBack){
                date.set(Calendar.DATE, date.get(Calendar.DATE) - 7);
            }else{
                date.set(Calendar.DATE, date.get(Calendar.DATE) + 7);
            }
            return date.getTimeInMillis();
        }

        /**
         * 获取前n月 后n月
         *
         * @return
         */
        public static long getMonth(long time,boolean isBack) {
            java.util.Date date1 = new java.util.Date(time);
            Calendar date = Calendar.getInstance();
            date.setTime(date1);
            if(isBack){
                date.add(Calendar.MONTH, -1);
            }else {
                date.add(Calendar.MONTH, +1);
            }
            return date.getTimeInMillis();
        }

        public static List<String> getDateList(long time,int dayCount){
            List<String> list = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

            if(dayCount == 1){
                list.add(simpleDateFormat.format(time));
                return list;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);


            for (int i = 0; i < dayCount; i++) {
                cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
                list.add(simpleDateFormat.format(cal.getTimeInMillis()));
            }
            return list;
        }

        public static int getMonthDayCount(long time){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.DATE, 1);//把日期设置为当月第一天
            cal.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
            int maxDate = cal.get(Calendar.DATE);

            return maxDate;
        }

        public static String getDayFirstLast(long time){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(time);
            cal2.set(Calendar.HOUR_OF_DAY, 24);
            cal2.set(Calendar.SECOND, 0);
            cal2.set(Calendar.MINUTE, 0);
            cal2.set(Calendar.MILLISECOND, 0);

            return getLongTimeToString(cal.getTimeInMillis(),cal2.getTimeInMillis());
        }

        public static String getWeekFirstLast(long time){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(cal.getTimeInMillis());
            cal2.add(Calendar.DAY_OF_WEEK, 7);

            return getLongTimeToString(cal.getTimeInMillis(),cal2.getTimeInMillis());
        }


        public static String getMonthFirstLast(long time){
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time);
            cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));

            Calendar cal2 = Calendar.getInstance();
            cal2.setTimeInMillis(time);
            cal2.set(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            cal2.set(Calendar.DAY_OF_MONTH, cal2.getActualMaximum(Calendar.DAY_OF_MONTH));
            cal2.set(Calendar.HOUR_OF_DAY, 24);

            return getLongTimeToString(cal.getTimeInMillis(),cal2.getTimeInMillis());
        }

        public static String getLongTimeToString(long firstTime,long lastTime){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            StringBuffer sb = new StringBuffer();
            sb.append(simpleDateFormat.format(firstTime));
            sb.append(", ");
            sb.append(simpleDateFormat.format(lastTime));

            return sb.toString();
        }


        public static String getEnglishDdMmY(long timeInMills) {
            return englishddmmyyyy.format(new java.util.Date(timeInMills));
        }

        /**
         * 年月日[2015-07-28]
         *
         * @param timeInMills
         * @return
         */
        public static String getYmd(long timeInMills) {
            return ymd.format(new java.util.Date(timeInMills));
        }

        /**
         * 年月日[2015-07-28]
         *
         * @param timeInMills
         * @return
         */
        public static String getYm(long timeInMills) {
            return ym.format(new java.util.Date(timeInMills));
        }

        /***
         * @param timeInMills
         * @return
         */
        public static String getYyyy(long timeInMills) {
            return yyyy.format(new java.util.Date(timeInMills));
        }

        /***
         * @param timeInMills
         * @return
         */
        public static String getYy(long timeInMills) {
            return yy.format(new java.util.Date(timeInMills));
        }

        /**
         * 年月日[2015.07.28]
         *
         * @param timeInMills
         * @return
         */
        public static String getYmdDot(long timeInMills) {
            return ymdDot.format(new java.util.Date(timeInMills));
        }

        public static String getYmdhms(long timeInMills) {
            return ymdhms.format(new java.util.Date(timeInMills));
        }

        public static String getYmdhmsS(long timeInMills) {
            return ymdhmss.format(new java.util.Date(timeInMills));
        }

        public static String getYmdhm(long timeInMills) {
            return ymdhm.format(new java.util.Date(timeInMills));
        }

        public static String getHm(long timeInMills) {
            return hm.format(new java.util.Date(timeInMills));
        }

        public static String getMd(long timeInMills) {
            return md.format(new java.util.Date(timeInMills));
        }

        public static String getMd1(long timeInMills) {
            return md1.format(new java.util.Date(timeInMills));
        }

        public static String getMdhm(long timeInMills) {
            return mdhm.format(new java.util.Date(timeInMills));
        }

        public static String getMdhmLink(long timeInMills) {
            return mdhmLink.format(new java.util.Date(timeInMills));
        }

        public static String getM(long timeInMills) {
            return m.format(new java.util.Date(timeInMills));
        }

        public static String getD(long timeInMills) {
            return d.format(new java.util.Date(timeInMills));
        }

        public static String getYmdhmChinese(long timeInMills) {
            return ymdhmChinese.format(new java.util.Date(timeInMills));
        }

        /**
         * 判断是否今年
         * @param timeInMills
         * @return
         */
        public static boolean isThisYear(long timeInMills) {
            String dest = getYyyy(timeInMills);
            String now = getYyyy(Calendar.getInstance().getTimeInMillis());
            return dest.equals(now);
        }

        /**
         * 是否是今天
         *
         * @param timeInMills
         * @return
         */
        public static boolean isToday(long timeInMills) {
            String dest = getYmd(timeInMills);
            String now = getYmd(Calendar.getInstance().getTimeInMillis());
            return dest.equals(now);
        }

        /**
         * 是否是同一天
         *
         * @param aMills
         * @param bMills
         * @return
         */
        public static boolean isSameDay(long aMills, long bMills) {
            String aDay = getYmd(aMills);
            String bDay = getYmd(bMills);
            return aDay.equals(bDay);
        }

        /**
         * 获取年份
         *
         * @param mills
         * @return
         */
        public static int getYear(long mills) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mills);
            return calendar.get(Calendar.YEAR);
        }

        /**
         * 获取月份
         *
         * @param mills
         * @return
         */
        public static int getMonth(long mills) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mills);
            return calendar.get(Calendar.MONTH) + 1;
        }


        /**
         * 获取月份的天数
         *
         * @param mills
         * @return
         */
        public static int getDaysInMonth(long mills) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mills);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);

            switch (month) {
                case Calendar.JANUARY:
                case Calendar.MARCH:
                case Calendar.MAY:
                case Calendar.JULY:
                case Calendar.AUGUST:
                case Calendar.OCTOBER:
                case Calendar.DECEMBER:
                    return 31;
                case Calendar.APRIL:
                case Calendar.JUNE:
                case Calendar.SEPTEMBER:
                case Calendar.NOVEMBER:
                    return 30;
                case Calendar.FEBRUARY:
                    return (year % 4 == 0) ? 29 : 28;
                default:
                    throw new IllegalArgumentException("Invalid Month");
            }
        }


        /**
         * 获取星期,0-周日,1-周一，2-周二，3-周三，4-周四，5-周五，6-周六
         *
         * @param mills
         * @return
         */
        public static int getWeek(long mills) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mills);

            return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        }

        /**
         * 获取当月第一天的时间（毫秒值）
         *
         * @param mills
         * @return
         */
        public static long getFirstOfMonth(long mills) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mills);
            calendar.set(Calendar.DAY_OF_MONTH, 1);

            return calendar.getTimeInMillis();
        }

        /**
         * 判断2个时间大小
         * HH:mm 格式
         *
         * @param startTime
         * @param endTime
         * @return
         */
        public static int getTimeCompareSize(String startTime, String endTime) {
            int i = -1;
            try {
                java.util.Date date1 = hm.parse(startTime);//开始时间
                java.util.Date date2 = hm.parse(endTime);//结束时间
                // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
                if (date2.getTime() < date1.getTime()) {
                    i = 1;
                } else if (date2.getTime() == date1.getTime()) {
                    i = 2;
                } else if (date2.getTime() > date1.getTime()) {
                    i = 3;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return i;
        }


        /**
         * 计算相差的小时
         *
         * @param starTime
         * @param endTime
         * @return
         */
        public static float getTimeDifferenceHour(String starTime, String endTime) {
            float timeString = 0;

            try {
                java.util.Date parse = ymdhms.parse(starTime);
                java.util.Date parse1 = ymdhms.parse(endTime);

                long diff = parse1.getTime() - parse.getTime();
                String string = Long.toString(diff);

                float parseFloat = Float.parseFloat(string);

                timeString = parseFloat / (60 * 60 * 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return timeString;

        }

        /**
         * 计算相差的分钟
         *
         * @param starTime
         * @param endTime
         * @return
         */
        public static float getTimeDifferenceMinute(long starTime, long endTime) {
            float timeString = 0;
            try {

                long diff = endTime - starTime;
                String string = Long.toString(diff);

                float parseFloat = Float.parseFloat(string);

                timeString = parseFloat / (60 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return timeString;

        }

        /**
         * 获取前n天日期、后n天日期
         *
         * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
         * @return
         */
        public static long getOldDate(long time, int distanceDay) {
            java.util.Date date1 = new java.util.Date(time);
            Calendar date = Calendar.getInstance();
            date.setTime(date1);
            date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);

            return date.getTime().getTime();
        }

        /**
         * 获取前n月 后n月
         *
         * @param distanceDay 前几天 如获取前7天日期则传-7即可；如果后7天则传7
         * @return
         */
        public static long getOldMonth(long time, int distanceDay) {
            java.util.Date date1 = new java.util.Date(time);
            Calendar date = Calendar.getInstance();
            date.setTime(date1);
            date.set(Calendar.MONTH, date.get(Calendar.MONTH) + distanceDay);

            return date.getTime().getTime();
        }

        /**
         * 获取当前时间的 周一
         * @return
         */
        public static long getCurrTimeWeekFirst(long time) {
            Calendar cal = Calendar.getInstance();
            java.util.Date d = new java.util.Date(time);
            cal.setTime(d);

            //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

            int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值

            return cal.getTime().getTime();
        }

        /**
         * 获取当前时间的周一 和周日
         * @param time
         * @return
         */
        public static String getWeekFirstAndLastDay(long time) {
            StringBuffer sb = new StringBuffer();
            Calendar cal = Calendar.getInstance();
            java.util.Date d = new java.util.Date(time);
            cal.setTime(d);

            //判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            cal.setFirstDayOfWeek(Calendar.MONDAY);//设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一

            int day = cal.get(Calendar.DAY_OF_WEEK);//获得当前日期是一个星期的第几天
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);//根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            sb.append(md1.format(cal.getTime()));
            sb.append("-");
            cal.add(Calendar.DATE, 6);
            sb.append(md1.format(cal.getTime()));
            return sb.toString();
        }

    }


    public static class NetWork {
        public static final String NETWORK_TYPE_WIFI = "wifi";
        public static final String NETWORK_TYPE_3G = "3G";
        public static final String NETWORK_TYPE_4G = "4G";
        public static final String NETWORK_TYPE_2G = "2G";
        public static final String NETWORK_TYPE_WAP = "wap";
        public static final String NETWORK_TYPE_UNKNOWN = "unknown";
        public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

        public static boolean checkNetworkIfAvailable(Context context) {
            if (context == null) return false;
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            } else {
                NetworkInfo netWorkInfo = connectivityManager.getActiveNetworkInfo();
                return (netWorkInfo != null && netWorkInfo.isAvailable());
            }
        }

        public static int getNetworkType(Context context) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
            return networkInfo == null ? -1 : networkInfo.getType();
        }

        public static String getNetworkTypeName(Context context) {
            String strNetworkType = "";

            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected())
            {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                {
                    strNetworkType = NETWORK_TYPE_WIFI;
                }
                else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
                {
                    String _strSubTypeName = networkInfo.getSubtypeName();
                    // TD-SCDMA   networkType is 17
                    int networkType = networkInfo.getSubtype();
                    switch (networkType) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                            strNetworkType = NETWORK_TYPE_2G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            strNetworkType = NETWORK_TYPE_3G;
                            break;
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            strNetworkType = NETWORK_TYPE_4G;
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000"))
                            {
                                strNetworkType = NETWORK_TYPE_3G;
                            }
                            else
                            {
                                strNetworkType = _strSubTypeName;
                            }

                            break;
                    }
                }
            }

            return strNetworkType;
        }

        @SuppressLint("MissingPermission")
        private static boolean isFastMobileNetwork(Context context) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager == null) {
                return false;
            }

            switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false;
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true;
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false;
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return true;
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return true;
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return true;
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return false;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return true;
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return false;
                default:
                    return false;
            }
        }

        public static String getOperator(Context context) {


            String ProvidersName = "无运营商";
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED) {
                String IMSI = telephonyManager.getSubscriberId();
                Log.i("qweqwes", "运营商代码" + IMSI);
                if (IMSI != null) {
                    if (IMSI.startsWith("46000") || IMSI.startsWith("46002") || IMSI.startsWith("46007")) {
                        ProvidersName = "中国移动";
                    } else if (IMSI.startsWith("46001")  || IMSI.startsWith("46006")) {
                        ProvidersName = "中国联通";
                    } else if (IMSI.startsWith("46003")) {
                        ProvidersName = "中国电信";
                    }
                    return ProvidersName;
                } else {
                    return ProvidersName;
                }
            }else {
                return ProvidersName;
            }
        }



    }


    public static class Empty {


        public static boolean check(Object obj) {
            return obj == null;
        }

        public static boolean check(List list) {
            return list == null || list.isEmpty();
        }

        public static boolean check(Object[] array) {
            return array == null || array.length == 0;
        }

        public static boolean check(String str) {
            return str == null || "".equals(str);
        }

        public static boolean check(Map map) {
            return map == null || map.isEmpty();
        }

        public static boolean check(Set set) {
            return set == null || set.isEmpty();
        }


    }

    public static class AppInfo{

        /**
         * 获取设备id
         * @param context
         * @return
         */
        @SuppressLint({"MissingPermission", "HardwareIds"})
        public static String getDeviceId(Context context) {
            String deviceId;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                assert telephonyManager != null;
                deviceId = telephonyManager.getDeviceId();
            } catch (Exception e) {
                deviceId = "";
            }
            return deviceId;
        }

        /**
         * 获取手机型号
         * @return
         */
        public static String getPhoneModel(){
            return TextUtils.isEmpty(Build.MODEL)?"unknown":Build.MODEL;
        }

        /**
         * 获取手机品牌
         * @return
         */
        public static String getPhoneBrand(){
            return TextUtils.isEmpty(Build.BRAND)?"unknown":Build.BRAND;
        }

        /**
         * 获取手机版本
         * @return
         */
        public static String getPhoneVersion(){
            return TextUtils.isEmpty(Build.VERSION.RELEASE)?"unknown":Build.VERSION.RELEASE;
        }

    }

}

