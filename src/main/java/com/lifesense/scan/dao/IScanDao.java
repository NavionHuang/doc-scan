package com.lifesense.scan.dao;


import com.lifesense.scan.domain.Pager;
import com.lifesense.scan.domain.Scan;
import com.lifesense.scan.domain.ScanResult;

import java.util.List;

/**
 * Created by 赵春定 on 2017/6/1.
 */
public interface IScanDao {

    /**
     * 保存扫描文件名称记录
     * @param list
     */
    void batchSaveScanFile(List<String> list);

    /**
     * 更新文件分析结果
     * @param scanFile 文件名称
     */
    int updateScanFile(String scanFile,int status);

    /**
     * 获取未分析的文件名称
     * @return
     */
    List<String> getUnScanFileList();

    /**
     * 新增扫描MAC
     * @param san
     */
    int saveScan(Scan san);

    /**
     * 提交扫描，分批一个卡板号
     * @param line 产线
     * @param no
     */
    void updateScan(String line,String no);


    /**
     *
     * @param pager
     * @param scan mac码
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    Pager<Scan> queryScan(Pager<Scan> pager, Scan scan, String startTime, String endTime) ;

    /**
     * 批量更新扫描结果
     * @param list
     */
    void batchSaveScanResult(String filePath,List<ScanResult> list);

    List<Scan> getUnSubmitScan(String line, String no);

    /**
     *  查询扫描记录
     * @param mac mac
     * @return
     */
    List<ScanResult> getScanResult(String mac);
}
