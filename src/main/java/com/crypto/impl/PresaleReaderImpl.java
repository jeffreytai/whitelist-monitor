package com.crypto.impl;

import com.crypto.api.PageReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PresaleReaderImpl implements PageReader {

    /**
     * Logging
     */
    private static final Logger logger = LoggerFactory.getLogger(PresaleReaderImpl.class);

    /**
     * Base url
     */
    private final String PRESALE_URL = "https://icodrops.com/pre-sales/";

    public PresaleReaderImpl() {

    }

    @Override
    public List<Object> readEntries() {
        return null;
    }

    @Override
    public boolean processEntries(List<? extends Object> firstBatch, List<? extends Object> secondBatch) {
        return true;
    }

    @Override
    public void save(List<? extends Object> entries) {

    }
}
