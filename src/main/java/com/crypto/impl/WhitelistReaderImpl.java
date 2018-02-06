package com.crypto.impl;

import com.crypto.api.PageReader;
import com.crypto.orm.entity.WhitelistCoin;
import com.crypto.slack.SlackWebhook;
import com.utils.DbUtils;
import org.javatuples.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WhitelistReaderImpl implements PageReader {

    /**
     * Logging
     */
    private final static Logger logger = LoggerFactory.getLogger(WhitelistReaderImpl.class);

    /**
     * Base url
     */
    private final String WHITELIST_URL = "https://icodrops.com/whitelist/";

    /**
     * User agent for web requests
     */
    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

    /**
     * Constructor
     */
    public WhitelistReaderImpl() {}

    @Override
    public List<Object> readEntries() {
        logger.info("Reading whitelist entries");
        List<Object> entries = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(this.WHITELIST_URL).userAgent(this.USER_AGENT).get();
            Elements projects = doc.select(".whiteico");

            for (Element project : projects) {
                WhitelistCoin entry = new WhitelistCoin(project);

                entries.add(entry);

                logger.debug("Created whitelist entity for {}", entry.getName());
            }
        }
        catch (IOException ex) {
            logger.error("Error parsing data from whitelist url");
        }

        return entries;
    }

    @Override
    public boolean processEntries(List<? extends Object> firstBatch, List<? extends Object> secondBatch) {
        logger.info("Processing whitelist entries");

        List<WhitelistCoin> previousBatch = (List<WhitelistCoin>) firstBatch;
        List<WhitelistCoin> currentBatch = (List<WhitelistCoin>) secondBatch;

        List<WhitelistCoin> newProjects = new ArrayList<>();
        List<Pair<WhitelistCoin, WhitelistCoin>> updatedProjects = new ArrayList<>();

        Map<String, WhitelistCoin> existingEntries =
                previousBatch.stream()
                        .collect(Collectors.toMap(
                                kv -> kv.getName(), kv -> kv));

        for (WhitelistCoin entry : currentBatch) {
            if (existingEntries.containsKey(entry.getName())) {
                WhitelistCoin existingEntry = existingEntries.get(entry.getName());
                if (existingEntry.compareTo(entry) != 0) {
                    updatedProjects.add(new Pair<>(existingEntry, entry));
                    logger.info("Project has been updated: {}", entry.getName());
                }
            }
            else {
                newProjects.add(entry);
                logger.debug("New project found: {}", entry.getName());
            }
        }

        boolean hasChanges = newProjects.size() > 0 || updatedProjects.size() > 0;

        if (hasChanges) {
            sendAlerts(newProjects, updatedProjects);
        }

        return hasChanges;
    }

    @Override
    public void save(List<? extends Object> entries) {
        DbUtils.saveEntities(entries);
    }

    /**
     * Sends alerts to Slack regarding new and updated projects
     * @param newProjects
     * @param updatedProjects
     */
    private void sendAlerts(List<WhitelistCoin> newProjects, List<Pair<WhitelistCoin, WhitelistCoin>> updatedProjects) {
        SlackWebhook slack = new SlackWebhook("whitelist-update-alert");

        if (newProjects.size() > 0) {
            logger.debug("Sending slack alerts for new whitelist projects");

            StringBuilder sb = new StringBuilder();
            sb.append("New whitelist projects:\n");

            for (WhitelistCoin newProject : newProjects) {
                String message = String.format("<%s|%s> (category: %s) added with a status of %s\n",
                        newProject.getUrl(),
                        newProject.getName(),
                        newProject.getCategoryName(),
                        newProject.getStatus());

                sb.append(message);
            }

            slack.sendMessage(sb.toString());
        }

        if (updatedProjects.size() > 0) {
            logger.debug("Sending slack alerts for updated whitelist projects");

            StringBuilder sb = new StringBuilder();
            sb.append("Updated whitelist projects:\n");

            for (Pair<WhitelistCoin, WhitelistCoin> updatedProject : updatedProjects) {
                WhitelistCoin prev = updatedProject.getValue0();
                WhitelistCoin curr = updatedProject.getValue1();

                String message = String.format("<%s|%s> (category: %s) updated from %s to %s",
                        curr.getUrl(),
                        curr.getName(),
                        curr.getCategoryName(),
                        prev.getStatus(),
                        curr.getStatus());

                sb.append(message);
            }

            slack.sendMessage(sb.toString());
        }

        logger.debug("Shutting down slack instance");
        slack.shutdown();
    }
}
