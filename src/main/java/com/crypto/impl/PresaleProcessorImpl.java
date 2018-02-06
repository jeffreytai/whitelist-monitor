package com.crypto.impl;

import com.crypto.api.PageProcessor;
import com.crypto.api.PageReader;
import com.crypto.orm.entity.PresaleCoin;
import com.crypto.orm.repository.PresaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class PresaleProcessorImpl implements PageProcessor {

    /**
     * Logging
     */
    private static final Logger logger = LoggerFactory.getLogger(PresaleProcessorImpl.class);

    public PresaleProcessorImpl() {}

    @Override
    public void process() {
        Date currentDate = new Date();

        // Extract the current batch from the page
        PageReader reader = new PresaleReaderImpl();
        List<? extends Object> entries = reader.readEntries();
        List<PresaleCoin> currentBatch = (List<PresaleCoin>) entries;

        // Process the previous batch if exists
        List<PresaleCoin> previousBatch = PresaleRepository.findAll();
        if (previousBatch.size() > 0) {
            boolean hasChanges = reader.processEntries(previousBatch, currentBatch);

            if (!hasChanges) {
                logger.debug("No changes found");
                return;
            }

            logger.debug("Deleting existing whitelist entries in database");
            PresaleRepository.deleteIfLessThanDate(currentDate);
        }

        // Save the current batch
        logger.debug("Saving current presale entries into database");
        reader.save(currentBatch);
    }
}
