package com.crypto.api;

import java.util.List;

public interface PageReader {

    /**
     * Read entries from page and return list of entities
     * @return list of entries
     */
    List<Object> readEntries();

    /**
     * Compare the previous and current batch and return
     * @param previousBatch
     * @param currentBatch
     * @return if there is any difference between the two
     */
    boolean processEntries(List<? extends Object> previousBatch, List<? extends Object> currentBatch);

    /**
     * Save entities to database
     * @param entries
     */
    void save(List<? extends Object> entries);
}
