package com.djdenpa.learn.musicfeel.tools;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by H on 2016/02/15.
 */
public class DebugLogger {
    public static org.apache.log4j.Logger getLogger(Class sClass) {
        final LogConfigurator logConfigurator = new LogConfigurator();
        logConfigurator.setRootLevel(Level.ALL);
        logConfigurator.setUseLogCatAppender(true);
        logConfigurator.setUseFileAppender(false);
        logConfigurator.configure();
        Logger log = Logger.getLogger(sClass);
        return log;
    }
}
