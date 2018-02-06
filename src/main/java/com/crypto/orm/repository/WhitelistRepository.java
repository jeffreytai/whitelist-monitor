package com.crypto.orm.repository;

import com.crypto.orm.entity.WhitelistCoin;
import com.utils.DbUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhitelistRepository {

    /**
     * Return all whitelist entries in the table
     * @return
     */
    public static List<WhitelistCoin> findAll() {
        String query = "SELECT w FROM WhitelistCoin w";

        List<WhitelistCoin> entries = (List<WhitelistCoin>) DbUtils.runMultipleResultQuery(query);
        return entries;
    }

    /**
     * Delete entries that are less than a specific date
     * @param date
     * @return number of rows deleted
     */
    public static Integer deleteIfLessThanDate(Date date) {
        String query = "DELETE FROM WhitelistCoin w WHERE w.Created < :date";
        Map<Object, Object> bindedParameters = new HashMap<>();
        bindedParameters.put("date", date);

        Integer affectedRows = DbUtils.runQueryWithoutResults(query, bindedParameters);
        return affectedRows;
    }
}
