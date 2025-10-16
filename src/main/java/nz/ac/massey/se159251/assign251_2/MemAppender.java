package nz.ac.massey.se159251.assign251_2;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Layout;

import java.util.Collections;
import java.util.List;

public class MemAppender extends AppenderSkeleton{
    private static MemAppender instance;
    private List<LoggingEvent> events;
    private int maxSiza = 1000; //default, older logs deleted
    private long discardedCount = 0;

    // constructor singleton
    private MemAppender(List<LoggingEvent> eventList, Layout layout){
        this.events = eventList;
        this.layout = layout;
    }

    // singleton accessor
    public static synchronized MemAppender getInstance(List<LoggingEvent> list, Layout layout){
        if (instance == null){
            instance = new MemAppender(list,layout);
        }
        return instance;
    }

    // called automatically by Log4j when there is a log
    @Override
    protected void append(LoggingEvent loggingEvent) {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}
