package com.crypto;

import com.crypto.api.PageProcessor;
import com.crypto.impl.PresaleProcessorImpl;
import com.crypto.impl.WhitelistProcessorImpl;
import com.crypto.orm.HibernateFactory;

public class Application {

    public static void main(String[] args) {
        PageProcessor processor = new WhitelistProcessorImpl();
        processor.process();

        processor = new PresaleProcessorImpl();
        processor.process();

        // Clean up and shutdown connections
        HibernateFactory.shutdown();
    }
}