package com.crypto.impl;

import com.crypto.api.PageReader;
import com.crypto.orm.entity.PresaleCoin;
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

public class PresaleReaderImpl implements PageReader {

    /**
     * Logging
     */
    private static final Logger logger = LoggerFactory.getLogger(PresaleReaderImpl.class);

    /**
     * Base url
     */
    private final String PRESALE_URL = "https://icodrops.com/pre-sales/";

    /**
     * User agent for web requests
     */
    private final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

    public PresaleReaderImpl() {}

    @Override
    public List<Object> readEntries() {
        logger.info("Reading presale entries");

        List<Object> entries = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(this.PRESALE_URL).userAgent(this.USER_AGENT).get();
            Elements projects = doc.select(".whiteico");

            for (Element project : projects) {
                PresaleCoin entry = new PresaleCoin(project);

                entries.add(entry);

                logger.debug("Created presale entity for {}", entry.getName());
            }
        } catch (IOException ex) {
            logger.error("Error parsing data from presale url");
        }

        return entries;
    }

    @Override
    public boolean processEntries(List<? extends Object> firstBatch, List<? extends Object> secondBatch) {
        logger.info("Processing presale entries");

        List<PresaleCoin> previousBatch = (List<PresaleCoin>) firstBatch;
        List<PresaleCoin> currentBatch = (List<PresaleCoin>) secondBatch;

        List<PresaleCoin> newProjects = new ArrayList<>();
        List<Pair<PresaleCoin, PresaleCoin>> updatedProjects = new ArrayList<>();

        Map<String, PresaleCoin> existingEntries =
                previousBatch.stream()
                        .collect(Collectors.toMap(
                                kv -> kv.getName(), kv -> kv));

        for (PresaleCoin entry : currentBatch) {
            if (existingEntries.containsKey(entry.getName())) {
                PresaleCoin existingEntry = existingEntries.get(entry.getName());
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
    private void sendAlerts(List<PresaleCoin> newProjects, List<Pair<PresaleCoin, PresaleCoin>> updatedProjects) {
        SlackWebhook slack = new SlackWebhook("presale-update-alert");

        if (newProjects.size() > 0) {
            logger.debug("Sending slack alerts for new presale projects");

            StringBuilder sb = new StringBuilder();
            sb.append("New presale projects:\n");

            for (PresaleCoin newProject : newProjects) {
                String message = String.format("<%s|%s> added with %s interest; status: %s; bonus: %s; rate: %s\n",
                        newProject.getUrl(),
                        newProject.getName(),
                        newProject.getPresaleInterest(),
                        newProject.getPresaleDate(),
                        newProject.getBonus(),
                        newProject.getMinRate());

                sb.append(message);
            }

            slack.sendMessage(sb.toString());
        }

        if (updatedProjects.size() > 0) {
            logger.debug("Sending slack alerts for updated presale projects");

            StringBuilder sb = new StringBuilder();
            sb.append("Updated presale projects:\n");

            for (Pair<PresaleCoin, PresaleCoin> updatedProject : updatedProjects) {
                PresaleCoin prev = updatedProject.getValue0();
                PresaleCoin curr = updatedProject.getValue1();

                StringBuilder message = new StringBuilder();
                message.append(String.format(
                        "<%s|%s> updated: ",
                        curr.getUrl(),
                        curr.getName()));

                List<String> appendedDetails = new ArrayList<>();

                if (!prev.getPresaleInterest().equals(curr.getPresaleInterest())) {
                    appendedDetails.add(String.format(
                            "interest went from %s to %s",
                            prev.getPresaleInterest(),
                            curr.getPresaleInterest()));
                }

                if (!prev.getPresaleDate().equals(curr.getPresaleDate())) {
                    appendedDetails.add(String.format(
                            "status went from %s to %s",
                            prev.getPresaleDate(),
                            curr.getPresaleDate()));
                }

                if (!prev.getBonus().equals(curr.getBonus())) {
                    appendedDetails.add(String.format(
                            "bonus went from %s to %s",
                            prev.getBonus(),
                            curr.getBonus()));
                }

                if (!prev.getMinRate().equals(curr.getMinRate())) {
                    appendedDetails.add(String.format(
                            "rate went from %s to %s",
                            prev.getMinRate(),
                            curr.getMinRate()));
                }

                String details = String.join("; ", appendedDetails);

                message.append(details).append("\n");
                sb.append(message.toString());
            }

            slack.sendMessage(sb.toString());
        }

        logger.debug("Shutting down slack instance");
        slack.shutdown();
    }
}
