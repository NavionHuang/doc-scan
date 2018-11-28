package com.lifesense.scan.controller;


import com.lifesense.scan.domain.Pager;
import com.lifesense.scan.domain.Scan;
import com.lifesense.scan.domain.ScanResult;
import com.lifesense.scan.service.IScanService;
import com.lifesense.scan.util.DateUtils;
import com.lifesense.scan.util.FtpUtil;
import com.lifesense.scan.util.LocalFileUtil;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.server.SocketSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 赵春定 on 2017/5/25.
 */
@Controller
@RequestMapping("/scan")
public class ScanController {

    @Autowired
    private IScanService scanService;
    @Autowired
    private FtpTaskExcutor ftpTaskExcutor;
    @Value("${root}")
    private String root;

    private static String startYmd = "20180101";

    @RequestMapping("/index")
    ModelAndView index(@RequestParam(value = "page", defaultValue = "1") int page,String startTime, String endTime,Scan scan) {
        int pageSize = 20;
        Pager<Scan> pager = scanService.queryScan(page,pageSize,scan,startTime,endTime);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/scan/index");
        modelAndView.addObject("pager",pager);
        modelAndView.addObject("page", page);
        modelAndView.addObject("scan", scan);
        modelAndView.addObject("startTime", startTime);
        modelAndView.addObject("endTime", endTime);
        return modelAndView;
    }

    @RequestMapping("/scan")
    ModelAndView scan(String line,String no) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/scan/scan");
        if (line!=null && no!=null) {
            Scan scan = new Scan();
            scan.setLine(line);
            scan.setModel(no);
            List<Scan> list = scanService.queryUnSubmitScan(line,no);
            modelAndView.addObject("list", list);
        }
        modelAndView.addObject("line", line);
        modelAndView.addObject("no", no);
        return modelAndView;
    }

    @RequestMapping("/addScan")
    @ResponseBody
    String addScan(Scan san) {
        int result = scanService.addScan(san);
        return String.valueOf(result);
    }

    @RequestMapping("/scanResult")
    ModelAndView scanResult(String mac) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/scan/scan_result");
        List<ScanResult> resultList = scanService.queryScanResult(mac);
        modelAndView.addObject("resultList", resultList);
        return modelAndView;
    }

    @RequestMapping("/submitScan")
    @ResponseBody
    String submitScan(String line,String no,@RequestParam(value = "paths[]", required = false) String[] paths) {
        if (paths == null) return "-1";
        ftpTaskExcutor.add(paths);
        ftpTaskExcutor.setNo(no);
        ftpTaskExcutor.setLine(line);
        ftpTaskExcutor.setRoot(root);
        Thread thread = new Thread(ftpTaskExcutor);
        thread.start();
        return "1";
    }

    @RequestMapping("/scanstatus")
    @ResponseBody
    String scanStatus() {
        return String.valueOf(ftpTaskExcutor.getFinish());
    }


    @RequestMapping("/ftp/directory")
    @ResponseBody
    List<DirectoryTree> directoryTree() {
    	//读取15天内测量文件
        Date beforeDay = DateUtils.getBeforeDay(new Date(), 15);
        startYmd = DateUtils.getDateString(beforeDay, "yyyyMMdd");
        List<DirectoryTree> directory = new ArrayList<DirectoryTree>();
        //根
        DirectoryTree tree = new DirectoryTree();
        directory.add(tree);
        tree.selectable = false;
        tree.text = "/log";
        //一级节点 start
        List<DirectoryTree> wbsTree = new ArrayList<DirectoryTree>();
        DirectoryTree wbs05Tree01 = new DirectoryTree();
        wbs05Tree01.selectable = false;
        wbs05Tree01.text = "/wbs05/log/csv/toupload";
        DirectoryTree wbs05Tree02 = new DirectoryTree();
        wbs05Tree02.selectable = false;
        wbs05Tree02.text = "/wbs05/log/csv/uploaded";
        DirectoryTree wbs06Tree01 = new DirectoryTree();
        wbs06Tree01.selectable = false;
        wbs06Tree01.text = "/wbs06/log/csv/toupload";
        DirectoryTree wbs06Tree02 = new DirectoryTree();
        wbs06Tree02.selectable = false;
        wbs06Tree02.text = "/wbs06/log/csv/uploaded";
        wbsTree.add(wbs05Tree01);
        wbsTree.add(wbs05Tree02);
        wbsTree.add(wbs06Tree01);
        wbsTree.add(wbs06Tree02);
        //二级节点 start
        try {
            if(root!=null && !"".equals(root)){
                List<String> filesOf05to = LocalFileUtil.directories(root+"/wbs05/log/csv/toupload",FILENAME_FILTER);
                wbs05Tree01.nodes = createNodes(filesOf05to);
                List<String> filesOf05up = LocalFileUtil.directories(root+"/wbs05/log/csv/uploaded",FILENAME_FILTER);
                wbs05Tree02.nodes = createNodes(filesOf05up);
                List<String> filesOf06to = LocalFileUtil.directories(root+"/wbs06/log/csv/toupload",FILENAME_FILTER);
                wbs06Tree01.nodes = createNodes(filesOf06to);
                List<String> filesOf06up = LocalFileUtil.directories(root+"/wbs06/log/csv/uploaded",FILENAME_FILTER);
                wbs06Tree02.nodes = createNodes(filesOf06up);
            }else {
                FtpUtil ftpUtil = new FtpUtil();
                List<String> filesOf05to = ftpUtil.files("/wbs05/log/csv/toupload", DIRECTORIES);
                wbs05Tree01.nodes = createNodes(filesOf05to);
                List<String> filesOf05up = ftpUtil.files("/wbs05/log/csv/uploaded", DIRECTORIES);
                wbs05Tree02.nodes = createNodes(filesOf05up);
                List<String> filesOf06to = ftpUtil.files("/wbs06/log/csv/toupload", DIRECTORIES);
                wbs06Tree01.nodes = createNodes(filesOf06to);
                List<String> filesOf06up = ftpUtil.files("/wbs06/log/csv/uploaded", DIRECTORIES);
                wbs06Tree02.nodes = createNodes(filesOf06up);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //二级节点 end
        //一级节点 end
        tree.nodes = wbsTree;
        return directory;
    }

    private List<DirectoryTree> createNodes(List<String> list){
        if (list != null && list.size() > 0) {
            List<DirectoryTree> nodes = new ArrayList<DirectoryTree>();
            for (String path : list){
                DirectoryTree node = new DirectoryTree();
                node.text = path;
                node.selectable = true;
                node.icon = "glyphicon glyphicon-file";
                nodes.add(node);
            }
            return nodes;
        }
        return null;
    }


    private class DirectoryTree {
        List<DirectoryTree> nodes;
        String text;
        boolean selectable;
        String icon;

        public List<DirectoryTree> getNodes() {
            return nodes;
        }

        public void setNodes(List<DirectoryTree> nodes) {
            this.nodes = nodes;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public boolean isSelectable() {
            return selectable;
        }

        public void setSelectable(boolean selectable) {
            this.selectable = selectable;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    private static final FTPFileFilter DIRECTORIES = new FTPFileFilter() {

        public boolean accept(FTPFile file) {
            if (file != null && file.isDirectory()) {
                String name = file.getName();
                if (Integer.valueOf(name) >= Integer.valueOf(startYmd)) {
                    return true;
                }
            }
            return false;
        }
    };

    private static final FilenameFilter  FILENAME_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File file, String name) {
            if (file != null && file.isDirectory()) {
                if (Integer.valueOf(name) >= Integer.valueOf(startYmd)) {
                    return true;
                }
            }
            return false;
        }
    };
}
