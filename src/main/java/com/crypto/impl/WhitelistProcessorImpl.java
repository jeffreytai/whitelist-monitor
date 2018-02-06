package com.crypto.impl;

import com.crypto.api.PageProcessor;
import com.crypto.api.PageReader;
import com.crypto.orm.entity.WhitelistCoin;
import com.crypto.orm.repository.WhitelistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class WhitelistProcessorImpl implements PageProcessor {

    /**
     * Logging
     */
    private static final Logger logger = LoggerFactory.getLogger(WhitelistProcessorImpl.class);

    public WhitelistProcessorImpl() {}

    @Override
    public void process() {
        Date currentDate = new Date();

        // Extract the current batch from the page
        PageReader reader = new WhitelistReaderImpl();
        List<? extends Object> entries = reader.readEntries();
        List<WhitelistCoin> currentBatch = (List<WhitelistCoin>) entries;

        // Process the previous batch if exists
        List<WhitelistCoin> previousBatch = WhitelistRepository.findAll();
        if (previousBatch.size() > 0) {
            boolean hasChanges = reader.processEntries(previousBatch, currentBatch);

            if (!hasChanges) {
                logger.debug("No changes found");
                return;
            }

            logger.debug("Deleting existing whitelist entries in database");
            WhitelistRepository.deleteIfLessThanDate(currentDate);
        }

        // Save the current batch
        logger.debug("Saving current whitelist entries into database");
        reader.save(currentBatch);
    }
}
