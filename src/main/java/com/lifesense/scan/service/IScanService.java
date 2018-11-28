package com.lifesense.scan.service;


import com.lifesense.scan.domain.Pager;
import com.lifesense.scan.domain.Scan;
import com.lifesense.scan.domain.ScanResult;

import java.util.List;

/**
 * Created by 赵春定 on 2017/6/2.
 */
public interface IScanService {

    /**
     * 新增扫描mac
     *
     * @param scan
     * @return
     */
    int addScan(Scan scan);

    /**
     * 更新扫描文件状态
     *
     * @param file
     * @return
     */
    int updateScanFile(String file, int status);

    /**
     * 批量保存扫描结果
     *
     * @param list
     */
    void batchAddScanResult(String filePath, List<ScanResult> list);

    /**
     * 批量保存文件，已保存过的忽略
     *
     * @param fileList
     */
    void batchAddScanFile(List<String> fileList);


    /**
     * 未扫描的文件列表
     *
     * @return
     */
    List<String> getUnScanFile();

    /**
     * @param page        开始页
     * @param pageSize    页大小
     * @paramScan scan
     * @param startTime   开始时间
     * @param endTime     结束时间
     * @return
     */
    Pager<Scan> queryScan(int page, int pageSize,Scan scan, String startTime, String endTime);

    /**
     * 更新提交的mac码
     *
     * @param line 产线
     * @param no
     */
    void updateScan(String line, String no);

    List<Scan> queryUnSubmitScan(String line, String no);

    /**
     *  查询扫描结果
     * @param mac
     * @return
     */
    List<ScanResult> queryScanResult(String mac);
}
