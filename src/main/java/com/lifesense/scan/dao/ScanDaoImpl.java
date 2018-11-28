package com.lifesense.scan.dao;


import com.lifesense.scan.domain.Pager;
import com.lifesense.scan.domain.Scan;
import com.lifesense.scan.domain.ScanResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 赵春定 on 2017/6/1.
 */
@Repository
public class ScanDaoImpl implements IScanDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void batchSaveScanFile(final List<String> list) {
        String sql = "insert ignore into scan_file(file_path,scan_status) values(?,0)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setString(1, list.get(i));
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    @Override
    public int updateScanFile(String scanFile, int status) {
        String sql = "update scan_file set scan_status=? where file_path=?";
        return jdbcTemplate.update(sql, status, scanFile);
    }

    @Override
    public List<String> getUnScanFileList() {
        String sql = "select file_path from scan_file where scan_status=0";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public int saveScan(Scan san) {
        String sql = "insert ignore into scan(mac,line,no) values(?,?,?)";
        return jdbcTemplate.update(sql, san.getMac(), san.getLine(), san.getNo());
    }

    @Override
    public void updateScan(String line, String no) {
        String sql = "update scan set scan_status=1 where scan_status=0 and line =? and no=? ";
        jdbcTemplate.update(sql, line, no);
    }

    @Override
    public Pager<Scan> queryScan(Pager<Scan> pager, Scan scan, String startTime, String endTime) {
        String line = scan.getLine();
        String no = scan.getNo();
        String mac = scan.getMac();
        int result = Integer.valueOf(scan.getResult());
        String model = scan.getModel();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select t.id,t.no,t.mac,t.create_time,t.line,r.start_time,r.model,r.f1,r.f2,r.f3 from scan t ");
        buffer.append("left join ");
        buffer.append("(select m.mac,b.starttime start_time,max(model) model,max(case m.station when 'f1' then m.result else '' end) f1,");
        buffer.append("max(case m.station when 'f2' then m.result else '' end) f2,");
        buffer.append("max(case m.station when 'rc' then m.result else '' end) f3 ");
        buffer.append("from scan_result m,(select mac,station,max(start_time) starttime from scan_result group by mac,station) b,");
        buffer.append("scan s where m.mac=b.mac and m.station = b.station and m.start_time=b.starttime and m.mac = s.mac group by m.mac) r ");
        buffer.append("on t.mac = r.mac ");
        buffer.append("where 1=1 and ");
        List<Object> params = new ArrayList<Object>();
        if (startTime != null && !"".equals(startTime)) {
            buffer.append("t.create_time >= ? and ");
            params.add(startTime);
        }
        if (endTime != null && !"".equals(endTime)) {
            buffer.append("t.create_time <= DATE_SUB(?,interval -1 DAY) and ");
            params.add(endTime);
        }
        if (line != null && !"".equals(line)) {
            buffer.append("t.line = ? and ");
            params.add(line);
        }
        if (no != null && !"".equals(no)) {
            buffer.append("t.no = ? and ");
            params.add(no);
        }
        if (mac != null && !"".equals(mac)) {
            buffer.append("t.mac = ? and ");
            params.add(mac);
        }
        if (model != null && !"".equals(model)) {
            buffer.append("r.model = ? and ");
            params.add(mac);
        }
        String sql = "select * from (";
        sql += buffer.substring(0, buffer.lastIndexOf("and"));
        sql += ") T ";
        if (result > 0) {
            if (result == 1) {
                sql += " where T.f1 ='pass' and  T.f2 ='pass' and  T.f3 ='pass' ";
            } else {
                sql += " where T.f1 !='pass' OR  T.f2 !='pass' OR  T.f3 !='pass' OR T.f1 is NULL OR T.f2 is NULL OR T.f3 is NULL ";
            }
        }

        int count = jdbcTemplate.queryForObject("select count(1) from (" + sql + ") tt", params.toArray(new Object[params.size()]), Integer.class);
        pager.setTotalRow(count);
        if (count > 0) {
            sql += " order by id DESC limit ?,?";
            params.add(pager.getStart());
            params.add(pager.getPageSize());
            List<Scan> data = jdbcTemplate.query(sql, params.toArray(new Object[params.size()]), new RowMapper<Scan>() {
                @Override
                public Scan mapRow(ResultSet rs, int i) throws SQLException {
                    Scan scan = new Scan();
                    scan.setId(rs.getInt("id"));
                    scan.setNo(rs.getString("no"));
                    scan.setMac(rs.getString("mac"));
                    scan.setF1(rs.getString("f1"));
                    scan.setF2(rs.getString("f2"));
                    scan.setF3(rs.getString("f3"));
                    scan.setCreate_time(rs.getDate("create_time"));
                    scan.setLine(rs.getString("line"));
                    scan.setStart_time(rs.getString("start_time"));
                    scan.setModel(rs.getString("model"));
                    return scan;
                }
            });
            pager.setData(data);
        }else{
            pager.setData(new ArrayList<Scan>());
        }
        return pager;
    }


    @Override
    public void batchSaveScanResult(final String filePath, final List<ScanResult> list) {
        final String sql = "insert ignore into scan_result(mac,station,result,file_path,model,start_time) VALUES (?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                ScanResult scan = list.get(i);
                preparedStatement.setString(1, scan.getMac());
                preparedStatement.setString(2, scan.getStation());
                preparedStatement.setString(3, scan.getResult());
                preparedStatement.setString(4, filePath);
                preparedStatement.setString(5, scan.getModel());
                preparedStatement.setString(6, scan.getStarttime());
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });
    }

    @Override
    public List<Scan> getUnSubmitScan(String line, String no) {
        String sql = "select t.id,t.no,t.mac,t.create_time,t.line from scan t where t.line =? and t.no=?";
        List<Scan> data = jdbcTemplate.query(sql, new Object[]{line, no}, new RowMapper<Scan>() {
            @Override
            public Scan mapRow(ResultSet rs, int i) throws SQLException {
                Scan scan = new Scan();
                scan.setId(rs.getInt("id"));
                scan.setNo(rs.getString("no"));
                scan.setMac(rs.getString("mac"));
                scan.setLine(rs.getString("line"));
                return scan;
            }
        });
        return data;
    }

    @Override
    public List<ScanResult> getScanResult(String mac) {
        String sql = "select t.mac,t.station,t.model,t.file_path,t.result,t.start_time from scan_result t where t.mac=?";
        List<ScanResult> data = jdbcTemplate.query(sql, new Object[]{mac}, new RowMapper<ScanResult>() {
            @Override
            public ScanResult mapRow(ResultSet rs, int i) throws SQLException {
                ScanResult result = new ScanResult();
                result.setModel(rs.getString("model"));
                result.setMac(rs.getString("mac"));
                result.setStation(rs.getString("station"));
                result.setResult(rs.getString("result"));
                result.setFilePath(rs.getString("file_path"));
                result.setStarttime(rs.getString("start_time"));
                return result;
            }
        });
        return data;
    }
}
