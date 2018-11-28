package com.lifesense.scan.service;

import com.lifesense.scan.dao.IScanDao;
import com.lifesense.scan.domain.Pager;
import com.lifesense.scan.domain.Scan;
import com.lifesense.scan.domain.ScanResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 赵春定 on 2017/6/2.
 */
@Service
public class ScanServiceImpl implements IScanService{

    @Autowired
    private IScanDao scanDao;

    @Override
    public int addScan(Scan scan) {
        return scanDao.saveScan(scan);
    }

    @Override
    public int updateScanFile(String file,int status){
        return scanDao.updateScanFile(file,status);
    }

    @Override
    public void batchAddScanResult(String filePath,List<ScanResult> list) {
        scanDao.batchSaveScanResult(filePath,list);
    }

    @Override
    public void batchAddScanFile(List<String> fileList) {
        scanDao.batchSaveScanFile(fileList);
    }

    @Override
    public List<String> getUnScanFile() {
        return scanDao.getUnScanFileList();
    }

    @Override
    public Pager<Scan> queryScan(int page, int pageSize, Scan scan, String startTime, String endTime) {
        Pager<Scan> pager = new Pager<Scan>();
        pager.setPageSize(pageSize);
        pager.setCurPage(page);
        pager = scanDao.queryScan(pager,scan, startTime, endTime);
        return pager;
    }

    @Override
    public void updateScan(String line,String no) {
        scanDao.updateScan(line,no);
    }

    @Override
    public List<Scan> queryUnSubmitScan(String line, String no) {
        return scanDao.getUnSubmitScan(line,no);
    }

    @Override
    public List<ScanResult> queryScanResult(String mac) {
        return scanDao.getScanResult(mac);
    }


}
