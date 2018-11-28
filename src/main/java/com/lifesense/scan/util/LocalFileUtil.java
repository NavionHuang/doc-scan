package com.lifesense.scan.util;

import org.junit.Assert;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by 赵春定 on 2017/7/17.
 */
public class LocalFileUtil {

    private LocalFileUtil() {

    }


    public static List<String> allFile(String path, FilenameFilter filter) throws IOException {
        Assert.assertNotNull("path is Muster not null", path);
        path.replaceAll("//", "/").replaceAll("\\\\", "/");
        if (!path.startsWith("/")) path = "/" + path;
        File file = new File(path);
        List<String> fileList = new ArrayList<String>();
        File[] files = file.listFiles(filter);
        for (File nextFile : files) {
            if (nextFile.isDirectory()) {
                String nextPath = nextFile.getPath();
                List<String> list = allFile(nextPath, filter);
                fileList.addAll(list);
            } else {
                fileList.add(nextFile.getPath());
            }
        }
        return fileList;
    }


    public static List<String> directories(String path,FilenameFilter filter) throws IOException {
        path.replaceAll("//", "/").replaceAll("\\\\", "/");
        if (!path.startsWith("/")) path = "/" + path;
        File file = new File(path);
        List<String> fileList = new ArrayList<String>();
        if (!file.exists()) return fileList;
        if (file.isDirectory()) {
            String[] list = file.list(filter);//(filter);
           return Arrays.asList(list);
        }
        return fileList;
    }


    public static InputStream getFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return new FileInputStream(file);
        }
        return null;
    }

    public static void main(String[] args) {
        //String root = "E:/ftp/log\\wbs06\\log\\csv\\toupload\\20170717";
        String root = "E:\\ftp\\wbs05\\log\\csv\\toupload\\20171015";
        try {
            List<String> list = allFile(root, new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (!Pattern.matches("wbs[0-9]{2}_transtek_log_[0-9a-z]{2}_[0-9]+_[0-9]{8}_[0-9]{6}\\.csv", name)) {
                        return false;
                    }
                    return true;
                }
            });
            for (String str : list) {
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
