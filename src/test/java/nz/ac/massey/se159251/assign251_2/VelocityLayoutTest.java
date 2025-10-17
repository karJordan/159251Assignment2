package nz.ac.massey.se159251.assign251_2;

import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.Test;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VelocityLayoutTest {
    @Test
    void testFormatProducesExpectedPatter(){
        VelocityLayout layout = new VelocityLayout("[$p] $m");
        Logger logger = Logger.getLogger("test");
        LoggingEvent e = new LoggingEvent(
                "test", logger, System.currentTimeMillis(),
                Level.INFO, "Message",Thread.currentThread().getName(),null,null,null,null
        );
        String result = layout.format(e);
        assertTrue(result.contains("INFO"));
        assertTrue(result.contains("Message"));
    }
}
