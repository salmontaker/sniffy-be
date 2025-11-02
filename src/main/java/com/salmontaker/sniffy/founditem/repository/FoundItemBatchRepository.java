package com.salmontaker.sniffy.founditem.repository;

import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FoundItemBatchRepository {
    private final JdbcTemplate jdbc;

    public void createTempTable() {
        // 원본 스키마 기준으로 임시 테이블 생성
        jdbc.execute("CREATE TEMPORARY TABLE temp_found_item LIKE found_item");

        // 원본 테이블에 없는 컬럼 추가
        jdbc.execute("ALTER TABLE temp_found_item ADD COLUMN dep_place VARCHAR(100)");
    }

    public void insertTempTable(List<LostFoundResponse.Item> items) {
        String sql = """
                INSERT INTO temp_found_item 
                    (atc_id, clr_nm, dep_place, fd_file_path_img, fd_prdt_nm, fd_sbjt, fd_sn, fd_ymd, prdt_cl_nm)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbc.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                LostFoundResponse.Item item = items.get(i);
                ps.setString(1, item.getAtcId());
                ps.setString(2, item.getClrNm());
                ps.setString(3, item.getDepPlace());
                ps.setString(4, item.getFdFilePathImg());
                ps.setString(5, item.getFdPrdtNm());
                ps.setString(6, item.getFdSbjt());
                ps.setInt(7, item.getFdSn());
                ps.setString(8, item.getFdYmd());
                ps.setString(9, item.getPrdtClNm());
            }

            @Override
            public int getBatchSize() {
                return items.size();
            }
        });
    }

    public void mergeToMainTable() {
        String sql = """
                INSERT INTO found_item
                    (agency_id, atc_id, clr_nm, fd_file_path_img, fd_prdt_nm, fd_sbjt, fd_sn, fd_ymd, prdt_cl_nm, created_at)
                SELECT 
                    a.id as agency_id,
                    t.atc_id,
                    t.clr_nm,
                    t.fd_file_path_img,
                    t.fd_prdt_nm,
                    t.fd_sbjt,
                    t.fd_sn,
                    t.fd_ymd,
                    t.prdt_cl_nm,
                    NOW()
                FROM temp_found_item t
                JOIN agency a ON t.dep_place = a.name
                ON DUPLICATE KEY UPDATE 
                    agency_id = VALUES(agency_id),
                    clr_nm = VALUES(clr_nm),
                    fd_file_path_img = VALUES(fd_file_path_img),
                    fd_prdt_nm = VALUES(fd_prdt_nm),
                    fd_sbjt = VALUES(fd_sbjt),
                    fd_ymd = VALUES(fd_ymd),
                    prdt_cl_nm = VALUES(prdt_cl_nm),
                    updated_at = NOW()
                """;

        jdbc.execute(sql);
    }

    public void dropTempTable() {
        jdbc.execute("DROP TEMPORARY TABLE IF EXISTS temp_found_item");
    }
}
