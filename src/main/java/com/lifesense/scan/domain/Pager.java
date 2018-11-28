package com.lifesense.scan.domain;

import java.util.List;

/**
 * Created by 赵春定 on 2017/3/27.
 */
public class Pager<T> {
    private int curPage = 1; // 当前页
    private int pageSize = 10; // 每页多少行
    private int totalRow; // 共多少行
    private int start;// 当前页起始行
    private int end;// 结束行
    private int totalPage; // 共多少页
    private List<T> data;

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public int getStart() {
        return (getCurPage() - 1) * getPageSize() > 0 ? (getCurPage() - 1) * getPageSize() : 0;
    }

    public int getEnd() {
        return getTotalRow() > getPageSize() * getCurPage() ? getPageSize() * getCurPage() : getTotalRow();
    }

    public int getTotalPage() {
        return getTotalRow() % getPageSize() > 0 ? getTotalRow() / getPageSize() + 1 : getTotalRow() / getPageSize();
    }


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
