package nz.ac.massey.se159251.assign251_2;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemAppenderTest {
    private MemAppender appender;

    @BeforeEach
    void setUp(){
        MemAppender.resetInstance();
        List<LoggingEvent> aList = new ArrayList<>();
        Layout layout = new SimpleLayout();
        appender = MemAppender.getInstance(aList, layout);
    }

    @Test
    void testAppenderAndRetrieve(){
        Logger logger = Logger.getLogger("testLogger");
        logger.addAppender(appender);
        logger.info("Hello");

        assertEquals(1,appender.getCurrentLogs().size());
        assertTrue(appender.getEventStrings().get(0).contains("Hello"));
    }

    @Test
    void testDiscardCountWhenFull() {
        int maxSize = 100;
        int total = 1000;

        Layout layout = new PatternLayout("[%p] %m%n");
        List<LoggingEvent> list = new ArrayList<>();
        MemAppender memAppender = MemAppender.getInstance(list, layout);
        memAppender.setMaxSize(maxSize);

        Logger logger = Logger.getLogger("testLogger");

        // act
        for (int i = 0; i < total; i++) {
            LoggingEvent e = new LoggingEvent(
                    "testLogger",
                    logger,
                    System.currentTimeMillis(),
                    Level.INFO,
                    "Message " + i,
                    null
            );
            memAppender.append(e);
        }

        // assert
        int expected = total - maxSize; // number of discarded logs
        assertEquals(expected, memAppender.getDiscardedCount());
    }

}
