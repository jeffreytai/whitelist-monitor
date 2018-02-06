package com.crypto.orm.repository;

import com.crypto.orm.entity.PresaleCoin;
import com.utils.DbUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresaleRepository {

    /**
     * Return all presale entries in the table
     * @return
     */
    public static List<PresaleCoin> findAll() {
        String query = "SELECT p FROM PresaleCoin p";

        List<PresaleCoin> entries = (List<PresaleCoin>) DbUtils.runMultipleResultQuery(query);
        return entries;
    }

    /**
     * Delete entries that are less than a specific date
     * @param date
     * @return number of rows deleted
     */
    public static Integer deleteIfLessThanDate(Date date) {
        String query = "DELETE FROM PresaleCoin p WHERE p.created < :date";
        Map<Object, Object> bindedParameters = new HashMap<>();
        bindedParameters.put("date", date);

        Integer affectedRows = DbUtils.runQueryWithoutResults(query, bindedParameters);
        return affectedRows;
    }
}
