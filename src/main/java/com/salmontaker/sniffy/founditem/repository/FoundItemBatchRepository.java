package com.salmontaker.sniffy.founditem.repository;

import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FoundItemBatchRepository {
    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public boolean hasTodayChangedItems() {
        String sql = """
                SELECT EXISTS(
                    SELECT 1
                    FROM found_item
                    WHERE created_at >= CURDATE()
                       OR updated_at >= CURDATE()
                       OR deleted_at >= CURDATE()
                )
                """;

        Boolean exists = jdbc.queryForObject(sql, Boolean.class);
        return exists != null && exists;
    }

    public void createTempTable() {
        // 원본 스키마 기준으로 임시 테이블 생성
        jdbc.execute("CREATE TEMPORARY TABLE temp_found_item LIKE found_item");

        // 원본 테이블에 없는 컬럼 추가
        jdbc.execute("ALTER TABLE temp_found_item ADD COLUMN dep_place VARCHAR(100)");
    }

    public void insertTempTable(List<LostFoundResponse> items) {
        String sql = """
                INSERT INTO temp_found_item 
                    (atc_id, clr_nm, dep_place, fd_file_path_img, fd_prdt_nm, fd_sbjt, fd_sn, fd_ymd, prdt_cl_nm)
                VALUES
                    (:atcId, :clrNm, :depPlace, :fdFilePathImg, :fdPrdtNm, :fdSbjt, :fdSn, :fdYmd, :prdtClNm)
                """;

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(items);
        namedJdbc.batchUpdate(sql, batch);
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

    public void deleteFoundOrExpiredItem() {
        String sql = """
                UPDATE found_item fi
                SET deleted_at = NOW()
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM temp_found_item t
                    WHERE fi.atc_id = t.atc_id
                      AND fi.fd_sn = t.fd_sn
                )
                """;

        jdbc.execute(sql);
    }

    public void dropTempTable() {
        jdbc.execute("DROP TEMPORARY TABLE IF EXISTS temp_found_item");
    }
}
