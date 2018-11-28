package com.lifesense.scan.controller;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.lifesense.scan.domain.ScanResult;
import com.lifesense.scan.service.IScanService;
import com.lifesense.scan.util.FtpUtil;
import com.lifesense.scan.util.LocalFileUtil;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by 赵春定 on 2017/6/2.
 */
@Service
public class FtpTaskExcutor implements Runnable {
    @Autowired
    private IScanService scanService;
    private int finish;
    private List<String> paths = new ArrayList<String>();
    FtpUtil ftpUtil = new FtpUtil();
    private int index;
    private String no;
    private String line;

    private String root;

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public void add(String... obj) {
        paths.clear();
        if (obj != null && obj.length > 0) {
            for (String path : obj) {
                paths.add(path);
            }
        }
    }

    @Override
    public void run() {
        if (paths.size() > 0) {
            if (root != null && !"".equals(root)) {
                localRun();
            } else {
                ftpRun();
            }
        } else {
            finish = 0;
        }
    }

    private void localRun() {
        finish = 1;
        index = 0;
        try {
            for (String path : paths) {
                List<String> allFile = LocalFileUtil.allFile(root+path, new FileFilter());
                scanService.batchAddScanFile(allFile);
                List<String> unScanFile = scanService.getUnScanFile();
                for (String scanFile : unScanFile) {
                    System.out.println(scanFile);
                    //50个文件重置一次
                    InputStream inputStream = LocalFileUtil.getFile(scanFile);
                    List<ScanResult> list = scanFile(inputStream);
                    if (list!=null) {
                        scanService.batchAddScanResult(scanFile, list);
                        scanService.updateScanFile(scanFile, 1);
                    }else{
                        scanService.updateScanFile(scanFile, 2);
                    }
                }
            }
            finish = 0;
        } catch (IOException e) {
            e.printStackTrace();
            finish = -1;
        }
        scanService.updateScan(this.line, this.no);
    }

    private void ftpRun() {
        finish = 1;
        index = 0;
        try {
            for (String path : paths) {
                List<String> allFile = ftpUtil.allFile(path, new FileFTPFileFilter());
                scanService.batchAddScanFile(allFile);
                List<String> unScanFile = scanService.getUnScanFile();
                for (String scanFile : unScanFile) {
                    System.out.println(scanFile);
                    index++;
                    //50个文件重置一次
                    if (index > 1) {
                        ftpUtil.completePendingCommand();
                    }
                    InputStream inputStream = ftpUtil.getFile(scanFile);
                    List<ScanResult> list = scanFile(inputStream);
                    if (list!=null) {
                        scanService.batchAddScanResult(scanFile, list);
                        scanService.updateScanFile(scanFile, 1);
                    }else{
                        scanService.updateScanFile(scanFile, 2);
                    }
                }
            }
            finish = 0;
        } catch (IOException e) {
            e.printStackTrace();
            finish = -1;
        }
        scanService.updateScan(this.line, this.no);
        ftpUtil.logout();
    }

    private List<ScanResult> scanFile(InputStream inputStream) {
        try {
            if (inputStream != null) {
                CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
                HeaderColumnNameTranslateMappingStrategy<ScanResult> mapper = new
                        HeaderColumnNameTranslateMappingStrategy<ScanResult>();
                mapper.setType(ScanResult.class);
                Map<String, String> map = new HashMap<String, String>();
                map.put("model", "model");
                map.put("mac", "mac");
                map.put("station", "station");
                map.put("result", "result");
                map.put("start time", "starttime");
                mapper.setColumnMapping(map);
                CsvToBean<ScanResult> csvToBean = new CsvToBean<ScanResult>();
                List<ScanResult> list = csvToBean.parse(mapper, reader);
                reader.close();
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }


    private static class FileFTPFileFilter implements FTPFileFilter {
        @Override
        public boolean accept(FTPFile ftpFile) {
            if (!ftpFile.isDirectory()) {
                if (!Pattern.matches("wbs[0-9]{2}_transtek_log_[0-9a-z]{2}_[0-9]+_[0-9]{8}_[0-9]{6}\\.csv", ftpFile.getName())) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class FileFilter implements FilenameFilter{

        @Override
        public boolean accept(File dir, String name) {
            if (!Pattern.matches("wbs[0-9]{2}_transtek_log_[0-9a-z]{2}_[0-9]+_[0-9]{8}_[0-9]{6}\\.csv", name)) {
                return false;
            }
            return true;
        }
    }
}
