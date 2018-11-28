package com.lifesense.scan.domain;

import java.util.Date;

/**
 * Created by 赵春定 on 2017/5/25.
 */
public class Scan {
    private Integer id;
    private String no;
    private String mac;
    private String f1;
    private String f2;
    private String f3;
    private String result;
    private String file_path;
    private Date create_time;
    private String start_time;
    private String model;
    private String line;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public String getResult() {
        if (result!=null) return result;
        if ("pass".equalsIgnoreCase(f1) && "pass".equalsIgnoreCase(f2) && "pass".equalsIgnoreCase(f3)){
            result = "1";
        }else{
            result = "2";
        }
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
