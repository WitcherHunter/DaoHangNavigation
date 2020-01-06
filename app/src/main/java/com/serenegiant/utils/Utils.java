package com.serenegiant.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;

import com.serenegiant.AppConfig;
import com.serenegiant.net.DeviceParameter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
/**
 * Created by Administrator on 2017/7/10.
 */

public class Utils {
    private static final String TAG = "FileCopyUtil";


    /**
     * 验证驾校车牌
     */

    public static boolean isCarnumberNO(String carnumber) {
               /*
            车牌号格式：汉字 + A-Z + 5位A-Z或0-9
            （只包括了普通车牌号，教练车和部分部队车等车牌号不包括在内）
             */
               String carnumRegex = "[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{4}[学]{1}";
               return carnumber.matches(carnumRegex);
            }


            /*
            * 验证IP
            * */
        public static boolean isIP(String carnumber) {
               /*
            车牌号格式：汉字 + A-Z + 5位A-Z或0-9
            （只包括了普通车牌号，教练车和部分部队车等车牌号不包括在内）
             */
               String carnumRegex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                       +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                       +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                       +"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
               return carnumber.matches(carnumRegex);
         }

     /**
     * 大陆号码或香港号码均可
     */
    public static boolean isPhoneLegal(String str)throws PatternSyntaxException {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 香港手机号码8位数，5|6|8|9开头+7位任意数
     */
    public static boolean isHKPhoneLegal(String str)throws PatternSyntaxException {
        String regExp = "^(5|6|8|9)\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * param context
     * param msg
     */
    public static void write(Context context, String fileName, String content) {
        if (content == null)
            content = "";

        try {
            FileOutputStream fos = context.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fos.write(content.getBytes());

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(InputStream inStream, String filePath) {
        try {
            int bytesum = 0;
            int byteread = 0;

            FileOutputStream fs = new FileOutputStream(filePath);
            byte[] buffer = new byte[1444];
            int length;
            while ((byteread = inStream.read(buffer)) > 0) {
                bytesum += byteread; // 字节数 文件大小
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }

            fs.close();
            inStream.close();

        } catch (Exception e) {
            System.out.println("写入单个文件操作出错");
            e.printStackTrace();
        }

    }

    /**
     * 读取文本文件
     *
     * param context
     * param fileName
     * @return
     */
    public static String read(Context context, String fileName) {
        try {
            FileInputStream in = context.openFileInput(fileName);
            return readInStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String readInStream(FileInputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, length);
            }

            outStream.close();
            inStream.close();
            return outStream.toString();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    /**
     * 读取文件内容
     *
     * param path
     * @return
     */
    public static String readFile(String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        File file = new File(path);
        if (file.exists()) {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
            return readInStream(fin);
        } else{
            return null;
        }
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }

    /**
     * 向手机写图片
     *
     * param buffer
     * param folder
     * param fileName
     * @return
     */
    public static boolean writeFile(byte[] buffer, String folder,
                                    String fileName) {
        boolean writeSucc = false;

        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);

        String folderPath = "";
        if (sdCardExist) {
            folderPath = Environment.getExternalStorageDirectory()
                    + File.separator + folder + File.separator;
        } else {
            writeSucc = false;
        }

        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File file = new File(folderPath + fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(buffer);
            writeSucc = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return writeSucc;
    }


    /**
     * 向手机写图片
     *
     * param buffer
     * param folder
     * param fileName
     * @return
     */
    public static boolean writeFileCreated(String folder, String fileName, byte[] buffer, int startIndex, int bytesNumber) {
        String pathFile = folder + File.separator + fileName;
        boolean res = false;
        File f = new File(folder);
        if (!f.exists()) {
            f.mkdirs();
        }
        File f2 = new File(pathFile);
        try {
            if (f2.exists()) {
                f2.delete();
            }
            f2.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fs = null;
        BufferedOutputStream bos = null;
        try {
            fs = new FileOutputStream(f2, true);
            bos = new BufferedOutputStream(fs);
            bos.write(buffer, startIndex, bytesNumber);
            res = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
    public static boolean writeFileCreated(String folder, String fileName, byte[] buffer) {
        return writeFileCreated(folder, fileName, buffer, 0, buffer.length);
    }
    /**
     * 根据文件绝对路径获取文件名
     *
     * param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 根据文件的绝对路径获取文件名但不包含扩展名
     *
     * param filePath
     * @return
     */
    public static String getFileNameNoFormat(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return "";
        }
        int point = filePath.lastIndexOf('.');
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1,
                point);
    }

    /**
     * 获取文件扩展名
     *
     * param fileName
     * @return
     */
    public static String getFileFormat(String fileName) {
        if (StringUtils.isEmpty(fileName))
            return "";

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    /**
     * 获取文件大小
     *
     * param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        long size = 0;

        File file = new File(filePath);
        if (file != null && file.exists()) {
            size = file.length();
        }
        return size;
    }

    /**
     * 获取文件大小
     *
     * param size 字节
     * @return
     */
    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";
        java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
        float temp = (float) size / 1024;
        if (temp >= 1024) {
            return df.format(temp / 1024) + "M";
        } else {
            return df.format(temp) + "K";
        }
    }

    /**
     * 转换文件大小
     *
     * param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小
     *
     * param fileS
     * @return B/KB/MB/GB
     */
    public static String formatSpeedSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS <= 0) {
            fileSizeString = "0.00b";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS * 8) + "b";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS * 8 / 1024) + "Kb";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS * 8 / 1048576) + "Mb";
        }
        return fileSizeString;
    }

    /**
     * 获取目录文件大小
     *
     * param dir
     * @return
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 获取目录文件个数
     *
     * param f
     * @return
     */
    public long getFileList(File dir) {
        long count = 0;
        File[] files = dir.listFiles();
        count = files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = count + getFileList(file);// 递归
                count--;
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    /**
     * 检查文件是否存在
     *
     * param name
     * @return
     */
    public static boolean checkFileExists(String name) {
        boolean status;
        if (!name.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + name);
            status = newPath.exists();
        } else {
            status = false;
        }
        return status;
    }

    /**
     * 检查路径是否存在
     *
     * param path
     * @return
     */
    public static boolean checkFilePathExists(String path) {
        return new File(path).exists();
    }

    /**
     * 计算SD卡的剩余空间
     *
     * @return 返回-1，说明没有安装sd卡
     */
    public static long getFreeDiskSpace() {
        String status = Environment.getExternalStorageState();
        long freeSpace = 0;
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                File path = Environment.getExternalStorageDirectory();
                StatFs stat = new StatFs(path.getPath());
                long blockSize = stat.getBlockSize();
                long availableBlocks = stat.getAvailableBlocks();
                freeSpace = availableBlocks * blockSize / 1024;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return -1;
        }
        return (freeSpace);
    }

    /**
     * 新建目录
     *
     * param directoryName
     * @return
     */
    public static boolean createDirectory(String directoryName) {
        boolean status;
        if (!directoryName.equals("")) {
            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + directoryName);
            status = newPath.mkdir();
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 检查是否安装SD卡
     *
     * @return
     */
    public static boolean checkSaveLocationExists() {
        String sDCardStatus = Environment.getExternalStorageState();
        boolean status;
        if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
            status = true;
        } else
            status = false;
        return status;
    }

    /**
     * 删除目录(包括：目录里的所有文件)
     *
     * param fileName
     * @return
     */
    public static boolean deleteDirectory(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isDirectory()) {
                String[] listfile = newPath.list();
                // delete all files within the specified directory and then
                // delete the directory
                try {
                    for (int i = 0; i < listfile.length; i++) {
                        File deletedFile = new File(newPath.toString() + "/"
                                + listfile[i].toString());
                        deletedFile.delete();
                    }
                    newPath.delete();
                    Log.i("DirectoryManager deleteDirectory", fileName);
                    status = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    status = false;
                }

            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除文件
     *
     * param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();

        if (!fileName.equals("")) {

            File path = Environment.getExternalStorageDirectory();
            File newPath = new File(path.toString() + fileName);
            checker.checkDelete(newPath.toString());
            if (newPath.isFile()) {
                try {
                    Log.i("DirectoryManager deleteFile", fileName);
                    newPath.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else
                status = false;
        } else
            status = false;
        return status;
    }

    /**
     * 删除空目录
     * <p>
     * 返回 0代表成功 ,1 代表没有删除权限, 2代表不是空目录,3 代表未知错误
     *
     * @return
     */
    public static int deleteBlankPath(String path) {
        File f = new File(path);
        if (!f.canWrite()) {
            return 1;
        }
        if (f.list() != null && f.list().length > 0) {
            return 2;
        }
        if (f.delete()) {
            return 0;
        }
        return 3;
    }

    /**
     * 重命名
     *
     * param oldName
     * param newName
     * @return
     */
    public static boolean reNamePath(String oldName, String newName) {
        File f = new File(oldName);
        return f.renameTo(new File(newName));
    }

    /**
     * 删除文件
     *
     * param filePath
     */
    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            Log.i("DirectoryManager deleteFile", filePath);
            f.delete();
            return true;
        }
        return false;
    }

    /**
     * 获取SD卡的根目录，末尾带\
     *
     * @return
     */
    public static String getSDRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
    }

    /**
     * 列出root目录下所有子目录
     *
     * param path
     * @return 绝对路径
     */
    public static List<String> listPath(String root) {
        List<String> allDir = new ArrayList<String>();
        SecurityManager checker = new SecurityManager();
        File path = new File(root);
        checker.checkRead(root);
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                if (f.isDirectory()) {
                    allDir.add(f.getAbsolutePath());
                }
            }
        }
        return allDir;
    }

    public enum PathStatus {
        SUCCESS, EXITS, ERROR
    }

    /**
     * 创建目录
     *
     * param path
     */
    public static PathStatus createPath(String newPath) {
        File path = new File(newPath);
        if (path.exists()) {
            return PathStatus.EXITS;
        }
        if (path.mkdir()) {
            return PathStatus.SUCCESS;
        } else {
            return PathStatus.ERROR;
        }
    }

    /**
     * 截取路径名
     *
     * @return
     */
    public static String getPathName(String absolutePath) {
        int start = absolutePath.lastIndexOf(File.separator) + 1;
        int end = absolutePath.length();
        return absolutePath.substring(start, end);
    }

    /**
     * 复制单个文件
     *
     * param oldPath String 原文件路径 如：c:/fqf.txt
     * param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static boolean copyFile(String oldPath, String newPath) {
        try {

            Log.i("MY", "FileCopyUtil.copyFile oldPath" + oldPath);

            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { // 文件不存在时

                Log.i("MY", "FileCopyUtil.copyFile exists");

                InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                return true;
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 复制整个文件夹内容
     *
     * param oldPath String 原文件路径 如：c:/fqf
     * param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static void copyFolder(String oldPath, String newPath) {

        try {
            (new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath
                            + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {// 如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            System.out.println("复制整个文件夹内容操作出错");
            e.printStackTrace();

        }
    }


    public static class XMLManage {
        private static String pathDirectory;
        private static String pathFile;
        private static String pathFile2;

        /*
         读取参数配置数据
         */
        public static HashMap<String, String> getParameterData() {
            HashMap<String, String> map = new HashMap<String, String>();

            pathDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"cwj600";
            pathFile = pathDirectory + "/DeviceParameter.xml";
            pathFile2 = pathDirectory + "/DeviceParameter.xml";
            try {
                createParameter();
                File f = new File(pathFile);
                InputStream in = new FileInputStream(f);
                XmlPullParser pullParser = Xml.newPullParser();
                pullParser.setInput(in, "UTF-8"); //为Pull解释器设置要解析的XML数据
                int event = pullParser.getEventType();
                String keyName = "";
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_DOCUMENT:
                            break;
                        case XmlPullParser.START_TAG:
                            keyName = pullParser.getName();
                            break;
                        case XmlPullParser.TEXT:
                            String txt = pullParser.getText();
                            if (!keyName.equals("") && !txt.equals("")) {
                                map.put(keyName, txt);
                                if (keyName.equals("ServerIP")) {
                                    DeviceParameter.setLoginIP(txt);
                                } else if (keyName.equals("ServerPort")) {
                                    int port = Integer.valueOf(txt);
                                    DeviceParameter.setLoginPort(port);
                                } else if (keyName.equals("LoginPlate")) {
                                    DeviceParameter.setLoginPlate(txt);
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            keyName = "";
                            break;

                    }
                    event = pullParser.next();
                }
            } catch (Exception ex) {
                Log.e(TAG, "读取xml数据异常:" + ex.getMessage());
            }
            return map;
        }

        /**
         * 保存数据到xml文件中
         *
         * param persons
         * param out
         * @throws Exception
         */
        public static void setParameterData(HashMap<String, String> map) {
            try {
                HashMap<String, String> tempMap = map;
                File f2 = new File(pathFile);
                if (f2.exists() == false)
                    return;
                // 1.得到DOM解析器的工厂实例
                // 2.从DOM工厂里获取DOM解析器
                // 3.解析XML文档，得到document，即DOM树
                //调用 DocumentBuilderFactory.newInstance() 方法得到创建 DOM 解析器的工厂
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                //调用工厂对象的 newDocumentBuilder方法得到 DOM 解析器对象
                DocumentBuilder builder =  builderFactory.newDocumentBuilder();
                //通过文件的方式获取Document对象
                File file = new File(pathFile2);
                Document doc = builder.parse(file);
//                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pathFile2);
                NodeList list = doc.getElementsByTagName("DeviceParameter").item(0).getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Element brandElement = (Element) list.item(i);
                    String name = brandElement.getTagName();
                    boolean flag = false;
                    for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
                        Map.Entry entry = (Map.Entry) iter.next();
                        Object key = entry.getKey().toString();
                        Object val = entry.getValue().toString();
                        if (name.equals(key)) {
                            brandElement.setTextContent(val.toString());
                            flag = true;
                        }
                    }
                    if (flag) {
                        //  map.remove(name);
                    }
                }
                //保存xml文件
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                DOMSource domSource = new DOMSource(doc);
                //设置编码类型
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                StreamResult result = new StreamResult(new FileOutputStream(pathFile));
                //把DOM树转换为xml文件
                transformer.transform(domSource, result);

                for (Iterator iter = tempMap.entrySet().iterator(); iter.hasNext(); ) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String key = entry.getKey().toString();
                    String val = entry.getValue().toString();

                    if (val == "")
                        continue;

                    if (key.equals("ServerIP")) {
                        DeviceParameter.setLoginIP(val);
                    } else if (key.equals("ServerPort")) {
                        int port = Integer.valueOf(val);
                        DeviceParameter.setLoginPort(port);
                    } else if (key.equals("LoginPlate")) {
                        DeviceParameter.setLoginPlate(val);
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "读取xml数据异常:" + ex.getMessage());
            }
        }

        //创建文件
        //openFileOutput()方法的第一参数用于指定文件名称，不能包含路径分隔符“/” ，
        // 如果文件不存在，Android 会自动创建它。
        // 创建的文件保存在/data/data/<package name>/files目录
        private static void createParameter() {
            try {
                File f = new File(pathDirectory);
                if (f.exists() == false) {
                    f.mkdirs();//注意是mkdirs()有个s 这样可以创建多重目录。
                }

                File f2 = new File(pathFile);
                if (f2.exists() == false) {
                    OutputStream os = new FileOutputStream(f2);
                    OutputStreamWriter osw = new OutputStreamWriter(os);
                    String txt = writeXml();
                    osw.write(txt);
                    osw.close();
                    os.close();
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "创建xml异常:" + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "创建xml异常:" + e.getMessage());
            }
        }

        /**
         * <xml version=”1.0″ encoding=”UTF-8″ standalone=”yes”?>
         * <DeviceParameter>
         * <loginIP>192.168.1.1<loginIP/>
         * <loginPort>0000<loginPort/>
         * <DeviceParameter/>
         */
        private static String writeXml() {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            try {
                serializer.setOutput(writer);
                // <?xml version=”1.0″ encoding=”UTF-8″ standalone=”yes”?>
                serializer.startDocument("UTF-8", true);
                serializer.startTag("", "DeviceParameter");
                //服务器IP地址
                serializer.startTag("", "ServerIP");
                serializer.text("203.86.28.33");
                serializer.endTag("", "ServerIP");
                //服务器端口
                serializer.startTag("", "ServerPort");
                serializer.text("6379");
                serializer.endTag("", "ServerPort");
                //登录ID
                serializer.startTag("", "LoginID");
                serializer.text("13602531165");
                serializer.endTag("", "LoginID");
                //登录车牌
                serializer.startTag("", "LoginPlate");
                serializer.text("粤BH123学");
                serializer.endTag("", "LoginPlate");

                serializer.endTag("", "DeviceParameter");
                serializer.endDocument();
                return writer.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    public static void saveRunningLog(String info) {
        String pathDirectory = AppConfig.HAMBOBO_DEBUG_INFON_ADDR;
        String pathFile = pathDirectory + File.separator + "Hambobo.txt";

        File f = new File(pathDirectory);
        if (!f.exists()) {
            f.mkdirs();
        }
        File f2 = new File(pathFile);
        if (!f2.exists()) {
            try {
                f2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fs = null;
        BufferedOutputStream bos = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:");
        try {
            fs = new FileOutputStream(f2, true);
            bos = new BufferedOutputStream(fs);
            String ds = sdf.format(new Date());
            bos.write(ds.getBytes());
            bos.write(info.getBytes());
            bos.write("\n".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static boolean copyBigDataToSD(String srcPath, String drtPath)
    {
        InputStream myInput;
        try {
            OutputStream myOutput = new FileOutputStream(drtPath);
            myInput = new FileInputStream(srcPath);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while (length > 0) {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
            return true;
        }catch (IOException ex){
            ex.printStackTrace();
            return false;
        }
    }
    /**
     * 循环录像，当内存卡容量少于300M时，自动删除视频列表里面的第一个文件
     */
    private void recycleRecordVideo() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            // 取得sdcard文件路径
            StatFs statfs = new StatFs(path.getPath());
            // 获取block的SIZE
            long blocSize = statfs.getBlockSize();
            // 获取BLOCK数量
            long totalBlocks = statfs.getBlockCount();
            // 己使用的Block的数量
            long availaBlock = statfs.getAvailableBlocks();
            // 获取当前可用内存容量，单位：MB
            long sd = availaBlock * blocSize / 1024 / 1024;
            if (sd < 300) {
                String filepath = (Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/video/");
                File file = new File(filepath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                File[] files = file.listFiles();
                if (files.length > 0) {
                    String childFile[] = file.list();
                    String dele = (filepath + childFile[0]);
                    File file2 = new File(dele);
                    file2.delete();
                }
            }
        } else if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_REMOVED)) {
        }
    }


    /**
     * 检查权限
     * @param context
     * @param permission
     * @return
     */
    public static boolean checkPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED;
    }

}
