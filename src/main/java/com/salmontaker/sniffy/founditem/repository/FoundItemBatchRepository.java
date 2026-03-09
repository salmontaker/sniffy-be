package com.salmontaker.sniffy.founditem.repository;

import com.salmontaker.sniffy.founditem.dto.external.response.LostFoundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    public void recreateStagingTable() {
        jdbc.execute("DROP TABLE IF EXISTS staging_found_item");
        jdbc.execute("CREATE TABLE staging_found_item LIKE found_item");
        jdbc.execute("ALTER TABLE staging_found_item ADD COLUMN dep_place VARCHAR(100)");
    }

    public void insertStagingTable(List<LostFoundResponse> items) {
        String sql = """
                INSERT INTO staging_found_item
                    (atc_id, clr_nm, dep_place, fd_file_path_img, fd_prdt_nm, fd_sbjt, fd_sn, fd_ymd, prdt_cl_nm)
                VALUES
                    (:atcId, :clrNm, :depPlace, :fdFilePathImg, :fdPrdtNm, :fdSbjt, :fdSn, :fdYmd, :prdtClNm)
                """;

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(items);
        namedJdbc.batchUpdate(sql, batch);
    }

    @Transactional
    public void mergeAndSync() {
        mergeToMainTable();
        deleteFoundOrExpiredItem();
    }

    private void mergeToMainTable() {
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
                FROM staging_found_item t
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

    private void deleteFoundOrExpiredItem() {
        String sql = """
                UPDATE found_item fi
                SET deleted_at = NOW()
                WHERE deleted_at IS NULL
                  AND NOT EXISTS (
                    SELECT 1
                    FROM staging_found_item t
                    WHERE fi.atc_id = t.atc_id
                      AND fi.fd_sn = t.fd_sn
                )
                """;

        jdbc.execute(sql);
    }
}
