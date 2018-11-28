package com.lifesense.scan.util;

import org.apache.commons.net.ftp.*;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵春定 on 2017/5/27.
 */
public class FtpUtil {

    private FTPClient client;

    public FtpUtil() {
        client = new FTPClient();
    }


    public List<String> allFile(String path, FTPFileFilter ftpFileFilter) throws IOException {
        connection();
        Assert.assertNotNull("path is Muster not null",path);
        path.replaceAll("//","/").replaceAll("\\\\","/");
        if (!path.startsWith("/")) path = "/" + path;
        List<String> fileList = new ArrayList<String>();
        if (client.isConnected()) {
            client.changeWorkingDirectory(path);
            FTPFile[] ftpFiles = client.listFiles(path, ftpFileFilter);
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.isDirectory()) {
                    String nextPath = path.endsWith("/") ? path + ftpFile.getName() : path + "/" + ftpFile.getName();
                    List<String> list = allFile(nextPath, ftpFileFilter);
                    fileList.addAll(list);
                } else {
                    String file = path.endsWith("/") ? path + ftpFile.getName() : path + "/" + ftpFile.getName();
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    public List<String> files(String path, FTPFileFilter ftpFileFilter) throws IOException {
        connection();
        path.replaceAll("//","/").replaceAll("\\\\","/");
        if (!path.startsWith("/")) path = "/" + path;
        List<String> fileList = new ArrayList<String>();
        if (client.isConnected()) {
            client.changeWorkingDirectory(path);
            FTPFile[] ftpFiles = client.listFiles(path, ftpFileFilter);
            for (FTPFile ftpFile : ftpFiles) {
                fileList.add(ftpFile.getName());
            }
        }
        return fileList;
    }

    public InputStream getFile(String file) throws IOException {
        connection();
        client.enterLocalPassiveMode();
        client.setFileType(FTPClient.BINARY_FILE_TYPE); //设置下载文件为二进制模式
        client.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE); ///传输文件为流的形式
        client.setControlEncoding("UTF-8");
        client.changeWorkingDirectory(file);
       return client.retrieveFileStream(file);
    }

    //设置处理多个文件
    public boolean completePendingCommand(){
        try {
            if (client.isConnected())
            return client.completePendingCommand();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getReplyString(){
       return client.getReplyString();
    }

    private void connection() throws IOException {
        if (!client.isConnected()){
            this.login("192.168.7.152", 21, "life", "log2015*-");
        }
    }


    private boolean login(String host, int port, String username, String password) throws IOException {
        this.client.connect(host, port);
        if (FTPReply.isPositiveCompletion(client.getReplyCode())) {
            if (client.login(username, password)) {
                FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
                client.setRemoteVerificationEnabled(false);
                client.configure(conf);
                client.enterLocalPassiveMode(); //设置被动模式
                client.setFileType(FTPClient.BINARY_FILE_TYPE); //设置下载文件为二进制模式
                client.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE); ///传输文件为流的形式
                client.setControlEncoding("UTF-8");
                return true;
            }
        }else {
            client.disconnect();
        }
        return false;
    }

    public void logout(){
        if (this.client.isConnected()) {
            try {
                this.client.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    this.client.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
